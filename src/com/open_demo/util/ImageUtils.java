package com.open_demo.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.NinePatchDrawable;
import android.media.ExifInterface;
import android.net.Uri;

public class ImageUtils {

	private static final int IMAGE_COMPRESSION_QUALITY = 90;
	private static final int NUMBER_OF_RESIZE_ATTEMPTS = 100;
	private static final int MINIMUM_IMAGE_COMPRESSION_QUALITY = 50;

	
	public static Bitmap toRoundCorner(Context context, Bitmap src, Bitmap dst) {
		
		if(src == null || dst == null){
			return null;
		}
//		NinePatchDrawable nine = (NinePatchDrawable) context.getResources().getDrawable(R.drawable.gotye_bg_msg_text_normal_right);
//		nine.setBounds(0, 0, src.getWidth(), src.getHeight());
		
		Bitmap output = Bitmap.createBitmap(dst.getWidth(),
				dst.getHeight(), Config.ARGB_8888);

		Canvas canvas = new Canvas(output);
		Paint paint = new Paint();
		canvas.drawBitmap(dst, 0, 0, paint);
		
		int width = dst.getWidth();
		
		//Bitmap bitmap = src;
		src = Bitmap.createScaledBitmap(src, width,
				dst.getHeight(), true);
		if(src == null){
			return null;
		}
		
		paint.setAntiAlias(true);
//		canvas.drawBitmap(dst, 0, 0, paint);
		
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(src, 0, 0, paint);
		canvas.save();
		
		return output;
	}
	
	public static Bitmap toRoundCornerScaleDst(Context context, Bitmap src, NinePatchDrawable dstDrawable) {
		if(src == null || dstDrawable == null){
			return null;
		}
		dstDrawable.setBounds(0, 0, src.getWidth(), src.getHeight());
		
		Bitmap output = Bitmap.createBitmap(src.getWidth(),
				src.getHeight(), Config.ARGB_8888);

		Canvas canvas = new Canvas(output);
		Paint paint = new Paint();
		dstDrawable.draw(canvas);
//		canvas.drawBitmap(dstDrawablable.get, 0, 0, paint);
		
//		int width = dst.getWidth();
		
//		Bitmap bitmap = src;
//		src = Bitmap.createScaledBitmap(src, width,
//				dst.getHeight(), true);
		
		
		paint.setAntiAlias(true);
//		canvas.drawBitmap(dst, 0, 0, paint);
		
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(src, 0, 0, paint);
		canvas.save();
		
		return output;
	}

