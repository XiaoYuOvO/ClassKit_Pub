package net.xiaoyu233.classkit.tools;

import com.telchina.zhzd.common.volume.VolumeControl;
import net.xiaoyu233.classkit.config.VolumeToolConfig;
import net.xiaoyu233.classkit.util.Utils;

import java.util.Objects;

public class VolumeTool extends Tool<VolumeToolConfig>{
    private final VolumeControl volumeControl = Objects.requireNonNull(VolumeControl.getInstance());
    @Override
    public String getName() {
        return "Volume Control";
    }

    @Override
    public void init(VolumeToolConfig config) {
        this.addKeyCallback(config.getIncrease(), Utils.safeRun(()->{
            volumeControl.setMasterVolume(volumeControl.getMasterVolume() + config.getStep());
            this.sendMessage("当前音量: " + volumeControl.getMasterVolume());
        },false));
        this.addKeyCallback(config.getDecrease(), Utils.safeRun(()->{
            volumeControl.setMasterVolume(volumeControl.getMasterVolume() - config.getStep());
            this.sendMessage("当前音量: " + volumeControl.getMasterVolume());
        },false));
        this.addKeyCallback(config.getMute(), Utils.safeRun(()->{
            volumeControl.setMute(true);
            this.sendMessage("已静音");
        },false));
        this.addKeyCallback(config.getUnmute(), Utils.safeRun(()->{
            volumeControl.setMute(false);
            this.sendMessage("已解除静音");
        },false));
    }

    @Override
    public void reloadConfig(VolumeToolConfig config) {

    }

    @Override
    public void tick() {

    }

    @Override
    public void clear() {
        super.clear();
        volumeControl.finalize();
    }
}
