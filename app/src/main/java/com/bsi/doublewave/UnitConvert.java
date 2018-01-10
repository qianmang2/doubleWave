package com.bsi.doublewave;

import android.content.Context;
import android.util.TypedValue;

/**
 * @author QianMang
 * @Date 2017/1/10.
 * @Email qianmang@51bsi.com
 */
public class UnitConvert {
    public static int px2dp(Context context, int dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
