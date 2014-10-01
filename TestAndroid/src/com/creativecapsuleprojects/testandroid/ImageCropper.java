package com.creativecapsuleprojects.testandroid;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.creativecapsuleprojects.testandroid.utility.CustomImageView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;

public class ImageCropper extends Activity {
	
	
	Uri sourceUri;
	Uri targetUri;
	int outputWidth,outputHeight;
	int outputShape;
	
	//intent parameters
	public static final String IMAGE_CROPPER_OUTPUT_X = "outputX";
	public static final String IMAGE_CROPPER_OUTPUT_Y = "outputY";
	public static final String IMAGE_CROPPER_SOURCE_URI = "source_uri";
	public static final String IMAGE_CROPPER_TARGET_URI = "target_uri";
	public static final String IMAGE_CROPPER_OUTPUT_SHAPE = "output_shape";
	
	public static final int outputShapeTypeRectangle = 101;
	public static final int outputShapeTypeSquare = 102;
	public static final int outputShapeTypeCircle = 103;
	
	private static final String DEBUG_TAG = "Image Cropper";
	
	CustomImageView sourceImagePreview;
	ImageView imageCropMask;
	
	Bitmap sourceBitmap,scaledBitmap,croppedBitmap;
	
	int cropFrameX,cropFrameY,cropFrameWidth,cropFrameHeight;
	int sourceFrameX,sourceFrameY,sourceFrameWidth,sourceFrameHeight;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_cropper);
		
		sourceImagePreview = (CustomImageView) findViewById(R.id.source_image_preview);
		sourceImagePreview.setScaleType(ScaleType.MATRIX);
		sourceImagePreview.setEnabled(false);
		imageCropMask = (ImageView) findViewById(R.id.image_crop_mask);
		
		sourceUri = Uri.parse("file://"+getIntent().getExtras().getString(IMAGE_CROPPER_SOURCE_URI));
		targetUri = Uri.parse("file://"+getIntent().getExtras().getString(IMAGE_CROPPER_TARGET_URI));
		Log.d(DEBUG_TAG, "output URI:"+targetUri.getEncodedPath());
		outputWidth = getIntent().getExtras().getInt(IMAGE_CROPPER_OUTPUT_X);
		outputShape = getIntent().getExtras().getInt(IMAGE_CROPPER_OUTPUT_SHAPE,outputShapeTypeSquare);
		if(outputShape == outputShapeTypeRectangle){
			outputHeight = getIntent().getExtras().getInt(IMAGE_CROPPER_OUTPUT_Y);
		}
		else{
			outputHeight = outputWidth;
		}
		
		try {
			sourceBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), sourceUri);
			if(sourceBitmap.getWidth() > 1024){
				BitmapFactory.Options options = new BitmapFactory.Options();
		        options.inSampleSize = 4;
		        
		        int width = 1024;
		        int height = (int) ((float)sourceBitmap.getHeight() / sourceBitmap.getWidth() * width);
		        Log.d(DEBUG_TAG, "source width:"+sourceBitmap.getWidth()+" height:"+sourceBitmap.getHeight());
		        Log.d(DEBUG_TAG, "scaled width:"+width+" height:"+height);
		        
		        scaledBitmap = Bitmap.createScaledBitmap(sourceBitmap, width, height, false);
				sourceImagePreview.setImageBitmap(scaledBitmap);
				//sourceImagePreview.setImageBitmap(sourceBitmap);
			}
			else{
				sourceImagePreview.setImageBitmap(sourceBitmap);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		LayoutParams params = (LayoutParams) imageCropMask.getLayoutParams();
		params.width = outputWidth;
		params.height = outputHeight;
		imageCropMask.setLayoutParams(params);
		
		ViewTreeObserver imagePreviewObserver = sourceImagePreview.getViewTreeObserver();
		imagePreviewObserver.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				sourceFrameX = sourceImagePreview.getLeft();
				sourceFrameY = sourceImagePreview.getTop();
				sourceFrameWidth = sourceImagePreview.getWidth();
				sourceFrameHeight = sourceImagePreview.getHeight();
				Log.d(DEBUG_TAG, "Image frame x:"+sourceFrameX+" y:"+sourceFrameY+" width:"+sourceFrameWidth+" height:"+sourceFrameHeight);
				sourceImagePreview.setEnabled(true);
				
			}
		});
		
		Button saveBtn = (Button) findViewById(R.id.image_crop_save_btn);
		saveBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(DEBUG_TAG, "crop mask frame x:"+imageCropMask.getLeft()+" y:"+imageCropMask.getTop()+" width:"+imageCropMask.getWidth()+" height:"+imageCropMask.getHeight());
				float frame[] = sourceImagePreview.getImageFrame();
				boolean successfullyWritten = false;
				
				int x,y,w,h;
				x = (int)((imageCropMask.getLeft() - frame[0]) * (float)sourceBitmap.getWidth()/frame[2]);
				y = (int)((imageCropMask.getTop() - frame[1]) * (float)sourceBitmap.getHeight()/frame[3]);
				w = (int)(imageCropMask.getWidth()/frame[2] * (float)sourceBitmap.getWidth());
				h = (int)(imageCropMask.getHeight()/frame[3] * (float)sourceBitmap.getHeight());
				
				Log.d(DEBUG_TAG, "cropped view frame x:"+(imageCropMask.getLeft() - frame[0])+" y:"+(imageCropMask.getTop() - frame[1])+" width:"+imageCropMask.getWidth()+" height:"+imageCropMask.getHeight());
				Log.d(DEBUG_TAG, "source Image view frame x:"+frame[0]+" y:"+frame[1]+" width:"+frame[2]+" height:"+frame[3]);
				Log.d(DEBUG_TAG, "source Image crop frame x:"+x+" y:"+y+" width:"+w+" height:"+h);
				
				//crop image from source image
				croppedBitmap = Bitmap.createBitmap(sourceBitmap, x,y,w,h);
				//scale the image to desired dimensions
				croppedBitmap = Bitmap.createScaledBitmap(croppedBitmap, outputWidth, outputHeight, false);
				if(outputShape == outputShapeTypeCircle){
					croppedBitmap = cropCircularImage(croppedBitmap);
				}
				try {
					//write to output URI
					String filePath = targetUri.getEncodedPath();
					File tempFile = new File(filePath);
					tempFile.createNewFile();
					FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
					BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
					croppedBitmap.compress(CompressFormat.JPEG, 100, bos);
					bos.flush();
					bos.close();
					
					successfullyWritten = true;

				} catch (FileNotFoundException e) {
					Log.w("TAG", "Error saving image file FileNotFoundException: " + e.getMessage());
					e.printStackTrace();
				} catch (IOException e) {
					Log.w("TAG", "Error saving image file IOException: " + e.getMessage());
					e.printStackTrace();
				}
				
				if(successfullyWritten){
					closeWithSuccess();
				}
				else{
					//closeWithFailure();
				}
			}
		});
		
		Button cancelBtn = (Button) findViewById(R.id.image_crop_cancel_btn);
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sourceImagePreview.getImageFrame();
				//closeWithFailure();
			}
		});
	}
	
	@SuppressLint("NewApi")
	View.OnLayoutChangeListener layoutChangedListener = new OnLayoutChangeListener() {
		
		@Override
		public void onLayoutChange(View view, int left, int top, int right,
				int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
			if(view == sourceImagePreview){
				sourceFrameX = sourceImagePreview.getLeft();
				sourceFrameY = sourceImagePreview.getTop();
				sourceFrameWidth = sourceImagePreview.getWidth();
				sourceFrameHeight = sourceImagePreview.getHeight();
				Log.d(DEBUG_TAG, "Image o=LayoutChange frame x:"+sourceFrameX+" y:"+sourceFrameY+" width:"+sourceFrameWidth+" height:"+sourceFrameHeight);
				sourceImagePreview.setEnabled(true);
			}
		}
	};
	

	@Override
	public void onBackPressed() {
		//super.onBackPressed();
		closeWithFailure();
	}

	private void closeWithFailure() {
		finish();
	}
	
	private void closeWithSuccess() {
		Intent resultIntent = new Intent();
		setResult(RESULT_OK, resultIntent);
		this.finish();
	}
	
	private void imagePreviewLayoutUpdated(){
		//check if frame outside image and reset
	}
	
	private Bitmap cropCircularImage(Bitmap source){
		Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, source.getWidth(), source.getHeight());

	    float r = 0;

	    if (source.getWidth() > source.getHeight()) {
	        r = source.getHeight() / 2;
	    } else {
	        r = source.getWidth() / 2;
	    }

	    paint.setAntiAlias(true);
	    canvas.drawColor(Color.TRANSPARENT);
	    paint.setColor(color);
	    
	    canvas.drawCircle(r, r, r, paint);
	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(source, rect, rect, paint);
		
		return output;
	}

}
