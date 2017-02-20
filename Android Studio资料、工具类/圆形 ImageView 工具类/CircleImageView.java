package com.spro.dailynews.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by macbook on 2016/11/11.
 */

public class CircleImageView extends ImageView {

    private Paint paint;

    public CircleImageView(Context context) {
        this(context, null);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        paint = new Paint();

    }

    /**
     * 绘制圆形图片
     */
    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable();
        if (null != drawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap == null) {
                return;
            }
            Bitmap b = getCircleBitmap(bitmap, 14);

            final Rect rectSrc, rectDest;
            if (b.getWidth() < b.getHeight()) {
                rectSrc = new Rect(0, 0, b.getWidth(), b.getWidth());
                rectDest = new Rect(0, 0, getWidth(), getWidth());
            } else {
                rectSrc = new Rect(0, 0, b.getHeight(), b.getHeight());
                rectDest = new Rect(0, 0, getHeight(), getHeight());
            }

            paint.reset();
            canvas.drawBitmap(b, rectSrc, rectDest, paint);

        } else {
            super.onDraw(canvas);
        }
    }

    /**
     * 获取圆形图片方法
     */
    private Bitmap getCircleBitmap(Bitmap bitmap, int pixels) {

        Bitmap output;
        if (bitmap.getWidth() < bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;

        Rect rect;
        int x;
        if (bitmap.getWidth() < bitmap.getHeight()) {
            rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getWidth());
            x = bitmap.getWidth();
        } else {
            rect = new Rect(0, 0, bitmap.getHeight(), bitmap.getHeight());
            x = bitmap.getHeight();
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);


        canvas.drawCircle(x / 2, x / 2, x / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
}