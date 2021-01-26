package com.app.wingmate.ui.adapters;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.wingmate.R;
import com.app.wingmate.models.QuestionOption;
import com.app.wingmate.models.UserAnswer;
import com.app.wingmate.utils.AppConstants;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ProfileOptionsListAdapter extends RecyclerView.Adapter<ProfileOptionsListAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private Activity context;
    private List<UserAnswer> itemsList;

    public ProfileOptionsListAdapter(Activity context, List<UserAnswer> itemsList) {
        this.inflater = LayoutInflater.from(context);
        this.itemsList = itemsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ProfileOptionsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProfileOptionsListAdapter.ViewHolder(inflater.inflate(R.layout.list_item_profile, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileOptionsListAdapter.ViewHolder holder, final int position) {
        UserAnswer object = itemsList.get(position);

        String sourceString = "";
        String val = "";

        if (object.getDummyUser() != null) {
            val = object.getDummyUser().getString(AppConstants.PARAM_ABOUT_ME);
            if (val != null)
                sourceString = "<b>" + "About Me: " + "</b> " + val;
        } else {
            val = "";
            List<QuestionOption> optionList = object.getOptionsObjArray();
            if (optionList != null && optionList.size() > 0) {
                for (int i = 0; i < optionList.size(); i++) {
                    if (val.length() > 0) {
                        val = val + ", " + optionList.get(i).getOptionTitle();
                    } else {
                        val = optionList.get(i).getOptionTitle();
                    }
                }
            } else {
                val = "N/A";
            }
            sourceString = "<b>" + object.getQuestionId().getShortTitle() + ": " + "</b> " + val;
        }
        holder.valueTV.setText(Html.fromHtml(sourceString));

        holder.itemView.setOnClickListener(view -> {
        });
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public void setData(List<UserAnswer> itemsList) {
//        this.itemsList = new ArrayList<>();
        this.itemsList = itemsList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout root;
        TextView titleTV;
        TextView valueTV;

        ViewHolder(View view) {
            super(view);
            this.root = view.findViewById(R.id.root);
            this.titleTV = view.findViewById(R.id.title);
            this.valueTV = view.findViewById(R.id.value);
        }
    }
}