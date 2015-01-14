package com.open_demo.util;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

public class UriImage {
    private static final String TAG = "uri/image";
    private static final boolean LOCAL_LOGV = false;

    private final Context mContext;
    private final Uri mUri;
    private String mContentType;
    private String mPath;
    private String mSrc;
    private int mWidth;
    private int mHeight;

    public UriImage(Context context, Uri uri) {
        if ((null == context) || (null == uri)) {
            throw new IllegalArgumentException();
        }

        String scheme = uri.getScheme();
        if (scheme.equals("content")) {
            initFromContentUri(context, uri);
        } else if (uri.getScheme().equals("file")) {
            initFromFile(context, uri);
        }

        mContext = context;
        mUri = uri;

        decodeBoundsInfo();

        if (LOCAL_LOGV) {
            Log.v(TAG, "UriImage uri: " + uri + " mPath: " + mPath + " mWidth: " + mWidth +
                    " mHeight: " + mHeight);
        }
    }

    private void initFromFile(Context context, Uri uri) {
        mPath = uri.getPath();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String extension = MimeTypeMap.getFileExtensionFromUrl(mPath);
        if (TextUtils.isEmpty(extension)) {
            // getMimeTypeFromExtension() doesn't handle spaces in filenames nor can it handle
            // urlEncoded strings. Let's try one last time at finding the extension.
            int dotPos = mPath.lastIndexOf('.');
            if (0 <= dotPos) {
                extension = mPath.substring(dotPos + 1);
            }
        }
        mContentType = mimeTypeMap.getMimeTypeFromExtension(extension);
        // It's ok if mContentType is null. Eventually we'll show a toast telling the
        // user the picture couldn't be attached.

        buildSrcFromPath();
    }

    private void buildSrcFromPath() {
        mSrc = mPath.substring(mPath.lastIndexOf('/') + 1);

        if(mSrc.startsWith(".") && mSrc.length() > 1) {
            mSrc = mSrc.substring(1);
        }

        // Some MMSCs appear to have problems with filenames
        // containing a space.  So just replace them with
        // underscores in the name, which is typically not
        // visible to the user anyway.
        mSrc = mSrc.replace(' ', '_');
    }


    private void decodeBoundsInfo() {
        InputStream input = null;
        try {
            input = mContext.getContentResolver().openInputStream(mUri);
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, opt);
            mWidth = opt.outWidth;
            mHeight = opt.outHeight;
        } catch (FileNotFoundException e) {
            // Ignore
            Log.e(TAG, "IOException caught while opening stream", e);
        } finally {
            if (null != input) {
                try {
                    input.close();
                } catch (IOException e) {
                    // Ignore
                    Log.e(TAG, "IOException caught while closing stream", e);
                }
            }
        }
    }

    public String getContentType() {
        return mContentType;
    }

    public String getSrc() {
        return mSrc;
    }

    public String getPath() {
        return mPath;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    /**
     * Get a version of this image resized to fit the given dimension and byte-size limits. Note
     * that the content type of the resulting PduPart may not be the same as the content type of
     * this UriImage; always call {@link PduPart#getContentType()} to get the new content type.
     *
     * @param widthLimit The width limit, in pixels
     * @param heightLimit The height limit, in pixels
     * @param byteLimit The binary size limit, in bytes
     * @return A new PduPart containing the resized image data
     */
    public byte[] getResizedImage(int widthLimit, int heightLimit, int byteLimit) {
        byte[] data =  ImageUtils.getResizedImageData(mWidth, mHeight,
                widthLimit, heightLimit, byteLimit, mUri, mContext);
        if (data == null) {
            if (LOCAL_LOGV) {
                Log.v(TAG, "Resize image failed.");
            }
            return null;
        }

        return data;
    }

    private void initFromContentUri(Context context, Uri uri) {
        ContentResolver resolver = context.getContentResolver();
        Cursor c = MediaStore.Images.Media.query(resolver, uri, null);

        mSrc = null;
        if (c == null) {
            throw new IllegalArgumentException(
                    "Query on " + uri + " returns null result.");
        }

        try {
            if ((c.getCount() != 1) || !c.moveToFirst()) {
                throw new IllegalArgumentException(
                        "Query on " + uri + " returns 0 or multiple rows.");
            }

            String filePath;
            filePath = uri.getPath();
            try {
                mContentType = c.getString(
                        c.getColumnIndexOrThrow(Images.Media.MIME_TYPE)); // mime_type
            } catch (IllegalArgumentException e) {
                try {
                    mContentType = c.getString(c.getColumnIndexOrThrow("mimetype"));
                } catch (IllegalArgumentException ex) {
                    mContentType = resolver.getType(uri);
                    Log.v(TAG, "initFromContentUri: " + uri + ", getType => " + mContentType);
                }
            }

            // use the original filename if possible
            int nameIndex = c.getColumnIndex(Images.Media.DISPLAY_NAME);
            if (nameIndex != -1) {
                mSrc = c.getString(nameIndex);
                if (!TextUtils.isEmpty(mSrc)) {
                    // Some MMSCs appear to have problems with filenames
                    // containing a space.  So just replace them with
                    // underscores in the name, which is typically not
                    // visible to the user anyway.
                    mSrc = mSrc.replace(' ', '_');
                } else {
                    mSrc = null;
                }
            }
            mPath = filePath;
            if (mSrc == null) {
                buildSrcFromPath();
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "initFromContentUri couldn't load image uri: " + uri, e);
        } finally {
            c.close();
        }
    }
}
