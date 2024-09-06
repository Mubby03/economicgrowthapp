package com.example.economicgrowthapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.economicgrowthapp.Models.CustomAdapter;
import com.example.economicgrowthapp.Models.NewsApiResponse;
import com.example.economicgrowthapp.Models.NewsHeadlines;
import com.example.economicgrowthapp.Models.OnFetchDataListener;
import com.example.economicgrowthapp.Models.RequestManager;
import com.example.economicgrowthapp.Models.SelectListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NewssActivity extends AppCompatActivity implements SelectListener, View.OnClickListener {

    private final OnFetchDataListener<NewsApiResponse> listener = new OnFetchDataListener<NewsApiResponse>() {
        @Override
        public void onFetchData(List<NewsHeadlines> list, String message) {
            if (list.isEmpty()) {
                showToast("No data found");
            } else {
                showNews(list);
                dismissProgressDialog();
            }
        }

        @Override
        public void onError(String message) {
            showToast("An error occurred");
        }
    };

    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    private ProgressDialog progressDialog;
    private SearchView searchView;
    private ImageView fullImgNews, backBtn;
    private TextView newsTitle;
    private NewsHeadlines headlines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newssactivity);

        initializeViews();

        setProgressDialog();

        setClickListeners();

        setCardViewHeight();

        handleIntentExtras();

        setSearchViewListener();

        fetchData("business", null);
    }

    private void initializeViews() {
        searchView = findViewById(R.id.search_view);
        fullImgNews = findViewById(R.id.full_ImgNews);
        newsTitle = findViewById(R.id.titleNews);
        progressDialog = new ProgressDialog(this);
        backBtn = findViewById(R.id.backbtn);
        recyclerView = findViewById(R.id.recycler_main);
    }

    private void setProgressDialog() {
        progressDialog.setTitle("Fetching news articles");
        progressDialog.show();
    }

    private void setClickListeners() {
        backBtn.setOnClickListener(view -> onBackPressed());
    }

    private void setCardViewHeight() {
        ConstraintLayout cardView = findViewById(R.id.cardView);
        headlines = (NewsHeadlines) getIntent().getSerializableExtra("data");

        if (headlines != null) {
            newsTitle.setText(headlines.getTitle());
        }
        Picasso.get().load(headlines.getUrlToImage()).into(fullImgNews);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        int cardViewHeight = (int) (screenHeight * 0.43);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, cardViewHeight
        );
        cardView.setLayoutParams(layoutParams);
    }

    private void handleIntentExtras() {
        headlines = (NewsHeadlines) getIntent().getSerializableExtra("data");
        if (headlines != null) {
            newsTitle.setText(headlines.getTitle());
        }
        Picasso.get().load(headlines.getUrlToImage()).into(fullImgNews);
    }

    private void setSearchViewListener() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                progressDialog.setTitle("Fetching news articles of " + query);
                progressDialog.show();
                fetchData("business", query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void fetchData(String category, String query) {
        RequestManager manager = new RequestManager(this);
        manager.getNewsHeadlines(listener, category, query);
    }

    private void showNews(List<NewsHeadlines> list) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));
        adapter = new CustomAdapter(this, list, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void OnNewsClicked(NewsHeadlines headlines) {
        startActivity(new Intent(NewssActivity.this, DetailsActivity.class)
                .putExtra("data", headlines));
    }

    @Override
    public void onClick(View v) {
        Button button = (Button) v;
        String category = button.getText().toString();
        progressDialog.setTitle("Fetching news articles of " + category);
        progressDialog.show();
        fetchData(category, null);
    }

    private void dismissProgressDialog() {
        progressDialog.dismiss();
    }

    private void showToast(String message) {
        Toast.makeText(NewssActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
