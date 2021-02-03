package com.app.wingmate.dashboard.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.dashboard.DashboardFragment;
import com.app.wingmate.dashboard.DashboardInteractor;
import com.app.wingmate.dashboard.DashboardPresenter;
import com.app.wingmate.dashboard.DashboardView;
import com.app.wingmate.models.Question;
import com.app.wingmate.ui.adapters.QuestionOptionsSelectorAdapter;
import com.app.wingmate.ui.dialogs.OptionsSelectorDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.AppConstants.TAG_SEARCH;

public class SearchFragment extends BaseFragment implements DashboardView, OptionsSelectorDialog.OptionsSelectorDialogClickListener {

    public static final String TAG = SearchFragment.class.getName();

    private DashboardPresenter presenter;

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

        presenter = new DashboardPresenter(this, new DashboardInteractor());

        questions = new ArrayList<>();
        gridLayoutManager = new GridLayoutManager(getActivity(), 1);

        adapter = new QuestionOptionsListAdapter(getActivity(), this, questions);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);

        showProgress();
        presenter.queryQuestions(getContext());
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

    @OnClick({})
    public void onViewClicked(View v) {

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