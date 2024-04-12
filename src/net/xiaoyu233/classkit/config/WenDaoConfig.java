package net.xiaoyu233.classkit.config;

import net.xiaoyu233.classkit.util.FieldReference;

import java.io.File;

public class WenDaoConfig extends ToolConfig{
    private final FieldReference<File> wendaoExecutable = new FieldReference<>(new File("D:\\东方闻道多媒体直播教学平台\\UnitedClass.exe"));
    private final FieldReference<File> wendaoWorkDir = new FieldReference<>(new File("D:\\东方闻道多媒体直播教学平台"));
    private final ConfigRegistry REGISTRY =
            new ConfigRegistry(ConfigCategory.of("东方闻道设置").
                                       addEntry(ConfigEntry.of("wendao_executable",wendaoExecutable)).
                                       addEntry(ConfigEntry.of("wendao_work_dir",wendaoWorkDir)),
                               new File("wendao_config.json"));

    public File getWendaoExecutable() {
        return wendaoExecutable.get();
    }

    public File getWendaoWorkDir() {
        return wendaoWorkDir.get();
    }

    @Override
    public ConfigRegistry getConfig() {
        return REGISTRY;
    }

    @Override
    public void reload() {

    }
}
