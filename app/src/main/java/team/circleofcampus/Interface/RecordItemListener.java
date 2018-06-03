package team.circleofcampus.Interface;

import android.view.View;

/**
 * 聊天界面 头像与对话框点击监听
 */

public interface RecordItemListener {
    void ClickIcon(View v, int position);
    void ClickDialog(View v, int position);
}
