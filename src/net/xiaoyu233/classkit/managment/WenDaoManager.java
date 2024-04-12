package net.xiaoyu233.classkit.managment;

import net.xiaoyu233.classkit.config.WenDaoConfig;
import net.xiaoyu233.classkit.keys.KeyBind;
import net.xiaoyu233.classkit.keys.KeyModifier;
import net.xiaoyu233.classkit.keys.Keys;
import net.xiaoyu233.classkit.tools.Tool;
import net.xiaoyu233.classkit.util.Utils;
import net.xiaoyu233.classkit.util.natives.FindWindowEnhance;
import net.xiaoyu233.classkit.util.natives.WindowHandler;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

public class WenDaoManager extends Tool<WenDaoConfig> {
    private WenDaoConfig config = new WenDaoConfig();
    private Process wendaoProcess;
    private WindowHandler wendaoHandler;
    public WenDaoManager(){
    }

    public void createWenDaoProcess(){
        try {
            if (!Utils.isProcessRunning("UnitedClass") || !FindWindowEnhance.isWindowPresent("","EducationUI")){
                this.wendaoProcess = new ProcessBuilder().
                        command("powershell","-Command","\"Start-Process \\\""
                                +config.getWendaoExecutable().toString() + "\\\" -verb Runas -wait\"").
                        directory(config.getWendaoWorkDir()).
                        redirectError(new File("err.log")).
                        start();
                while (!FindWindowEnhance.isWindowPresent("", "LoginFrame")) {
                    Thread.sleep(100);
                }
                Thread.sleep(500);
                WindowHandler handler = FindWindowEnhance.findWindowEnhanced("","LoginFrame");
                this.wendaoHandler = handler;
                handler.switchToThisWindow(true);
                Robot robot = new Robot();
                Thread.sleep(500);
                robot.keyPress(KeyEvent.VK_ENTER);
                robot.keyRelease(KeyEvent.VK_ENTER);
            }else {
                this.wendaoHandler = FindWindowEnhance.findWindowEnhanced("","EducationUI");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException | AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public int getPid(){
        return this.wendaoHandler.getPid();
    }

    private void shutdownWendaoProcess() throws InterruptedException, IOException {
        Runtime.getRuntime().exec("powershell -Command \"Start-Process 'taskkill' '-f -pid "+ this.getPid() +"' -verb Runas\"").waitFor();
    }

    public void waitForLogin() throws InterruptedException {
        while (!FindWindowEnhance.isWindowPresent("", "EducationUI")) {
            Thread.sleep(100);
        }
        this.wendaoHandler = FindWindowEnhance.findWindowEnhanced("","EducationUI");
    }

    public void waitFor(Runnable tickRun,int waitTime){
        try {
            while (FindWindowEnhance.isWindowPresent("", "EducationUI")){
                Thread.sleep(waitTime);
                tickRun.run();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "wendao_manager";
    }

    @Override
    public void init(WenDaoConfig config) {
        this.addKeyCallback(new KeyBind(Keys.VK_F4,new KeyModifier(KeyModifier.Modifier.CONTROL)),
                            ()-> System.exit(0));
        this.addKeyCallback(new KeyBind(Keys.VK_F4,new KeyModifier(KeyModifier.Modifier.SHIFT, KeyModifier.Modifier.CONTROL)),
                            Utils.safeRun(this::shutdownWendaoProcess,false));
        this.config = config;
    }

    @Override
    public void reloadConfig(WenDaoConfig config) {

    }

    @Override
    public void tick() {

    }
}
