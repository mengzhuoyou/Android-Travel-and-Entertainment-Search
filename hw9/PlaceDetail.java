package com.example.mengzhuy.hw9;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class PlaceDetail extends AppCompatActivity implements TabPhoto.OnCompleteListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    public static String results, resultsyelp;
    public static DetailFunction DF;
    private ProgressDialog progressDialog;

    private FavoriteFunction FF = new FavoriteFunction();
    private SharedPreferences sharedFavorites;
    private Editor editor;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        //Show ProgressDialog
        progressDialog = ProgressDialog.show(this, "", "Fetching details", true, false);

        Intent intent = getIntent();
        results = intent.getStringExtra("PlaceDetail");
        DF = new DetailFunction(results);

        //get yelp reviews
        getResultsYelp(DF.getYelpreviews());

        Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(DF.name);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ImageView ItemFav = findViewById(R.id.ItemFav);
        FF = new FavoriteFunction();
        sharedFavorites = getSharedPreferences("PlaceSearch", Context.MODE_PRIVATE);
        FF.setFavorites(sharedFavorites.getString("Favorites",""));
        editor = sharedFavorites.edit();
        context = this;
        final String place_id = DF.place_id;
        final String name = DF.name;
        final String icon = DF.icon;
        final String vicinity = DF.address;
        if (FF.inFavoritesStr(place_id)){
            ItemFav.setTag(R.drawable.heart_fill_red_icon);
            ItemFav.setImageResource(R.drawable.heart_fill_red_icon);
        }
        else {
            ItemFav.setTag(R.drawable.heart_fill_white_icon);
            ItemFav.setImageResource(R.drawable.heart_fill_white_icon);
        }
        ItemFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Integer)ItemFav.getTag() == R.drawable.heart_fill_white_icon){
                    FF.addtoFavoritesStr(place_id, name, icon, vicinity);
                    ItemFav.setImageResource(R.drawable.heart_fill_red_icon);
                    ItemFav.setTag(R.drawable.heart_fill_red_icon);
                    Toast.makeText(context, name+"was added to favorites", Toast.LENGTH_SHORT).show();
                }
                else{
                    FF.removefromFavoritesStr(place_id);
                    ItemFav.setTag(R.drawable.heart_fill_white_icon);
                    ItemFav.setImageResource(R.drawable.heart_fill_white_icon);
                    Toast.makeText(context, name+"was removed from favorites", Toast.LENGTH_SHORT).show();
                }
                FF.UpdateFavorites(editor);
            }
        });

        final ImageView ItemShare = findViewById(R.id.ItemShare);
        ItemShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent("android.intent.action.VIEW",
                        Uri.parse("https://twitter.com/intent/tweet?text=Check out "+
                                DF.name+" located at "+DF.address+". Website: "+DF.website+" %23TravelAndEntertainmentSearch"));
                startActivity(browserIntent);
            }
        });

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs2);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    public void onComplete() {
        // After the fragment completes, it calls this callback.
        // setup the rest of your layout now
        progressDialog.dismiss();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_place_detail, menu);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == android.R.id.home)
        {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 4;
        }
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position){
                case 0:
                    TabInfo tabInfo = new TabInfo();
                    return tabInfo;
                case 1:
                    TabPhoto tabPhoto = new TabPhoto();
                    return tabPhoto;
                case 2:
                    TabMap tabMap = new TabMap();
                    return tabMap;
                case 3:
                    TabReviews tabReviews = new TabReviews();
                    return tabReviews;
                default:
                    return null;
            }
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 0:
                    return "INFO";
                case 1:
                    return "PHOTOS";
                case 2:
                    return "MAP";
                case 3:
                    return "REVIEWS";
            }
            return null;
        }
    }
    private void getResultsYelp(final String data){
        try{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        CommonFunction CF = new CommonFunction();
                        URL url = new URL(CF.baseURL+"/yelp?"+data);
                        HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(8000);
                        connection.setReadTimeout(8000);
                        connection.setRequestProperty("Content-Type","application/json");
                        resultsyelp = CF.getResponseJava(connection);
                        System.out.println("Gettingrevie2w="+resultsyelp); //no result
                        if (resultsyelp.length()>0) {
                            // get reviewsyelp
                            if (resultsyelp.compareTo("NoReview") == 0 || resultsyelp.compareTo("UnMatch") == 0) {
                                return;
                            }
                            DF.setYelpreviews(resultsyelp);
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
