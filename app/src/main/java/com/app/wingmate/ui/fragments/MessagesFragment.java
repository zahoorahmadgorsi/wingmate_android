package com.app.wingmate.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.models.InstantsList;
import com.app.wingmate.ui.activities.MainActivity;
import com.app.wingmate.ui.adapters.ChatsAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.AppConstants.PARAM_PROFILE_PIC;
import static com.app.wingmate.utils.AppConstants.USER_CLASS_NAME;
import static com.app.wingmate.utils.CommonKeys.INSTANTS_CLASS_NAME;
import static com.app.wingmate.utils.CommonKeys.INSTANTS_ID;
import static com.app.wingmate.utils.CommonKeys.INSTANTS_UPDATED_AT;
import static com.app.wingmate.utils.CommonKeys.LAST_MESSAGE;
import static com.app.wingmate.utils.DateUtils.timeAgoSinceDate;

import java.util.ArrayList;
import java.util.List;

public class MessagesFragment extends BaseFragment {

    public static final String TAG = MessagesFragment.class.getName();

    private DashboardFragment dashboardInstance;

    Unbinder unbinder;
    RecyclerView chatList;
    SwipeRefreshLayout pullToRefresh;
    List<ParseObject> instantsArray = new ArrayList<>();
    int skip = 0;
    ChatsAdapter chatsAdapter;
    List<InstantsList> instantsLists = new ArrayList<>();
    public MessagesFragment() {

    }

    public static MessagesFragment newInstance() {
        MessagesFragment contentFragment = new MessagesFragment();
        return contentFragment;
    }

    public static MessagesFragment newInstance(DashboardFragment dashboardInstance) {
        MessagesFragment contentFragment = new MessagesFragment();
        contentFragment.dashboardInstance = dashboardInstance;
        return contentFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        skip = 0;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        unbinder = ButterKnife.bind(this, view);
        chatList = view.findViewById(R.id.chatList);
        pullToRefresh = view.findViewById(R.id.pullToRefresh);
        chatsAdapter = new ChatsAdapter(getActivity(),instantsArray);
        chatList.setAdapter(chatsAdapter);
        chatList.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        //queryInstants();
        showProgress();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(false);
                showProgress();
                queryInstants();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setProfileImage(ParseUser.getCurrentUser().getString(PARAM_PROFILE_PIC));
//        dashboardInstance.performUserUpdateAction();
        queryInstants();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({})
    public void onViewClicked(View v) {

    }

    private void queryInstants() {
        ParseUser currentUser = ParseUser.getCurrentUser();

        // Query
        ParseQuery<ParseObject> query = ParseQuery.getQuery(INSTANTS_CLASS_NAME);
        query.include(USER_CLASS_NAME);
        query.whereContains(INSTANTS_ID, currentUser.getObjectId());
        query.orderByDescending("msgCreateAt");
        query.setSkip(skip);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    instantsArray.clear();
                    instantsArray.addAll(objects);
                    if (objects.size() == 100) {
                        skip = skip + 100;
                        queryInstants();
                    }
                    chatsAdapter.notifyDataSetChanged();
                    dismissProgress();
                    // error
                } else {
                    Toast.makeText(getContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                }}});
    }
}