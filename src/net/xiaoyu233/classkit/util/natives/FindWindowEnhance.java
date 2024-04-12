package net.xiaoyu233.classkit.util.natives;


import org.apache.commons.compress.utils.IOUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FindWindowEnhance {
    static {
        try {
            System.loadLibrary("FindWindowEnhance");
        }catch (UnsatisfiedLinkError ignored){
            InputStream resourceAsStream = FindWindowEnhance.class.getResourceAsStream("/FindWindowEnhance.dll");
            File libFile = new File("./FindWindowEnhance.dll");
            if(resourceAsStream != null){
                try {
                    Files.copy(resourceAsStream,libFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new IllegalStateException("Cannot extract library FindWindowEnhance", e);
                }
            }else {
                throw new IllegalStateException("Cannot extract library FindWindowEnhance");
            }
            System.load(libFile.getAbsolutePath());
        }
    }
    public static native void injectFindWindow();

    public static WindowHandler findWindowEnhanced(String title,String clsName){
        return new WindowHandler(findWindowEnhanced0(title, clsName));
    }

    public static boolean isWindowPresent(@Nonnull String title, @Nonnull String clsName){
        return findWindowEnhanced0(title, clsName) != 0;
    }

    public static WindowHandler findWindowA(String title, String clsName){
        return new WindowHandler(findWindowA0(title, clsName));
    }
    private native static long findWindowEnhanced0(String title, String clsName);
    private native static long findWindowA0(String title, String clsName);
}
