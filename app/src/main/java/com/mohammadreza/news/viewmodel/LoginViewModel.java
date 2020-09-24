package com.mohammadreza.news.viewmodel;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.BaseObservable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;

import com.budiyev.android.circularprogressbar.CircularProgressBar;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.mohammadreza.news.R;
import com.mohammadreza.news.authentication.Authentication;
import com.mohammadreza.news.constant.NewsConstant;
import com.mohammadreza.news.databinding.ForgotPassBinding;
import com.mohammadreza.news.model.UserModel;
import com.mohammadreza.news.repository.NewsRealmDatabase;
import com.mohammadreza.news.ui.RegisterActivity;
import com.mohammadreza.news.utils.MyAnimationUtils;
import com.mohammadreza.news.utils.SendMailTask;
import com.mohammadreza.news.utils.Utils;

import java.util.Arrays;

import static com.mohammadreza.news.constant.NewsConstant.KEEP_ONLINE;
import static com.mohammadreza.news.utils.MyAnimationUtils.animationShakeError;
import static com.mohammadreza.news.utils.MyAnimationUtils.animationTransactionToCircle;
import static com.mohammadreza.news.utils.MyAnimationUtils.animationTransactionToRound;
import static com.mohammadreza.news.utils.Utils.checkPassValidation;
import static com.mohammadreza.news.utils.Utils.showProgressDialog;

public class LoginViewModel extends BaseObservable {
    private AppCompatActivity appCompatActivity;
    private ObservableInt isProgressForgotVisible = new ObservableInt(View.INVISIBLE);
    private ObservableBoolean isDialogShow = new ObservableBoolean(false);
    public static Dialog forgotPassDialog;

