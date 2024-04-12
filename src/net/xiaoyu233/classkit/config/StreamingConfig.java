package net.xiaoyu233.classkit.config;

import net.xiaoyu233.classkit.util.FieldReference;

import javax.annotation.Nonnull;
import java.io.File;

public class StreamingConfig extends ToolConfig{
    private static final FieldReference<Integer> PORT = new FieldReference<>(8888);
    private static final FieldReference<Integer> WIDTH = new FieldReference<>(1366);
    private static final FieldReference<Integer> HEIGHT = new FieldReference<>(768);
    private static final ConfigRegistry REGISTRY = new ConfigRegistry(ConfigCategory.of("streaming").addEntry(ConfigEntry.of("port",PORT)),new File("streaming.json"));
    @Nonnull
    @Override
    public ConfigRegistry getConfig() {
        return REGISTRY;
    }

    public int getPort(){
        return PORT.get();
    }

    public int getWidth(){
        return WIDTH.get();
    }

    public int getHeight(){
        return HEIGHT.get();
    }

    public void setWidth(int width){
        WIDTH.set(width);
    }

    public void setHeight(int height){
        WIDTH.set(height);
    }


    @Override
    public void reload() {

    }
}
