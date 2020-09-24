package com.mohammadreza.news.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.mohammadreza.news.R;
import com.mohammadreza.news.databinding.FragmentLoginBinding;
import com.mohammadreza.news.viewmodel.LoginViewModel;


public class LoginFragment extends Fragment {

//    public static LinearLayout btnGoogleLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentLoginBinding loginBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_login, container, false);
        LoginViewModel loginViewModel = new LoginViewModel((AppCompatActivity) getActivity());
        loginBinding.setLogin(loginViewModel);
//        btnGoogleLogin = loginBinding.linearGoogleSignUp;

        return loginBinding.getRoot();
    }

}
