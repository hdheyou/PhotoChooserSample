package com.tianma.photochooser.image;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.FileDescriptor;
import java.io.FileInputStream;

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
     * @param options        BitmapFactory.Options对象
     * @param requiredWidth  需要的图片的宽度
     * @param requiredHeight 需要的图片的高度
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

//    /**
//     * 根据图片的Uri获取图片的绝对路径(已经适配多种API)
//     *
//     * @param context
//     *            上下文对象
//     * @param uri
//     *            图片的Uri
//     * @return 如果Uri对应的图片存在,那么返回该图片的绝对路径,否则返回null
//     */
//    public static String getRealPathFromUri(Context context, Uri uri) {
//        Log.d("Tianma", "Uri = " + uri);
//        int sdkVersion = Build.VERSION.SDK_INT;
//        if (sdkVersion < 11) {
//            // SDK < Api11
//            return getRealPathFromUri_BelowApi11(context, uri);
//        }
//        if (sdkVersion < 19) {
//            // SDK >= 11 && SDK < 19
//            return getRealPathFromUri_Api11To18(context, uri);
//        }
//        // SDK >= 19
//        return getRealPathFromUri_AboveApi19(context, uri);
//    }
//
//    /**
//     * 适配api19以上,根据uri获取图片的绝对路径
//     *
//     * @param context
//     *            上下文对象
//     * @param uri
//     *            图片的Uri
//     * @return 如果Uri对应的图片存在,那么返回该图片的绝对路径,否则返回null
//     */
//    @SuppressLint("NewApi")
//    private static String getRealPathFromUri_AboveApi19(Context context, Uri uri) {
//        String filePath = null;
//        String wholeID = DocumentsContract.getDocumentId(uri);
//
//        Log.d("Tianma", "wholeId = " + wholeID);
//        // 使用':'分割
//        String id = wholeID.split(":")[1];
//
//        String[] projection = { MediaStore.Images.Media.DATA };
//        String selection = MediaStore.Images.Media._ID + "=?";
//        String[] selectionArgs = { id };
//
//        Cursor cursor = context.getContentResolver().query(
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
//                selection, selectionArgs, null);
//        int columnIndex = cursor.getColumnIndex(projection[0]);
//
//        if (cursor.moveToFirst()) {
//            filePath = cursor.getString(columnIndex);
//        }
//        cursor.close();
//        return filePath;
//    }
//
//    /**
//     * 适配api11-api18,根据uri获取图片的绝对路径
//     *
//     * @param context
//     *            上下文对象
//     * @param uri
//     *            图片的Uri
//     * @return 如果Uri对应的图片存在,那么返回该图片的绝对路径,否则返回null
//     */
//    private static String getRealPathFromUri_Api11To18(Context context, Uri uri) {
//        String filePath = null;
//        String[] projection = { MediaStore.Images.Media.DATA };
//
//        CursorLoader loader = new CursorLoader(context, uri, projection, null,
//                null, null);
//        Cursor cursor = loader.loadInBackground();
//
//        if (cursor != null) {
//            cursor.moveToFirst();
//            filePath = cursor.getString(cursor.getColumnIndex(projection[0]));
//            cursor.close();
//        }
//
//        return filePath;
//
//    }
//
//    /**
//     * 适配api11以下(不包括api11),根据uri获取图片的绝对路径
//     *
//     * @param context
//     *            上下文对象
//     * @param uri
//     *            图片的Uri
//     * @return 如果Uri对应的图片存在,那么返回该图片的绝对路径,否则返回null
//     */
//    private static String getRealPathFromUri_BelowApi11(Context context, Uri uri) {
//        String filePath = null;
//        String[] projection = { MediaStore.Images.Media.DATA };
//        Cursor cursor = context.getContentResolver().query(uri, projection,
//                null, null, null);
//        if (cursor != null) {
//            cursor.moveToFirst();
//            filePath = cursor.getString(cursor.getColumnIndex(projection[0]));
//            cursor.close();
//        }
//        return filePath;
//    }

    /**
     * 适配api19以上,根据uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    public static String getRealPathFromUri(Context context, Uri uri) {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion < 19) { // < 19
            return getRealPathFromUriBelowAPI19(context, uri);
        } else { // >= 19
            return getRealPathFromUriAboveAPI19(context, uri);
        }
    }

    // < 19
    private static String getRealPathFromUriBelowAPI19(Context context, Uri uri) {
        return getDataColumn(context, uri, null, null);
    }

    // >= 19
    @TargetApi(19)
    private static String getRealPathFromUriAboveAPI19(Context context, Uri uri) {
        String imagePath = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // document 类型的 Ui, 需要通过 document id 处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出 id
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{id};
                imagePath = getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getDataColumn(context, contentUri, null, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // content 类型的 Uri，使用普通方式处理
            imagePath = getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // file 类型的 Uri，直接获取图片路径
            imagePath = uri.getPath();
        }
        return imagePath;
    }

    /**
     * 获取 _data 列对应的内容, 其实就是 path
     * @return
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String dataColumn = MediaStore.Images.Media.DATA;
        String[] projection = new String[]{dataColumn};
        Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
        String path = null;
        if (cursor != null && cursor.moveToFirst()) {
            path = cursor.getString(cursor.getColumnIndex(dataColumn));
            cursor.close();
        }
        return path;
    }

}