package com.app.wingmate.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.wingmate.R;
import com.app.wingmate.models.Fans;
import com.app.wingmate.utils.ActivityUtility;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import me.tankery.lib.circularseekbar.CircularSeekBar;

import static com.app.wingmate.utils.AppConstants.PARAM_NICK;
import static com.app.wingmate.utils.AppConstants.PARAM_PROFILE_PIC;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PROFILE;
import static com.app.wingmate.utils.Utilities.getDistanceBetweenUser;
import static com.app.wingmate.utils.Utilities.getMatchPercentage;
import static com.app.wingmate.utils.Utilities.getUserAge;

public class MyFansAdapter extends RecyclerView.Adapter<MyFansAdapter.ViewHolder> {

    Activity activity;
    Context context;
    private View empty;
    private List<Fans> mValues;

    public MyFansAdapter(Context context, List<Fans> mValues) {
        this.context = context;
        this.activity = (Activity) context;
        this.mValues = mValues;
    }

    @Override
    public MyFansAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_user_new, parent, false);
        return new MyFansAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyFansAdapter.ViewHolder holder, final int position) {

        final ParseUser parseUser = mValues.get(position).getFromUser();
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

        holder.distanceTV.setText(getDistanceBetweenUser(parseUser));
        int percent = getMatchPercentage(parseUser);
        holder.matchPercentTV.setText(percent + "%");
        holder.matchProgress.setMax(100);
        holder.matchProgress.setProgress(percent);

        if (mValues.get(position).getMySelectedType()!=null && mValues.get(position).getMySelectedType().equals(mValues.get(position).getFanType())) {
            holder.byMeAswell.setVisibility(View.VISIBLE);
        } else {
            holder.byMeAswell.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(view -> {
            ActivityUtility.startProfileActivity(activity, KEY_FRAGMENT_PROFILE, false, parseUser);
        });
    }

    @Override
    public int getItemCount() {
//        if (empty != null) empty.setVisibility(mValues.size() == 0 ? View.VISIBLE : View.GONE);
        return mValues.size();
    }

    public void setEmpty(View empty) {
        this.empty = empty;
    }

    public void setData(List<Fans> mValues) {
        this.mValues = new ArrayList<>();
        this.mValues = mValues;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView userPic;
        ImageView byMeAswell;
        TextView ageLocTV;
        TextView matchPercentTV;
        TextView distanceTV;
        CircularSeekBar matchProgress;

        public ViewHolder(View view) {
            super(view);
            userPic = view.findViewById(R.id.user_pic);
            byMeAswell = view.findViewById(R.id.by_me_aswell);
            ageLocTV = view.findViewById(R.id.age_loc_tv);
            matchPercentTV = view.findViewById(R.id.match_percent_tv);
            distanceTV = view.findViewById(R.id.distance_tv);
            matchProgress = view.findViewById(R.id.match_progress);
        }
    }
}
