package ua.kiev.vignatyev.vhome1;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by vignatyev on 07.10.2015.
 */
public class ExtendedViewPager extends ViewPager {

    public ExtendedViewPager(Context context) {
        super(context);
    }

    public ExtendedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        return (v instanceof TouchImageView) ? ((TouchImageView) v).canScrollHorizontallyFroyo(-dx) :
            super.canScroll(v, checkV, dx, x, y);
    }
}
