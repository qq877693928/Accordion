package joneslee.android.com.library.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import java.util.ArrayList;

import joneslee.android.com.library.R;
import joneslee.android.com.library.adapter.ViewHolder;
import joneslee.android.com.library.adapter.ViewListAdapter;
import joneslee.android.com.library.helper.ViewTouchHelper;

/**
 * Description:Accordion View Group
 *
 * @author lizhenhua2003@gmail.com (Jones.Lee)
 * @date 15/12/7 16:20
 */
public class AccordionLayout extends FrameLayout implements ViewTouchHelper.OnViewScrollListener {

  private ViewTouchHelper mViewTouchHelper;
  private Context mContext;
  /** 默认Item的宽度, Default width */
  private float mDefaultItemWidth = 120.0F;
  /** Item的宽度, Item width */
  private int mItemWidth;
  /** 正常显示Item, normal visible width */
  private int mVisibleItemWidth;
  /** 展开Item的最大, Max width */
  private int mMaxItemWidth;
  private int mMaxOffsetX;
  private int mLayoutHeight;
  private int mLayoutWidth;
  private ArrayList<AccordionItemTransform> mAccordionItemTransforms =
      new ArrayList<AccordionItemTransform>();
  public float mLeftStart = 0.0f;
  private float mScrollX;

  private ViewListAdapter mAdapter;

  float mScrollOffset = 0.0f;
  float mEnterOffset = 1.0f;
  float mOffsetStart = 60.0f;


  public AccordionLayout(Context context) {
    this(context, null, 0);
  }

  public AccordionLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public AccordionLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    mContext = context;

