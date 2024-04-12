package net.xiaoyu233.classkit.config;

import net.xiaoyu233.classkit.util.FieldReference;

import java.io.File;

public class DisplayerConfig extends ToolConfig{
    private final FieldReference<Integer> WIDTH = new FieldReference<>(1920);
    private final FieldReference<Integer> HEIGHT = new FieldReference<>(1080);
    private final ConfigRegistry CONFIG =
            new ConfigRegistry(
                    ConfigCategory.of("渲染窗口设置").
                            addEntry(ConfigEntry.of("width",WIDTH).withComment("窗口宽度")).
                            addEntry(ConfigEntry.of("height",HEIGHT).withComment("窗口高度")),
                               new File("displayer.json"));
    @Override
    public ConfigRegistry getConfig() {
        return CONFIG;
    }

    public int getHeight() {
        return HEIGHT.get();
    }

    @Override
    public void reload() {

    }
}
