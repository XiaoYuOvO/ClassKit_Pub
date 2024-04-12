package net.xiaoyu233.classkit.av;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.xiaoyu233.classkit.config.EmptyConfig;
import net.xiaoyu233.classkit.keys.KeyBind;
import net.xiaoyu233.classkit.keys.KeyModifier;
import net.xiaoyu233.classkit.keys.Keys;
import net.xiaoyu233.classkit.tools.Tool;
import net.xiaoyu233.classkit.util.TaskThread;
import net.xiaoyu233.classkit.util.Utils;
import net.xiaoyu233.classkit.util.natives.loopback_capture.CaptureDataLine;
import net.xiaoyu233.classkit.util.profiler.*;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameRecorder;

import javax.sound.sampled.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class WenDaoGrabber extends Tool<EmptyConfig> implements VideoProvider, AudioProvider {
    protected final FFmpegFrameGrabber grabber ;
    protected final SwitchDataLine dataLine;
    protected int frameRate = 15;
    protected int audioRate = 50;
    private final String title;
    protected final List<AVListener> newCallbacks = Lists.newArrayList();
    protected final List<AudioListener> audioCallback = Lists.newArrayList();
    private final List<VideoListener> videoCallback = Lists.newArrayList();
    protected final Map<AVListener,TaskThread> threadMap = Maps.newHashMap();
    protected volatile boolean started;
    protected final ProfilerSystem audioProfiler = new ProfilerSystem();
    private long lastFrameTime;
    protected ProfilingMonitor monitor;

    public WenDaoGrabber(String title, SwitchDataLine dataLine) throws LineUnavailableException {
        this.title = title;
        this.grabber = new FFmpegFrameGrabber("title=" + title);
        System.out.println("Opening audio input at " + dataLine.getCurrentLine().getLineInfo().toString());
        this.dataLine = dataLine;
    }


    @Override
    public void addAudioCallback(AudioListener callback) {
        this.audioCallback.add(callback);
        this.addAVCallback(callback);
    }

    private void addAVCallback(AVListener callback){
        this.threadMap.put(callback,new TaskThread(1000 / this.frameRate,callback.getName()));
        if (this.isStarted()){
            TaskThread taskThread = this.threadMap.get(callback);
            taskThread.enqueueTask(callback::streamStart);
            taskThread.start();
        }else {
            newCallbacks.add(callback);
        }
    }

    @Override
    public void removeAudioCallback(AudioListener callback) {
        this.audioCallback.remove(callback);
        this.threadMap.remove(callback);
    }

    @Override
    public void addVideoCallback(VideoListener callback) {
        this.videoCallback.add(callback);
        this.addAVCallback(callback);
    }

    @Override
    public void removeVideoCallback(VideoListener callback) {
        this.videoCallback.remove(callback);
        this.threadMap.remove(callback);

    }

    @Override
    public void start() throws FrameRecorder.Exception, FrameGrabber.Exception, LineUnavailableException {
        monitor = new ProfilingMonitor();
        EventQueue.invokeLater(()->monitor.setVisible(true));
        grabber.setFormat("gdigrab");
        grabber.setFrameRate(this.frameRate);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        float zoom = 1;
        grabber.setOption("draw_mouse","0");
        grabber.setOption("framerate",String.valueOf(this.frameRate));
        grabber.setOption("offset_x", String.valueOf((int) (screenSize.width / zoom - 1366) / 2));
        grabber.setOption("offset_y",String.valueOf((int) (screenSize.height / zoom - 768) / 2));
        grabber.setImageWidth(1366);
        grabber.setImageHeight(768);
        grabber.start();
        this.threadMap.values().forEach(TaskThread::start);
        dataLine.open(dataLine.getFormat());
        dataLine.start();
        for (AVListener videoListener : this.newCallbacks) {
            this.threadMap.get(videoListener).enqueueTask(videoListener::streamStart);
        }
        this.newCallbacks.clear();
        started = true;
    }

    public void scheduleGrabJob(ScheduledThreadPoolExecutor executor){
        executor.setThreadFactory(r -> {
            Thread thread = new Thread(r);
            thread.setName("AV Grabbing Thread");
            return thread;
        });
        executor.execute(()->{
            try {

//                videoProfiler.startTick();
                this.loopVideo();
//                videoProfiler.endTick();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
        executor.scheduleAtFixedRate(() -> {
            try {
                if (this.isStarted()) {
                    this.loopAudio();
//                    this.audioProfiler.getResult().save(new File("./audio_profile.txt").toPath());
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }, 0,(long) 1000 / (audioRate * 2L), TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(System::gc,0,1000,TimeUnit.MILLISECONDS);
    }

    public String getTitle() {
        return title;
    }

    @Override
    public void stop() throws FrameGrabber.Exception {
        this.started = false;
        this.grabber.stop();
        this.grabber.close();
    }

    @Override
    public void loopVideo() throws FrameGrabber.Exception {
        Frame capturedFrame;

        while ((capturedFrame = grabber.grab()) != null && this.isStarted()){
//            this.videoProfiler.startTick();
//            this.videoProfiler.push("grab_frame");
//            long cost = System.currentTimeMillis() - lastFrameTime;
//            if (cost > 1000 / this.frameRate){
//                System.err.println("Grabbing overtimed " + cost);
//            }
//            lastFrameTime = System.currentTimeMillis();
//            long taskstart = System.currentTimeMillis();
            Frame clone = capturedFrame.clone();
            List<VideoListener> callback = this.videoCallback;
            for (int i = 0, callbackSize = callback.size(); i < callbackSize; i++) {
                VideoListener videoListener = callback.get(i);
                TaskThread taskThread = this.threadMap.get(videoListener);
                if (videoListener.isStarted()){
                    clone.timestamp = System.currentTimeMillis();
                    taskThread.enqueueTask(()-> {
                        try {
//                            this.videoProfiler.push(videoListener.getName());
                            videoListener.onFrame(clone);
//                            this.videoProfiler.pop();
                        } catch (FrameRecorder.Exception e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    });
                }else if (videoListener.isStopped()){
                    this.videoCallback.remove(videoListener);
                    this.threadMap.remove(videoListener);
                    if (taskThread != null) {
                        taskThread.enqueueTask(() -> {
                            videoListener.onTerminated();
                            videoListener.onStopped();
                            taskThread.stopExecuting();
                        });
                    }
                }
            }
//            this.videoProfiler.pop();
//            this.videoProfiler.endTick();
//            this.videoCallback.entrySet().removeIf((entry) ->{
//                if (!entry.getKey().isStarted()) {
//                    entry.getValue().enqueueTask(()-> entry.getKey().onStopped());
//                    return true;
//                }
//                return false;
//            });
//            System.out.println("video task cost "+ (System.currentTimeMillis()-taskstart));


        }
    }

    public synchronized void reopenAudioChannel(){
        this.dataLine.stop();
        this.dataLine.close();
        try {
            this.dataLine.open();
            this.dataLine.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void loopAudio() {
//                this.audioProfiler.push("grab_audio");
//                this.audioProfiler.push("read_data_line");
        if (dataLine.available() != 0) {
            long l = System.currentTimeMillis();
            AudioSample audioSample = AudioSample.readFromDataLine(this.dataLine,audioRate);
//                     this.dataLine.drain();
//                    System.out.println(dataLine.available());
//                    AudioSample audioSample2 = AudioSample.readFromDataLine(this.dataLine);
//                this.audioProfiler.swap("callbacks");
            ProfileType.AUDIO_READ_TIME.updateValue((System.currentTimeMillis() - l));
            List<AudioListener> callback = this.audioCallback;
            for (int i = 0, callbackSize = callback.size(); i < callbackSize; i++) {
                AudioListener audioListener = callback.get(i);
                TaskThread taskThread = this.threadMap.get(audioListener);
                if (audioListener.isStarted()) {
//                        this.audioProfiler.push("call_" + audioListener.getName());
                    taskThread.enqueueTask(() -> {
                        try {
//                        this.audioProfiler.push(audioListener.getName());
                            ProfileType.AUDIO_QUEUE_SIZE.updateValue(taskThread.getTaskCount());

                            audioListener.onSample(audioSample.clone());

//                        this.audioProfiler.pop();
                        } catch (FrameRecorder.Exception e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    });
//                        this.audioProfiler.pop();
                } else if (audioListener.isStopped()) {
                    this.audioCallback.remove(audioListener);
                    this.threadMap.remove(audioListener);
                    if (taskThread != null) {
                        taskThread.enqueueTask(() -> {
                            audioListener.onTerminated();
                            audioListener.onStopped();
                            taskThread.stopExecuting();
                        });
                    }
                }
            }
        }
//                this.audioProfiler.pop();
    }

    @Override
    public double getGamma() {
        return this.grabber.getGamma();
    }

    public boolean isStarted() {
        return started;
    }

    @Override
    public String getName() {
        return "Grabber";
    }

    @Override
    public void init(EmptyConfig config) {
        this.addKeyCallback(new KeyBind(Keys.VK_R, new KeyModifier(KeyModifier.Modifier.CONTROL, KeyModifier.Modifier.SHIFT, KeyModifier.Modifier.ALT)),
                Utils.safeRun(this::reopenAudioChannel,true));
        this.addKeyCallback(new KeyBind(Keys.VK_C, new KeyModifier(KeyModifier.Modifier.CONTROL, KeyModifier.Modifier.ALT, KeyModifier.Modifier.SHIFT)),
                Utils.safeRun(() -> {
                    dataLine.switchNext();
                    if (dataLine.getCurrentLine() instanceof CaptureDataLine){
                        dataLine.getCurrentLine().drain();
                    }
                    this.sendMessage("切换音频输入:" + dataLine.getCurrentLine().getLineInfo());
                }, true));
    }

    @Override
    public void reloadConfig(EmptyConfig config) {

    }

    @Override
    public void tick() {

    }
}
