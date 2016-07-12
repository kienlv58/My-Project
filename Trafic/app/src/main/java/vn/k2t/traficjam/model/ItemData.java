package vn.k2t.traficjam.model;

import java.io.Serializable;
import java.util.HashMap;

public class ItemData implements Serializable {

    private static final long serialVersionUID = 1L;
    private HashMap<String, String> mItemData;

    public ItemData() {
        mItemData = new HashMap<String, String>();
    }

    public HashMap<String, String> getmItemData() {
        return mItemData;
    }

    public void setmItemData(HashMap<String, String> mItemData) {
        this.mItemData = mItemData;
    }
}