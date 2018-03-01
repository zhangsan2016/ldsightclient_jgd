package com.ldsight.component;

import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.ldsightclient_jgd.R;

public class DeviceTable extends AbsoluteLayout {
	private Paint mPaint;
	private Constant constant;
	private Context context;

	private TimeSelectView timeSelectView100;
	private TimeSelectView timeSelectView75;
	private TimeSelectView timeSelectView50;
	private TimeSelectView timeSelectView25;

	public int hourOf100;
	public int minuteOf100;

	public int hourOf75;
	public int minuteOf75;

	public int hourOf50;
	public int minuteOf50;

	public int hourOf25;
	public int minuteOf25;

	public int getHourOf100() {
		return timeSelectView100.getHour();
	}

	public void setHourOf100(int hourOf100) {
		this.hourOf100 = hourOf100;
	}

	public int getMinuteOf100() {
		return timeSelectView100.getMinute();
	}

	public void setMinuteOf100(int minuteOf100) {
		this.minuteOf100 = minuteOf100;
	}

	public int getHourOf75() {
		return timeSelectView75.getHour();
	}

	public void setHourOf75(int hourOf75) {
		this.timeSelectView75.setHour(hourOf75);
	}

	public int getMinuteOf75() {
		return timeSelectView75.getMinute();
	}

	public void setMinuteOf75(int minuteOf75) {
		this.timeSelectView75.setMinute(minuteOf75);
	}

	public int getHourOf50() {
		return this.timeSelectView50.getHour();
	}

	public void setHourOf50(int hourOf50) {
		this.timeSelectView50.setHour(hourOf50);
	}

	public int getMinuteOf50() {
		return this.timeSelectView50.getMinute();
	}

	public void setMinuteOf50(int minuteOf50) {
		this.timeSelectView50.setMinute(minuteOf50);
	}

	public int getHourOf25() {
		return this.timeSelectView25.getHour();
	}

	public void setHourOf25(int hourOf25) {
		this.timeSelectView25.setHour(hourOf25);
	}

	public int getMinuteOf25() {
		return this.timeSelectView25.getMinute();
	}

	public void setMinuteOf25(int minuteOf25) {
		this.timeSelectView25.setMinute(minuteOf25);
	}

	public void init100PickerDialog(int hourOfDay, int minute) {
		this.timeSelectView100.initPickerDialog(hourOfDay, minute);
	}

	public void init75PickerDialog(int hourOfDay, int minute) {
		this.timeSelectView75.initPickerDialog(hourOfDay, minute);
	}

	public void init50PickerDialog(int hourOfDay, int minute) {
		this.timeSelectView50.initPickerDialog(hourOfDay, minute);
	}

	public void init25PickerDialog(int hourOfDay, int minute) {
		this.timeSelectView25.initPickerDialog(hourOfDay, minute);
	}

	/**
	 * Constant类存放了条形图中参数信息，包括各个顶点的坐标，还有坐标轴的长度等等
	 * */
	private class Constant {
		int viewWidth;// 条形图的线条宽度
		int textSize;// 文本框字体大小
		int paddingBase;// 条形图的固定内边距
		int xLightText;// ”亮度“字符串x坐标
		int yLightText;// ”亮度“字符串y坐标
		int textViewPadding;// 文本框内间距

		int padding1;// 文本框和三角形顶点之间的距离
		int xAxisLeft;// 坐标轴左上三角形顶点x坐标
		int yAxisLeft;// 坐标轴坐上三角形顶点y坐标

		float triangleHeight;// 三角形高度
		float triangleWidth;// 三角形宽度

		int lightHeight;// 亮度轴高度（从左上三角形顶点到左下角底点）
		int xBottom, yBottom;// 左下角的底点
		int timeWidth;// 时间轴宽度（从左下角底点到右下角三角形顶点）

		int xAxisRight;// 坐标轴右下三角形顶点x坐标
		int yAxisRight;// 坐标轴右下三角形顶点y坐标

		int xTimeText;// 时间字符串x坐标
		int yTimeText;// 时间字符串y坐标

		// 时间选择框的坐标
		int x100View;
		int y100View;
		int x75View;
		int y75View;
		int x50View;
		int y50View;
		int x25View;
		int y25View;

		// 亮度logo字体大小
		int bgTextSize;

		// x,y(1-9)为条形图区域中从左到右的9个点
		int x1, y1;
		int x2, y2;
		int x3, y3;
		int x4, y4;
		int x5, y5;
		int x6, y6;
		int x7, y7;
		int x8, y8;
		int x9, y9;

