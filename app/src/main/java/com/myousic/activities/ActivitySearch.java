package com.myousic.activities;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import com.myousic.R;
import com.myousic.models.QueuedSong;
import com.myousic.models.Song;
import com.myousic.util.WebAPIWrapper;
import com.myousic.util.SearchController;
import com.myousic.widgets.WidgetSongRow;

import java.util.List;

public class ActivitySearch extends FragmentActivity {
    private static final String TAG = "ActivitySearch";

    private SearchView searchView;
    private TableLayout tableLayout;
    private final String ARTIST = "artist";
    private final String TRACK = "track";
    private final String ALBUM = "album";

    static final int NUM_TABS = 3;
    TabAdapter mAdapter;
    ViewPager mPager;
    private final int TAB_SONGS = 0;
    private final int TAB_ARTISTS = 1;
    private final int TAB_ALBUMS = 2;
    SongListFragment songTabFragment;
    SongListFragment artistTabFragment;
    SongListFragment albumTabFragment;

    private SearchController searchControllerInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        tableLayout = (TableLayout) findViewById(R.id.result_table);

        searchView = (SearchView) findViewById(R.id.search);
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                search(query);
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return true;
            }
        });

        searchControllerInstance = SearchController.getInstance();
        ((TextView) findViewById(R.id.instructions)).setText(searchControllerInstance.getInstuctions());

        mAdapter = new TabAdapter(getSupportFragmentManager());
        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setTab(TAB_SONGS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        searchControllerInstance.resetCallback();
    }

    public void setTab(int tab) {
        Button songTab = (Button)findViewById(R.id.tab_song);
        Button artistTab = (Button) findViewById(R.id.tab_artist);
        Button albumTab = (Button)findViewById(R.id.tab_album);

        resetTabColors(songTab);
        resetTabColors(artistTab);
        resetTabColors(albumTab);

        if(tab == TAB_SONGS) {
            setTabColors(songTab);
        } else if(tab == TAB_ARTISTS) {
            setTabColors(artistTab);
        } else if(tab == TAB_ALBUMS) {
            setTabColors(albumTab);
        }

        mPager.setCurrentItem(tab);
    }

    public void resetTabColors(Button tabButton) {
        tabButton.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        tabButton.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    public void setTabColors(Button tabButton) {
        tabButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        tabButton.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    public static class TabAdapter extends FragmentPagerAdapter {
        public TabAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_TABS;
        }

        @Override
        public Fragment getItem(int position) {
            return SongListFragment.newInstance(position);
        }
    }

    protected void search(String query) {
        SharedPreferences prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        final String token = prefs.getString("token", null);
        tableLayout.removeAllViews();
        WebAPIWrapper webAPIWrapper = WebAPIWrapper.getInstance(this);
        webAPIWrapper.search(token, query, TRACK, new WebAPIWrapper.SearchResultResponseListener() {
            @Override
            public void onResponse(List<Song> songs) {
                for (Song result : songs) {
                    WidgetSongRow songRow = (WidgetSongRow) LayoutInflater.from(ActivitySearch.this)
                            .inflate(R.layout.layout_song_row, null);
                    songRow.setSong(result);
                    songRow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addSong(v);
                        }
                    });
                    ((TextView)songRow.findViewById(R.id.song_name)).setText(result.getName());
                    ((TextView)songRow.findViewById(R.id.song_artist)).setText(result.getArtist());
                    songRow.findViewById(R.id.song_image).setVisibility(View.GONE);
                    tableLayout.addView(songRow);
                }
            }
        });
    }

    public static class SongListFragment extends Fragment {
        int mNum;

        /**
         * Create a new instance of CountingFragment, providing "num"
         * as an argument.
         */
        static SongListFragment newInstance(int num) {
            SongListFragment f = new SongListFragment();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);

            return f;
        }

        /**
         * When creating, retrieve this instance's number from its arguments.
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.layout_song_list, container, false);
            return v;
        }

        public void addSong(View v) {
            String partyID = getActivity().getSharedPreferences(
                    "Party", Context.MODE_PRIVATE).getString("party_id", null);
            if (partyID == null) {
                Log.d(TAG, "Preferences not set");
                return;
            } try {
                WidgetSongRow widgetSongRow = (WidgetSongRow) v;
                QueuedSong queuedSong = new QueuedSong(widgetSongRow.getSong());
                ((ActivitySearch)getActivity()).searchControllerInstance.songChosen(queuedSong);
            } catch (Exception e) {
                Log.d(TAG, "Database error. Song not queued: " + e.toString());
            }
        }
    }
}