    // get attr value
    TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.AccordionLayout);
    // defalut item width
    int defaultItemHeight =
        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mDefaultItemWidth, context
            .getResources()
            .getDisplayMetrics());
    mItemWidth =
        ta.getDimensionPixelOffset(R.styleable.AccordionLayout_itemWidth, defaultItemHeight);

    mMaxOffsetX = (int) (mItemWidth * 0.4f);

    mVisibleItemWidth = (int) ((mItemWidth * 0.6f) + mScrollOffset);
    mMaxItemWidth = (int) (mItemWidth * 0.8f);


    mViewTouchHelper = new ViewTouchHelper(context, this, this);

    ta.recycle();
  }

  private void initItemTransforms() {
    mAccordionItemTransforms.clear();
    for (int i = 0; i < getChildCount(); i++) {
      AccordionItemTransform itemTransform = new AccordionItemTransform();
      itemTransform.setLeft(getVisibleItemWidth() * i + getRight() * mEnterOffset
          + (float) Math.pow((double) 2, (double) i) * mVisibleItemWidth * mEnterOffset);
      itemTransform.setTop(0.0f);
      itemTransform.setRight(getVisibleItemWidth() * (i + 1) + getRight() * mEnterOffset
          + (float) Math.pow((double) 2, (double) i) * mVisibleItemWidth * mEnterOffset);
      mAccordionItemTransforms.add(i, itemTransform);
    }
  }

  public void setAdapter(ViewListAdapter adapter) {
    mAdapter = adapter;
    removeAllViews();
    for (int i = 0; i < mAdapter.getItemCount(); i++) {
      ViewHolder viewHolder = mAdapter.createViewHolder(mContext, mAdapter.getItemViewType(i));
      viewHolder.getItemView().setTag(Integer.valueOf(i));
      mAdapter.bindViewHolder(viewHolder, i);
      addView(viewHolder.getItemView());
    }
    performEnterAnimation();
  }

  public ArrayList<AccordionItemTransform> getAccordionItemTransforms() {
    return mAccordionItemTransforms;
  }

  public int getVisibleItemWidth() {
    return mVisibleItemWidth;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    mLayoutHeight = getMeasuredHeight();
    mLayoutWidth = getMeasuredWidth();

    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
    int heightSize = MeasureSpec.getSize(heightMeasureSpec);
    for (int i = 0; i < getChildCount(); i++) {
      View view = getChildAt(i);
      view.setElevation((getChildCount() - i) * 2.0f);
      if (view != null && view.getVisibility() != View.GONE) {
        measureChildren(MeasureSpec.makeMeasureSpec(mItemWidth, widthMode),
            MeasureSpec.makeMeasureSpec(mLayoutHeight, heightMode));
      }
    }
    setMeasuredDimension(widthSize,
        heightSize);

  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    if (getChildCount() > 0) {
      for (int i = getChildCount() - 1; i >= 0; i--) {
        View view = getChildAt(i);
        if (mAccordionItemTransforms.size() >= i) {
          // view layout method, set view transform in ViewGroup
          view.layout((int) (left + mAccordionItemTransforms.get(i).getLeft()),
              top,
              (int) (left + mAccordionItemTransforms.get(i).getRight()),
              top + view.getMeasuredHeight());
        }
      }
    }
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    return mViewTouchHelper.onInterceptTouchEvent(ev);
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    return mViewTouchHelper.onTouchEvent(ev);
  }

  @Override
  public void onScrollChanged(float scroll) {
    requestWidthOffsetX(scroll);
    requestItemLayoutModel();
    requestLayout();
  }

  @Override
  public void onAnimaitonChanged(float offset) {
    mScrollOffset = mOffsetStart * offset;
    requestItemLayoutModel();
    requestLayout();
  }

  private void requestItemLayoutModel() {
    mVisibleItemWidth = (int) Math.min((mItemWidth * 0.6f) + mScrollOffset, mMaxItemWidth);

    int dragIndex = mViewTouchHelper.getCurrentDragIndex();
    AccordionItemTransform currentItemTransfrom = mAccordionItemTransforms.get(dragIndex);
    currentItemTransfrom.setLeft(Math.max(
        Math.min(mViewTouchHelper.getVisibleX() + mScrollX, dragIndex * getVisibleItemWidth()),
        getRight() - (getChildCount() - dragIndex - 1) * getVisibleItemWidth()
            - getVisibleItemWidth()));
    currentItemTransfrom.setRight(Math.max(currentItemTransfrom.getLeft() + getVisibleItemWidth(),
        getRight() - (getChildCount() - dragIndex - 1) * getVisibleItemWidth()));
    for (int i = 0; i < getChildCount(); i++) {
      AccordionItemTransform item = mAccordionItemTransforms.get(i);
      if (i < mViewTouchHelper.getCurrentDragIndex()) {
        item.setLeft(Math.min(
            currentItemTransfrom.getLeft() + (i - mViewTouchHelper.getCurrentDragIndex())
                * getVisibleItemWidth(), i * getVisibleItemWidth()));
        item.setRight(Math.max(item.getLeft() + getVisibleItemWidth(), getRight()
            - (getChildCount() - i - 1)
            * getVisibleItemWidth()));
      } else if (i > mViewTouchHelper.getCurrentDragIndex()) {
        item.setRight(Math.max(
            currentItemTransfrom.getRight() + (i - mViewTouchHelper.getCurrentDragIndex())
                * getVisibleItemWidth(),
            getRight() - (getChildCount() - i - 1) * getVisibleItemWidth()));
        item.setLeft(Math.min(item.getRight() - getVisibleItemWidth(), i * getVisibleItemWidth()));
      }
    }
  }

  private void requestWidthOffsetX(float p) {
    mScrollX = p;
    mScrollOffset = Math.max(mScrollOffset, Math.min(mOffsetStart, Math.abs(mScrollX)));
  }

  @Override
  public void computeScroll() {
    mViewTouchHelper.performComputeScroll();
  }

  public void performEnterAnimation() {
    ValueAnimator valueAnim = ObjectAnimator.ofFloat(1.0f, 0.0f);
    valueAnim.setDuration(1000);
    valueAnim.setInterpolator(new DecelerateInterpolator());
    valueAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        mEnterOffset = (float) animation.getAnimatedValue();
        mAccordionItemTransforms.clear();
        initItemTransforms();
        requestLayout();
      }
    });
    valueAnim.start();
  }

  public void performOutAnimation() {
    mViewTouchHelper.recordCurrentVisibleIndex();
    mLeftStart =
        mAccordionItemTransforms.get(mViewTouchHelper.getCurrentLocationLeftIndex()).getLeft();
    ValueAnimator valueAnim = ObjectAnimator.ofFloat(0.0f, 1.0f);
    valueAnim.setDuration(1000);
    valueAnim.setInterpolator(new DecelerateInterpolator());
    valueAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        mEnterOffset = (float) animation.getAnimatedValue();
        updateItemTransforms();
      }
    });
    valueAnim.start();
  }

  private void updateItemTransforms() {
    for (int i = mViewTouchHelper.getCurrentLocationRightIndex(); i >= mViewTouchHelper
        .getCurrentLocationLeftIndex(); i--) {
      AccordionItemTransform itemTransform = mAccordionItemTransforms.get(i);
      int offsetIndex = i - mViewTouchHelper.getCurrentLocationLeftIndex();
      itemTransform.setLeft(mLeftStart + offsetIndex * getVisibleItemWidth() + getRight()
          * mEnterOffset
          + (float) Math.pow((double) 2, (double) offsetIndex) * getVisibleItemWidth()
          * mEnterOffset);
      itemTransform.setRight(itemTransform.getLeft() + getVisibleItemWidth());
    }
    requestLayout();
  }
}
