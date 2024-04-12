package net.xiaoyu233.classkit.tools;

import net.xiaoyu233.classkit.av.FrameDisplayer;
import net.xiaoyu233.classkit.av.VideoProvider;
import net.xiaoyu233.classkit.av.processors.BlurProcessor;
import net.xiaoyu233.classkit.av.processors.ResizingProcess;
import net.xiaoyu233.classkit.av.processors.SharpeningProcess;
import net.xiaoyu233.classkit.config.DisplayerConfig;
import net.xiaoyu233.classkit.event.EventType;
import net.xiaoyu233.classkit.gui.RenderFrame;
import net.xiaoyu233.classkit.keys.KeyBind;
import net.xiaoyu233.classkit.keys.KeyModifier;
import net.xiaoyu233.classkit.keys.Keys;
import net.xiaoyu233.classkit.managment.EventManager;
import org.bytedeco.javacv.CanvasFrame;

import javax.swing.*;
import java.awt.event.FocusEvent;

public class DisplayerTool extends Tool<DisplayerConfig>{
    private FrameDisplayer displayer;

    public DisplayerTool() {
    }

    @Override
    public String getName() {
        return "Displayer";
    }

    @Override
    public void init(DisplayerConfig config) {
        try {
            displayer = new FrameDisplayer(1920,1080, CanvasFrame.getDefaultGamma());
            ResizingProcess resizing = new ResizingProcess(1920,1080);
            displayer.addProcessor(resizing);
            BlurProcessor blur = new BlurProcessor();
            displayer.addProcessor(blur);
            SharpeningProcess sharpe = new SharpeningProcess();
            displayer.addProcessor(sharpe);

            this.addKeyCallback(new KeyBind(Keys.VK_B, new KeyModifier(KeyModifier.Modifier.CONTROL, KeyModifier.Modifier.ALT)), ()->{
                blur.setD(blur.getD() + 1);
                this.sendMessage("模糊范围:" + blur.getD());
            });
            this.addKeyCallback(new KeyBind(Keys.VK_B, new KeyModifier(KeyModifier.Modifier.CONTROL, KeyModifier.Modifier.ALT, KeyModifier.Modifier.SHIFT)), ()->{
                blur.setD(blur.getD() - 1);
                this.sendMessage("模糊范围:" + blur.getD());
            });

            this.addKeyCallback(new KeyBind(Keys.VK_S, new KeyModifier(KeyModifier.Modifier.CONTROL, KeyModifier.Modifier.ALT)), ()->{
                sharpe.setSharpenTime(sharpe.getSharpenTime() + 1);
                this.sendMessage("锐化次数:" + sharpe.getSharpenTime());
            });
            this.addKeyCallback(new KeyBind(Keys.VK_S, new KeyModifier(KeyModifier.Modifier.SHIFT, KeyModifier.Modifier.CONTROL, KeyModifier.Modifier.ALT)), ()->{
                sharpe.setSharpenTime(sharpe.getSharpenTime() - 1);
                this.sendMessage("锐化次数:" + sharpe.getSharpenTime());
            });

            this.addKeyCallback(new KeyBind(Keys.VK_R, new KeyModifier(KeyModifier.Modifier.CONTROL, KeyModifier.Modifier.ALT)), ()->{
                resizing.switchInterpolationMethod();
                this.sendMessage("放大方法:" + resizing.getInterpolationName());
            });

            RenderFrame frame = displayer.getFrame();
            this.addKeyCallback(new KeyBind(Keys.VK_B, new KeyModifier(KeyModifier.Modifier.CONTROL, KeyModifier.Modifier.ALT)),()->{
                frame.setInverseGamma(frame.getInverseGamma() + 0.5f);
                this.sendMessage("伽马值:" + frame.getInverseGamma());
            });
            this.addKeyCallback(new KeyBind(Keys.VK_B, new KeyModifier(KeyModifier.Modifier.SHIFT, KeyModifier.Modifier.CONTROL, KeyModifier.Modifier.ALT)), ()->{
                frame.setInverseGamma(frame.getInverseGamma() - 0.5f);
                this.sendMessage("伽马值:" + frame.getInverseGamma());
            });
//            this.displayer.getFrame().setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        } catch (CanvasFrame.Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void registerEvent(EventManager eventManager) {
        super.registerEvent(eventManager);
        this.getEventManager().registerListener(EventType.CLASS_BEGIN, (e)->{
            RenderFrame displayerFrame = this.displayer.getFrame();
            displayerFrame.setVisible(false);
            displayerFrame.setVisible(true);
            displayerFrame.setAlwaysOnTop(true);
            displayerFrame.setAlwaysOnTop(false);
            displayerFrame.requestFocus(FocusEvent.Cause.ACTIVATION);
        });
    }

    public void registerThis(VideoProvider provider){
        provider.addVideoCallback(displayer);
    }

    @Override
    public void reloadConfig(DisplayerConfig config) {

    }

    @Override
    public void tick() {

    }
}
