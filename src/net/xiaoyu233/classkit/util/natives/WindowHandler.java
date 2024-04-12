package net.xiaoyu233.classkit.util.natives;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;

public class WindowHandler {
    private final long hwnd;

    WindowHandler(long hwnd) {
        this.hwnd = hwnd;
    }

    public void setMinimized(boolean minimized){
        setMinimized0(this.hwnd,minimized);
    }
    public void setMaximized(boolean maximized){
        setMaximized0(this.hwnd,maximized);
    }
    public boolean isMinimized(){
        return isMinimized0(this.hwnd);
    }
    public boolean isMaximized(){
        return isMaximized0(this.hwnd);
    }
    public void setWindowTransparent(byte alpha){
        setWindowTransparent0(this.hwnd,alpha);
    }
    public void closeWindow(){
        closeWindow0(this.hwnd);
    }
    public boolean isWindowVisible(){
        return isWindowVisible0(this.hwnd);
    }
    public void switchToThisWindow(boolean unknown){
        switchToThisWindow0(this.hwnd,unknown);
    }

    public int getPid(){
        IntByReference lpdwProcessId = new IntByReference();
        User32.INSTANCE.GetWindowThreadProcessId(new WinDef.HWND(Pointer.createConstant(hwnd)), lpdwProcessId);
        return lpdwProcessId.getValue();
    }

    //Natives
    private static native void setMinimized0(long hwnd,boolean minimized);
    private static native void setMaximized0(long hwnd,boolean maximized);

    private static native boolean isMinimized0(long hwnd);
    private static native boolean isMaximized0(long hwnd);

    private static native void setWindowTransparent0(long hwnd,byte alpha);

    private static native void closeWindow0(long hwnd);

    private static native boolean isWindowVisible0(long hwnd);
    private static native void switchToThisWindow0(long hwnd,boolean unknown);


}