	/**
	 * Resize and recompress the image such that it fits the given limits. The
	 * resulting byte array contains an image in JPEG format, regardless of the
	 * original image's content type.
	 * 
	 * @param widthLimit
	 *            The width limit, in pixels
	 * @param heightLimit
	 *            The height limit, in pixels
	 * @param byteLimit
	 *            The binary size limit, in bytes
	 * @return A resized/recompressed version of this image, in JPEG format
	 */
	public static byte[] getResizedImageData(byte[] image, int width,
			int height, int widthLimit, int heightLimit, int byteLimit) {
		if (image == null) {
			return null;
		}
		// int width = WIDTH;
		// int height = HEIGHT;

		// int widthLimit = WIDTH_LIMIT;
		// int heightLimit = HEIGHT_LIMIT;

		// int byteLimit = byteLimit;

		int outWidth = width;
		int outHeight = height;

		float scaleFactor = 1.F;
		while ((outWidth * scaleFactor > widthLimit)
				|| (outHeight * scaleFactor > heightLimit)) {
			scaleFactor *= .75F;
		}

		// if (Log.isLoggable(LogTag.APP, Log.VERBOSE)) {
		// Log.v(TAG, "getResizedBitmap: wlimit=" + widthLimit +
		// ", hlimit=" + heightLimit + ", sizeLimit=" + byteLimit +
		// ", width=" + width + ", height=" + height +
		// ", initialScaleFactor=" + scaleFactor +
		// ", uri=" + uri);
		// }

		InputStream input = null;
		try {
			ByteArrayOutputStream os = null;
			int attempts = 1;
			int sampleSize = 1;
			BitmapFactory.Options options = new BitmapFactory.Options();
			int quality = IMAGE_COMPRESSION_QUALITY;
			Bitmap b = null;

			// In this loop, attempt to decode the stream with the best possible
			// subsampling (we
			// start with 1, which means no subsampling - get the original
			// content) without running
			// out of memory.
			do {
				input = new ByteArrayInputStream(image);
				options.inSampleSize = sampleSize;
				try {
					b = BitmapFactory.decodeStream(input, null, options);
					if (b == null) {
						return null; // Couldn't decode and it wasn't because of
										// an exception,
										// bail.
					}
				} catch (OutOfMemoryError e) {
					// Log.w(TAG,
					// "getResizedBitmap: img too large to decode (OutOfMemoryError), "
					// +
					// "may try with larger sampleSize. Curr sampleSize=" +
					// sampleSize);
					sampleSize *= 2; // works best as a power of two
					attempts++;
					continue;
				} finally {
					if (input != null) {
						try {
							input.close();
						} catch (IOException e) {
							// Log.e(TAG, e.getMessage(), e);
						}
					}
				}
			} while (b == null && attempts < NUMBER_OF_RESIZE_ATTEMPTS);

			if (b == null) {
				// if (Log.isLoggable(LogTag.APP, Log.VERBOSE)
				// && attempts >= NUMBER_OF_RESIZE_ATTEMPTS) {
				// Log.v(TAG,
				// "getResizedImageData: gave up after too many attempts to resize");
				// }
				return null;
			}

			boolean resultTooBig = true;
			attempts = 1; // reset count for second loop
			// In this loop, we attempt to compress/resize the content to fit
			// the given dimension
			// and file-size limits.
			do {
				try {
					if (options.outWidth > widthLimit
							|| options.outHeight > heightLimit
							|| (os != null && os.size() > byteLimit)) {
						// The decoder does not support the inSampleSize option.
						// Scale the bitmap using Bitmap library.
						int scaledWidth = (int) (outWidth * scaleFactor);
						int scaledHeight = (int) (outHeight * scaleFactor);

						// if (Log.isLoggable(LogTag.APP, Log.VERBOSE)) {
						// Log.v(TAG,
						// "getResizedImageData: retry scaling using " +
						// "Bitmap.createScaledBitmap: w=" + scaledWidth +
						// ", h=" + scaledHeight);
						// }

						b = Bitmap.createScaledBitmap(b, scaledWidth,
								scaledHeight, false);
						if (b == null) {
							// if (Log.isLoggable(LogTag.APP, Log.VERBOSE)) {
							// Log.v(TAG,
							// "Bitmap.createScaledBitmap returned NULL!");
							// }
							return null;
						}
					}

					// Compress the image into a JPG. Start with
					// IMAGE_COMPRESSION_QUALITY.
					// In case that the image byte size is still too large
					// reduce the quality in
					// proportion to the desired byte size.
					os = new ByteArrayOutputStream();
					b.compress(CompressFormat.JPEG, quality, os);
					int jpgFileSize = os.size();
					if (jpgFileSize > byteLimit) {
						quality = (quality * byteLimit) / jpgFileSize; // watch
																		// for
																		// int
																		// division!
						if (quality < MINIMUM_IMAGE_COMPRESSION_QUALITY) {
							quality = MINIMUM_IMAGE_COMPRESSION_QUALITY;
						}

						// if (Log.isLoggable(LogTag.APP, Log.VERBOSE)) {
						// Log.v(TAG,
						// "getResizedImageData: compress(2) w/ quality=" +
						// quality);
						// }

						os = new ByteArrayOutputStream();
						b.compress(CompressFormat.JPEG, quality, os);
					}
				} catch (java.lang.OutOfMemoryError e) {
					// Log.w(TAG,
					// "getResizedImageData - image too big (OutOfMemoryError), will try "
					// + " with smaller scale factor, cur scale factor: " +
					// scaleFactor);
					// fall through and keep trying with a smaller scale factor.
				}
				// if (Log.isLoggable(LogTag.APP, Log.VERBOSE)) {
				// Log.v(TAG, "attempt=" + attempts
				// + " size=" + (os == null ? 0 : os.size())
				// + " width=" + outWidth * scaleFactor
				// + " height=" + outHeight * scaleFactor
				// + " scaleFactor=" + scaleFactor
				// + " quality=" + quality);
				// }
				scaleFactor *= .75F;
				attempts++;
				resultTooBig = os == null || os.size() > byteLimit;
			} while (resultTooBig && attempts < NUMBER_OF_RESIZE_ATTEMPTS);
			b.recycle(); // done with the bitmap, release the memory
			// if (Log.isLoggable(LogTag.APP, Log.VERBOSE) && resultTooBig) {
			// Log.v(TAG,
			// "getResizedImageData returning NULL because the result is too big: "
			// +
			// " requested max: " + byteLimit + " actual: " + os.size());
			// }

			return resultTooBig ? null : os.toByteArray();
		} catch (java.lang.OutOfMemoryError e) {
			// Log.e(TAG, e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Resize and recompress the image such that it fits the given limits. The
	 * resulting byte array contains an image in JPEG format, regardless of the
	 * original image's content type.
	 * 
	 * @param widthLimit
	 *            The width limit, in pixels
	 * @param heightLimit
	 *            The height limit, in pixels
	 * @param byteLimit
	 *            The binary size limit, in bytes
	 * @return A resized/recompressed version of this image, in JPEG format
	 */
	public static byte[] getResizedImageData(int width, int height,
			int widthLimit, int heightLimit, int byteLimit, Uri uri,
			Context context) {
		int outWidth = width;
		int outHeight = height;

		float scaleFactor = 1.F;
		while ((outWidth * scaleFactor > widthLimit)
				|| (outHeight * scaleFactor > heightLimit)) {
			scaleFactor *= .75F;
		}

		// if (Log.isLoggable(LogTag.APP, Log.VERBOSE)) {
		// Log.v(TAG, "getResizedBitmap: wlimit=" + widthLimit +
		// ", hlimit=" + heightLimit + ", sizeLimit=" + byteLimit +
		// ", width=" + width + ", height=" + height +
		// ", initialScaleFactor=" + scaleFactor +
		// ", uri=" + uri);
		// }

		InputStream input = null;
		try {
			ByteArrayOutputStream os = null;
			int attempts = 1;
			int sampleSize = 1;
			BitmapFactory.Options options = new BitmapFactory.Options();
			int quality = IMAGE_COMPRESSION_QUALITY;
			Bitmap b = null;

			// In this loop, attempt to decode the stream with the best possible
			// subsampling (we
			// start with 1, which means no subsampling - get the original
			// content) without running
			// out of memory.
			do {
				input = context.getContentResolver().openInputStream(uri);
				options.inSampleSize = sampleSize;
				try {
					b = BitmapFactory.decodeStream(input, null, options);
					if (b == null) {
						return null; // Couldn't decode and it wasn't because of
										// an exception,
										// bail.
					}
				} catch (OutOfMemoryError e) {
					// Log.w(TAG,
					// "getResizedBitmap: img too large to decode (OutOfMemoryError), "
					// +
					// "may try with larger sampleSize. Curr sampleSize=" +
					// sampleSize);
					sampleSize *= 2; // works best as a power of two
					attempts++;
					continue;
				} finally {
					if (input != null) {
						try {
							input.close();
						} catch (IOException e) {
							// Log.e(TAG, e.getMessage(), e);
						}
					}
				}
			} while (b == null && attempts < NUMBER_OF_RESIZE_ATTEMPTS);

			if (b == null) {
				// if (Log.isLoggable(LogTag.APP, Log.VERBOSE)
				// && attempts >= NUMBER_OF_RESIZE_ATTEMPTS) {
				// Log.v(TAG,
				// "getResizedImageData: gave up after too many attempts to resize");
				// }
				return null;
			}

			boolean resultTooBig = true;
			attempts = 1; // reset count for second loop
			// In this loop, we attempt to compress/resize the content to fit
			// the given dimension
			// and file-size limits.
			do {
				try {
					if (options.outWidth > widthLimit
							|| options.outHeight > heightLimit
							|| (os != null && os.size() > byteLimit)) {
						// The decoder does not support the inSampleSize option.
						// Scale the bitmap using Bitmap library.
						int scaledWidth = (int) (outWidth * scaleFactor);
						int scaledHeight = (int) (outHeight * scaleFactor);

						// if (Log.isLoggable(LogTag.APP, Log.VERBOSE)) {
						// Log.v(TAG,
						// "getResizedImageData: retry scaling using " +
						// "Bitmap.createScaledBitmap: w=" + scaledWidth +
						// ", h=" + scaledHeight);
						// }

						b = Bitmap.createScaledBitmap(b, scaledWidth,
								scaledHeight, false);
						// if (b == null) {
						// if (Log.isLoggable(LogTag.APP, Log.VERBOSE)) {
						// Log.v(TAG,
						// "Bitmap.createScaledBitmap returned NULL!");
						// }
						// return null;
						// }
					}

					// Compress the image into a JPG. Start with
					// IMAGE_COMPRESSION_QUALITY.
					// In case that the image byte size is still too large
					// reduce the quality in
					// proportion to the desired byte size.
					os = new ByteArrayOutputStream();
					b.compress(CompressFormat.JPEG, quality, os);
					int jpgFileSize = os.size();
					if (jpgFileSize > byteLimit) {
						quality = (quality * byteLimit) / jpgFileSize; // watch
																		// for
																		// int
																		// division!
						if (quality < MINIMUM_IMAGE_COMPRESSION_QUALITY) {
							quality = MINIMUM_IMAGE_COMPRESSION_QUALITY;
						}

						// if (Log.isLoggable(LogTag.APP, Log.VERBOSE)) {
						// Log.v(TAG,
						// "getResizedImageData: compress(2) w/ quality=" +
						// quality);
						// }

						os = new ByteArrayOutputStream();
						b.compress(CompressFormat.JPEG, quality, os);
					}
				} catch (java.lang.OutOfMemoryError e) {
					// Log.w(TAG,
					// "getResizedImageData - image too big (OutOfMemoryError), will try "
					// + " with smaller scale factor, cur scale factor: " +
					// scaleFactor);
					// fall through and keep trying with a smaller scale factor.
				}
				// if (Log.isLoggable(LogTag.APP, Log.VERBOSE)) {
				// Log.v(TAG, "attempt=" + attempts
				// + " size=" + (os == null ? 0 : os.size())
				// + " width=" + outWidth * scaleFactor
				// + " height=" + outHeight * scaleFactor
				// + " scaleFactor=" + scaleFactor
				// + " quality=" + quality);
				// }
				scaleFactor *= .75F;
				attempts++;
				resultTooBig = os == null || os.size() > byteLimit;
			} while (resultTooBig && attempts < NUMBER_OF_RESIZE_ATTEMPTS);
			b.recycle(); // done with the bitmap, release the memory
			// if (Log.isLoggable(LogTag.APP, Log.VERBOSE) && resultTooBig) {
			// Log.v(TAG,
			// "getResizedImageData returning NULL because the result is too big: "
			// +
			// " requested max: " + byteLimit + " actual: " + os.size());
			// }

			return resultTooBig ? null : os.toByteArray();
		} catch (FileNotFoundException e) {
			// Log.e(TAG, e.getMessage(), e);
			return null;
		} catch (java.lang.OutOfMemoryError e) {
			// Log.e(TAG, e.getMessage(), e);
			return null;
		}
	}

	public static int getBitmapOritation(String path) {
		int digree = 0;
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(path);
		} catch (IOException e) {
			e.printStackTrace();
			exif = null;
		}
		if (exif != null) { // 读取图片中相机方向信息
			int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_UNDEFINED); // 计算旋转角度
			switch (ori) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				digree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				digree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				digree = 270;
				break;
			default:
				digree = 0;
				break;
			}
		}
		return digree;
	}

	public static Bitmap ratoteBitmap(Bitmap bm, int digree) {
		if (digree != 0) { // 旋转图片
			Matrix m = new Matrix();
			m.postRotate(digree);
			Bitmap tmp = bm;
			bm = Bitmap.createBitmap(bm, 0, 0, tmp.getWidth(), tmp.getHeight(),
					m, true);
			tmp.recycle();
			return bm;
		}
		return bm;
	}

	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		if (bitmap == null) {
			return null;
		}
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(1, 1, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		// final Rect rectFrame = new Rect(0, 0, bitmap.getWidth() + 2,
		// bitmap.getHeight() + 2);
		// final RectF rectFrameF = new RectF(rectFrame);
		// final Paint paintFrame = new Paint();
		// paintFrame.setAntiAlias(true);
		// paintFrame.setColor(Color.BLACK);
		// paintFrame.setStrokeWidth(2);

		final float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(1, 1, 1, 1);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

//	static void setCornerRadii(GradientDrawable drawable, float r0, float r1,
//			float r2, float r3) {
//		drawable.setCornerRadii(new float[] { r0, r0, r1, r1, r2, r2, r3, r3 });
//	}
//
//	/**
//	 * 圆锟斤拷图片锟斤拷锟斤拷影锟斤拷锟解方锟斤拷实锟街对斤拷圆锟斤拷
//	 * 
//	 * @param bitmap
//	 * @param pixels
//	 * @return
//	 */
//	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
//		if (bitmap == null) {
//			return null;
//		}
//		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//		Bitmap output_shadow = Bitmap.createBitmap(bitmap.getWidth(),
//				bitmap.getHeight(), Config.ARGB_8888);
//		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
//				bitmap.getHeight(), Config.ARGB_8888);
//		Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
//		RectF rectF = new RectF(rect);
//		Canvas canvas = new Canvas(output);
//		// Canvas canvas_shadow = new Canvas(output_shadow);
//		// 锟斤拷锟斤拷透锟斤拷
//		canvas.drawColor(0x00000000);
//		GradientDrawable mDrawable = new GradientDrawable(
//				GradientDrawable.Orientation.TL_BR, new int[] { 0xFFFF0000,
//						0xFF00FF00, 0xFF0000FF });
//		mDrawable.setBounds(rect);
//		mDrawable.setShape(GradientDrawable.RECTANGLE);
//		mDrawable.setGradientRadius((float) (Math.sqrt(2) * 60));
//		mDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
//
//		// 锟斤拷锟斤拷锟斤拷锟矫成诧拷同圆锟斤拷
//		setCornerRadii(mDrawable, 0, pixels, 0, pixels);
//		// paint.setShadowLayer(5f, 5.0f, 5.0f,0xFF909090);
//		// canvas.saveLayer(rectF, paint, Canvas.ALL_SAVE_FLAG);
//		mDrawable.draw(canvas);
//		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
//		paint.setShadowLayer(0, 5.0f, 5.0f, 0xFF909090);
//		canvas.saveLayer(rectF, paint, Canvas.ALL_SAVE_FLAG);
//		BitmapDrawable imageDrawable = new BitmapDrawable(bitmap);
//		imageDrawable.setBounds(rect);
//		imageDrawable.draw(canvas);
//		canvas.restore();
//		// 锟結锟斤拷铣锟酵�
//
//		output_shadow = drawImageDropShadow(output);
//		output.recycle();
//		// Paint shadowPaint = new Paint();
//		// // canvas_shadow.drawColor(0xFF000000);
//		// shadowPaint.setShadowLayer(10.0f, 0.0f, 2.0f, 0xFF000000);
//		// canvas_shadow.drawBitmap(output,0,0, shadowPaint);
//
//		return output_shadow;
//	}

//	/**
//	 * 为图片锟斤拷锟斤拷锟接靶э拷锟�
//	 * 
//	 * @param originalBitmap
//	 *            锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷茫锟斤拷锟斤拷锟絩ecycle锟斤拷
//	 * @return
//	 */
//	private static Bitmap drawImageDropShadow(Bitmap originalBitmap) {
//		float radius = 8f;
//		float readiusHalf = radius / 2;
//		// EmbossMaskFilter filter = new EmbossMaskFilter(new float[]{ 0, 0, 1
//		// }, 0.1f, 20, 20.0f);
//		BlurMaskFilter filter = new BlurMaskFilter(radius,
//				BlurMaskFilter.Blur.NORMAL);
//		Paint shadowPaint = new Paint();
//		shadowPaint.setAlpha(10);
//		shadowPaint.setStyle(Paint.Style.FILL);
//		shadowPaint.setColor(Color.WHITE);
//		// shadowPaint.setAntiAlias(true);
//		shadowPaint.setMaskFilter(filter);
//		int[] offsetXY = new int[2];
//		Bitmap shadowBitmap = originalBitmap
//				.extractAlpha(shadowPaint, offsetXY);
//		Bitmap shadowBitmap32 = shadowBitmap
//				.copy(Bitmap.Config.ARGB_8888, true);
//		Canvas c = new Canvas(shadowBitmap32);
////		Log.d("flydy", " px:" + offsetXY[0] + " py:" + offsetXY[1]);
//		c.drawBitmap(originalBitmap, 0, 0, null);
//		shadowBitmap.recycle();
//		return shadowBitmap32;
//	}
	
	public static byte[] makeThumbnail(ByteArrayOutputStream bytes){
		if(bytes == null){
			return new byte[0];
		}
		return makeThumbnail(bytes.toByteArray());
	}
	
	public static byte[] makeThumbnail(byte[] data){
		if(data == null){
			return new byte[0];
		}
		Options op = new Options();
		op.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, op);
		byte[] thumbnailData = ImageUtils.getResizedImageData(data, op.outWidth, op.outHeight, op.outWidth, op.outHeight, 3800);
		if(thumbnailData == null){
			thumbnailData = new byte[0];
		}
		return thumbnailData;
	}
}
