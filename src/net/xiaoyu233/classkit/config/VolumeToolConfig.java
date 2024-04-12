package net.xiaoyu233.classkit.config;

import net.xiaoyu233.classkit.keys.KeyModifier;
import net.xiaoyu233.classkit.keys.Keys;
import net.xiaoyu233.classkit.keys.KeyBind;

import java.io.File;

public class VolumeToolConfig extends ToolConfig {
    private int step = 1;
    private KeyBind increase = new KeyBind(Keys.VK_PAGE_UP, new KeyModifier(KeyModifier.Modifier.CONTROL, KeyModifier.Modifier.ALT));
    private KeyBind decrease = new KeyBind(Keys.VK_PAGE_DOWN, new KeyModifier(KeyModifier.Modifier.CONTROL, KeyModifier.Modifier.ALT));
    private KeyBind mute = new KeyBind(Keys.VK_M, new KeyModifier(KeyModifier.Modifier.CONTROL, KeyModifier.Modifier.ALT));
    private KeyBind unmute = new KeyBind(Keys.VK_W, new KeyModifier(KeyModifier.Modifier.CONTROL, KeyModifier.Modifier.ALT));
    private static final ConfigRegistry CONFIG = new ConfigRegistry(
            ConfigCategory.of("音量控制"),
            new File("volume_control"));

    public KeyBind getIncrease() {
        return increase;
    }

    public void setIncrease(KeyBind increase) {
        this.increase = increase;
    }

    public KeyBind getDecrease() {
        return decrease;
    }

    public void setDecrease(KeyBind decrease) {
        this.decrease = decrease;
    }

    public KeyBind getMute() {
        return mute;
    }

    public void setMute(KeyBind mute) {
        this.mute = mute;
    }

    public KeyBind getUnmute() {
        return unmute;
    }

    public void setUnmute(KeyBind unmute) {
        this.unmute = unmute;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    @Override
    public ConfigRegistry getConfig() {
        return CONFIG;
    }

    @Override
    public void reload() {

    }
}
