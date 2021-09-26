package com.app.wingmate.ui.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.wingmate.R;
import com.app.wingmate.models.MembershipModel;

import java.util.List;

public class PackageOptionsAdapter extends RecyclerView.Adapter<PackageOptionsAdapter.OptionsViewHolder> {

    Context context;
    LayoutInflater inflater;
    List<MembershipModel> list;

    public PackageOptionsAdapter(Context context, List<MembershipModel> list){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @NonNull
    @Override
    public OptionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OptionsViewHolder(inflater.inflate(R.layout.package_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OptionsViewHolder holder, int position) {
        holder.packageTxt.setText(list.get(position).getPackageText());
        holder.pakageCost.setText(list.get(position).getPackagePrice());
        if (list.get(position).getSelected()){
            holder.selectionImg.setImageResource(R.drawable.select);
        }else{
            holder.selectionImg.setImageResource(R.drawable.uncheck);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (list.get(position).getSelected()){
                    resetList();
                    list.get(position).setSelected(false);
                }else{
                    resetList();
                    list.get(position).setSelected(true);
                }
                notifyDataSetChanged();
            }
        });
    }

    private void resetList() {
        for (int i=0; i<list.size(); i++){
            list.get(i).setSelected(false);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class OptionsViewHolder extends RecyclerView.ViewHolder{
        ImageView selectionImg;
        TextView packageTxt;
        TextView pakageCost;

        OptionsViewHolder(View view){
            super(view);
            this.packageTxt = view.findViewById(R.id.package_type);
            this.selectionImg = view.findViewById(R.id.select_icon);
            this.pakageCost = view.findViewById(R.id.rate);
        }
    }
}
