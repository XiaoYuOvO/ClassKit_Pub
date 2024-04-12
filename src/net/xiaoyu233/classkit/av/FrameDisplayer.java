package net.xiaoyu233.classkit.av;

import net.xiaoyu233.classkit.gui.RenderFrame;
import net.xiaoyu233.classkit.util.profiler.ProfileType;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameRecorder;
import org.bytedeco.opencv.opencv_core.Mat;

import java.awt.*;

public class FrameDisplayer extends FrameReceiver {
    private final RenderFrame cFrame;
    private final int width, height;
    private Mat lastFrame;

    public FrameDisplayer(int width, int height, double gamma) throws CanvasFrame.Exception {
        this.cFrame = new RenderFrame("Capture Preview", new DisplayMode(width, height, 32, 60), CanvasFrame.getDefaultGamma() / gamma);
        this.width = width;
        this.height = height;
        cFrame.getGraphicsConfiguration()
                .getDevice()
                .setFullScreenWindow(null);
        cFrame.setBounds(new Rectangle(new Dimension(width, height)));
    }

    @Override
    public void onFrame(Frame frame) {
        if (cFrame.isVisible()) {
            if (this.hasProcessor()) {
//                if (lastFrame != null) {
//                    lastFrame.close();
//                }
                long grabTime = System.currentTimeMillis();
                 lastFrame = this.processImage(frame);
                    cFrame.showImage(covertFrameToImg(lastFrame,this.cFrame.getInverseGamma()));
                ProfileType.GRABBING_TIME.updateValue(System.currentTimeMillis() - grabTime);
            } else {

                try {
                    cFrame.showImage(covertFrameToImg(frame));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public void streamStart() {
        super.streamStart();

        this.cFrame.setVisible(true);

    };

    @Override
    public String getName() {
        return "frame_displayer";
    }

    @Override
    public void requestStop() throws FrameRecorder.Exception {
        super.requestStop();
        this.cFrame.dispose();
    }

    @Override
    public void onStopped() {

    }

    @Override
    public int getPriority() {
        return 0;
    }

    public RenderFrame getFrame() {
        return cFrame;
    }
}
