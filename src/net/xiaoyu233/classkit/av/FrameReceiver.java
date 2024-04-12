package net.xiaoyu233.classkit.av;

import com.google.common.collect.Lists;
import org.bytedeco.javacpp.PointerScope;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.Java2DFrameUtils;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.UMat;

import java.awt.*;
import java.util.List;

public abstract class FrameReceiver implements VideoListener {
    private final List<ImageProcessor> processors = Lists.newArrayList();

    private final OpenCVFrameConverter.ToMat toMat = new OpenCVFrameConverter.ToMat();
    private volatile boolean started;
    private volatile boolean stopped;
    private final Java2DFrameConverter frameConverter = new Java2DFrameConverter();
    private volatile boolean hasTerminated;

    @Override
    public void streamStart() {
        this.started = true;
    }

    @Override
    public void addProcessor(ImageProcessor processor) {
        this.processors.add(processor);
    }

    protected Mat processImage(Mat src){
        UMat umat = src.getUMat(opencv_core.ACCESS_RW);
        src.release();
        for (ImageProcessor processor : processors) {
            UMat lastOut = umat;
            umat = processor.process(umat);
//            lastOut.close();
            lastOut.release();
        }
        Mat mat = new Mat();
        umat.copyTo(mat);
        umat.release();
        return mat;
    }

    @Override
    public void requestStop() throws FrameRecorder.Exception {
        this.started = false;
        this.stopped = true;
    }

    @Override
    public void onTerminated() {
        this.hasTerminated = true;
    }

    public boolean hasTerminated() {
        return hasTerminated;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public boolean isStopped() {
        return stopped;
    }

    protected Mat processImage(Frame f){
        return processImage(covertFrame(f));
    }

    protected Mat covertFrame(Frame f){
        return Java2DFrameUtils.toMat(f);
    }

    protected Frame covertFrame(Mat m){
        return Java2DFrameUtils.toFrame(m).clone();
    }

    protected Image covertFrameToImg(Frame f){
        try(PointerScope ignored =  new PointerScope()) {
            return frameConverter.convert(f);
        }
    }

    protected Image covertFrameToImg(Mat f){
        return this.frameConverter.getBufferedImage(this.covertFrame(f),1.0);
    }

    protected Image covertFrameToImg(Mat f, double gamma){
        try(PointerScope ignored =  new PointerScope()) {
            return this.frameConverter.getBufferedImage(this.covertFrame(f), gamma);
        }
    }

    public boolean hasProcessor(){
        return !this.processors.isEmpty();
    }
}
