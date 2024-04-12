package net.xiaoyu233.classkit.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.xiaoyu233.classkit.util.FieldReference;

public class ConfigEntry<T> extends Config {
    private final Codec<T> codec;
    private final FieldReference<T> configRef;
    private final T defaultValue;
    protected String comment = null;
    public ConfigEntry(String name, Codec<T> codec, T defaultValue, FieldReference<T> configRef) {
        super(name);
        this.codec = codec;
        this.defaultValue = defaultValue;
        this.configRef = configRef;
    }
    public ConfigEntry(String name,FieldReference<T> configRef) {
        super(name);
        this.codec = (Codec<T>) Codec.getFromClass(configRef.getValueClass());
        this.defaultValue = configRef.get();
        this.configRef = configRef;
    }
    public ConfigEntry(String name, Codec<T> codec,FieldReference<T> configRef) {
        super(name);
        this.codec = codec;
        this.defaultValue = configRef.get();
        this.configRef = configRef;
    }

    public String getComment() {
        return comment;
    }

    public Codec<T> getCodec() {
        return codec;
    }

    public T getCurrentValue(){
        return configRef.get();
    }

    public void setCurrentValue(T value){
        configRef.set(value);
    }

    public static <T> ConfigEntry<T> of(String name, FieldReference<T> configRef){
        return new ConfigEntry<>(name, configRef);
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public Config.ReadResult read(JsonElement json) {
        try {
            if (json != null){
                if (json.isJsonObject()){
                    this.configRef.set(this.codec.read(json.getAsJsonObject().get("value")));
                    if (!json.getAsJsonObject().get("_comment").getAsString().equals(this.comment)) {
                        return Config.ReadResult.ofChanged(this.writeWithValue(this.configRef.get()));
                    }
                }else {
                    this.configRef.set(this.codec.read(json));
                    if (this.comment != null && !this.comment.isEmpty()){
                        return Config.ReadResult.ofChanged(this.writeWithValue(this.configRef.get()));
                    }
                }
                return Config.ReadResult.NO_CHANGE;
            }else {
                this.configRef.set(this.defaultValue);
                return Config.ReadResult.ofChanged(this.writeDefault());
            }
        }catch (Throwable t) {
            System.err.println("Cannot read config: " + this.getName());
            t.printStackTrace();
            this.configRef.set(this.defaultValue);
            return Config.ReadResult.ofChanged(this.writeDefault());
        }
    }

    @Override
    public JsonElement write() {
        return this.writeWithValue(this.configRef.get());
    }

    public ConfigEntry<T> withComment(String comment) {
        this.comment = comment;
        return this;
    }

    @Override
    public JsonElement writeDefault() {
        return this.writeWithValue(this.defaultValue);
    }

    private JsonElement writeWithValue(T value) {
        if (this.comment != null){
            JsonObject json = new JsonObject();
            json.addProperty("_comment",this.comment);
            json.add("value",this.codec.write(value));
            return json;
        }
        return this.codec.write(value);
    }
}
