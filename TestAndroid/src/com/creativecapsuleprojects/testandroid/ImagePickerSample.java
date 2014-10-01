package com.creativecapsuleprojects.testandroid;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class ImagePickerSample extends Activity implements OnClickListener {

	private static final String DEBUG_TAG = "Utility - Image Picker";
	private static final String TEMP_PHOTO_FILE = "temp_photo";

	AlertDialog dialog;
	Uri mImageCaptureUri, mImageCroppedUri;
	ImageView previewImage;

	private static final int PICK_FROM_CAMERA = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private static final int PICK_FROM_FILE = 3;
	private static final int REQUEST_IMAGE_CROP = 4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_picker);

		Button imagePickerBtn = (Button) findViewById(R.id.image_picker_btn);
		imagePickerBtn.setOnClickListener(this);

		previewImage = (ImageView) findViewById(R.id.preview_image);

		setupImagePickerDialog();
	}

	private void setupImagePickerDialog() {
		final String[] items = new String[] { "Take from camera",
				"Select from gallery" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.select_dialog_item, items);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Select Image");
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) { // pick
																	// from
																	// camera
				if (item == 0) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

					mImageCaptureUri = Uri.fromFile(new File(Environment
							.getExternalStorageDirectory(), "tmp_avatar_"
							+ String.valueOf(System.currentTimeMillis())
							+ ".jpg"));

					intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
							mImageCaptureUri);

					try {
						intent.putExtra("return-data", true);

						startActivityForResult(intent, PICK_FROM_CAMERA);
					} catch (ActivityNotFoundException e) {
						e.printStackTrace();
					}
				} else { // pick from file
					Intent intent = new Intent();

					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);

					startActivityForResult(Intent.createChooser(intent,
							"Complete action using"), PICK_FROM_FILE);
				}
			}
		});
		dialog = builder.create();
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.image_picker_btn:
			dialog.show();
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK)
			return;

		switch (requestCode) {
		case PICK_FROM_CAMERA:
			initImageCropper();
			break;

		case PICK_FROM_FILE:
			mImageCaptureUri = data.getData();
			doCrop();
			break;

		case CROP_FROM_CAMERA:
			Bundle extras = data.getExtras();
			if (extras != null) {
				try {
					Bitmap bitmap = MediaStore.Images.Media.getBitmap(
							this.getContentResolver(), mImageCroppedUri);
					previewImage.setImageBitmap(bitmap);
					deleteTempFile();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
			
		case REQUEST_IMAGE_CROP:
			try {
				Bitmap bitmap = MediaStore.Images.Media.getBitmap(
						this.getContentResolver(), mImageCroppedUri);
				if(bitmap.getWidth() > 1024){
					BitmapFactory.Options options = new BitmapFactory.Options();
			        options.inSampleSize = 4;
			        
			        int width = 1024;
			        int height = (int) ((float)bitmap.getHeight() / bitmap.getWidth() * width);
			        Log.d(DEBUG_TAG, "source width:"+bitmap.getWidth()+" height:"+bitmap.getHeight());
			        Log.d(DEBUG_TAG, "scaled width:"+width+" height:"+height);
			        
			        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
			        previewImage.setImageBitmap(scaledBitmap);
					//sourceImagePreview.setImageBitmap(sourceBitmap);
				}
				else{
					previewImage.setImageBitmap(bitmap);
				}
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;

		}
	}

	private void doCrop() {
		final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();


		deleteTempFile();
		
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");

		List<ResolveInfo> list = getPackageManager().queryIntentActivities(
				intent, 0);

		int size = list.size();

		if (size == 0) {
			Toast.makeText(this, "Can not find image crop app",
					Toast.LENGTH_SHORT).show();

			return;
		} else {
			intent.setData(mImageCaptureUri);
			mImageCroppedUri = getTempUri(System.currentTimeMillis());
			intent.putExtra("outputX", 200);
			intent.putExtra("outputY", 200);
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("scale", true);
			intent.putExtra("circleCrop", new String(""));
			intent.putExtra("return-data", false);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCroppedUri);
			intent.putExtra("outputFormat",
					Bitmap.CompressFormat.JPEG.toString());

			if (size == 1) {
				Intent i = new Intent(intent);
				ResolveInfo res = list.get(0);

				i.setComponent(new ComponentName(res.activityInfo.packageName,
						res.activityInfo.name));

				startActivityForResult(i, CROP_FROM_CAMERA);
			} else {
				for (ResolveInfo res : list) {
					final CropOption co = new CropOption();

					co.title = getPackageManager().getApplicationLabel(
							res.activityInfo.applicationInfo);
					co.icon = getPackageManager().getApplicationIcon(
							res.activityInfo.applicationInfo);
					co.appIntent = new Intent(intent);

					co.appIntent.setComponent(new ComponentName(
									res.activityInfo.packageName,
									res.activityInfo.name));

					cropOptions.add(co);
				}

				CropOptionAdapter adapter = new CropOptionAdapter(
						getApplicationContext(), cropOptions);

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Choose Crop App");
				builder.setAdapter(adapter,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								startActivityForResult(
										cropOptions.get(item).appIntent,
										CROP_FROM_CAMERA);
							}
						});

				builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {

						if (mImageCaptureUri != null) {
							getContentResolver().delete(mImageCaptureUri, null,
									null);
							mImageCaptureUri = null;
						}
					}
				});

				AlertDialog alert = builder.create();

				alert.show();
			}
		}
	}

	private void initImageCropper(){
		mImageCroppedUri = Uri.fromFile(new File(Environment
				.getExternalStorageDirectory(), TEMP_PHOTO_FILE
				+ String.valueOf(System.currentTimeMillis())
				+ ".jpg"));
		Intent intent = new Intent(this, ImageCropper.class);
		intent.putExtra(ImageCropper.IMAGE_CROPPER_OUTPUT_X, 400);
		intent.putExtra(ImageCropper.IMAGE_CROPPER_OUTPUT_Y, 300);
		intent.putExtra(ImageCropper.IMAGE_CROPPER_OUTPUT_SHAPE, ImageCropper.outputShapeTypeCircle);
		intent.putExtra(ImageCropper.IMAGE_CROPPER_SOURCE_URI, mImageCaptureUri.getEncodedPath());
		intent.putExtra(ImageCropper.IMAGE_CROPPER_TARGET_URI, mImageCroppedUri.getEncodedPath());
		Log.d(DEBUG_TAG, "sent URI:"+mImageCroppedUri.getEncodedPath());
		startActivityForResult(intent, REQUEST_IMAGE_CROP);
	}
	
	private void deleteTempFile() {
		File f = new File(Environment.getExternalStorageDirectory(),
				TEMP_PHOTO_FILE);
		f.delete();
	}

	private Uri getTempUri(long subscript) {
		return Uri.fromFile(getTempFile(subscript));
	}

	private File getTempFile(long subscript) {
		if (isSDCARDMounted()) {
			File f = new File(Environment.getExternalStorageDirectory(),
					TEMP_PHOTO_FILE+"_"+subscript+".jpg");
			try {
				f.createNewFile();
			} catch (IOException e) {
				// Toast.makeText (this, R.string.fileIOIssue,
				// Toast.LENGTH_LONG). Show ();
			}
			return f;
		} else {
			return null;
		}
	}

	private boolean isSDCARDMounted() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED))
			return true;
		return false;
	}

}
