package com.app.wingmate.dashboard.search;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.wingmate.R;
import com.app.wingmate.models.Question;

import java.util.ArrayList;
import java.util.List;

import static com.app.wingmate.utils.AppConstants.PARAM_QUESTION_ID;

public class QuestionOptionsListAdapter extends RecyclerView.Adapter<QuestionOptionsListAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private Activity context;
    private SearchFragment fragment;
    private List<Question> itemsList;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public QuestionOptionsListAdapter(Activity context, SearchFragment fragment, List<Question> itemsList) {
        this.inflater = LayoutInflater.from(context);
        this.itemsList = itemsList;
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public QuestionOptionsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.list_item_question_option, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionOptionsListAdapter.ViewHolder holder, final int position) {
        Question object = itemsList.get(position);
        holder.titleTV.setText(object.getShortTitle());

        String valTxt = "";
        if (object.getUserAnswer() != null
                && object.getUserAnswer().getOptionsObjArray() != null
                && object.getUserAnswer().getOptionsObjArray().size() > 0) {
            for (int i = 0; i < object.getUserAnswer().getOptionsObjArray().size(); i++) {
                try {
                    if (valTxt.length() > 0) {
                        valTxt = valTxt + ", " + object.getUserAnswer().getOptionsObjArray().get(i).getOptionTitle();
                    } else {
                        valTxt = object.getUserAnswer().getOptionsObjArray().get(i).getOptionTitle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        holder.valueTV.setText(valTxt);

        holder.itemView.setOnClickListener(view -> {
            object.setOptions(object.getOptionsObjArray());
            fragment.showOptionSelectionDialog(object, position, PARAM_QUESTION_ID);
        });
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public void setData(List<Question> itemsList) {
        this.itemsList = new ArrayList<>();
        this.itemsList = itemsList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout root;
        TextView titleTV;
        TextView valueTV;

        ViewHolder(View view) {
            super(view);
            this.root = view.findViewById(R.id.root);
            this.titleTV = view.findViewById(R.id.title_tv);
            this.valueTV = view.findViewById(R.id.value_tv);
        }
    }
}
