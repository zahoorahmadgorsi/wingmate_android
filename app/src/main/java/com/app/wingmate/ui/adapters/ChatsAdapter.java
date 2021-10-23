package com.app.wingmate.ui.adapters;

import static com.app.wingmate.utils.AppConstants.PARAM_PROFILE_PIC;
import static com.app.wingmate.utils.CommonKeys.INSTANTS_RECEIVER;
import static com.app.wingmate.utils.CommonKeys.INSTANTS_SENDER;
import static com.app.wingmate.utils.CommonKeys.KEY_FRAGMENT_CHAT;
import static com.app.wingmate.utils.CommonKeys.LAST_MESSAGE;
import static com.app.wingmate.utils.CommonKeys.NICK;
import static com.app.wingmate.utils.CommonKeys.USER_USERNAME;
import static com.app.wingmate.utils.DateUtils.timeAgoSinceDate;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.wingmate.R;
import com.app.wingmate.utils.ActivityUtility;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatViewHolder>{

    List<ParseObject> instantsArray = new ArrayList<>();
    Activity activity;

    public ChatsAdapter(Activity activity, List<ParseObject> instantsArray){
        this.instantsArray = instantsArray ;
        this.activity = activity;
    }
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_layout,parent,false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        // Parse Object
        String name = "";
        String userId = "";
        final ParseObject iObj = instantsArray.get(position);

        // currentUser
        final ParseUser currentUser = ParseUser.getCurrentUser();

        // senderUser
        Objects.requireNonNull(iObj.getParseObject(INSTANTS_SENDER)).fetchIfNeededInBackground(new GetCallback<ParseObject>() {
            public void done(final ParseObject senderUser, ParseException e) {

                // receiverUser
                Objects.requireNonNull(iObj.getParseObject(INSTANTS_RECEIVER)).fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                    public void done(ParseObject receiverUser, ParseException e) {
                        // Date
                        holder.time.setText(timeAgoSinceDate(iObj.getDate("msgCreateAt"),true));
                        String lastmessage = iObj.getString(LAST_MESSAGE);
                        holder.lastMessage.setText(lastmessage);
                        // Avatar Image of the User you're chatting with
                        if (iObj.getBoolean("isUnread")){
                            if (!senderUser.getObjectId().matches(currentUser.getObjectId())){
                                holder.newMessage.setVisibility(View.VISIBLE);
                            }else{
                                holder.newMessage.setVisibility(View.GONE);
                            }
                        }
                        else{
                            holder.newMessage.setVisibility(View.GONE);
                        }
                        if (senderUser.getObjectId().matches(currentUser.getObjectId())) {
                            getParseImage(holder.userImage, receiverUser, "avatar");
                            //getParseImage();
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //makeUnreadZero(iObj);
                                    ActivityUtility.startChatActivity(activity,KEY_FRAGMENT_CHAT,receiverUser.getObjectId(),receiverUser.getString(NICK));
                                }
                            });
                            holder.userName.setText(receiverUser.getString(NICK));
                        } else {
                            //getParseImage(avatarImg, senderUser, USER_AVATAR);
                            getParseImage(holder.userImage, senderUser, "avatar");
                            holder.userName.setText(senderUser.getString(NICK));
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    makeUnreadZero(iObj);
                                    ActivityUtility.startChatActivity(activity,KEY_FRAGMENT_CHAT,senderUser.getObjectId(),senderUser.getString(NICK));
                                }
                            });
                        }

                    }});// ./ receiverUser

            }});// ./ senderUser

    }

    private void makeUnreadZero(ParseObject iObj) {
        iObj.put("isUnread",false);
        iObj.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    //Log.i("log-", "LAST MESS SAVED: " + lastMessage);
                } else {
                    //Toast.makeText(,"Something went wrong",Toast.LENGTH_SHORT).show();
                    //simpleAlert(e.getMessage(), ctx);
                }
            }
        });
    }

    public static void getParseImage(final ImageView imgView, ParseObject parseObj, String className) {
        /*ParseFile fileObject = parseObj.getParseFile(className);
        if (fileObject != null ) {
            fileObject.getDataInBackground(new GetDataCallback() {
                public void done(byte[] data, ParseException error) {
                    if (error == null) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                        if (bmp != null) {
                            imgView.setImageBitmap(bmp);
                        }}}});}*/
        String pic = parseObj.getString("profilePic");
        if (pic!=null){
            Picasso.get()
                    .load(pic)
                    .centerCrop()
                    .resize(500, 500)
                    .placeholder(R.drawable.image_placeholder)
                    .into(imgView);
        }
    }

    @Override
    public int getItemCount() {
        return instantsArray.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder{

        TextView time;
        TextView userName;
        TextView lastMessage;
        ImageView userImage;
        TextView newMessage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            userImage = itemView.findViewById(R.id.image);
            userName = itemView.findViewById(R.id.user_name);
            lastMessage = itemView.findViewById(R.id.last_message);
            newMessage = itemView.findViewById(R.id.unread);
        }
    }
}
