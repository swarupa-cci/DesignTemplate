package com.creativecapsuleprojects.testandroid;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

public class ImageFromView extends Activity {

	private static final String DEBUG_TAG = "ImageFromView";
	RelativeLayout containerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_from_view);

		containerView = (RelativeLayout) findViewById(R.id.container_view);

		Button save2x = (Button) findViewById(R.id.save_2_x);
		save2x.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveImageWithScale(6);
			}
		});

		Button save3x = (Button) findViewById(R.id.save_3_x);
		save3x.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveImageWithScale(3);
			}
		});

		Button save4x = (Button) findViewById(R.id.save_4_x);
		save4x.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveImageWithScale(4);
			}
		});

		Button saveTransparentBtn = (Button) findViewById(R.id.save_transparent);
		saveTransparentBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveTransparentImage(1);
			}
		});

	}

	private void saveImageWithScale(int scale) {
		// create bitmap
		Canvas bitmapCanvas = new Canvas();
		Bitmap bitmap = Bitmap.createBitmap(containerView.getWidth() * scale,
				containerView.getHeight() * scale, Bitmap.Config.ARGB_8888);

		bitmapCanvas.setBitmap(bitmap);
		bitmapCanvas.scale(scale, scale);
		containerView.draw(bitmapCanvas);

		try {
			// write to memory
			String filePath = Environment.getExternalStorageDirectory()
					+ "/image_" + scale + "x_" + ".jpg";
			File tempFile = new File(filePath);
			tempFile.createNewFile();
			FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
			BufferedOutputStream bos = new BufferedOutputStream(
					fileOutputStream);
			bitmap.compress(CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			Log.d(DEBUG_TAG, "image saved with scale:" + scale);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void saveTransparentImage(int scale) {
		// create bitmap
		Bitmap bitmap = Bitmap.createBitmap(containerView.getWidth() * scale,
				containerView.getHeight() * scale, Bitmap.Config.ARGB_8888);
		Canvas bitmapCanvas = new Canvas(bitmap);
		bitmapCanvas.setBitmap(bitmap);
		bitmapCanvas.scale(scale, scale);
		containerView.draw(bitmapCanvas);
		
		//apply blur filter
		Bitmap blurredBmp = getBlurredImage(bitmap, 25);
		Drawable dr = new BitmapDrawable(getResources(), blurredBmp);
		containerView.removeAllViews();
		if (Build.VERSION.SDK_INT >= 16) {

			containerView.setBackground(dr);

		} else {

			containerView.setBackgroundDrawable(dr);
		}

		try {
			// write to memory
			String filePath = Environment.getExternalStorageDirectory()
					+ "/image_blurred_" + scale + "x_" + System.currentTimeMillis() + ".jpg";
			File tempFile = new File(filePath);
			tempFile.createNewFile();
			FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
			BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
			blurredBmp.compress(CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			Log.d(DEBUG_TAG, "blurred image saved with scale:" + scale);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Bitmap getBlurredImage(Bitmap source,int radius){
		Bitmap overlay = Bitmap.createBitmap(
				source.getWidth(), 
		        source.getHeight(), 
		        Bitmap.Config.ARGB_8888);
		 
		    Canvas canvas = new Canvas(overlay);
		    canvas.drawBitmap(source, 0, 
		        0, null);
		    //canvas.scale(0.8f, 0.8f);
		 
		    RenderScript rs = RenderScript.create(this);
		 
		    Allocation overlayAlloc = Allocation.createFromBitmap(
		        rs, overlay);
		 
		    ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(
		        rs, overlayAlloc.getElement());
		 
		    blur.setInput(overlayAlloc);
		 
		    blur.setRadius(radius);
		 
		    blur.forEach(overlayAlloc);
		 
		    overlayAlloc.copyTo(overlay);
		    
		    return overlay;
	}

}
