package net.xiaoyu233.classkit.managment;

import net.xiaoyu233.classkit.util.natives.FindWindowEnhance;
import net.xiaoyu233.classkit.util.natives.WindowHandler;

public class ResourcePlayerManager {

    private final String frameClassName = "WebSeat_ZYLB_PlayerMainFrame";
    private WindowHandler resourcePlayerHandle;
    public void waitForLaunch(){
        while (!FindWindowEnhance.isWindowPresent("", frameClassName)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        resourcePlayerHandle = FindWindowEnhance.findWindowEnhanced("", frameClassName);
    }
    public void waitFor(Runnable tickRun,int waitTime){
        try {
            while (FindWindowEnhance.isWindowPresent("", frameClassName)){
                Thread.sleep(waitTime);
                tickRun.run();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getPid() {
        return this.resourcePlayerHandle.getPid();
    }
}
