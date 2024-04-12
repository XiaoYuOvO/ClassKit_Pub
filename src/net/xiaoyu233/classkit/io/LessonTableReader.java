package net.xiaoyu233.classkit.io;

import com.google.common.collect.Lists;
import net.xiaoyu233.classkit.api.Lesson;
import net.xiaoyu233.classkit.api.LessonTable;
import net.xiaoyu233.classkit.api.Subject;
import org.apache.poi.xwpf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class LessonTableReader {

    public static LessonTable readFromWordFile(File wordFile,SubjectFilter filter,LessonTable defaultTable) throws IOException {
        LessonTable lessonTable = new LessonTable(defaultTable.getLessonCount());
        for (int i = 0; i < defaultTable.getDayCount(); i++) {
            lessonTable.setLesson(DayOfWeek.values()[i],0,Lesson.EMPTY);
        }
        try (FileInputStream fileReader = new FileInputStream(wordFile)) {
            XWPFDocument xwpfDocument = new XWPFDocument(fileReader);

            List<XWPFParagraph> paragraphs = xwpfDocument.getParagraphs();
            ArrayList<String> targetContent = Lists.newArrayList();
            for (XWPFParagraph content : paragraphs) {
                String text = content.getText();
                targetContent.addAll(Lists.newArrayList(text.split("\n")));
            }


            Subject currentSubject = Subject.NONE;
            DayOfWeek day = DayOfWeek.MONDAY;
            int indexOfDay = -1;
            for (String string : targetContent) {
                if (string.contains("：") && string.contains("（") && string.contains("）：")){
                    //Lesson content
                    String dayOfWeek = string.substring(string.indexOf("（") + 1, string.indexOf("）"));
                    System.out.print(dayOfWeek  + ":");
                    day = mapDayOfWeekFromText(dayOfWeek);
                    for (Lesson lesson : defaultTable.getLessonsOf(day)) {
                        if (lesson.getSubject() == currentSubject){
                            indexOfDay = lesson.getIndexOfDay();
                            String content = processContent(string);
                            Lesson newLesson = new Lesson(lesson.getStartEndTime(), indexOfDay,lesson.getSubject(),
                                    content,hasNoLiving(content)
                                    );
                            System.out.println(newLesson);
                            lessonTable.setLesson(day, indexOfDay,newLesson);
                            break;
                        }
                    }

                }else if (string.trim().isEmpty()){
                    //Next subject
                }else if (string.startsWith(" ") || string.startsWith("            ") || string.startsWith("!")){
                    //Multi same subject lesson
                    if (currentSubject != Subject.NONE && indexOfDay != -1){
                        List<Lesson> lessonsOf = defaultTable.getLessonsOf(day);
                        for (int i = indexOfDay + 1; i < lessonsOf.size(); i++) {
                            Lesson lesson = lessonsOf.get(i);
                            if (lesson.getSubject() == currentSubject){
                                String content = processContent(string);
                                boolean noRecord = hasNoLiving(content);
                                Lesson newLesson = new Lesson(lesson.getStartEndTime(), i, currentSubject, content,
                                        noRecord);
                                System.out.println("Multi lesson: " + newLesson);
                                lessonTable.setLesson(day,i, newLesson);
                            }
                        }
                    }
                }else {
                    Subject fromLocalized = Subject.getFromLocalized(string.trim());
                    if (fromLocalized != Subject.NONE){
                        currentSubject = fromLocalized;
                        System.out.println("Switch to " + string);
                    }
                }
            }
        }
        return lessonTable;
    }

    private static boolean hasNoLiving(String content){
        return content.contains("不直播");
    }

    private static String processContent(String string){
        if (string.contains("：")){
            string = string.substring(string.indexOf("：") + 1);
        }
        String content =  string.replace("!","").replace(" ", "").trim();
        if (content.startsWith("《")){
            content = content.substring(content.indexOf("》") + 1);
        }
        return content;
    }

    private static DayOfWeek mapDayOfWeekFromText(String t){
        return switch (t) {
            case "周一" -> DayOfWeek.MONDAY;
            case "周二" -> DayOfWeek.TUESDAY;
            case "周三" -> DayOfWeek.WEDNESDAY;
            case "周四" -> DayOfWeek.THURSDAY;
            case "周五" -> DayOfWeek.FRIDAY;
            case "周六" -> DayOfWeek.SATURDAY;
            default -> DayOfWeek.SUNDAY;
        };
    }

    public enum SubjectFilter{
        LIBERAL("文科"),
        SCIENCE("理科");

        private final String identifier;

        SubjectFilter(String identifier) {
            this.identifier = identifier;
        }

        public boolean isOf(String header){
            return header.contains(this.identifier);
        }
    }
}
