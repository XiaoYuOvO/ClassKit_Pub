package net.xiaoyu233.classkit.util.profiler;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class ProfilingMonitor extends JFrame {
    private final Map<ProfileType,JLabel> types = new HashMap<>();
    private final Box contentPanel = Box.createVerticalBox();
    public ProfilingMonitor(){
        this.setContentPane(new JScrollPane(contentPanel));
//        this.setLayout(new FlowLayout());
        this.setAlwaysOnTop(true);
        registerAllTypes();
        this.pack();
    }

    private void registerAllTypes(){
        ProfileType.TYPES.forEach(this::registerType);
    }

    public void registerType(ProfileType type){
        XYSeries series = new XYSeries(type.getName(),false,false);
        series.setMaximumItemCount(100);
        XYSeriesCollection xyseriescollection = new XYSeriesCollection();
        xyseriescollection.addSeries(series);
        JFreeChart jfreechart = ChartFactory.createXYLineChart(
                null, null, null, xyseriescollection,
                PlotOrientation.VERTICAL, false, false, false);
        this.types.computeIfAbsent(type,(type1)-> {
            Box layoutBox = Box.createHorizontalBox();
            JLabel jLabel = new JLabel();
//            layoutBox.add(jLabel);
            StandardChartTheme mChartTheme = new StandardChartTheme("CN");
            mChartTheme.setLargeFont(new Font("黑体", Font.BOLD, 20));
            mChartTheme.setExtraLargeFont(new Font("宋体", Font.PLAIN, 15));
            mChartTheme.setRegularFont(new Font("宋体", Font.PLAIN, 15));
            ChartFactory.setChartTheme(mChartTheme);
            jfreechart.setBorderPaint(new Color(0,204,205));
            jfreechart.setBorderVisible(true);
            XYPlot xyplot = (XYPlot) jfreechart.getPlot();
// Y轴
            NumberAxis yAxis = (NumberAxis) xyplot.getRangeAxis();
            yAxis.setLowerBound(0);
            yAxis.setUpperBound(50);
            yAxis.setAutoRange(false);
//            yAxis.setTickUnit(new NumberTickUnit(1000));
// 只显示整数值
            yAxis.setStandardTickUnits(NumberAxis.createStandardTickUnits());
// numberaxis.setAutoRangeIncludesZero(true);
            yAxis.setLowerMargin(0); // 数据轴下(左)边距
            yAxis.setMinorTickMarksVisible(false);// 标记线是否显示
            yAxis.setTickMarkInsideLength(0);// 外刻度线向内长度
            yAxis.setTickMarkOutsideLength(0);
// X轴的设计
            NumberAxis x = (NumberAxis) xyplot.getDomainAxis();
            x.setLowerBound(0);
            x.setUpperBound(100);
            x.setAutoRange(false);// 自动设置数据轴数据范围
//            x.setTickUnit(new NumberTickUnit(60d));
// 设置最大的显示值和最小的显示值
// 数据轴的数据标签：只显示整数标签
            x.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
            x.setAxisLineVisible(true);// X轴竖线是否显示
            x.setTickMarksVisible(false);// 标记线是否显示
            RectangleInsets offset = new RectangleInsets(0, 0, 0, 0);
            xyplot.setAxisOffset(offset);// 坐标轴到数据区的间距
            xyplot.setBackgroundAlpha(0.0f);// 去掉柱状图的背景色
            xyplot.setOutlinePaint(null);// 去掉边框
            ChartPanel chartPanel = new ChartPanel(jfreechart, true);
            chartPanel.setPreferredSize(new Dimension(700,200));
            layoutBox.add(chartPanel);
            contentPanel.add(layoutBox);
            return jLabel;
        });

        type.setUpdateCallback(() -> {
            jfreechart.setTitle(MessageFormat.format("{0} {1} avg:{2} min:{3} max:{4}", type.getName(), type.getLastValue(), type.getLastAvg(), type.getMin(), type.getMax()));
            series.addOrUpdate(type.getUpdatedCount() % 100,type.getLastValue());
        });
    }
}
