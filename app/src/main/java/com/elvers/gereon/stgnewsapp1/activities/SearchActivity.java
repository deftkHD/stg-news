package com.elvers.gereon.stgnewsapp1.activities;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.elvers.gereon.stgnewsapp1.R;
import com.elvers.gereon.stgnewsapp1.adapter.ArticleAdapter;
import com.elvers.gereon.stgnewsapp1.adapter.AuthorAdapter;
import com.elvers.gereon.stgnewsapp1.api.WordPressAPI;
import com.elvers.gereon.stgnewsapp1.api.object.Post;
import com.elvers.gereon.stgnewsapp1.api.object.User;
import com.elvers.gereon.stgnewsapp1.api.request.PostsRequest;
import com.elvers.gereon.stgnewsapp1.api.request.UsersRequest;
import com.elvers.gereon.stgnewsapp1.api.response.PostsResponse;
import com.elvers.gereon.stgnewsapp1.api.response.UsersResponse;
import com.elvers.gereon.stgnewsapp1.utils.Utils;

import java.util.ArrayList;

/**
 * Search Activity of the App. This Activity is used when a search query is send through the SearchView on {@link MainActivity}.
 * It sends the query to the WordPress backend and displays the results in a ListView similar to the one used in {@link MainActivity}
 *
 * @author Gereon Elvers
 */
public class SearchActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, PostsRequest.ArticlesRequestResultHandler, UsersRequest.AuthorsRequestResultHandler {

    public static String ACTION_FILTER_AUTHOR = "FILTER_BY_AUTHOR";
    public static String EXTRA_AUTHOR_ID = "EXTRA_AUTHOR_ID";
    public static String EXTRA_CATEGORY_ID = "EXTRA_CATEGORY_ID";

    // Static request URL the data will be requested from. Putting it at the top like this allow easier modification of top level domain if required
    private static final String WP_REQUEST_URL = "www.stg-sz.net";

    /* There are a lot of items declared outside of individual methods here.
        This is done because they are required to be available across methods */
    SwipeRefreshLayout mSwipeRefreshLayout;
    String titleString = "";
    String searchFilter = ""; // don't want to cause a NullPointerException
    int authorId = -1;
    int categoryId = -1;
    ListView mListView;
    View loadingIndicator;
    TextView emptyView;
    String numberOfArticlesParam;
    Integer pageNumber;
    private ArrayAdapter mAdapter;

    private ProgressBar loadingIndicatorBottom;
    private boolean loadingContent = false;
    private boolean canLoadMoreContent = true;

    /**
     * onCreate is called when this Activity is launched. It is therefore responsible for setting it up based on the query specified in the Intent used to launch it.
     * As the base layout of this Activity is very similar to {@link MainActivity}, this will be as well.
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.updateNightMode(this);

        super.onCreate(savedInstanceState);
        // Setting XML base layout
        setContentView(R.layout.activity_search);

        // Setting up Actionbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        handleIntent(getIntent());
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setTitle(titleString);
        }

        // Getting numberOfArticlesParam from SharedPreferences (default: 10; modifiable through Preferences). This is the number of articles requested from the backend.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        numberOfArticlesParam = sharedPreferences.getString("post_number", "10");
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        // Finding loadingIndicator
        loadingIndicator = findViewById(R.id.loading_circle);

        // EmptyView (The message that is shown when ListView is empty) is initialized and set on ListView
        emptyView = findViewById(R.id.empty_view);
        mListView = findViewById(R.id.listView);
        mListView.setEmptyView(emptyView);

        loadingIndicatorBottom = new ProgressBar(this);
        loadingIndicatorBottom.setVisibility(View.GONE);
        mListView.addFooterView(loadingIndicatorBottom);

        mListView.setOnScrollListener(new InfinityScrollListener());

        // When launching the Activity, the first page should be loaded
        pageNumber = 1;

        // SwipeRefreshLayout is initialized and refresh functionality is implemented
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                refreshListView();
            }
        });

        //  Load of Article objects onto mListView is requested
        initLoaderListView();

    }

    /**
     * This method is called when performing a refresh of the ListView while the Layout remains visible
     * It dumps the old Adapter and makes corrects the visibility of loadingIndicator and emptyView so make the user aware that new data is about to appear
     */
    public void refreshListView() {
        if (mAdapter != null)
            mAdapter.clear();
        loadingIndicator.setVisibility(View.VISIBLE);
        initLoaderListView();
        emptyView.setVisibility(View.INVISIBLE);
    }

