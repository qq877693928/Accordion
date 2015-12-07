package joneslee.android.com.sample;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import joneslee.android.com.library.util.ViewUtils;


/**
 * Description:
 *
 * @date 15/11/4 10:28
 */
public class StretchItemView extends LinearLayout {
    private ImageView icon;
    private TextView txt;

    private int mScreenWidth;

    public StretchItemView(Context context) {
        super(context);
        mScreenWidth = (int) DisplayUtil.getScreenWidth(context);
    }

    public StretchItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScreenWidth = (int) DisplayUtil.getScreenWidth(context);
    }

    public static StretchItemView newInstance(ViewGroup parent) {
        return (StretchItemView) ViewUtils.newInstance(parent,
                R.layout.layout_item);
    }

    public static StretchItemView newInstance(Context context) {
        return (StretchItemView) ViewUtils
                .newInstance(context, R.layout.layout_item);
    }

    @Override
    public void setElevation(float elevation) {
        super.setElevation(elevation);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    private void initView() {
        icon = (ImageView) findViewById(R.id.image);
        txt = (TextView) findViewById(R.id.text);
    }

    public void animateLayout (){
        float f = (float)getWidth() / mScreenWidth;

        this.icon.setScaleX(Math.max(0.7F, f));
        this.icon.setScaleY(Math.max(0.7F, f));

        this.txt.setScaleX(f);
        this.txt.setScaleY(f);
        this.txt.setAlpha(f);
    }

    public void setTextString(String textString) {
        txt.setText(textString);
    }

    public void setIcon(int color) {
        icon.setBackgroundColor(color);
    }
}
