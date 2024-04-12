package net.xiaoyu233.classkit.av;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.UMat;

public interface ImageProcessor {
    UMat process(UMat image);
}
