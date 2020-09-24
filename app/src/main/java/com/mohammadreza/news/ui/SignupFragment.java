package com.mohammadreza.news.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.mohammadreza.news.R;
import com.mohammadreza.news.databinding.FragmentSignupBinding;
import com.mohammadreza.news.viewmodel.SignUpViewModel;

public class SignupFragment extends Fragment {

    //    private Button btnJoinUs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentSignupBinding signupBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.fragment_signup, container, false);
        SignUpViewModel signupViewModel = new SignUpViewModel((AppCompatActivity) getActivity());
        signupBinding.setSignupVm(signupViewModel);
        return signupBinding.getRoot();
    }


}
