package com.app.wingmate.ui.fragments;

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
import com.app.wingmate.base.BaseInteractor;
import com.app.wingmate.base.BasePresenter;
import com.app.wingmate.base.BaseView;
import com.app.wingmate.models.Question;
import com.app.wingmate.models.TermsConditions;
import com.app.wingmate.models.UserAnswer;
import com.app.wingmate.models.UserProfilePhotoVideo;
import com.app.wingmate.ui.adapters.QuestionOptionsSelectorAdapter;
import com.app.wingmate.ui.dialogs.OptionsSelectorDialog;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.AppConstants.TAG_PROFILE_EDIT;

public class EditProfileFragment extends BaseFragment implements BaseView, OptionsSelectorDialog.OptionsSelectorDialogClickListener {

    public static final String TAG = EditProfileFragment.class.getName();

    private BasePresenter presenter;

    Unbinder unbinder;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private QuestionOptionsSelectorAdapter adapter;
    private GridLayoutManager gridLayoutManager;

    private List<Question> questions;
    private List<UserAnswer> userAnswers;
    private int selectedIndex = -1;
    public boolean isAnyUpdate = false;

    public EditProfileFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new BasePresenter(this, new BaseInteractor());

        questions = new ArrayList<>();
        gridLayoutManager = new GridLayoutManager(getActivity(), 1);

        adapter = new QuestionOptionsSelectorAdapter(getActivity(), this, questions);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);

//        userAnswers = getActivity().getIntent().getParcelableArrayListExtra(KEY_USER_ANSWERS);
        if (userAnswers == null) userAnswers = new ArrayList<>();

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

    @OnClick({R.id.btn_back,})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                getActivity().onBackPressed();
                break;
        }
    }

    @Override
    public void setTermsResponseSuccess(List<TermsConditions> termsConditions) {

    }

    @Override
    public void setQuestionsResponseSuccess(List<Question> qa) {
        questions = new ArrayList<>();
        if (qa != null && qa.size() > 0) {
            questions = qa;
        }
        presenter.queryUserAnswers(getContext(), ParseUser.getCurrentUser());
    }

    @Override
    public void setUserAnswersResponseSuccess(List<UserAnswer> ua) {
        userAnswers = ua;
        Collections.sort(userAnswers, (lhs, rhs) -> Integer.compare(lhs.getQuestionId().getProfileDisplayOrder(), rhs.getQuestionId().getProfileDisplayOrder()));
        if (userAnswers != null && userAnswers.size() > 0) {
            for (int i = 0; i < questions.size(); i++) {
                for (int k = 0; k < userAnswers.size(); k++) {
                    if (questions.get(i).getObjectId().equalsIgnoreCase(userAnswers.get(k).getQuestionId().getObjectId())) {
                        questions.get(i).setUserAnswer(userAnswers.get(k));
                    }
                }
            }
        }
        adapter.setData(questions);
        adapter.notifyDataSetChanged();
        dismissProgress();
    }

    @Override
    public void setUserPhotosVideoResponseSuccess(List<UserProfilePhotoVideo> userProfilePhotoVideos) {

    }

    public void editProfileFields(String tag) {
        showOptionSelectionDialog(new Question(), -1, tag);
//        ActivityUtility.startEditProfileFieldsActivity(getActivity(), KEY_FRAGMENT_EDIT_PROFILE_TEXT_FIELDS, tag);
    }

    public void showOptionSelectionDialog(Question question, int index, String tag) {
        selectedIndex = index;
        OptionsSelectorDialog dialog = OptionsSelectorDialog.newInstance(this, getActivity(), question, tag, TAG_PROFILE_EDIT);
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
        if (adapter != null) adapter.notifyDataSetChanged();
        isAnyUpdate = true;
    }

    @Override
    public void onSelectorGenderUpdateClick(DialogFragment dialog) {
        dialog.dismiss();
        if (adapter != null) adapter.notifyDataSetChanged();
        isAnyUpdate = true;
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
        isAnyUpdate = true;
    }
}