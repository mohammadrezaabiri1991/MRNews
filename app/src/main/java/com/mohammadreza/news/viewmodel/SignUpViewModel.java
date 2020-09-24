package com.mohammadreza.news.viewmodel;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BaseObservable;

import com.budiyev.android.circularprogressbar.CircularProgressBar;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.mohammadreza.news.R;
import com.mohammadreza.news.constant.NewsConstant;
import com.mohammadreza.news.model.UserModel;
import com.mohammadreza.news.model.UserRealmModel;
import com.mohammadreza.news.repository.NewsRealmDatabase;
import com.mohammadreza.news.ui.RegisterActivity;
import com.mohammadreza.news.utils.Utils;

import java.util.Arrays;

import static com.mohammadreza.news.authentication.Authentication.signInWithGoogleAccount;
import static com.mohammadreza.news.repository.NewsRealmDatabase.addToRealmDatabaseFireBase;
import static com.mohammadreza.news.utils.MyAnimationUtils.animationTransactionToCircle;
import static com.mohammadreza.news.utils.MyAnimationUtils.animationTransactionToRound;
import static com.mohammadreza.news.utils.Utils.isOnline;
import static com.mohammadreza.news.utils.Utils.showProgressDialog;

public class SignUpViewModel extends BaseObservable {
    private AppCompatActivity appCompatActivity;

    public SignUpViewModel(AppCompatActivity appCompatActivity) {
        this.appCompatActivity = appCompatActivity;
    }

    public void onClickJoinUs(View view, EditText edtUserName, EditText edtPass, EditText edtRepeatPass, CircularProgressBar mProgressSignUp, FrameLayout mFrameLayout) {
        actionBtnJoinUs(view, edtUserName, edtPass, edtRepeatPass, mProgressSignUp, mFrameLayout);
    }

    public void onClickLinearGoogleAndFacebookSignUp(View btn) {
        if (isOnline(appCompatActivity)) {
            btn.setClickable(false);
            if (btn.getTag().equals(appCompatActivity.getString(R.string.linearGoogleSignUp))) {
                actionLinearGoogle(btn);
            } else if (btn.getTag().equals(appCompatActivity.getString(R.string.linearFacebookSingUp))) {
                actionLinearFacebook(btn);
            }
        } else {
            Utils.showAlertDialog(appCompatActivity, appCompatActivity.getString(R.string.no_access_to_net));
        }

    }

    private void actionLinearGoogle(View btn) {

//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(appCompatActivity.getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(appCompatActivity, gso);
//
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        mGoogleSignInClient.signOut();
//        appCompatActivity.startActivityForResult(signInIntent, RC_SIGN_IN);
        signInWithGoogleAccount(appCompatActivity, NewsConstant.RC_SIGN_IN);
        ((RegisterActivity) appCompatActivity).progressDialog = showProgressDialog(appCompatActivity, btn);
    }

    private void actionLinearFacebook(View btn) {
        LoginManager.getInstance().logInWithReadPermissions(appCompatActivity, Arrays.asList(NewsConstant.PUBLIC_PROFILE, NewsConstant.USER_FRIEND));
        FacebookSdk.sdkInitialize(appCompatActivity);
        ((RegisterActivity) appCompatActivity).progressDialog = showProgressDialog(appCompatActivity, btn);

    }


    private void actionBtnJoinUs(View view, EditText edtUserName, EditText edtEmail, EditText edtPassword, CircularProgressBar mProgressSignUp, FrameLayout mFrameLayout) {
        if (!TextUtils.isEmpty(edtUserName.getText().toString()) && !TextUtils.isEmpty(edtEmail.getText().toString()) && !TextUtils.isEmpty(edtPassword.getText().toString())) {
            if (edtUserName.getText().toString().length() != 0) {
                if (edtEmail.getText().toString().length() != 0 && Utils.isEmailValid(edtEmail.getText().toString())) {
                    if (edtPassword.getText().toString().length() != 0) {
                        if (edtPassword.getText().toString().length() > 5) {
                            animationTransactionToCircle((Button) view, mProgressSignUp, appCompatActivity);
                            RegisterViewModel.isTxtVisible.set(View.VISIBLE);

                            if (NewsRealmDatabase.checkUsernameAndEmailExistSignUp(NewsConstant.USER_EMAIL_FIELD, edtEmail.getText().toString(), NewsConstant.USER_NAME_FIELD, edtUserName.getText().toString(), appCompatActivity) == null) {
                                UserRealmModel userRealmModel = new UserRealmModel();
                                userRealmModel.setUserName(edtUserName.getText().toString());
                                userRealmModel.setPassword(edtPassword.getText().toString());
                                userRealmModel.setEmail(edtEmail.getText().toString());
                                userRealmModel.setIsOnline(NewsConstant.KEEP_ONLINE);

                                UserModel userModel = new UserModel();
                                userModel.setUserName(edtUserName.getText().toString());
                                userModel.setIsOnline(NewsConstant.KEEP_ONLINE);
                                int id = addToRealmDatabaseFireBase(userRealmModel, appCompatActivity);
                                userModel.setId(id);
                                Utils.callIntentRegister(appCompatActivity, userModel);

                            } else {
                                animationTransactionToRound((Button) view, mProgressSignUp, mFrameLayout, appCompatActivity.getString(R.string.account_exist), appCompatActivity);
                            }
                        } else {
                            Toast.makeText(appCompatActivity, appCompatActivity.getString(R.string.minimum_pass), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(appCompatActivity, appCompatActivity.getString(R.string.enter_pass), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(appCompatActivity, appCompatActivity.getString(R.string.enter_valid_email), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(appCompatActivity, appCompatActivity.getString(R.string.enter_user_name), Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(appCompatActivity, R.string.enter_user_email_pass, Toast.LENGTH_SHORT).show();
        }
    }
}
