package net.xiaoyu233.classkit.av;

import net.xiaoyu233.classkit.util.Utils;
import org.bytedeco.javacpp.indexer.FloatIndexer;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.UMat;

import static org.bytedeco.opencv.global.opencv_core.ACCESS_RW;
import static org.bytedeco.opencv.global.opencv_core.CV_32F;

public class ImageProcessing {
    private static final Mat kernel = Utils.make(new Mat(3, 3,CV_32F, new Scalar(0)),(kernel)->{
        FloatIndexer indexer = kernel.createIndexer();
        indexer.put(1, 1, 3);
        indexer.put(0, 1, -0.5f);
        indexer.put(2, 1, -0.5f);
        indexer.put(1, 0, -0.5f);
        indexer.put(1, 2, -0.5f);
    });

    private static final UMat uKernel = kernel.getUMat(ACCESS_RW);

    public static UMat sharpenImage(UMat src){
        UMat uMat = new UMat();
        opencv_imgproc.filter2D(src,uMat,src.depth(),uKernel);
        return uMat;
    }
}
