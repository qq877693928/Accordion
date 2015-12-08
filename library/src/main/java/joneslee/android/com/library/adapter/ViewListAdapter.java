package joneslee.android.com.library.adapter;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 *
 * @author lizhenhua2003@gmail.com (Jones.Lee)
 * @date 15/12/7 16:06
 */
public abstract class ViewListAdapter<VH extends ViewHolder, M extends Object> {
  private final static String TAG = "ViewListAdapter";

  private List<M> mItems = new ArrayList<>();

  /** Task stack callbacks */
  public interface OnDataChangedListener {
    public void onCardAdded(ViewListAdapter adapter, int position);

    public void onCardRemoved(ViewListAdapter adapter, int position);
  }

  OnDataChangedListener mOnDataChangedListener;

  /**
   * 构造，Contructor
   */
  public ViewListAdapter() {}

  public ViewListAdapter(List<M> items) {
    // data not is empty
    if (items != null) {
      mItems = items;
    } else {
      Log.d(TAG, "data may not be null");
    }
  }

  public void setData(List<M> items) {
    mItems = items;
  }

  public List<M> getData() {
    return mItems;
  }

  /**
   * 插入元素
   * 
   * @param model 元素
   * @param position 位置
   */
  public void notifyDataSetInserted(M model, int position) {
    if (position < 0 || position > mItems.size()) {
      throw new IllegalArgumentException("Position is out of bounds.");
    }

    mItems.add(position, model);

    if (mOnDataChangedListener != null) {
      mOnDataChangedListener.onCardAdded(this, position);
    }
  }

  /** Removes a task */
  public void notifyDataSetRemoved(int position) {
    if (position < 0 || position > mItems.size()) {
      throw new IllegalArgumentException("Position is out of bounds.");
    }

    mItems.remove(position);

    if (mOnDataChangedListener != null) {
      // Notify that a task has been removed
      mOnDataChangedListener.onCardRemoved(this, position);
    }
  }

    public int getItemCount() {
        return mItems.size();
    }

  public int getItemViewType(int position) {
    return 0;
  }

  public abstract VH onCreateViewHolder(Context context, int viewType);

  public abstract void onBindViewHolder(VH vh, int position);

  public final VH createViewHolder(Context context, int viewType) {
    VH vh = onCreateViewHolder(context, viewType);
    return vh;
  }

  public final void bindViewHolder(VH vh, int position) {
    onBindViewHolder(vh, position);
  }
}
