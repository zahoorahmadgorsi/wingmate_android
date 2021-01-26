package com.app.wingmate.ui.dialogs;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.wingmate.R;
import com.app.wingmate.models.Question;
import com.app.wingmate.models.QuestionOption;
import com.app.wingmate.models.UserAnswer;
import com.app.wingmate.profile.edit.EditProfileTextFieldFragment;
import com.app.wingmate.ui.adapters.DialogOptionsListAdapter;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.AppConstants.MANDATORY;
import static com.app.wingmate.utils.AppConstants.NATIONALITY_QUESTION_OBJECT_ID;
import static com.app.wingmate.utils.AppConstants.PARAM_ABOUT_ME;
import static com.app.wingmate.utils.AppConstants.PARAM_GENDER;
import static com.app.wingmate.utils.AppConstants.PARAM_NICK;
import static com.app.wingmate.utils.AppConstants.PARAM_OPTIONS_IDS;
import static com.app.wingmate.utils.AppConstants.PARAM_OPTIONS_OBJ_ARRAY;
import static com.app.wingmate.utils.AppConstants.PARAM_QUESTION_ID;
import static com.app.wingmate.utils.AppConstants.PARAM_USER_ID;
import static com.app.wingmate.utils.AppConstants.SUCCESS;
import static com.app.wingmate.utils.CommonKeys.KEY_TITLE;
import static com.app.wingmate.utils.Utilities.showToast;

public class OptionsSelectorDialog extends DialogFragment implements View.OnClickListener {

    private Activity myActivity;
    private View fragmentView;

    @BindView(R.id.screen_title)
    TextView screenTitleTV;
    @BindView(R.id.btn_back)
    ImageView backBtn;
    @BindView(R.id.btn_save)
    Button saveBtn;

