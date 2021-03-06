package team.circleofcampus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import team.circleofcampus.Interface.OnItemListPopupClickListener;
import team.circleofcampus.R;

/**
 * 历史登陆账号列表适配器
 */
public class HistoryAccountListPopupAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<String> historyAccounts;

    private OnItemListPopupClickListener onItemListPopupClickListener;
    public void setOnItemListPopupClickListener(OnItemListPopupClickListener onItemListPopupClickListener) {
        this.onItemListPopupClickListener = onItemListPopupClickListener;
    }

    public HistoryAccountListPopupAdapter (Context context, ArrayList<String> historyAccounts) {
        this.inflater = LayoutInflater.from(context);
        this.historyAccounts = historyAccounts;
    }

    @Override
    public int getCount() {
        return historyAccounts.size();
    }

    @Override
    public Object getItem(int position) {
        return historyAccounts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.item_history_account_list, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.historyAccount.setText(historyAccounts.get(position));
        holder.historyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onItemListPopupClickListener) {
                    onItemListPopupClickListener.onItemClick(position, R.id.item_history_account);
                }
            }
        });

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onItemListPopupClickListener) {
                    onItemListPopupClickListener.onItemClick(position, R.id.item_history_account_remove);
                }
            }
        });

        return convertView;
    }

    class ViewHolder {
        TextView historyAccount;
        ImageView remove;
        public ViewHolder(View itemView) {
            historyAccount = (TextView) itemView.findViewById(R.id.item_history_account);
            remove = (ImageView) itemView.findViewById(R.id.item_history_account_remove);
        }
    }
}