		public void init() {
			viewWidth = 1;
			paddingBase = (int) (getHeight() * 0.04);
			textSize = 16;
			textViewPadding = (int) (getHeight() * 0.01);
			if (textViewPadding < 1)
				textViewPadding = 1;
			xLightText = getPaddingLeft() + paddingBase;
			yLightText = getPaddingTop() + paddingBase + textViewPadding;
			padding1 = (int) (getHeight() * 0.003);
			if (padding1 < 1)
				padding1 = 1;
			xAxisLeft = xLightText;
			Rect lightRect = new Rect();
			mPaint.setTextSize(textSize
					* getResources().getDisplayMetrics().density);
			mPaint.getTextBounds("亮度", 0, "亮度".length(), lightRect);
			// System.out.println("yLightText:" + yLightText);
			yAxisLeft = yLightText + lightRect.height() + textViewPadding
					+ padding1;

			lightHeight = getHeight() - getPaddingBottom() - paddingBase
					- yAxisLeft;
			xBottom = xAxisLeft;
			yBottom = yAxisLeft + lightHeight;

			Rect timeRect = new Rect();
			mPaint.getTextBounds("时间", 0, "时间".length(), timeRect);
			// System.out.println("rigght" + getRight());
			xTimeText = (int) (getRight() - getPaddingRight() - paddingBase
					- mPaint.measureText("时间") - textViewPadding);
			yTimeText = getHeight() - getPaddingBottom() - paddingBase
					- textViewPadding - timeRect.height();

			xAxisRight = xTimeText - padding1 - textViewPadding;
			yAxisRight = yBottom;

			timeWidth = xAxisRight - xBottom;

			int lightHeightBase = lightHeight / 9;
			int timeWidthBase = timeWidth / 9;
			triangleHeight = lightHeightBase / 2;
			triangleWidth = (float) (triangleHeight * 0.75);
			x1 = xBottom;
			y1 = yAxisLeft + lightHeightBase;

			x2 = x1 + timeWidthBase * 2;
			y2 = y1;

			x3 = x2;
			y3 = y2 + lightHeightBase * 2;

			x4 = x3 + timeWidthBase * 2;
			y4 = y3;

			x5 = x4;
			y5 = y4 + lightHeightBase * 2;

			x6 = x5 + timeWidthBase * 2;
			y6 = y5;

			x7 = x6;
			y7 = y6 + lightHeightBase * 2;

			x8 = x7 + timeWidthBase * 2;
			y8 = y7;

			x9 = x8;
			y9 = yBottom;

			// System.out.println("width:" + getWidth());
			x100View = x2;
			y100View = y2 - timeSelectView100.getHeight() / 2;

			x75View = x4;
			y75View = y4 - timeSelectView75.getHeight() / 2;

			x50View = x6;
			y50View = y6 - timeSelectView50.getHeight() / 2;

			x25View = x8;
			y25View = y8 - timeSelectView25.getHeight() / 2;

			bgTextSize = timeSelectView100.getWidth();
			for (int i = 1; i < getHeight(); i++) {
				mPaint.setTextSize(i);
				if (mPaint.measureText("100%") - (x2 - x1) <= 10
						&& mPaint.measureText("100%") - (x2 - x1) >= 0) {
					bgTextSize = i;
					break;
				}
			}
		}
	}

	public DeviceTable(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initTableView();
	}

	public DeviceTable(Context context) {
		super(context);
		this.context = context;
		initTableView();
	}

