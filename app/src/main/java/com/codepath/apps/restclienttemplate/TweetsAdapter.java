package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import java.util.List;

import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {
    Context context;
    List<Tweet> tweets;


    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }


    //Pass in the context and list of tweets


    //for each row, inflate the layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Get the data at position
        Tweet tweet = tweets.get(position);
        //Bind the data with view holder
        holder.bind(tweet);

    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    //bind values based on the position of the element
    //define a viewholder

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        ImageView ivPostedPicture;
        TextView tvTimeStamp;
        TextView tvFavoriteCount;
        ImageButton ibFavorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            ivPostedPicture = itemView.findViewById(R.id.ivPostedPicture);
            tvTimeStamp = itemView.findViewById(R.id.tvTimeStamp);
            ibFavorite = itemView.findViewById(R.id.ibFavorite);
            tvFavoriteCount = itemView.findViewById(R.id.tvFavoriteCount);


        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvTimeStamp.setText(tweet.getFormattedTimestamp());
            tvScreenName.setText((tweet.user.screenName));
            Glide.with(context).load(tweet.user.profileImageUrl).into(ivProfileImage);
            tvFavoriteCount.setText(String.valueOf(tweet.favCount));

            if(tweet.isFavorited) {
                Drawable newImage = context.getDrawable((android.R.drawable.btn_star_big_on));
                ibFavorite.setImageDrawable(newImage);
            } else {
                Drawable newImage = context.getDrawable((android.R.drawable.btn_star_big_off));
                ibFavorite.setImageDrawable(newImage);
            }

            if (tweet.media != "none") {
                ivPostedPicture.setVisibility(View.VISIBLE);
                Glide.with(context).load(tweet.media).into(ivPostedPicture);
            } else {
                ivPostedPicture.setVisibility(View.GONE);
            }
            ibFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //if not already favorited
                    if(!tweet.isFavorited) {


                    // tell Twitter I want to favorite this
                    TwitterApp.getRestClient(context).favorite((tweet.id_str), new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Headers headers, JSON json) {

                        }

                        @Override
                        public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                        }
                    });

                    //change drawable to btn_big_star_on

                    //increment text inside tvFavoriteCount
                        tweet.isFavorited = true;
                        Drawable newImage = context.getDrawable((android.R.drawable.btn_star_big_on));
                       ibFavorite.setImageDrawable(newImage);

                      tweet.favCount++;
                       tvFavoriteCount.setText(String.valueOf(tweet.favCount));
                    }

                    //else if already Favorited
                    else {


                        //tell Twitter I want to unfavorite this
                        TwitterApp.getRestClient(context).unfavorite((tweet.id_str), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {

                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                            }
                        });
                        //change the drawable back to btn_star_big_off
                        //decrement the text inside tvFavoriteCount
                        tweet.isFavorited = false;
                        Drawable newImage = context.getDrawable((android.R.drawable.btn_star_big_off));
                        ibFavorite.setImageDrawable(newImage);
                        tweet.favCount--;
                        tvFavoriteCount.setText(String.valueOf(tweet.favCount));

                    }



                }
            });

        }
    }
}

