package net.xiaoyu233.classkit.config;

import net.xiaoyu233.classkit.util.FieldReference;

import java.io.File;

public class AudioPlaybackConfig extends ToolConfig{
    private final FieldReference<String> TARGET_DEVICE_NAME = new FieldReference<>("Intel(R) Display");
    private final ConfigRegistry CONFIG = new ConfigRegistry(
            ConfigCategory.of("音频回放设置").
                    addEntry(ConfigEntry.of("target_device_name",TARGET_DEVICE_NAME).withComment("输出设备名称")),
            new File("audio_playback.json")
    );
    @Override
    public ConfigRegistry getConfig() {
        return CONFIG;
    }

    public String getTargetDeviceName() {
        return "Intel";
    }

    @Override
    public void reload() {

    }
}
