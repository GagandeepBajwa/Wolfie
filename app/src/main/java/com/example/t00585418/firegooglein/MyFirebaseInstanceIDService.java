package com.example.t00585418.firegooglein;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private final String TAG = "WHat is this";


    public MyFirebaseInstanceIDService() {
    }


    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        // we are retreiving the registratiopn token every time new registration token is generated
        String refreshedToken= FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token"+refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }



    public void sendRegistrationToServer(String token)
    {



    }




}
