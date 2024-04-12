//
// Created by 705 on 2022/11/30.
//

#ifndef AUDIO_CAPTURE_QUEUE_H
#define AUDIO_CAPTURE_QUEUE_H


#include<queue>
#include<mutex>
#include<condition_variable>
#include<optional>
#include<cassert>
#include<thread>
#include <iostream>

template<typename T,typename Container = std::queue<T>>
class Queue	//无界队列
{
public:
    Queue() = default;
    ~Queue() = default;

    //禁止拷贝和移动，编译器会自动delete
    /*Queue(const Queue&) = delete;
    Queue(Queue&&) = delete;
    Queue& operator=(const Queue&) = delete;
    Queue& operator=(Queue&&) = delete;*/


    void push(const T& val)
    {
        emplace(val);
    }

    void push(T&& val)
    {
        emplace(std::move(val));
    }

    template<typename...Args>
    void emplace(Args&&...args)
    {
        std::lock_guard lk{ mtx_ };
        q_.push(std::forward<Args>(args)...);
        if (q_.size() > 50){
            pop()->ReleaseData();
        }
        cv_.notify_all();
    }

    T pop()//阻塞
    {
        if (!q_.empty()){
            T ret{ std::move_if_noexcept(q_.front()) };
            q_.pop();
            return ret;
        }
        std::unique_lock lk{ mtx_ };
        std::cout << "Queue is empty,start to wait" << std::endl;
        cv_.wait(lk, [this] {return !q_.empty(); });//如果队列不为空就继续执行，否则阻塞
//        std::cout << "Quitting wait for next sample" << std::endl;
        assert(!q_.empty());
        T ret{ std::move_if_noexcept(q_.front()) };
        q_.pop();
        return ret;
    }

    int size(){
        return q_.size();
    }

    std::optional<T> try_pop()//非阻塞
    {
        std::unique_lock lk{ mtx_ };
        if (q_.empty())return {};
        std::optional<T> ret{ std::move_if_noexcept(q_.front()) };
        q_.pop();
        return ret;
    }
    bool empty()const
    {
        std::lock_guard lk{ mtx_ };
        return q_.empty();
    }
private:
    Container q_;
    mutable std::mutex mtx_;
    std::condition_variable cv_;
};

#endif //AUDIO_CAPTURE_QUEUE_H
