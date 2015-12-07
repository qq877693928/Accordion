package joneslee.android.com.library.helper;

import android.view.VelocityTracker;
import android.widget.OverScroller;

/**
 * Description:
 *
 * @author lizhenhua2003@gmail.com (Jones.Lee)
 * @date 15/12/7 15:42
 */
public class ViewTouchHelper {

  private static int INACTIVE_POINTER_ID = -1;
  /**
   * 最小滑动速度，Minimum velocity
   */
  private static final int MINIMUM_FLING_VELOCITY = 20;
  /**
   * 最大滑动速度，Maximum velocity
   */
  private static final int MAXIMUM_FLING_VELOCITY = 4000;

  private static final int TOUCH_SLOP = 4;

  /**
   * 惯性滚动，inertia scroll when move_up
   */
  private static final int FlingScrollRange = 150;

  private VelocityTracker mVelocityTracker;
  private OverScroller mOverScroller;


  /**
   * Item改变监听，View transform change listener
   */
  public interface OnViewScrollListener {
    void onScrollChanged(float scroll);

    void onAnimaitonChanged(float offset);
  }
}
