package net.xiaoyu233.classkit.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ConfigCategory extends Config {
    private final List<Config> child = new ArrayList<>();
    public ConfigCategory(String name) {
        super(name);
    }

    public static ConfigCategory of(String name){
        return new ConfigCategory(name);
    }

    public ConfigCategory addEntry(Config entry){
        this.child.add(entry);
        return this;
    }

    public List<Config> getChild() {
        return child;
    }

    @Override
    @Nonnull
    public ReadResult read(JsonElement json) {
        try {
            if (json.isJsonObject()){
                JsonObject obj = ((JsonObject) json);
                boolean oneChanged = false;
                for (Config config : this.child) {
                    String name = config.getName();
                    ReadResult result = config.read(obj.get(name));
                    if (result.isDirty()){
                        oneChanged = true;
                        if (obj.has(name)){
                            obj.remove(name);
                        }
                        obj.add(name,result.getChanged());
                    }
                }
                if (oneChanged){
                    return ReadResult.ofChanged(obj);
                }
            }
            return ReadResult.NO_CHANGE;
        }catch (Throwable t) {
            System.err.println("Cannot read config: " + this.getName());
            t.printStackTrace();
            return ReadResult.ofChanged(this.writeDefault());
        }
    }

    @Override
    @Nonnull
    public JsonElement write() {
        try {
            JsonObject result = new JsonObject();
            for (Config config : this.child) {
                result.add(config.getName(), config.write());
            }
            return result;
        } catch (Throwable t) {
            System.err.println("Cannot write config: " + this.getName());
            t.printStackTrace();
            return new JsonObject();
        }
    }

    @Override
    public JsonElement writeDefault() {
        JsonObject result = new JsonObject();
        for (Config config : child) {
            result.add(config.getName(),config.writeDefault());
        }
        return result;
    }
}
