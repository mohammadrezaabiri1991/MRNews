package com.mohammadreza.news.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.mohammadreza.news.R;
import com.mohammadreza.news.application.App;
import com.mohammadreza.news.constant.NewsConstant;
import com.mohammadreza.news.viewmodel.NewsViewModel;


public class NewsActivity extends AppCompatActivity {
    private SearchView searchView;
    private MenuItem searchItem;
    private EditText editText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.mohammadreza.news.databinding.NewsActivityBinding mainProfileBinding = DataBindingUtil.setContentView(this, R.layout.news_activity);
        NewsViewModel news = new NewsViewModel(mainProfileBinding, this);
        mainProfileBinding.setNews(news);
        mainProfileBinding.txtNewsCategory.setTypeface(App.TYPE_FACE);

        setSupportActionBar(mainProfileBinding.toolbarNewsMainPAge);
        mainProfileBinding.toolbarNewsMainPAge.inflateMenu(R.menu.news_menu);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
    }


    @Override
    public void onBackPressed() {
        if (editText != null && editText.getText() != null && editText.getText().toString().isEmpty()) {
            super.onBackPressed();
        } else if (editText == null) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu, menu);

        searchItem = menu.findItem(R.id.action_search);
        actionExpandItemView();
        searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (manager != null) {
            searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        }
        changeSearchViewStyle();
        queryActionSearchView();
        return super.onCreateOptionsMenu(menu);
    }


    private void queryActionSearchView() {
        searchItem.setOnMenuItemClickListener(item -> {
            searchView.onActionViewExpanded();
            if (editText != null) {
                hideAndShowKeyboard(editText, 1);
            }
            return false;
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (NewsViewModel.adapter != null) {
                    NewsViewModel.adapter.getFilter().filter(newText);
                }
                return false;
            }

        });
    }


    @SuppressLint("RtlHardcoded")
    private void changeSearchViewStyle() {
        try {
            LinearLayout view = (LinearLayout) searchView.getChildAt(0);
            LinearLayout view2 = (LinearLayout) view.getChildAt(2);
            LinearLayout search_plate = (LinearLayout) view2.getChildAt(1);
            search_plate.setBackgroundColor(getResources().getColor(R.color.app_bar_color));
            int id = searchView.getContext().getResources().getIdentifier(NewsConstant.ID_EDT_SEARCH_VIEW, null, null);
            editText = searchView.findViewById(id);

            if (editText != null) {
                editText.setHint(getResources().getString(R.string.search));
                editText.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                editText.setTypeface(App.TYPE_FACE);
                editText.setOnFocusChangeListener((v, hasFocus) -> {
                    if (!hasFocus) {
                        hideAndShowKeyboard(v, 0);
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void actionExpandItemView() {
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                if (editText != null) {
                    editText.setText("");
                }
                return true;
            }
        });
    }

    private void hideAndShowKeyboard(View view, int visibility) {
        if (visibility == 0) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } else if (visibility == 1) {
            editText.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        }
    }

    @Override
    protected void onStop() {
        removeSearchQuery();
        super.onStop();
    }

    private void removeSearchQuery() {
        if (editText != null && searchItem != null) {
            editText.setText("");
            searchItem.collapseActionView();
        }
    }
}
