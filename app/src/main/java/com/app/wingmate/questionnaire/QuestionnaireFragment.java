package com.app.wingmate.questionnaire;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.wingmate.R;
import com.app.wingmate.base.BaseFragment;
import com.app.wingmate.models.Question;
import com.app.wingmate.models.QuestionOption;
import com.app.wingmate.models.UserAnswer;
import com.app.wingmate.ui.activities.MainActivity;
import com.app.wingmate.ui.adapters.OptionsListAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.wingmate.utils.AppConstants.MANDATORY;
import static com.app.wingmate.utils.AppConstants.NATIONALITY_QUESTION_OBJECT_ID;
import static com.app.wingmate.utils.AppConstants.OPTIONAL;
import static com.app.wingmate.utils.AppConstants.PARAM_MANDATORY_QUESTIONNAIRE_FILLED;
import static com.app.wingmate.utils.AppConstants.PARAM_OPTIONAL_QUESTIONNAIRE_FILLED;
import static com.app.wingmate.utils.AppConstants.PARAM_OPTIONS_IDS;
import static com.app.wingmate.utils.AppConstants.PARAM_OPTIONS_OBJ_ARRAY;
import static com.app.wingmate.utils.AppConstants.PARAM_OPTIONS_RELATION;
import static com.app.wingmate.utils.AppConstants.PARAM_QUESTION_ID;
import static com.app.wingmate.utils.AppConstants.PARAM_USER_ID;
import static com.app.wingmate.utils.AppConstants.SUCCESS;
import static com.app.wingmate.utils.CommonKeys.KEY_QUESTION_TYPE;
import static com.app.wingmate.utils.Utilities.showToast;

public class QuestionnaireFragment extends BaseFragment implements QuestionnaireView {

    public static final String TAG = QuestionnaireFragment.class.getName();

    Unbinder unbinder;

