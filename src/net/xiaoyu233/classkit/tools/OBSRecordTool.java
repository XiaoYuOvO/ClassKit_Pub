package net.xiaoyu233.classkit.tools;


import net.xiaoyu233.classkit.api.Lesson;
import net.xiaoyu233.classkit.api.Subject;
import net.xiaoyu233.classkit.config.RecorderConfig;
import net.xiaoyu233.classkit.event.ClassBeginEvent;
import net.xiaoyu233.classkit.event.EventType;
import net.xiaoyu233.classkit.managment.EventManager;
import net.xiaoyu233.classkit.util.Utils;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Deprecated
public class OBSRecordTool extends Tool<RecorderConfig>{
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private int endKey;
    private List<Lesson> lessonsToday;
    private boolean hasJobTodo = false;
    private File targetDir;
    private File recordFile;
    private final DelayedRunner delayedRunner = new DelayedRunner();
    private final Robot keySender = new Robot();
    private int startKey;
    private int lastIndex;
    private volatile boolean isRecording;
    private AtomicBoolean silenceNow = new AtomicBoolean();
    private Lesson currentLesson;

    public OBSRecordTool() throws AWTException {
    }

    @Override
    public String getName() {
        return "OBSClassRecorder";
    }

    @Override
    public void init(RecorderConfig config) {
        if (!Utils.isProcessRunning(config.getObsPath().getName())){
            Utils.runProcessWithPrivilege(new ProcessBuilder().directory(config.getObsPath().getParentFile()).command(config.getObsPath().getAbsolutePath()));
        }
        this.endKey = config.getEndRecord().getKeyId();
        this.startKey = config.getStartRecord().getKeyId();
        this.lessonsToday = config.getLessonTable().getLessonsOf(DayOfWeek.from(LocalDate.now()));
        this.targetDir = config.getTargetOutputDir();
        this.recordFile = config.getRecordedFile();
        this.addKeyCallback(config.getEndRecord(), () -> {if(this.isRecording){stopRecord();}});
        this.addKeyCallback(config.getStartRecord(), () -> {if(!this.isRecording){startRecord();}});
    }

    @Override
    public void registerEvent(EventManager eventManager) {
        super.registerEvent(eventManager);
        eventManager.registerListener(EventType.SILENCE, (event -> this.silenceNow.set(event.isSilence())));
    }

    @Override
    public void reloadConfig(RecorderConfig config) {

    }

    private void stopRecord(){
        if (this.isRecording) {
            this.isRecording = false;
            this.sendKey(this.endKey);
            this.sendMessage("已停止录制");

            File subjectDir = new File(this.targetDir, currentLesson.getSubject()
                    .getLocalizedName());
            Date date = new Date();
            String name = recordFile.getName();
            File dest = new File(subjectDir, simpleDateFormat.format(date) + DayOfWeek.from(LocalDate.now())
                    .getDisplayName(TextStyle.FULL, Locale.SIMPLIFIED_CHINESE) + "第" + (currentLesson.getIndexOfDay() + 1) + "节" + "(" + currentLesson.getContent() + ")" + name.substring(name.lastIndexOf(".")));
            if (subjectDir.exists() || subjectDir.mkdirs()) {
                new Thread(() -> {
                    try {
                        //Wait for write complete
                        try {
                            Thread.sleep(1000);
                            while (!this.recordFile.renameTo(this.recordFile)) {
                                this.sendMessage("等待录制文件写入完成...");
                                Thread.sleep(1000);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Files.copy(this.recordFile.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        this.sendMessage("已重命名文件至 " + dest.getAbsolutePath());
                        if (!this.recordFile.delete()) {
                            this.sendMessage("无法删除录制缓存");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        this.sendMessage("重命名文件失败!");
                    }
                }).start();
            }
            this.hasJobTodo = false;
        }

    }

    private void startRecord(){
        if (!this.isRecording) {
            this.getEventManager().sendEvent(new ClassBeginEvent());
            this.isRecording = true;
            this.sendKey(this.startKey);
            if (this.recordFile.exists() || !this.recordFile.delete()) {
                this.sendMessage("无法删除上一次缓存");
            }
            this.sendMessage("开始录制: " + currentLesson.getSubject() + " - " + currentLesson.getContent());
        }
    }

    private void sendKey(int key){
        this.keySender.keyPress(key);
        this.keySender.delay(100);
        this.keySender.keyRelease(key);
    }

    @Nonnull
    private Lesson findNextLesson() {
        LocalTime now = LocalTime.now();
        Lesson result = Lesson.EMPTY;
        for (Lesson lesson : this.lessonsToday) {
            if (lesson.getSubject() != Subject.NONE && this.currentLesson != lesson) {
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

    private void scheduleRecordJobs(){
        Lesson nextLesson = this.findNextLesson();
        if (nextLesson != null){
            LocalTime now = LocalTime.now();
            System.out.println("Next lesson " + nextLesson);
            this.currentLesson = nextLesson;
            //Start
            long between1 = ChronoUnit.SECONDS.between(now,nextLesson.getStartEndTime().lowerEndpoint());
            System.out.println("Time to start:" + between1);
            this.delayedRunner.scheduleTask(new ScheduledTask(
                    //Limit to immediately
                    between1,
                    this::startRecord));
            //Stop
            long between = ChronoUnit.SECONDS.between( now,nextLesson.getStartEndTime().upperEndpoint());
            System.out.println("Time to stop:" + between);
            this.delayedRunner.scheduleTask(new ScheduledTask(
                    //Limit to immediately
                    between,
                    this::stopRecord));
        }else {
            this.sendMessage("今日课程已录制完毕!");
        }
        this.hasJobTodo = true;
    }

    @Override
    public void tick() {
        this.delayedRunner.countdown();
        if (!hasJobTodo){
            this.scheduleRecordJobs();
        }
    }

    private static class DelayedRunner{
        private final List<ScheduledTask> scheduledTasks = new ArrayList<>();
        public void scheduleTask(ScheduledTask task){
            this.scheduledTasks.add(task);
        }

        public void countdown(){
            for (Iterator<ScheduledTask> iterator = scheduledTasks.iterator(); iterator.hasNext(); ) {
                ScheduledTask scheduledTask = iterator.next();
                scheduledTask.countdown();
                if (scheduledTask.timeRemaining <= 0) {
                    scheduledTask.task.run();
                    //Safe
                    iterator.remove();
                }
            }
        }
    }

    private static final class ScheduledTask{
        private final Runnable task;
        private long timeRemaining;

        private ScheduledTask(long delay, Runnable task) {
            this.task = task;
            this.timeRemaining = delay;
        }

        public void countdown(){
            this.timeRemaining--;
        }
    }
}
