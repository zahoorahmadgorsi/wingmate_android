package com.app.wingmate.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.base.BaseInteractor;
import com.app.wingmate.base.BasePresenter;
import com.app.wingmate.base.BaseView;
import com.app.wingmate.ui.adapters.QuestionOptionsListAdapter;
import com.app.wingmate.models.Question;
import com.app.wingmate.models.UserAnswer;
import com.app.wingmate.ui.dialogs.OptionsSelectorDialog;
import com.parse.FindCallback;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.AppConstants.CLASS_NAME_USER_ANSWER;
import static com.app.wingmate.utils.AppConstants.MANDATORY;
import static com.app.wingmate.utils.AppConstants.PARAM_OPTIONS_OBJ_ARRAY;
import static com.app.wingmate.utils.AppConstants.PARAM_USER_ID;
import static com.app.wingmate.utils.AppConstants.TAG_SEARCH;

public class SearchFragment extends BaseFragment implements BaseView, OptionsSelectorDialog.OptionsSelectorDialogClickListener {

    public static final String TAG = SearchFragment.class.getName();

    private BasePresenter presenter;

    private DashboardFragment dashboardInstance;

    Unbinder unbinder;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private QuestionOptionsListAdapter adapter;
    private GridLayoutManager gridLayoutManager;

    private List<Question> questions;
    private int selectedIndex = -1;
    private boolean onCrate = true;

    public SearchFragment() {

    }

    public static SearchFragment newInstance() {
        SearchFragment contentFragment = new SearchFragment();
        return contentFragment;
    }

    public static SearchFragment newInstance(DashboardFragment dashboardInstance) {
        SearchFragment contentFragment = new SearchFragment();
        contentFragment.dashboardInstance = dashboardInstance;
        return contentFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new BasePresenter(this, new BaseInteractor());

        questions = new ArrayList<>();
        gridLayoutManager = new GridLayoutManager(getActivity(), 1);

        adapter = new QuestionOptionsListAdapter(getActivity(), this, questions);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);

        showProgress();
        presenter.querySearchQuestions(getContext(), MANDATORY);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_search})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_search:

                showProgress();

                ParseQuery query = ParseQuery.getQuery(CLASS_NAME_USER_ANSWER);

                query.include(PARAM_OPTIONS_OBJ_ARRAY);
                query.include(PARAM_USER_ID);

                query.whereContainedIn(PARAM_OPTIONS_OBJ_ARRAY, questions.get(0).getUserAnswer().getOptionsObjArray());
                query.whereContainedIn(PARAM_OPTIONS_OBJ_ARRAY, questions.get(1).getUserAnswer().getOptionsObjArray());
                query.whereContainedIn(PARAM_OPTIONS_OBJ_ARRAY, questions.get(2).getUserAnswer().getOptionsObjArray());

                query.setLimit(1000);
                query.findInBackground((FindCallback<UserAnswer>) (objects, e) -> {
                    if (e == null) {
                        String results = "";
                        for (int i = 0; i < objects.size(); i++) {
                            System.out.println(objects.get(i).getUserId().getObjectId());
                            System.out.println(objects.get(i).getUserId().getEmail());
                            results = results + objects.get(i).getUserId().getUsername() + "\n\n";
//                            results = results + objects.get(i).getUserId().getObjectId() + ": " + objects.get(i).getUserId().getUsername() + "\n\n";
                        }
                        AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
                        dialog.setTitle("Alert")
                                .setIcon(R.drawable.app_heart)
                                .setMessage(objects.size() + " Results Found\n\n" + results)
                                .setNegativeButton("Ok", (dialoginterface, i) -> dialoginterface.cancel())
                                .show();
                    } else {
                        System.out.println("====ee====" + e.getMessage());
                    }
                    dismissProgress();
                });


                break;
        }

    }

    @Override
    public void setQuestionsResponseSuccess(List<Question> questions) {
        dismissProgress();
        this.questions = questions;
        adapter.setData(this.questions);
        adapter.notifyDataSetChanged();
    }

    public void showOptionSelectionDialog(Question question, int index, String tag) {
        selectedIndex = index;
        OptionsSelectorDialog dialog = OptionsSelectorDialog.newInstance(this, getActivity(), question, tag, TAG_SEARCH);
        dialog.show(getActivity().getSupportFragmentManager(), "selection_dialog");
    }

    @Override
    public void onSelectorBackButtonClick(DialogFragment dialog) {
        dialog.dismiss();
        selectedIndex = -1;
    }

    @Override
    public void onSelectorFieldsUpdateClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    @Override
    public void onSelectorGenderUpdateClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    @Override
    public void onSelectorApplyClick(DialogFragment dialog, Question question) {
        dialog.dismiss();
        if (selectedIndex > -1) {
            questions.set(selectedIndex, question);
            adapter.setData(questions);
            adapter.notifyDataSetChanged();
        }
        selectedIndex = -1;
    }
}