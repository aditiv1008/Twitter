package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import com.codepath.apps.restclienttemplate.TimeFormatter;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Parcel
public class Tweet {

    public String body;
    public String createdAt;
    public User user;
    public String media;
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    //empty contrustor needed by Parceler library
    public Tweet() {}

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));


     /* JSONObject tEntities = jsonObject.getJSONObject("entities");

        try {
            JSONArray pictures = tEntities.getJSONArray("media");
            JSONObject fPicture = pictures.getJSONObject(0);
            tweet.media = fPicture.getString("media_url_https");
            Log.d("media", tweet.media);
        } catch (JSONException e){
            tweet.media = "none";
            e.printStackTrace();
            } */

        if (!jsonObject.getJSONObject("entities").has("media")) {
            tweet.media = "none";
        } else {
            tweet.media  =  jsonObject.getJSONObject("entities").getJSONArray("media").getJSONObject(0).getString("media_url");
        }












        return tweet;


    }



    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    public String getFormattedTimestamp() {
        return TimeFormatter.getTimeDifference(createdAt);

    }
}



