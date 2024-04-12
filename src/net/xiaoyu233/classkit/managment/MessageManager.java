package net.xiaoyu233.classkit.managment;

import net.xiaoyu233.classkit.gui.dialog.MessageWindow;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class MessageManager {
    private static final int MSG_DELAY = 2000;
    private String currentMsg;
    private final Queue<String> toolTips = new ArrayBlockingQueue<>(10);
    private final MessageWindow messageWindow = new MessageWindow();

    public MessageManager(){
        Thread msgThread = new Thread(() -> {
            while (true) {
                if (!this.toolTips.isEmpty() && currentMsg == null) {
                    currentMsg = toolTips.poll();
                    this.messageWindow.setMsg(currentMsg);
//                    this.messageWindow.setOpacity(1f);
                } else if (currentMsg != null) {
                    int count = 0;
                    while (count < MSG_DELAY && this.toolTips.isEmpty()) {
                        count++;
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        messageWindow.setOpacity(calcOpacity(count,MSG_DELAY));
                    }
                    this.currentMsg = null;
                    this.messageWindow.setMsg("");
                }else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();

                    }
                }
            }
        });
        msgThread.setName("Msg Thread");
        msgThread.start();
    }


    public void addMessage(String msg){
        this.toolTips.offer(msg);
    }

    private float calcOpacity(int x,int max){
        return -(float) x/(float) max+1f;
    }
}
