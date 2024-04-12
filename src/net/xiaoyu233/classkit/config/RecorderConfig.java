package net.xiaoyu233.classkit.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import net.xiaoyu233.classkit.api.Lesson;
import net.xiaoyu233.classkit.api.LessonTable;
import net.xiaoyu233.classkit.api.Subject;
import net.xiaoyu233.classkit.io.LessonTableReader;
import net.xiaoyu233.classkit.keys.KeyBind;
import net.xiaoyu233.classkit.keys.KeyModifier;
import net.xiaoyu233.classkit.keys.Keys;
import net.xiaoyu233.classkit.util.FieldReference;

import java.io.File;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

public class RecorderConfig extends ToolConfig{
    public static final Range<LocalTime> LESSON1_MON = Range.closed(
            LocalTime.of(8,07),
            LocalTime.of(8,50));
    public static final Range<LocalTime> LESSON2_MON = Range.closed(
            LocalTime.of(8,57),
            LocalTime.of(9,40));
    public static final Range<LocalTime> LESSON3_MON = Range.closed(
            LocalTime.of(9,47),
            LocalTime.of(10,30));

    public static final Range<LocalTime> LESSON1 = Range.closed(
            LocalTime.of(7,52),
            LocalTime.of(8,35));
    public static final Range<LocalTime> LESSON2 = Range.closed(
            LocalTime.of(8,42),
            LocalTime.of(9,25));
    public static final Range<LocalTime> LESSON3 = Range.closed(
            LocalTime.of(9,32),
            LocalTime.of(10,15));

    public static final Range<LocalTime> LESSON4 = Range.closed(
            LocalTime.of(10,37),
            LocalTime.of(11,20));
    public static final Range<LocalTime> LESSON5 = Range.closed(
            LocalTime.of(11,27),
            LocalTime.of(12,10));
    public static final Range<LocalTime> LESSON6 = Range.closed(
            LocalTime.of(14,13),
            LocalTime.of(14,55));
    public static final Range<LocalTime> LESSON7 = Range.closed(
            LocalTime.of(15,02),
            LocalTime.of(15,45));
    public static final Range<LocalTime> LESSON8 = Range.closed(
            LocalTime.of(15,52),
            LocalTime.of(16,35));
    public static final Range<LocalTime> LESSON9 = Range.closed(
            LocalTime.of(16,42),
            LocalTime.of(17,25));
    private static final FieldReference<File> TARGET_OUTPUT_DIR = new FieldReference<>(new File("D:/第十五周/"));
    private static final ConfigRegistry CONFIG = new ConfigRegistry(
            ConfigCategory.of("录制设置").
                    addEntry(ConfigCategory.of("文件设置").
                                     addEntry(ConfigEntry.of("target_output_dir", TARGET_OUTPUT_DIR).withComment("最终输出目录"))).
                    addEntry(ConfigCategory.of("按键控制")),
            new File("record_config"));
    private final KeyBind startRecord = new KeyBind(Keys.VK_F9, KeyModifier.EMPTY);
    private final KeyBind endRecord = new KeyBind(Keys.VK_F10, KeyModifier.EMPTY);
    private final LessonTable lessonTable;

