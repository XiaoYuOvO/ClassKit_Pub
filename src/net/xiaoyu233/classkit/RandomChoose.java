package net.xiaoyu233.classkit;

import net.xiaoyu233.classkit.api.Class;
import net.xiaoyu233.classkit.api.ClassManager;
import net.xiaoyu233.classkit.api.ClassSize;
import net.xiaoyu233.classkit.io.ClassCodec;
import net.xiaoyu233.classkit.gui.RandomWindowMain;

import java.io.File;
import java.io.IOException;

public class RandomChoose {
    public static void main(String[] args) {
        try {
            new RandomWindowMain(ClassCodec.ClassDeserializer.readFromFile(new File("Class.json")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
