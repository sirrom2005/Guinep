package com.trafalgartmc.guinep.Assets;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * Created by Rohan Morris on 8/29/2016.
 */
public class TextLabel extends android.support.v7.widget.AppCompatTextView {
    public TextLabel(Context context) {
        super(context);
        styleChange(context);
    }

    public TextLabel(Context context, AttributeSet attrs) {
        super(context, attrs);
        styleChange(context);
    }

    public TextLabel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        styleChange(context);
    }

    public void styleChange(Context c) {
        this.setTypeface(Typeface.createFromAsset(c.getAssets(),"fonts/Roboto-Medium.ttf"));
        /*this.setTextColor(Color.rgb(0,0,0));
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        this.setPadding(0,0,0,0);*/
    }

    public int intToDip(int i) {
        final float scale = getResources().getDisplayMetrics().density;
        int padding_in_px = (int) (i * scale + 0.5f);
        return padding_in_px;
    }
}
