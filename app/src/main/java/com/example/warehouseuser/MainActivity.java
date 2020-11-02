package com.example.warehouseuser;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.warehouseuser.fragment.StartFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class MainActivity extends AppCompatActivity {

    private TextView signOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initActionBar();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragment_placeholder, new StartFragment());
        ft.commit();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        View view = LayoutInflater.from(this).inflate(R.layout.appbar, null);

        actionBar.setCustomView(view, layoutParams);

        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#FFAA66CC"));
        actionBar.setBackgroundDrawable(colorDrawable);

        signOut = view.findViewById(R.id.sign_out_button);
        signOut.setVisibility(View.INVISIBLE);
        signOut.setOnClickListener(this::onOptionsItemSelected);
    }

    public void showSignOutButtonWhichIsATextView() {
        signOut.setVisibility(View.VISIBLE);
    }

    public void onOptionsItemSelected(View view) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut();

        SessionManager manager = new SessionManager(this);
        manager.removeData();

        signOut.setVisibility(View.INVISIBLE);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, new StartFragment());
        ft.commit();
    }
}