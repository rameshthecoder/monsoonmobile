package in.monsoonmedia.monsoonmobile;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * Created by ramesh on 30/10/17.
 */

public class GridViewScrollable extends GridView {


    public GridViewScrollable(Context context) {
        super(context);
    }

    public GridViewScrollable(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridViewScrollable(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        requestDisallowInterceptTouchEvent(false);
        return super.onTouchEvent(ev);
    }
}