    public LoginViewModel(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    public void onClickHandlerGoLogin(View btnGo, EditText edtUserName, EditText edtPassword, FrameLayout mFrameLayout, CircularProgressBar progressBar, CheckBox checkBox) {
        actionBtnGo(btnGo, edtUserName, edtPassword, mFrameLayout, progressBar, checkBox);
    }

    public void onClickKeepMeLogin(View v, CheckBox checkBox) {
        if (checkBox.isChecked()) {
            checkBox.setChecked(false);
        } else {
            checkBox.setChecked(true);
        }
    }


    public void onClickHandlerGoogleAndFacebook(View btnFaceAndGoogle) {
        if (Utils.isOnline(appCompatActivity)) {
            btnFaceAndGoogle.setClickable(false);

            if (btnFaceAndGoogle.getTag().equals(btnFaceAndGoogle.getContext().getResources().getString(R.string.linearFacebookLogin))) {

                showFacebookCheckVpnDialog(btnFaceAndGoogle, appCompatActivity, appCompatActivity.getString(R.string.vpn_check));

            } else if (btnFaceAndGoogle.getTag().equals(btnFaceAndGoogle.getContext().getString(R.string.linearGoogleLogin))) {
                actionLinearGoogleLogin(btnFaceAndGoogle);
            }
        } else {
            Utils.showAlertDialog(appCompatActivity, appCompatActivity.getString(R.string.no_access_to_net));
        }
    }


    public void onClickForgotSendPasswordToEmail(View view, EditText editTextEmail, LinearLayout linearLayout) {
        if (!TextUtils.isEmpty(editTextEmail.getText().toString())) {
            if (Utils.isEmailValid(editTextEmail.getText().toString())) {
                view.setClickable(false);
                isProgressForgotVisible.set(View.VISIBLE);
                Utils.hideKeyboard(appCompatActivity);
                String toEmails = editTextEmail.getText().toString();
                UserModel user = NewsRealmDatabase.checkUsernameExist(toEmails, NewsConstant.USER_EMAIL_FIELD, editTextEmail.getContext());
                if (user != null) {
                    if (Utils.isOnline(appCompatActivity)) {
                        sendPasswordToEmail(user, toEmails, view);
                        isProgressForgotVisible.set(View.INVISIBLE);
                    } else {
                        Toast.makeText(appCompatActivity, appCompatActivity.getResources().getString(R.string.no_access_to_net), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    new Handler().postDelayed(() -> {
                        view.setClickable(true);
                        isProgressForgotVisible.set(View.INVISIBLE);
                        Toast.makeText(appCompatActivity, R.string.email_wrong, Toast.LENGTH_SHORT).show();
                    }, 3000);

                }

            } else {
                view.setClickable(true);
                Toast.makeText(appCompatActivity, R.string.enter_valid_email, Toast.LENGTH_SHORT).show();
            }
        } else {
            linearLayout.startAnimation(animationShakeError());
            MyAnimationUtils.setVibrate(appCompatActivity);
        }
    }


    public void onClickHandlerForgot(View view) {
        forgotPassDialog = createAndShowForgotDialog(view);
    }

    private void sendPasswordToEmail(UserModel user, String toEmails, View view) {
        String emailSubject = appCompatActivity.getString(R.string.yourPassword) + ": " + user.getPassword();
        new SendMailTask(appCompatActivity).execute(NewsConstant.EMAIL_SENDER_NAME, NewsConstant.EMAIL_SENDER_PASSWORD, toEmails, emailSubject, user.getEmail(), view);
    }


    private void actionBtnGo(View btnGo, EditText edtUserName, EditText edtPassword, FrameLayout mFrameLayout, CircularProgressBar progressBar, CheckBox checkBox) {
        if (edtUserName.getText().toString().length() == 0 && edtPassword.getText().toString().length() == 0) {
            Toast.makeText(btnGo.getContext(), R.string.enter_user_pass, Toast.LENGTH_SHORT).show();
        } else if (edtUserName.getText().toString().length() == 0) {
            Toast.makeText(btnGo.getContext(), R.string.enter_user_name, Toast.LENGTH_SHORT).show();
        } else if (edtPassword.getText().toString().length() == 0) {
            Toast.makeText(btnGo.getContext(), R.string.enter_pass, Toast.LENGTH_SHORT).show();
        } else {
            animationTransactionToCircle((Button) btnGo, progressBar, appCompatActivity);
            RegisterViewModel.isTxtVisible.set(View.VISIBLE);
            UserModel model = NewsRealmDatabase.checkUsernameExist(edtUserName.getText().toString(), NewsConstant.USER_NAME_FIELD, btnGo.getContext());

            if (model != null && checkPassValidation(edtPassword.getText().toString(), model.getPassword())) {
                int isOnline = 0;
                if (checkBox.isChecked()) {
                    isOnline = KEEP_ONLINE;
                }
                model.setIsOnline(isOnline);
                NewsRealmDatabase.changeIsOnline(NewsConstant.USER_NAME_FIELD, model.getUserName(), isOnline, appCompatActivity);
                Utils.callIntentRegister(appCompatActivity, model);
            } else {
                animationTransactionToRound((Button) btnGo, progressBar, mFrameLayout, appCompatActivity.getString(R.string.user_or_pass_wrong), appCompatActivity);
            }
        }
    }

    private void actionLinearGoogleLogin(View btnFaceAndGoogle) {
        Authentication.signInWithGoogleAccount(appCompatActivity, NewsConstant.RC_SIGN_IN);
        ((RegisterActivity) appCompatActivity).progressDialog = showProgressDialog(appCompatActivity, btnFaceAndGoogle);

    }

    private void actionLinearFacebookLogin(View btnFaceAndGoogle) {
        LoginManager.getInstance().logInWithReadPermissions(appCompatActivity, Arrays.asList(NewsConstant.PUBLIC_PROFILE, NewsConstant.USER_FRIEND));
        FacebookSdk.sdkInitialize(appCompatActivity);
        ((RegisterActivity) appCompatActivity).progressDialog = showProgressDialog(appCompatActivity, btnFaceAndGoogle);


    }

    private Dialog createAndShowForgotDialog(View view) {
        if (forgotPassDialog != null && forgotPassDialog.isShowing()) {
            return forgotPassDialog;
        }

        ForgotPassBinding binding = DataBindingUtil.inflate(LayoutInflater.from(appCompatActivity), R.layout.forgot_pass, null, false);
        Dialog dialog = new Dialog(appCompatActivity);
        Window window = dialog.getWindow();
        binding.setForgotPass(this);
        dialog.setCancelable(true);
        dialog.show();
        isDialogShow.set(dialog.isShowing());
        if (window != null) {
            window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        }
        dialog.setContentView(binding.getRoot());
        return dialog;
    }

    public ObservableInt getIsProgressForgotVisible() {
        return isProgressForgotVisible;
    }


    private void showFacebookCheckVpnDialog(View btnFaceAndGoogle, final AppCompatActivity appCompatActivity, final String message) {
        new AlertDialog.Builder(appCompatActivity)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> actionLinearFacebookLogin(btnFaceAndGoogle)).setNegativeButton(android.R.string.cancel, (dialog, which) -> btnFaceAndGoogle.setClickable(true))
                .show();
    }

}
