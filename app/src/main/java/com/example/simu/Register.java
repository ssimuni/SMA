package com.example.simu;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import android.app.DatePickerDialog;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.regex.Pattern;


public class Register extends AppCompatActivity {
    String designation, fdesignation, fdepartment, fdirectorate, office, fOffice, fblood;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    public static final String TAG = "TAG";
    EditText mName, mAddress, mWorkStation, mEmail, mNid, mDob, mPass, mUsername, mNumber, mdesignation_type;
    Spinner spinner, officerSpinner, departmentSpinner, directorateSpinner, officeSpinner, sOfficeSpinner, subSpinner;
    Spinner divisionSpinner, districtSpinner, upozilaSpinner, bloodSpinner;
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

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.CAMERA
            }, REQUEST_LOCATION_PERMISSION);
            return;
        }

        mName = findViewById(R.id.name);
        mAddress = findViewById(R.id.address);
        mWorkStation = findViewById(id.workstation);
        spinner = findViewById(id.spinner);
        officerSpinner = findViewById(id.officerSpinner);
        officeSpinner = findViewById(id.office);
        sOfficeSpinner = findViewById(id.sOffice);
        subSpinner = findViewById(id.subSpinner);
        departmentSpinner = findViewById(id.department);
        directorateSpinner = findViewById(id.directorate);
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
        divisionSpinner = findViewById(R.id.divisionSpinner);
        districtSpinner = findViewById(R.id.districtSpinner);
        upozilaSpinner = findViewById(R.id.upozilaSpinner);
        mdesignation_type = findViewById(R.id.designation_type);
        bloodSpinner = findViewById(id.blood);

        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        //division, district, upozila spinner
        String[] divisions = {"Dhaka", "Chattogram", "Rajshahi", "Rangpur", "Mymensingh", "Barishal", "Khulna", "Sylhet"};
        String[][] districts = {
                {"Dhaka", "Gazipur", "Gopalganj", "Kishoreganj", "Madaripur", "Manikganj", "Munshiganj", "Narayanganj", "Rajbari", "Shariatpur", "Faridpur", "Tangail", "Narsingdi"},
                {"Bandarban", "Brahmanbaria", "Chandpur", "Chattogram", "Cumilla", "Cox's Bazar", "Feni", "Khagrachhari", "Lakshmipur", "Noakhali", "Rangamati"},
                {"Joypurhat", "Bogura", "Naogaon", "Natore", "Nawabganj", "Pabna", "Sirajganj", "Rajshahi"},
                {"Dinajpur", "Gaibandha", "Kurigram", "Lalmonirhat", "Nilphamari", "Panchagarh", "Rangpur", "Thakurgaon"},
                {"Netrokona", "Sherpur", "Jamalpur", "Mymensingh"},
                {"Barguna", "Barishal", "Bhola", "Jhalokati", "Patuakhali", "Pirojpur"},
                {"Bagerhat", "Chuadanga", "Jashore", "Jhenaida", "Khulna", "Kushtia", "Magura", "Meherpur", "Narail", "Satkhira"},
                {"Habiganj", "Moulvibazar", "Sunamganj", "Sylhet"},
        };

        String[][][] upozilas = {
                {
                        //dhaka division
                        {"Dhamrai", "Dohar", "Keraniganj", "Nawabganj", "Savar"},  // Upozilas for Dhaka
                        {"Gazipur Sadar", "Kaliakair", "Kaliganj", "Kapasia", "Sreepur"},  // Upozilas for Gazipur
                        {"Gopalganj Sadar", "Kashiani", "Kotalipara", "Muksudpur", "Tungipara"},  // Upozilas for Gopalganj
                        {"Austagram", "Bajitpur", "Bhairab", "Hossainpur", "Itna", "Karimganj", "Katiadi", "Kishoreganj Sadar", "Kuliarchar", "Mithamain", "Nikli", "Pakundia", "Tarail"},  // Upozilas for Kishoreganj
                        {"Rajoir", "Madaripur Sadar", "Kalkini", "Shibchar", "Dasar"},  // Upozilas for Madaripur
                        {"Daulatpur", "Ghior", "Harirampur", "Manikgonj Sadar", "Saturia", "Shivalaya", "Singair"},  // Upozilas for Manikganj
                        {"Gazaria", "Lohajang", "Munshiganj Sadar", "Sirajdikhan", "Sreenagar", "Tongibari"},  // Upozilas for Munshiganj
                        {"Araihazar", "Bandar", "Narayanganj Sadar", "Rupganj", "Sonargaon"},  // Upozilas for Narayanganj
                        {"Baliakandi", "Goalandaghat", "Pangsha", "Rajbari Sadar", "Kalukhali"},  // Upozilas for Rajbari
                        {"Bhedarganj", "Damudya", "Gosairhat", "Naria", "Shariatpur Sadar", "Zajira"},  // Upozilas for Shariatpur
                        {"Alfadanga", "Bhanga", "Boalmari", "Charbhadrasan", "Faridpur Sadar", "Madhukhali", "Nagarkanda", "Sadarpur", "Saltha"},  // Upozilas for Faridpur
                        {"Gopalpur", "Basail", "Bhuapur", "Delduar", "Ghatail", "Kalihati", "Madhupur", "Mirzapur", "Nagarpur", "Sakhipur", "Dhanbari", "Tangail Sadar"},  // Upozilas for Tangail
                        {"Narsingdi Sadar", "Belabo", "Monohardi", "Palash", "Raipura", "Shibpur"}  // Upozilas for Narsingdi
                },
                {
                        //chittagong division
                        {"Ali Kadam", "Bandarban Sadar", "Lama", "Naikhongchhari", "Rowangchhari", "Ruma", "Thanchi"}, // Upozilas for Bandarban
                        {"Akhaura", "Bancharampur", "Brahmanbaria Sadar", "Kasba", "Nabinagar", "Nasirnagar", "Sarail", "Ashuganj", "Bijoynagar"}, // Upozilas for Brahmanbaria
                        {"Chandpur Sadar", "Faridganj", "Haimchar", "Haziganj", "Kachua", "Matlab Dakshin", "Matlab Uttar", "Shahrasti"}, // Upozilas for Chandpur
                        {"Anwara", "Banshkhali", "Boalkhali", "Chandanaish", "Fatikchhari", "Hathazari", "Karnaphuli", "Lohagara", "Mirsharai", "Patiya", "Rangunia", "Raozan", "Sandwip", "Satkania", "Sitakunda"}, // Upozilas for Chattogram
                        {"Barura", "Brahmanpara", "Burichang", "Chandina", "Chauddagram", "Daudkandi", "Debidwar", "Homna", "Laksam", "Lalmai", "Muradnagar", "Nangalkot", "Cumilla Adarsha Sadar", "Meghna", "Titas", "Monohargonj", "Cumilla Sadar Dakshin"}, // Upozilas for Cumilla
                        {"Chakaria", "Cox's Bazar Sadar", "Kutubdia", "Maheshkhali", "Ramu", "Teknaf", "Ukhia", "Pekua", "Eidgaon"}, // Upozilas for Cox's Bazar
                        {"Chhagalnaiya", "Daganbhuiyan", "Feni Sadar", "Parshuram", "Sonagazi", "Fulgazi"}, // Upozilas for Feni
                        {"Dighinala", "Khagrachhari", "Lakshmichhari", "Mahalchhari", "Manikchhari", "Matiranga", "Panchhari", "Ramgarh", "Guimara"}, // Upozilas for Khagrachhari
                        {"Lakshmipur Sadar", "Raipur", "Ramganj", "Ramgati", "Kamalnagar"}, // Upozilas for Lakshmipur
                        {"Begumganj", "Noakhali Sadar", "Chatkhil", "Companiganj", "Hatiya", "Senbagh", "Sonaimuri", "Subarnachar", "Kabirhat"}, // Upozilas for Noakhali
                        {"Bagaichhari", "Barkal", "Kawkhali (Betbunia)", "Belaichhari", "Kaptai", "Juraichhari", "Langadu", "Naniyachar", "Rajasthali", "Rangamati Sadar"} // Upozilas for Rangamati
                },
                {
                        //rajshahi division
                        {"Akkelpur", "Joypurhat Sadar", "Kalai", "Khetlal", "Panchbibi"}, // Upozilas for Joypurhat District
                        {"Adamdighi", "Bogura Sadar", "Dhunat", "Dhupchanchia", "Gabtali", "Kahaloo", "Nandigram", "Sariakandi", "Shajahanpur", "Sherpur", "Shibganj", "Sonatola"}, // Upozilas for Bogura District
                        {"Atrai", "Badalgachhi", "Manda", "Dhamoirhat", "Mohadevpur", "Naogaon Sadar", "Niamatpur", "Patnitala", "Porsha", "Raninagar", "Sapahar"}, // Upozilas for Naogaon District
                        {"Bagatipara", "Baraigram", "Gurudaspur", "Lalpur", "Natore Sadar", "Singra", "Naldanga"}, // Upozilas for Natore District
                        {"Bholahat", "Gomastapur", "Nachole", "Nawabganj Sadar", "Shibganj"}, // Upozilas for Nawabganj District
                        {"Atgharia", "Bera", "Bhangura", "Chatmohar", "Faridpur", "Ishwardi", "Pabna Sadar", "Santhia", "Sujanagar"}, // Upozilas for Pabna District
                        {"Belkuchi", "Chauhali", "Kamarkhanda", "Kazipur", "Raiganj", "Shahjadpur", "Sirajganj Sadar", "Tarash", "Ullahpara"}, // Upozilas for Sirajganj District
                        {"Bagha", "Bagmara", "Charghat", "Durgapur", "Godagari", "Mohanpur", "Paba", "Puthia", "Tanore"}, // Upozilas for Rajshahi District
                },
                {
                        //rangpur
                        {"Birampur", "Birganj", "Biral", "Bochaganj", "Chirirbandar", "Phulbari", "Ghoraghat", "Hakimpur", "Kaharole", "Khansama", "Dinajpur Sadar", "Nawabganj", "Parbatipur"}, // Upozilas for Dinajpur
                        {"Phulchhari", "Gaibandha Sadar", "Gobindaganj", "Palashbari", "Sadullapur", "Sughatta", "Sundarganj"}, // Upozilas for Gaibandha
                        {"Bhurungamari", "Char Rajibpur", "Chilmari", "Phulbari (Kurigram)", "Kurigram Sadar", "Nageshwari", "Rajarhat", "Raomari", "Ulipur"},// Upozilas for Kurigram
                        {"Aditmari", "Hatibandha", "Kaliganj", "Lalmonirhat Sadar", "Patgram"},// Upozilas for Lalmonirhat
                        {"Dimla", "Domar", "Jaldhaka", "Kishoreganj", "Nilphamari Sadar", "Saidpur"}, // Upozilas for Nilphamari
                        {"Atwari", "Boda", "Debiganj", "Panchagarh Sadar", "Tetulia"}, // Upozilas for Panchagarh
                        {"Badarganj", "Gangachhara", "Kaunia", "Rangpur Sadar", "Mithapukur", "Pirgachha", "Pirganj", "Taraganj"},// Upozilas for Rangpur
                        {"Baliadangi", "Haripur", "Pirganj (Thakurgaon)", "Ranisankail", "Thakurgaon Sadar"}// Upozilas for Thakurgaon
                },
                {
                        //mymensingh division
                        {"Atpara", "Barhatta", "Durgapur", "Khaliajuri", "Kalmakanda", "Kendua", "Madan", "Mohanganj", "Netrokona Sadar", "Purbadhala"}, // Upozilas for Netrokona
                        {"Jhenaigati", "Nakla", "Nalitabari", "Sherpur Sadar", "Sreebardi"}, // Upozilas for Sherpur
                        {"Baksiganj", "Dewanganj", "Islampur", "Jamalpur Sadar", "Madarganj", "Melandaha", "Sarishabari"}, // Upozilas for Jamalpur
                        {"Trishal", "Dhobaura", "Fulbaria", "Gafargaon", "Gauripur", "Haluaghat", "Ishwarganj", "Mymensingh Sadar", "Muktagachha", "Nandail", "Phulpur", "Bhaluka", "Tara Khanda"} // Upozilas for Mymensingh
                },
                {
                        //barishal
                        {"Amtali", "Bamna", "Barguna Sadar", "Betagi", "Patharghata", "Taltali"}, // Upozilas for Barguna
                        {"Agailjhara", "Babuganj", "Bakerganj", "Banaripara", "Gaurnadi", "Hizla", "Barishal Sadar", "Mehendiganj", "Muladi", "Wazirpur"}, // Upozilas for Barishal
                        {"Bhola Sadar", "Burhanuddin", "Char Fasson", "Daulatkhan", "Lalmohan", "Manpura", "Tazumuddin"}, // Upozilas for Bhola
                        {"Jhalokati Sadar", "Kathalia", "Nalchity", "Rajapur"}, // Upozilas for Jhalokati
                        {"Bauphal", "Dashmina", "Galachipa", "Kalapara", "Mirzaganj", "Patuakhali Sadar", "Rangabali", "Dumki"}, // Upozilas for Patuakhali
                        {"Bhandaria", "Kawkhali", "Mathbaria", "Nazirpur", "Pirojpur Sadar", "Nesarabad (Swarupkati)", "Indurkani"} // Upozilas for Pirojpur
                },
                {
                        //khulna
                        {"Bagerhat Sadar", "Chitalmari", "Fakirhat", "Kachua", "Mollahat", "Mongla", "Morrelganj", "Rampal", "Sarankhola"},// Upozilas for Bagerhat District
                        {"Alamdanga", "Chuadanga Sadar", "Damurhuda", "Jibannagar"},// Upozilas for Chuadanga District
                        {"Abhaynagar", "Bagherpara", "Chaugachha", "Jhikargachha", "Keshabpur", "Jashore Sadar", "Manirampur", "Sharsha"},// Upozilas for Jashore District
                        {"Harinakunda", "Jhenaidah Sadar", "Kaliganj", "Kotchandpur", "Maheshpur", "Shailkupa"},// Upozilas for Jhenaida District
                        {"Batiaghata", "Dacope", "Dumuria", "Dighalia", "Koyra", "Paikgachha", "Phultala", "Rupsha", "Terokhada"},// Upozilas for Khulna District
                        {"Bheramara", "Daulatpur", "Khoksa", "Kumarkhali", "Kushtia Sadar", "Mirpur"},// Upozilas for Kushtia District
                        {"Magura Sadar", "Mohammadpur", "Shalikha", "Sreepur"},// Upozilas for Magura District
                        {"Gangni", "Meherpur Sadar", "Mujibnagar"},// Upozilas for Meherpur District
                        {"Kalia", "Lohagara", "Narail Sadar"},// Upozilas for Narail District
                        {"Assasuni", "Debhata", "Kalaroa", "Kaliganj", "Satkhira Sadar", "Shyamnagar", "Tala"}
                },
                {
                        //sylhet
                        {"Ajmiriganj", "Bahubal", "Baniyachong", "Chunarughat", "Habiganj Sadar", "Lakhai", "Madhabpur", "Nabiganj", "Shayestaganj"},// Upozilas for Habiganj District
                        {"Barlekha", "Juri", "Kamalganj", "Kulaura", "Moulvibazar Sadar", "Rajnagar", "Sreemangal"},// Upozilas for Moulvibazar District
                        {"Bishwamvarpur", "Chhatak", "Shantiganj", "Derai", "Dharamapasha", "Dowarabazar", "Jagannathpur", "Jamalganj", "Sullah", "Sunamganj Sadar", "Tahirpur", "Madhyanagar"},// Upozilas for Sunamganj District
                        {"Balaganj", "Beanibazar", "Bishwanath", "Companiganj", "Dakshin Surma", "Fenchuganj", "Golapganj", "Gowainghat", "Jaintiapur", "Kanaighat", "Osmani Nagar", "Sylhet Sadar", "Zakiganj"}// Upozilas for Sylhet District
                }
        };

        ArrayAdapter<String> divisionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, divisions);
        divisionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        divisionSpinner.setAdapter(divisionAdapter);
        divisionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                districtSpinner.setVisibility(View.VISIBLE);
                String[] selectedDistricts = districts[position];
                ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(Register.this, android.R.layout.simple_spinner_item, selectedDistricts);
                districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                districtSpinner.setAdapter(districtAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                upozilaSpinner.setVisibility(View.VISIBLE);
                String[] selectedUpozilas = upozilas[divisionSpinner.getSelectedItemPosition()][position];
                ArrayAdapter<String> upozilaAdapter = new ArrayAdapter<>(Register.this, android.R.layout.simple_spinner_item, selectedUpozilas);
                upozilaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                upozilaSpinner.setAdapter(upozilaAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        divisionSpinner.setSelection(0);




        String[] spinner1 = {"Click here", "Officer", "Worker", "Teacher"};

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


                if ("Worker".equals(designation)) {
                    officerSpinner.setVisibility(View.VISIBLE);

                    String[] officerLevels = {"Select Level", "Union level Worker", "Upazila level Worker", "District level Worker", "Division level Worker"};
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
                }

                else if ("Officer".equals(designation)) {
                    officerSpinner.setVisibility(View.VISIBLE);

                    String[] officerLevels = {"Select Level", "Union level Officer", "Upazila level Officer", "District level Officer", "Division level Officer"};
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
                }

                else if ("Teacher".equals(designation)) {
                    officerSpinner.setVisibility(View.VISIBLE);

                    String[] teacherLevels = {"Select Institution Type", "High School", "Primary School", "College"};
                    ArrayAdapter<String> subAdapter = new ArrayAdapter<>(
                            getApplicationContext(),
                            android.R.layout.simple_spinner_item,
                            teacherLevels
                    );

                    subAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    officerSpinner.setAdapter(subAdapter);

                    officerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            String schoolType = parentView.getItemAtPosition(position).toString();

                            if ("High School".equals(schoolType)) {
                                subSpinner.setVisibility(View.VISIBLE);

                                String[] highSchoolRoles = {"Select Role", "Head Teacher", "Senior Assistant Teacher", "Assistant Teacher"};
                                ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(
                                        getApplicationContext(),
                                        android.R.layout.simple_spinner_item,
                                        highSchoolRoles
                                );

                                roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                subSpinner.setAdapter(roleAdapter);

                                subSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                        fdesignation = parentView.getItemAtPosition(position).toString();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parentView) {
                                    }
                                });
                            }
                            else if ("Primary School".equals(schoolType)) {
                                subSpinner.setVisibility(View.VISIBLE);

                                String[] primarySchoolRoles = {"Select Role", "Head Teacher", "Assistant Teacher"};
                                ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(
                                        getApplicationContext(),
                                        android.R.layout.simple_spinner_item,
                                        primarySchoolRoles
                                );

                                roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                subSpinner.setAdapter(roleAdapter);

                                subSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                        fdesignation = parentView.getItemAtPosition(position).toString();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parentView) {
                                    }
                                });
                            }
                            else if ("College".equals(schoolType)) {
                                subSpinner.setVisibility(View.VISIBLE);

                                String[] primarySchoolRoles = {"Select Role", "Principal", "Vice Principal", "Professor", "Associate Professor", "Assistant Professor", "Lecturer"};
                                ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(
                                        getApplicationContext(),
                                        android.R.layout.simple_spinner_item,
                                        primarySchoolRoles
                                );

                                roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                subSpinner.setAdapter(roleAdapter);

                                subSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                        fdesignation = parentView.getItemAtPosition(position).toString();
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parentView) {
                                    }
                                });
                            }
                            else {
                                subSpinner.setVisibility(View.INVISIBLE);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                        }
                    });
                }


                else {
                    officerSpinner.setVisibility(View.INVISIBLE);
                    subSpinner.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });



        //office spinner
        String[] spinner2 = {"Click here", "Upazila", "District", "Division"};

        ArrayAdapter<String> officeadapter = new ArrayAdapter<>(
                getApplicationContext(),
                android.R.layout.simple_spinner_item,
                spinner2
        );

        officeadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        officeSpinner.setAdapter(officeadapter);
        officeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                office = parentView.getItemAtPosition(position).toString();


                if ("Upazila".equals(office)) {
                    sOfficeSpinner.setVisibility(View.VISIBLE);

                    String[] officeLevels = {"Select Upazila Office", "Upazila Nirbahi Officer's Office", "Upazila Family Planning Office", "Upazila Election Office",
                            "Upazila Education Office", "Upozila ICT Division",
                            "Upazila Cooperative Office", "Upazila Accounts Office", "Police Station", "Office of the Upazila Engineer", "Upazila Ansar and VDP Office",
                            "Upazila Project Implementation Office", "Upazila Food Office", "Office of the Upazila Chairman", "Upazila Agriculture Office",
                            "Upazila Livestock Office", "Upazila Fisheries Office", "Upazila Public Health Engineering Office", "Upazila Women Affairs Office",
                            "Upazila Rural Development Office", "Upazila Secondary Education Office", "Upazila Statistics Office", "Upazila Youth Development Office",
                            "Upazila Land Office", "Upazila Health Complex", "Upazila Social Services Office", "Government College", "Government High School", "Upazila Service Center"};
                    ArrayAdapter<String> sAdapter = new ArrayAdapter<>(
                            getApplicationContext(),
                            android.R.layout.simple_spinner_item,
                            officeLevels
                    );

                    sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sOfficeSpinner.setAdapter(sAdapter);

                    sOfficeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            fOffice = parentView.getItemAtPosition(position).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                        }
                    });
                }

                else if ("District".equals(office)) {
                    sOfficeSpinner.setVisibility(View.VISIBLE);

                    String[] officeLevels = {"Select District Office","Deputy Commissioner's Office", "District Family Planning Office", "District Election Office",
                            "District Education Office", "District ICT Division", "District Cooperative Office", "District Accounts Office", "Police Station", "Office of the District Engineer",
                            "District Ansar and VDP Office", "District Project Implementation Office", "District Food Office", "Office of the District Chairman",
                            "District Agriculture Office", "District Livestock Office", "District Fisheries Office", "District Public Health Engineering Office",
                            "District Women Affairs Office", "District Rural Development Office", "District Secondary Education Office", "District Statistics Office",
                            "District Youth Development Office", "District Land Office", "District Health Complex", "District Social Services Office",
                            "Government College", "Government High School", "District Service Center"};
                    ArrayAdapter<String> sAdapter = new ArrayAdapter<>(
                            getApplicationContext(),
                            android.R.layout.simple_spinner_item,
                            officeLevels
                    );

                    sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sOfficeSpinner.setAdapter(sAdapter);

                    sOfficeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            fOffice = parentView.getItemAtPosition(position).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                        }
                    });
                }

                else if ("Division".equals(office)) {
                    sOfficeSpinner.setVisibility(View.VISIBLE);

                    String[] officeLevels = {"Select Division Office","Office of the Divisional Commissioner", "Division Family Planning Office", "Division Election Office",
                            "Division Education Office","ICT Division", "Division Cooperative Office", "Division Accounts Office", "Police Station", "Office of the Division Engineer",
                            "Division Ansar and VDP Office", "Division Project Implementation Office", "Division Food Office", "Office of the Division Chairman",
                            "Division Agriculture Office", "Division Livestock Office", "Division Fisheries Office", "Division Public Health Engineering Office",
                            "Division Women Affairs Office", "Division Rural Development Office", "Division Secondary Education Office", "Division Statistics Office",
                            "Division Youth Development Office", "Division Land Office", "Division Health Complex", "Division Social Services Office",
                            "Government College", "Government High School", "Division Service Center"};
                    ArrayAdapter<String> sAdapter = new ArrayAdapter<>(
                            getApplicationContext(),
                            android.R.layout.simple_spinner_item,
                            officeLevels
                    );

                    sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sOfficeSpinner.setAdapter(sAdapter);

                    sOfficeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            fOffice = parentView.getItemAtPosition(position).toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                        }
                    });
                }

                else {
                    sOfficeSpinner.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });



        //blood spinner
        String[] blood = {"Select Blood Group", "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
        ArrayAdapter<String> bloodAdapter = new ArrayAdapter<>(
                getApplicationContext(),
                android.R.layout.simple_spinner_item,
                blood
        );

        bloodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodSpinner.setAdapter(bloodAdapter);

        bloodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                fblood = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });


        //department spinner
        String[] department = {"Select Ministry", "Ministry of Chittagong Hill Tracts Affairs", "Ministry of Commerce", "Ministry of Cultural Affairs",
                "Ministry of Defence", "Ministry of Disaster Management and Relief", "Ministry of Education", "Ministry of Environment, Forest and Climate Change",
                "Ministry of Expatriates' Welfare and Overseas Employment", "Ministry of Fisheries and Livestock", "Ministry of Finance", "Ministry of Food",
                "Ministry of Foreign Affairs", "Medical Education and Family Welfare Division", "Health Services Division","Ministry of Home Affairs",
                "Ministry of Housing and Public Works", "Ministry of Industries", "Ministry of Information and Broadcasting", "Ministry of Labour and Employment",
                "Ministry of Land", "Ministry of Law, Justice and Parliamentary Affairs", "Ministry of Liberation War Affairs", "Ministry of Local Government, Rural Development and Co-operatives",
                "Ministry of Planning", "Ministry of Posts, Telecommunications and Information Technology", "Ministry of Power, Energy and Mineral Resources",
                "Ministry of Primary and Mass Education", "Ministry of Public Administration", "Ministry of Railways", "Ministry of Religious Affairs",
                "Ministry of Road Transport and Bridges", "Ministry of Science and Technology", "Ministry of Shipping", "Ministry of Social Welfare",
                "Ministry of Textiles and Jute", "Ministry of Water Resources", "Ministry of Women and Children Affairs", "Ministry of Youth and Sports"};
        ArrayAdapter<String> deptAdapter = new ArrayAdapter<>(
                getApplicationContext(),
                android.R.layout.simple_spinner_item,
                department
        );

        deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentSpinner.setAdapter(deptAdapter);


        EditText searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String searchdText = s.toString().toLowerCase(Locale.getDefault());

                ArrayList<String> filteredDepartments = new ArrayList<>();
                if (searchdText.isEmpty()) {
                    filteredDepartments.addAll(Arrays.asList(department));
                } else {
                    for (String department : department) {
                        if (department.toLowerCase(Locale.getDefault()).contains(searchdText)) {
                            filteredDepartments.add(department);
                        }
                    }
                }

                ArrayAdapter<String> filteredAdapter = new ArrayAdapter<>(
                        getApplicationContext(),
                        android.R.layout.simple_spinner_item,
                        filteredDepartments
                );

                filteredAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                departmentSpinner.setAdapter(filteredAdapter);
            }
        });


        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                fdepartment = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });


        //directorate spinner
        String[] directorate = {"Select Directorate", "Agriculture Information Service (AIS)", "Ashugonj Power Station Company Ltd.", "Agrani Bank Limited", "Anti-Corruption Commission",
                "Bangladesh Agricultural Development Corporation (BADC)", "Bangladesh Atomic Energy Commission", "Bangladesh Atomic Energy Regulatory Authority",
                "Bangladesh Agricultural Research Council (BARC)", "Bangladesh Film and Television Institute", "Bangladesh Betar",
                "BANGLADESH FISHERIES DEVELOPMENT CORPORATION (BFDC)", "Bandarban Hill District Council", "Bangladesh Space Research and Remote Sensing Organization",
                "Bangladesh Medical and Dental Council", "Bangladesh Overseas Employment and Services Limited (BOESL)", "Bangladesh Tourism Board",
                "Bangabandhu Sheikh Mujibur Rahman Novo Theatre", "Bangladesh Ansar and VDP", "Bureau of Mineral Development (BMD)", "Bangladesh Accreditation Board",
                "Bangladesh Bureau of Educational Information & Statistics (BANBEIS)", "Bangla Academy", "Bangladesh Bank", "Bangladesh National Museum",
                "Bangladesh Reference Institute for Chemical Measurements (BRICM)", "Bangladesh Gas Fields Company Limited (BGFCL)", "Bangladesh Veterinary Council",
                "Bangladesh Scouts", "Bangladesh National Scientific and Technical Documentation Centre", "Bangabandhu Poverty Eradication & Rural Development Academy (BAPERD)",
                "Bangladesh Rural Development Academy (BARD)", "Bangladesh Agricultural Research Institute (BARI)", "Bangladesh Bridge Authority", "Bangladesh Bureau of Statistics",
                "Bangladesh Computer Council (BCC)", "Bangladesh Climate Change Trust", "Bangladesh Chemical Industries Corporation (BCIC)",
                "Bangladesh College of Physicians and Surgeons", "Bangladesh Civil Service Administration Academy", "Bangladesh Council of Science and Industrial Research (BCSIR)",
                "Bangladesh Energy Regulatory Commission", "Bangladesh Economic Zones Authority", "Bangladesh Film Archive", "Bangladesh Film Sensor Board", "Bangladesh Freedom Fighter Welfare Trust",
                "Bangladesh Forest Industries Development Corporation (BFIDC)", "Bangladesh Forest Department", "Bangladesh Forest Research Institute (BFRI)", "Bangladesh Foreign Trade Institute",
                "Bakhrabad Gas Distribution Company Limited (BGDCL)", "Bangladesh Handloom Board (BHB)", "Bangladesh House Building Finance Corporation",
                "Department of Bangladesh Haor and Wetlands Development", "BIAM Foundation", "Bangladesh Institute of Bank Management", "Bangladesh Institute of Development Studies (BIDS)",
                "Bangladesh Institute of International and Strategic Studies (BIISS)", "Bangladesh Institute of Law and International Affairs (BILIA)", "Bangladesh Institute of Management (BIM)",
                "Biman Bangladesh Airlines", "Bangladesh Institute of Nuclear Agriculture (BINA)", "Bangladesh Institute of Research and Training on Applied Nutrition (BIRTAN)",
                "Bangladesh Nursing and Midwifery Council", "Board of Intermediate and Secondary Education, Chittagong", "Board of Intermediate and Secondary Education, Sylhet",
                "Bangladesh Industrial and Technical Assistance Center (BITAC)", "Bangladesh Inland Water Transport Authority", "Bangladesh Inland Water Transport Corporation",
                "Bangladesh Jute Mills Corporation (BJMC)", "Bangladesh Jute Research Institute (BJRI)", "Bangladesh Karmachari Kallyan Board (BKKB)", "Bangladesh Krira Shikkha Protisthan (BKSP)",
                "BANGLADESH LIVESTOCK RESEARCH INSTITUTE (BLRI)", "Bangladesh Meteorological Department", "Barind Multipurpose Development Authority (BMDA)", "Bangladesh Madrasah Education Board",
                "Bureau of Manpower, Employment and Training", "Bangladesh Madrasha Teachers' Training Institute (BMTTI)", "Bangladesh National Commission of UNESCO", "Bureau of Non-Formal Education (BNFE)",
                "Bangladesh National Herbarium (BNH)", "Bangladesh Ordnance Factories", "Bangladesh Investment Development Authority", "Bangladesh Public Administration Training Centre (BPATC)",
                "Bangladesh Power Development Board", "Bangladesh Public Service Commission", "Bangladesh Rural Development Board - BRDB", "Bangladesh Rice Research Institute",
                "Bangladesh Road Transport Authority", "Bangladesh Road Transport Corporation", "Bangladesh Sericulture Development Board (BSDB)", "Bangladesh Land Port Authority",
                "Bangladesh Samabaya Bank Ltd.", "Bangladesh Shipping Corporation", "Bangladesh Submarine Cable Company Limited (BSCCL)", "Bangladesh Small and Cottage Industries Corporation (BSCIC)",
                "Bangladesh Steel & Engineering Corporation (BSEC)", "Bangladesh Sugar & Food Industries Corporation", "Bangladesh Sugarcane Research Institute (BSRI)",
                "Bangladesh Sericulture Research and Training Institute (BSRT)", "Bangladesh Sangbad Sangshta", "Bangladesh Standards and Testing Institution (BSTI)",
                "Bangladesh Telecommunications Company Limited (BTCL)", "Bangladesh Technical Education Board", "Bangladesh Textiles Mills corporation",
                "Bangladesh Telecommunication Regulatory Commission", "Bangladesh Tea Research Institute (BTRI)", "Bangladesh Television", "Bangladesh Water Development Board",
                "Bangladesh Comptroller and Auditor General", "Bangladesh Coast Guard", "Bangladesh Petroliam Institute", "Bangladesh Export Promotion Bureau",
                "Bangladesh Export Processing Zones Authority (BEPZA)", "Bangladesh Film Development Corporation (BFDC)", "BANGLADESH FIRE SERVICE AND CIVIL DEFENCE",
                "Bangladesh Folk Art and Craft Foundation", "BANGLADESH FISHERIES RESEARCH INSTITUTE (BFRI)", "Bangladesh Hajj Office", "Bangladesh Hydrocarbon Unit",
                "Bangladesh Hi-Tech Park", "Bangladesh Islamic Foundation", "Bangladesh Judicial Service Commission", "Bangladesh Krishi Bank", "Bangladesh Marine Academy",
                "Bangladesh Shishu Academy", "Bangladesh Tea Board", "Bangladesh Parjatan Corporation", "Bangladesh Supreme Court", "Bangladesh Oil, Gas and Mineral Corporation",
                "Bangladesh Press Council", "Board of Intermediate and Secondary Education, Dhaka", "Bangladesh Railway", "Bangladesh Securities and Exchange Commission",
                "Central Fund, MOLE", "Civil Aviation Authority of Bangladesh", "Controller General Defence Finance", "Custom House Dhaka", "Cotton Development Board",
                "Chittagong City Corporation", "Center for Environmental and Geographic Information Services", "Custom House Chittagong", "Chittagong Hilltracts Development Board",
                "Child Labour Unit", "Chief Inspector of Factory and Establishment", "Copyright Office Bangladesh", "Chittagong Port Authority", "Cyclone Preparedness Programme (CPP)",
                "Central Procurement Technical Unit", "Chittagong Stock Exchange Ltd.", "Chittagong WASA", "Chittagong Hilltracts Regional Council", "Chittagong Development Authority",
                "Department of Agricultural Extension", "Directorate of Inspection and Audit (DIA)", "Directorate of Primary Education", "Department of Architecture", "Directorate of Posts",
                "Directorate General Defence Purchase", "Department of Cooperatives", "Department of Agricultural Marketing (DAM)", "Department of Relief & Rehabilitation (DRR)",
                "Disaster Management Department (DMD)", "Dhaka Power Distribution Company", "Department of Films & Publications", "Directorate General of Drug Administration (DGDA)",
                "Directorate General of Food", "Directorate General of Family Planning", "Directorate General of Health Services (DGHS)", "Dhaka Mass Transit Company Limited (DMTCL)",
                "Pharmacy Council of Bangladesh", "Department of Government Transport", "Dhaka South City Corporation", "Department of Immigration and Passports", "Department of Livestock Services",
                "Disaster Management Bureau (DMB)", "Dhaka Metropolitan Police", "Department of Narcotics Control", "Dhaka North City Corporation", "Directorate of National Consumer Rights Protection",
                "Directorate of Nursing", "Department of Environment", "Directorate of Information & Communication Technology", "Department of Jute (DOJ)", "Department of Labour",
                "Department of Shipping", "Department of Textiles", "Dhaka Power Distribution Company Limited", "Department of Patents, Designs and Trademarks (DPDT)",
                "Department of Public Health Engineering", "Department of Printing and Publications", "Directorate of Sports", "Defence Services Command and Staff College",
                "Dhaka Stock Exchange", "Directorate of Secondary and Higher Education (DSHE)", "Department of Social Services", "Dhaka Transport Co-ordination Authority",
                "Department of Women Affairs", "Dhaka WASA", "Department of Youth Development", "Department of Explosives", "Directorate of Archives and Libraries",
                "Directorate of Registration", "Directorate of Mass Communication", "Department of National Savings", "Department of Prisons", "Department of Public Libraries",
                "Directorate of Technical Education", "Department of Urban Development (UDD)", "Directorate of Telecommunications", "Election Commission", "Education Engineering Department (EED)",
                "Electricity Generation Company Limited", "Flood Forecasting and Warning Center", "Governance Innovation Unit", "Geological Survey of Bangladesh", "Housing & Building Research Institute (HBRI)",
                "Health Engineering Department (HED)", "Health Economics Unit", "Hindu Religious Welfare Trust", "International Mother Language Institute", "Intermediate & Secondary Education Boards, Bangladesh",
                "Institute of Chartered Accountants of Bangladesh", "Investment Corporation of Bangladesh", "International Centre for Diarrhoeal Disease Research, Bangladesh (ICDDRB)",
                "Institute of Cost and Management Accountants", "Insurance Development & Regulatory Authority Bangladesh", "Information Commission", "Institute of Water Modelling (IWM)", "Janata Bank Limited",
                "Jiban Bima Corporation", "Jute Diversification Promotion Centre", "Joint River Commission, Bangladesh", "Khulna Development Authority", "Khulna City Corporation",
                "Khagrachari Hill District Council", "Khulna WASA", "Karnaphuli Gas Distribution Company Limited", "Law Commission-Bangladesh", "Local Government Engineering Department (LGED)",
                "Land Reform Board", "Land Record and Survey Department", "Land Appeal Board", "Military Institute of Science & Technology", "MARINE FISHERIES ACADEMY (MFA)", "Minimum Wages Board",
                "Mongla Port Authority", "Microcredit Regulatory Authority", "National Freedom Fighters Welfare Trust", "National Museum Of Science and Technology",
                "National Academy for Computer Training and Research (NACTAR)", "National Academy for Educational Management (NAEM)", "National e-Government Procurement",
                "National Women Organization", "National Academy for Planning and Development (NAPD)", "National Academy for Primary Education", "National Board of Revenue (NBR)",
                "Narayanganj City Corporation", "National Curriculum and Textbook Board (NCTB)", "National Defence College", "National Foundation for Development of the Disabled Persons",
                "NGO Affairs Bureau", "National Housing Authority (NHA)", "National Human Rights Commission Bangladesh", "National Institute of Biotechnology",
                "National Institute of Local Government", "National Institute of Mass Communication", "National Institute of Population Research and Training (NIPORT)",
                "National Legal Aid Services Organization", "National Maritime Institute", "National Productivity Organization (NPO)", "National Sports Council",
                "Non-government Teachers Registration and Certification Authority (NTRCA)", "Office of the Controller of Certifying Authorities (CCA)",
                "Office of the Chief Controller of Imports & Exports", "Office of the Controller General of Accounts", "Office of the Attorney General",
                "Office of the Registrar of Joint Stock Companies and Farms", "Office of The Chief Inspector of Boilers", "Pay and Service Commission 2013",
                "Probashi Kallyan Bank", "Palli Daridro Bimochon Foundation (PDBF)", "Power Grid Company of Bangladesh", "Press Institute of Bangladesh",
                "Palli Karma-Sahayak Foundation", "Planning Commission", "Police Headquarters, Bangladesh Police", "Power Cell", "Public Private Partnership Office",
                "Press Information Department (PID)", "Public Works Department", "Pashchimanchal Gas Company Limited (PGCL)", "Rapid Action Battalion",
                "Rajdhani Unnayan Kartipakkha (RAJUK)", "Rajshahi Krishi Unnayan Bank", "Rural Development Academy (RDA) Bogra", "Rajshahi Unnayan Authority (RDA)",
                "Rural Electrification Board", "Roads and Highways Department", "Rangamati Hill District Council",
                "Rural Power Company Limited", "Rupantarita Prakritik Gas Company Ltd.", "River Research Institute (RRI)", "Rupali Bank Limited", "Solicitor Wing",
                "Saarc Agriculture Information Center (SAC)", "Sadharan Bima Corporation", "Seed Certification Agency (SCA)", "Small Farmer Development Foundation (SFDF)",
                "Sylhet Gas Fields Limited", "Small & Medium Enterprise Foundation (SMEF)", "Survey Of Bangladesh", "Sonali Bank Limited", "Soil Resource Development Institute (SRDI)",
                "Special Security Force", "Sylhet City Corporation", "Trading Corporation of Bangladesh (TCB)", "THE DEPARTMENT OF FISHERIES (DOF)",
                "The Security Printing Corporation (Bangladesh) Ltd.", "THE CHRISTIAN RELIGIOUS WELFARE TRUST (CRWT)", "The American Institute of Bangladesh Studies (AIBS)",
                "Teletalk Bangladesh", "Titas Gas Transmission and Distribution Company", "Telephone Shilpo Sangshta", "University Grants Commission (UGC)",
                "Waqf Administration", "Water Resources Planning Organization (WARPO)", "West Zone Power Distribution Company Ltd.", "Agency to Innovate (A2i)"};
        ArrayAdapter<String> directorateAdapter = new ArrayAdapter<>(
                getApplicationContext(),
                android.R.layout.simple_spinner_item,
                directorate
        );

        directorateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        directorateSpinner.setAdapter(directorateAdapter);


        EditText searchText = findViewById(R.id.searchText);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String searchText = s.toString().toLowerCase(Locale.getDefault());

                ArrayList<String> filtereDirectorate = new ArrayList<>();
                if (searchText.isEmpty()) {
                    filtereDirectorate.addAll(Arrays.asList(directorate));
                } else {
                    for (String directorate : directorate) {
                        if (directorate.toLowerCase(Locale.getDefault()).contains(searchText)) {
                            filtereDirectorate.add(directorate);
                        }
                    }
                }

                ArrayAdapter<String> filteredAdapter = new ArrayAdapter<>(
                        getApplicationContext(),
                        android.R.layout.simple_spinner_item,
                        filtereDirectorate
                );

                filteredAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                directorateSpinner.setAdapter(filteredAdapter);
            }
        });

        directorateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                fdirectorate = parentView.getItemAtPosition(position).toString();
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
                if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                } else {
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
                final String designation_type = mdesignation_type.getText().toString();
                String password = mPass.getText().toString().trim();

                final String[] radioSelection = {"None"};

                RadioGroup radioGroup = findViewById(R.id.radioGroup);
                RadioButton radioYes = findViewById(R.id.radioYes);
                RadioButton radioNo = findViewById(R.id.radioNo);
                int selectedId = radioGroup.getCheckedRadioButtonId();

                if (selectedId == radioYes.getId()) {
                    radioSelection[0] = "Yes";
                } else if (selectedId == radioNo.getId()) {
                    radioSelection[0] = "No";
                } else {
                    Toast.makeText(Register.this, "Please select an option (Yes/No)", Toast.LENGTH_SHORT).show();
                    return;
                }

                final String selectedDivision = divisionSpinner.getSelectedItem().toString();
                final String selectedDistrict = districtSpinner.getSelectedItem().toString();
                final String selectedUpozila = upozilaSpinner.getSelectedItem().toString();

                if (Pattern.compile("\\s").matcher(email).find()) {
                    mEmail.setError("Email cannot contain whitespace");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(name)) {
                    mName.setError("Name is required");
                    return;
                }

                if (TextUtils.isEmpty(designation_type)) {
                    mdesignation_type.setError("Designation is required");
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

                if ("Click here".equals(office)) {
                    Toast.makeText(getApplicationContext(), "Office selection is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if ("Click here".equals(designation)) {
                    Toast.makeText(getApplicationContext(), "Designation is required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if ("Select Blood Group".equals(fblood)) {
                    Toast.makeText(getApplicationContext(), "Blood Group is not selected", Toast.LENGTH_SHORT).show();
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
                                    startActivity(new Intent(getApplicationContext(), Login.class));

                                    userID = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                                    DocumentReference documentReference = fstore.collection("users").document(userID);

                                    Map<String, Object> user = new HashMap<>();
                                    user.put("name", name);
                                    user.put("address", address);
                                    user.put("workstation", workstation);
                                    user.put("email", email);
                                    user.put("designation_type", designation_type);
                                    user.put("nid", nid);
                                    user.put("dob", dob);
                                    user.put("designation", fdesignation);
                                    user.put("office", fOffice);
                                    user.put("department", fdepartment);
                                    user.put("directorate", fdirectorate);
                                    user.put("blood", fblood);
                                    user.put("username", username);
                                    user.put("number", pnumber);
                                    user.put("isAdmin", "No");
                                    user.put("isApproved", "No");
                                    user.put("head", radioSelection[0]);
                                    user.put("division", selectedDivision);
                                    user.put("district", selectedDistrict);
                                    user.put("upozila", selectedUpozila);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, "Location permission is required.", Toast.LENGTH_SHORT).show();
            }

            if (grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, "Camera permission is required.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}