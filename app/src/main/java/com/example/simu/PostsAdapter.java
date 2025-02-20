package com.example.simu;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.annotation.Nonnull;


public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.MyViewHolder> {

    private Context context;
    private List<PostModel> postModelList;

    public PostsAdapter(Context context) {
        this.context = context;
        postModelList = new ArrayList<>();
    }

    public void addPost(PostModel postModel) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@Nonnull MyViewHolder holder, int position) {
        PostModel postModel = postModelList.get(position);
        if (postModel.getPostImage() != null) {
            holder.postImage.setVisibility(View.VISIBLE);
            Glide.with(context).load(postModel.getPostImage()).into(holder.postImage);
        } else {
            holder.postImage.setVisibility(View.GONE);
        }

        holder.postText.setText(postModel.getPostText());
        holder.likesCount.setText(String.valueOf(postModel.getPostLikes()));
        holder.dislikesCount.setText(String.valueOf(postModel.getPostDislikes()));
        holder.postTime.setText(formatTime(postModel.getPostingTime()));
        holder.locationText.setText(postModel.getAddress());

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentsActivity.class);
                intent.putExtra("id", postModel.getPostId());
                context.startActivity(intent);
            }
        });

        FirebaseFirestore.getInstance()
                .collection("Likes")
                .document(postModel.getPostId() + FirebaseAuth.getInstance().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot != null) {
                            String data = documentSnapshot.getString("postId");
                            if (data != null) {
                                postModel.setLiked(true);
                                holder.like.setImageResource(R.drawable.likef);
                            } else {
                                postModel.setLiked(false);
                                holder.like.setImageResource(R.drawable.like_blackf);
                            }
                        } else {
                            postModel.setLiked(false);
                            holder.like.setImageResource(R.drawable.like_blackf);
                        }
                    }
                });

        FirebaseFirestore.getInstance()
                .collection("Dislikes")
                .document(postModel.getPostId() + FirebaseAuth.getInstance().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot != null) {
                            String data = documentSnapshot.getString("postId");
                            if (data != null) {
                                postModel.setDisliked(true);
                                holder.dislike.setImageResource(R.drawable.dislike_blue);
                            } else {
                                postModel.setDisliked(false);
                                holder.dislike.setImageResource(R.drawable.dislike_black);
                            }
                        } else {
                            postModel.setDisliked(false);
                            holder.dislike.setImageResource(R.drawable.dislike_black);
                        }
                    }
                });

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (postModel.isLiked()) {
                    postModel.setLiked(false);
                    holder.like.setImageResource(R.drawable.like_blackf);

                    holder.dislike.setEnabled(true);

                    int likes = Integer.parseInt(postModel.getPostLikes());
                    if (likes > 0) {
                        postModel.setPostLikes(String.valueOf(likes - 1));
                    }

                    FirebaseFirestore.getInstance()
                            .collection("Likes")
                            .document(postModel.getPostId() + FirebaseAuth.getInstance().getUid())
                            .delete();
                } else {
                    postModel.setLiked(true);
                    holder.like.setImageResource(R.drawable.likef);

                    holder.dislike.setEnabled(false);

                    int likes = Integer.parseInt(postModel.getPostLikes());
                    postModel.setPostLikes(String.valueOf(likes + 1));

                    FirebaseFirestore.getInstance()
                            .collection("Likes")
                            .document(postModel.getPostId() + FirebaseAuth.getInstance().getUid())
                            .set(new PostModel("just check if null"));
                }
                FirebaseFirestore.getInstance()
                        .collection("Posts")
                        .document(postModel.getPostId())
                        .update("postLikes", postModel.getPostLikes())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                notifyDataSetChanged();
                            }
                        });
                FirebaseFirestore.getInstance()
                        .collection("Attendance")
                        .document(postModel.getPostId())
                        .update("postLikes", postModel.getPostLikes())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                notifyDataSetChanged();
                            }
                        });
                FirebaseFirestore.getInstance()
                        .collection("DeptPosts")
                        .document(postModel.getPostId())
                        .update("postLikes", postModel.getPostLikes())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                notifyDataSetChanged();
                            }
                        });
            }
        });

        holder.dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int dislikes = 0;
                if (postModel.getPostDislikes() != null) {
                    dislikes = Integer.parseInt(postModel.getPostDislikes());
                }
                if (postModel.isDisliked()) {
                    postModel.setDisliked(false);
                    holder.dislike.setImageResource(R.drawable.dislike_black);

                    holder.like.setEnabled(true);

                    if (dislikes > 0) {
                        postModel.setPostDislikes(String.valueOf(dislikes - 1));
                    }

                    FirebaseFirestore.getInstance()
                            .collection("Dislikes")
                            .document(postModel.getPostId() + FirebaseAuth.getInstance().getUid())
                            .delete();
                } else {
                    postModel.setDisliked(true);
                    holder.dislike.setImageResource(R.drawable.dislike_blue);

                    holder.like.setEnabled(false);

                    postModel.setPostDislikes(String.valueOf(dislikes + 1));

                    FirebaseFirestore.getInstance()
                            .collection("Dislikes")
                            .document(postModel.getPostId() + FirebaseAuth.getInstance().getUid())
                            .set(new PostModel("just check if null"));
                }
                FirebaseFirestore.getInstance()
                        .collection("Posts")
                        .document(postModel.getPostId())
                        .update("postDislikes", postModel.getPostDislikes())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                notifyDataSetChanged();
                            }
                        });
                FirebaseFirestore.getInstance()
                        .collection("Attendance")
                        .document(postModel.getPostId())
                        .update("postLikes", postModel.getPostLikes())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                notifyDataSetChanged();
                            }
                        });
                FirebaseFirestore.getInstance()
                        .collection("DeptPosts")
                        .document(postModel.getPostId())
                        .update("postLikes", postModel.getPostLikes())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                notifyDataSetChanged();
                            }
                        });
            }
        });


        String uid = postModel.getUserId();
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
        private TextView userName, postText, likesCount, dislikesCount, postTime, locationText;
        private ImageView userProfile, postImage, like, comment, dislike;

        public MyViewHolder(View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.username);
            postText = itemView.findViewById(R.id.postText);
            userProfile = itemView.findViewById(R.id.userdp);
            postImage = itemView.findViewById(R.id.postImage);
            like = itemView.findViewById(R.id.like);
            dislike = itemView.findViewById(R.id.dislike);
            comment = itemView.findViewById(R.id.comment);
            likesCount = itemView.findViewById(R.id.likesCount);
            dislikesCount = itemView.findViewById(R.id.dislikesCount);
            postTime = itemView.findViewById(R.id.postTime);
            locationText = itemView.findViewById(R.id.locationText);
        }
    }

    private String formatTime(long postingTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Dhaka"));
        return sdf.format(new Date(postingTime));
    }
}