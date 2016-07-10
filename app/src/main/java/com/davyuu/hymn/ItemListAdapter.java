package com.davyuu.hymn;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by David Yu on 2016-07-04.
 */
public class ItemListAdapter extends BaseAdapter implements Filterable {
    LayoutInflater mInflater;
    private List<String> mOriginalItemNameList;
    private List<String> mDisplayItemNameList;
    private final Map<String, Integer> mItemImageList;

    public ItemListAdapter(Context context, List<String> itemNameList){
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        this.mOriginalItemNameList = itemNameList;
        this.mDisplayItemNameList = itemNameList;
        this.mItemImageList = dbHelper.getAllImageIds();
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mDisplayItemNameList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder{
        TextView itemNameText, itemCostText;
        ImageView itemImageView;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent){
        ViewHolder holder;

        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.item_list_view, null);
            holder.itemNameText = (TextView) view.findViewById(R.id.hymn_number);
            holder.itemCostText = (TextView) view.findViewById(R.id.hymn_name);
            view.setTag(holder);
        }
        else{
            holder = (ViewHolder) view.getTag();
        }

        String itemName = mDisplayItemNameList.get(position);
        holder.itemNameText.setText(itemName);
        holder.itemImageView.setImageResource(mItemImageList.get(itemName));

        return view;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<String> filteredItemNameList = new ArrayList<>();

                if(mOriginalItemNameList == null){
                    mOriginalItemNameList = new ArrayList<>(mDisplayItemNameList);
                }

                if(constraint == null || constraint.length() == 0){
                    results.count = mOriginalItemNameList.size();
                    results.values = mOriginalItemNameList;
                }
                else {
                    for(String name : mOriginalItemNameList){
                        if(name.toLowerCase().contains(constraint.toString().toLowerCase())){
                            filteredItemNameList.add(name);
                        }
                    }
                    results.count = filteredItemNameList.size();
                    results.values = filteredItemNameList;
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mDisplayItemNameList = (List<String>) results.values;
                notifyDataSetChanged();
            }
        };

        return filter;
    }
}
