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

public class SignInFragment extends Fragment {

    private Button signIn;
    private boolean emptyUsername;
    private boolean emptyPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sign_in_view, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        initButtons();
        initEditTextFields();
    }

    private void initButtons() {
        Button back = (Button) getActivity().findViewById(R.id.go_back);
        back.setOnClickListener(view -> {
            Log.i("Screen", "Back to start view from sign in view");
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_placeholder, new StartFragment());
            ft.commit();
        });

        signIn = (Button) getActivity().findViewById(R.id.sign_in);
        signIn.setOnClickListener(view -> {
            ///TODO check login & pass
            Log.i("Screen", "Go to list view");
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_placeholder, new ListFragment());
            ft.commit();
        });
        signIn.setEnabled(false);
    }

    private void initEditTextFields() {
        emptyUsername = true;
        EditText username = (EditText) getActivity().findViewById(R.id.username);
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
        EditText password = (EditText) getActivity().findViewById(R.id.password);
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
}
