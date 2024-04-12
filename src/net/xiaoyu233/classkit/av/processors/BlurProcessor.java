package net.xiaoyu233.classkit.av.processors;

import net.xiaoyu233.classkit.av.ImageProcessor;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.UMat;

public class BlurProcessor implements ImageProcessor {
    private int d = 5;

    @Override
    public UMat process(UMat image) {
        if (d > 0){
            UMat mat = new UMat();
            opencv_imgproc.bilateralFilter(image,mat,d,50,50);
            return mat;
        }else return image.clone();
    }

    public int getD() {
        return d;
    }

    public void setD(int d) {
        this.d = Math.max(0,d);
    }
}
