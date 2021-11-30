package com.app.wingmate.ui.adapters;

import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.AppConstants.USER_CLASS_NAME;
import static com.app.wingmate.utils.CommonKeys.KEY_CHANGE_PASSWORD;
import static com.app.wingmate.utils.CommonKeys.KEY_CONTACT_US_FRAGMENT;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_ABOUT_US;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_HELP;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_NOTIFICATION_SETTINGS;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_PRIVACY_POLICY;
import static com.app.wingmate.utils.CommonKeys.KEY_TERMS_OF_CONDITIONS;
import static com.app.wingmate.utils.Utilities.showToast;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.app.wingmate.R;
import com.app.wingmate.models.SettingsModel;
import com.app.wingmate.ui.fragments.SettingsFragment;
import com.app.wingmate.utils.ActivityUtility;
import com.google.android.material.transition.Hold;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder>{

    List<SettingsModel> itemsList;
    Activity context;
    Fragment fragment;
    int listSize;
    String versionNo= "";
    List<String> dataList = new ArrayList<>();
    boolean isMessageDisabled = false;
    boolean isLikeDisabled = false;
    boolean isUnsubscribed = false;

    public SettingsAdapter(Activity context, List<String> dataList, Fragment fragment, String versionNo, boolean isMessageDisabled, boolean isLikeDisabled, boolean isUnsubscribed){
        this.context = context;
        this.dataList = dataList;
        this.fragment = fragment;
        this.versionNo = versionNo;
        this.isMessageDisabled = isMessageDisabled;
        this.isLikeDisabled = isLikeDisabled;
        this.isUnsubscribed = isUnsubscribed;
    }

    @NonNull
    @Override
    public SettingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.settings_item,parent,false);
        return new SettingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsViewHolder holder, int position) {
        if (dataList.get(position).equalsIgnoreCase("change password")){
            holder.layoutTwo.setVisibility(View.GONE);
            holder.layoutOne.setVisibility(View.VISIBLE);
            holder.versionText.setVisibility(View.GONE);
            holder.itemTitle.setVisibility(View.VISIBLE);
            holder.div.setVisibility(View.VISIBLE);
            holder.itemTitle.setText("Change Password");
            holder.switchCompat.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityUtility.startChangePasswordFragment(context,KEY_CHANGE_PASSWORD);
                }
            });

        }else if (dataList.get(position).equalsIgnoreCase("contact us")){
            holder.layoutTwo.setVisibility(View.GONE);
            holder.layoutOne.setVisibility(View.VISIBLE);
            holder.versionText.setVisibility(View.GONE);
            holder.itemTitle.setVisibility(View.VISIBLE);
            holder.div.setVisibility(View.VISIBLE);
            holder.itemTitle.setText("Contact us");
            holder.switchCompat.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityUtility.startContactUsFragment(context,KEY_CONTACT_US_FRAGMENT);
                }
            });
        }else if (dataList.get(position).equalsIgnoreCase("help")){
            holder.layoutTwo.setVisibility(View.GONE);
            holder.layoutOne.setVisibility(View.VISIBLE);
            holder.versionText.setVisibility(View.GONE);
            holder.itemTitle.setVisibility(View.VISIBLE);
            holder.div.setVisibility(View.VISIBLE);
            holder.itemTitle.setText("Help");
            holder.switchCompat.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityUtility.startHelpFragment(context,KEY_FRAGMENT_HELP);
                }
            });
        }else if (dataList.get(position).equalsIgnoreCase("About us")){
            holder.layoutTwo.setVisibility(View.GONE);
            holder.layoutOne.setVisibility(View.VISIBLE);
            holder.versionText.setVisibility(View.GONE);
            holder.itemTitle.setVisibility(View.VISIBLE);
            holder.div.setVisibility(View.VISIBLE);
            holder.itemTitle.setText("About Us");
            holder.switchCompat.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityUtility.startAboutUsFragment(context,KEY_FRAGMENT_ABOUT_US);
                }
            });
        }else if (dataList.get(position).equalsIgnoreCase("Terms of use")){
            holder.layoutTwo.setVisibility(View.GONE);
            holder.layoutOne.setVisibility(View.VISIBLE);
            holder.versionText.setVisibility(View.GONE);
            holder.itemTitle.setVisibility(View.VISIBLE);
            holder.div.setVisibility(View.VISIBLE);
            holder.itemTitle.setText("Terms of use");
            holder.switchCompat.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityUtility.startTermsOfConditions(context,KEY_TERMS_OF_CONDITIONS);
                }
            });
        }else if (dataList.get(position).equalsIgnoreCase("Privacy policy")){
            holder.layoutTwo.setVisibility(View.GONE);
            holder.layoutOne.setVisibility(View.VISIBLE);
            holder.versionText.setVisibility(View.GONE);
            holder.itemTitle.setVisibility(View.VISIBLE);
            holder.div.setVisibility(View.VISIBLE);
            holder.itemTitle.setText("Privacy policy");
            holder.switchCompat.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityUtility.startPrivacyPolicy(context,KEY_FRAGMENT_PRIVACY_POLICY);
                }
            });
        }else if (dataList.get(position).equalsIgnoreCase("Notifications")){
            holder.layoutTwo.setVisibility(View.GONE);
            holder.layoutOne.setVisibility(View.VISIBLE);
            holder.versionText.setVisibility(View.GONE);
            holder.itemTitle.setVisibility(View.VISIBLE);
            holder.div.setVisibility(View.VISIBLE);
            holder.itemTitle.setText("Notification");
            holder.switchCompat.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityUtility.startNotificationSettings(context,KEY_FRAGMENT_NOTIFICATION_SETTINGS);
                }
            });
        }else if (dataList.get(position).equalsIgnoreCase("Disable messages")){
            holder.layoutTwo.setVisibility(View.GONE);
            holder.layoutOne.setVisibility(View.VISIBLE);
            holder.versionText.setVisibility(View.GONE);
            holder.itemTitle.setVisibility(View.VISIBLE);
            holder.div.setVisibility(View.VISIBLE);
            holder.itemTitle.setText("Disable messages");
            holder.switchCompat.setVisibility(View.VISIBLE);
            holder.switchCompat.setChecked(isMessageDisabled);
            holder.switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    ((SettingsFragment)fragment).disableMessages(b);
                }
            });
        }else if (dataList.get(position).equalsIgnoreCase("Disable likes")){
            holder.layoutTwo.setVisibility(View.GONE);
            holder.layoutOne.setVisibility(View.VISIBLE);
            holder.versionText.setVisibility(View.GONE);
            holder.itemTitle.setVisibility(View.VISIBLE);
            holder.div.setVisibility(View.VISIBLE);
            holder.itemTitle.setText("Disable likes");
            holder.switchCompat.setVisibility(View.VISIBLE);
            holder.switchCompat.setChecked(isLikeDisabled);
            holder.switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    ((SettingsFragment)fragment).disableLikes(b);
                }
            });
        }else if (dataList.get(position).equalsIgnoreCase("Change saved credit card")){
            holder.layoutTwo.setVisibility(View.GONE);
            holder.layoutOne.setVisibility(View.VISIBLE);
            holder.versionText.setVisibility(View.GONE);
            holder.itemTitle.setVisibility(View.VISIBLE);
            holder.div.setVisibility(View.VISIBLE);
            holder.itemTitle.setText("Change saved credit card");
            holder.switchCompat.setVisibility(View.GONE);
        }else if (dataList.get(position).equalsIgnoreCase("unsubscribe")){
            holder.layoutTwo.setVisibility(View.GONE);
            holder.layoutOne.setVisibility(View.VISIBLE);
            holder.versionText.setVisibility(View.GONE);
            holder.itemTitle.setVisibility(View.VISIBLE);
            holder.div.setVisibility(View.VISIBLE);
            holder.itemTitle.setText("Unsubscribe");
            holder.switchCompat.setVisibility(View.VISIBLE);
            holder.switchCompat.setChecked(isUnsubscribed);
            holder.switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    ((SettingsFragment)fragment).unSubscribe(b);
                }
            });
        }else if (dataList.get(position).equalsIgnoreCase("Go to admin section")){
            holder.layoutTwo.setVisibility(View.GONE);
            holder.layoutOne.setVisibility(View.VISIBLE);
            holder.versionText.setVisibility(View.GONE);
            holder.itemTitle.setVisibility(View.VISIBLE);
            holder.div.setVisibility(View.VISIBLE);
            holder.itemTitle.setText("Go to Admin section");
            holder.switchCompat.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((SettingsFragment)fragment).call(2);
                }
            });
        }else if (dataList.get(position).equalsIgnoreCase("Pay Now")){
            holder.layoutTwo.setVisibility(View.GONE);
            holder.layoutOne.setVisibility(View.VISIBLE);
            holder.versionText.setVisibility(View.GONE);
            holder.itemTitle.setVisibility(View.VISIBLE);
            holder.div.setVisibility(View.VISIBLE);
            holder.itemTitle.setText("Pay Now");
            holder.switchCompat.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((SettingsFragment)fragment).call(3);
                }
            });
        }else{
            holder.versionText.setVisibility(View.VISIBLE);
            holder.itemTitle.setVisibility(View.GONE);
            holder.switchCompat.setVisibility(View.GONE);
            holder.div.setVisibility(View.GONE);
            holder.versionText.setText(versionNo);
            holder.layoutTwo.setVisibility(View.VISIBLE);
            holder.layoutOne.setVisibility(View.GONE);
            holder.logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((SettingsFragment)fragment).call(1);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class SettingsViewHolder extends RecyclerView.ViewHolder{
        TextView itemTitle;
        SwitchCompat switchCompat;
        TextView versionText;
        View div;
        RelativeLayout layoutOne;
        LinearLayout layoutTwo;
        Button logout;

        public SettingsViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTitle = itemView.findViewById(R.id.settingItem);
            switchCompat = itemView.findViewById(R.id.choice);
            versionText = itemView.findViewById(R.id.version_tv);
            div = itemView.findViewById(R.id.div);

            layoutOne = itemView.findViewById(R.id.layoutOne);
            layoutTwo = itemView.findViewById(R.id.layoutTwo);
            logout = itemView.findViewById(R.id.logout);
        }
    }

    public static void simpleAlert(String mess, Context activity) {
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(activity);
        alert.setMessage(mess)
                .setTitle(R.string.app_name)
                .setPositiveButton("OK", null)
                .setIcon(R.mipmap.ic_launcher);
        alert.create().show();
    }
}
