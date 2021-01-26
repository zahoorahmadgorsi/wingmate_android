package com.app.wingmate.ui.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.wingmate.R;
import com.app.wingmate.models.QuestionOption;
import com.app.wingmate.ui.dialogs.OptionsSelectorDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.app.wingmate.utils.AppConstants.PARAM_TITLE;

public class DialogOptionsListAdapter extends RecyclerView.Adapter<DialogOptionsListAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private Activity context;
    private OptionsSelectorDialog fragment;
    private List<QuestionOption> itemsList;
    private boolean isMultiSelection;
    private boolean isCountrySelection;

    public DialogOptionsListAdapter(Activity context, OptionsSelectorDialog fragment, List<QuestionOption> itemsList) {
        this.inflater = LayoutInflater.from(context);
        this.itemsList = itemsList;
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public DialogOptionsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DialogOptionsListAdapter.ViewHolder(inflater.inflate(R.layout.list_item_option, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DialogOptionsListAdapter.ViewHolder holder, final int position) {
        QuestionOption object = itemsList.get(position);
        holder.textView.setText(object.getString(PARAM_TITLE));
        boolean isPresent = false;
        if (fragment.currentSelectedOptions != null && fragment.currentSelectedOptions.contains(object.getObjectId())) {
            isPresent = true;
        }
        holder.root.setSelected(isPresent);
        holder.textView.setSelected(isPresent);
        RelativeLayout.LayoutParams lp;
        if (object.getCountryFlagImage()!=null) {
            holder.icon.setVisibility(View.VISIBLE);
            lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            Picasso.get().load(object.getCountryFlagImage().getUrl()).placeholder(R.drawable.flag_placeholder).into(holder.icon);
        } else {
            lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            holder.icon.setVisibility(View.GONE);
        }
        holder.root.setLayoutParams(lp);
        holder.itemView.setOnClickListener(view -> fragment.selectOption(position, isMultiSelection));
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public void setData(List<QuestionOption> itemsList) {
        this.itemsList = new ArrayList<>();
        this.itemsList = itemsList;
        notifyDataSetChanged();
    }

    public void setMultiSelection(boolean isMultiSelection) {
        this.isMultiSelection = isMultiSelection;
    }

    public void setCountrySelection(boolean isCountrySelection) {
        this.isCountrySelection = isCountrySelection;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout root;
        ImageView icon;
        TextView textView;

        ViewHolder(View view) {
            super(view);
            this.root = view.findViewById(R.id.root);
            this.icon = view.findViewById(R.id.icon);
            this.textView = view.findViewById(R.id.option_tv);
        }
    }
}
