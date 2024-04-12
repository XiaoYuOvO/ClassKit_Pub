package net.xiaoyu233.classkit.gui.config;

import net.xiaoyu233.classkit.api.LessonTable;
import net.xiaoyu233.classkit.config.Codec;
import net.xiaoyu233.classkit.config.ConfigEntry;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class EditorAdapter {
    private final Map<Codec<?>,EditorFactory<?>> factoryMap = new HashMap<>();
    public EditorAdapter() {
        this.registerFactory(Codec.BOOLEAN, (configEntry -> {
            JCheckBox jCheckBox = new JCheckBox();
            jCheckBox.setBorderPaintedFlat(true);
            jCheckBox.setSelected(configEntry.getCurrentValue());
            jCheckBox.setPreferredSize(new Dimension(20, jCheckBox.getHeight()));
            jCheckBox.setBackground(new Color(253, 253, 254, 255));
            return new EditingComponent<>(jCheckBox, configEntry.getComment(), jCheckBox::setSelected, () -> {
                configEntry.setCurrentValue(jCheckBox.isSelected());
            });
        }));
        this.registerFactory(Codec.DOUBLE,this.createNumberFactory(NumberFormat.getNumberInstance(),Number::doubleValue));
        this.registerFactory(Codec.FLOAT,this.createNumberFactory(NumberFormat.getNumberInstance(),Number::floatValue));
        this.registerFactory(Codec.INTEGER,this.createNumberFactory(NumberFormat.getIntegerInstance(),Number::intValue));
        this.registerFactory(Codec.STRING,this.createTextFactory(String::toString));
        this.registerFactory(Codec.FILE,this.createTextFactory(File::new));
        this.registerFactory(Codec.LESSON_TABLE,(configEntry) -> {
            JButton button = new JButton("打开编辑器");
            return new EditingComponent<>(button,configEntry.getComment(),
                                          (LessonTable) -> {},
                                          ()->{});
        });
    }

    private <T extends Number> EditorFactory<T>  createNumberFactory(NumberFormat numberInstance, Function<Number,T> castFunction){
        return (ConfigEntry<T> configEntry) -> {
            JFormattedTextField jFormattedTextField = new JFormattedTextField(configEntry.getCurrentValue());
            DefaultFormatterFactory defaultFormatterFactory = new DefaultFormatterFactory();
            numberInstance.setGroupingUsed(false);
            NumberFormatter atf = new NumberFormatter(numberInstance);
            atf.setAllowsInvalid(false);
            atf.setCommitsOnValidEdit(true);
            defaultFormatterFactory.setDefaultFormatter(atf);
            jFormattedTextField.setFormatterFactory(defaultFormatterFactory);
            Dimension preferredSize = jFormattedTextField.getPreferredSize();
            preferredSize.setSize(preferredSize.getWidth() + 40, preferredSize.getHeight());
            jFormattedTextField.setPreferredSize(preferredSize);
            return new EditingComponent<>(jFormattedTextField,configEntry.getComment(),jFormattedTextField::setValue,() -> {
                configEntry.setCurrentValue(castFunction.apply(((Number) jFormattedTextField.getValue())));
            });
        };
    }

    private <T> EditorFactory<T> createTextFactory(Function<String,T> castFunction){
        return (ConfigEntry<T> configEntry) -> {
            JTextField jTextField = new JTextField(configEntry.getCurrentValue().toString());
            Dimension preferredSize = jTextField.getPreferredSize();
            preferredSize.setSize(preferredSize.getWidth() + 40, preferredSize.getHeight());
            jTextField.setPreferredSize(preferredSize);
            return new EditingComponent<>(jTextField,configEntry.getComment(),(value) -> jTextField.setText(value.toString()),() -> {
                configEntry.setCurrentValue(castFunction.apply(jTextField.getText()));
            });
        };
    }


    @SuppressWarnings("unchecked")
    public <T> EditingComponent<T> getEditorFor(ConfigEntry<T> entry){
        return ((EditorFactory<T>) this.factoryMap.get(entry.getCodec())).create(entry);
    }

    public <T> void registerFactory(Codec<T> codec,EditorFactory<T> editorFactory){
        this.factoryMap.put(codec, editorFactory);
    }
}
