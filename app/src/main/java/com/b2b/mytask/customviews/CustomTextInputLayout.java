package com.b2b.mytask.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;


import com.b2b.mytask.R;

import java.util.HashMap;


public class CustomTextInputLayout extends TextInputLayout {

    private static HashMap<String, Typeface> fontCache = new HashMap<>();

    public CustomTextInputLayout(Context context) {
        super(context);
        applyCustomFont(null);
    }

    public CustomTextInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(attrs);
    }

    public CustomTextInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(attrs);
    }

    private void applyCustomFont(AttributeSet attrs) {
        if (attrs != null) {

            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomTextInputLayout);

            String fontName = a.getString(R.styleable.CustomTextInputLayout_inputFont);
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
}
