package team.circleofcampus.model;

import java.io.Serializable;

/**
 * Created by Jaye Li on 2018/6/2.
 */
public class SpinnerItem implements Serializable {
    private int itemId;
    private String itemName;

    public SpinnerItem(int itemId, String itemName) {
        this.itemId = itemId;
        this.itemName = itemName;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
