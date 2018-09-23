package com.example.t00585418.firegooglein;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class MyBackgroundService extends Service  {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
   String prov;
    UploadRealtimeLocation t=new UploadRealtimeLocation();
    public static final String ACTION_LOCATION_BROADCAST=MyBackgroundService.class.getName() +"Location Broadcast";


    public MyBackgroundService()
    {
        super();

    }


    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
        /*    try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }
            Log.e("What the hell", "LocationListener ");
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
           // stopSelf(msg.arg1);*/
        }
    }

    private static final String TAG = "MyLocationService";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 10;
    private static final float LOCATION_DISTANCE = 10f;
    Location mLastLocation;



   private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);

        }



        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            sendLocationData(String.valueOf(mLastLocation.getLatitude()), String.valueOf(mLastLocation.getLongitude()));

        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
          //  Log.e(TAG, "onStatusChanged: " + provider);
        }

       // DocumentReference upData = data.collection("OnlineUsers").document(user.getEmail());


       private void sendLocationData(String lat, String lng)
       {
          Log.e("Sending", "latlng");
           FirebaseFirestore data=FirebaseFirestore.getInstance();;
           FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();;
           Map<String, Object> userid=new HashMap<>();
           Log.d("Send", "Sending Location Data");
           //sending the location broadcast
           Intent intent=new Intent(ACTION_LOCATION_BROADCAST);
           intent.putExtra("LATITUDE", lat);
           intent.putExtra("LONGITUDE", lng);
           userid.put("CurrentLatitude", lat);
           userid.put("CurrentLongitude", lng);
           LocalBroadcastManager.getInstance(MyBackgroundService.this).sendBroadcast(intent);
           DocumentReference db = data.collection("OnlineUsers").document(user.getEmail());//.set(userid);
           db.update("CurrentLatitude", lat);
           db.update("CurrentLongitude",lng);


       }


    }



    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };




    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
      mLastLocation=new Location(prov);
        Thread t = new Thread(new Runnable(){
            @Override
            public void run() {


            }
        });
        t.start();
        Log.e(TAG, "onStartCommand");
       // t.run();
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {


        // DocumentReference upData = data.collection("OnlineUsers").document(user.getEmail());


        Log.e(TAG, "onCreate");

        initializeLocationManager();

            try {

                  //Thread.sleep(3000);


                mLocationManager.requestLocationUpdates(
                        LocationManager.PASSIVE_PROVIDER,
                        LOCATION_INTERVAL,
                        LOCATION_DISTANCE,
                        mLocationListeners[0]
                );


            } catch (java.lang.SecurityException ex) {
                Log.i(TAG, "fail to request location update, ignore", ex);
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "network provider does not exist, " + ex.getMessage());
            } //catch (InterruptedException e) {
            //e.printStackTrace();
            //}
            try {

                mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        LOCATION_INTERVAL,
                        LOCATION_DISTANCE,
                        mLocationListeners[1]
                );
            } catch (java.lang.SecurityException ex) {
                Log.i(TAG, "fail to request location update, ignore", ex);
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "gps provider does not exist " + ex.getMessage());
            }
        }


    @Override
    public void onDestroy() {
       // Log.e(TAG, "onDestroy");
        super.onDestroy();
       if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listener, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
       // Log.e(TAG, "initializeLocationManager - LOCATION_INTERVAL: "+ LOCATION_INTERVAL + " LOCATION_DISTANCE: " + LOCATION_DISTANCE);
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

   /* private void sendLocationData() {


        try {
            Thread.sleep(5000 );

        FirebaseFirestore data = FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Map<String, Object> userid = new HashMap<>();
        Log.d("Send", "Sending Location Data");
        //sending the location broadcast
        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra("LATITUDE", String.valueOf(mLastLocation.getLatitude()));
        intent.putExtra("LONGITUDE", String.valueOf(mLastLocation.getLongitude()));
        userid.put("CurrentLatitude", String.valueOf(mLastLocation.getLatitude()));
        userid.put("CurrentLongitude", String.valueOf(mLastLocation.getLongitude()));
        LocalBroadcastManager.getInstance(MyBackgroundService.this).sendBroadcast(intent);
        DocumentReference db = data.collection("OnlineUsers").document(user.getEmail());//.set(userid);
        db.update("CurrentLatitude", String.valueOf(mLastLocation.getLatitude()));
        db.update("CurrentLongitude", String.valueOf(mLastLocation.getLongitude()));
    }
    catch (InterruptedException e) {
        e.printStackTrace();
    }

        sendLocationData();

    }

    //providing the location services provider
    public String getProvider(String Provider)
    {

        return Provider;

    }
       */




}
