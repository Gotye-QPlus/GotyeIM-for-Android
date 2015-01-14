package com.open_demo.activity;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.gotye.api.FileUtil;
import com.open_demo.R;
import com.open_demo.view.photoview.PhotoView;
/**
 * 下载显示大图
 * 
 */
public class ShowBigImage extends Activity {
	private PhotoView image;
	private int default_res = R.drawable.ic_launcher;
	// flag to indicate if need to delete image on server after download
	private Bitmap bitmap;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_show_big_image);
		image = (PhotoView) findViewById(R.id.image);
		default_res = getIntent().getIntExtra("default_image",
				R.drawable.ic_launcher);
		showMap(getIntent());
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent != null) {
			showMap(intent);
		}
	}

	private void showMap(Intent intent) {
		Uri uri = intent.getParcelableExtra("uri");
		if (uri != null && new File(uri.getPath()).exists()) {
			String path = uri.getPath();
			byte[] bitmapData=FileUtil.getBytes(path);
			bitmap= BitmapFactory.decodeByteArray(bitmapData, 0, bitmapData.length);
			if (bitmap != null) {
				image.setImageBitmap(bitmap);
			}
		} else {
			image.setImageResource(default_res);
		}
		image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
