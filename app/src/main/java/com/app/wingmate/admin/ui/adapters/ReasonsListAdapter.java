package com.app.wingmate.admin.ui.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.wingmate.R;
import com.app.wingmate.admin.models.RejectionReason;
import com.app.wingmate.admin.ui.dialogs.RejectionReasonsDialog;

import java.util.ArrayList;
import java.util.List;

public class ReasonsListAdapter extends RecyclerView.Adapter<ReasonsListAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private Activity context;
    private RejectionReasonsDialog fragment;
    private List<RejectionReason> itemsList;

    public ReasonsListAdapter(Activity context, RejectionReasonsDialog fragment, List<RejectionReason> itemsList) {
        this.inflater = LayoutInflater.from(context);
        this.itemsList = itemsList;
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ReasonsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.list_item_reject_reason, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReasonsListAdapter.ViewHolder holder, final int position) {
        RejectionReason object = itemsList.get(position);
        holder.radioButton.setText(object.getReason());
        holder.radioButton.setChecked(object.isSelected());
//        holder.radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//            }
//        });
        holder.itemView.setOnClickListener(v -> fragment.setReason(position));
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public void setData(List<RejectionReason> itemsList) {
        this.itemsList = new ArrayList<>();
        this.itemsList = itemsList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout root;
        RadioButton radioButton;

        ViewHolder(View view) {
            super(view);
            this.root = view.findViewById(R.id.root);
            this.radioButton = view.findViewById(R.id.radio);
        }
    }
}