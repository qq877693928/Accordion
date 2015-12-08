package joneslee.android.com.library.adapter;

import android.view.View;

/**
 * Description:Adapter's Viewholder
 *
 * @author lizhenhua2003@gmail.com (Jones.Lee)
 * @date 15/12/7 16:06
 */
public abstract class ViewHolder<V extends View> {
  public final V mItemView;

  public ViewHolder(V itemView) {
    if (itemView == null) {
      throw new IllegalArgumentException("itemView may not be null");
    }
    mItemView = itemView;
  }

  public V getItemView() {
    return mItemView;
  }
}