    @BindView(R.id.placeholder_tv)
    TextView placeholderTV;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.btn_continue)
    Button saveAndContinueBtn;
    @BindView(R.id.btn_back)
    ImageView backBtn;
    @BindView(R.id.et_search)
    EditText searchET;

    private OptionsListAdapter adapter;
    private GridLayoutManager gridLayoutManager;

    private List<Question> questionnaire;
    private List<QuestionOption> allOptionsList = new ArrayList<>();
    private List<QuestionOption> filteredOptionsList;
    public UserAnswer currentUserAnswer;
    public List<String> currentSelectedOptions;

    private int questionNo = 0;
    private int totalNoOfQuestions = 0;
    private int selectedQuestionIndex = 0;
    private int totalNoOfMandatoryQuestions = 0;
    private boolean isChange = false;

    private String questionType = MANDATORY;

    private MainActivity mainActivity;

    private QuestionnairePresenter presenter;

    public QuestionnaireFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        mainActivity = ((MainActivity) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_questionnaire, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new QuestionnairePresenter(this, new QuestionnaireInteractor());

        initOptionsListView();
        setSkipBtnListener();

        questionType = requireActivity().getIntent().getExtras().getString(KEY_QUESTION_TYPE, MANDATORY);

        showProgress();
        presenter.queryQuestions(getContext(), questionType);
    }

    private void initOptionsListView() {
        disableContinueBtn();

        searchET.setVisibility(View.GONE);
        backBtn.setVisibility(View.VISIBLE);

        filteredOptionsList = new ArrayList<>();
        currentSelectedOptions = new ArrayList<>();

        gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        adapter = new OptionsListAdapter(getActivity(), this, filteredOptionsList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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

    @OnClick({R.id.btn_continue, R.id.btn_back})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_continue:
                moveNext();
                break;
            case R.id.btn_back:
                moveBack();
                break;
            default:
                break;
        }
    }

    @Override
    public void setQuestionResponseSuccess(List<Question> objects) {
        questionnaire = objects;
        questionNo = 1;
        totalNoOfQuestions = questionnaire.size();
        if (questionType.equals(MANDATORY)) totalNoOfMandatoryQuestions = totalNoOfQuestions;
        else totalNoOfMandatoryQuestions = 0;
        selectedQuestionIndex = 0;
        presenter.queryQuestionOptions(getContext(), questionnaire.get(selectedQuestionIndex));
    }

    @Override
    public void setOptionsResponseSuccess(List<QuestionOption> objects) {
        allOptionsList = objects;
        filteredOptionsList = objects;
        questionnaire.get(selectedQuestionIndex).setOptions(objects);
        presenter.queryUserAnswers(getContext(), questionnaire.get(selectedQuestionIndex));
        System.out.println("==tr size1===" + allOptionsList.size());
    }

    @Override
    public void setUserAnswersResponseSuccess(UserAnswer object) {
        if (object != null) {
            questionnaire.get(selectedQuestionIndex).setUserAnswer(object);
//            currentUserAnswer = object;
        }
        // boolean isMultiSelect = questionnaire.get(selectedQuestionIndex).getBoolean(PARAM_MULTIPLE_SELECTION);
        boolean isMultiSelect = !(questionType.equals(MANDATORY));
        loadQuestion(questionnaire.get(selectedQuestionIndex), isMultiSelect);
        dismissProgress();
    }

    @Override
    public void setUserAnswersResponseError(ParseException e) {
        // boolean isMultiSelect = questionnaire.get(selectedQuestionIndex).getBoolean(PARAM_MULTIPLE_SELECTION);
        boolean isMultiSelect = !(questionType.equals(MANDATORY));
        loadQuestion(questionnaire.get(selectedQuestionIndex), isMultiSelect);
        dismissProgress();
    }

    private void setSkipBtnListener() {
        mainActivity.getSkipTV().setOnClickListener(v -> skipNext());
    }

    private void disableContinueBtn() {
        saveAndContinueBtn.setSelected(false);
        saveAndContinueBtn.setEnabled(false);
        saveAndContinueBtn.setClickable(false);
        saveAndContinueBtn.setAlpha(0.5f);
    }

    private void enableContinueBtn() {
        saveAndContinueBtn.setSelected(true);
        saveAndContinueBtn.setEnabled(true);
        saveAndContinueBtn.setClickable(true);
        saveAndContinueBtn.setAlpha(1.0f);
    }

    private void loadQuestion(Question question, boolean isMultiSelection) {
        isChange = false;
        if (isMultiSelection) placeholderTV.setText(R.string.sel_multi);
        else placeholderTV.setText(R.string.sel_one);

        disableContinueBtn();
        mainActivity.showScreenTitle();
        mainActivity.setScreenTitle(question.getQuestionTitle());

        filteredOptionsList = new ArrayList<>();
        filteredOptionsList.addAll(question.getOptions());

        currentSelectedOptions = new ArrayList<>();
        currentUserAnswer = question.getUserAnswer();

        if (question.getUserAnswer() != null && question.getUserAnswer().getSelectedOptionIds() != null && question.getUserAnswer().getSelectedOptionIds().size() > 0)
            currentSelectedOptions = question.getUserAnswer().getSelectedOptionIds();

        if (questionType.equals(MANDATORY)) mainActivity.hideSkip();
        else mainActivity.showSkip();
        mainActivity.showStepView();
        mainActivity.setStepViewStepTVAndProgress(questionNo, totalNoOfQuestions);

        if (question.getObjectId().equals(NATIONALITY_QUESTION_OBJECT_ID)
                || question.getQuestionTitle().equals(getString(R.string.question_country))
                || question.getShortTitle().contains(getString(R.string.nationality))
        ) {
            adapter.setCountrySelection(true);
//            placeholderTV.setText(R.string.sel_one_country);
            searchET.setVisibility(View.VISIBLE);
            if (!searchET.getText().toString().isEmpty())
                searchQuery(searchET.getText().toString());
        } else {
            adapter.setCountrySelection(false);
            searchET.setVisibility(View.GONE);
        }

        adapter.setData(filteredOptionsList);
        adapter.setMultiSelection(isMultiSelection);
        adapter.notifyDataSetChanged();
        if (currentSelectedOptions.size() > 0) enableContinueBtn();
    }

    public void selectOption(int index, boolean isMultiSelect) {
        isChange = true;
        String objectId = filteredOptionsList.get(index).getObjectId();
        if (!isMultiSelect) currentSelectedOptions = new ArrayList<>();
        if (currentSelectedOptions.contains(objectId))
            currentSelectedOptions.remove(objectId);
        else currentSelectedOptions.add(objectId);
        if (currentSelectedOptions != null && currentSelectedOptions.size() > 0)
            enableContinueBtn();
        else disableContinueBtn();
        adapter.notifyDataSetChanged();
    }

    public void searchQuery(String s) {
        filteredOptionsList = filter(questionnaire.get(selectedQuestionIndex).getOptions(), s);
        adapter.setData(filteredOptionsList);
        adapter.notifyDataSetChanged();
    }

    private List<QuestionOption> filter(List<QuestionOption> models, String query) {
        query = query.toLowerCase();
        final List<QuestionOption> filteredModelList = new ArrayList<>();
        for (QuestionOption model : models) {
            if (model.getOptionTitle().toLowerCase().contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    private void skipNext() {

        boolean checkNxtQue = false;
        if (questionNo < totalNoOfQuestions) {
            if (questionnaire.get((selectedQuestionIndex + 1)).getOptions() == null || questionnaire.get((selectedQuestionIndex + 1)).getOptions().size() == 0 || questionnaire.get((selectedQuestionIndex + 1)).getUserAnswer() == null) {
                checkNxtQue = true;
            }
        }

        isChange = false;
        if (checkNxtQue) {
            showProgress();
            if (questionNo < totalNoOfQuestions) {
                questionnaire.get(selectedQuestionIndex).setUserAnswer(currentUserAnswer);
                questionNo++;
                selectedQuestionIndex++;
                presenter.queryQuestionOptions(getContext(), questionnaire.get(selectedQuestionIndex));
            } else {
                setUserQuestionsStatus();
            }
        } else {
            if (questionNo < totalNoOfQuestions) {
                questionNo++;
                selectedQuestionIndex++;
                currentUserAnswer = questionnaire.get(selectedQuestionIndex).getUserAnswer();
                boolean isMultiSelect = !(questionType.equals(MANDATORY));
                loadQuestion(questionnaire.get(selectedQuestionIndex), isMultiSelect);
            } else {
                setUserQuestionsStatus();
            }
        }
        backBtn.setVisibility(View.VISIBLE);
//        if (currentUserAnswer == null) {
////            ParseObject currentUserAnswer = new ParseObject(CLASS_NAME_USER_ANSWER);
//            currentUserAnswer = new UserAnswer();
//            currentUserAnswer.put(PARAM_USER_ID, ParseUser.getCurrentUser());
//            currentUserAnswer.put(PARAM_QUESTION_ID, questionnaire.get(selectedQuestionIndex));
//        }
//        currentUserAnswer.put(PARAM_OPTIONS_IDS, currentSelectedOptions);
//        currentUserAnswer.saveInBackground(e -> {
//            if (e == null) {
//                if (questionNo < totalNoOfQuestions) {
//                    questionnaire.get(selectedQuestionIndex).setUserAnswer(currentUserAnswer);
//                    questionNo++;
//                    selectedQuestionIndex++;
//                    presenter.queryQuestionOptions(getContext(), questionnaire.get(selectedQuestionIndex));
//                } else {
//                    setUserQuestionsStatus();
//                }
//                backBtn.setVisibility(View.VISIBLE);
//            } else {
//                setResponseError(e);
//            }
//        });
    }

    private void moveNext() {

        boolean checkNxtQue = false;

        if (questionNo < totalNoOfQuestions) {
            if (questionnaire.get((selectedQuestionIndex + 1)).getOptions() == null || questionnaire.get((selectedQuestionIndex + 1)).getOptions().size() == 0 || questionnaire.get((selectedQuestionIndex + 1)).getUserAnswer() == null) {
                checkNxtQue = true;
            }
        }

        if (isChange || checkNxtQue) {
            showProgress();
            if (currentUserAnswer == null) {
//            ParseObject currentUserAnswer = new ParseObject(CLASS_NAME_USER_ANSWER);
                currentUserAnswer = new UserAnswer();
                currentUserAnswer.put(PARAM_USER_ID, ParseUser.getCurrentUser());
                currentUserAnswer.put(PARAM_QUESTION_ID, questionnaire.get(selectedQuestionIndex));
            }
            currentUserAnswer.put(PARAM_OPTIONS_IDS, currentSelectedOptions);

//            currentUserAnswer.getRelation(PARAM_OPTIONS_RELATION).getQuery().findInBackground(new FindCallback<ParseObject>() {
//                @Override
//                public void done(List<ParseObject> objects, ParseException e) {
//                    if (e != null) {
//                        e.printStackTrace();
//                    } else if (objects != null && objects.size() > 0) {
//                        for (int i = 0; i < objects.size(); i++) {
//                            currentUserAnswer.getRelation(PARAM_OPTIONS_RELATION).remove(objects.get(i));
//                            Log.d("SIZE::", "removing...");
//
//                        }
//                    }
//                    Log.d("SIZE::", Integer.toString(objects.size()));
//                }
//            });
            ArrayList<ParseObject> myOptions = new ArrayList<ParseObject>();

            for (int i = 0; i < currentSelectedOptions.size(); i++) {
                for (int j = 0; j < allOptionsList.size(); j++) {
                    if (currentSelectedOptions.get(i).equals(allOptionsList.get(j).getObjectId())) {
//                        currentUserAnswer.getRelation(PARAM_OPTIONS_RELATION).add(allOptionsList.get(j));
                        myOptions.add(allOptionsList.get(j));
                        break;
                    }
                }
            }

            currentUserAnswer.put(PARAM_OPTIONS_OBJ_ARRAY, myOptions);

            currentUserAnswer.saveInBackground(e -> {
                if (e == null) {
                    if (questionNo < totalNoOfQuestions) {
                        questionnaire.get(selectedQuestionIndex).setUserAnswer(currentUserAnswer);
                        questionNo++;
                        selectedQuestionIndex++;
                        presenter.queryQuestionOptions(getContext(), questionnaire.get(selectedQuestionIndex));
                    } else {
                        setUserQuestionsStatus();
                    }
                    backBtn.setVisibility(View.VISIBLE);
                } else {
                    setResponseError(e);
                }
                isChange = false;
            });
        } else {
            if (questionNo < totalNoOfQuestions) {
                questionNo++;
                selectedQuestionIndex++;
                currentUserAnswer = questionnaire.get(selectedQuestionIndex).getUserAnswer();
                boolean isMultiSelect = !(questionType.equals(MANDATORY));
                loadQuestion(questionnaire.get(selectedQuestionIndex), isMultiSelect);
            } else {
                setUserQuestionsStatus();
            }
            backBtn.setVisibility(View.VISIBLE);
            isChange = false;
        }
    }

    private void setUserQuestionsStatus() {
        switch (questionType) {
            case MANDATORY:
                ParseUser.getCurrentUser().put(PARAM_MANDATORY_QUESTIONNAIRE_FILLED, true);
                break;
            case OPTIONAL:
                ParseUser.getCurrentUser().put(PARAM_OPTIONAL_QUESTIONNAIRE_FILLED, true);
                break;
        }
        showProgress();
        ParseUser.getCurrentUser().saveInBackground(e -> {
            dismissProgress();
            showToast(requireActivity(), requireContext(), "You have successfully saved " + questionType + " questions.", SUCCESS);
            new Handler().postDelayed(() -> requireActivity().onBackPressed(), 1000);
        });

    }

    private void moveBack() {
        isChange = false;
        if (questionNo > 1) {
            questionNo--;
            selectedQuestionIndex--;
            currentUserAnswer = questionnaire.get(selectedQuestionIndex).getUserAnswer();
            // boolean isMultiSelect = questionnaire.get(selectedQuestionIndex).getBoolean(PARAM_MULTIPLE_SELECTION);
            boolean isMultiSelect = !(questionType.equals(MANDATORY));
            loadQuestion(questionnaire.get(selectedQuestionIndex), isMultiSelect);
//            if (questionNo == 1)
//                backBtn.setVisibility(View.VISIBLE);
//            else
//                backBtn.setVisibility(View.VISIBLE);
        } else {
            requireActivity().onBackPressed();
        }
    }
}