package com.mohammadreza.news.utils;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;

import androidx.appcompat.app.AppCompatActivity;

import com.mohammadreza.news.R;
import com.mohammadreza.news.constant.NewsConstant;

import static com.mohammadreza.news.viewmodel.LoginViewModel.forgotPassDialog;

public class SendMailTask extends AsyncTask<Object, String, String> {

    @SuppressLint("StaticFieldLeak")
    private AppCompatActivity appCompatActivity;
    private ProgressDialog statusDialog;
    private boolean isException;

    public SendMailTask(AppCompatActivity activity) {

        this.appCompatActivity = activity;

    }

    protected void onPreExecute() {
        statusDialog = new ProgressDialog(appCompatActivity);
        statusDialog.setMessage(NewsConstant.GET_READY);
        statusDialog.setIndeterminate(false);
        statusDialog.setCancelable(false);
        statusDialog.show();

    }

    @Override
    protected String doInBackground(Object[] args) {
        isException = false;
        try {
            publishProgress(NewsConstant.PROCESSING);
            GMailSender androidEmail = new GMailSender(args[0].toString(), args[1].toString(), args[2].toString(), args[3].toString(), args[4].toString());
            publishProgress(NewsConstant.PREPARING);
            androidEmail.createEmailMessage();
            publishProgress(NewsConstant.SEND_EMAIL);
            androidEmail.sendEmail();
            publishProgress(NewsConstant.EMAIL_SENT);
        } catch (Exception e) {
            e.printStackTrace();
            isException = true;
        }
        return null;
    }

    @Override
    public void onProgressUpdate(String... values) {
        statusDialog.setMessage(values[0]);


    }

    @Override
    public void onPostExecute(String result) {
        statusDialog.dismiss();
        if (!isException) {
            if (forgotPassDialog != null && !appCompatActivity.isFinishing()) {
                forgotPassDialog.dismiss();
                Utils.showAlertDialog(appCompatActivity, appCompatActivity.getResources().getString(R.string.send_pass_to_email));
            }
        } else {
            if (forgotPassDialog != null && !appCompatActivity.isFinishing()) {
                forgotPassDialog.dismiss();
                Utils.showAlertDialog(appCompatActivity, appCompatActivity.getResources().getString(R.string.wrong_email));
                isException = false;
            }

        }


    }


}