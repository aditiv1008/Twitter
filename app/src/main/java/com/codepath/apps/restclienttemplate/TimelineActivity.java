package com.codepath.apps.restclienttemplate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {
    public static final String TAG = "TimelineActivity";
    private final int REQUEST_CODE = 20;
    private EndlessRecyclerViewScrollListener rvScrollListener;
    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        //find the recycler view
        rvTweets = findViewById(R.id.rvTweets);
        //init the list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets);
        LinearLayoutManager lManager = new LinearLayoutManager(this);

        //Recyler view setup: layout manager and the adapter
        rvTweets.setLayoutManager(lManager);
        rvTweets.setAdapter(adapter);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        client = TwitterApp.getRestClient(this);
        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        //getSupportActionBar().setTitle("Twitter"); // set the top title
       // String title = actionBar.getTitle().toString(); // get the title

        //String title = actionBar.getTitle().toString(); // get the title

        getSupportActionBar().setDisplayShowHomeEnabled(true);
      getSupportActionBar().setLogo(R.drawable.transparent2);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        rvScrollListener = new EndlessRecyclerViewScrollListener(lManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, String.valueOf(page));
                long tweetId = tweets.get(tweets.size() - 1).id;
                loadNextDataFromApi(tweetId);
            }
        };
        rvTweets.addOnScrollListener(rvScrollListener);

        fetchTimelineAsync(0);

    }
    public void loadNextDataFromApi(long offset) {
        client.endlessHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    tweets.addAll(Tweet.fromJsonArray(json.jsonArray));
                    adapter.notifyDataSetChanged();
                    Log.i(TAG, String.valueOf(offset));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                swipeContainer.setRefreshing(false);

            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d("DEBUG", "Fetch timeline error: " + throwable.toString());
                swipeContainer.setRefreshing(false);

            }


        }, offset);
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
    }

    public void fetchTimelineAsync(int page) {
            // Send the network request to fetch the updated data
            // `client` here is an instance of Android Async HTTP
            // getHomeTimeline is an example endpoint.
        // on some click or some loading we need to wait for...
        ProgressBar pb = (ProgressBar) findViewById(R.id.pbLoading);
        pb.setVisibility(ProgressBar.VISIBLE);
// run a background job and once complete

            client.getHomeTimeline(new JsonHttpResponseHandler() {
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    try {
                        adapter.clear();
                        tweets.addAll(Tweet.fromJsonArray(json.jsonArray));
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    swipeContainer.setRefreshing(false);

                }

                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.d("DEBUG", "Fetch timeline error: " + throwable.toString());
                    swipeContainer.setRefreshing(false);

                }


            });
        pb.setVisibility(ProgressBar.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.compose) {
            //compose icon has been selected
            //Toast.makeText(this, "compose!", Toast.LENGTH_SHORT).show();
            //navigate to the compose activity
            Intent intent = new Intent(this, ComposeActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            //Get data from the intent (tweet)
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));


            //Update the RV with the tweet

            //Modify data source
            tweets.add(0, tweet);
            //update the adapter
            adapter.notifyItemInserted(0);
            rvTweets.smoothScrollToPosition(0);
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void onLogoutButton(View view) {
        // forget who's logged in
        TwitterApp.getRestClient(this).clearAccessToken();

        // navigate backwards to Login screen
        Intent i = new Intent(this, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // this makes sure the Back button won't work
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // same as above
        startActivity(i);

    }
}