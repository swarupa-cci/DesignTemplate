package com.creativecapsuleprojects.testandroid.utility;

import java.util.Stack;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class CustomImageView extends ImageView implements OnTouchListener {

	private Stack<Integer> eventStack = new Stack<Integer>();

	static Paint paint = new Paint();

	// transformations
	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();

	// States
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private int mode = NONE;

	// For zooming
	private PointF start = new PointF();
	private PointF mid = new PointF();
	private float oldDist = 1f;
	private float[] lastEvent = null;
	float MAX_ZOOM = 1.25f;
	float MIN_ZOOM = 0.20f;

	public float mCurrentScale = 1.0f;

	Canvas canvas;

	private float dx; // postTranslate X distance
	private float dy; // postTranslate Y distance
	private float[] matrixValues = new float[9];
	float matrixX = 0; // X coordinate of matrix inside the ImageView
	float matrixY = 0; // Y coordinate of matrix inside the ImageView
	float width = 0; // width of drawable
	float height = 0; // height of drawable
	
	private static final String DEBUG_TAG = "Custom Image View";

	public CustomImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnTouchListener(this);
	}

	public CustomImageView(Context context, Point centre, int width, int height) {

		super(context);

		this.setOnTouchListener(this);

	}

	/**
	 * @category OnTouchListener_Methods
	 */

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {

			this.eventStack.push(Integer.valueOf(MotionEvent.ACTION_DOWN));

			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			mode = DRAG;
			lastEvent = null;
			break;
		}

		case MotionEvent.ACTION_POINTER_DOWN: {
			this.eventStack.push(Integer
					.valueOf(MotionEvent.ACTION_POINTER_DOWN));

			oldDist = spacing(event);
			if (oldDist > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
			}
			lastEvent = new float[4];
			lastEvent[0] = event.getX(0);
			lastEvent[1] = event.getX(1);
			lastEvent[2] = event.getY(0);
			lastEvent[3] = event.getY(1);
			break;
		}

		case MotionEvent.ACTION_UP: {
			this.eventStack.push(Integer.valueOf(MotionEvent.ACTION_UP));

		}

		case MotionEvent.ACTION_POINTER_UP: {
			this.eventStack
					.push(Integer.valueOf(MotionEvent.ACTION_POINTER_UP));

			mode = NONE;
			lastEvent = null;
			break;
		}

		case MotionEvent.ACTION_MOVE: {
			this.eventStack.push(Integer.valueOf(MotionEvent.ACTION_MOVE));

			if (mode == DRAG) {
				matrix.set(savedMatrix);
				matrix.getValues(matrixValues);
				matrixX = matrixValues[2];
				matrixY = matrixValues[5];
				width = matrixValues[0]
						* (((ImageView) view).getDrawable().getIntrinsicWidth());
				height = matrixValues[4]
						* (((ImageView) view).getDrawable()
								.getIntrinsicHeight());

				dx = event.getX() - start.x;
				dy = event.getY() - start.y;

				// if image will go outside left bound
				if (matrixX + dx + width / 2 + getPaddingLeft() < 0) {
					dx = -matrixX - width / 2 - getPaddingLeft();
				}
				// if image will go outside right bound
				if (matrixX + dx + width / 2 + getPaddingRight()
						+ getPaddingLeft() > view.getWidth()) {
					dx = view.getWidth() - matrixX - width / 2
							- getPaddingRight() - getPaddingLeft();
				}
				// if image will go oustside top bound
				if (matrixY + dy + height / 2 + getPaddingTop() < 0) {
					dy = -matrixY - height / 2 - getPaddingTop();
				}

				// if image will go outside bottom bound
				if (matrixY + dy + height / 2 + getPaddingBottom()
						+ getPaddingTop() > view.getHeight()
						- getPaddingBottom()) {
					dy = view.getHeight() - matrixY - height / 2
							- getPaddingTop() - getPaddingBottom();
				}

				matrix.postTranslate(dx, dy);
			}
			if (mode == ZOOM) {
				float newDist = spacing(event);
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float scale = newDist / oldDist;
					matrix.getValues(matrixValues);
					float currentScale = matrixValues[Matrix.MSCALE_X];
					mCurrentScale = currentScale;
					// limit zoom
					if (scale * currentScale > MAX_ZOOM) {
						scale = MAX_ZOOM / currentScale;
					} else if (scale * currentScale < MIN_ZOOM) {
						scale = MIN_ZOOM / currentScale;
					}

					matrix.postScale(scale, scale, mid.x, mid.y);
					this.setImageMatrix(matrix);
					this.translate(0, 0);
				}
			}
			break;
		}
		}
		this.setImageMatrix(matrix);
		return true;
	}

	private void translate(float dx, float dy) {

		matrix.getValues(matrixValues);
		matrixX = matrixValues[2];
		matrixY = matrixValues[5];
		width = matrixValues[0] * this.getDrawable().getIntrinsicWidth();
		height = matrixValues[4] * this.getDrawable().getIntrinsicHeight();

		// if image will go outside left bound
		if (matrixX + dx + width / 2 < 0) {
			dx = -matrixX - width / 2;
		}
		// if image will go outside right bound
		if (matrixX + dx + width / 2 > this.getWidth()) {
			dx = this.getWidth() - matrixX - width / 2;
		}
		// if image will go oustside top bound
		if (matrixY + dy + height / 2 < 0) {
			dy = -matrixY - height / 2;
		}
		// if image will go outside bottom bound
		if (matrixY + dy + height / 2 > this.getHeight()) {
			dy = this.getHeight() - matrixY - height / 2;
		}
		matrix.postTranslate(dx, dy);
	}

	// Determine space between first two fingers
	private float spacing(MotionEvent event) {

		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

	// Calculate the mid point of the first two fingers
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}
	
	public float[] getImageFrame(){
		float[] frame = new float[4];
		
		//Log.d(DEBUG_TAG, "custom Image frame x:"+this.getLeft()+" y:"+this.getTop()+" width:"+this.getWidth()+" height:"+this.getHeight());
		matrix.getValues(matrixValues);
		//Log.d(DEBUG_TAG, "Matrix values:"+matrixValues[0]+","+matrixValues[1]+","+matrixValues[2]+","+matrixValues[3]+","+matrixValues[4]+","+matrixValues[5]+","+matrixValues[6]+","+matrixValues[7]+","+matrixValues[8]);
		frame[0] = matrixValues[2];
		frame[1] = matrixValues[5];
		frame[2] = matrixValues[0] * this.getDrawable().getIntrinsicWidth();
		frame[3] = matrixValues[4] * this.getDrawable().getIntrinsicHeight();
		//Log.d(DEBUG_TAG, "Matrix frame values:"+frame[0]+","+frame[1]+","+frame[2]+","+frame[3]);
		return frame;
	}
	
}
