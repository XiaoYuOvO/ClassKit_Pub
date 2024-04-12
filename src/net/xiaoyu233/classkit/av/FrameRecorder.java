package net.xiaoyu233.classkit.av;

import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.opencv_core.Mat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;

public class FrameRecorder extends FrameReceiver implements AudioListener {
    private final FFmpegFrameRecorder recorder;
    private long startTime = 0;
    private int frameCount = 0;
    private long lastFrameTime;

    public FrameRecorder(File outputFile, int width, int height) throws FileNotFoundException {
        try {
            outputFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.recorder = new FFmpegFrameRecorder(outputFile, width, height, 2);
        recorder.setInterleaved(false);

        // decrease "startup" latency in FFMPEG (see:
        // https://trac.ffmpeg.org/wiki/StreamingGuide)
//        recorder.setVideoOption("tune", "stillimage");
        // tradeoff between quality and encode speed
        // possible values are ultrafast,superfast, veryfast, faster, fast,
        // medium, slow, slower, veryslow
        // ultrafast offers us the least amount of compression (lower encoder
        // CPU) at the cost of a larger stream size
        // at the other end, veryslow provides the best compression (high
        // encoder CPU) while lowering the stream size
        // (see: https://trac.ffmpeg.org/wiki/Encode/H.264)
        recorder.setVideoOption("preset", "slow");
        // Constant Rate Factor (see: https://trac.ffmpeg.org/wiki/Encode/H.264)
        //Use CRF 28 if is hevc
        recorder.setVideoOption("crf", "25");
        recorder.setVideoOption("threads","8");
        recorder.setVideoOption("profile", "main");
//        recorder.setVideoOption("x265-params","ref=7");
//        recorder.setVideoOption("level", "5.1");
        recorder.setVideoOption("psy", "1");
        // 2000 kb/s, reasonable "sane" area for 720
//        recorder.setVideoBitrate(450_000);
//        recorder.setVideoOption("tune","stillimage");
        recorder.setVideoCodecName("libx265");
//        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
//        Field video_c = recorder.getClass()
//                .getField("video_c");
//        AVCodecContext ctx = ((AVCodecContext) video_c.get(recorder));
//        AVHWAccel hwaccel = ctx.hwaccel();
        recorder.setFormat("mp4");
        // FPS (frames per second)
        recorder.setFrameRate(15);
//        recorder.setVideoMetadata();
        // Key frame interval, in our case every 2 seconds -> 30 (fps) * 2 = 60
        // (gop length)
//        recorder.setGopSize(15*10);

        // We don't want variable bitrate audio
//        recorder.setAudioOption("vbr", "constrained");
//        recorder.setOption("strict", "-2");
//        recorder.setAudioOption("application", "voip");
        // Highest quality
//        recorder.setAudioQuality(2);
        // 128 Kbps
        recorder.setSampleRate(48000);
        recorder.setAudioBitrate(80_000);
        recorder.setAudioChannels(1);
        recorder.setAudioCodecName("libmp3lame");
//        recorder.setAudioOption("profile","aac_he");
//        recordThread = new TaskThread((int) (1000 / this.recorder.getFrameRate()));
    }

    @Override
    public void onFrame(Frame frame) {
        this.lastFrameTime = System.currentTimeMillis();
        if (this.isStarted()) {
            try {
                if (frame.imageStride > 0 && frame.imageChannels > 0 && frame.imageHeight > 0 && frame.imageWidth > 0) {
                    long videoTS = (frame.timestamp - startTime) * 1000;

                    // Check for AV drift
                    if (videoTS > recorder.getTimestamp()) {
//                            System.out.println(
//                                    "Lip-flap video correction: "
//                                            + videoTS + " : "
//                                            + recorder.getTimestamp() + " -> "
//                                            + (videoTS - recorder.getTimestamp()));

                        // We tell the recorder to write this frame at this timestamp
                        recorder.setTimestamp(videoTS);
                    }
                    if (this.hasProcessor()) {
                        Mat m = this.processImage(frame);
                        this.recorder.record(this.covertFrame(m));
                        m.close();
                    } else {
                        this.recorder.record(frame);
                    }
                }
            } catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public synchronized void streamStart() {
        if (startTime == 0 && !this.isStarted()) {
            try {
                this.recorder.start();
                super.streamStart();
                this.startTime = System.currentTimeMillis();
            } catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getName() {
        return "frame_recorder";
    }

    @Override
    public void requestStop() throws org.bytedeco.javacv.FrameRecorder.Exception {
        super.requestStop();
//        recorder.flush();
    }

    @Override
    public void onStopped() {

        try {

            this.recorder.stop();
        } catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("Recorded time:" + (System.currentTimeMillis() - this.startTime));

    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public void onSample(AudioSample sample) throws org.bytedeco.javacv.FrameRecorder.Exception {
//        ProfilerSystem.PROFILER.push("recording_audio");
        if (this.isStarted()) {
            long videoTS = (sample.getTimestamp() - startTime) * 1000;

            // Check for AV drift
            if (videoTS > recorder.getTimestamp()) {
//                    System.out.println(
//                            "Lip-flap audio correction: "
//                                    + videoTS + " : "
//                                    + recorder.getTimestamp() + " -> "
//                                    + (videoTS - recorder.getTimestamp()));

                // We tell the recorder to write this frame at this timestamp
                recorder.setTimestamp(videoTS);
            }
            this.recorder.recordSamples(sample.getSampleRate(), sample.getChannelCount(), sample.getData());
        }
//        ProfilerSystem.PROFILER.pop();
    }
}
