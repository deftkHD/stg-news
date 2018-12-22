package com.elvers.gereon.stgnewsapp1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


/**
 * Activity that manages displaying the selected Article
 *
 * @author Gereon Elvers
 */
public class ArticleActivity extends AppCompatActivity {


    int articleID;
    boolean isComments;
    FragmentManager fragmentManager;
    FloatingActionButton fab;
    String articleURI;
    Drawable ic_chat;
    Drawable ic_plus;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setting XML base layout
        setContentView(R.layout.activity_article);
        // Setting up Actionbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        }
        Intent articleIntent = getIntent();
        final String titleString = articleIntent.getStringExtra("ARTICLE_TITLE");
        articleID = articleIntent.getIntExtra("ARTICLE_ID", -1);
        final Spanned titleSpanned = Html.fromHtml(titleString);
        if (actionbar != null) {
            actionbar.setTitle(titleSpanned);
        }
        articleURI = articleIntent.getStringExtra("ARTICLE_URI");

        fab = findViewById(R.id.fab);
        ic_chat = getResources().getDrawable(R.drawable.ic_chat);
        ic_plus = getResources().getDrawable(R.drawable.ic_plus);

        fragmentManager = getSupportFragmentManager();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isComments) {
                    showComments();
                } else {
                    Intent createCommentIntent = new Intent(getApplicationContext(), CreateCommentActivity.class);
                    createCommentIntent.putExtra("ARTICLE_ID", articleID);
                    startActivity(createCommentIntent);
                }
            }
        });

        showArticle();

    }


    /**
     * Setting OptionsMenu on ActionBar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.article_menu, menu);
        return true;
    }

    /**
     * This method sets associates actions with the menu options representing them
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Back button
            case android.R.id.home:
                if (!isComments) {
                    NavUtils.navigateUpFromSameTask(this);
                } else {
                    showArticle();
                }
                return true;
            // Share button
            case R.id.share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                // subject text: share_subject
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
                // main text: share_text
                String shareUrlString = articleURI;
                if (isComments) {
                    shareUrlString += "#comments";
                }
                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + shareUrlString);
                // launches share recipient chooser with share_title as title
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_title)));
                return true;
            // Settings button
            case R.id.settings:
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            // About button
            case R.id.about:
                Intent aboutIntent = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(aboutIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isComments) {
            showArticle();
        } else {
            NavUtils.navigateUpFromSameTask(this);
        }
    }

    public void showComments() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        CommentsFragment commentFragment = CommentsFragment.newInstance(articleID);
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        fragmentTransaction.replace(R.id.article_frame, commentFragment);
        fragmentTransaction.commit();
        fab.setImageDrawable(ic_plus);
        isComments = true;
    }

    public void showArticle() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ArticleFragment articleFragment = ArticleFragment.newInstance(articleURI);
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.article_frame, articleFragment);
        fragmentTransaction.commit();
        fab.setImageDrawable(ic_chat);
        isComments = false;
    }
}