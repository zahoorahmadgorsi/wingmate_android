package com.app.wingmate.admin.ui.dialogs;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.wingmate.R;
import com.app.wingmate.admin.models.RejectionReason;
import com.app.wingmate.admin.ui.adapters.ReasonsListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.Utilities.showToast;

public class RejectionReasonsDialog extends DialogFragment implements View.OnClickListener {

    private Activity myActivity;
    private View fragmentView;

    @BindView(R.id.screen_title)
    TextView screenTitleTV;
    @BindView(R.id.placeholder_tv)
    TextView placeholderTV;
    @BindView(R.id.btn_back)
    ImageView backBtn;
    @BindView(R.id.btn_save)
    Button saveBtn;
    @BindView(R.id.comment_et)
    EditText commentET;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private List<RejectionReason> rejectReasonList = new ArrayList<>();
    private String type;

    private ReasonsListAdapter adapter;
    private GridLayoutManager gridLayoutManager;

    static RejectionReasonsDialogClickListener myListener;

    public interface RejectionReasonsDialogClickListener {
        void onRejectionSelectorBackButtonClick(DialogFragment dialog);

        void onRejectionSelectorSaveClick(DialogFragment dialog, String reason, String comment);
    }

    public static RejectionReasonsDialog newInstance(RejectionReasonsDialogClickListener myListener, Activity myActivity, List<RejectionReason> rejectReasonList, String type) {
        RejectionReasonsDialog dialogFrag = new RejectionReasonsDialog();
        RejectionReasonsDialog.myListener = myListener;
        dialogFrag.myActivity = myActivity;
        dialogFrag.rejectReasonList = rejectReasonList;
        dialogFrag.type = type;
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
        fragmentView = inflater.inflate(R.layout.dialog_reject_reasons, container, false);
        ButterKnife.bind(this, fragmentView);
        Objects.requireNonNull(getDialog()).getWindow().setBackgroundDrawableResource(R.color.transparent);
        getDialog().getWindow().setGravity(Gravity.BOTTOM);
        setViews();
        return fragmentView;
    }

    private void setViews() {
        backBtn.setOnClickListener(this);
        saveBtn.setOnClickListener(this);

        placeholderTV.setText("");

        commentET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        disableContinueBtn();

        if (rejectReasonList == null) rejectReasonList = new ArrayList<>();

        gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        adapter = new ReasonsListAdapter(getActivity(), this, rejectReasonList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    public void setReason(int index) {
        for (int i = 0; i < rejectReasonList.size(); i++) {
            rejectReasonList.get(i).setSelected(false);
        }
        rejectReasonList.get(index).setSelected(true);
        adapter.setData(rejectReasonList);
        adapter.notifyDataSetChanged();
        enableContinueBtn();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                myListener.onRejectionSelectorBackButtonClick(this);
                break;
            case R.id.btn_save:
                boolean hasReason = false;
                String reason = "";
                for (int i = 0; i < rejectReasonList.size(); i++) {
                    if (rejectReasonList.get(i).isSelected()) {
                        hasReason = true;
                        reason = rejectReasonList.get(i).getReason();
                    }
                }
                if (!hasReason && commentET.getText().toString().isEmpty())
                    showToast(requireActivity(), requireContext(), "Please select reason!", ERROR);
                else {
                    myListener.onRejectionSelectorSaveClick(this, reason, commentET.getText().toString());
                }
                break;
            default:
                break;
        }
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
}