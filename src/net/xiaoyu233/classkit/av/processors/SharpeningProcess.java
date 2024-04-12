package net.xiaoyu233.classkit.av.processors;

import net.xiaoyu233.classkit.av.ImageProcessing;
import net.xiaoyu233.classkit.av.ImageProcessor;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.UMat;

public class SharpeningProcess implements ImageProcessor {
    private int sharpenTime = 1;
    @Override
    public UMat process(UMat image) {
        if (sharpenTime == 0){
            return image.clone();
        }
        UMat ret = image;
        for (int i = 0; i < sharpenTime; i++) {
            UMat last = ret;
            ret = ImageProcessing.sharpenImage(last);
//            last.close();
        }
        return ret;
    }

    public int getSharpenTime() {
        return sharpenTime;
    }

    public void setSharpenTime(int sharpenTime) {
        this.sharpenTime = sharpenTime;
    }
}
