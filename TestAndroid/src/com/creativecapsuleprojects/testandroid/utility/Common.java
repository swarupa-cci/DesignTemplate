package com.creativecapsuleprojects.testandroid.utility;

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.view.Window;

public class Common {

	public static boolean isInternetConnectivityAvailable(Context context) {
		boolean internetConnectionAvailable;

		ConnectivityManager connectivitymanager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = connectivitymanager.getActiveNetworkInfo();
		internetConnectionAvailable = ((networkinfo != null)
				&& (networkinfo.isAvailable()) && (networkinfo.isConnected()));

		return internetConnectionAvailable;
	}

	public static Bitmap obtainBitmapFromUri(Uri uri, Context context) {
		Bitmap bitmap = null;
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		ContentResolver cr = context.getContentResolver();
		try {
			InputStream stream = cr.openInputStream(uri);
			bitmap = BitmapFactory.decodeStream(stream, null, options);
			options.inJustDecodeBounds = false;

			stream = cr.openInputStream(uri);
			bitmap = BitmapFactory.decodeStream(stream, null, options);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	public static Bitmap obtainBitmapOfDesiredSize(Bitmap bitmap, int reqWidth,
			int reqHeight) {

		// Get height and width of bitmap
		int bitmapWidth = bitmap.getWidth();
		int bitmapHeight = bitmap.getHeight();

		float ratioWidth = ((float) bitmapWidth / (float) reqWidth);
		float ratioHeight = ((float) bitmapHeight / (float) reqHeight);

		float scale = 1.0f;
		int width = 0;
		int height = 0;
		if ((ratioHeight > ratioWidth)) {

			scale = (float) reqWidth / (float) bitmapWidth;
		} else {

			scale = (float) reqHeight / (float) bitmapHeight;
		}

		width = (int) ((float) reqWidth / scale);
		height = (int) ((float) reqHeight / scale);

		int diffWidth = bitmapWidth - width;
		int diffHeight = bitmapHeight - height;

		bitmap = Bitmap.createBitmap(bitmap, diffWidth / 2, diffHeight / 2,
				width, height);

		Bitmap resizedbitmap = Bitmap.createScaledBitmap(bitmap, reqWidth,
				reqHeight, true);
		bitmap = resizedbitmap;
		resizedbitmap = null;

		return bitmap;
	}

	private static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}

	public static Bitmap scaleDownBitmap(Bitmap photo, int newHeight,
			Context context) {

		final float densityMultiplier = context.getResources()
				.getDisplayMetrics().density;

		int h = (int) (newHeight * densityMultiplier);
		int w = (int) (h * photo.getWidth() / ((double) photo.getHeight()));

		photo = Bitmap.createScaledBitmap(photo, w, h, true);

		return photo;
	}

	public static void showCustomAlertDialog(Context context, String message,
			String positiveButtonMessage) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message);
		builder.setPositiveButton(positiveButtonMessage, new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.show();
	}

	

}
