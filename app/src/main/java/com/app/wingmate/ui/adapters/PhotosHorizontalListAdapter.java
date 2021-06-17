package com.app.wingmate.ui.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.wingmate.R;
import com.app.wingmate.models.UserProfilePhotoVideo;
import com.app.wingmate.ui.fragments.UploadPhotoVideoFragment;
import com.app.wingmate.utils.ActivityUtility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.app.wingmate.utils.AppConstants.ACTIVE;
import static com.app.wingmate.utils.AppConstants.PARAM_FILE_STATUS;
import static com.app.wingmate.utils.AppConstants.PENDING;
import static com.app.wingmate.utils.AppConstants.REJECTED;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PHOTO_VIEW;

public class PhotosHorizontalListAdapter extends RecyclerView.Adapter<PhotosHorizontalListAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private Activity context;
    private UploadPhotoVideoFragment fragment;
    private List<UserProfilePhotoVideo> itemsList;

    public PhotosHorizontalListAdapter(Activity context, UploadPhotoVideoFragment fragment, List<UserProfilePhotoVideo> itemsList) {
        this.inflater = LayoutInflater.from(context);
        this.itemsList = itemsList;
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public PhotosHorizontalListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PhotosHorizontalListAdapter.ViewHolder(inflater.inflate(R.layout.list_item_photo, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PhotosHorizontalListAdapter.ViewHolder holder, final int position) {
        UserProfilePhotoVideo object = itemsList.get(position);
        if (object.isDummyFile()) {
            holder.delBtn.setVisibility(View.GONE);
            holder.addBtn.setVisibility(View.VISIBLE);
            holder.pic.setImageResource(android.R.color.transparent);
            holder.statusTV.setVisibility(View.GONE);
        } else {
            holder.addBtn.setVisibility(View.GONE);
            holder.delBtn.setVisibility(View.VISIBLE);
            if (object.getFile() != null)
                Picasso.get().load(object.getFile().getUrl()).resize(500, 500).placeholder(R.drawable.image_placeholder).into(holder.pic);

            if (object.getInt(PARAM_FILE_STATUS) == ACTIVE) {
                holder.statusTV.setVisibility(View.GONE);
            } else if (object.getInt(PARAM_FILE_STATUS) == PENDING) {
                holder.statusTV.setVisibility(View.VISIBLE);
                holder.statusTV.setText("Pending");
                holder.statusTV.setBackground(context.getResources().getDrawable(R.drawable.bg_status_pending));
            } else if (object.getInt(PARAM_FILE_STATUS) == REJECTED) {
                holder.statusTV.setVisibility(View.VISIBLE);
                holder.statusTV.setText("Rejected");
                holder.statusTV.setBackground(context.getResources().getDrawable(R.drawable.bg_status_rejected));
            }
        }
        holder.pic.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.itemView.setOnClickListener(v -> {
            if (object.isDummyFile()) {
                fragment.addImage();
            } else {
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(object.getFile().getUrl());
                ActivityUtility.startPhotoViewActivity(context, KEY_FRAGMENT_PHOTO_VIEW, arrayList, 0);
//                ActivityUtility.startPhotoViewActivity(context, KEY_FRAGMENT_PHOTO_VIEW, object.getFile().getUrl());
            }
        });

        holder.delBtn.setOnClickListener(v -> fragment.deleteImage(position));

    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public void setData(List<UserProfilePhotoVideo> itemsList) {
        this.itemsList = new ArrayList<>();
        this.itemsList = itemsList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView delBtn;
        ImageView addBtn;
        ImageView pic;
        TextView statusTV;

        ViewHolder(View view) {
            super(view);
            this.pic = view.findViewById(R.id.pic);
            this.delBtn = view.findViewById(R.id.del_btn);
            this.addBtn = view.findViewById(R.id.add_view);
            this.statusTV = view.findViewById(R.id.status);
        }
    }
}
