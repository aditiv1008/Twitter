package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    public static final String TAG = "ComposeActivity";
    public final int MAX_TWEET_LENGTH = 140;

    EditText etCompose;
    Button btnTweet;

    TwitterClient client;


    JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Headers headers, JSON json) {
            Log.e(TAG, "onSuccess to publish tweet");
            try {
                Tweet tweet = Tweet.fromJson(json.jsonObject);
                Log.i(TAG, "Published tweet says: " + tweet);
                Intent intent = new Intent();
                intent.putExtra("tweet", Parcels.wrap(tweet));
                setResult(RESULT_OK, intent);
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

        }
    };


        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        client = TwitterApp.getRestClient(this);
        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);

        //Set click listener on button
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tweetContent = etCompose.getText().toString();
                if (tweetContent.isEmpty()) {
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if (tweetContent.length() > MAX_TWEET_LENGTH) {
                    Toast.makeText(ComposeActivity.this, "Sorry, you tweet is too long", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(ComposeActivity.this, tweetContent, Toast.LENGTH_LONG).show();
                // Make an API call to Twitter to publish the tweet
                //if we're replying to a tweet
                if (getIntent().hasExtra("should_reply_to_tweet")) {
                   String idOfTweetToReplyTo = getIntent().getStringExtra("id_of_tweet_to_reply_to");
                    String screenname = getIntent().getStringExtra("screenname_of_tweet_to_reply_to");
                     client.replyToTweet(idOfTweetToReplyTo, "@" + screenname + " " + tweetContent,  handler);
                 } else {
                     client.publishTweet(tweetContent, handler);
                }
             //



            }

        });
    }
}