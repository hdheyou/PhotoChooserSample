package com.tianma.photochooser.image;

import java.io.FileDescriptor;
import java.io.FileInputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.CursorLoader;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

/**
 * 与图片操作相关的工具类
 */
public class ImageUtils {

    /**
     * 将资源路径下的图片资源压缩后返回对应的Bitmap对象
     *
     * @param res
     * @param resId
     * @param requiredWidth
     * @param requiredHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res,
                                                         int resId, int requiredWidth, int requiredHeight) {
        // 第一次解析将inJustDecodeBounds置为true用以获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // 调用calcuteInSampleSize计算inSampleSize的值
        options.inSampleSize = calculateInSampleSize(options, requiredWidth,
                requiredHeight);
        // 将inJustDecodeBounds置为false,再次解析
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * 将磁盘上的FileDescriptor代表的图片文件压缩后返回对应的Bitmap对象
     *
     * @param descriptor
     * @param requiredWidth
     * @param requiredHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromDisk(FileDescriptor descriptor,
                                                     int requiredWidth, int requiredHeight) {
        // 第一次解析将inJustDecodeBounds置为true用以获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Rect outPadding = new Rect(0, 0, requiredWidth, requiredHeight);
        outPadding = null;
        BitmapFactory.decodeFileDescriptor(descriptor, outPadding, options);
        // 调用calcuteInSampleSize计算inSampleSize的值
        options.inSampleSize = calculateInSampleSize(options, requiredWidth,
                requiredHeight);
        // 将inJustDecodeBounds置为false,再次解析
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(descriptor, outPadding,
                options);
    }

    public static Bitmap decodeSampledBitmap(FileInputStream is,
                                             int requiredWidth, int requiredHeight) {
        // 第一次解析将inJustDecodeBounds置为true用以获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // Rect outPadding = new Rect(0, 0, requiredWidth, requiredHeight);
        // outPadding = null;
        BitmapFactory.decodeStream(is, null, options);
        // 调用calcuteInSampleSize计算inSampleSize的值
        options.inSampleSize = calculateInSampleSize(options, requiredWidth,
                requiredHeight);
        // 将inJustDecodeBounds置为false,再次解析
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(is, null, options);
    }

    /**
     * 将磁盘上的imagePath路径下的图片文件压缩后返回对应的Bitmap对象
     *
     * @param imagePath
     * @param requiredWidth
     * @param requiredHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromDisk(String imagePath,
                                                     int requiredWidth, int requiredHeight) {
        // 第一次解析将inJustDecodeBounds置为true用以获取图片大小
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        // 调用calcuteInSampleSize计算inSampleSize的值
        options.inSampleSize = calculateInSampleSize(options, requiredWidth,
                requiredHeight);
        // 将inJustDecodeBounds置为false,再次解析
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath, options);
    }

    /**
     * 根据传入的BitmapFactory.Options对象和图片需要的宽和高计算出压缩比例
     *
     * @param options
     *            BitmapFactory.Options对象
     * @param requiredWidth
     *            需要的图片的宽度
     * @param requiredHeight
     *            需要的图片的高度
     * @return
     */
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int requiredWidth, int requiredHeight) {
        // 根据BitmapFactory.Options对象获取原图片的高度和宽度
        final int width = options.outWidth;
        final int height = options.outHeight;
        int inSampleSize = 1;
        if (width > requiredWidth || height > requiredHeight) {
            // 计算出实际的宽高比率
            final int widthRatio = Math.round((float) width
                    / (float) requiredWidth);
            final int heightRatio = Math.round((float) height
                    / (float) requiredHeight);
            // 选择宽高比率最小的作为inSampleSize的值,保证最后图片的宽和高大于等于需要的宽和高
            inSampleSize = widthRatio < heightRatio ? widthRatio : heightRatio;
        }
        return inSampleSize;
    }

    /**
     * 根据图片的Uri获取图片的绝对路径(已经适配多种API)
     *
     * @param context
     *            上下文对象
     * @param uri
     *            图片的Uri
     * @return 如果Uri对应的图片存在,那么返回该图片的绝对路径,否则返回null
     */
    public static String getRealPathFromUri(Context context, Uri uri) {
        Log.d("Tianma", "Uri = " + uri);
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion < 11) {
            // SDK < Api11
            return getRealPathFromUri_BelowApi11(context, uri);
        }
        if (sdkVersion < 19) {
            // SDK > 11 && SDK < 19
            return getRealPathFromUri_Api11To18(context, uri);
        }
        // SDK > 19
        return getRealPathFromUri_AboveApi19(context, uri);
    }

    /**
     * 适配api19以上,根据uri获取图片的绝对路径
     *
     * @param context
     *            上下文对象
     * @param uri
     *            图片的Uri
     * @return 如果Uri对应的图片存在,那么返回该图片的绝对路径,否则返回null
     */
    @SuppressLint("NewApi")
    private static String getRealPathFromUri_AboveApi19(Context context, Uri uri) {
        String filePath = null;
        String wholeID = DocumentsContract.getDocumentId(uri);

        Log.d("Tianma", "wholeId = " + wholeID);
        // 使用':'分割
        String id = wholeID.split(":")[1];

        String[] projection = { MediaStore.Images.Media.DATA };
        String selection = MediaStore.Images.Media._ID + "=?";
        String[] selectionArgs = { id };

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                selection, selectionArgs, null);
        int columnIndex = cursor.getColumnIndex(projection[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    /**
     * 适配api11-api18,根据uri获取图片的绝对路径
     *
     * @param context
     *            上下文对象
     * @param uri
     *            图片的Uri
     * @return 如果Uri对应的图片存在,那么返回该图片的绝对路径,否则返回null
     */
    private static String getRealPathFromUri_Api11To18(Context context, Uri uri) {
        String filePath = null;
        String[] projection = { MediaStore.Images.Media.DATA };

        CursorLoader loader = new CursorLoader(context, uri, projection, null,
                null, null);
        Cursor cursor = loader.loadInBackground();

        if (cursor != null) {
            cursor.moveToFirst();
            filePath = cursor.getString(cursor.getColumnIndex(projection[0]));
            cursor.close();
        }

        return filePath;

    }

    /**
     * 适配api11以下(不包括api11),根据uri获取图片的绝对路径
     *
     * @param context
     *            上下文对象
     * @param uri
     *            图片的Uri
     * @return 如果Uri对应的图片存在,那么返回该图片的绝对路径,否则返回null
     */
    private static String getRealPathFromUri_BelowApi11(Context context, Uri uri) {
        String filePath = null;
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, projection,
                null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            filePath = cursor.getString(cursor.getColumnIndex(projection[0]));
            cursor.close();
        }
        return filePath;
    }

}