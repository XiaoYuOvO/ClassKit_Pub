package net.xiaoyu233.classkit.tools;

import net.xiaoyu233.classkit.config.ResolutionConfig;

import javax.swing.*;
import java.awt.*;

public class ResolutionTool extends Tool<ResolutionConfig>{
    private final JFrame frame = new JFrame();
    @Override
    public String getName() {
        return "Resolution Tool";
    }

    @Override
    public void init(ResolutionConfig config) {

        GraphicsDevice device = frame.getGraphicsConfiguration().getDevice();
        device.setFullScreenWindow(frame);
        device.setDisplayMode(config.getTarget());
        frame.setVisible(false);
    }

    @Override
    public void reloadConfig(ResolutionConfig config) {

    }

    @Override
    public void tick() {

    }

    @Override
    public void clear() {
        super.clear();
        this.frame.dispose();
    }
}
