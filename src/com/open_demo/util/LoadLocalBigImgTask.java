package com.open_demo.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.open_demo.R;
import com.open_demo.view.photoview.PhotoView;

public class LoadLocalBigImgTask extends AsyncTask<Void, Void, Bitmap> {

	private ProgressBar pb;
	private PhotoView photoView;
	private String path;
	//private int width;
	//private int height;
	private Context context;

	public LoadLocalBigImgTask(Context context, String path,
			PhotoView photoView, ProgressBar pb, int width, int height) {
		this.context = context;
		this.path = path;
		this.photoView = photoView;
		this.pb = pb;
		//this.width = width;
		//this.height = height;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pb.setVisibility(View.INVISIBLE);
		photoView.setVisibility(View.VISIBLE);
	}

	@Override
	protected Bitmap doInBackground(Void... params) {
		Bitmap bitmap = BitmapUtil.getBitmap(path);
		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		super.onPostExecute(result);
		pb.setVisibility(View.INVISIBLE);
		photoView.setVisibility(View.VISIBLE);
		if (result != null)
			ImageCache.getInstance().put(path, result);
		else
			result = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.ic_launcher);
		photoView.setImageBitmap(result);
	}
}
