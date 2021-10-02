package com.app.wingmate.ui.adapters;

import static com.app.wingmate.utils.CommonKeys.MESSAGES_SENDER;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.app.wingmate.R;
import com.app.wingmate.utils.DateUtils;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    public static final int LEFT_MESSAGE_LAYOUT = 0;
    public static final int RIGHT_MESSAGE_LAYOUT = 1;
    List<ParseObject> messagesArray;

    public MessagesAdapter(List<ParseObject> messagesArray){
        this.messagesArray = messagesArray;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item,parent,false);
        return new MessageViewHolder (view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Log.e("position",""+position);
        ParseObject mObj =messagesArray.get(position);
        ParseUser currentUser = ParseUser.getCurrentUser();
        Objects.requireNonNull(mObj.getParseObject(MESSAGES_SENDER)).fetchIfNeededInBackground(new GetCallback<ParseObject>() {

            @Override
            public void done(ParseObject userPointer, ParseException e) {
                if (userPointer.getObjectId().matches(currentUser.getObjectId())){
                    holder.leftMessageLayout.setVisibility(View.GONE);
                    holder.rightMessageLayout.setVisibility(View.VISIBLE);
                    holder.rightMessage.setText(mObj.getString("message"));
                    Date date = mObj.getCreatedAt();
                    holder.rightTime.setText(DateUtils.dateToTime(date));
                }else if (!userPointer.getObjectId().matches(currentUser.getObjectId())){
                    holder.rightMessageLayout.setVisibility(View.GONE);
                    holder.leftMessageLayout.setVisibility(View.VISIBLE);
                    holder.leftMessage.setText(mObj.getString("message"));
                    Date date = mObj.getCreatedAt();
                    holder.leftTime.setText(DateUtils.dateToTime(date));
                    Log.e("date",date.toString());
                    String pic = userPointer.getString("profilePic");
                    if (pic!=null){
                        Picasso.get()
                                .load(pic)
                                .centerCrop()
                                .resize(500, 500)
                                .placeholder(R.drawable.image_placeholder)
                                .into(holder.image);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return messagesArray.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        TextView rightMessage, leftMessage,leftTime,rightTime;
        ImageView image;
        ConstraintLayout leftMessageLayout;
        ConstraintLayout rightMessageLayout;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            leftMessage = itemView.findViewById(R.id.left_message);
            rightMessage = itemView.findViewById(R.id.right_message);
            leftTime = itemView.findViewById(R.id.left_time);
            rightTime = itemView.findViewById(R.id.right_time);
            image = itemView.findViewById(R.id.image);
            leftMessageLayout = itemView.findViewById(R.id.left_chat_layout);
            rightMessageLayout = itemView.findViewById(R.id.right_chat_layout);

        }
    }
}
