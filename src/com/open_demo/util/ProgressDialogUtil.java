package com.open_demo.util;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressDialogUtil {
	private static ProgressDialog progress;

	public static void showProgress(Context context, String message) {
		progress = new ProgressDialog(context);
		progress.setMessage(message);
		progress.setCanceledOnTouchOutside(false);
		progress.show();
	}

	public static void dismiss() {
		if (progress != null && progress.isShowing()) {
			progress.dismiss();
		}
	}
}
