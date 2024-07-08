package com.example.simu;

import android.app.AlertDialog;
import androidx.activity.OnBackPressedCallback;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.simu.databinding.ActivityUploadAttendanceBinding;
import com.example.simu.databinding.ActivityUploadPostBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Upload_post extends AppCompatActivity {
    ActivityUploadPostBinding binding;
    private Uri pickedImgUri;
    ImageView pickedImg;
    ActivityResultLauncher<Intent> cameraLauncher;
    LinearLayout addPhoto;
    String userID;
    EditText postText;
    Button postButton;
    Bitmap selectedImageBitmap;
    ImageView userdp;
    TextView username;
    ProgressBar progressBar;
    private double latitude;
    private double longitude;
    private String address;
    private LocationManager locationManager;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private String workstation;
    private String designation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_post);

        Intent intent = getIntent();
        String attendanceType = intent.getStringExtra("attendanceType");

        pickedImg = findViewById(R.id.pickedimg);
        addPhoto = findViewById(R.id.addPhoto);
        postButton = findViewById(R.id.postButton);
        postText = findViewById(R.id.postText);
        userdp = findViewById(R.id.userdp);
        username = findViewById(R.id.username);
        progressBar  = findViewById(R.id.progressBar);

        loadUserProfileImage();

        binding = ActivityUploadPostBinding.inflate(getLayoutInflater());

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String id = UUID.randomUUID().toString();
                getLocationCoordinates(id);
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
                                                    FirebaseFirestore.getInstance().collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).get()
                                                            .addOnSuccessListener(documentSnapshot -> {
                                                                if (documentSnapshot.exists()) {
                                                                    workstation = documentSnapshot.getString("workstation");
                                                                    designation = documentSnapshot.getString("designation");

                                                                    PostModel postModel = new PostModel(id,
                                                                            FirebaseAuth.getInstance().getUid(),
                                                                            postText.getText().toString(),
                                                                            uri.toString(), "0", "0", "0",
                                                                            System.currentTimeMillis(), latitude, longitude, address, attendanceType, workstation, designation);

                                                                    FirebaseFirestore.getInstance()
                                                                            .collection("Attendance")
                                                                            .document(id)
                                                                            .set(postModel)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    progressBar.setVisibility(View.GONE);
                                                                                    Toast.makeText(Upload_post.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                                                                    Intent intent = new Intent(Upload_post.this, NewsFeed.class);
                                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                                    startActivity(intent);
                                                                                    finish();
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    progressBar.setVisibility(View.GONE);
                                                                                    Toast.makeText(Upload_post.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            });
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
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Upload_post.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
                else if(!postText.getText().toString().isEmpty()){
                    FirebaseFirestore.getInstance().collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    workstation = documentSnapshot.getString("workstation");
                                    designation = documentSnapshot.getString("designation");

                                    PostModel postModel = new PostModel(id,
                                            FirebaseAuth.getInstance().getUid(),
                                            postText.getText().toString(),
                                            null, "0", "0", "0",
                                            System.currentTimeMillis(), latitude, longitude, address, attendanceType, workstation, designation);

                                    FirebaseFirestore.getInstance()
                                            .collection("Attendance")
                                            .document(id)
                                            .set(postModel)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(Upload_post.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(Upload_post.this, NewsFeed.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(Upload_post.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    postButton.setEnabled(false);
                    Toast.makeText(Upload_post.this, "Please write something or upload image!!!", Toast.LENGTH_SHORT).show();
                    postButton.setEnabled(true);
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


        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraLauncher.launch(cameraIntent);
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
    private void getLocationCoordinates(String id) {
        Intent intent = getIntent();
        String attendanceType = intent.getStringExtra("attendanceType");
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();

                locationManager.removeUpdates(this);

                Geocoder geocoder = new Geocoder(getApplicationContext());
                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addresses != null && !addresses.isEmpty()) {
                        Address address = addresses.get(0);
                        String addressLine = address.getAddressLine(0);
                        String completeAddress = addressLine != null ? addressLine : "";
                        StorageReference storageRef = FirebaseStorage.getInstance().getReference("Posts/" + id + "image.png");

                        if (selectedImageBitmap!=null) {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();

                            storageRef.putBytes(data)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            // Get download URL of the image
                                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    // Create PostModel with image URL
                                                    PostModel postModel = new PostModel(id,
                                                            FirebaseAuth.getInstance().getUid(),
                                                            postText.getText().toString(),
                                                            uri.toString(),
                                                            "0", "0", "0",
                                                            System.currentTimeMillis(),
                                                            latitude,
                                                            longitude,
                                                            completeAddress, attendanceType, workstation, designation);

                                                    // Save PostModel to Firestore
                                                    FirebaseFirestore.getInstance()
                                                            .collection("Posts")
                                                            .document(id)
                                                            .set(postModel)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    // Handle success if needed
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    // Handle failure if needed
                                                                }
                                                            });
                                                }
                                            });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Upload_post.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        else if(!postText.getText().toString().isEmpty()) {
                            PostModel postModel = new PostModel(id,
                                    FirebaseAuth.getInstance().getUid(),
                                    postText.getText().toString(),
                                    null,
                                    "0", "0", "0",
                                    System.currentTimeMillis(),
                                    latitude,
                                    longitude,
                                    completeAddress, attendanceType, workstation, designation);

                            FirebaseFirestore.getInstance()
                                    .collection("Posts")
                                    .document(id)
                                    .set(postModel)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Handle success if needed
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Handle failure if needed
                                        }
                                    });
                        }
                    } else {
                        Log.e("getLocationCoordinates", "No address found for the given coordinates.");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("getLocationCoordinates", "Error getting address from location: " + e.getMessage());
                }
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("LocationListener", "Provider enabled: " + provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("LocationListener", "Provider disabled: " + provider);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("LocationListener", "Status changed: " + provider + " - Status: " + status);
            }
        };

        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, locationListener);
        } else {
            Log.e("getLocationCoordinates", "Location manager is null. Unable to request location updates.");
        }
    }

}