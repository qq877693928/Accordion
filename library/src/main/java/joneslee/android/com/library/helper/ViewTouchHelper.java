package joneslee.android.com.library.helper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewParent;
import android.view.animation.LinearInterpolator;
import android.widget.OverScroller;

import java.util.ArrayList;

import joneslee.android.com.library.widget.AccordionItemTransform;
import joneslee.android.com.library.widget.AccordionLayout;

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
  private static final int FING_SCROLL_RANGE = 150;

  private AccordionLayout mAccordionLayout;
  private VelocityTracker mVelocityTracker;
  private OverScroller mOverScroller;
  private OnViewScrollListener mOnViewScrollListener;

  private float mTotalPMotion;
  private boolean mIsScrolling;
  private int mInitialMotionX, mInitialMotionY;
  private int mLastMotionX, mLastMotionY;
  private int mActivePointerId = INACTIVE_POINTER_ID;

  private int mMinimumVelocity = MINIMUM_FLING_VELOCITY;
  private int mMaximumVelocity = MAXIMUM_FLING_VELOCITY;

  private int mCurrentLocationLeftIndex;
  private int mCurrentLocationRightIndex;
  private int mCurrentDragIndex;

  /**
   * 屏幕的起始坐标x, Screen visible view's x coordinate
   */
  private float mVisibleX;

  /**
   * 滑动的最小距离，The scroll touch slop is used to calculate when we start scrolling
   */
  private int mScrollTouchSlop = TOUCH_SLOP;

  private ArrayList<AccordionItemTransform> mAccordionItemTransforms;

  private float mScrollP;
  private float mScorllX = 0.0f;

  /**
   * 动画, Animation
   */
  private ValueAnimator mValueAnimator;

  public ViewTouchHelper(Context context, AccordionLayout accordionLayout,
      ViewTouchHelper.OnViewScrollListener listener) {
    mAccordionLayout = accordionLayout;
    mOverScroller = new OverScroller(context);
    mOnViewScrollListener = listener;
  }

  /** 速度监听初始化/销毁，Velocity tracker helpers */
  void initOrResetVelocityTracker() {
    if (mVelocityTracker == null) {
      mVelocityTracker = VelocityTracker.obtain();
    } else {
      mVelocityTracker.clear();
    }
  }

  void initVelocityTrackerIfNotExists() {
    if (mVelocityTracker == null) {
      mVelocityTracker = VelocityTracker.obtain();
    }
  }

  void recycleVelocityTracker() {
    if (mVelocityTracker != null) {
      mVelocityTracker.recycle();
      mVelocityTracker = null;
    }
  }

  /** 事件Intercept，Touch preprocessing for handling below */
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    /** ViewGroup,必须有children, Return early if we have no children */
    boolean hasChildren = (mAccordionLayout.getChildCount() > 0);
    if (!hasChildren) {
      return false;
    }

    boolean wasScrolling = isScrolling() ||
        (mValueAnimator != null && mValueAnimator.isRunning());
    int action = ev.getAction();
    switch (action & MotionEvent.ACTION_MASK) {
      case MotionEvent.ACTION_DOWN: {
        /** 当手指按下时记录手指位置，Save the touch down info */
        mInitialMotionX = mLastMotionX = (int) ev.getX();
        mInitialMotionY = mLastMotionY = (int) ev.getY();
        mActivePointerId = ev.getPointerId(0);
        /** 停止惯性滚动，Stop the current scroll if it is still flinging */
        stopScroller();
        stopBoundScrollAnimation();

        initOrResetVelocityTracker();
        mVelocityTracker.addMovement(createMotionEventForStackScroll(ev));
        /** 滚动是否完成，Check if the scroller is finished yet */
        mIsScrolling = isScrolling();
        break;
      }
      case MotionEvent.ACTION_MOVE: {
        if (mActivePointerId == INACTIVE_POINTER_ID) break;

        int activePointerIndex = ev.findPointerIndex(mActivePointerId);
        int y = (int) ev.getY(activePointerIndex);
        int x = (int) ev.getX(activePointerIndex);
        if (Math.abs(x - mInitialMotionX) > mScrollTouchSlop) {
          mIsScrolling = true;
          initVelocityTrackerIfNotExists();
          mVelocityTracker.addMovement(createMotionEventForStackScroll(ev));
          // Disallow parents from intercepting touch events
          final ViewParent parent = mAccordionLayout.getParent();
          if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
          }
        }
        break;
      }
      case MotionEvent.ACTION_CANCEL:
      case MotionEvent.ACTION_UP: {
        /**
         * 恢复状态，手指松开
         * Animate the scroll back if we've cancelled
         * animateBoundScroll();
         * Reset the drag state and the velocity tracker
         */
        mIsScrolling = false;
        mActivePointerId = INACTIVE_POINTER_ID;
        mTotalPMotion = 0;
        recycleVelocityTracker();
        break;
      }
    }
    return mIsScrolling;
  }

  /** 事件分发，Handles touch events */
  public boolean onTouchEvent(MotionEvent ev) {

    // Short circuit if we have no children
    boolean hasChildren = (mAccordionLayout.getChildCount() > 0);
    if (!hasChildren) {
      return false;
    }

    // Update the velocity tracker
    initVelocityTrackerIfNotExists();

    int action = ev.getAction();
    switch (action & MotionEvent.ACTION_MASK) {
      case MotionEvent.ACTION_DOWN: {
        // Save the touch down info
        mInitialMotionX = mLastMotionX = (int) ev.getX();
        mInitialMotionY = mLastMotionY = (int) ev.getY();
        recordCurrentDragIndex(mLastMotionX);
        mVisibleX = mAccordionLayout.mAccordionItemTransforms.get(mCurrentDragIndex).getLeft();
        mActivePointerId = ev.getPointerId(0);
        // Stop the current scroll if it is still flinging
        stopScroller();
        stopBoundScrollAnimation();
        // Initialize the velocity tracker
        initOrResetVelocityTracker();
        mVelocityTracker.addMovement(createMotionEventForStackScroll(ev));
        // Disallow parents from intercepting touch events
        final ViewParent parent = mAccordionLayout.getParent();
        if (parent != null) {
          parent.requestDisallowInterceptTouchEvent(true);
        }
        break;
      }
      case MotionEvent.ACTION_POINTER_DOWN: {
        final int index = ev.getActionIndex();
        mActivePointerId = ev.getPointerId(index);
        break;
      }
      case MotionEvent.ACTION_MOVE: {
        if (mActivePointerId == INACTIVE_POINTER_ID) break;

        int activePointerIndex = ev.findPointerIndex(mActivePointerId);
        int x = (int) ev.getX(activePointerIndex);
        int y = (int) ev.getY(activePointerIndex);
        int xTotal = Math.abs(x - mInitialMotionX);
        // float curP = screenYToCurveProgress(y);
        // float deltaP = mLastP - curP;
        if (!mIsScrolling) {
          if (xTotal > mScrollTouchSlop) {
            mIsScrolling = true;
            // Initialize the velocity tracker
            initOrResetVelocityTracker();
            setStackScroll(x - mInitialMotionX);
            mVelocityTracker.addMovement(createMotionEventForStackScroll(ev));
            // Disallow parents from intercepting touch events
          }
        }
        if (mIsScrolling) {
          setStackScroll(x - mInitialMotionX);
          mVelocityTracker.addMovement(createMotionEventForStackScroll(ev));
        }
        mAccordionLayout.isAnimation = false;
        final ViewParent parent = mAccordionLayout.getParent();
        if (parent != null) {
          parent.requestDisallowInterceptTouchEvent(true);
        }
        break;
      }
      case MotionEvent.ACTION_UP: {
        final VelocityTracker velocityTracker = mVelocityTracker;
        velocityTracker.computeCurrentVelocity(mMinimumVelocity, mMaximumVelocity);
        int velocity = (int) velocityTracker.getXVelocity(mActivePointerId);

        mOverScroller.startScroll((int) getStackScroll(), 0, velocity, 0);
        performStretchItmesAnim(velocity, null);

        mAccordionItemTransforms =
            (ArrayList<AccordionItemTransform>) mAccordionLayout.mAccordionItemTransforms.clone();
        mActivePointerId = INACTIVE_POINTER_ID;
        mIsScrolling = false;
        mTotalPMotion = 0;
        recycleVelocityTracker();
        break;
      }
      case MotionEvent.ACTION_POINTER_UP: {
        int pointerIndex = ev.getActionIndex();
        int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == mActivePointerId) {
          // Select a new active pointer id and reset the motion state
          final int newPointerIndex = (pointerIndex == 0) ? 1 : 0;
          mActivePointerId = ev.getPointerId(newPointerIndex);
          // mLastMotionX = (int) ev.getX(newPointerIndex);
          mLastMotionY = (int) ev.getY(newPointerIndex);
          mVelocityTracker.clear();
        }
        break;
      }
      case MotionEvent.ACTION_CANCEL: {
        mActivePointerId = INACTIVE_POINTER_ID;
        mIsScrolling = false;
        mTotalPMotion = 0;
        recycleVelocityTracker();
        break;
      }
    }
    return true;
  }

  /** Item之间的收缩动画，Stretch animates between items */
  void performStretchItmesAnim(int velocity, final Runnable postRunnable) {
    stopScroller();
    stopBoundScrollAnimation();

    mValueAnimator = ObjectAnimator.ofFloat(1.0f, 0.0f);
    mValueAnimator.setDuration(1000 - Math.abs(velocity));
    mValueAnimator.setInterpolator(new LinearInterpolator());
    mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        setItemSpace((Float) animation.getAnimatedValue());
      }
    });
    mValueAnimator.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        if (postRunnable != null) {
          postRunnable.run();
        }
        mAccordionLayout.mItemAnimWidthOffsetX = 1.0f;
        mAccordionLayout.mItemWidthOffsetX = 0;
        mAccordionLayout.isAnimation = false;
        mValueAnimator.removeAllListeners();
      }
    });
    mValueAnimator.start();
  }

  /**
   * 设置Item之间的空隙，set item's space
   * 
   * @param animatedValue space of items
   */
  private void setItemSpace(Float animatedValue) {
    mAccordionLayout.onAnimaitonChanged(animatedValue);
  }

  boolean isScrolling() {
    return !mOverScroller.isFinished();
  }

  void stopScroller() {
    if (!mOverScroller.isFinished()) {
      mOverScroller.abortAnimation();
    }
  }

  /** 停止动画，Aborts current animation */
  void stopBoundScrollAnimation() {
    if (mValueAnimator != null) {
      mValueAnimator.removeAllListeners();
      mValueAnimator.cancel();
    }
  }

  /** Gets the current stack scroll */
  public float getStackScroll() {
    return mScrollP;
  }

  /** Sets the current stack scroll */
  public void setStackScroll(float s) {
    mScrollP = s;
    if (mOnViewScrollListener != null) {
      mOnViewScrollListener.onScrollChanged(s);
    }
  }

  /**
   * 当调用startScroll系统回调
   * Called from the view draw, computes the next scroll.
   * link#OverScroller.startScroll()
   */
  public boolean performComputeScroll() {
    if (mOverScroller.computeScrollOffset()) {
      float scroll = mOverScroller.getCurrX();
      setStackScroll(scroll);
      return true;
    } else {
      return false;
    }
  }

  /** 记录当前拖拽的Index */
  private void recordCurrentDragIndex(float scrollX) {
    for (int i = 0; i < mAccordionLayout.mAccordionItemTransforms.size(); i++) {
      if (scrollX >= mAccordionLayout.mAccordionItemTransforms.get(i).getLeft()
          && scrollX < mAccordionLayout.mAccordionItemTransforms.get(i).getRight()) {
        mCurrentDragIndex = i;
        break;
      }
    }
  }

  /** 记录当前左右边界view的Index */
  public void recordCurrentVisibleIndex() {
    for (int i = 0; i < mAccordionLayout.mAccordionItemTransforms.size(); i++) {
      if (0 >= mAccordionLayout.mAccordionItemTransforms.get(i).getLeft()
          && 0 < mAccordionLayout.mAccordionItemTransforms.get(i).getRight()) {
        mCurrentLocationLeftIndex = i;
        break;
      }
    }

    for (int i = mAccordionLayout.mAccordionItemTransforms.size() - 1; i >= 0; i--) {
      if (mAccordionLayout.getRight() <= mAccordionLayout.mAccordionItemTransforms.get(i)
          .getRight()
          && mAccordionLayout.getRight() > mAccordionLayout.mAccordionItemTransforms.get(i)
              .getLeft()) {
        mCurrentLocationRightIndex = i;
        break;
      }
    }
  }

  /** 构建事件，Constructs a simulated motion event for the current stack scroll. */
  MotionEvent createMotionEventForStackScroll(MotionEvent ev) {
    MotionEvent pev = MotionEvent.obtainNoHistory(ev);
    return pev;
  }

  public int getCurrentDragIndex() {
    return mCurrentDragIndex;
  }

  public int getCurrentLocationLeftIndex() {
    return mCurrentLocationLeftIndex;
  }

  public int getCurrentLocationRightIndex() {
    return mCurrentLocationRightIndex;
  }

  public float getVisibleX() {
    return mVisibleX;
  }

  /**
   * Item改变监听，View transform change listener
   */
  public interface OnViewScrollListener {
    void onScrollChanged(float scroll);

    void onAnimaitonChanged(float offset);
  }
}
