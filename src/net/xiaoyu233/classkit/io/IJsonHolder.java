package net.xiaoyu233.classkit.io;

import com.google.gson.TypeAdapter;

public interface IJsonHolder<T> {
    TypeAdapter<T> getAdapter();
}
