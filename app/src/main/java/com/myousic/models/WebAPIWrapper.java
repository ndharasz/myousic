package com.myousic.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
        void onResponse(List<Song> song);
    }

    public interface AlbumCoverListener {
        void onResponse(Bitmap bitmap);
    }

    public interface GetPlaylistListener {
        void onResponse(List<Playlist> playlists);
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

    private class JsonObjectRequestWithAuthHeader extends JsonObjectRequest {
        private String authToken;

        public JsonObjectRequestWithAuthHeader(int requestMethod, String url,
                                               JSONObject jsonRequest, Response.Listener<JSONObject> listener,
                                               Response.ErrorListener errorListener, String authToken) {
            super(requestMethod, url, jsonRequest, listener, errorListener);
            this.authToken = authToken;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> params = new HashMap<>();
            params.put("Authorization", "Bearer " + authToken);
            return params;
        }
    }

    private WebAPIWrapper(Context context) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context.getApplicationContext());
    }

    // This needs to take in a functional interface because this thread is non-blocking.
    public void search(String query, String type, final SearchResultResponseListener searchResultResponseListener) {
        Log.d(TAG, "Initiating request");
        // Maybe better sanitation should happen
        if (query.contains("&")) {
            return;
        }
        String url = "https://api.spotify.com/v1/search?q=" + query.replaceAll(" ", "%20") + "&type=" + type;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONObject("tracks").getJSONArray("items");
                    List<Song> songs = new LinkedList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String song = jsonArray.getJSONObject(i).getString("name");
                        String artist = jsonArray.getJSONObject(i).getJSONArray("artists").getJSONObject(0).getString("name");
                        String album = jsonArray.getJSONObject(i).getJSONObject("album").getString("name");
                        String uri = jsonArray.getJSONObject(i).getString("uri");
                        songs.add(new Song(song, artist, album, uri));
                    }
                    searchResultResponseListener.onResponse(songs);
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

    public void getLargeAlbumCover(String uri, final AlbumCoverListener albumCoverListener) {
        uri = uri.substring(uri.lastIndexOf(":") + 1, uri.length());
        String url = "https://api.spotify.com/v1/tracks/" + uri;
        Log.d(TAG, url);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONObject("album").getJSONArray("images");
                    final String smallestImageURL = jsonArray.getJSONObject(0).getString("url");
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

    public void getPlaylists(final String authToken, final GetPlaylistListener getPlaylistListener) {
        //String url = "https://api.spotify.com/v1/users/" + uid + "/playlists";
        String url = "https://api.spotify.com/v1/me/playlists";
        Log.d(TAG, url);
        JsonObjectRequestWithAuthHeader jsonObjectRequest = new JsonObjectRequestWithAuthHeader(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d(TAG, "JSON Response getting playlists");
                    JSONArray jsonArray = response.getJSONArray("items");
                    List<Playlist> playlists = new LinkedList<Playlist>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonPlaylist = jsonArray.getJSONObject(i);
                        String name = jsonPlaylist.getString("name");
                        String owner = jsonPlaylist.getJSONObject("owner").getString("id");
                        String uri = jsonPlaylist.getString("uri");
                        playlists.add(new Playlist(name, owner, uri));
                    }
                    getPlaylistListener.onResponse(playlists);
                } catch (JSONException e) {
                    Log.d(TAG, "Error retrieving playlists");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "JSON url error");
            }
        }, authToken);
        queue.add(jsonObjectRequest);
    }

    public void getSongsFromPlaylist(final String authToken, Playlist playlist,
                                     final SearchResultResponseListener searchResultResponseListener) {
        String owner = playlist.getOwner();
        String playlistID = playlist.getUri().split(":")[4];
        String url = "https://api.spotify.com/v1/users/" + owner + "/playlists/" + playlistID + "/tracks";
        Log.d(TAG, url);
        Log.d(TAG, authToken);
        JsonObjectRequestWithAuthHeader jsonObjectRequest = new JsonObjectRequestWithAuthHeader(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d(TAG, "JSON Response getting songs from playlist");
                    JSONArray jsonArray = response.getJSONArray("items");
                    List<Song> songs = new LinkedList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonSong = jsonArray.getJSONObject(i).getJSONObject("track");
                        String song = jsonSong.getString("name");
                        String artist = jsonSong.getJSONArray("artists").getJSONObject(0).getString("name");
                        String album = jsonSong.getJSONObject("album").getString("name");
                        String uri = jsonSong.getString("uri");
                        songs.add(new Song(song, artist, album, uri));
                    }
                    searchResultResponseListener.onResponse(songs);
                } catch (JSONException e) {
                    Log.d(TAG, "Error retrieving playlists");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "URL or header error.");
            }
        }, authToken);
        queue.add(jsonObjectRequest);

    }

    public static synchronized WebAPIWrapper getInstance(Context context) {
        if (instance == null) {
            instance = new WebAPIWrapper(context);
        }
        return instance;
    }
}
