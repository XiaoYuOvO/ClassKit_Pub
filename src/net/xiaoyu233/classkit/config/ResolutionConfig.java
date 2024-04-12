package net.xiaoyu233.classkit.config;

import net.xiaoyu233.classkit.util.FieldReference;

import java.awt.*;
import java.io.File;

public class ResolutionConfig extends ToolConfig {

    private static final FieldReference<Integer> WIDTH = new FieldReference<>(1280);
    private static final FieldReference<Integer> HEIGHT = new FieldReference<>(960);
    private static final FieldReference<Integer> BIT_DEPTH = new FieldReference<>(32);
    private static final FieldReference<Integer> REFRESH_RATE = new FieldReference<>(60);
    private static final ConfigRegistry CONFIG = new ConfigRegistry(
            ConfigCategory.of("分辨率设置").
                    addEntry(ConfigEntry.of("width",WIDTH).withComment("长")).
                    addEntry(ConfigEntry.of("height",HEIGHT).withComment("宽")).
                    addEntry(ConfigEntry.of("bit_depth",BIT_DEPTH).withComment("位深度")).
                    addEntry(ConfigEntry.of("refresh_rate",REFRESH_RATE).withComment("刷新率")),
            new File("resolution_config"));
    private DisplayMode target = new DisplayMode(1280,960,32,60);
    public DisplayMode getTarget() {
        return target;
    }

    @Override
    public ConfigRegistry getConfig() {
        return CONFIG;
    }

    @Override
    public void reload() {
        target = new DisplayMode(WIDTH.get(),HEIGHT.get(),BIT_DEPTH.get(),REFRESH_RATE.get());
    }
}