    @BindView(R.id.questions_view)
    LinearLayout questionsView;
    @BindView(R.id.placeholder_tv)
    TextView placeholderTV;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.et_search)
    EditText searchET;

    @BindView(R.id.gender_view)
    LinearLayout genderView;
    @BindView(R.id.male_layout)
    RelativeLayout maleLayout;
    @BindView(R.id.male_tv)
    TextView maleTV;
    @BindView(R.id.female_layout)
    RelativeLayout femaleLayout;
    @BindView(R.id.female_tv)
    TextView femaleTV;

    @BindView(R.id.edit_fields_view)
    LinearLayout editFieldsView;
    @BindView(R.id.title)
    TextView titleTV;
    @BindView(R.id.et)
    EditText editText;
    @BindView(R.id.error_email)
    LinearLayout errorLayout;
    @BindView(R.id.error_email_tv)
    TextView errorTV;

    public Question question;
    public List<String> currentSelectedOptions;
    private List<QuestionOption> allOptionsList = new ArrayList<>();
    private List<QuestionOption> filteredOptionsList;
    public UserAnswer currentUserAnswer;
    private boolean isChange = false;
    private String questionType = MANDATORY;
    private String gender = "male";

    private DialogOptionsListAdapter adapter;
    private GridLayoutManager gridLayoutManager;

    public KProgressHUD dialog;

    private String TAG;

    static OptionsSelectorDialogClickListener myListener;

    public interface OptionsSelectorDialogClickListener {
        void onSelectorBackButtonClick(DialogFragment dialog);

        void onSelectorFieldsUpdateClick(DialogFragment dialog);

        void onSelectorGenderUpdateClick(DialogFragment dialog);

        void onSelectorApplyClick(DialogFragment dialog, Question question);
    }

    public static OptionsSelectorDialog newInstance(OptionsSelectorDialogClickListener myListener, Activity myActivity, Question question, String tag) {
        OptionsSelectorDialog dialogFrag = new OptionsSelectorDialog();
        OptionsSelectorDialog.myListener = myListener;
        dialogFrag.myActivity = myActivity;
        dialogFrag.question = question;
        dialogFrag.TAG = tag;
        return dialogFrag;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        myActivity = (Activity) context;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.customDialogUpDown);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.dialog_options_selector, container, false);
        ButterKnife.bind(this, fragmentView);
        Objects.requireNonNull(getDialog()).getWindow().setBackgroundDrawableResource(R.color.transparent);
        setViews();
        return fragmentView;
    }

    private void setViews() {
        dialog = KProgressHUD.create(myActivity)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        backBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);

        maleLayout.setOnClickListener(this);
        femaleLayout.setOnClickListener(this);

        questionsView.setVisibility(View.GONE);
        editFieldsView.setVisibility(View.GONE);
        genderView.setVisibility(View.GONE);

        if (TAG.equalsIgnoreCase(PARAM_NICK) || TAG.equalsIgnoreCase(PARAM_ABOUT_ME)) {
            setEditFieldsView();
        } else if (TAG.equalsIgnoreCase(PARAM_GENDER)) {
            setGenderView();
        } else if (TAG.equalsIgnoreCase(PARAM_QUESTION_ID)) {
            setQuestionsView();
        }
    }

    private void setQuestionsView() {
        questionsView.setVisibility(View.VISIBLE);
        disableContinueBtn();

        searchET.setVisibility(View.GONE);

        allOptionsList = new ArrayList<>();
        filteredOptionsList = new ArrayList<>();
        currentSelectedOptions = new ArrayList<>();

        gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        adapter = new DialogOptionsListAdapter(getActivity(), this, filteredOptionsList);
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

        currentUserAnswer = question.getUserAnswer();
        questionType = question.getQuestionType();
        boolean isMultiSelect = !(questionType.equals(MANDATORY));
        allOptionsList = question.getOptions();
        filteredOptionsList = question.getOptions();
        loadQuestion(question, isMultiSelect);
    }

    private void setEditFieldsView() {
        if (TAG.equalsIgnoreCase(PARAM_NICK))
            screenTitleTV.setText("You can call me");
        else if (TAG.equalsIgnoreCase(PARAM_ABOUT_ME))
            screenTitleTV.setText("Me, Myself & I");
        else
            screenTitleTV.setText("Edit Profile");
        editFieldsView.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.INVISIBLE);
        disableContinueBtn();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorLayout.setVisibility(View.INVISIBLE);
                editText.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.field_bg_selected, null));
                if (!editText.getText().toString().isEmpty()) {
                    enableContinueBtn();
                } else {
                    disableContinueBtn();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText.setOnFocusChangeListener((arg0, isFocus) -> {
            if (myActivity == null || editText == null) return;
            if (isFocus) {
                editText.setBackground(ResourcesCompat.getDrawable(myActivity.getResources(), R.drawable.field_bg_selected, null));
            } else {
                editText.setBackground(ResourcesCompat.getDrawable(myActivity.getResources(), R.drawable.field_bg_unselected, null));
            }
        });

        if (TAG.equalsIgnoreCase(PARAM_NICK)) {
            titleTV.setText("Name or Nick Name");
            editText.append(ParseUser.getCurrentUser().getString(PARAM_NICK));
        } else if (TAG.equalsIgnoreCase(PARAM_ABOUT_ME)) {
            titleTV.setText("About Me");
            String aboutme = ParseUser.getCurrentUser().getString(PARAM_ABOUT_ME);
            if (aboutme != null) editText.append(aboutme);
            ViewGroup.LayoutParams layoutParams = editText.getLayoutParams();
            int heightInPixels = (int) getResources().getDimension(R.dimen._100ssp);
            heightInPixels = (int) getResources().getDimension(R.dimen.dp200);
            layoutParams.height = heightInPixels;
            int paddingDp = (int) getResources().getDimension(R.dimen._8ssp);
            int paddingPixel = paddingDp;
            editText.setPadding(paddingPixel, paddingPixel, paddingPixel, paddingPixel);
            editText.setLayoutParams(layoutParams);
            editText.setGravity(Gravity.START);
        }
        editText.requestFocus();
    }

    private void setGenderView() {
        screenTitleTV.setText("I am");
        genderView.setVisibility(View.VISIBLE);
        gender = ParseUser.getCurrentUser().getString(PARAM_GENDER);
        if (gender.equalsIgnoreCase("male")) {
            maleLayout.setSelected(true);
            maleTV.setSelected(true);
            femaleLayout.setSelected(false);
            femaleTV.setSelected(false);
        } else if (gender.equalsIgnoreCase("female")) {
            gender = "female";
            femaleLayout.setSelected(true);
            femaleTV.setSelected(true);
            maleLayout.setSelected(false);
            maleTV.setSelected(false);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.male_layout:
                gender = "male";
                maleLayout.setSelected(true);
                maleTV.setSelected(true);
                femaleLayout.setSelected(false);
                femaleTV.setSelected(false);
                break;
            case R.id.female_layout:
                gender = "female";
                femaleLayout.setSelected(true);
                femaleTV.setSelected(true);
                maleLayout.setSelected(false);
                maleTV.setSelected(false);
                break;
            case R.id.btn_back:
                myListener.onSelectorBackButtonClick(this);
                break;
            case R.id.btn_save:
                if (TAG.equalsIgnoreCase(PARAM_NICK) || TAG.equalsIgnoreCase(PARAM_ABOUT_ME)) {
                    saveFieldsData();
                } else if (TAG.equalsIgnoreCase(PARAM_GENDER)) {
                    saveGenderData();
                } else if (TAG.equalsIgnoreCase(PARAM_QUESTION_ID)) {
                    if (currentSelectedOptions != null && currentSelectedOptions.size() > 0) {
                        saveQuestionsData();
                    } else
                        showToast(requireActivity(), requireContext(), "Please select option!", SUCCESS);
                }
                break;
            default:
                break;
        }
    }

    private void loadQuestion(Question question, boolean isMultiSelection) {
        isChange = false;
        if (isMultiSelection) placeholderTV.setText(R.string.sel_multi);
        else placeholderTV.setText(R.string.sel_one);

        disableContinueBtn();
        screenTitleTV.setText(question.getQuestionTitle());

        filteredOptionsList = new ArrayList<>();
        filteredOptionsList.addAll(question.getOptions());

        currentSelectedOptions = new ArrayList<>();
        currentUserAnswer = question.getUserAnswer();

        if (question.getUserAnswer() != null && question.getUserAnswer().getSelectedOptionIds() != null && question.getUserAnswer().getSelectedOptionIds().size() > 0)
            currentSelectedOptions = question.getUserAnswer().getSelectedOptionIds();

        if (question.getObjectId().equals(NATIONALITY_QUESTION_OBJECT_ID)
                || question.getQuestionTitle().equals(getString(R.string.question_country))
                || question.getShortTitle().contains(getString(R.string.nationality))
        ) {
            adapter.setCountrySelection(true);
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

    private void disableContinueBtn() {
        saveBtn.setSelected(false);
        saveBtn.setEnabled(false);
        saveBtn.setClickable(false);
        saveBtn.setAlpha(0.5f);
    }

    private void enableContinueBtn() {
        saveBtn.setSelected(true);
        saveBtn.setEnabled(true);
        saveBtn.setClickable(true);
        saveBtn.setAlpha(1.0f);
    }

    public void searchQuery(String s) {
        filteredOptionsList = filter(question.getOptions(), s);
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

    private void saveQuestionsData() {
        if (isChange) {
            dialog.show();
            if (currentUserAnswer == null) {
                currentUserAnswer = new UserAnswer();
                currentUserAnswer.put(PARAM_USER_ID, ParseUser.getCurrentUser());
                currentUserAnswer.put(PARAM_QUESTION_ID, question);
            }
            currentUserAnswer.put(PARAM_OPTIONS_IDS, currentSelectedOptions);

            ArrayList<ParseObject> myOptions = new ArrayList<ParseObject>();
            for (int i = 0; i < currentSelectedOptions.size(); i++) {
                for (int j = 0; j < allOptionsList.size(); j++) {
                    if (currentSelectedOptions.get(i).equals(allOptionsList.get(j).getObjectId())) {
                        myOptions.add(allOptionsList.get(j));
                        break;
                    }
                }
            }
            currentUserAnswer.put(PARAM_OPTIONS_OBJ_ARRAY, myOptions);

            currentUserAnswer.saveInBackground(e -> {
                dialog.dismiss();
                if (e == null) {
                    question.setUserAnswer(currentUserAnswer);
                    myListener.onSelectorApplyClick(this, question);
                } else {
                    showToast(requireActivity(), requireContext(), e.getMessage(), ERROR);
                }
                isChange = false;
            });
        }
    }

    private void saveFieldsData() {
        if (TAG.equalsIgnoreCase(PARAM_NICK)) {
            ParseUser.getCurrentUser().put(PARAM_NICK, editText.getText().toString());
        } else if (TAG.equalsIgnoreCase(PARAM_ABOUT_ME)) {
            ParseUser.getCurrentUser().put(PARAM_ABOUT_ME, editText.getText().toString());
        }
        dialog.show();
        ParseUser.getCurrentUser().saveInBackground(e -> {
            if (e == null) {
                showToast(getActivity(), getContext(), "Profile has been updated successfully", SUCCESS);
                dialog.dismiss();
                myListener.onSelectorFieldsUpdateClick(this);
            } else {
                showToast(getActivity(), getContext(), e.getMessage(), ERROR);
            }
        });
    }

    private void saveGenderData() {
        ParseUser.getCurrentUser().put(PARAM_GENDER, gender);
        dialog.show();
        ParseUser.getCurrentUser().saveInBackground(e -> {
            if (e == null) {
                showToast(getActivity(), getContext(), "Profile has been updated successfully", SUCCESS);
                dialog.dismiss();
                myListener.onSelectorGenderUpdateClick(this);
            } else {
                showToast(getActivity(), getContext(), e.getMessage(), ERROR);
            }
        });
    }

}