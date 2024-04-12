package net.xiaoyu233.classkit.config;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.xiaoyu233.classkit.api.LessonTable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public abstract class Codec<T> {
    private static final Map<Class<?>,Codec<?>> types = new HashMap<>();
    public static final Codec<Boolean> BOOLEAN = new Codec<Boolean>(Boolean.class) {
        @Override
        public Boolean read(JsonElement json) {
            return json.getAsBoolean();
        }
        @Override
        public JsonElement write(Boolean value) {
            return new JsonPrimitive(value);
        }
    };
    public static final Codec<Double> DOUBLE = new Codec<Double>(Double.class) {
        @Override
        public Double read(JsonElement json) {
            return json.getAsDouble();
        }
        @Override
        public JsonElement write(Double value) {
            return new JsonPrimitive(value);
        }
    };
    public static final Codec<Float> FLOAT = new Codec<Float>(Float.class) {
        @Override
        public Float read(JsonElement json) {
            return json.getAsFloat();
        }
        @Override
        public JsonElement write(Float value) {
            return new JsonPrimitive(value);
        }
    };
    public static final Codec<File> FILE = new Codec<File>(File.class) {
        @Override
        public File read(JsonElement json) {
            return new File(json.getAsString());
        }
        @Override
        public JsonElement write(File value) {
            return new JsonPrimitive(value.toString());
        }
    };
    public static final Codec<Integer> INTEGER = new Codec<Integer>(Integer.class) {
        @Override
        public Integer read(JsonElement json) {
            return json.getAsInt();
        }
        @Override
        public JsonElement write(Integer value) {
            return new JsonPrimitive(value);
        }
    };
    public static final Codec<String> STRING = new Codec<String>(String.class) {
        @Override
        public String read(JsonElement json) {
            return json.getAsString();
        }
        @Override
        public JsonElement write(String value) {
            return new JsonPrimitive(value);
        }
    };
    public static final Codec<LessonTable> LESSON_TABLE = new Codec<LessonTable>(LessonTable.class){
        @Override
        public LessonTable read(JsonElement json) {
            return null;
        }
        @Override
        public JsonElement write(LessonTable value) {
            return null;
        }
    };
//    public static final Codec<Enum<?>> ENUM = new Codec<Enum<?>>() {
//        @Override
//        public Enum<?> read(JsonElement json) {
//            return null;
//        }
//
//        @Override
//        public JsonElement write(Enum<?> value) {
//            return null;
//        }
//    }
    private Codec(Class<T> typeClass){
        types.put(typeClass,this);
    }
    public static <T> Codec<List<T>> forList(Codec<T> raw){
        return new Codec<List<T>>((Class<List<T>>) ((List<T>)new ArrayList<T>()).getClass()) {
            @Override
            public List<T> read(JsonElement json) {
                List<T> list = new ArrayList<>();
                if (json.isJsonArray()){
                    for (JsonElement jsonElement : json.getAsJsonArray()) {
                        list.add(raw.read(jsonElement));
                    }
                }
                return list;
            }
            @Override
            public JsonElement write(List<T> value) {
                JsonArray result = new JsonArray();
                value.forEach(value1 -> result.add(raw.write(value1)));
                return result;
            }
        };
    }
    public static <T> Codec<T> getFromClass(Class<T> clazz){
        return (Codec<T>) types.get(clazz);
    }
    public abstract T read(JsonElement json);
    public abstract JsonElement write(T value);
}
