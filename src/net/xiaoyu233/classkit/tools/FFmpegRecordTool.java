package net.xiaoyu233.classkit.tools;


import net.xiaoyu233.classkit.api.Lesson;
import net.xiaoyu233.classkit.api.Subject;
import net.xiaoyu233.classkit.av.AudioProvider;
import net.xiaoyu233.classkit.av.FrameRecorder;
import net.xiaoyu233.classkit.av.VideoProvider;
import net.xiaoyu233.classkit.config.RecorderConfig;
import net.xiaoyu233.classkit.event.ClassBeginEvent;
import net.xiaoyu233.classkit.event.ClassOverEvent;
import net.xiaoyu233.classkit.event.EventType;
import net.xiaoyu233.classkit.managment.EventManager;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class FFmpegRecordTool extends Tool<RecorderConfig>{
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private List<Lesson> lessonsToday;
    private boolean hasJobTodo = false;
    private boolean isClassOver;
    private boolean isSilenceNow;
    private File targetDir;
    private final FFmpegRecordTool.DelayedRunner delayedRunner = new FFmpegRecordTool.DelayedRunner();
    private FrameRecorder recorder;
    private volatile boolean isRecording;
    private Lesson currentLesson;
    private VideoProvider videoProvider;
    private AudioProvider audioProvider;

    public FFmpegRecordTool() {
    }

    @Override
    public String getName() {
        return "ClassRecorder";
    }

    @Override
    public void init(RecorderConfig config) {
        this.lessonsToday = config.getLessonTable().getLessonsOf(DayOfWeek.from(LocalDate.now()));
        this.targetDir = config.getTargetOutputDir();
        this.addKeyCallback(config.getEndRecord(), () -> {if(this.isRecording){stopRecord();}});
        this.addKeyCallback(config.getStartRecord(), () -> {if(!this.isRecording){startRecord();}});
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (this.isRecording) {
                stopRecord();
                while (!this.recorder.hasTerminated()){
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }));
    }

    @Override
    public void reloadConfig(RecorderConfig config) {

    }

    @Override
    public void registerEvent(EventManager eventManager) {
        super.registerEvent(eventManager);
        eventManager.registerListener(EventType.SILENCE, (silence) -> this.isSilenceNow = silence.isSilence());
    }

    public <T extends VideoProvider & AudioProvider> void registerProvider(T provider){
        audioProvider = provider;
        videoProvider = provider;
    }

    private void stopRecord(){
        if (this.isRecording) {
            this.isRecording = false;
            this.getEventManager().sendEvent(new ClassOverEvent());
            try {
                this.recorder.requestStop();
            } catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
                throw new RuntimeException(e);
            }
            this.sendMessage("已停止录制");
            this.hasJobTodo = false;
        }

    }

    private void startRecord(){
        if (!this.isRecording && currentLesson != null) {
            this.getEventManager().sendEvent(new ClassBeginEvent());
            this.isRecording = true;
            this.isClassOver = false;
            File subjectDir = new File(this.targetDir, currentLesson.getSubject()
                    .getLocalizedName());
            Date date = new Date();
            File dest = new File(subjectDir, simpleDateFormat.format(date) + DayOfWeek.from(LocalDate.now())
                    .getDisplayName(TextStyle.FULL, Locale.SIMPLIFIED_CHINESE) + "第" + (currentLesson.getIndexOfDay() + 1) + "节" + "(" + preventInvalidFileName(currentLesson.getContent()) + ").mp4");
            int count = 0;
            while (dest.exists()){
                dest = new File(subjectDir, simpleDateFormat.format(date) + DayOfWeek.from(LocalDate.now())
                        .getDisplayName(TextStyle.FULL, Locale.SIMPLIFIED_CHINESE) + "第" + (currentLesson.getIndexOfDay() + 1) + "节" + "(" + preventInvalidFileName(currentLesson.getContent()) + ")_" + count + ".mp4");
                count++;
            }
            if (subjectDir.exists() || subjectDir.mkdirs()) {
                try {
                    this.recorder = new FrameRecorder(dest, 1366, 768);
//                    this.recorder.streamStart();
                    this.videoProvider.addVideoCallback(this.recorder);
                    this.audioProvider.addAudioCallback(this.recorder);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            this.sendMessage("开始录制: " + currentLesson.getSubject() + " - " + currentLesson.getContent());
        }
    }


    @Nonnull
    private Lesson findNextLesson() {
        LocalTime now = LocalTime.now();
        Lesson result = Lesson.EMPTY;
        for (Lesson lesson : this.lessonsToday) {
            if (lesson.getSubject() != Subject.NONE && this.currentLesson != lesson && !lesson.isNoRecord()) {
                if (lesson.getStartEndTime()
                        .contains(now)) {
                    return lesson;
                } else if (lesson.getStartEndTime()
                        .lowerEndpoint()
                        .isAfter(now)) {
                    result = lesson;
                    break;
                }
            }
        }
        return result;
    }

    private static String preventInvalidFileName(String name){
        return name.replace(":","：").replace("\\","、").replace("/","、").replace("*","").replace("?","？");
    }

    private void scheduleRecordJobs(){
        Lesson nextLesson = this.findNextLesson();
        if (nextLesson != null){
            System.out.println("Next lesson " + nextLesson);
            this.currentLesson = nextLesson;
            //Start
            LocalTime start = nextLesson.getStartEndTime().lowerEndpoint();
            System.out.println("Time to start:" + start);
            this.delayedRunner.scheduleTask(new FFmpegRecordTool.ScheduledTask(start, this::startRecord));
            //Stop
            LocalTime stop = nextLesson.getStartEndTime().upperEndpoint();
            System.out.println("Time to stop:" + stop);
            this.delayedRunner.scheduleTask(new FFmpegRecordTool.ScheduledTask(stop, () -> this.isClassOver = true));
        }else {
            this.sendMessage("今日课程已录制完毕!");
        }
        this.hasJobTodo = true;
    }

    @Override
    public void tick() {
        this.delayedRunner.tick();
        if (!hasJobTodo){
            this.scheduleRecordJobs();
        }
        if (this.isClassOver && this.isSilenceNow && this.isRecording){
            this.stopRecord();
        }
    }

    private static class DelayedRunner{
        private final List<FFmpegRecordTool.ScheduledTask> scheduledTasks = new ArrayList<>();
        public void scheduleTask(FFmpegRecordTool.ScheduledTask task){
            this.scheduledTasks.add(task);
        }

        public void tick(){
            for (Iterator<FFmpegRecordTool.ScheduledTask> iterator = scheduledTasks.iterator(); iterator.hasNext(); ) {
                FFmpegRecordTool.ScheduledTask scheduledTask = iterator.next();
                if (scheduledTask.canStartNow()) {
                    scheduledTask.task.run();
                    //Safe
                    iterator.remove();
                }
            }
        }
    }

    private static final class ScheduledTask{
        private final Runnable task;
        private final LocalTime timeToStart;

        private ScheduledTask(LocalTime timeToStart, Runnable task) {
            this.task = task;
            this.timeToStart = timeToStart;
        }

        public boolean canStartNow(){
            return LocalTime.now().isAfter(this.timeToStart);
        }
    }
}
