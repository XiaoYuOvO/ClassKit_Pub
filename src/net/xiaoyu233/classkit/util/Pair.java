package net.xiaoyu233.classkit.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.xiaoyu233.classkit.api.unit.Student;
import net.xiaoyu233.classkit.io.IJsonHolder;

import java.io.IOException;

public class Pair<T> {
    public static final Pair<Student> EMPTY = new Pair<>(Student.EMPTY,Student.EMPTY);
    public final TypeAdapter<Pair<T>> codec = new TypeAdapter<Pair<T>>() {
        @Override
        public void write(JsonWriter out, Pair<T> value) {
            JsonObject jsonObject = new JsonObject();
            if (value.getLeft() instanceof IJsonHolder) {
                jsonObject.add("left",((IJsonHolder<T>) value.getLeft()).getAdapter().toJsonTree(value.getLeft()));
                jsonObject.add("right",((IJsonHolder<T>) value.getRight()).getAdapter().toJsonTree(value.getRight()));
                new Gson().toJson(jsonObject,out);
            }
        }

        @Override
        public Pair<T> read(JsonReader in) {
            JsonObject asJsonObject = JsonParser.parseReader(in).getAsJsonObject();
            T leftS = null,rightS = null;
            if (Pair.this.left instanceof IJsonHolder) {
                TypeAdapter<T> adapter = ((IJsonHolder<T>) Pair.this.left).getAdapter();
                JsonObject left = asJsonObject.get("left").getAsJsonObject();
                JsonObject right = asJsonObject.get("right").getAsJsonObject();
                leftS = adapter.fromJsonTree(left);
                rightS = adapter.fromJsonTree(right);
            }
            return new Pair<>(leftS, rightS);
        }
    };
    private T left,right;

    public  Pair(T left,T right){
        this.left = left;
        this.right = right;
    }
    public T getLeft() {
        return left;
    }

    public T getRight() {
        return right;
    }

    public void setLeft(T left) {
        this.left = left;
    }

    public void setRight(T right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "left=" + left +
                ", right=" + right +
                '}';
    }

    @Override
    public Pair<T> clone() {
        return new Pair<>(this.left,this.right);
    }
}
