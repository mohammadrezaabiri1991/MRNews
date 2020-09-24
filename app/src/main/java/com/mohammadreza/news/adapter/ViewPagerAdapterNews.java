package com.mohammadreza.news.adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.mohammadreza.news.R;
import com.mohammadreza.news.ui.LoginFragment;
import com.mohammadreza.news.ui.SignupFragment;

public class ViewPagerAdapterNews extends FragmentPagerAdapter {

    private final Context context;

    public ViewPagerAdapterNews(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
            default:
                return new LoginFragment();
            case 1:
                return new SignupFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
            default:
                return context.getString(R.string.login);
            case 1:
                return context.getString(R.string.signup);


        }
    }
}
