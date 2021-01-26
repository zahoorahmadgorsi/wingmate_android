package com.app.wingmate.ui.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.wingmate.R;
import com.app.wingmate.models.TermsConditions;

import java.util.ArrayList;
import java.util.List;

import static com.app.wingmate.utils.AppConstants.PARAM_TEXT;

public class DosDontsListAdapter extends RecyclerView.Adapter<DosDontsListAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private Activity context;
    private List<TermsConditions> itemsList;

    public DosDontsListAdapter(Activity context, List<TermsConditions> itemsList) {
        this.inflater = LayoutInflater.from(context);
        this.itemsList = itemsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.list_item_dos_donts, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        TermsConditions object = itemsList.get(position);
        holder.textView.setText(object.getText());
        if (object.isDo()) {
            holder.icon.setImageResource(R.drawable.big_green_tick);
        } else {
            holder.icon.setImageResource(R.drawable.big_red_cross);
        }
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public void setData(List<TermsConditions> itemsList) {
        this.itemsList = new ArrayList<>();
        this.itemsList = itemsList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView textView;

        ViewHolder(View view) {
            super(view);
            this.icon = view.findViewById(R.id.tick);
            this.textView = view.findViewById(R.id.txt);
        }
    }
}