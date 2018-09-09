package com.b2b.mytask.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.b2b.mytask.R;

import java.util.HashMap;


public class CustomTextView extends AppCompatTextView {

    private static HashMap<String, Typeface> fontCache = new HashMap<>();
    public Paint paint;
    public boolean addStrike = false;

    public CustomTextView(Context context) {
        super(context);
        applyCustomFont(null);
        init(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(attrs);
        init(context);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(attrs);
        init(context);
    }

    private void applyCustomFont(AttributeSet attrs) {
        if (attrs != null) {

            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomTextView);

            String fontName = a.getString(R.styleable.CustomTextView_fontName);
            try {
                if (fontName != null) {
                    Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + fontName);
                    setTypeface(myTypeface);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            a.recycle();
        }

    }

    private void init(Context context) {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(getResources().getDisplayMetrics().density * 1);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        if (addStrike) {
            canvas.drawLine(0, getHeight() / 2, getWidth(),
                    getHeight() / 2, paint);
        }
    }

}
