package com.mohammadreza.news.rss.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.mohammadreza.news.R;
import com.mohammadreza.news.databinding.NewsItemBinding;
import com.mohammadreza.news.model.NewsModel;
import com.mohammadreza.news.ui.NewsDetailActivity;
import com.mohammadreza.news.utils.Utils;
import com.mohammadreza.news.viewmodel.NewsViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.mohammadreza.news.application.App.TYPE_FACE;


public class RssItemListAdapter extends RecyclerView.Adapter<RssItemListAdapter.FeedModelViewHolder> implements Filterable {

    private final boolean isOnline;
    private final ArrayList<NewsViewModel> viewModels;
    private AppCompatActivity appCompatActivity;
    private List<NewsViewModel> items;
    private LayoutInflater inflater;
    private String urlCategory;

    public RssItemListAdapter(List<NewsViewModel> items, String urlCategory, AppCompatActivity appCompatActivity) {
        this.items = items;
        this.appCompatActivity = appCompatActivity;
        this.urlCategory = urlCategory;
        isOnline = Utils.isOnline(appCompatActivity);

        viewModels = new ArrayList<>(items);

    }

    @Override
    public FeedModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {

        if (inflater == null)
            inflater = LayoutInflater.from(parent.getContext());
        NewsItemBinding itemBinding = DataBindingUtil.inflate(inflater, R.layout.news_item_rss_feed, parent, false);
        return new FeedModelViewHolder(itemBinding);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(FeedModelViewHolder holder, int position) {
        final NewsViewModel item = items.get(position);
        holder.bind(item);
        String date = Utils.convertToPersianDate(appCompatActivity, item.getNewsDate());
        ((TextView) holder.itemView.findViewById(R.id.txtDateItem)).setText(Utils.setPersianNumbers(date));


        NewsModel newsModel = new NewsModel(item);

        holder.itemView.findViewById(R.id.cardViewNewsItem).setOnClickListener(v -> Utils.callIntent(appCompatActivity, NewsDetailActivity.class, newsModel, urlCategory));

        if (isOnline) {
            if (item.getNewsImageUrl() != null) {
                if (!item.getNewsImageUrl().isEmpty() && appCompatActivity != null && !appCompatActivity.isFinishing()) {
                    Glide.with(appCompatActivity)
                            .asBitmap()
                            .load(item.getNewsImageUrl())
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onLoadStarted(@Nullable Drawable placeholder) {
                                    super.onLoadStarted(placeholder);
                                    holder.newsItemBinding.imgUrlItem.setImageResource(R.drawable.gif);
                                }

                                @Override
                                public void onResourceReady(@NonNull Bitmap resources, @Nullable Transition<? super Bitmap> transition) {
//                                    Bitmap bitmap = Bitmap.createScaledBitmap(resources, 100, 100, false);
                                    holder.newsItemBinding.imgUrlItem.setImageBitmap(resources);

                                    new Thread(() -> Utils.saveImageToInternalStorage(item.getNewsTitle(), resources, appCompatActivity)).start();
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                }
                            });


                } else {
                    holder.newsItemBinding.imgUrlItem.setImageResource(R.drawable.all_news);
                }
            } else {
                holder.newsItemBinding.imgUrlItem.setImageResource(R.drawable.all_news);
            }
        } else {
            new Thread(() -> Utils.loadImageFromStorage(item.getNewsTitle(), (AppCompatActivity) holder.newsItemBinding.imgUrlItem.getContext(), holder.newsItemBinding.imgUrlItem)).start();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public Filter getFilter() {
        return modelFilter;
    }

    private Filter modelFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<NewsViewModel> models = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                models.addAll(viewModels);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (NewsViewModel viewModel : viewModels) {
                    if (viewModel.getNewsTitle().toLowerCase().contains(filterPattern)) {
                        models.add(viewModel);
                    }

                }
            }
            FilterResults results = new FilterResults();
            results.values = models;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            items.clear();
            if (results != null && results.values != null) {
                items.addAll((List) results.values);
                notifyDataSetChanged();
            }
        }
    };

    public static class FeedModelViewHolder extends RecyclerView.ViewHolder {
        private NewsItemBinding newsItemBinding;

        private FeedModelViewHolder(NewsItemBinding newsItemBinding) {
            super(newsItemBinding.getRoot());
            this.newsItemBinding = newsItemBinding;
//            newsItemBinding.descriptionItem.setTypeface(TYPE_FACE);
            newsItemBinding.titleItem.setTypeface(TYPE_FACE);
        }

        private void bind(NewsViewModel newsViewModel) {
            this.newsItemBinding.setItems(newsViewModel);
            this.newsItemBinding.executePendingBindings();
        }
    }
}