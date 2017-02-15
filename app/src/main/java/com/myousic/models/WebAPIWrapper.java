package com.myousic.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import static android.R.attr.value;

/**
 * Created by brian on 2/12/17.
 *
 * Singleton class for handling a request queue
 */

public class WebAPIWrapper {
    private static WebAPIWrapper instance;
    private Context context;
    private RequestQueue queue;
    private static String TAG = "WebAPIWrapper";

    public interface SearchResultResponseListener {
        public void onResponse(List<SearchResult> searchResult);
    }

    public interface AlbumCoverListener {
        public void onResponse(Bitmap bitmap);
    }

    private class RetrieveAlbumTask extends AsyncTask<String, Void, Bitmap> {
        private AlbumCoverListener albumCoverListener;

        public RetrieveAlbumTask(AlbumCoverListener albumCoverListener) {
            this.albumCoverListener = albumCoverListener;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            for (String url : params) {
                try {
                    bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
                } catch (MalformedURLException e) {
                    Log.d(TAG, "Malformed URL");
                } catch (IOException e) {
                    Log.d(TAG, "IOException in retrieving image");
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap image) {
            if (image != null) {
                albumCoverListener.onResponse(image);
            } else {
                Log.d(TAG, "Could not set image");
            }
        }
    }

    private WebAPIWrapper(Context context) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public void search(String query, String type, final SearchResultResponseListener searchResultResponseListener) {
        Log.d(TAG, "Initiating request");
        // Sanitize query!
        String url = "https://api.spotify.com/v1/search?q=" + query.replaceAll(" ", "%20") + "&type=" + type;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONObject("tracks").getJSONArray("items");
                    List<SearchResult> searchResults = new LinkedList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String song = jsonArray.getJSONObject(i).getString("name");
                        String artist = jsonArray.getJSONObject(i).getJSONArray("artists").getJSONObject(0).getString("name");
                        String album = jsonArray.getJSONObject(i).getJSONObject("album").getString("name");
                        String uri = jsonArray.getJSONObject(i).getString("uri");
                        searchResults.add(new SearchResult(song, artist, album, uri));
                    }
                    searchResultResponseListener.onResponse(searchResults);
                } catch (JSONException e) {
                    Log.d(TAG, "Search response unsuccessful");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error retrieving JSON request");
            }
        });
        queue.add(jsonObjectRequest);
    }

    public void getAlbumCover(String uri, final AlbumCoverListener albumCoverListener) {
        uri = uri.substring(uri.lastIndexOf(":") + 1, uri.length());
        String url = "https://api.spotify.com/v1/tracks/" + uri;
        Log.d(TAG, url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONObject("album").getJSONArray("images");
                    final String smallestImageURL = jsonArray.getJSONObject(jsonArray.length() - 1).getString("url");
                    Log.d(TAG, "Image URL: " + smallestImageURL);
                    new RetrieveAlbumTask(albumCoverListener).execute(smallestImageURL);
                } catch (JSONException e) {
                    Log.d(TAG, "Error retrieving art from JSON");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error retrieving album art request");
            }
        });
        queue.add(jsonObjectRequest);
    }

    public static synchronized WebAPIWrapper getInstance(Context context) {
        if (instance == null) {
            instance = new WebAPIWrapper(context);
        }
        return instance;
    }
}
