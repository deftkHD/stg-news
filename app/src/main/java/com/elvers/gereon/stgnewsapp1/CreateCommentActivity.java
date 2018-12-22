package com.elvers.gereon.stgnewsapp1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Activity that manages creating and posting a comment
 *
 * @author Gereon Elvers
 */
public class CreateCommentActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Integer> {

    String articleId;
    EditText nameET;
    EditText emailET;
    EditText contentET;
    String nameString;
    String emailString;
    String contentString;
    int POSTER_ID = 5;
    LoaderManager loaderManager;
    ImageView name_help;
    ImageView email_help;
    ImageView content_help;
    ImageView email_wand;
    AlertDialog.Builder alertDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_comment);
        Toolbar toolbar = findViewById(R.id.create_comment_toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_cancel);
            actionbar.setTitle(R.string.app_name);
        }
        loaderManager = getSupportLoaderManager();
        Intent createCommentIntent = getIntent();
        Integer idInt = createCommentIntent.getIntExtra("ARTICLE_ID", -1);
        articleId = idInt.toString();
        nameET = findViewById(R.id.create_comment_name_et);
        emailET = findViewById(R.id.create_comment_email_et);
        contentET = findViewById(R.id.create_comment_content_et);
        name_help = findViewById(R.id.create_comment_name_question_iv);
        email_help = findViewById(R.id.create_comment_email_question_iv);
        content_help = findViewById(R.id.create_comment_content_question_iv);
        email_wand = findViewById(R.id.create_comment_email_wand_iv);

        alertDialogBuilder = new AlertDialog.Builder(getApplication());

        name_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateCommentActivity.this);
                alertDialogBuilder.setTitle(getString(R.string.create_comments_hint_title));
                alertDialogBuilder.setMessage(getString(R.string.create_comments_hint_name));
                alertDialogBuilder.setCancelable(true);
                alertDialogBuilder.setNeutralButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        email_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateCommentActivity.this);
                alertDialogBuilder.setTitle(getString(R.string.create_comments_hint_title));
                alertDialogBuilder.setMessage(getString(R.string.create_comment_hint_email));
                alertDialogBuilder.setCancelable(true);
                alertDialogBuilder.setNeutralButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        content_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateCommentActivity.this);
                alertDialogBuilder.setTitle(getString(R.string.create_comments_hint_title));
                alertDialogBuilder.setMessage(getString(R.string.create_comment_hint_content));
                alertDialogBuilder.setCancelable(true);
                alertDialogBuilder.setNeutralButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        email_wand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lonetString = getString(R.string.lonet_string);
                nameString = nameET.getText().toString();
                if (!nameString.isEmpty()) {
                    try {
                        // Create an Array of names, only the first and last name are relevant
                        String[] emailNameParts = nameString.split(" ");
                        if (!emailNameParts[0].equals(emailNameParts[emailNameParts.length - 1])) {
                            String firstName = emailNameParts[0].substring(0, 3).toLowerCase();
                            String lastName = emailNameParts[emailNameParts.length - 1].toLowerCase();
                            lonetString = firstName + "." + lastName + lonetString;
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.first_last_name_identical), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("onClick", "Not working");
                    }
                }
                emailET.setText(lonetString);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_comment_menu, menu);
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
                onBackPressed();
                return true;
            // Submit button
            case R.id.submit:
                nameString = nameET.getText().toString();
                emailString = emailET.getText().toString();
                contentString = contentET.getText().toString();
                if (articleId != null) {
                    if (emailString.contains("stg-se.sh.lo-net2.de")) {
                        loaderManager.destroyLoader(POSTER_ID);
                        loaderManager.initLoader(POSTER_ID, null, this);
                    } else {
                        Toast.makeText(this, getString(R.string.email_invalid), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("submit button", "Article ID empty");
                }

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

    @NonNull
    @Override
    public Loader<Integer> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new CommentPoster(this, articleId, nameString, emailString, contentString);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Integer> loader, Integer responseCode) {
        if (responseCode == 201) {
            onBackPressed();
            Toast.makeText(this, getResources().getString(R.string.successful_post), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, getResources().getString(R.string.unsuccessful_post) + " " + responseCode.toString(), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Integer> loader) {
    }

}