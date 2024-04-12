package net.xiaoyu233.classkit.av;

public interface AVListener {
    void streamStart();
    void onTerminated();
    String getName();
}
