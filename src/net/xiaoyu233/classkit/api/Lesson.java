package net.xiaoyu233.classkit.api;

import com.google.common.collect.Range;

import java.time.LocalTime;

public class Lesson {
    public static final Lesson EMPTY = new Lesson(Range.closed(LocalTime.of(23,0),LocalTime.of(23,0)),-1,Subject.NONE,"",
            false);
    private final Range<LocalTime> startEndTime;
    private final Subject subject;
    private final String content;
    private final int indexOfDay;
    private final boolean noRecord;

    public Lesson(Range<LocalTime> startEndTime, int indexOfDay, Subject subject, String content, boolean noRecord) {
        this.startEndTime = startEndTime;
        this.subject = subject;
        this.content = content;
        this.indexOfDay = indexOfDay;
        this.noRecord = noRecord;
    }

    public boolean isNoRecord() {
        return noRecord;
    }

    public int getIndexOfDay() {
        return indexOfDay;
    }

    public String getContent() {
        return content;
    }

    public Subject getSubject() {
        return subject;
    }

    public Range<LocalTime> getStartEndTime() {
        return startEndTime;
    }

    @Override
    public String toString() {
        if (noRecord){
            return "Lesson{" +
                    "startEndTime=" + startEndTime +
                    ", subject='" + subject + '\'' +
                    ", content='" + content + '\'' +
                    "}[NoRecord]";
        }
        return "Lesson{" +
                "startEndTime=" + startEndTime +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
