//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.anubis.module_extends;

import android.content.Context;
import android.content.Intent;
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
import android.graphics.YuvImage;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build.VERSION;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BitmapUtil {
    private static final String TAG = BitmapUtil.class.getSimpleName();

    public BitmapUtil() {
    }

    public static byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream bos = null;
        byte[] bitmapByte = null;

        try {
            if (null != bitmap) {
                bos = new ByteArrayOutputStream();
                bitmap.compress(CompressFormat.JPEG, 90, bos);
                bos.flush();
                bos.close();
                bitmapByte = bos.toByteArray();
            }
        } catch (Exception var12) {
            var12.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException var11) {
                    var11.printStackTrace();
                }
            }

        }

        return bitmapByte;
    }

    public static int[] getBGRAImageByte(Bitmap image) {
        int width = image.getWidth();
        int height = image.getHeight();
        if (image.getConfig().equals(Config.ARGB_8888)) {
            int[] imgData = new int[width * height];
            image.getPixels(imgData, 0, width, 0, 0, width, height);
            return imgData;
        } else {
            return null;
        }
    }

    public static Bitmap getBitmapFromByte(byte[] b) {
        return b != null && b.length != 0 ? BitmapFactory.decodeByteArray(b, 0, b.length) : null;
    }

    public static Bitmap getBitmapFromYuvByte(byte[] yuv, int iw, int ih) {
        return getBitmapFromYuvByte(yuv, iw, ih, 17);
    }

    public static Bitmap getBitmapFromYuvByte(byte[] yuv, int iw, int ih, int imageformat) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        YuvImage yuvImage = new YuvImage(yuv, imageformat, iw, ih, (int[]) null);
        yuvImage.compressToJpeg(new Rect(0, 0, iw, ih), 80, out);
        return getBitmapFromByte(out.toByteArray());
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        return Base64.encodeToString(bitmapToByte(bitmap), 0);
    }

    public static Bitmap base64ToBitmap(String base64String) {
        byte[] bytes = Base64.decode(base64String, 0);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        return drawable == null ? null : ((BitmapDrawable) drawable).getBitmap();
    }

    public static Drawable bitmapToDrawable(Bitmap bitmap) {
        return bitmap == null ? null : new BitmapDrawable(bitmap);
    }

    public static Bitmap scaleImageTo(Bitmap org, int newWidth, int newHeight) {
        return scaleImage(org, (float) newWidth / (float) org.getWidth(), (float) newHeight / (float) org.getHeight());
    }

    public static Bitmap scaleImage(Bitmap org, float scaleWidth, float scaleHeight) {
        if (org == null) {
            return null;
        } else {
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            return Bitmap.createBitmap(org, 0, 0, org.getWidth(), org.getHeight(), matrix, true);
        }
    }

    public static Bitmap toRoundCorner(Bitmap bitmap) {
        int height = bitmap.getHeight();
        int width = bitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, width, height);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(0);
        canvas.drawCircle((float) (width / 2), (float) (height / 2), (float) (width / 2), paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static Bitmap createBitmapThumbnail(Bitmap bitMap, boolean needRecycle, int newHeight, int newWidth) {
        int width = bitMap.getWidth();
        int height = bitMap.getHeight();
        float scaleWidth = (float) newWidth / (float) width;
        float scaleHeight = (float) newHeight / (float) height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBitMap = Bitmap.createBitmap(bitMap, 0, 0, width, height, matrix, true);
        if (needRecycle) {
            bitMap.recycle();
        }

        return newBitMap;
    }

    public static boolean saveBitmap(Bitmap bitmap, File file) {
        if (bitmap == null) {
            return false;
        } else {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                bitmap.compress(CompressFormat.PNG, 100, fos);
                fos.flush();
                boolean var3 = true;
                return var3;
            } catch (Exception var13) {
                var13.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException var12) {
                        var12.printStackTrace();
                    }
                }

            }

            return false;
        }
    }

    public static boolean saveBitmap(Bitmap bitmap, String absPath) {
        return saveBitmap(bitmap, new File(absPath));
    }

    public static Intent buildImageGetIntent(Uri saveTo, int outputX, int outputY, boolean returnData) {
        return buildImageGetIntent(saveTo, 1, 1, outputX, outputY, returnData);
    }

    public static Intent buildImageGetIntent(Uri saveTo, int aspectX, int aspectY, int outputX, int outputY, boolean returnData) {
        Log.i(TAG, "Build.VERSION.SDK_INT : " + VERSION.SDK_INT);
        Intent intent = new Intent();
        if (VERSION.SDK_INT < 19) {
            intent.setAction("android.intent.action.GET_CONTENT");
        } else {
            intent.setAction("android.intent.action.OPEN_DOCUMENT");
            intent.addCategory("android.intent.category.OPENABLE");
        }

        intent.setType("image/*");
        intent.putExtra("output", saveTo);
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", returnData);
        intent.putExtra("outputFormat", CompressFormat.PNG.toString());
        return intent;
    }

    public static Intent buildImageCropIntent(Uri uriFrom, Uri uriTo, int outputX, int outputY, boolean returnData) {
        return buildImageCropIntent(uriFrom, uriTo, 1, 1, outputX, outputY, returnData);
    }

    public static Intent buildImageCropIntent(Uri uriFrom, Uri uriTo, int aspectX, int aspectY, int outputX, int outputY, boolean returnData) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uriFrom, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("output", uriTo);
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", returnData);
        intent.putExtra("outputFormat", CompressFormat.PNG.toString());
        return intent;
    }

    public static Intent buildImageCaptureIntent(Uri uri) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra("output", uri);
        return intent;
    }

    public static Bitmap getSmallBitmap(String filePath, int reqWidth, int reqHeight) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public byte[] compressBitmapToBytes(String filePath, int reqWidth, int reqHeight, int quality) {
        Bitmap bitmap = getSmallBitmap(filePath, reqWidth, reqHeight);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, quality, baos);
        byte[] bytes = baos.toByteArray();
        bitmap.recycle();
        Log.i(TAG, "Bitmap compressed success, size: " + bytes.length);
        return bytes;
    }

    public byte[] compressBitmapSmallTo(String filePath, int reqWidth, int reqHeight, int maxLenth) {
        int quality = 100;

        byte[] bytes;
        for (bytes = this.compressBitmapToBytes(filePath, reqWidth, reqHeight, quality); bytes.length > maxLenth && quality > 0; bytes = this.compressBitmapToBytes(filePath, reqWidth, reqHeight, quality)) {
            quality /= 2;
        }

        return bytes;
    }

    public byte[] compressBitmapQuikly(String filePath) {
        return this.compressBitmapToBytes(filePath, 480, 800, 50);
    }

    public byte[] compressBitmapQuiklySmallTo(String filePath, int maxLenth) {
        return this.compressBitmapSmallTo(filePath, 480, 800, maxLenth);
    }

    public static int calculateInSampleSize(Options options, int destWidth, int destHeight) {
        int h = options.outHeight;
        int w = options.outWidth;
        int scaleWidth = (int) Math.ceil((double) ((float) w / (float) destWidth));
        int scaleHeight = (int) Math.ceil((double) ((float) h / (float) destHeight));
        return Math.max(scaleWidth, scaleHeight);
    }

    public static Bitmap rotaingImageView(int paramInt, Bitmap paramBitmap) {
        Matrix localMatrix = new Matrix();
        localMatrix.postRotate((float) paramInt);
        return Bitmap.createBitmap(paramBitmap, 0, 0, paramBitmap.getWidth(), paramBitmap.getHeight(), localMatrix, true);
    }

    public static Bitmap decodeBitmapFromFile(String imagePath) {
        Bitmap localBitmap1 = null;
        File f = new File(imagePath);
        Options options = new Options();
        options.inPreferredConfig = Config.ARGB_8888;

        try {
            localBitmap1 = BitmapFactory.decodeStream(new FileInputStream(f), (Rect) null, options);
            return localBitmap1;
        } catch (FileNotFoundException var5) {
            var5.printStackTrace();
            return localBitmap1;
        }
    }

    public static Bitmap localBitmap1 = null;
    public static Bitmap localBitmap2 = null;

    public static Bitmap decodeScaleImage(String imagePath, int outWidth, int outHeight) {
        Options localOptions = new Options();
        localOptions.inJustDecodeBounds = true;
        localOptions.inPreferredConfig = Config.ARGB_8888;
        BitmapFactory.decodeFile(imagePath, localOptions);
        int i = calculateInSampleSize(localOptions, outWidth, outHeight);
        localOptions.inSampleSize = i;
        localOptions.inJustDecodeBounds = false;
        localBitmap1 = BitmapFactory.decodeFile(imagePath, localOptions);
        int j = getPictureDegree(imagePath);
        if (localBitmap1 != null && j != 0) {
            localBitmap2 = rotaingImageView(j, localBitmap1);
            localBitmap1.recycle();
            localOptions.inPreferredConfig = Config.RGB_565;
            localOptions.inDither = true;
            return localBitmap2;
        } else {
            return localBitmap1;
        }
    }

    public static int getPictureDegree(String imagePath) {
        short i = 0;

        try {
            ExifInterface localExifInterface = new ExifInterface(imagePath);
            int j = localExifInterface.getAttributeInt("Orientation", 1);
            switch (j) {
                case 3:
                    i = 180;
                case 4:
                case 5:
                case 7:
                default:
                    break;
                case 6:
                    i = 90;
                    break;
                case 8:
                    i = 270;
            }
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        return i;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
        if (bitmap == null) {
            return null;
        } else {
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            int color = -12434878;
            Paint paint = new Paint();
            Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            RectF rectF = new RectF(rect);
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(-12434878);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            return output;
        }
    }

    public static Bitmap decodeUriAsBitmap(Context mContext, Uri uri) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(uri));
            return bitmap;
        } catch (FileNotFoundException var4) {
            return null;
        }
    }

    public static boolean bitmap2File(Bitmap bitmap, File imageFile) {
        try {
            OutputStream os = new FileOutputStream(imageFile);
            boolean isOK = bitmap.compress(CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
            return isOK;
        } catch (Exception var4) {
            var4.printStackTrace();
            return false;
        }
    }

    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.JPEG, 100, baos);
        int options = 100;

        while (baos.toByteArray().length / 1024 > 100) {
            options -= 10;
            if (options > 0) {
                baos.reset();
                image.compress(CompressFormat.JPEG, options, baos);
            }
        }

        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        return BitmapFactory.decodeStream(isBm, (Rect) null, (Options) null);
    }

    public static Bitmap compressFixBitmap(Bitmap bitMap, int outWidth, int outHeight) {
        int width = bitMap.getWidth();
        int height = bitMap.getHeight();
        float scaleWidth = (float) outWidth / (float) width;
        float scaleHeight = (float) outHeight / (float) height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitMap, 0, 0, width, height, matrix, true);
    }

    public static Bitmap createCircleImage(Bitmap source, int min) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(min, min, Config.ARGB_8888);
        Canvas canvas = new Canvas(target);
        canvas.drawCircle((float) (min / 2), (float) (min / 2), (float) (min / 2), paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(source, 0.0F, 0.0F, paint);
        return target;
    }

    public static Bitmap getBitmapFromView(View view) {
        Bitmap bmp = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        view.draw(canvas);
        return bmp;
    }

    private static Bitmap getCacheBitmapFromView(View view) {
        boolean drawingCacheEnabled = true;
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        Bitmap drawingCache = view.getDrawingCache();
        Bitmap bitmap;
        if (drawingCache != null) {
            bitmap = Bitmap.createBitmap(drawingCache);
            view.setDrawingCacheEnabled(false);
        } else {
            bitmap = null;
        }

        return bitmap;
    }

    /**
     * 旋转图片
     *
     * @param bm
     * @param orientationDegree
     * @return
     */
    private Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {
        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        float targetX, targetY;
        if (orientationDegree == 90) {
            targetX = bm.getHeight();
            targetY = 0;
        } else {
            targetX = bm.getHeight();
            targetY = bm.getWidth();
        }
        final float[] values = new float[9];
        m.getValues(values);
        float x1 = values[Matrix.MTRANS_X];
        float y1 = values[Matrix.MTRANS_Y];
        m.postTranslate(targetX - x1, targetY - y1);
        Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(), Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bm1);
        canvas.drawBitmap(bm, m, paint);
        return bm1;
    }

}
