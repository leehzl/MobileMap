package com.lilynlee.mobilemap.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lilynlee.mobilemap.R;
import com.lilynlee.mobilemap.object.ReportItem;

import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by admin on 2016/8/1.
 */
public class StickyListAdapter extends ArrayAdapter<ReportItem> implements StickyListHeadersAdapter {

    private LayoutInflater mInflater;
    private int resourceId;
    private Context mContext;
    private List<ReportItem> mlist;

    public StickyListAdapter(Context context,int resource, List<ReportItem> list){
        super(context,resource,list);
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.resourceId = resource;
        this.mlist = list;
    }
    @Override
    public View getHeaderView(int i, View view, ViewGroup viewGroup) {
        HeaderViewHolder holder;
        if (view == null) {
            holder = new HeaderViewHolder();
            view = mInflater.inflate(R.layout.reportlist_header,null);
            holder.text = (TextView) view.findViewById(R.id.listheader_text);
            view.setTag(holder);
        } else {
            holder = (HeaderViewHolder) view.getTag();
        }
        CharSequence headerChar = mlist.get(i).getFile_name().subSequence(0, 7);
        holder.text.setText(headerChar);
        return view;
    }
    /**
     * 这个是用来边标记浮动headerView的一个方法，返回相同ID的将被显示为同一View
     * @param i 当前位置
     * @return 返回唯一ID
     */
    @Override
    public long getHeaderId(int i) {
        return mlist.get(i).getFile_name().subSequence(0,7).hashCode();
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public ReportItem getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ReportItem item = getItem(position);
        ViewHolder holder;
        View view;
        if (convertView == null) {
            view = mInflater.inflate(resourceId,null);
            holder = new ViewHolder();
            holder.name_tv = (TextView) view.findViewById(R.id.reportitem_name);
            holder.location_tv = (TextView) view.findViewById(R.id.reportitem_location);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.name_tv.setText(item.getFile_name());
        holder.location_tv.setText(item.getLocation());
        return view;
    }

    /**
     * 列表分组头
     */
    class HeaderViewHolder {
        TextView text;
    }

    /**
     * 列表内容
     */
    class ViewHolder {
        TextView name_tv;
        TextView location_tv;
    }
}
