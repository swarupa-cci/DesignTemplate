package com.creativecapsuleprojects.testandroid;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_cropper);
		
		sourceUri = Uri.parse(getIntent().getExtras().getString(IMAGE_CROPPER_SOURCE_URI));
		sourceUri = Uri.parse(getIntent().getExtras().getString(IMAGE_CROPPER_SOURCE_URI));
		outputWidth = getIntent().getExtras().getInt(IMAGE_CROPPER_OUTPUT_X);
		outputShape = getIntent().getExtras().getInt(IMAGE_CROPPER_OUTPUT_SHAPE,outputShapeTypeSquare);
		if(outputShape == outputShapeTypeRectangle){
			outputHeight = getIntent().getExtras().getInt(IMAGE_CROPPER_OUTPUT_Y);
		}
		else{
			outputHeight = outputWidth;
		}
		
		try {
			Bitmap sourceBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), sourceUri);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