    public RecorderConfig() {
        LessonTable lessonTable1;
        ConfigBuilder lesson = ConfigBuilder.of(9).
                switchToDay(DayOfWeek.MONDAY).
        lesson(Subject.MT, "").
                lesson(Subject.MT, "").
                lesson(Subject.CH, "").
                lesson(Subject.CE, "").
                lesson(Subject.BIO, "").
                lesson(Subject.PH, "").
                empty().
                lesson(Subject.EN, "").
                empty().
                switchToDay(DayOfWeek.TUESDAY).
        lesson(Subject.CH, "").
                lesson(Subject.CH, "").
                lesson(Subject.PH, "").
                lesson(Subject.MT, "").
                lesson(Subject.EN, "").

                lesson(Subject.CE, "").
                lesson(Subject.BIO, "").
                lesson(Subject.MT, "").
                lesson(Subject.MT, "").
                switchToDay(DayOfWeek.WEDNESDAY).
        lesson(Subject.CH, "").
                lesson(Subject.EN, "").
                lesson(Subject.MT, "").
                lesson(Subject.MT, "").
                empty().

                lesson(Subject.BIO, "").
                lesson(Subject.CE, "").
                lesson(Subject.PH, "").
                lesson(Subject.CH, "").
                switchToDay(DayOfWeek.THURSDAY).
        lesson(Subject.PH, "").
                lesson(Subject.PH, "").
                lesson(Subject.CE, "").
                lesson(Subject.EN, "").
                lesson(Subject.EN, "").
                lesson(Subject.CH, "").
                lesson(Subject.MT, "").
                lesson(Subject.BIO, "").
                empty().
                switchToDay(DayOfWeek.FRIDAY).

        lesson(Subject.CE, "").
                lesson(Subject.EN, "").
                lesson(Subject.PH, "").
                lesson(Subject.BIO, "").
                lesson(Subject.CH, "").

                lesson(Subject.MT, "").
                lesson(Subject.PH, "").
                lesson(Subject.BIO, "").
                lesson(Subject.CE, "");
        buildSaturdayTable(lesson);
        lessonTable1 = lesson.build();

        try {
            lessonTable1 = LessonTableReader.readFromWordFile(new File("./5月15日至5月20日教学计划.docx"), LessonTableReader.SubjectFilter.SCIENCE, lessonTable1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.lessonTable = lessonTable1;
    }

    private static void buildSaturdayTable(ConfigBuilder builder){
        //Check the time of system now,if is on the week of 2023/4/15 then call buildSaturdayTableThree() with arg builder,
        // if is on the week of  2023/4/8 or after the monday of the week of  2023/4/22 then call buildSaturdayTableTwo() with arg builder
        LocalDate currentDate = LocalDate.now();
        LocalDate saturdayTableThreeDate = LocalDate.of(2023, 4, 15);
        LocalDate saturdayTableTwoStartDate = LocalDate.of(2023, 4, 8);
        LocalDate saturdayTableTwoEndDate = LocalDate.of(2023, 4, 22);
        if (currentDate.isAfter(saturdayTableThreeDate.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY))) &&
                currentDate.isBefore(saturdayTableThreeDate.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)))) {
            builder.switchToDay(DayOfWeek.SATURDAY).
                    lesson(Subject.BIO, "").
                    lesson(Subject.EN, "").
                    lesson(Subject.MT, "").
                    lesson(Subject.CH, "").
                    lesson(Subject.CE, "").
                    lesson(Subject.PH, "").
                    empty();
        } else if ((currentDate.isAfter(saturdayTableTwoStartDate.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY))) &&
                currentDate.isBefore(saturdayTableTwoStartDate.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)))) ||
                currentDate.isAfter(saturdayTableTwoEndDate.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY)))) {
            builder.switchToDay(DayOfWeek.SATURDAY).
                    lesson(Subject.CH, "").
                    lesson(Subject.CH, "").
                    lesson(Subject.BIO, "").
                    lesson(Subject.MT, "").
                    lesson(Subject.MT, "").
                    lesson(Subject.PH, "").
                    lesson(Subject.PH, "").
                    lesson(Subject.EN, "").
                    lesson(Subject.CE, "");
        }
    }
    public KeyBind getEndRecord() {
        return endRecord;
    }

    public File getTargetOutputDir() {
        return TARGET_OUTPUT_DIR.get();
    }

    @Deprecated
    public File getObsPath() {
        return new File("");
    }

    @Deprecated
    public File getRecordedFile() {
        return new File("");
    }

    public KeyBind getStartRecord() {
        return startRecord;
    }

    public LessonTable getLessonTable() {
        return lessonTable;
    }

    @Override
    public ConfigRegistry getConfig() {
        return CONFIG;
    }

    @Override
    public void reload() {

    }

    static class ConfigBuilder{
        private static final List<Range<LocalTime>> timeList = Lists.newArrayList(
                LESSON1,LESSON2,LESSON3,LESSON4,LESSON5,LESSON6,LESSON7,LESSON8,LESSON9,null
        );
        private static final List<Range<LocalTime>> timeListMon = Lists.newArrayList(
                LESSON1_MON,LESSON2_MON,LESSON3_MON,LESSON4,LESSON5,LESSON6,LESSON7,LESSON8,LESSON9,null
        );
        private final LessonTable lessonTable;
        private DayOfWeek dayNow;
        private int currentIndex;

        private ConfigBuilder(int lessonOneDay){
            this.lessonTable = new LessonTable(lessonOneDay);
        }

        public static ConfigBuilder of(int lessonOneDay){
            return new ConfigBuilder(lessonOneDay);
        }

        public ConfigBuilder switchToDay(DayOfWeek day){
            this.dayNow = day;
            this.currentIndex = 0;
            return this;
        }

        public ConfigBuilder lesson(Subject subject, String content){
            this.lessonTable.setLesson(this.dayNow,currentIndex,new Lesson(getTimeFor(currentIndex,dayNow),currentIndex,subject,content,
                    false));
            currentIndex++;
            return this;
        }

        public ConfigBuilder testLesson(Subject subject,String content,Range<LocalTime> range){
            this.lessonTable.setLesson(this.dayNow,currentIndex,new Lesson(range,currentIndex,subject,content, false));
            this.currentIndex++;
            return this;
        }

        public ConfigBuilder empty(){
            return this.lesson(Subject.NONE, "");
        }

        private static Range<LocalTime> getTimeFor(int index,DayOfWeek day){
            if (day == DayOfWeek.MONDAY || day == DayOfWeek.SUNDAY){
                return timeListMon.get(index);
            }else {
                return timeList.get(index);
            }
        }

        public LessonTable build(){
            return lessonTable;
        }
    }
}
