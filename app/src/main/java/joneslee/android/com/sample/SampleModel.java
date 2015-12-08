package joneslee.android.com.sample;

import android.graphics.Color;

/**
 * Description:
 *
 * @author lizhenhua2003@gmail.com (Jones.Lee)
 * @date 15/12/8 17:17
 */
public class SampleModel {
    private String itemStr;
    private int itemColor;

    public SampleModel() {
    }

    public SampleModel(String itemStr, int itemColor) {
        this.itemStr = itemStr;
        this.itemColor = itemColor;
    }

    public String getItemStr() {
        return itemStr;
    }

    public int getItemColor() {
        return itemColor;
    }

    public void setItemStr(String itemStr) {
        this.itemStr = itemStr;
    }

    public void setItemColor(int itemColor) {
        this.itemColor = itemColor;
    }
}
