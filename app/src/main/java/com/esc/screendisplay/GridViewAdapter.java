package com.esc.screendisplay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.esc.screendisplay.entity.DisplayEntity;

import java.util.ArrayList;
import java.util.List;

public class GridViewAdapter extends BaseAdapter {
    private List<DisplayEntity> list = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context mContext;
    private static int normalId = R.drawable.i_n_01;
    private static int focusId = R.drawable.i_n_01_h;
    private static int selectId = R.drawable.i_n_01_s;

    public GridViewAdapter(Context context, List<DisplayEntity> list) {
        this.list = list;
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        int value;
        if(list.get(position).getImageUrl().length() > 1)
            value = 1;
        else
            value = Integer.parseInt(list.get(position).getImageUrl());
        switch (value) {
            case 1:
                normalId = R.drawable.i_n_01;
                focusId = R.drawable.i_n_01_h;
                selectId = R.drawable.i_n_01_s;
                break;
            case 2:
                normalId = R.drawable.i_n_02;
                focusId = R.drawable.i_n_02_h;
                selectId = R.drawable.i_n_02_s;
                break;
            case 3:
                normalId = R.drawable.i_n_03;
                focusId = R.drawable.i_n_03_h;
                selectId = R.drawable.i_n_03_s;
                break;
            case 4:
                normalId = R.drawable.i_n_04;
                focusId = R.drawable.i_n_04_h;
                selectId = R.drawable.i_n_04_s;
                break;
        }
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.grid_view_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mRl = (RelativeLayout) convertView.findViewById(R.id.item_rl);
            viewHolder.mIcon = (ImageView) convertView.findViewById(R.id.grid_view_iv_main);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DisplayEntity displayEntity = list.get(position);
                if(displayEntity.isSelected())
                    displayEntity.setSelected(false);
                else
                    displayEntity.setSelected(true);
                ((MainActivity) mContext).updateSelectList(displayEntity);
                notifyDataSetChanged();
            }
        });
        viewHolder.mRl.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                int value;
                if(list.get(position).getImageUrl().length() > 1)
                    value = 1;
                else
                    value = Integer.parseInt(list.get(position).getImageUrl());
                switch (value) {
                    case 1:
                        normalId = R.drawable.i_n_01;
                        focusId = R.drawable.i_n_01_h;
                        selectId = R.drawable.i_n_01_s;
                        break;
                    case 2:
                        normalId = R.drawable.i_n_02;
                        focusId = R.drawable.i_n_02_h;
                        selectId = R.drawable.i_n_02_s;
                        break;
                    case 3:
                        normalId = R.drawable.i_n_03;
                        focusId = R.drawable.i_n_03_h;
                        selectId = R.drawable.i_n_03_s;
                        break;
                    case 4:
                        normalId = R.drawable.i_n_04;
                        focusId = R.drawable.i_n_04_h;
                        selectId = R.drawable.i_n_04_s;
                        break;
                }
                if(b) {
                    Glide.with(mContext).load(focusId).error(R.drawable.default_image).
                            into(viewHolder.mIcon);
                }
                else {
                    if(!list.get(position).isSelected())
                        Glide.with(mContext).load(normalId).error(R.drawable.default_image).
                                into(viewHolder.mIcon);
                    else
                        Glide.with(mContext).load(selectId).error(R.drawable.default_image).
                                into(viewHolder.mIcon);
                }
            }
        });
        if(list.get(position).isSelected())
        {
            Glide.with(mContext).load(selectId).error(R.drawable.default_image).
                    into(viewHolder.mIcon);
        }
        else
        {
            Glide.with(mContext).load(normalId).error(R.drawable.default_image).
                    into(viewHolder.mIcon);
        }
        return convertView;
    }

    class ViewHolder
    {
        protected RelativeLayout mRl;
        protected ImageView mIcon;
        public ViewHolder()
        {

        }
    }

    public void updateList(List<DisplayEntity> list) {
        this.list = list;
        notifyDataSetChanged();
    }
}
