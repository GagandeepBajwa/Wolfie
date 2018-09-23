package com.example.t00585418.firegooglein;

import android.*;
import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SecondOne extends AppCompatActivity {

    Button button;
    Button Subscribe;
    Button Downloaded;
    private final String TAG = "WHat is this";
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    private final int GALLERY_INTENT = 2;
    StorageReference imagesRef;
    DatabaseReference mDataReference;
    FirebaseFirestore db;
    FirebaseUser user;
    private RecyclerView mDataList;
private static final
String Job_Tag = "my_job_tag";
private FirebaseJobDispatcher jobDispatcher;




    final static int PICK_PDF_CODE = 2342;

    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    TextView t1;
    TextView t2;
    final double range=6;
    //vars
    private boolean mLocationPermissionGranted=false;
    private FusedLocationProviderClient mFusedLocationProviderClient;



    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_one);


         Downloaded=(Button) findViewById(R.id.ShowDownloads);
        t2=(TextView) findViewById(R.id.editText);



     // startService(new Intent(this, MyFirebaseInstanceIDService.class));
    //  startService(new Intent(this, MyFirebaseMessagingService.class));

         startService(new Intent(this, MyBackgroundService.class));





      //getting instance tto check the notification channel(just for testing services)
       FirebaseMessaging.getInstance().subscribeToTopic("Testing");

        Subscribe=(Button)findViewById(R.id.notiBtn);
        Subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Subscription();


            }
        });

      /*  //code for the notification channel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //setting up the notification manager
            String channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW));

        }
        */
       //this is for the further actions if the user tapped the notification appeared
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }


        //getting the registration token for the user
        String refreshedToken= FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token"+refreshedToken);



        if (Build.VERSION.SDK_INT < 28) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }


        button = (Button) findViewById(R.id.logout);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(SecondOne.this, MainActivity.class));
                }
            }
        };

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
            }
        });

        // On click listener for the upload button
        Button btnUP = (Button) findViewById(R.id.upBtn);
        btnUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagesRef = FirebaseStorage.getInstance().getReference();
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("*/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });

        //button onclick listener to view downloads
        Downloaded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dIntent=new Intent(SecondOne.this, DownloadActivity.class);
                startActivity(dIntent);
            }
        });


        //getting  firestore instances
        db=FirebaseFirestore.getInstance();
        // getting the auth instance using firebaseUser
        user= FirebaseAuth.getInstance().getCurrentUser();

        //getting the location permission
           getLocationPermission();
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            final  Intent k;
              k=data;



        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {

            final boolean[] upload=new boolean[2];
            upload[0]=false;
            Toast.makeText(SecondOne.this, "Upload Done", Toast.LENGTH_SHORT).show();
            // getting location metadata for the image top be uploaded
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            // It will throw the security exception if it is enable to get the device location
            try {

                final String[] Latitude = new String[30];
                final String[] Longitude = new String[30];
                if (mLocationPermissionGranted) {

                    final Task location = mFusedLocationProviderClient.getLastLocation();
                    location.addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SecondOne.this, "location is successfull", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "onComplete: found location!");
                                Location currentLocation = (Location) location.getResult();
                                double lat = (currentLocation.getLatitude());
                                double log = currentLocation.getLongitude();
                                Latitude[0] = Double.toString(lat);
                                Longitude[0] = Double.toString(log);
                                upload[0] = true;

                              //  t1.setText("lstitude "+Double.toString(lat));

                                Uri uri = k.getData();

                                StorageReference filepath = imagesRef.child("photos").child(uri.getLastPathSegment());

                                mDataReference = FirebaseDatabase.getInstance().getReference("images");
                                //adding file meta data
                                String k1 = "Latitude";
                                String k2 = "Longitude";
                                String email="Email";
                                String r="Range";
                                StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpg").setCustomMetadata(k1, Latitude[0]).setCustomMetadata(k2, Longitude[0]).setCustomMetadata(email,user.getEmail()).setCustomMetadata(r, Double.toString(range)).build();
                                //file upload listener
                                filepath.putFile(uri, metadata)
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                String userEmail=taskSnapshot.getMetadata().getCustomMetadata("Email");
                                                String name = taskSnapshot.getMetadata().getName();
                                                String url = taskSnapshot.getDownloadUrl().toString();
                                                String latData=taskSnapshot.getMetadata().getCustomMetadata("Latitude");
                                                String longData=taskSnapshot.getMetadata().getCustomMetadata("Longitude");
                                                String accRange=taskSnapshot.getMetadata().getCustomMetadata("Range");

                                                addToFirestore(name, url, latData, longData, userEmail, accRange);
                                                Toast.makeText(SecondOne.this, "Upload Done", Toast.LENGTH_SHORT).show();
                                            }

                                            //firebase database


                                        });


                            }

                        }

                    });
                }
            }
            catch(SecurityException e)
            {
                Log.e(TAG, "getDeviceLocation: Security Exception"+ e.getMessage());
            }





        }


    }



//getting location permission method

    private void  getLocationPermission()
    {

        Log.d(TAG, "gettingLocationPermission: getting location permissons");
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;

            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }


    }

    // Permission results
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionGranted = true;
                    //initialize our map
                }
            }
        }
    }



//method to put data in the firestore realtime database

  public void addToFirestore(String name, String url, String latData, String longData, String userEmail, String accRange )
  {
      Map<String, Object> data= new HashMap<>();

      data.put("Name", name);
      data.put("DownloadUrl", url );
      data.put("LatitudeCoordinates", latData);
      data.put("LongitudeCoordinates", longData);
      data.put("AccessibleRange", accRange);
      db.collection("Users").document(userEmail).collection("DataUploaded").document(name).set(data, SetOptions.merge());

  }


//
    public void Subscription()
    {
        Toast.makeText(SecondOne.this, "Subscribed to Testing", Toast.LENGTH_SHORT).show();
       // FirebaseMessaging.getInstance().unsubscribeFromTopic("Tttt");
        FirebaseMessaging.getInstance().subscribeToTopic("Te");
    }




}



