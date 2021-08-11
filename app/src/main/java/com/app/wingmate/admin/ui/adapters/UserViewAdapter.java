package com.app.wingmate.admin.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.wingmate.R;
import com.app.wingmate.models.MyCustomUser;
import com.app.wingmate.admin.ui.fragments.AdminDashboardFragment;
import com.app.wingmate.utils.ActivityUtility;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.app.wingmate.utils.AppConstants.PARAM_ACCOUNT_STATUS;
import static com.app.wingmate.utils.AppConstants.PARAM_GROUP_CATEGORY;
import static com.app.wingmate.utils.AppConstants.PARAM_MEDIA_PENDING;
import static com.app.wingmate.utils.AppConstants.PARAM_NICK;
import static com.app.wingmate.utils.AppConstants.PARAM_PROFILE_PIC;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_ADMIN_PROFILE;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PROFILE;
import static com.app.wingmate.utils.Utilities.getDistanceBetweenUser;

public class UserViewAdapter extends RecyclerView.Adapter<UserViewAdapter.ViewHolder> {

    Activity activity;
    Context context;
    private View empty;
    private List<MyCustomUser> mValues;
    private AdminDashboardFragment dashboardFragment;

    public UserViewAdapter(Context context, AdminDashboardFragment dashboardFragment, List<MyCustomUser> mValues) {
        this.context = context;
        this.activity = (Activity) context;
        this.mValues = mValues;
        this.dashboardFragment = dashboardFragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_user_new_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final ParseUser parseUser = mValues.get(position).getParseUser();
        if (parseUser.getString(PARAM_PROFILE_PIC) != null && parseUser.getString(PARAM_PROFILE_PIC).length() > 0)
            Picasso.get().load(parseUser.getString(PARAM_PROFILE_PIC)).centerCrop().resize(500, 500).placeholder(R.drawable.image_placeholder).into(holder.userPic);
        else {
            holder.userPic.setImageResource(R.drawable.image_placeholder);
        }
        String name = parseUser.getString(PARAM_NICK);
//        String age = getUserAge(parseUser);
        String txt = name;
//        if (age != null && age.length() > 0) txt = name + ", " + age;
        String[] split = txt.split(" ");
        holder.ageLocTV.setText(split[0]);

        ParseGeoPoint myGeoPoint = new ParseGeoPoint();
        myGeoPoint.setLatitude(dashboardFragment.getLastBestLocation().getLatitude());
        myGeoPoint.setLongitude(dashboardFragment.getLastBestLocation().getLongitude());
        holder.distanceTV.setText(getDistanceBetweenUser(parseUser));
//        holder.distanceTV.setText(getDistanceBetweenUser(parseUser, myGeoPoint));

        String groupCat = parseUser.getString(PARAM_GROUP_CATEGORY);
        int accStatus = parseUser.getInt(PARAM_ACCOUNT_STATUS);
        boolean isMediaPending = parseUser.getBoolean(PARAM_MEDIA_PENDING);

        holder.statusTV.setVisibility(View.GONE);

        if (groupCat != null && groupCat.equalsIgnoreCase("A")) {
            holder.tagTV.setText("Group A");
            holder.tagTV.setBackgroundResource(R.drawable.bg_group_a);
            holder.statusTV.setVisibility(View.VISIBLE);
        } else if (groupCat != null && groupCat.equalsIgnoreCase("B")) {
            holder.tagTV.setText("Group B");
            holder.tagTV.setBackgroundResource(R.drawable.bg_group_b);
            holder.statusTV.setVisibility(View.VISIBLE);

        } else if (groupCat == null || groupCat.isEmpty() || groupCat.equalsIgnoreCase("N")) {
            holder.statusTV.setVisibility(View.VISIBLE);
            holder.tagTV.setText("New User");
            holder.tagTV.setBackgroundResource(R.drawable.bg_group_n);
        }

        if (accStatus == 0) {
            holder.statusTV.setText("Pending");
            holder.statusTV.setBackgroundResource(R.drawable.bg_status_pending);
        } else if (accStatus==1) {
            holder.statusTV.setText("Active");
            holder.statusTV.setBackgroundResource(R.drawable.bg_status_active);
            if (isMediaPending) {
                holder.statusTV.setText("Media Pending");
                holder.statusTV.setBackgroundResource(R.drawable.bg_status_pending);
                holder.statusTV.setVisibility(View.VISIBLE);
            } else {
                holder.statusTV.setVisibility(View.GONE);
            }
        } else if (accStatus==2) {
            holder.statusTV.setText("Rejected");
            holder.statusTV.setBackgroundResource(R.drawable.bg_status_rejected);
        }


        holder.itemView.setOnClickListener(view -> {
            ActivityUtility.startProfileActivity(activity, KEY_FRAGMENT_ADMIN_PROFILE, false, parseUser);
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setEmpty(View empty) {
        this.empty = empty;
    }

    public void setData(List<MyCustomUser> mValues) {
        this.mValues = new ArrayList<>();
        this.mValues = mValues;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView userPic;
        TextView ageLocTV;
        TextView distanceTV;
        TextView tagTV;
        TextView statusTV;

        public ViewHolder(View view) {
            super(view);
            userPic = view.findViewById(R.id.user_pic);
            ageLocTV = view.findViewById(R.id.age_loc_tv);
            distanceTV = view.findViewById(R.id.distance_tv);
            tagTV = view.findViewById(R.id.tag);
            statusTV = view.findViewById(R.id.status);
        }
    }
}