package com.example.warehouseuser.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.warehouseuser.R;
import com.example.warehouseuser.RequestResponseStatus;
import com.example.warehouseuser.api.RestApi;
import com.example.warehouseuser.fragment.update.OnAuthenticationUpdate;
import com.google.android.material.snackbar.Snackbar;

public class SignInFragment extends Fragment implements OnAuthenticationUpdate {

    private RestApi api;
    private Button signIn;
    private boolean emptyUsername;
    private boolean emptyPassword;
    private EditText username;
    private EditText password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sign_in_view, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initButtons();
        initEditTextFields();
        api = new RestApi(this.getContext());
    }

    private void initButtons() {
        Button back = getActivity().findViewById(R.id.go_back);
        back.setOnClickListener(view -> {
            Log.i("Screen", "Back to start view from sign in view");
            FragmentManager fm = getFragmentManager();
            fm.popBackStack();
        });

        signIn = getActivity().findViewById(R.id.sign_in);
        signIn.setOnClickListener(view -> {
            api.getToken( this, username.getText().toString(), password.getText().toString());
        });
        signIn.setEnabled(false);
    }

    private void initEditTextFields() {
        emptyUsername = true;
        username = getActivity().findViewById(R.id.username);
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    emptyUsername = false;
                    if (!emptyPassword) {
                        signIn.setEnabled(true);
                    }
                } else {
                    emptyUsername = true;
                    signIn.setEnabled(false);
                }
            }
        });

        emptyPassword = true;
        password = getActivity().findViewById(R.id.password);
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    emptyPassword = false;
                    if (!emptyUsername) {
                        signIn.setEnabled(true);
                    }
                } else {
                    emptyPassword = true;
                    signIn.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void onAuthentication(RequestResponseStatus status) {
        if (status == RequestResponseStatus.OK) {
            Log.i("Screen", "Go to list view");
            api.saveUserRole();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            fm.popBackStack();
            ft.replace(R.id.fragment_placeholder, new ListFragment()).addToBackStack(null);
            ft.commit();
        }
        else if (status == RequestResponseStatus.BAD_CREDENTIALS) {
            Snackbar mySnackbar = Snackbar.make(getActivity().findViewById(R.id.sign_in),
                    getString(R.string.signIn_failed), Snackbar.LENGTH_LONG);
            mySnackbar.show();
        }
        else if (status == RequestResponseStatus.TIMEOUT) {
            Snackbar mySnackbar = Snackbar.make(getActivity().findViewById(R.id.sign_in),
                    getString(R.string.connection_timeout), Snackbar.LENGTH_INDEFINITE);
            mySnackbar.setAction(getString(R.string.retry_connection),
                    view12 -> api.getToken(this, username.getText().toString(), password.getText().toString()));
            mySnackbar.show();
        }
    }
}
