package com.app.wingmate.ui.fragments;

import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.AppConstants.USER_CLASS_NAME;
import static com.app.wingmate.utils.Utilities.showToast;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.ui.activities.MainActivity;
import com.app.wingmate.utils.SharedPrefers;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class NotificationsSettingsFragment extends BaseFragment{

    View view;
    public static final String TAG = NotificationsSettingsFragment.class.getName();
    Button save;
    ImageView back;
    SwitchCompat notificationSwitch;
    SwitchCompat chatNotificationSwitch;
    boolean isChatNotificationEnabled = false;
    boolean isNotificationAlertEnabled = true;
    boolean isNotificationAlertModified = false;
    boolean isChatNotificationModified = false;


    public NotificationsSettingsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.notification_settings_layout,container,false);
        init(view);
        setListeners();
        return view;
    }

    private void setListeners() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).onBackPressed();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNotificationAlertModified){
                    SharedPrefers.saveBoolean(getContext(),"isNotificationAlertEnabled",isNotificationAlertEnabled);
                    isNotificationAlertModified = false;
                }
                if (isChatNotificationModified){
                    showProgress();
                    ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery(USER_CLASS_NAME);
                    parseQuery.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
                    parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            object.put("allowChatNotification",isChatNotificationEnabled);
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e==null){
                                        ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseObject>() {
                                            @Override
                                            public void done(ParseObject object, ParseException e) {
                                                if (e==null){
                                                    isChatNotificationModified = false;
                                                    dismissProgress();
                                                }else{
                                                    dismissProgress();
                                                    showToast(getActivity(), getContext(), e.getMessage(), ERROR);
                                                }
                                            }
                                        });
                                    }else{
                                        dismissProgress();
                                        showToast(getActivity(), getContext(), e.getMessage(), ERROR);
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isNotificationAlertEnabled = b;
                isNotificationAlertModified = true;
                //SharedPrefers.saveBoolean(getContext(),"isNotificationAlertEnabled",b);
            }
        });

        chatNotificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isChatNotificationEnabled = b;
                isChatNotificationModified = true;
         /*       showProgress();
                ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery(USER_CLASS_NAME);
                parseQuery.whereEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
                parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        object.put("allowChatNotification",b);
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e==null){
                                    dismissProgress();
                                }else{
                                    dismissProgress();
                                    showToast(getActivity(), getContext(), e.getMessage(), ERROR);
                                }
                            }
                        });
                    }
                });*/
            }
        });
    }

    private void init(View view) {
        save = view.findViewById(R.id.save);
        back = view.findViewById(R.id.back);
        notificationSwitch = view.findViewById(R.id.notification_alert_choice);
        chatNotificationSwitch = view.findViewById(R.id.chatNotificationChoice);
    }


    @Override
    public void onResume() {
        super.onResume();
        isChatNotificationEnabled = ParseUser.getCurrentUser().getBoolean("allowChatNotification");
        isNotificationAlertEnabled = SharedPrefers.getBoolean(getContext(),"isNotificationAlertEnabled",true);
        chatNotificationSwitch.setChecked(isChatNotificationEnabled);
        notificationSwitch.setChecked(isNotificationAlertEnabled);
    }
}
