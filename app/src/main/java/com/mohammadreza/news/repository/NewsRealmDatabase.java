package com.mohammadreza.news.repository;


import android.content.Context;
import android.util.Log;

import com.mohammadreza.news.constant.NewsConstant;
import com.mohammadreza.news.model.NewsDetailModel;
import com.mohammadreza.news.model.NewsDetailRealmModel;
import com.mohammadreza.news.model.NewsModel;
import com.mohammadreza.news.model.NewsRealmModel;
import com.mohammadreza.news.model.UserModel;
import com.mohammadreza.news.model.UserRealmModel;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import static com.mohammadreza.news.constant.NewsConstant.NEWS_DATABASE_NAME;
import static com.mohammadreza.news.constant.NewsConstant.NEWS_DETAIL_DATABASE_NAME;
import static com.mohammadreza.news.constant.NewsConstant.USER_DATABASE_NAME;
import static com.mohammadreza.news.constant.NewsConstant.USER_ID_TABLE;


public class NewsRealmDatabase {


    public static int addToRealmDatabaseFireBase(UserRealmModel userRealmModel, Context context) {
        Realm mRealm = Realm.getInstance(configUserDatabase(context));
        mRealm.beginTransaction();
        Number currentIdNum = mRealm.where(UserRealmModel.class).max(USER_ID_TABLE);
        int nextId;
        if (currentIdNum == null) {
            nextId = 1;
        } else {
            nextId = currentIdNum.intValue() + 1;
        }
        userRealmModel.setId(nextId);

        mRealm.copyToRealmOrUpdate(userRealmModel);
        mRealm.commitTransaction();
        mRealm.close();
        return nextId;

    }

    public static void changeIsOnline(String filedName, String requestValue, int isOnline, Context context) {
        Realm mRealm = Realm.getInstance(configUserDatabase(context));
        mRealm.beginTransaction();
        UserRealmModel toEdit = mRealm.where(UserRealmModel.class).equalTo(filedName, requestValue).findFirst();
        if (toEdit != null) {
            toEdit.setIsOnline(isOnline);
        }
        mRealm.commitTransaction();
        mRealm.close();

    }

    public static void changeIsOnline(int logOut, Context context) {
        Realm mRealm = Realm.getInstance(configUserDatabase(context));
        mRealm.beginTransaction();
        RealmResults<UserRealmModel> userRealmModels = mRealm.where(UserRealmModel.class).findAll();
        if (userRealmModels != null) {
            for (UserRealmModel userRealmModel : userRealmModels) {
                if (userRealmModel.getIsOnline() == 1) {
                    userRealmModel.setIsOnline(logOut);
                }

            }

        }
        mRealm.commitTransaction();
        mRealm.close();

    }


    public static UserModel checkUsernameExist(String requestValue, String fieldName, Context context) {
        Realm mRealm = Realm.getInstance(configUserDatabase(context));
        try {
            mRealm.beginTransaction();
            UserRealmModel userRealmModel = mRealm.where(UserRealmModel.class).equalTo(fieldName, requestValue).findFirst();
            mRealm.commitTransaction();
            if (userRealmModel != null) {
                return new UserModel(userRealmModel);
            } else {
                return null;
            }
        } finally {
            mRealm.close();

        }
    }

    public static UserModel checkUsernameAndEmailExistSignUp(String useField, String userValue, String emailField, String emailValue, Context context) {
        Realm mRealm = Realm.getInstance(configUserDatabase(context));
        try {
            mRealm.beginTransaction();
            UserRealmModel userRealmModel = mRealm.where(UserRealmModel.class)
                    .contains(useField, userValue, Case.SENSITIVE)
                    .or()
                    .contains(emailField, emailValue, Case.SENSITIVE)
                    .findFirst();
            mRealm.commitTransaction();

            if (userRealmModel != null) {
                return new UserModel(userRealmModel);
            } else {
                return null;
            }
        } finally {
            mRealm.close();

        }
    }

    public static UserModel checkUsernameExist(int requestValue, String fieldName, Context context) {
        Realm mRealm = Realm.getInstance(configUserDatabase(context));
        try {
            mRealm.beginTransaction();
            UserRealmModel userRealmModel = mRealm.where(UserRealmModel.class).equalTo(fieldName, requestValue).findFirst();
            mRealm.commitTransaction();
            if (userRealmModel != null) {
                return new UserModel(userRealmModel);
            } else {
                return null;
            }
        } finally {
            mRealm.close();

        }
    }

    public static void storeNewsList(NewsRealmModel newsRealmModel, String category, Context context) {
        Realm mRealm = Realm.getInstance(configNewsDatabase(context));

        NewsRealmModel results = mRealm.where(NewsRealmModel.class).equalTo(NewsConstant.REALM_URL, category).findFirst();
        mRealm.beginTransaction();
        if (results != null) {
            results.deleteFromRealm();
        }
        mRealm.commitTransaction();

        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(newsRealmModel);
        mRealm.commitTransaction();
        mRealm.close();

    }

    public static void saveNewsDetail(NewsDetailRealmModel newsDetailRealmModel, Context context) {
        Realm mRealm = Realm.getInstance(configNewsDetailDatabase(context));
        mRealm.beginTransaction();
        Log.d("onMYResponse", "setDetail " + newsDetailRealmModel.getDetail());
        mRealm.copyToRealmOrUpdate(newsDetailRealmModel);
        mRealm.commitTransaction();
        mRealm.close();

    }

    public static NewsDetailModel getNewsDetail(String newsDetailLink, Context context) {
        Realm mRealm = Realm.getInstance(configNewsDetailDatabase(context));
        try {
            NewsDetailRealmModel newsRealmModel = mRealm.where(NewsDetailRealmModel.class).equalTo(NewsConstant.NEWS_ID_TABLE, newsDetailLink).findFirst();
            mRealm.beginTransaction();
            mRealm.commitTransaction();

            Log.d("onMYResponse", "2 " + newsDetailLink);

            if (newsRealmModel != null && !newsRealmModel.getDetail().isEmpty()) {
                Log.d("onMYResponse", "get " + newsRealmModel.getDetail());
                return new NewsDetailModel(newsRealmModel);
            } else {
                Log.d("onMYResponse", "return null ");
                return null;
            }

        } finally {
            mRealm.close();
        }
    }

    public static NewsModel getNews(String category, Context context) {
        Realm mRealm = Realm.getInstance(configNewsDatabase(context));
        try {
            mRealm.beginTransaction();
            NewsRealmModel newsRealmModel = mRealm.where(NewsRealmModel.class).equalTo(NewsConstant.REALM_URL, category).findFirst();
            mRealm.commitTransaction();
            if (newsRealmModel != null) {
                return new NewsModel(newsRealmModel.getNewsItemsRealmModel());
            } else {
                return null;
            }
        } finally {
//            mRealm.close();
        }
    }

    public static RealmConfiguration configUserDatabase(Context context) {
        Realm.init(context);
        return new RealmConfiguration.Builder()
                .name(USER_DATABASE_NAME)
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    public static RealmConfiguration configNewsDatabase(Context context) {
        Realm.init(context);
        return new RealmConfiguration.Builder()
                .name(NEWS_DATABASE_NAME)
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    public static RealmConfiguration configNewsDetailDatabase(Context context) {
        Realm.init(context);
        return new RealmConfiguration.Builder()
                .name(NEWS_DETAIL_DATABASE_NAME)
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
    }

}
