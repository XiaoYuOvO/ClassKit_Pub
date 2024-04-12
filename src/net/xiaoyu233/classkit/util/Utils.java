package net.xiaoyu233.classkit.util;



import javax.annotation.Nullable;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.function.Consumer;

public class Utils {
    public static Mixer intel = Utils.findMixer("PC Monitor");
    public static Mixer vbcable = Utils.findMixer("CABLE Output");
    public static final SourceDataLine SOURCE_DATA_LINE;
    public static final TargetDataLine TARGET_DATA_LINE;

    static {
        try {
            SOURCE_DATA_LINE = (SourceDataLine) intel.getLine(intel.getSourceLineInfo()[0]);
            AudioFormat format = new AudioFormat(48000, 16, 2, true, false);
            SOURCE_DATA_LINE.open(format);
            TARGET_DATA_LINE = (TargetDataLine) vbcable.getLine(vbcable.getTargetLineInfo()[0]);
            TARGET_DATA_LINE.open(format);
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }
    public static Box createHorizontalBoxFor(JComponent c){
        Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.add(Box.createHorizontalGlue());
        horizontalBox.add(c);
        horizontalBox.add(Box.createHorizontalGlue());
        return horizontalBox;
    }

    public static long getMeasuringTimeNano() {
        return System.nanoTime();
    }

    public static Box createHorizontalBoxFor(JComponent... c){
        Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.add(Box.createHorizontalGlue());
        for (JComponent jComponent : c) {
            horizontalBox.add(jComponent);
        }
        horizontalBox.add(Box.createHorizontalGlue());
        return horizontalBox;
    }

    @Nullable
    public static Mixer findMixer(String mixerName){
        for (Mixer.Info info : AudioSystem.getMixerInfo()) {
            if (!info.getName()
                    .contains("Port")) {
                if (info.getName().contains(mixerName)) {
                    return AudioSystem.getMixer(info);
                }
            }
        }
        return AudioSystem.getMixer(null);
    }

    public static void fitTableColumns(JTable myTable) {
        myTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JTableHeader header = myTable.getTableHeader();
        int rowCount = myTable.getRowCount();
        Enumeration<TableColumn> columns = myTable.getColumnModel().getColumns();
        while(columns.hasMoreElements())
        {
            TableColumn column = columns.nextElement();
            int col = header.getColumnModel().getColumnIndex(column.getIdentifier());
            int width = (int)header.getDefaultRenderer().getTableCellRendererComponent
                    (myTable, column.getIdentifier(), false, false, -1, col).getPreferredSize().getWidth();
            for(int row = 0; row < rowCount; row++)
            {
                int preferedWidth = (int)myTable.getCellRenderer(row, col).getTableCellRendererComponent
                        (myTable, myTable.getValueAt(row, col), false, false, row, col).getPreferredSize().getWidth();
                width = Math.max(width, preferedWidth);
            }
            header.setResizingColumn(column);
            column.setWidth(width+myTable.getIntercellSpacing().width);
        }
    }

    @FunctionalInterface
    public interface SafeRunnable{
        void run() throws Throwable;
    }

    public static <T> T make(T src, Consumer<T> maker){
        maker.accept(src);
        return src;
    }


    public static void centerizeWindow(Window window){
        Dimension d = window.getSize();
        DisplayMode displayMode = window.getGraphicsConfiguration()
                .getDevice()
                .getDisplayMode();
        int width = d.width;
        int height = d.height;
        window.setBounds( (displayMode.getWidth() - width) / 2,(displayMode.getHeight() - height) / 2, width, height);
    }

    public static Runnable safeRun(SafeRunnable source,boolean printErr){
        return ()->{
            try {
                source.run();
            }catch (Throwable t){
                if (printErr){
                    t.printStackTrace();
                }
            }
        };
    }

    public static boolean isProcessRunning(String procName) {
        try {
            ProcessBuilder pb = new ProcessBuilder("tasklist");
            Process p = pb.start();
            BufferedReader out = new BufferedReader(new InputStreamReader(new BufferedInputStream(p.getInputStream()), Charset.forName("GBK")));
            String ostr;
            StringBuilder result = new StringBuilder();
            while ((ostr = out.readLine()) != null){
                result.append(ostr);
            }
            return result.toString().contains(procName);
        }catch (Exception e){
            return false;
        }
    }

    public static Process runProcessWithPrivilege(ProcessBuilder process){
        return AccessController.doPrivileged((PrivilegedAction<Process>) () -> {
            try {
                return process.start();
            }catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    private static String readFully(InputStream s){
        StringBuilder sb = new StringBuilder();
        Scanner scanner = new Scanner(s);
        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine());
        }
        return sb.toString();
    }
}
