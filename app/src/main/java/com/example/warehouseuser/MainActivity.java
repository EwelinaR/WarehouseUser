package com.example.warehouseuser;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.warehouseuser.api.RestApi;
import com.example.warehouseuser.fragment.ListFragment;
import com.example.warehouseuser.fragment.StartFragment;
import com.example.warehouseuser.fragment.update.FragmentUpdate;
import com.example.warehouseuser.fragment.update.OnAuthenticationUpdate;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MainActivity extends AppCompatActivity implements FragmentUpdate, OnAuthenticationUpdate {

    private TextView signOut;
    private ImageView sync;
    private RestApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        api = new RestApi(this);

        initActionBar();

        Window window = MainActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark));

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

        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#6200EE"));
        actionBar.setBackgroundDrawable(colorDrawable);

        sync = view.findViewById(R.id.sync_button);
        sync.setVisibility(View.INVISIBLE);
        sync.setOnClickListener(this::sync);

        signOut = view.findViewById(R.id.sign_out_button);
        signOut.setVisibility(View.INVISIBLE);
        signOut.setOnClickListener(this::onOptionsItemSelected);
    }

    public void displayButtonsAfterSignIn() {
        sync.setVisibility(View.VISIBLE);
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
        sync.setVisibility(View.INVISIBLE);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        getSupportFragmentManager().popBackStack();
        ft.replace(R.id.fragment_placeholder, new StartFragment());
        ft.commit();
    }

    public void sync(View view) {
        InternalStorage storage = new InternalStorage(this);
        List<UpdateInstrument> updateInstruments = storage.readUpdates();
        if (updateInstruments.size() > 0) {
            api.sendUpdates(updateInstruments, this);
        } else {
            storage.deleteData();
            goToListFragment();
        }
    }

    @Override
    public void updateView(RequestResponseStatus status) {
        if (status == RequestResponseStatus.TIMEOUT) {
            Snackbar mySnackbar = Snackbar.make(findViewById(R.id.list_view),
                    getString(R.string.connection_timeout), Snackbar.LENGTH_INDEFINITE);
            mySnackbar.setAction(getString(R.string.retry_connection), view12 -> sync(null));
            mySnackbar.show();
            return;
        }
        else if (status == RequestResponseStatus.UNAUTHORIZED) {
            api.refreshToken(this);
            return;
        }

        InternalStorage storage = new InternalStorage(this);
        storage.deleteData();

        goToListFragment();
    }

    private void goToListFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        getSupportFragmentManager().popBackStack();
        ft.replace(R.id.fragment_placeholder, new ListFragment());
        ft.commit();
    }

    @Override
    public void onAuthentication(RequestResponseStatus status) {
        sync(null);
    }
}