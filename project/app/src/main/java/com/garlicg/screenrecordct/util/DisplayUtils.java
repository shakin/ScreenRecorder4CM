package com.garlicg.screenrecordct.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.WindowManager;

public class DisplayUtils {
	
	/**
	 * Windowサイズを取得する
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static Point getWindowSize(Context context){
		WindowManager manager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		Point outSize = new Point();
		if(VERSION.SDK_INT < VERSION_CODES.HONEYCOMB_MR2){
			outSize.x = manager.getDefaultDisplay().getWidth();
			outSize.y = manager.getDefaultDisplay().getHeight();
			return outSize;
		}
		else{
			manager.getDefaultDisplay().getSize(outSize);
			return outSize;
		}
	}

    /**
     * dpをpxに変換する
     */
    public static int dpToPx(Resources res, int dp) {
        return (int) (res.getDisplayMetrics().density * dp + 0.5f);
    }
}