package com.creativecapsuleprojects.testandroid;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class CustomTextView extends TextView implements OnTouchListener{

	private static final String DEBUG_TAG = "Test Android - CustomTextView";
	int initialX,initialY;
	
	
	public CustomTextView(Context context) {
		super(context);
		
		this.setOnTouchListener(this);
	}

	public CustomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnTouchListener(this);
	}
	
	public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.setOnTouchListener(this);
	}
	
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {
			initialX = (int) event.getX();
            initialY = (int) event.getY();
			break;
		}
		
		case MotionEvent.ACTION_MOVE: {
			int currentX = (int) event.getX();
            int currentY = (int) event.getY();
            LayoutParams lp = (LayoutParams) this.getLayoutParams();

            int left = lp.leftMargin + (currentX - initialX);
            int top = lp.topMargin + (currentY - initialY);
            int right = lp.rightMargin - (currentX - initialX);
            int bottom = lp.bottomMargin - (currentY - initialY);

            lp.rightMargin = right;
            lp.leftMargin = left;
            lp.bottomMargin = bottom;
            lp.topMargin = top;

            this.setLayoutParams(lp);
			break;
		}
		}
		return true;
	}


}
