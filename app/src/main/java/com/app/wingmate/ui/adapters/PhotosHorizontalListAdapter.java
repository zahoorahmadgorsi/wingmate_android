package com.app.wingmate.ui.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.wingmate.R;
import com.app.wingmate.models.PhotoFile;
import com.app.wingmate.models.UserProfilePhotoVideo;
import com.app.wingmate.profile.edit.UploadPhotoVideoFragment;
import com.parse.ParseException;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
        } else {
            holder.addBtn.setVisibility(View.GONE);
            holder.delBtn.setVisibility(View.VISIBLE);
            if (object.getFile() != null)
                Picasso.get().load(object.getFile().getUrl()).resize(500, 500).placeholder(R.drawable.image_placeholder).into(holder.pic);
        }
        holder.itemView.setOnClickListener(v -> {
            if (object.isDummyFile()) {
                fragment.addImage();
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

        ViewHolder(View view) {
            super(view);
            this.pic = view.findViewById(R.id.pic);
            this.delBtn = view.findViewById(R.id.del_btn);
            this.addBtn = view.findViewById(R.id.add_view);
        }
    }
}
