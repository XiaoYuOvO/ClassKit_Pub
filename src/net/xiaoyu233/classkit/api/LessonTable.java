package net.xiaoyu233.classkit.api;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LessonTable {
    private final int lessonOfDays;
    private Map<DayOfWeek, List<Lesson>> tables;

    public LessonTable(int lessonOfDay) {
        this.lessonOfDays = lessonOfDay;
        this.tables = new HashMap<>(DayOfWeek.values().length);
    }
    
    public LessonTable deepCopy(){
        LessonTable lessonTable = new LessonTable(this.lessonOfDays);
        lessonTable.tables = new HashMap<>(this.tables);
        return lessonTable;
    }

    public int getLessonCount(){
        return this.lessonOfDays;
    }

    public int getDayCount(){
        return this.tables.size();
    }



    public void setLesson(DayOfWeek day,int index,Lesson lesson){
        tables.computeIfAbsent(day,(day1) -> {
            ArrayList<Lesson> lessons = new ArrayList<>();
            for (int i = 0; i < this.lessonOfDays; i++) {
                lessons.add(Lesson.EMPTY);
            }
            return lessons;
        }).set(index,lesson);
    }

    public List<Lesson> getLessonsOf(DayOfWeek day){
        return tables.get(day);
    }
}
