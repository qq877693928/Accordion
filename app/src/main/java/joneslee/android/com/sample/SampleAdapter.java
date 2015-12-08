package joneslee.android.com.sample;

import android.content.Context;
import android.view.View;

import joneslee.android.com.library.adapter.ViewHolder;
import joneslee.android.com.library.adapter.ViewListAdapter;

/**
 * Description:
 *
 * @author lizhenhua2003@gmail.com (Jones.Lee)
 * @date 15/12/8 17:15
 */
public class SampleAdapter extends ViewListAdapter<SampleAdapter.SampleViewHolder, SampleModel> {

  @Override
  public SampleViewHolder onCreateViewHolder(Context context, int viewType) {
    StretchItemView view = StretchItemView.newInstance(context);
    return new SampleViewHolder(view);
  }

  @Override
  public void onBindViewHolder(SampleViewHolder sampleViewHolder, int position) {
      sampleViewHolder.getItemView().setTextString(getData().get(position).getItemStr());
      sampleViewHolder.getItemView().setIcon(getData().get(position).getItemColor());
  }

  public class SampleViewHolder extends ViewHolder<StretchItemView> {

    public SampleViewHolder(StretchItemView itemView) {
      super(itemView);
    }
  }
}
