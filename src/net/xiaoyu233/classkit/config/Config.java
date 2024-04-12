package net.xiaoyu233.classkit.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public abstract class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final String name;
    protected Config(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Nonnull
    public abstract ReadResult read(JsonElement json);

    @Nonnull
    public abstract JsonElement write();

    public void writeToFile(File cfgFile){
        File configFile = new File(cfgFile.toString());
        if (!configFile.exists()){
            this.createConfigFile(configFile);
        }
        try (FileWriter writer = new FileWriter(configFile)){
            GSON.toJson(this.write(),writer);
        } catch (IOException e) {
            System.err.println("Error in writing config");
            e.printStackTrace();
        }
    }
    private void createConfigFile(File configFile){
        try {
            configFile.getParentFile().mkdirs();
            if (!configFile.createNewFile()) {
                System.err.println("Cannot create config file");
            }else {
                try (FileWriter writer = new FileWriter(configFile)){
                    GSON.toJson(this.writeDefault(),writer);
                }
            }
        } catch (IOException e) {
            System.err.println("Cannot create config file");
            e.printStackTrace();
        }
    }
    public void readFromFile(File cfgFile){
        File configFile = new File(cfgFile.toString());
        ReadResult read = ReadResult.NO_CHANGE;
        if (!configFile.exists()){
            this.createConfigFile(configFile);
        }
        try (FileReader reader = new FileReader(configFile)){
            read= this.read(new JsonParser().parse(reader));
        }catch (Throwable e) {
            System.err.println("Error in reading config");
            e.printStackTrace();
        }
        try {
            if (read.isDirty()){
                try (FileWriter writer = new FileWriter(configFile)){
                    GSON.toJson(read.getChanged(),writer);
                }
            }
        }catch (Throwable e) {
            System.err.println("Error in writing config");
            e.printStackTrace();
        }
    }

    public abstract JsonElement writeDefault();

    public static class ReadResult{
        public static final ReadResult NO_CHANGE = new ReadResult(false,null);
        @Nullable
        private final JsonElement changed;
        private final boolean dirty;

        private ReadResult(boolean dirty, @Nullable JsonElement changed) {
            this.dirty = dirty;
            this.changed = changed;
        }

        public static ReadResult ofChanged(JsonElement changed){
            return new ReadResult(true,changed);
        }

        @Nullable
        public JsonElement getChanged() {
            return changed;
        }

        public boolean isDirty() {
            return dirty;
        }
    }
}
