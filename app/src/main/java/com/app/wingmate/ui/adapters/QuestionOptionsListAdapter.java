package com.app.wingmate.ui.adapters;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.recyclerview.widget.RecyclerView;

import com.app.wingmate.R;
import com.app.wingmate.ui.fragments.SearchFragment;
import com.app.wingmate.models.Question;

import java.util.ArrayList;
import java.util.List;

import static com.app.wingmate.utils.AppConstants.PARAM_QUESTION_ID;
import static com.app.wingmate.utils.AppConstants._1000KM;
import static com.app.wingmate.utils.AppConstants._100KM;
import static com.app.wingmate.utils.AppConstants._100M;
import static com.app.wingmate.utils.AppConstants._10KM;
import static com.app.wingmate.utils.AppConstants._10M;
import static com.app.wingmate.utils.AppConstants._1KM;
import static com.app.wingmate.utils.AppConstants._250M;
import static com.app.wingmate.utils.AppConstants._5000KM;
import static com.app.wingmate.utils.AppConstants._50M;
import static com.app.wingmate.utils.AppConstants._5KM;
import static com.app.wingmate.utils.AppConstants._5M;

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

        if (position == itemsList.size() - 1) {
            holder.locLayout.setVisibility(View.VISIBLE);
        } else {
            holder.locLayout.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(view -> {
            object.setOptions(object.getOptionsObjArray());
            fragment.showOptionSelectionDialog(object, position, PARAM_QUESTION_ID);
        });

//        case range0 = "No distance selected"
//        case range1 = "5m"
//        case range2 = "10m"
//        case range3 = "50m"
//        case range4 = "100m"
//        case range5 = "250m"
//        case range6 = "1km"
//        case range7 = "5km"
//        case range8 = "10km"
//        case range9 = "100km"
//        case range10 = "1000km"
//        case range11 = "5000km"

        holder.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress == 0) {
                    holder.disValueTV.setText(R.string.no_distance_selected);
                    fragment.setDistanceValue(0.0);
                }
                if (progress == 1) {
                    holder.disValueTV.setText(_5M);
                    fragment.setDistanceValue(5.0);
                }
                if (progress == 2) {
                    holder.disValueTV.setText(_10M);
                    fragment.setDistanceValue(10.0);
                }
                if (progress == 3) {
                    holder.disValueTV.setText(_50M);
                    fragment.setDistanceValue(50.0);
                }
                if (progress == 4) {
                    holder.disValueTV.setText(_100M);
                    fragment.setDistanceValue(100.0);
                }
                if (progress == 5) {
                    holder.disValueTV.setText(_250M);
                    fragment.setDistanceValue(250.0);
                }
                if (progress == 6) {
                    holder.disValueTV.setText(_1KM);
                    fragment.setDistanceValue(1000.0);
                }
                if (progress == 7) {
                    holder.disValueTV.setText(_5KM);
                    fragment.setDistanceValue(5000.0);
                }
                if (progress == 8) {
                    holder.disValueTV.setText(_10KM);
                    fragment.setDistanceValue(10000.0);
                }
                if (progress == 9) {
                    holder.disValueTV.setText(_100KM);
                    fragment.setDistanceValue(100000.0);
                }
                if (progress == 10) {
                    holder.disValueTV.setText(_1000KM);
                    fragment.setDistanceValue(1000000.0);
                }
                if (progress == 11) {
                    holder.disValueTV.setText(_5000KM);
                    fragment.setDistanceValue(5000000.0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
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
        LinearLayout locLayout;
        TextView titleTV;
        TextView valueTV;
        TextView disValueTV;
        AppCompatSeekBar seekbar;

        ViewHolder(View view) {
            super(view);
            this.root = view.findViewById(R.id.root);
            this.locLayout = view.findViewById(R.id.loc_layout);
            this.titleTV = view.findViewById(R.id.title_tv);
            this.valueTV = view.findViewById(R.id.value_tv);
            this.disValueTV = view.findViewById(R.id.dis_value_tv);
            this.seekbar = view.findViewById(R.id.loc_seekbar);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) this.seekbar.setMin(0);
            this.seekbar.setMax(11);

        }
    }
}
