package net.xiaoyu233.classkit.av.processors;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * This AudioProcessor can be used to sync events with sound. It uses a pattern
 * described in JavaFX Special Effects Taking Java RIA to the Extreme with
 * Animation, Multimedia, and Game Element Chapter 9 page 185: <blockquote><i>
 * The variable LineWavelet is the Java Sound object that actually makes the sound. The
 * write method on LineWavelet is interesting because it blocks until it is ready for
 * more data. </i></blockquote> If this AudioProcessor chained with other
 * AudioProcessors the others should be able to operate in real time or process
 * the signal on a separate thread.
 *
 * @author Joren Six
 */
public final class AudioPlayer implements AudioProcessor {


    /**
     * The LineWavelet to send sound to. Is also used to keep everything in sync.
     */
    private SourceDataLine line;


    private final AudioFormat format;

    /**
     * Creates a new audio player.
     *
     * @param format
     *            The AudioFormat of the buffer.
     * @throws LineUnavailableException
     *             If no output LineWavelet is available.
     */
    public AudioPlayer(final AudioFormat format)	throws LineUnavailableException {
        final DataLine.Info info = new DataLine.Info(SourceDataLine.class,format);
        this.format = format;
        line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(this.format);
        line.start();
    }

    public AudioPlayer(final SourceDataLine line) {
        this.format = line.getFormat();
        this.line = line;
    }

    public void start() throws LineUnavailableException {
        this.line.open(this.format);
        this.line.start();
    }

    public AudioPlayer(final AudioFormat format, int bufferSize) throws LineUnavailableException {
        final DataLine.Info info = new DataLine.Info(SourceDataLine.class,format,bufferSize);
        this.format = format;
        line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(format,bufferSize*2);
        System.out.println("Buffer size:" + line.getBufferSize());
        line.start();
    }

    public AudioPlayer(final TarsosDSPAudioFormat format, int bufferSize) throws LineUnavailableException {
        this(JVMAudioInputStream.toAudioFormat(format), bufferSize);
    }
    public AudioPlayer(final TarsosDSPAudioFormat format) throws LineUnavailableException {
        this(JVMAudioInputStream.toAudioFormat(format));
    }

    public long getMicroSecondPosition(){
        return line.getMicrosecondPosition();
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        // overlap in samples * nr of bytes / sample = bytes overlap

		/*
		if(byteStepSize < line.available()){
			System.out.println(line.available() + " Will not block " + line.getMicrosecondPosition());
		}else {
			System.out.println("Will block " + line.getMicrosecondPosition());
		}
		*/

        byte[] b = floatToByte(audioEvent.getFloatBuffer());
        line.write(b,0,b.length);
//        if(bytesWritten != byteStepSize){
//            System.err.println(String.format("Expected to write %d bytes but only wrote %d bytes",byteStepSize,bytesWritten));
//        }
        return true;
    }

    public static byte[] floatToByte(float[] data) {
        int length = data.length;
        if (length % 2 != 0) {
            length++;
        }
        ByteBuffer outBuffer = ByteBuffer.allocate(length * 2);
        outBuffer.order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < data.length; i++) {
            if (data[i] > 1f) {
                data[i] = 1f;
            }
            if (data[i] < -1) {
                data[i] = -1;
            }
            outBuffer.putShort((short) (data[i] * 32767f));
        }
        return outBuffer.array();
    }

    /*
     * (non-Javadoc)
     *
     * @see be.tarsos.util.RealTimeAudioProcessor.AudioProcessor#
     * processingFinished()
     */
    public void processingFinished() {
        // cleanup
        line.drain();//drain takes too long..
        line.stop();
        line.close();
    }
}