    public void initLoaderListView() {
        pageNumber = 1;

        if (categoryId == -3) {
            mAdapter = new AuthorAdapter(this, new ArrayList<User>());
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (mAdapter.getCount() > i) {
                        User currentUser = (User) mAdapter.getItem(i);
                        Intent authorIntent = new Intent(getApplicationContext(), SearchActivity.class);
                        if (currentUser != null) {
                            authorIntent.setAction(SearchActivity.ACTION_FILTER_AUTHOR);
                            authorIntent.putExtra(SearchActivity.EXTRA_AUTHOR_ID, currentUser.getId());
                        }
                        finish(); // maybe the articles listed by author should have their own activity (this is just a dirty fix)
                        startActivity(authorIntent);
                    }
                }
            });
        } else {
            mAdapter = new ArticleAdapter(this, new ArrayList<Post>());
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (mAdapter.getCount() > i) {
                        Post currentPost = (Post) mAdapter.getItem(i);
                        Intent articleIntent = new Intent(getApplicationContext(), ArticleActivity.class);
                        if (currentPost != null) {
                            articleIntent.putExtra("ARTICLE_URI", currentPost.getUrl());
                            articleIntent.putExtra("ARTICLE_TITLE", currentPost.getTitleHtmlEscaped());
                            articleIntent.putExtra("ARTICLE_ID", currentPost.getId());
                        }
                        startActivity(articleIntent);
                    }
                }
            });
        }

        startFetchingContent();
    }

    private void startFetchingContent() {
        loadingContent = true;

        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme("https");
        uriBuilder.authority(WP_REQUEST_URL);
        uriBuilder.appendPath("wp-json").appendPath("wp").appendPath("v2");
        if (categoryId == -3) {
            uriBuilder.appendPath("users");
        } else {
            uriBuilder.appendPath("posts");
        }

        if (!searchFilter.isEmpty()) {
            uriBuilder.appendQueryParameter("search", searchFilter);
        } else if (authorId != -1 && categoryId != -3) {
            uriBuilder.appendQueryParameter("author", String.valueOf(authorId));
        }

        if (categoryId == -3) { // search author
            WordPressAPI.requestUsers(this, searchFilter, Integer.parseInt(numberOfArticlesParam), pageNumber);
        } else { // search article
            String[] whitelist = categoryId == -2 ? PreferenceManager.getDefaultSharedPreferences(this).getString("favorites", "").split(",") : null;
            String categoryFilter = categoryId > 0 ? String.valueOf(categoryId) : null;
            String authorFilter = authorId != -1 ? String.valueOf(authorId) : null;
            WordPressAPI.requestPosts(this, categoryFilter, searchFilter, authorFilter, Integer.parseInt(numberOfArticlesParam), whitelist, pageNumber);
        }
    }

    @Override
    public void onArticlesReceived(PostsResponse response) {
        loadingIndicator.setVisibility(View.GONE);
        loadingIndicatorBottom.setVisibility(View.GONE);

        canLoadMoreContent = true;

        loadingIndicator.setVisibility(View.GONE);
        emptyView.setText(R.string.no_result_search);

        mAdapter.notifyDataSetChanged();
        if (response != null && !response.posts.isEmpty()) {
            mAdapter.addAll(response.posts);
            if (response.posts.size() != Integer.parseInt(numberOfArticlesParam))
                canLoadMoreContent = false;
        } else {
            if (response != null)
                canLoadMoreContent = false;
        }

        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }

        loadingContent = false;
    }

    @Override
    public void onAuthorsReceived(UsersResponse response) {
        loadingIndicator.setVisibility(View.GONE);
        loadingIndicatorBottom.setVisibility(View.GONE);

        canLoadMoreContent = true;

        loadingIndicator.setVisibility(View.GONE);
        emptyView.setText(R.string.no_result_search);

        mAdapter.notifyDataSetChanged();
        if (response != null && !response.users.isEmpty()) {
            mAdapter.addAll(response.users);
            if (response.users.size() != Integer.parseInt(numberOfArticlesParam))
                canLoadMoreContent = false;
        } else {
            if (response != null)
                canLoadMoreContent = false;
        }

        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }

        loadingContent = false;
    }

    /**
     * This method is called when a new is detected (e.g. when launching the Activity through an Intent)
     * Since the response to it is the same as the one required in onCreate(), it simply calls handleIntent()
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    /**
     * This method is called whenever data that was pushed through an Intent needs to be retrieved. It gets the String and saves it as searchFilter.
     * It also refreshes titleString to match the new search term.
     */
    public void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchFilter = intent.getStringExtra(SearchManager.QUERY);
            if (intent.hasExtra(EXTRA_CATEGORY_ID)) {
                categoryId = intent.getIntExtra(EXTRA_CATEGORY_ID, -1);
            }
            titleString = (categoryId == -3 ? getString(R.string.search_title_author) : getString(R.string.search_title)) + searchFilter + "\"";
        } else if (ACTION_FILTER_AUTHOR.equals(intent.getAction())) {
            authorId = intent.getIntExtra(EXTRA_AUTHOR_ID, -1);
            titleString = getString(R.string.search_title_by_author) + WordPressAPI.getUserName(authorId);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("dark_mode")) {
            recreate();
        } else if (key.equals("post_number")) {
            recreate();
        }
    }

    private class InfinityScrollListener implements AbsListView.OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (firstVisibleItem + visibleItemCount + 2 >= totalItemCount && !loadingContent && canLoadMoreContent && totalItemCount > 1 && visibleItemCount > 1) {
                loadingIndicatorBottom.setVisibility(View.VISIBLE);
                pageNumber++;
                startFetchingContent();
            }
        }
    }

}