package com.creativecapsuleprojects.testandroid;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class TextCustomizer extends Activity implements OnClickListener {

	private static final String DEBUG_TAG = "Utility - Text Customizer";

	private static final String fontFile1 = "fonts/Raleway-Bold.otf";
	private static final String fontFile2 = "fonts/Raleway-Light.otf";
	private static final String fontFile3 = "fonts/Raleway-Medium.otf";
	private static final String fontFile4 = "fonts/Raleway-Regular.otf";

	Button fontBtn1, fontBtn2, fontBtn3, fontBtn4;
	Button colorBtn1, colorBtn2, colorBtn3, colorBtn4, colorBtn5, colorBtn6,
			colorBtn7, colorBtn8;
	SeekBar textSizeController;
	CustomTextView customizedTextView;

	private float mScaleSpan = 0;
	private boolean isScaling = false;
	private ScaleGestureDetector mScaleDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text_customizer);

		mScaleDetector = new ScaleGestureDetector(this, new ScaleListener());

		customizedTextView = (CustomTextView) findViewById(R.id.custom_text_view);

		fontBtn1 = (Button) findViewById(R.id.text_font_sample_1);
		fontBtn1.setOnClickListener(this);

		fontBtn2 = (Button) findViewById(R.id.text_font_sample_2);
		fontBtn2.setOnClickListener(this);

		fontBtn3 = (Button) findViewById(R.id.text_font_sample_3);
		fontBtn3.setOnClickListener(this);

		fontBtn4 = (Button) findViewById(R.id.text_font_sample_4);
		fontBtn4.setOnClickListener(this);

		textSizeController = (SeekBar) findViewById(R.id.text_size_controller);
		textSizeController
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						// TODO Auto-generated method stub
						Log.d(DEBUG_TAG, "Progress changed to " + progress);
						setSizeToTextView(customizedTextView, progress + 10);
					}
				});

		colorBtn1 = (Button) findViewById(R.id.text_color_sample_1);
		colorBtn2 = (Button) findViewById(R.id.text_color_sample_2);
		colorBtn3 = (Button) findViewById(R.id.text_color_sample_3);
		colorBtn4 = (Button) findViewById(R.id.text_color_sample_4);
		colorBtn5 = (Button) findViewById(R.id.text_color_sample_5);
		colorBtn6 = (Button) findViewById(R.id.text_color_sample_6);
		colorBtn7 = (Button) findViewById(R.id.text_color_sample_7);
		colorBtn8 = (Button) findViewById(R.id.text_color_sample_8);
		colorBtn1.setOnClickListener(this);
		colorBtn2.setOnClickListener(this);
		colorBtn3.setOnClickListener(this);
		colorBtn4.setOnClickListener(this);
		colorBtn5.setOnClickListener(this);
		colorBtn6.setOnClickListener(this);
		colorBtn7.setOnClickListener(this);
		colorBtn8.setOnClickListener(this);

		LayoutParams params = (LayoutParams) customizedTextView
				.getLayoutParams();
		Log.d(DEBUG_TAG, "Textview Initial Width:" + params.width + "- Height:"
				+ params.height);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		mScaleDetector.onTouchEvent(event);
		return true;
	}

	@Override
	public void onClick(View clickedView) {
		switch (clickedView.getId()) {
		case R.id.text_font_sample_1:
			Typeface tf1 = Typeface.createFromAsset(getAssets(), fontFile1);
			// setFontToTextView(customizedTextView, tf1);

			// sample
			resizeTextViewWithScale(customizedTextView, 5);
			break;
		case R.id.text_font_sample_2:
			Typeface tf2 = Typeface.createFromAsset(getAssets(), fontFile2);
			// setFontToTextView(customizedTextView, tf2);

			resizeTextViewWithScale(customizedTextView, -5);
			break;
		case R.id.text_font_sample_3:
			Typeface tf3 = Typeface.createFromAsset(getAssets(), fontFile3);
			setFontToTextView(customizedTextView, tf3);
			break;
		case R.id.text_font_sample_4:
			Typeface tf4 = Typeface.createFromAsset(getAssets(), fontFile4);
			setFontToTextView(customizedTextView, tf4);
			break;

		case R.id.text_color_sample_1:
			int color1 = getResources().getColor(R.color.black);
			setColorToTextView(customizedTextView, color1);
			break;
		case R.id.text_color_sample_2:
			int color2 = getResources().getColor(R.color.blue);
			setColorToTextView(customizedTextView, color2);
			break;
		case R.id.text_color_sample_3:
			int color3 = getResources().getColor(R.color.brown);
			setColorToTextView(customizedTextView, color3);
			break;
		case R.id.text_color_sample_4:
			int color4 = getResources().getColor(R.color.gray);
			setColorToTextView(customizedTextView, color4);
			break;
		case R.id.text_color_sample_5:
			int color5 = getResources().getColor(R.color.green);
			setColorToTextView(customizedTextView, color5);
			break;
		case R.id.text_color_sample_6:
			int color6 = getResources().getColor(R.color.purple);
			setColorToTextView(customizedTextView, color6);
			break;
		case R.id.text_color_sample_7:
			int color7 = getResources().getColor(R.color.red);
			setColorToTextView(customizedTextView, color7);
			break;
		case R.id.text_color_sample_8:
			int color8 = getResources().getColor(R.color.yellow);
			setColorToTextView(customizedTextView, color8);
			break;
		default:
			break;
		}
	}

	private void setFontToTextView(TextView textView, Typeface font) {
		textView.setTypeface(font);
	}

	private void setSizeToTextView(TextView textView, int size) {
		textView.setTextSize((float) size);
	}

	private void setColorToTextView(TextView textview, int color) {
		textview.setTextColor(color);
	}

	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			float newScale = detector.getCurrentSpan();
			Log.d(DEBUG_TAG, "is scaling");
			if (isScaling) {
				float diff = newScale - mScaleSpan;
				Log.d(DEBUG_TAG, "scaling updated with diff:" + diff);
				resizeTextViewWithScale(customizedTextView, diff);
			}
			mScaleSpan = detector.getCurrentSpan(); // average distance between

			return true;
		}

		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			// TODO Auto-generated method stub
			Log.d(DEBUG_TAG, "started scaling");
			isScaling = true;
			return super.onScaleBegin(detector);
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector) {
			// TODO Auto-generated method stub
			Log.d(DEBUG_TAG, "stopped scaling");
			isScaling = false;
			mScaleSpan = 0.0f;
			super.onScaleEnd(detector);
		}

	}

	/*
	 * private float spacing(MotionEvent event) { float x = event.getX(0) -
	 * event.getX(1); float y = event.getY(0) - event.getY(1); return
	 * FloatMath.sqrt(x * x + y * y); }
	 * 
	 * private void midPoint(PointF point, MotionEvent event) { float x =
	 * event.getX(0) + event.getX(1); float y = event.getY(0) + event.getY(1);
	 * point.set(x / 2, y / 2); }
	 */

	private void resizeTextViewWithScale(TextView textView, float scale) {
		if (scale > 10 || scale < -10)
			return;

		LayoutParams params = (LayoutParams) textView
				.getLayoutParams();
		Log.d(DEBUG_TAG, "Textview current Width:" + params.width + "- Height:"
				+ params.height + " scale:" + scale);
		int newHeight = params.height + (int) scale;
		int newWidth = params.width + (int) scale;

		params.height = newHeight;
		params.width = newWidth;
		textView.setLayoutParams(params);
		Log.d(DEBUG_TAG, "Textview updated Width:" + params.width + "- Height:"
				+ params.height);
	}

}
