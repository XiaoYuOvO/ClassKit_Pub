package net.xiaoyu233.classkit.gui;

import org.bytedeco.javacv.CanvasFrame;

import javax.swing.*;
import java.awt.*;

public class RenderFrame extends CanvasFrame {
    public RenderFrame(String title,  DisplayMode displayMode, double gamma) throws Exception {
        super(title, gamma);
        this.setBounds(0,0,displayMode.getWidth(),displayMode.getHeight());
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
    }

    @Override
    public GraphicsConfiguration getGraphicsConfiguration() {
        if (!this.isVisible()){
            setUndecorated(true);
        }
        return super.getGraphicsConfiguration();
    }

    public double getInverseGamma(){
        return inverseGamma;
    }

    public void setInverseGamma(double inverseGamma){
        this.inverseGamma = inverseGamma;
    }

}
