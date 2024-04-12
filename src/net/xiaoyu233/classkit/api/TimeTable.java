package net.xiaoyu233.classkit.api;

import com.google.common.collect.Range;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TimeTable {
    private final List<Range<LocalTime>> times;

    public TimeTable(int timePeriodPerDay) {
        this.times = new ArrayList<>(timePeriodPerDay);
    }

    public void setPeriod(Range<LocalTime> period,int index){
            this.times.set(index,period);
    }

    public Range<LocalTime> get(int index) {
        return times.get(index);
    }

}
