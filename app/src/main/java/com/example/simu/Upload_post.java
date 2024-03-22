package com.example.simu;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.simu.databinding.ActivityUploadPostBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;

public class Upload_post extends AppCompatActivity {
    ActivityUploadPostBinding binding;
    private Uri pickedImgUri;
    ImageView pickedImg;
    ActivityResultLauncher<Intent> galleryLauncher;
    ActivityResultLauncher<Intent> cameraLauncher;
    LinearLayout addPhoto;
    String userID;
    EditText postText;
    Button postButton;
    Bitmap selectedImageBitmap;
    ImageView userdp;
    TextView username;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_post);

        pickedImg = findViewById(R.id.pickedimg);
        addPhoto = findViewById(R.id.addPhoto);
        postButton = findViewById(R.id.postButton);
        postText = findViewById(R.id.postText);
        userdp = findViewById(R.id.userdp);
        username = findViewById(R.id.username);
        progressBar  = findViewById(R.id.progressBar);

        loadUserProfileImage();

        binding = ActivityUploadPostBinding.inflate(getLayoutInflater());
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String id = UUID.randomUUID().toString();
                StorageReference storageRef = FirebaseStorage.getInstance().getReference("Posts/" + id + "image.png");

                if(selectedImageBitmap!=null){
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    storageRef.putBytes(data)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    progressBar.setVisibility(View.GONE);
                                                    finish();
                                                    Toast.makeText(Upload_post.this, "Posted", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(Upload_post.this, NewsFeed.class));
                                                    PostModel postModel = new PostModel(id,
                                                            FirebaseAuth.getInstance().getUid(),
                                                            postText.getText().toString(),
                                                            uri.toString(),"0", "0","0",  System.currentTimeMillis());

                                                    FirebaseFirestore.getInstance()
                                                            .collection("Posts")
                                                            .document(id)
                                                            .set(postModel);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(Upload_post.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Upload_post.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }else {
                    progressBar.setVisibility(View.GONE);
                   finish();
                    Toast.makeText(Upload_post.this, "Posted", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Upload_post.this, NewsFeed.class));
                    PostModel postModel = new PostModel(id,
                            FirebaseAuth.getInstance().getUid(),
                            postText.getText().toString(),
                            null,"0", "0","0", System.currentTimeMillis());

                    FirebaseFirestore.getInstance()
                            .collection("Posts")
                            .document(id)
                            .set(postModel);
                }
            }
        });


        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            selectedImageBitmap = (Bitmap) data.getExtras().get("data");
                            pickedImg.setImageBitmap(selectedImageBitmap);
                        }
                    }
                });
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri selectedImageUri = data.getData();
                            try {
                                selectedImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                                pickedImg.setImageBitmap(selectedImageBitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });


        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Upload_post.this);
                builder.setTitle("Choose Image Source");
                builder.setItems(new CharSequence[]{"Camera", "Gallery"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                cameraLauncher.launch(cameraIntent);
                                break;
                            case 1:
                                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                galleryLauncher.launch(galleryIntent);
                                break;
                        }
                    }
                });
                builder.show();
            }
        });

    }

    private void loadUserProfileImage() {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String userProfileImage = documentSnapshot.getString("profileImageUrl");
                        String userName = documentSnapshot.getString("name");

                        if (userProfileImage != null) {
                            Glide.with(Upload_post.this).load(userProfileImage).into(userdp);
                        }
                        if (userName != null) {
                            username.setText(userName);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Upload_post.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}