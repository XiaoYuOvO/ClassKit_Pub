package net.xiaoyu233.classkit.av.processors;

import net.xiaoyu233.classkit.av.ImageProcessor;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_core.UMat;

public class ResizingProcess implements ImageProcessor {
    private final int width,height;
    private int interpolation = opencv_imgproc.INTER_CUBIC;

    public ResizingProcess(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public UMat process(UMat image) {
        UMat mat = new UMat();
        opencv_imgproc.resize(image,mat, new Size(this.width, this.height), 0, 0, interpolation);
        return mat;
    }

    public String getInterpolationName(){
        return switch (this.interpolation) {
            case 0 -> "INTER_NEAREST";
            case 1 -> "INTER_LINEAR";
            case 2 -> "INTER_CUBIC";
            case 3 -> "INTER_AREA";
            case 4 -> "INTER_LANCZOS4";
            case 5 -> "INTER_LINEAR_EXACT";
            case 7 -> "INTER_MAX";
            default -> "NONE";
        };
    }

    public void switchInterpolationMethod(){
        if (this.interpolation >= opencv_imgproc.INTER_LINEAR_EXACT) {
            this.interpolation = 0;
        }else {
            this.interpolation++;
        }
    }
}