	public void initTableView() {
		setWillNotDraw(false);
		mPaint = new Paint();
		constant = new Constant();
		mPaint.setAntiAlias(true);

		timeSelectView100 = new TimeSelectView(context);
		timeSelectView75 = new TimeSelectView(context);
		timeSelectView50 = new TimeSelectView(context);
		timeSelectView25 = new TimeSelectView(context);

		// 增加四个时间选择框
		AbsoluteLayout.LayoutParams layoutParams = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				constant.x100View, constant.y100View);
		removeView(timeSelectView100);
		addView(timeSelectView100, layoutParams);
		layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, constant.x75View, constant.y75View);
		removeView(timeSelectView75);
		addView(timeSelectView75, layoutParams);

		layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, constant.x50View, constant.y50View);
		removeView(timeSelectView50);
		addView(timeSelectView50, layoutParams);

		layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, constant.x25View, constant.y25View);
		removeView(timeSelectView25);
		addView(timeSelectView25, layoutParams);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// 亮度字符串
		mPaint.setTextSize(constant.textSize
				* getResources().getDisplayMetrics().density);
		mPaint.setColor(Color.rgb(1, 127, 255));
		mPaint.setStrokeWidth(constant.viewWidth
				* getResources().getDisplayMetrics().density);
		canvas.drawText("亮度", constant.xLightText,
				constant.yLightText - mPaint.ascent(), mPaint);
		// 亮度字符串边框
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(1 * getResources().getDisplayMetrics().density);
		Rect rec = new Rect();
		mPaint.getTextBounds("亮度", 0, "亮度".length(), rec);

		RectF rect = new RectF(constant.xLightText - constant.textViewPadding,
				constant.yLightText - constant.textViewPadding,
				constant.xLightText + mPaint.measureText("亮度")
						+ constant.textViewPadding, constant.yLightText
				+ rec.height() + constant.textViewPadding);
		canvas.drawRoundRect(rect, 10, 10, mPaint);

		// 时间字符串
		mPaint.setStyle(Style.FILL);
		canvas.drawText("时间", constant.xTimeText,
				constant.yTimeText - mPaint.ascent(), mPaint);
		// 时间字符串边框
		mPaint.setStyle(Style.STROKE);
		mPaint.getTextBounds("时间", 0, "时间".length(), rec);
		RectF rect2 = new RectF(constant.xTimeText - constant.textViewPadding,
				constant.yTimeText - constant.textViewPadding,
				constant.xTimeText + mPaint.measureText("时间")
						+ constant.textViewPadding, constant.yTimeText
				+ rec.height() + constant.textViewPadding);
		canvas.drawRoundRect(rect2, 10, 10, mPaint);
		mPaint.setStrokeWidth(constant.viewWidth
				* getResources().getDisplayMetrics().density);
		// 画左上角三角形
		mPaint.setColor(Color.BLACK);
		mPaint.setStyle(Style.FILL);
		Path path = new Path();
		path.moveTo(constant.xAxisLeft, constant.yAxisLeft);
		path.lineTo(constant.xAxisLeft + constant.triangleWidth / 2,
				constant.yAxisLeft + constant.triangleHeight);
		path.lineTo(constant.xAxisLeft - constant.triangleWidth / 2,
				constant.yAxisLeft + constant.triangleHeight);
		path.lineTo(constant.xAxisLeft, constant.yAxisLeft);
		canvas.drawPath(path, mPaint);
		// 画右下角三角形
		path.moveTo(constant.xAxisRight, constant.yAxisRight);
		path.lineTo(constant.xAxisRight - constant.triangleHeight,
				constant.yAxisRight + constant.triangleWidth / 2);
		path.lineTo(constant.xAxisRight - constant.triangleHeight,
				constant.yAxisRight - constant.triangleWidth / 2);
		path.lineTo(constant.xAxisRight, constant.yAxisRight);
		canvas.drawPath(path, mPaint);
		// 画亮度轴
		canvas.drawLine(constant.xAxisLeft, constant.yAxisLeft,
				constant.xBottom, constant.yBottom, mPaint);
		// 画时间轴
		canvas.drawLine(constant.xBottom, constant.yBottom, constant.xBottom
				+ constant.timeWidth, constant.yAxisRight, mPaint);
		mPaint.setColor(Color.BLACK);

		// canvas.drawText("aaaaaaa", 0, 10 - (int) mPaint.ascent(), mPaint);

		// 条形图左边框
		mPaint.setColor(Color.rgb(1, 127, 255));
		canvas.drawLine(constant.x1 + constant.viewWidth, constant.y1,
				constant.xBottom + constant.viewWidth, constant.yBottom
						- constant.viewWidth, mPaint);
		// 条形图底线
		canvas.drawLine(constant.xBottom + constant.viewWidth, constant.yBottom
				- constant.viewWidth, constant.x9, constant.y9
				- constant.viewWidth, mPaint);

		// 画出各个条形图的顶线和右侧线
		canvas.drawLine(constant.x1, constant.y1, constant.x2, constant.y2,
				mPaint);
		canvas.drawLine(constant.x2, constant.y2, constant.x3, constant.y3,
				mPaint);
		canvas.drawLine(constant.x3, constant.y3, constant.x4, constant.y4,
				mPaint);
		canvas.drawLine(constant.x4, constant.y4, constant.x5, constant.y5,
				mPaint);
		canvas.drawLine(constant.x5, constant.y5, constant.x6, constant.y6,
				mPaint);
		canvas.drawLine(constant.x6, constant.y6, constant.x7, constant.y7,
				mPaint);
		canvas.drawLine(constant.x7, constant.y7, constant.x8, constant.y8,
				mPaint);
		canvas.drawLine(constant.x8, constant.y8, constant.x9, constant.y9,
				mPaint);

		// 画出条形图中的三条垂线
		mPaint.setColor(Color.rgb(148, 185, 224));
		canvas.drawLine(constant.x3, constant.y3, constant.x3, constant.yBottom
				- constant.viewWidth, mPaint);
		canvas.drawLine(constant.x5, constant.y5, constant.x5, constant.yBottom
				- constant.viewWidth, mPaint);
		canvas.drawLine(constant.x7, constant.y7, constant.x7, constant.yBottom
				- constant.viewWidth, mPaint);
		// 填写四个亮度字符串
		mPaint.setTextSize(constant.bgTextSize);
		System.out.println("text" + constant.bgTextSize);
		canvas.drawText("100%", constant.x1, constant.y1 - mPaint.ascent(),
				mPaint);
		canvas.drawText("75%", constant.x3, constant.y3 - mPaint.ascent(),
				mPaint);
		canvas.drawText("50%", constant.x5, constant.y5 - mPaint.ascent(),
				mPaint);
		canvas.drawText("25%", constant.x7, constant.y7 - mPaint.ascent(),
				mPaint);
		// 填满四个条形框的颜色
		mPaint.setColor(Color.rgb(85, 124, 163));
		// 用paint.setAlpha()，alpha即为透明度，0～255之间的值，0表示完全透明。
		mPaint.setAlpha(100);
		Rect rect3 = new Rect(constant.x1 + 1, constant.y1 + 1,
				constant.x2 - 1, constant.y9 - 1);
		canvas.drawRect(rect3, mPaint);
		rect3 = new Rect(constant.x3 + 1, constant.y3 + 1, constant.x5 - 1,
				constant.y9 - 1);
		canvas.drawRect(rect3, mPaint);
		rect3 = new Rect(constant.x5 + 1, constant.y5 + 1, constant.x7 - 1,
				constant.y9 - 1);
		canvas.drawRect(rect3, mPaint);
		rect3 = new Rect(constant.x7 + 1, constant.y7 + 1, constant.x9 - 1,
				constant.y9 - 1);
		canvas.drawRect(rect3, mPaint);

		super.onDraw(canvas);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		constant.init();
		// 增加四个时间选择框
		AbsoluteLayout.LayoutParams layoutParams = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				constant.x100View, constant.y100View);
		removeView(timeSelectView100);
		addView(timeSelectView100, layoutParams);
		layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, constant.x75View, constant.y75View);
		removeView(timeSelectView75);
		addView(timeSelectView75, layoutParams);

		layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, constant.x50View, constant.y50View);
		removeView(timeSelectView50);
		addView(timeSelectView50, layoutParams);

		layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, constant.x25View, constant.y25View);
		removeView(timeSelectView25);
		addView(timeSelectView25, layoutParams);
	}

	private class TimeSelectView extends LinearLayout {
		private int hour;
		private int minute;
		TimePickerDialog timePickerDialog;
		TextView textView;

		public TimeSelectView(Context context, AttributeSet attrs) {
			super(context, attrs);
			// TODO Auto-generated constructor stub
			initView();
		}

		public TimeSelectView(Context context) {
			super(context);
			initView();
			// TODO Auto-generated constructor stub
		}

		public void initView() {
			ImageView imageView = new ImageView(context);
			textView = new TextView(context);
			textView.setText("0时0分");
			textView.setTextColor(Color.WHITE);
			imageView.setImageResource(R.drawable.triangle_left);
			setOrientation(LinearLayout.HORIZONTAL);
			LayoutParams layoutParams = new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			addView(imageView, layoutParams);
			addView(textView, layoutParams);
			timePickerDialog = new TimePickerDialog(context,
					new OnTimeSetListener() {
						public void onTimeSet(TimePicker view, int hourOfDay,
											  int minute) {
							setHour(hourOfDay);
							setMinute(minute);
						}
					}, 0, 0, true);
			this.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
					timePickerDialog.show();
				}
			});
		}

		public void initPickerDialog(int hourOfDay, int minute) {
			timePickerDialog = new TimePickerDialog(context,
					new OnTimeSetListener() {
						public void onTimeSet(TimePicker view, int hourOfDay,
											  int minute) {
							setHour(hourOfDay);
							setMinute(minute);
						}
					}, hourOfDay, minute, true);
			setHour(hourOfDay);
			setMinute(minute);
		}

		public int getHour() {
			return hour;
		}

		public void setHour(int hour) {
			this.hour = hour;
			textView.setText(this.hour + "时" + this.minute + "分");
		}

		public int getMinute() {
			return minute;
		}

		public void setMinute(int minute) {
			this.minute = minute;
			textView.setText(this.hour + "时" + this.minute + "分");
		}

		// 拦截事件，不让button获取到事件
		public boolean onInterceptTouchEvent(MotionEvent ev) {
			return true;
		}
	}
}
