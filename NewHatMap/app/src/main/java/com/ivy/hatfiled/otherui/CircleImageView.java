package com.ivy.hatfiled.otherui;
/*�Զ����Բ��ImageView������ֱ�ӵ�����ڲ�����ʹ�á�*/
import android.widget.ImageView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

public class CircleImageView extends ImageView {
    private Paint paint ;
    public CircleImageView(Context context) {
        this(context,null);
    }
    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        paint = new Paint();
    }

     /** * ����Բ��ͼƬ */
     @Override
     protected void onDraw(Canvas canvas) {
         Drawable drawable = getDrawable();
         if (null != drawable) {
             Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
             Bitmap b = getCircleBitmap(bitmap, 14);
             final Rect rectSrc = new Rect(0, 0, b.getWidth(), b.getHeight());
             final Rect rectDest = new Rect(0,0,getWidth(),getHeight());
             paint.reset();
             canvas.drawBitmap(b, rectSrc, rectDest, paint);
         } else {
             super.onDraw(canvas);
         }
     }
    /** * ��ȡԲ��ͼƬ����
     * * @param bitmap
     * * @param pixels
     * * @return Bitmap
     * * @author caizhiming */
    private Bitmap getCircleBitmap(Bitmap bitmap, int pixels) {
        int x = bitmap.getWidth();
        int y = bitmap.getHeight();
        int r = Math.min(x, y);
        Bitmap output = Bitmap.createBitmap(r,r, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Rect rect = new Rect(0, 0, r, r);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r/ 2, r / 2, r / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
}

