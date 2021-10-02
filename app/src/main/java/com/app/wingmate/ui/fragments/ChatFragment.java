package com.app.wingmate.ui.fragments;

import static com.app.wingmate.utils.AppConstants.CLASS_NAME_USER;
import static com.app.wingmate.utils.AppConstants.PARAM_OBJECT_ID;
import static com.app.wingmate.utils.AppConstants.USER_CLASS_NAME;
import static com.app.wingmate.utils.CommonKeys.INSTANTS_CLASS_NAME;
import static com.app.wingmate.utils.CommonKeys.INSTANTS_ID;
import static com.app.wingmate.utils.CommonKeys.INSTANTS_RECEIVER;
import static com.app.wingmate.utils.CommonKeys.INSTANTS_SENDER;
import static com.app.wingmate.utils.CommonKeys.KEY_PARSE_USER;
import static com.app.wingmate.utils.CommonKeys.LAST_MESSAGE;
import static com.app.wingmate.utils.CommonKeys.MESSAGES_CLASS_NAME;
import static com.app.wingmate.utils.CommonKeys.MESSAGES_CREATED_AT;
import static com.app.wingmate.utils.CommonKeys.MESSAGES_MESSAGE;
import static com.app.wingmate.utils.CommonKeys.MESSAGES_MESSAGE_ID;
import static com.app.wingmate.utils.CommonKeys.MESSAGES_RECEIVER;
import static com.app.wingmate.utils.CommonKeys.MESSAGES_SENDER;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.ui.adapters.MessagesAdapter;
import com.app.wingmate.utils.AppConstants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends BaseFragment {

    public static final String TAG = ChatFragment.class.getName();
    private DashboardFragment dashboardInstance;
    private View view;
    TextView user_name;
    EditText message;
    CardView send;
    String username,objectID;
    ParseUser parseUser;
    ParseUser userObj;
    RecyclerView messagesList;
    MessagesAdapter messagesAdapter;
    List<ParseObject>messagesArray = new ArrayList<>();
    List<ParseObject>instantsArray = new ArrayList<>();
    int skip = 0;
    Timer refreshTimer = new Timer();
    ImageView back;

    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment contentFragment = new ChatFragment();
        return contentFragment;
    }
    public static ChatFragment newInstance(DashboardFragment dashboardInstance) {
        ChatFragment contentFragment = new ChatFragment();
        contentFragment.dashboardInstance = dashboardInstance;
        return contentFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            getArguments();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        //parseUser = getArguments().getParcelable(KEY_PARSE_USER);
        //username = parseUser.getString(AppConstants.PARAM_NICK);
        //objectID = parseUser.getObjectId();
        objectID = getArguments().getString("userId");
        username = getArguments().getString("username");
        back = view.findViewById(R.id.back);
        user_name = view.findViewById(R.id.username);
        messagesList = view.findViewById(R.id.messagesList);
        messagesList.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        message = view.findViewById(R.id.message);
        send = view.findViewById(R.id.sendBtn);
        if (username!=null && !username.isEmpty()){
            user_name.setText(username);
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        userObj = (ParseUser) ParseObject.createWithoutData(USER_CLASS_NAME, objectID);
        messagesAdapter = new MessagesAdapter(messagesArray);
        messagesList.setAdapter(messagesAdapter);
        queryMessages();
        startRefreshTimer();
        try {
            userObj.fetchIfNeeded().getParseObject(USER_CLASS_NAME);
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!message.getText().toString().isEmpty()){
                        final ParseObject mObj = new ParseObject(MESSAGES_CLASS_NAME);
                        final ParseUser currentUser = ParseUser.getCurrentUser();

                        // Prepare data
                        String pic = currentUser.getString("profilePic");
                        Log.e("picurl",pic);
                        mObj.put(MESSAGES_SENDER, currentUser);
                        mObj.put(MESSAGES_RECEIVER, userObj);
                        mObj.put(MESSAGES_MESSAGE_ID, currentUser.getObjectId() + userObj.getObjectId());
                        mObj.put(MESSAGES_MESSAGE, message.getText().toString());

                        mObj.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e==null){
                                    String lastMessage = message.getText().toString();
                                    message.setText("");
                                    messagesArray.add(mObj);
                                    messagesAdapter.notifyItemInserted(messagesArray.size());
                                    messagesList.scrollToPosition(messagesArray.size()-1);
                                    updateInstants(lastMessage);
                                    //startRefreshTimer();
                                }else{
                                    //Toast.makeText(getContext(),e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            });
        }catch (ParseException parseException){
            parseException.printStackTrace();
        }
        return view;
    }
    void startRefreshTimer() {
        int delay = 8 * 1000;
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override public void run() {
                getActivity().runOnUiThread(new Runnable() { @Override public void run() {
                    skip = 0;
                    //messagesArray = new ArrayList<>();

                    // Call query
                    queryMessages();
                    Log.i(TAG, "REFRESH MESSAGES!");

                }});}}, delay, delay);
    }
    private void updateInstants(String lastMessage) {
        final ParseUser currentUser = ParseUser.getCurrentUser();

        String messId1 = currentUser.getObjectId() + userObj.getObjectId();
        String messId2 = userObj.getObjectId() + currentUser.getObjectId();

        final ParseQuery<ParseObject> query = ParseQuery.getQuery(INSTANTS_CLASS_NAME);
        String[] ids = {messId1, messId2};
        query.whereContainedIn(INSTANTS_ID, Arrays.asList(ids));

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    instantsArray = objects;

                    ParseObject iObj = new ParseObject(INSTANTS_CLASS_NAME);
                    if (instantsArray.size() != 0) { iObj = instantsArray.get(0); }

                    // Prepare data
                    iObj.put(INSTANTS_SENDER, currentUser);
                    iObj.put(INSTANTS_RECEIVER, userObj);
                    iObj.put(INSTANTS_ID, currentUser.getObjectId() + userObj.getObjectId());
                    iObj.put(LAST_MESSAGE,lastMessage);
                    // Saving...
                    iObj.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                //Log.i("log-", "LAST MESS SAVED: " + lastMessage);
                            } else {
                                Toast.makeText(getContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                                //simpleAlert(e.getMessage(), ctx);
                            }}});

                    // error
                } else { //simpleAlert(e.getMessage(), ctx);
                    Toast.makeText(getContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                }}});
    }

    private void queryMessages() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        String messId1 = currentUser.getObjectId() + userObj.getObjectId();
        String messId2 = userObj.getObjectId() + currentUser.getObjectId();

        ParseQuery<ParseObject> query = ParseQuery.getQuery(MESSAGES_CLASS_NAME);
        String ids[] = {messId1,messId2};
        query.whereContainedIn(MESSAGES_MESSAGE_ID, Arrays.asList(ids));
        query.orderByAscending(MESSAGES_CREATED_AT);
        query.setSkip(skip);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                messagesArray.clear();
                messagesArray.addAll(objects);
                if (objects.size() == 100) {
                    skip = skip + 100;
                    queryMessages();
                }
                messagesAdapter.notifyDataSetChanged();
                /*new Runnable(){
                    @Override
                    public void run() {
                        messagesList.scrollToPosition(messagesArray.size());
                    }
                };*/
                messagesList.scrollToPosition(messagesArray.size()-1);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (refreshTimer != null) {
            refreshTimer.cancel();
            refreshTimer = null;
        }
    }
}