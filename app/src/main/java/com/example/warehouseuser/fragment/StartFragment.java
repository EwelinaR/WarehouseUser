package com.example.warehouseuser.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.warehouseuser.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class StartFragment extends Fragment {

    private static final int RC_SIGN_IN = 1;
    private static final String LOGIN_TAG = "LogIn";
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.start_view, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initLogIn();
        initGoogleLogIn();
        tmpInit();
    }

    private void initLogIn() {
        final Button button = (Button) getActivity().findViewById(R.id.sign_in_button);
        button.setOnClickListener(v -> {
            Log.i("Screen", "Go to login view");
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_placeholder, new EditFragment());
            ft.commit();
        });
    }

    private void initGoogleLogIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        SignInButton signInButton = (SignInButton) getActivity().findViewById(R.id.google_sign_in_button);
        signInButton.setOnClickListener(v -> logInWithGoogle());
    }

    private void logInWithGoogle() {
        Log.i(LOGIN_TAG, "Log in with Google");
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Log.i(LOGIN_TAG, "Successfully logged in as a  " + account.getAccount().name);
            String idToken = account.getIdToken();
            // TODO(developer): send ID Token to server and validate
            // TODO updateUI(account);
        } catch (ApiException e) {
            Log.w(LOGIN_TAG, "handleSignInResult:error", e);
            // TODO updateUI(null);
        }
    }

    private void tmpInit() {
        final Button button = (Button) getActivity().findViewById(R.id.api_button);
        button.setOnClickListener(v -> {
            Log.i("Screen", "TMP go to list view from start");
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_placeholder, new ListFragment());
            ft.commit();
        });
    }
}
