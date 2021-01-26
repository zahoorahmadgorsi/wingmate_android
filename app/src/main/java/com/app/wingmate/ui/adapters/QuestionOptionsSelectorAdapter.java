package com.app.wingmate.ui.adapters;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.wingmate.R;
import com.app.wingmate.models.Question;
import com.app.wingmate.models.QuestionOption;
import com.app.wingmate.models.UserAnswer;
import com.app.wingmate.profile.edit.EditProfileFragment;
import com.app.wingmate.utils.AppConstants;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.app.wingmate.utils.AppConstants.PARAM_ABOUT_ME;
import static com.app.wingmate.utils.AppConstants.PARAM_GENDER;
import static com.app.wingmate.utils.AppConstants.PARAM_NICK;
import static com.app.wingmate.utils.AppConstants.PARAM_QUESTION_ID;
import static com.app.wingmate.utils.AppConstants.PARAM_TITLE;

//public class QuestionOptionsSelectorAdapter extends RecyclerView.Adapter<QuestionOptionsSelectorAdapter.ViewHolder> {
public class QuestionOptionsSelectorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater inflater;
    private Activity context;
    private EditProfileFragment fragment;
    private List<Question> itemsList;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public QuestionOptionsSelectorAdapter(Activity context, EditProfileFragment fragment, List<Question> itemsList) {
        this.inflater = LayoutInflater.from(context);
        this.itemsList = itemsList;
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
//    public QuestionOptionsSelectorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            return new VHItem(inflater.inflate(R.layout.list_item_question_option, parent, false));
        } else if (viewType == TYPE_HEADER) {
            return new VHHeader(inflater.inflate(R.layout.layout_header, parent, false));
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");

//        return new ViewHolder(inflater.inflate(R.layout.list_item_question_option, parent, false));
    }

//    @Override
//    public void onBindViewHolder(@NonNull QuestionOptionsSelectorAdapter.ViewHolder holder, final int position) {
//        Question object = itemsList.get(position);
//        holder.titleTV.setText(object.getShortTitle());
//        System.out.println("===="+object.getShortTitle());
//
//        String valTxt = "";
//        if (object.getUserAnswer() != null
//                && object.getUserAnswer().getOptionsObjArray() != null
//                && object.getUserAnswer().getOptionsObjArray().size() > 0) {
//            for (int i = 0; i < object.getUserAnswer().getOptionsObjArray().size(); i++) {
//                try {
//                    if (valTxt.length() > 0) {
//                        valTxt = valTxt + ", " + object.getUserAnswer().getOptionsObjArray().get(i).getOptionTitle();
////                        valTxt = valTxt + ", " + object.getUserAnswer().getOptionsObjArray().get(i).fetchIfNeeded().getString(PARAM_TITLE);
//                    } else {
////                        valTxt = object.getUserAnswer().getOptionsObjArray().get(i).fetchIfNeeded().getString(PARAM_TITLE);
//                        valTxt = object.getUserAnswer().getOptionsObjArray().get(i).getOptionTitle();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        holder.valueTV.setText(valTxt);
//
//        holder.itemView.setOnClickListener(view -> {
//        });
//    }

//    @Override
//    public int getItemCount() {
//        return itemsList.size();
//    }

    public void setData(List<Question> itemsList) {
        this.itemsList = new ArrayList<>();
        this.itemsList = itemsList;
        notifyDataSetChanged();
    }

//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        LinearLayout root;
//        TextView titleTV;
//        TextView valueTV;
//
//        ViewHolder(View view) {
//            super(view);
//            this.root = view.findViewById(R.id.root);
//            this.titleTV = view.findViewById(R.id.title_tv);
//            this.valueTV = view.findViewById(R.id.value_tv);
//        }
//    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VHItem) {
            //cast holder to VHItem and set data

            Question object = getItem(position);
            ((VHItem) holder).titleTV.setText(object.getShortTitle());
            System.out.println("====" + object.getShortTitle());

            String valTxt = "";
            if (object.getUserAnswer() != null
                    && object.getUserAnswer().getOptionsObjArray() != null
                    && object.getUserAnswer().getOptionsObjArray().size() > 0) {
                for (int i = 0; i < object.getUserAnswer().getOptionsObjArray().size(); i++) {
                    try {
                        if (valTxt.length() > 0) {
                            valTxt = valTxt + ", " + object.getUserAnswer().getOptionsObjArray().get(i).getOptionTitle();
//                        valTxt = valTxt + ", " + object.getUserAnswer().getOptionsObjArray().get(i).fetchIfNeeded().getString(PARAM_TITLE);
                        } else {
//                        valTxt = object.getUserAnswer().getOptionsObjArray().get(i).fetchIfNeeded().getString(PARAM_TITLE);
                            valTxt = object.getUserAnswer().getOptionsObjArray().get(i).getOptionTitle();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            ((VHItem) holder).valueTV.setText(valTxt);

            ((VHItem) holder).itemView.setOnClickListener(view -> {
                getItem(position).setOptions(object.getOptionsObjArray());
                fragment.showOptionSelectionDialog(getItem(position), (position - 1), PARAM_QUESTION_ID);
            });
        } else if (holder instanceof VHHeader) {
            //cast holder to VHHeader and set data for header.
            if (ParseUser.getCurrentUser() != null) {
                System.out.println("====here in header====");
                ((VHHeader) holder).nickTV.setText(ParseUser.getCurrentUser().getString(PARAM_NICK));
                String gender = ParseUser.getCurrentUser().getString(AppConstants.PARAM_GENDER).substring(0, 1).toUpperCase()
                        + ParseUser.getCurrentUser().getString(AppConstants.PARAM_GENDER).substring(1).toLowerCase();
                ((VHHeader) holder).genderTV.setText(gender);
                ((VHHeader) holder).aboutMeTV.setText(ParseUser.getCurrentUser().getString(AppConstants.PARAM_ABOUT_ME));
            }

            ((VHHeader) holder).nickTV.setOnClickListener(v -> {
                fragment.editProfileFields(PARAM_NICK);
            });
            ((VHHeader) holder).genderTV.setOnClickListener(v -> {
                fragment.editProfileFields(PARAM_GENDER);
            });
            ((VHHeader) holder).aboutMeTV.setOnClickListener(v -> {
                fragment.editProfileFields(PARAM_ABOUT_ME);
            });
        }
    }

    @Override
    public int getItemCount() {
        return itemsList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private Question getItem(int position) {
        return itemsList.get(position - 1);
    }

    class VHItem extends RecyclerView.ViewHolder {
        LinearLayout root;
        TextView titleTV;
        TextView valueTV;

        VHItem(View view) {
            super(view);
            this.root = view.findViewById(R.id.root);
            this.titleTV = view.findViewById(R.id.title_tv);
            this.valueTV = view.findViewById(R.id.value_tv);
        }
    }

    class VHHeader extends RecyclerView.ViewHolder {
        TextView nickTV;
        TextView genderTV;
        TextView aboutMeTV;

        public VHHeader(View view) {
            super(view);
            this.nickTV = view.findViewById(R.id.nick_tv);
            this.genderTV = view.findViewById(R.id.gender_tv);
            this.aboutMeTV = view.findViewById(R.id.about_tv);
        }
    }
}