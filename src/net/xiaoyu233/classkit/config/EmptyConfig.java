package net.xiaoyu233.classkit.config;


import javax.annotation.Nonnull;
import java.io.File;

public class EmptyConfig extends ToolConfig {
    public static final EmptyConfig INSTANCE = new EmptyConfig();

    @Override
    @Nonnull
    public ConfigRegistry getConfig() {
        return ConfigRegistry.EMPTY;
    }

    @Override
    public void reload() {

    }
}
