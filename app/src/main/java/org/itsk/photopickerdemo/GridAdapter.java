package org.itsk.photopickerdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.itsk.photopicker.utils.OtherUtils;

import java.util.List;

public class GridAdapter extends BaseAdapter {
    private final int mColumnWidth;
    private List<SelectedPicture> dataSets;
    private Context mContext;
    private LayoutInflater layoutInflater;
    private int addImgResId;

    public GridAdapter(Context context, List<SelectedPicture> items) {
        this.mContext = context;
        this.dataSets = items;
        this.layoutInflater = LayoutInflater.from(context);
        int screenWidth = OtherUtils.getWidthInPx(mContext.getApplicationContext());
        mColumnWidth = (screenWidth - OtherUtils.dip2px(mContext.getApplicationContext(), 60)) / 3;
    }

    @Override
    public int getCount() {
        if (dataSets != null)
            return dataSets.size();
        return 0;
    }

    @Override
    public SelectedPicture getItem(int position) {
        if (dataSets != null)
            return dataSets.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_photo_grid, null);
            convertView.setTag(new ViewHolder(convertView));
        }
        initializeViews(getItem(position), (ViewHolder) convertView.getTag());
        return convertView;
    }

    private void initializeViews(final SelectedPicture item, ViewHolder holder) {
        holder.image.setImageResource(org.itsk.photopicker.R.drawable.ic_photo_loading);
        if ("add".equalsIgnoreCase(item.getPath())) {
            holder.image.setImageResource(addImgResId == 0 ? R.mipmap.icon_add_picture : addImgResId);
            holder.delete.setVisibility(View.GONE);
        } else {
            Bitmap bitmap= BitmapFactory.decodeFile(item.getPath());
            if (bitmap!=null)
                holder.image.setImageBitmap(bitmap);
            if (item.isShowDelete()) {
                holder.delete.setVisibility(View.VISIBLE);
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dataSets.remove(item);
                        if (dataSets.size() < 9) {
                            if (!"add".equals(dataSets.get(dataSets.size() - 1).getPath()))
                                dataSets.add(dataSets.size(), new SelectedPicture("add", false));
                        }
                        notifyDataSetChanged();
                    }
                });
            } else {
                holder.delete.setVisibility(View.GONE);
            }
        }
    }

    public void setAddImgResId(int addImgResId) {
        this.addImgResId = addImgResId;
    }

    public void showDelete(boolean isShow) {
        if (isShow) {
            for (SelectedPicture s : dataSets) {
                if ("add".equalsIgnoreCase(s.getPath())) {
                    s.setShowDelete(false);
                } else {
                    s.setShowDelete(true);
                }
            }
        } else {
            for (SelectedPicture s : dataSets) {
                s.setShowDelete(false);
            }
        }
        notifyDataSetChanged();
    }

    public double getColumnWidth() {
        return mColumnWidth;
    }

    protected class ViewHolder {
        private ImageView image;
        private ImageView delete;

        public ViewHolder(View view) {
            image = (ImageView) view.findViewById(R.id.image);
            delete = (ImageView) view.findViewById(R.id.delete);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mColumnWidth, mColumnWidth);
            image.setLayoutParams(params);
        }
    }
}
