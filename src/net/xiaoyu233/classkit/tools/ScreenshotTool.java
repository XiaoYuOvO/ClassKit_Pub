package net.xiaoyu233.classkit.tools;

import com.sun.imageio.plugins.png.PNGImageWriter;
import com.sun.imageio.plugins.png.PNGImageWriterSpi;
import net.xiaoyu233.classkit.config.EmptyConfig;

import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotTool extends Tool<EmptyConfig> {
    @Override
    public String getName() {
        return "Screenshot Tool";
    }

    @Override
    public void init(EmptyConfig config) {
        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        systemClipboard.addFlavorListener(e -> {
            try {
                if (systemClipboard.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
                    BufferedImage data = (BufferedImage)(((Clipboard) e.getSource())).getData(DataFlavor.imageFlavor);
                    PNGImageWriter writer = new PNGImageWriter(new PNGImageWriterSpi());
                    ImageWriteParam defaultWriteParam = writer.getDefaultWriteParam();
                    IIOMetadata defaultImageMetadata = writer.getDefaultImageMetadata(ImageTypeSpecifier.createFromBufferedImageType(data.getType()), defaultWriteParam);
                    File outputFile = new File(new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + "-screenshot.png");
                    FileImageOutputStream output = new FileImageOutputStream(outputFile);
                    writer.setOutput(output);
                    writer.write(defaultImageMetadata,new IIOImage(data,null,defaultImageMetadata),defaultWriteParam);
                    System.out.println("Successfully stored screenshot to: " + outputFile.getCanonicalPath());
                    writer.dispose();
                    output.close();
                    StringSelection selection = new StringSelection("");
                    this.sendMessage("已截图");
                    try {
                        systemClipboard.setContents(selection,selection);
                    }catch (IllegalStateException e1) {
                        e1.printStackTrace();
                    }
                }
            } catch (UnsupportedFlavorException | IOException unsupportedFlavorException) {
                unsupportedFlavorException.printStackTrace();
            }
        });
    }

    @Override
    public void reloadConfig(EmptyConfig config) {

    }

    @Override
    public void tick() {

    }

    @Override
    public void clear() {
        super.clear();
    }
}
