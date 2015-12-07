package joneslee.android.com.library.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import joneslee.android.com.library.helper.ViewTouchHelper;

/**
 * Description:
 *
 * @author lizhenhua2003@gmail.com (Jones.Lee)
 * @date 15/12/7 16:20
 */
public class AccordionLayout extends FrameLayout implements ViewTouchHelper.OnViewScrollListener {


    public AccordionLayout(Context context) {
        this(context, null, 0);
    }

    public AccordionLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AccordionLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onScrollChanged(float scroll) {

    }

    @Override
    public void onAnimaitonChanged(float offset) {

    }
}
