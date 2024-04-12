package net.xiaoyu233.classkit.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TaskThread extends Thread{
    private volatile boolean stopped;
    private final int idleWaitTime;
    private final List<Runnable> tasks = new ArrayList<>();

    public TaskThread(int idleWaitTime,String name) {
        this.idleWaitTime = idleWaitTime;
        this.setName(name);
    }

    @Override
    public void run() {
        while (!stopped) {
            int taskExecuted = 0;
            synchronized (tasks){
                for (Iterator<Runnable> iterator = tasks.iterator(); iterator.hasNext(); ) {
                    Runnable task = iterator.next();
                    task.run();
                    taskExecuted++;
//                    if (taskExecuted > 10){
//                        System.err.println("Worker " + this.getName() + " overburnt! With " + taskExecuted + " tasks done in last period");
//                    }
                    iterator.remove();
                }
            }
            try {
                if (taskExecuted != 0){
                    Thread.sleep(idleWaitTime / taskExecuted);
                }else {
                    Thread.sleep(idleWaitTime);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void enqueueTask(Runnable runnable){
        synchronized (tasks){
            this.tasks.add(runnable);
        }
    }

    public synchronized void clean(){
        synchronized (tasks){
            this.tasks.clear();
        }
    }

    public int getTaskCount(){
        return tasks.size();
    }

    public void stopExecuting(){
        this.stopped = true;
    }
}
