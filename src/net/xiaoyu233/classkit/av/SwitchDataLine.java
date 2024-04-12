package net.xiaoyu233.classkit.av;

import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SwitchDataLine implements TargetDataLine {
    List<TargetDataLine> dataLines = new ArrayList<>();
    TargetDataLine currentLine;
    public SwitchDataLine(TargetDataLine... dataLines){
        if (dataLines.length == 0) throw new IllegalArgumentException("Must have a least one data line");
        this.dataLines.addAll(Arrays.asList(dataLines));
        currentLine = dataLines[0];
    }

    public void switchNext(){
        currentLine = dataLines.get((dataLines.indexOf(currentLine) + 1) % dataLines.size());
    }

    public TargetDataLine getCurrentLine() {
        return currentLine;
    }

    @Override
    public void open(AudioFormat format, int bufferSize) throws LineUnavailableException {
        for (TargetDataLine dataLine : dataLines) {
            dataLine.open(format, bufferSize);
        }
    }

    @Override
    public void open(AudioFormat format) throws LineUnavailableException {
        for (TargetDataLine dataLine : dataLines) {
            if (!dataLine.isOpen()){
                dataLine.open(format);
            }
        }
    }

    @Override
    public int read(byte[] b, int off, int len) {
        return currentLine.read(b, off, len);
    }

    @Override
    public void drain() {
        currentLine.drain();
    }

    @Override
    public void flush() {
        currentLine.flush();
    }

    @Override
    public void start() {
        for (TargetDataLine dataLine : dataLines) {
            dataLine.start();
        }
    }

    @Override
    public void stop() {
        for (TargetDataLine dataLine : dataLines) {
            dataLine.stop();
        }
    }

    @Override
    public boolean isRunning() {
        return currentLine.isRunning();
    }

    @Override
    public boolean isActive() {
        return currentLine.isActive();
    }

    @Override
    public AudioFormat getFormat() {
        return currentLine.getFormat();
    }

    @Override
    public int getBufferSize() {
        return currentLine.getBufferSize();
    }

    @Override
    public int available() {
        return currentLine.available();
    }

    @Override
    public int getFramePosition() {
        return currentLine.getFramePosition();
    }

    @Override
    public long getLongFramePosition() {
        return currentLine.getLongFramePosition();
    }

    @Override
    public long getMicrosecondPosition() {
        return currentLine.getMicrosecondPosition();
    }

    @Override
    public float getLevel() {
        return currentLine.getLevel();
    }

    @Override
    public Line.Info getLineInfo() {
        return currentLine.getLineInfo();
    }

    @Override
    public void open() throws LineUnavailableException {
        for (TargetDataLine dataLine : dataLines) {
            dataLine.open();
        }
    }

    @Override
    public void close() {
        for (TargetDataLine dataLine : dataLines) {
            dataLine.close();
        }
    }

    @Override
    public boolean isOpen() {
        return dataLines.stream().anyMatch(TargetDataLine::isOpen);
    }

    @Override
    public Control[] getControls() {
        return currentLine.getControls();
    }

    @Override
    public boolean isControlSupported(Control.Type control) {
        return currentLine.isControlSupported(control);
    }

    @Override
    public Control getControl(Control.Type control) {
        return currentLine.getControl(control);
    }

    @Override
    public void addLineListener(LineListener listener) {
        this.dataLines.forEach(line -> line.addLineListener(listener));
    }

    @Override
    public void removeLineListener(LineListener listener) {
        this.dataLines.forEach(line -> line.removeLineListener(listener));
    }
}
