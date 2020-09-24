package com.mohammadreza.news.authentication;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.mohammadreza.news.R;
import com.mohammadreza.news.constant.NewsConstant;
import com.mohammadreza.news.model.UserModel;
import com.mohammadreza.news.model.UserRealmModel;
import com.mohammadreza.news.repository.NewsRealmDatabase;
import com.mohammadreza.news.ui.NewsCategoryActivity;
import com.mohammadreza.news.utils.Utils;

import static com.mohammadreza.news.constant.NewsConstant.KEEP_ONLINE;

public class Authentication {


    public static void signInWithGoogleAccount(final AppCompatActivity appCompatActivity, final int requestCode) {
        new Thread(() -> {
// Configure Google Sign In
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(appCompatActivity.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(appCompatActivity, gso);
            mGoogleSignInClient.signOut();
            mGoogleSignInClient.revokeAccess();

            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            appCompatActivity.startActivityForResult(signInIntent, requestCode);
        }).start();

    }


    //    ----------------------------------------------------Authentication Gmail And Facebook -----------------------------------------------------------------------------------------
//    public static void firebaseAuthCredential(AuthCredential credential, final AppCompatActivity appCompatActivity) {
//        final FirebaseAuth auth = FirebaseAuth.getInstance();
//        auth.signInWithCredential(credential).addOnCompleteListener(appCompatActivity, task -> {
//            if (task.isSuccessful()) {
//                FirebaseUser user = auth.getCurrentUser();
//
//                if (user != null) {
//                    UserModel model = NewsRealmDatabase.checkUsernameExist(user.getDisplayName(), NewsConstant.EMAIL_DISPLAYED_NAME_FIELD, appCompatActivity);
//                    if (model == null) {
//                        UserModel userModel = new UserModel(user);
//                        UserRealmModel userRealmModel = new UserRealmModel(user);
//                        userModel.setIsOnline(KEEP_ONLINE);
//                        userRealmModel.setIsOnline(KEEP_ONLINE);
//                        userRealmModel.setImageUrl(String.valueOf(user.getPhotoUrl()));
//                        int id = NewsRealmDatabase.addToRealmDatabaseFireBase(userRealmModel, appCompatActivity);
//                        userModel.setId(id);
//                        Utils.callIntent(appCompatActivity, NewsCategoryActivity.class, userModel);
//
//                    } else {
//                        model.setIsOnline(KEEP_ONLINE);
//                        NewsRealmDatabase.changeIsOnline(NewsConstant.EMAIL_DISPLAYED_NAME_FIELD, model.getEmailDisplayName(), KEEP_ONLINE, appCompatActivity);
//                        Utils.callIntent(appCompatActivity, NewsCategoryActivity.class, model);
//                    }
//                    Utils.hideProgressDialog(appCompatActivity);
//                } else {
//                    Utils.hideProgressDialog(appCompatActivity);
//                    Utils.showAlertDialog(appCompatActivity, appCompatActivity.getString(R.string.wrong));
//                }
//
//
//            } else {
//                Utils.hideProgressDialog(appCompatActivity);
//                Utils.showAlertDialog(appCompatActivity, appCompatActivity.getString(R.string.wrong));
//            }
//        });
//    }

    public static void authCredential(UserModel user, final AppCompatActivity appCompatActivity) {
        UserModel model = NewsRealmDatabase.checkUsernameExist(user.getEmailDisplayName(), NewsConstant.EMAIL_DISPLAYED_NAME_FIELD, appCompatActivity);
        if (model == null) {
            UserRealmModel userRealmModel = new UserRealmModel(user);
            user.setIsOnline(KEEP_ONLINE);
            userRealmModel.setIsOnline(KEEP_ONLINE);
            userRealmModel.setImageUrl(String.valueOf(user.getImageProfileUri()));
            int id = NewsRealmDatabase.addToRealmDatabaseFireBase(userRealmModel, appCompatActivity);
            user.setId(id);
            Utils.callIntent(appCompatActivity, NewsCategoryActivity.class, user);

        } else {
            model.setIsOnline(KEEP_ONLINE);
            NewsRealmDatabase.changeIsOnline(NewsConstant.EMAIL_DISPLAYED_NAME_FIELD, model.getEmailDisplayName(), KEEP_ONLINE, appCompatActivity);
            Utils.callIntent(appCompatActivity, NewsCategoryActivity.class, model);
        }
        Utils.hideProgressDialog(appCompatActivity);
    }
}
