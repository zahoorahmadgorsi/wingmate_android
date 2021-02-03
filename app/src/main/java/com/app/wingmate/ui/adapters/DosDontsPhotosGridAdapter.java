package com.app.wingmate.ui.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.wingmate.R;
import com.app.wingmate.models.TermsConditions;
import com.app.wingmate.utils.ActivityUtility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.app.wingmate.utils.AppConstants.KEY_VIDEO;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PHOTO_VIEW;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_VIDEO_VIEW;

public class DosDontsPhotosGridAdapter extends RecyclerView.Adapter<DosDontsPhotosGridAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private Activity context;
    private List<TermsConditions> itemsList;

    public DosDontsPhotosGridAdapter(Activity context, List<TermsConditions> itemsList) {
        this.inflater = LayoutInflater.from(context);
        this.itemsList = itemsList;
        this.context = context;
    }

    @NonNull
    @Override
    public DosDontsPhotosGridAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DosDontsPhotosGridAdapter.ViewHolder(inflater.inflate(R.layout.list_item_dos_photos, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DosDontsPhotosGridAdapter.ViewHolder holder, final int position) {
        TermsConditions object = itemsList.get(position);

        if (object.getTermsType().equalsIgnoreCase(KEY_VIDEO)) {
            holder.playIcon.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(object.getFile().getUrl())
                    .thumbnail(Glide.with(context).load(object.getFile().getUrl()).placeholder(R.drawable.image_placeholder).apply(new RequestOptions().override(600, 600)))
                    .apply(new RequestOptions().override(600, 600))
                    .placeholder(R.drawable.image_placeholder)
                    .into(holder.pic);
        } else {
            holder.playIcon.setVisibility(View.GONE);
            Picasso.get().load(object.getFile().getUrl()).resize(600, 600).placeholder(R.drawable.image_placeholder).into(holder.pic);
        }

        if (object.isDo()) {
            holder.icon.setImageResource(R.drawable.big_green_tick);
        } else {
            holder.icon.setImageResource(R.drawable.big_red_cross);
        }

        holder.itemView.setOnClickListener(v -> {
            if (object.getTermsType().equalsIgnoreCase(KEY_VIDEO)) {
                ActivityUtility.startVideoViewActivity(context, KEY_FRAGMENT_VIDEO_VIEW, object.getFile().getUrl());
            } else {
                ActivityUtility.startPhotoViewActivity(context, KEY_FRAGMENT_PHOTO_VIEW, object.getFile().getUrl());
            }
        });
    }

    private void setVideoImage(String videoFilePath) {

    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public void setData(List<TermsConditions> itemsList) {
        this.itemsList = new ArrayList<>();
        this.itemsList = itemsList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView playIcon;
        ImageView pic;
        ImageView icon;

        ViewHolder(View view) {
            super(view);
            this.cardView = view.findViewById(R.id.card_view);
            this.pic = view.findViewById(R.id.pic_1);
            this.icon = view.findViewById(R.id.tick);
            this.playIcon = view.findViewById(R.id.play_icon);
        }
    }
}