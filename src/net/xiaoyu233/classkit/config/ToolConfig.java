package net.xiaoyu233.classkit.config;

import javax.annotation.Nonnull;

public abstract class ToolConfig {
    @Nonnull
    public abstract ConfigRegistry getConfig();
    public abstract void reload();
}
