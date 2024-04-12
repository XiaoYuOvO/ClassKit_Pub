package net.xiaoyu233.classkit.api;

import java.time.DayOfWeek;
import java.time.LocalTime;

public interface TimeProvider {

    boolean isFor(DayOfWeek day);
    TimeTable getFor(DayOfWeek day);
}
