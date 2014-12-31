package com.open_demo.util;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

public class URIUtil {
	public static String toPath(Context context, Uri selectedImage) {
		// String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(selectedImage, null,
				null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex("_data");
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			cursor = null;
			return picturePath;

		} else {
			File file = new File(selectedImage.getPath());
			if (file.exists()) {
				return file.getAbsolutePath();
			}
		}
		return null;
	}

	public static String uriToPath(Context context, Uri uri) {
		String scheme = uri.getScheme();
		if (scheme.equals("content")) {
			return initFromContentUri(context, uri);
		} else if (uri.getScheme().equals("file")) {
			return initFromFile(context, uri);
		}
		return null;
	}

	private static String initFromContentUri(Context context, Uri uri) {
		String fileName = null;

		if (uri != null) {
			if (uri.getScheme().toString().compareTo("content") == 0) // content://开头的uri
			{
				Cursor cursor = context.getContentResolver().query(uri, null,
						null, null, null);
				if (cursor != null && cursor.moveToFirst()) {
					int column_index = cursor.getColumnIndex("_data");
					fileName = cursor.getString(column_index); // 取出文件路径
					cursor.close();
				}
			} else if (uri.getScheme().compareTo("file") == 0) // file:///开头的uri
			{
				fileName = uri.toString();
				fileName = uri.toString().replace("file://", "");
				// 替换file://
				if (!fileName.startsWith("/mnt")) {
					// 加上"/mnt"头
					fileName += "/mnt";
				}
			}
		}
		return fileName;
	}

	private static String initFromFile(Context context, Uri uri) {
		String mPath = uri.getPath();
		String extension = MimeTypeMap.getFileExtensionFromUrl(mPath);
		if (TextUtils.isEmpty(extension)) {
			// getMimeTypeFromExtension() doesn't handle spaces in filenames nor
			// can it handle
			// urlEncoded strings. Let's try one last time at finding the
			// extension.
			int dotPos = mPath.lastIndexOf('.');
			if (0 <= dotPos) {
				extension = mPath.substring(dotPos + 1);
			}
		}

		return mPath;
	}
}
