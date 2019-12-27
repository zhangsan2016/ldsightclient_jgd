package com.ldsight.fragment;

import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.example.ldsightclient_jgd.R;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.Random;

public class SystemLogFragment extends Fragment {

	private LinearLayout ly_linear_graph;

	// 用于存放每条折线的点数据
	private XYSeries line1, line2;
	// 用于存放所有需要绘制的XYSeries
	private XYMultipleSeriesDataset mDataset;
	// 用于存放每条折线的风格
	private XYSeriesRenderer renderer1, renderer2;
	// 用于存放所有需要绘制的折线的风格
	private XYMultipleSeriesRenderer mXYMultipleSeriesRenderer;
	private GraphicalView chart;
	private int[] colors; // 线颜色数组
	PointStyle[] styles; // 线形状数组

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.system_log_fragment,
				container, false);

		ly_linear_graph = (LinearLayout) rootView
				.findViewById(R.id.ly_linear_graph);
		mXYMultipleSeriesRenderer = new XYMultipleSeriesRenderer();
		mDataset = new XYMultipleSeriesDataset();

		//String[] streetNames = { "安置房1", "安置房2", "安置房3" };
		String[] streetNames = {};

		colors = new int[] { Color.GREEN, Color.RED, Color.YELLOW, Color.BLUE,
				Color.WHITE ,getResources().getColor(R.color.peachpuff),getResources().getColor(R.color.peru),getResources().getColor(R.color.sienna)};


		styles = new PointStyle[] { PointStyle.CIRCLE,// 圆圈状
				PointStyle.DIAMOND, PointStyle.SQUARE };// 菱形状,矩形状


		initChart(streetNames);

		return rootView;
	}

	private void initChart(String[] streetNames) {

		for (int i = 0; i < streetNames.length; i++) {
			// 初始化，必须保证XYMultipleSeriesDataset对象中的XYSeries数量和
			// XYMultipleSeriesRenderer对象中的XYSeriesRenderer数量一样多
			line1 = new XYSeries(streetNames[i]);
			renderer1 = new XYSeriesRenderer(); // 渲染器
			// 对XYSeries和XYSeriesRenderer的对象的参数赋值
			initLine(line1);
			int color = 0;
			PointStyle style = null;
			if (colors.length > i) { // color如果大于i设置默认值
				color = colors[i];

			} else {
				color = colors[2];
			}
			if (styles.length > i) { // sysle如果大于i设置默认值
				style = styles[i];

			} else {
				style = styles[0];
			}

			initRenderer(renderer1, color, style, true);

			// 将XYSeries对象和XYSeriesRenderer对象分别添加到XYMultipleSeriesDataset对象和XYMultipleSeriesRenderer对象中。
			mDataset.addSeries(line1);
			mXYMultipleSeriesRenderer.addSeriesRenderer(renderer1);
		}

		// 配置chart参数
		setChartSettings(mXYMultipleSeriesRenderer, "X", "Y", 1, 10, 100, 500,
				Color.RED, Color.BLACK);

		// 通过该函数获取到一个View 对象
		chart = ChartFactory.getLineChartView(getActivity()
				.getApplicationContext(), mDataset, mXYMultipleSeriesRenderer);

		// 将该View 对象添加到layout中。
		ly_linear_graph.addView(chart, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}

	private void initLine(XYSeries series) {
		// 随机生成两组数据
		Random r = new Random();

		for (int i = 0; i < 30; i++) {
			series.add(i + 1, r.nextInt(500 - 350) + 200);
		}
	}

	private XYSeriesRenderer initRenderer(XYSeriesRenderer renderer, int color,
										  PointStyle style, boolean fill) {
		// 设置图表中曲线本身的样式，包括颜色、点的大小以及线的粗细等
		renderer.setColor(color);
		renderer.setPointStyle(style);
		renderer.setFillPoints(fill);
		renderer.setLineWidth(1);
		return renderer;
	}

	// setChartSettings(mXYMultipleSeriesRenderer, "X", "Y", 0, 10, 0, 500,
	// Color.RED, Color.WHITE);
	protected void setChartSettings(
			XYMultipleSeriesRenderer mXYMultipleSeriesRenderer, String xTitle,
			String yTitle, double xMin, double xMax, double yMin, double yMax,
			int axesColor, int labelsColor) {
		// 有关对图表的渲染可参看api文档
		// mXYMultipleSeriesRenderer.setChartTitle(title);
		mXYMultipleSeriesRenderer.setXTitle(xTitle);
		mXYMultipleSeriesRenderer.setYTitle(yTitle);
		mXYMultipleSeriesRenderer.setXAxisMin(xMin);

		mXYMultipleSeriesRenderer.setAxisTitleTextSize(30);
		mXYMultipleSeriesRenderer.setChartTitleTextSize(50);
		mXYMultipleSeriesRenderer.setLabelsTextSize(15);
		mXYMultipleSeriesRenderer.setXAxisMax(xMax);
		mXYMultipleSeriesRenderer.setYAxisMin(yMin);
		mXYMultipleSeriesRenderer.setYAxisMax(yMax);
		mXYMultipleSeriesRenderer.setAxesColor(axesColor);
		mXYMultipleSeriesRenderer.setLabelsColor(labelsColor);
		mXYMultipleSeriesRenderer.setShowGrid(true);
		mXYMultipleSeriesRenderer.setGridColor(Color.GRAY);
		// mXYMultipleSeriesRenderer.setXLabels(20);
		mXYMultipleSeriesRenderer.setXLabels(10);
		mXYMultipleSeriesRenderer.setYLabels(5);
		mXYMultipleSeriesRenderer.setXTitle("日期（天）");
		mXYMultipleSeriesRenderer.setYTitle("功率");
		mXYMultipleSeriesRenderer.setYLabelsAlign(Align.RIGHT);
		mXYMultipleSeriesRenderer.setPointSize((float) 5);
		mXYMultipleSeriesRenderer.setShowLegend(true);
		mXYMultipleSeriesRenderer.setLegendTextSize(10);
		mXYMultipleSeriesRenderer.setApplyBackgroundColor(true); // 设置为可设置背景
		mXYMultipleSeriesRenderer.setBackgroundColor(Color.WHITE); // 设置内背景颜色
		mXYMultipleSeriesRenderer.setMarginsColor(Color.WHITE);  // 设置外背景颜色
		mXYMultipleSeriesRenderer.setLegendTextSize(35); // 设置字体大小
		//	mXYMultipleSeriesRenderer.setZoomButtonsVisible(true); // 设置缩放按钮可见
		mXYMultipleSeriesRenderer.setMargins(new int[] { 40, 50, 50, 50 }); // 设置外边距
		// mXYMultipleSeriesRenderer.setMargins(new int[] { 20, 30, 15, 20 });//
		// 上,左,下,右

	}
}
