package com.mohammadreza.news.viewmodel;


import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.BaseObservable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableInt;
import androidx.viewpager.widget.ViewPager;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.material.tabs.TabLayout;
import com.mohammadreza.news.R;
import com.mohammadreza.news.adapter.ViewPagerAdapterNews;
import com.mohammadreza.news.constant.NewsConstant;
import com.mohammadreza.news.model.UserModel;
import com.mohammadreza.news.repository.NewsRealmDatabase;
import com.mohammadreza.news.ui.NewsCategoryActivity;
import com.mohammadreza.news.utils.Utils;

import org.json.JSONException;

import java.util.Objects;

import static com.mohammadreza.news.authentication.Authentication.authCredential;


public class RegisterViewModel extends BaseObservable {

    private static AppCompatActivity appCompatActivity;
    public static ObservableInt isTxtVisible = new ObservableInt(View.GONE);

    public RegisterViewModel(AppCompatActivity appCompatActivity, CallbackManager callbackManager) {
        RegisterViewModel.appCompatActivity = appCompatActivity;
        actionFacebookLogin(callbackManager);
        UserModel user = NewsRealmDatabase.checkUsernameExist(NewsConstant.KEEP_ONLINE, NewsConstant.IS_ONLINE_FIELD_NAME, appCompatActivity);
        if (user != null) {
            Utils.callIntent(appCompatActivity, NewsCategoryActivity.class, user);
        }

    }

    @BindingAdapter("app:handler")
    public static void bindViewPagerNews(final ViewPager view, AppCompatActivity activity) {
        ViewPagerAdapterNews adapter = new ViewPagerAdapterNews(view.getContext(), appCompatActivity.getSupportFragmentManager());
        view.setAdapter(adapter);
    }

    @BindingAdapter("app:tab_layout")
    public static void bindTabLayoutNews(TabLayout tabLayout, ViewPager viewPager) {
        tabLayout.setupWithViewPager(viewPager, true);

    }

    private void actionFacebookLogin(final CallbackManager callbackManager) {
        FacebookSdk.sdkInitialize(appCompatActivity);

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
//                CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode();
//                AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
//                FirebaseAuthentication.firebaseAuthCredential(credential, appCompatActivity);
                handleFacebookSignInResult(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Utils.hideProgressDialog(appCompatActivity);
//                Toast.makeText(appCompatActivity, "onCancel", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(FacebookException exception) {
                Utils.hideProgressDialog(appCompatActivity);
//                Toast.makeText(appCompatActivity, "onError", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void handleFacebookSignInResult(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, (object, response) -> {
            try {
                String facebookId = object.getString(NewsConstant.ID_FACEBOOK);
                String imageUrl = NewsConstant.IMAGE_URL_FACEBOOK + facebookId + NewsConstant.IMAGE_TYPE_FACEBOOK;
                UserModel userModel = new UserModel();
                userModel.setEmailDisplayName(object.getString(NewsConstant.FIRST_NAME_FACEBOOK));
                userModel.setImageProfileUri(imageUrl);

                authCredential(userModel, appCompatActivity);
                Utils.hideProgressDialog(appCompatActivity);

            } catch (JSONException e) {
                Utils.hideProgressDialog(appCompatActivity);
                if (!Objects.requireNonNull(e.getMessage()).contains(appCompatActivity.getResources().getString(R.string.exception_cancel))) {
                    Utils.showAlertDialog(appCompatActivity, appCompatActivity.getString(R.string.wrong));
                }


            }
        });
        Bundle bundle = new Bundle();
        bundle.putString(NewsConstant.FIELDS_FACEBOOK, NewsConstant.FIELDS_FACEBOOK_ITEMS);
        request.setParameters(bundle);
        request.executeAsync();
    }


}
