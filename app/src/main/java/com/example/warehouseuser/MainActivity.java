package com.example.warehouseuser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

import com.example.warehouseuser.fragment.StartFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            Log.i("LogIn", "User is already logged in with google");
            Log.i("Screen", "Go to start view (WILL BE CHANGED)");
            ft.add(R.id.fragment_placeholder, new StartFragment());
        } else {
            Log.i("Screen", "Go to start view");

            ft.add(R.id.fragment_placeholder, new StartFragment());
        }
        ft.commit();
    }
}