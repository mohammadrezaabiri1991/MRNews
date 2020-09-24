package com.mohammadreza.news.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.mohammadreza.news.R;
import com.mohammadreza.news.authentication.Authentication;
import com.mohammadreza.news.databinding.ActivityRegisterBinding;
import com.mohammadreza.news.model.UserModel;
import com.mohammadreza.news.utils.Utils;
import com.mohammadreza.news.viewmodel.RegisterViewModel;

import java.util.Objects;

import static com.mohammadreza.news.constant.NewsConstant.RC_SIGN_IN;

public class RegisterActivity extends AppCompatActivity {
    public ProgressDialog progressDialog;
    private CallbackManager callbackManager;
    private int numberOfClick;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityRegisterBinding registerBinding = DataBindingUtil.setContentView(RegisterActivity.this, R.layout.activity_register);
        callbackManager = CallbackManager.Factory.create();
        RegisterViewModel viewModel = new RegisterViewModel(RegisterActivity.this, callbackManager);
        registerBinding.setRegister(viewModel);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task);
        }
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            UserModel userModel = new UserModel();
            if (account != null) {
                userModel.setEmailDisplayName(account.getDisplayName());
                userModel.setImageProfileUri(String.valueOf(account.getPhotoUrl()));
            }
            Authentication.authCredential(userModel, this);
        } catch (ApiException e) {
            Log.d("ApiException","error ==>  " +e.getMessage());
            if (!Objects.requireNonNull(e.getMessage()).contains(getString(R.string.exception_cancel))) {
                Utils.showAlertDialog(this, getString(R.string.wrong));
            }
            Utils.hideProgressDialog(this);
        }
    }


    //    private void actionOnActivityResult(Intent data) {
//        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//        try {
//            // Google Sign In was successful, authenticate with Firebase
//            GoogleSignInAccount account = task.getResult(ApiException.class);
//            if (account != null) {
//                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
//                FirebaseAuthentication.firebaseAuthCredential(credential, this);
//            } else {
//                Utils.showAlertDialog(this, getString(R.string.account_dose_not_exist));
//            }
//        } catch (final ApiException e) {
//            Utils.hideProgressDialog(RegisterActivity.this);
////            Utils.showAlertDialog(this, getString(R.string.install_update_play_services));
//        }
//    }
    @Override
    public void onBackPressed() {
        numberOfClick += 1;
        if (numberOfClick == 1) {
            Toast.makeText(this, R.string.exit_app_message, Toast.LENGTH_SHORT).show();
        } else if ((numberOfClick > 1)) {
            this.finishAffinity();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAndRemoveTask();
            }
        }
        new Handler().postDelayed(() -> numberOfClick = 0, 2000);
    }
}
