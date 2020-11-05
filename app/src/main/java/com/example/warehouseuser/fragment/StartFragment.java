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
import com.example.warehouseuser.RequestResponseStatus;
import com.example.warehouseuser.api.RestApi;
import com.example.warehouseuser.fragment.update.OnAuthenticationUpdate;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

public class StartFragment extends Fragment implements OnAuthenticationUpdate {

    private static final int RC_SIGN_IN = 1;
    private static final String LOGIN_TAG = "LogIn";
    private GoogleSignInClient mGoogleSignInClient;
    private RestApi api;
    private GoogleSignInAccount account;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.start_view, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        api = new RestApi(this.getContext());
        account = GoogleSignIn.getLastSignedInAccount(this.getContext());
        if (account != null) {
            Log.i(LOGIN_TAG, "User is already signed in with google");
            api.getToken(this, account.getEmail(), account.getDisplayName());
        }
        initLogIn();
        initGoogleLogIn();
    }

    private void initLogIn() {
        final Button button =  getActivity().findViewById(R.id.sign_in_button);
        button.setOnClickListener(v -> {
            Log.i("Screen", "Go to login view");
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_placeholder, new SignInFragment()).addToBackStack(null);
            ft.commit();
        });
    }

    private void initGoogleLogIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        SignInButton signInButton = getActivity().findViewById(R.id.google_sign_in_button);
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

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);
            api.getToken(this, account.getEmail(), account.getDisplayName());
        } catch (ApiException e) {
            Log.w(LOGIN_TAG, "handleSignInResult:error", e);
        }
    }

    @Override
    public void onAuthentication(RequestResponseStatus status) {
        if (status == RequestResponseStatus.OK) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_placeholder, new ListFragment());
            ft.commit();
        } else if (status == RequestResponseStatus.BAD_CREDENTIALS) {
            mGoogleSignInClient.signOut();
            Snackbar mySnackbar = Snackbar.make(getActivity().findViewById(R.id.sign_in_button),
                    getString(R.string.bad_credentials), Snackbar.LENGTH_LONG);
            mySnackbar.show();
        } else if (status == RequestResponseStatus.TIMEOUT) {
            mGoogleSignInClient.signOut();
        }
    }
}
