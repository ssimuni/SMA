package com.example.simu;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simu.R.id;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.regex.Pattern;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class Register extends AppCompatActivity {
    String designation, fdesignation;
    public static final String TAG = "TAG";
    EditText mName, mAddress, mWorkStation, mEmail,mNid, mDob, mPass, mUsername, mNumber;
    Spinner spinner, officerSpinner;
    Button mRegister;
    Button mProfileBtn;
    TextView mLogin;
    ImageView mProfilePic;
    FirebaseFirestore fstore;
    String userID;
    FirebaseAuth fAuth;
    Bitmap selectedImageBitmap;
    ActivityResultLauncher<Intent> cameraLauncher;
    ActivityResultLauncher<Intent> galleryLauncher;
    private StorageReference storageReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mName = findViewById(R.id.name);
        mAddress = findViewById(R.id.address);
        mWorkStation = findViewById(id.workstation);
        spinner = findViewById(id.spinner);
        officerSpinner = findViewById(id.officerSpinner);
        mEmail = findViewById(R.id.email);
        mNid = findViewById(R.id.nid);
        mDob = findViewById(R.id.dob);
        mPass = findViewById(R.id.password);
        mRegister = findViewById(R.id.buttonReg);
        mLogin = findViewById(R.id.haveanaccount);
        mProfileBtn = findViewById(R.id.profilePicBtn);
        mProfilePic = findViewById(R.id.profilepic);
        mUsername = findViewById(id.username);
        mNumber = findViewById(id.p_number);


        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        String[] spinner1 = {"Click here", "Officer", "Worker"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getApplicationContext(),
                android.R.layout.simple_spinner_item,
                spinner1
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                designation = parentView.getItemAtPosition(position).toString();



                if ("Officer".equals(designation)) {
                    officerSpinner.setVisibility(View.VISIBLE);

                    String[] officerLevels = {"Select Level", "Union level Officer", "Upozela level Officer", "District level Officer", "Division level Officer"};
                    ArrayAdapter<String> subAdapter = new ArrayAdapter<>(
                            getApplicationContext(),
                            android.R.layout.simple_spinner_item,
                            officerLevels
                    );

                    subAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    officerSpinner.setAdapter(subAdapter);

                    officerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            fdesignation = parentView.getItemAtPosition(position).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                        }
                    });
                } else {
                    officerSpinner.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            selectedImageBitmap = (Bitmap) data.getExtras().get("data");
                            mProfilePic.setImageBitmap(selectedImageBitmap);
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
                                mProfilePic.setImageBitmap(selectedImageBitmap);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });


        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

        mProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
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

        CollectionReference usersCollection = FirebaseFirestore.getInstance().collection("users");
        mUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                final String enteredUsername = editable.toString();
                if (!TextUtils.isEmpty(enteredUsername)) {
                    usersCollection.whereEqualTo("username", enteredUsername)
                            .addSnapshotListener((queryDocumentSnapshots, e) -> {
                                if (e != null) {
                                    Log.e("FirestoreQuery", "Error querying Firestore", e);
                                    return;
                                }

                                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                    mUsername.setError("Username already taken");
                                } else {
                                    mUsername.setHint("username available");
                                }
                            });
                }
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = mName.getText().toString();
                final String address = mAddress.getText().toString();
                final String workstation = mWorkStation.getText().toString();
                final String email = mEmail.getText().toString().trim();
                final String nid = mNid.getText().toString();
                final String dob = mDob.getText().toString();
                final String username = mUsername.getText().toString();
                final String pnumber = mNumber.getText().toString();
                String password = mPass.getText().toString().trim();


                if (Pattern.compile("\\s").matcher(email).find()) {
                    mEmail.setError("Email cannot contain whitespace");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    mPass.setError("Password is required");
                    return;
                }

                if (password.length() < 6) {
                    mPass.setError("Password should be of more than 6 character");
                    return;
                }

                if(pnumber.length() < 11) {
                    mNumber.setError("Enter 11 digit number");
                    return;
                }

                if(TextUtils.isEmpty(username)){
                    mUsername.setError("Username required");
                }

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Register.this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    saveLoginState(true);
                                    saveFirstTimeState(false);


                                    Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), verification.class));

                                    userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                                    DocumentReference documentReference = fstore.collection("users").document(userID);

                                    Map<String, Object> user = new HashMap<>();
                                    user.put("name", name);
                                    user.put("address", address);
                                    user.put("workstation", workstation);
                                    user.put("email", email);
                                    user.put("nid", nid);
                                    user.put("dob", dob);
                                    user.put("designation", fdesignation);
                                    user.put("username", username);
                                    user.put("number", pnumber);

                                    documentReference.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                           Log.d(TAG, "on success: user profile is created for" + userID);
                                            uploadImageToFirebaseStorage();
                                            finish();
                                        }
                                    });
                                }

                                else {
                                    Toast.makeText(Register.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void uploadImageToFirebaseStorage() {
        if (selectedImageBitmap != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("users/" + userID + "/profile_image.jpg");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos); // Experiment with compression level
            byte[] data = baos.toByteArray();

            storageRef.putBytes(data)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Image uploaded successfully
                            Log.d(TAG, "Image uploaded to Firebase Storage");
                            storageRef.getDownloadUrl().addOnCompleteListener(urlTask -> {
                                if (urlTask.isSuccessful()) {
                                    String downloadUrl = urlTask.getResult().toString();
                                    saveImageUrlToFirestore(downloadUrl);
                                } else {
                                    Log.e(TAG, "Failed to get download URL: " + urlTask.getException());
                                }
                            });
                        } else {
                            Log.e(TAG, "Image upload failed: " + Objects.requireNonNull(task.getException()).getMessage());
                        }
                    });
        }
    }

    private void saveImageUrlToFirestore(String imageUrl) {
        DocumentReference documentReference = fstore.collection("users").document(userID);

        documentReference.update("profileImageUrl", imageUrl)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Image URL saved to Firestore");
                    } else {
                        Log.e(TAG, "Failed to save image URL to Firestore: " + task.getException());
                    }
                });
    }

    private void saveLoginState(boolean isLoggedIn) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isLoggedIn", isLoggedIn);
        editor.apply();
    }

    private void saveFirstTimeState(boolean isFirstTime) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isFirstTime", isFirstTime);
        editor.apply();
    }

    public void showDatePickerDialog(View view) {
        final EditText dobEditText = findViewById(R.id.dob);


        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, R.style.DatePickerTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                        String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        dobEditText.setText(selectedDate);
                    }
                },
                year, month, day);

        datePickerDialog.show();
    }
}