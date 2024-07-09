package com.example.simu;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder> {

    private Context context;
    private List<CommentModel> postModelList;

    public CommentsAdapter(Context context) {
        this.context = context;
        this.postModelList = new ArrayList<>();
    }

    public void addPost(CommentModel postModel) {
        postModelList.add(postModel);
        notifyDataSetChanged();
    }

    public void clearPost() {
        postModelList.clear();
        notifyDataSetChanged();
    }

    @Nonnull
    @Override
    public MyViewHolder onCreateViewHolder(@Nonnull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@Nonnull MyViewHolder holder, int position) {
        CommentModel commentModel = postModelList.get(position);
        holder.comment.setText(commentModel.getComment());
        holder.positiveScore.setText(String.format("Positive: %.2f", commentModel.getPositiveScore()));
        holder.negativeScore.setText(String.format("Negative: %.2f", commentModel.getNegativeScore()));

        if ("positive".equals(commentModel.getSentiment())) {
            holder.comment.setTextColor(Color.parseColor("#2E8B57"));
        } else {
            holder.comment.setTextColor(Color.RED);
        }

        String uid = commentModel.getUserId();
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String profileImageUrl = documentSnapshot.getString("profileImageUrl");
                            String username = documentSnapshot.getString("name");

                            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                                Glide.with(context).load(profileImageUrl).into(holder.userProfile);
                            }
                            holder.userName.setText(username);
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return postModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView userName, comment, positiveScore, negativeScore;
        private ImageView userProfile;

        public MyViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.username);
            userProfile = itemView.findViewById(R.id.userdp);
            comment = itemView.findViewById(R.id.comment);
            positiveScore = itemView.findViewById(R.id.positiveScore);
            negativeScore = itemView.findViewById(R.id.negativeScore);
        }
    }
}
