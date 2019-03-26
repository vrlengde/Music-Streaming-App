package com.himanshusingh.www.musicplayer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.himanshusingh.www.musicplayer.models.AlbumLyricsModel;
import com.himanshusingh.www.musicplayer.models.AlbumModel;
import com.himanshusingh.www.musicplayer.models.LyricsModel;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SongsAdapterRecyclerView.OnSongClickListener, LyricsAdapterRecyclerView.OnLyricsClickListener, AlbumAdapterRecyclerView.OnAlbumClickListener, AlbumAdapterRecyclerView_Vertical.OnAlbumClickListenerMore, LyricsAdapterRecyclerView_Vertical.OnLyricsClickListenerMore{
    Toolbar searchToolbar;
    MaterialSearchView materialSearchView;
    TextView tvSearchAlbum, tvSearchLyrics;
    boolean doubleBackToExitPressedOnce = false;
    TextView tvMoreSongs, tvMoreAlbums, tvMoreLyrics;
    private ImageView mPlayerControl;
    Toolbar toolbar;
    int NO_OF_ALBUMS=5;
    int pos=0;
    ProgressDialog progress;
    RecyclerView songsList;
    ArrayList<LyricsModel> lyricsModelArrayList;
    ArrayList<AlbumLyricsModel> albumLyricsModelArrayList;
    ArrayList<AlbumModel> albumModelArrayList;
    ArrayList<String> song_names;
    ArrayList<String> song_urls;
    private TextView mSelectedTrackTitle;
    private ImageView mSelectedTrackImage;
    public boolean initialStage=true;
    public boolean playPause = false;
    private final static int NUM_PAGES = 5;
    private ViewPager mViewPager;
    private SwipeAdapter swipeAdapter;
    private List<ImageView> dots;
    RecyclerView allAlbumList;
    RecyclerView allLyricsList;
    DividerItemDecoration itemDecorator;
    LinearLayout linearLayoutControlBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        toolbar = findViewById(R.id.idToolbar);
        linearLayoutControlBottom = findViewById(R.id.idControlBottom);

        searchToolbar = findViewById(R.id.idSearchToolbar);
        setSupportActionBar(searchToolbar);
        getSupportActionBar().setTitle("Music Player");
        materialSearchView = findViewById(R.id.idSearchView);
        tvSearchAlbum = findViewById(R.id.idSearchAlbumText);
        tvSearchLyrics = findViewById(R.id.idSearchLyricsText);

        itemDecorator = new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.divider));

        allAlbumList = findViewById(R.id.idSearchAlbums);
        allLyricsList = findViewById(R.id.idSearchLyrics);

        materialSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {

            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                tvSearchAlbum.setText("");
                tvSearchLyrics.setText("");
                ArrayList<AlbumModel> lstFoundAlbum = new ArrayList<AlbumModel>();
                allAlbumList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                allAlbumList.setAdapter(new AlbumAdapterRecyclerView_Vertical(lstFoundAlbum, MainActivity.this));

                ArrayList<AlbumLyricsModel> lstFoundLyrics = new ArrayList<AlbumLyricsModel>();
                allLyricsList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                allLyricsList.setAdapter(new LyricsAdapterRecyclerView_Vertical(lstFoundLyrics, MainActivity.this));

            }
        });

        materialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText!=null && !newText.isEmpty() && newText.length()>=3)
                {
                    Log.d("query", newText);
                    ArrayList<AlbumModel> lstFoundAlbum = new ArrayList<AlbumModel>();
                    for(AlbumModel item:albumModelArrayList)
                    {
                        String album_name = item.getAlbum();
                        if(album_name.toLowerCase().contains(newText.toLowerCase()))
                        {
                            lstFoundAlbum.add(item);
                        }
                    }

                    if(lstFoundAlbum.size()==0)
                        tvSearchAlbum.setText("No album found!");
                    else
                        tvSearchAlbum.setText("Album");
                    allAlbumList.addItemDecoration(itemDecorator);
                    allAlbumList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    allAlbumList.setAdapter(new AlbumAdapterRecyclerView_Vertical(lstFoundAlbum, MainActivity.this));

                    ArrayList<AlbumLyricsModel> lstFoundLyrics = new ArrayList<AlbumLyricsModel>();
                    for(AlbumLyricsModel item:albumLyricsModelArrayList)
                    {
                        String album_name = item.getAlbum();
                        if(album_name.toLowerCase().contains(newText.toLowerCase()))
                        {
                            lstFoundLyrics.add(item);
                        }
                    }
                    if(lstFoundLyrics.size()==0)
                        tvSearchLyrics.setText("No lyrics found!");
                    else tvSearchLyrics.setText("Lyrics");
                    allLyricsList.addItemDecoration(itemDecorator);
                    allLyricsList.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    allLyricsList.setAdapter(new LyricsAdapterRecyclerView_Vertical(lstFoundLyrics, MainActivity.this));

                    if(lstFoundAlbum.size()==0 && lstFoundLyrics.size()==0)
                    {
                        tvSearchLyrics.setText("");
                        tvSearchAlbum.setText("Nothing matches your query");
                    }
                }
                else
                {
                    // return default
                }
                return true;
            }
        });

        mSelectedTrackTitle = (TextView)findViewById(R.id.id_selected_track_title);
        mSelectedTrackImage = (ImageView)findViewById(R.id.id_selected_track_image);
        tvMoreSongs = findViewById(R.id.idMoreSongs);
        tvMoreAlbums = findViewById(R.id.idMoreAlbums);
        tvMoreLyrics = findViewById(R.id.idMoreLyrics);
        mPlayerControl = (ImageView)findViewById(R.id.id_player_control);

        lyricsModelArrayList = getIntent().getParcelableArrayListExtra("lyrics");
        albumLyricsModelArrayList = getIntent().getParcelableArrayListExtra("album_lyrics");
        albumModelArrayList = getIntent().getParcelableArrayListExtra("album");
        ArrayList<String> temp_urls = getIntent().getStringArrayListExtra("allsongs");
        song_names = new ArrayList<>();
        song_urls = new ArrayList<>();
        for(int i=0;i<temp_urls.size();i++)
        {
            song_urls.add(Endpoints.SITE_URL+temp_urls.get(i).replace(" ", "%20"));
            String[] tt = temp_urls.get(i).split("/");
            song_names.add(tt[tt.length-1]);
        }

        mViewPager = findViewById(R.id.idViewPager);
        swipeAdapter = new SwipeAdapter(this);
        mViewPager.setAdapter(swipeAdapter);

        addDots();
        
        mPlayerControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPause();
            }
        });
        MusicManager.player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayerControl.setImageResource(R.drawable.icon_play);
            }
        });

        mSelectedTrackTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Player.class);
                i.putStringArrayListExtra("song_urls", song_urls);
                i.putExtra("current_song_name", song_names.get(pos));
                i.putExtra("song_pos", pos);
                startActivity(i);
            }
        });

        mSelectedTrackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Player.class);
                i.putStringArrayListExtra("song_urls", song_urls);
                i.putExtra("current_song_name", song_names.get(pos));
                i.putExtra("song_pos", pos);
                startActivity(i);
            }
        });


        tvMoreSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Songs.class);
                startActivity(i);
            }
        });

        tvMoreAlbums.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MoreAlbums.class);
                i.putParcelableArrayListExtra("more_albums", albumModelArrayList);
                startActivity(i);
            }
        });

        tvMoreLyrics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MoreLyrics.class);
                i.putParcelableArrayListExtra("more_lyrics", albumLyricsModelArrayList);
                startActivity(i);
            }
        });
        songsList = findViewById(R.id.idRVSongs);
        songsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        songsList.setAdapter(new SongsAdapterRecyclerView(song_names, this));

        ArrayList<AlbumModel> temp_albumModelArrayList=new ArrayList<AlbumModel>();
        for(int i=0;i<NO_OF_ALBUMS;i++)
            temp_albumModelArrayList.add(albumModelArrayList.get(i));
        RecyclerView albumList = findViewById(R.id.idRVAlbums);
        albumList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        albumList.setAdapter(new AlbumAdapterRecyclerView(temp_albumModelArrayList, this));


        ArrayList<AlbumLyricsModel> temp_albumLyricsModelArrayList=new ArrayList<AlbumLyricsModel>();
        for(int i=0;i<NO_OF_ALBUMS;i++)
            temp_albumLyricsModelArrayList.add(albumLyricsModelArrayList.get(i));
        RecyclerView lyricsList = findViewById(R.id.idRVLyrics);
        lyricsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        lyricsList.setAdapter(new LyricsAdapterRecyclerView(temp_albumLyricsModelArrayList, this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        MenuItem menuItem = menu.findItem(R.id.id_action_search);
        materialSearchView.setMenuItem(menuItem);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    private void addDots() {
        dots = new ArrayList<>();
        LinearLayout dotsLayout = (LinearLayout)findViewById(R.id.idDots);

        for(int i = 0; i < NUM_PAGES; i++) {
            ImageView dot = new ImageView(this);
            dot.setImageDrawable(getResources().getDrawable(R.drawable.pager_dot_selected));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            dotsLayout.addView(dot, params);

            dots.add(dot);
        }

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                selectDot(position);
//                Toast.makeText(MainActivity.this, "Position : "+position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void selectDot(int idx) {
        Resources res = getResources();
        for(int i = 0; i < NUM_PAGES; i++) {
            int drawableId = (i==idx)?(R.drawable.pager_dot_not_selected):(R.drawable.pager_dot_selected);
            Drawable drawable = res.getDrawable(drawableId);
            dots.get(i).setImageDrawable(drawable);
        }
    }

    private void togglePlayPause() {
        if (MusicManager.isPlaying == true) {
            MusicManager.player.pause();
            MusicManager.isPlaying = false;
            mPlayerControl.setImageResource(R.drawable.icon_play);
        } else {
            MusicManager.isPlaying = true;
            MusicManager.player.start();
            mPlayerControl.setImageResource(R.drawable.icon_pause);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(MusicManager.isPlaying==true)
            mPlayerControl.setImageResource(R.drawable.icon_pause);
        else if(!mSelectedTrackTitle.getText().toString().matches(""))
            mPlayerControl.setImageResource(R.drawable.icon_play);

        mSelectedTrackTitle.setText(MusicManager.current_song_name);
        if(!MusicManager.current_song_icon_url.matches(""))
            Picasso.get().load(MusicManager.current_song_icon_url).into(mSelectedTrackImage);

    }

    @Override
    public void onSongClick(int position) {
        pos = position;
        mPlayerControl.setImageResource(R.drawable.icon_pause);
        linearLayoutControlBottom.setBackgroundColor(999999);
        mSelectedTrackTitle.setText(song_names.get(position));
        mSelectedTrackImage.setBackgroundResource(R.drawable.icon_cover);

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        MusicManager.SoundPlayer(this, song_urls.get(position), progress);
    }

    @Override
    public void onImageClick(int position) {
        Log.d("Image: ", position+"");
    }


    @Override
    public void onLyricsTitleClick(int position) {
        Toast.makeText(this, "Title clicked is "+position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLyricsImageClick(int position) {
        Intent i = new Intent(MainActivity.this, LyricsAlbum.class);
        //i.putExtra("image_url", Endpoints.BASE_URL+albumLyricsModelArrayList.get(position).getAlbumCoverImagePath().substring(2));
        i.putExtra("album_lyrics_one_item", albumLyricsModelArrayList.get(position));
        startActivity(i);

    }

    @Override
    public void onAlbumTitleClick(int position) {
        Toast.makeText(this, "Album title clicked is : "+position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAlbumImageClick(int position) {
        Intent i = new Intent(MainActivity.this, SongAlbum.class);
        i.putExtra("album_song_one_item", albumModelArrayList.get(position));
        startActivity(i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onAlbumTitleClickMore(int position) {
        Intent i = new Intent(MainActivity.this, SongAlbum.class);
        i.putExtra("album_song_one_item", albumModelArrayList.get(position));
        startActivity(i);
    }

    @Override
    public void onAlbumImageClickMore(int position) {
        Intent i = new Intent(MainActivity.this, SongAlbum.class);
        i.putExtra("album_song_one_item", albumModelArrayList.get(position));
        startActivity(i);
    }

    @Override
    public void onLyricsTitleClickMore(int position) {
        Intent i = new Intent(MainActivity.this, LyricsAlbum.class);
        //i.putExtra("image_url", Endpoints.BASE_URL+albumLyricsModelArrayList.get(position).getAlbumCoverImagePath().substring(2));
        i.putExtra("album_lyrics_one_item", albumLyricsModelArrayList.get(position));
        startActivity(i);
    }

    @Override
    public void onLyricsImageClickMore(int position) {
        Intent i = new Intent(MainActivity.this, LyricsAlbum.class);
        //i.putExtra("image_url", Endpoints.BASE_URL+albumLyricsModelArrayList.get(position).getAlbumCoverImagePath().substring(2));
        i.putExtra("album_lyrics_one_item", albumLyricsModelArrayList.get(position));
        startActivity(i);
    }
}
