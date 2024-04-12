package net.xiaoyu233.classkit.tools;

import net.xiaoyu233.classkit.av.AudioProvider;
import net.xiaoyu233.classkit.av.AudioReplayer;
import net.xiaoyu233.classkit.config.AudioPlaybackConfig;
import net.xiaoyu233.classkit.event.SilenceEvent;
import net.xiaoyu233.classkit.keys.KeyBind;
import net.xiaoyu233.classkit.keys.KeyModifier;
import net.xiaoyu233.classkit.keys.Keys;
import net.xiaoyu233.classkit.managment.EventManager;
import net.xiaoyu233.classkit.util.Utils;

import javax.sound.sampled.LineUnavailableException;
import java.text.DecimalFormat;

public class AudioPlaybackTool extends Tool<AudioPlaybackConfig>{
    private static final DecimalFormat FORMAT = new DecimalFormat("#.##");
    private AudioReplayer replayer;
    private boolean silenceNow;
    private int silenceCount;
    @Override
    public String getName() {
        return "AudioPlayback";
    }

    @Override
    public void init(AudioPlaybackConfig config) {
        try {
            replayer = new AudioReplayer(Utils.SOURCE_DATA_LINE);
            KeyBind up = new KeyBind(Keys.VK_PAGE_UP, new KeyModifier(KeyModifier.Modifier.CONTROL));
            this.addKeyCallback(up, Utils.safeRun(() -> {
                this.replayer.setTargetSPL(replayer.getTargetSPL() + 0.2d);
                this.sendMessage("目标声强:" + FORMAT.format(replayer.getTargetSPL()) + "dBSPL");
            }, true));
            KeyBind down = new KeyBind(Keys.VK_PAGE_DOWN, new KeyModifier(KeyModifier.Modifier.CONTROL));
            this.addKeyCallback(down, Utils.safeRun(() -> {
                this.replayer.setTargetSPL(replayer.getTargetSPL() - 0.2d);
                this.sendMessage("目标声强:" + FORMAT.format(replayer.getTargetSPL()) + "dBSPL");
            }, true));

            KeyBind sup = new KeyBind(Keys.VK_PAGE_UP, new KeyModifier(KeyModifier.Modifier.CONTROL, KeyModifier.Modifier.ALT));
            this.addKeyCallback(sup, Utils.safeRun(() -> {
                this.replayer.setForceVolumeGain(replayer.getForceVolumeGain() + 0.05f);
                this.sendMessage("音量倍增:" + FORMAT.format(replayer.getForceVolumeGain()) + "倍");
            }, true));
            KeyBind sdown = new KeyBind(Keys.VK_PAGE_DOWN, new KeyModifier(KeyModifier.Modifier.CONTROL, KeyModifier.Modifier.ALT));
            this.addKeyCallback(sdown, Utils.safeRun(() -> {
                this.replayer.setForceVolumeGain(replayer.getForceVolumeGain() - 0.05f);
                this.sendMessage("音量倍增:" + FORMAT.format(replayer.getForceVolumeGain()) + "倍");
            }, true));

            KeyBind sadowm = new KeyBind(Keys.VK_PAGE_DOWN, new KeyModifier(KeyModifier.Modifier.CONTROL, KeyModifier.Modifier.ALT, KeyModifier.Modifier.SHIFT));
            KeyBind saup = new KeyBind(Keys.VK_PAGE_UP, new KeyModifier(KeyModifier.Modifier.CONTROL, KeyModifier.Modifier.ALT, KeyModifier.Modifier.SHIFT));
            this.addKeyCallback(sadowm, Utils.safeRun(() -> {
                this.replayer.setSilenceThreshold(replayer.getSilenceThreshold() - 0.1f);
                this.sendMessage("静音阈值:" + FORMAT.format(replayer.getSilenceThreshold()) + "dBSPL");
            }, true));
            this.addKeyCallback(saup, Utils.safeRun(() -> {
                this.replayer.setSilenceThreshold(replayer.getSilenceThreshold() + 0.1f);
                this.sendMessage("静音阈值:" + FORMAT.format(replayer.getSilenceThreshold()) + "dBSPL");
            }, true));

            KeyBind scadowm = new KeyBind(Keys.VK_PAGE_DOWN, new KeyModifier(KeyModifier.Modifier.CONTROL, KeyModifier.Modifier.SHIFT));
            KeyBind scaup = new KeyBind(Keys.VK_PAGE_UP, new KeyModifier(KeyModifier.Modifier.CONTROL, KeyModifier.Modifier.SHIFT));
            this.addKeyCallback(scadowm, Utils.safeRun(() -> {
                this.replayer.setSplitThreshold(replayer.getSplitThreshold() - 0.1f);
                this.sendMessage("分离阈值:" + FORMAT.format(replayer.getSplitThreshold()) + "dBSPL");
            }, true));
            this.addKeyCallback(scaup, Utils.safeRun(() -> {
                this.replayer.setSplitThreshold(replayer.getSplitThreshold() + 0.1f);
                this.sendMessage("分离阈值:" + FORMAT.format(replayer.getSplitThreshold()) + "dBSPL");
            }, true));

            KeyBind normalize = new KeyBind(Keys.VK_N, new KeyModifier(KeyModifier.Modifier.CONTROL, KeyModifier.Modifier.ALT));
            this.addKeyCallback(normalize, Utils.safeRun(() -> {
                this.replayer.enableNormalize();
                this.sendMessage("启用响度均一化");
            }, true));
            KeyBind disnormalize = new KeyBind(Keys.VK_N, new KeyModifier(KeyModifier.Modifier.CONTROL, KeyModifier.Modifier.ALT, KeyModifier.Modifier.SHIFT));
            this.addKeyCallback(disnormalize, Utils.safeRun(() -> {
                this.replayer.disableNormalize();
                this.sendMessage("禁用响度均一化");
            }, true));

            KeyBind mute = new KeyBind(Keys.VK_M, new KeyModifier(KeyModifier.Modifier.CONTROL, KeyModifier.Modifier.SHIFT , KeyModifier.Modifier.ALT));
            this.addKeyCallback(mute, Utils.safeRun(() -> {
                this.replayer.setMute(true);
                this.sendMessage("已静音");
            }, true));
            KeyBind unmute = new KeyBind(Keys.VK_W, new KeyModifier(KeyModifier.Modifier.CONTROL,KeyModifier.Modifier.SHIFT, KeyModifier.Modifier.ALT));
            this.addKeyCallback(unmute, Utils.safeRun(() -> {
                this.replayer.setMute(false);
                this.sendMessage("已解除静音");
            }, true));

            KeyBind reopenChannel = new KeyBind(Keys.VK_R, new KeyModifier(KeyModifier.Modifier.CONTROL, KeyModifier.Modifier.SHIFT, KeyModifier.Modifier.ALT));
            this.addKeyCallback(reopenChannel, Utils.safeRun(() -> {
                this.replayer.reopenPlaybackChannel();
                this.sendMessage("已重新打开音频输出");
            }, true));

            this.replayer.addSilenceCallback((silence)-> this.silenceNow = silence);
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void registerEvent(EventManager eventManager) {
        super.registerEvent(eventManager);
    }

    public void registerThis(AudioProvider provider){
        provider.addAudioCallback(this.replayer);
    }

    @Override
    public void reloadConfig(AudioPlaybackConfig config) {

    }

    @Override
    public void tick() {
        if (this.silenceNow) {
            this.silenceCount++;
            if (this.silenceCount > 15){
                this.getEventManager().sendEvent(new SilenceEvent(true));
            }
        }else {
            if (this.silenceCount != 0){
                this.getEventManager().sendEvent(new SilenceEvent(false));
                this.silenceCount = 0;
            }
        }
    }
}
