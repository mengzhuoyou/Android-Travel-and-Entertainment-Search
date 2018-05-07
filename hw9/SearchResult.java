package com.example.mengzhuy.hw9;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import java.io.StringReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import static android.provider.AlarmClock.EXTRA_MESSAGE;


public class SearchResult extends AppCompatActivity {
    /*
    public ArrayList<String> jsonStringToArray(String jsonString) throws JSONException {
        ArrayList<String> stringArray = new ArrayList<String>();
        JSONArray jsonArray = new JSONArray(jsonString);
        for (int i = 0; i < jsonArray.length(); i++) {
            stringArray.add(jsonArray.getString(i));
        }
        return stringArray;
    }*/

    private Button btn_pre, btn_next;
    private ArrayList<JSONObject> SearchResults;
    private Integer PageIdx;
    private ListView SR_ListView;
    private CustomListViewAdapter SR_Adapter;
    private Context context;
    private CommonFunction CF;
    private ProgressDialog progressDialog;

    private void createlistview(String listview, int idx){
        CF = new CommonFunction();
        PageIdx = idx;

        setContentView(R.layout.activity_search_result);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btn_pre = findViewById(R.id.btn_pre);
        btn_next = findViewById(R.id.btn_next);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CF.DecodeSearchResults(listview);
        SearchResults = new ArrayList<>();
        copySearchResults(CF.getSearchResults(PageIdx));

        SR_ListView = findViewById(R.id.ListView_content);
        SR_ListView.setEmptyView(findViewById(R.id.NoResults));
        context = this;
        SR_Adapter = new CustomListViewAdapter(this, SearchResults, false);
        SR_ListView.setAdapter(SR_Adapter);
        SetBtnEnable(PageIdx);

        SR_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position,
                                    long id) {
                try{
                    final String place_id = SearchResults.get(position).getString("place_id");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HttpURLConnection connection = null;
                            URL url = null;
                            String results = "";
                            Intent intent = new Intent(view.getContext(), PlaceDetail.class);
                            try {
                                url = new URL(CF.baseURL+"/search/detail?place_id="+place_id);
                                connection = (HttpURLConnection) url.openConnection();
                                connection.setRequestMethod("GET");
                                connection.setConnectTimeout(8000);
                                connection.setReadTimeout(8000);
                                connection.setRequestProperty("Content-Type","application/json");
                                results = CF.getResponseJava(connection);
                                if (results.length()>0){
                                    intent.putExtra("PlaceDetail", results);
                                    startActivity(intent);
                                }
                                else{
                                    //No results
                                    return;
                                }
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (ProtocolException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();
                }
                catch (Exception e){
                    System.out.println("Getting Details Error");
                    Toast.makeText(view.getContext(),"Getting Details Error", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        btn_pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                -- PageIdx;
                copySearchResults(CF.getSearchResults(PageIdx));
                SR_Adapter.notifyDataSetChanged();
                SetBtnEnable(PageIdx);
            }
        });
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++ PageIdx;
                if (PageIdx < CF.getSearchAllResultsNum()) {
                    copySearchResults(CF.getSearchResults(PageIdx));
                    SR_Adapter.notifyDataSetChanged();
                    SetBtnEnable(PageIdx);
                } else {
                    FetchingNextPage(v);
                }
            }
        });

    }

    private String listview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        listview = intent.getStringExtra("SearchString");
        createlistview(listview, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        Integer pre_pageIdx = PageIdx;
        createlistview(listview, 0);
        while (PageIdx < pre_pageIdx) {
            try {
                CountDownLatch countDownLatch = new CountDownLatch(1);
                CF.DecodeNextSearchResults(CF.getNext_page_token(), countDownLatch);
                countDownLatch.await();
            } catch (Exception e) {
                System.out.println("CountDownLatch Error");
                e.printStackTrace();
            }
            //FetchingNextPage(findViewById(R.id.btn_next));
            ++ PageIdx;
        }
        copySearchResults(CF.getSearchResults(PageIdx));
        SR_Adapter.notifyDataSetChanged();
        SetBtnEnable(PageIdx);
        //System.out.println("zzzzNum2="+CF.getSearchAllResultsNum());
    }

    private void SetBtnEnable(Integer PageIdx){
        if (PageIdx==0)
            btn_pre.setEnabled(false);
        else
            btn_pre.setEnabled(true);
        if (PageIdx+1 < CF.getSearchAllResultsNum() || CF.getNext_page_token().length()>0)
            btn_next.setEnabled(true);
        else
            btn_next.setEnabled(false);
    }

    private void copySearchResults(ArrayList<JSONObject> newSearchResults){
        SearchResults.clear();
        for (Integer i = 0; i < newSearchResults.size(); ++i)
            //SearchResults.add(newSearchResults.get(i));
            try{
                SearchResults.add(new JSONObject(newSearchResults.get(i).toString()));
            }
            catch (Exception e){
                System.out.println("Fail in copySearchResults");
                e.printStackTrace();
            }
        return;
    }
    private void FetchingNextPage(final View v){
        AsyncTask<Integer,Void,Void> task = new AsyncTask<Integer,Void,Void>() {
            ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(v.getContext(), "", "Fetching next page", true, false);

            }
            @Override
            protected Void doInBackground(Integer... integers) {
                try {
                    CountDownLatch countDownLatch = new CountDownLatch(1);
                    CF.DecodeNextSearchResults(CF.getNext_page_token(), countDownLatch);
                    countDownLatch.await();
                } catch (Exception e) {
                    System.out.println("CountDownLatch Error");
                    e.printStackTrace();
                }
                copySearchResults(CF.getSearchResults(PageIdx));
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                progressDialog.cancel();
                SR_Adapter.notifyDataSetChanged();
                SetBtnEnable(PageIdx);
            }

        };
        task.execute();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("SearchResult", CF.getSearchAllResults());
        savedInstanceState.putInt("PageIdx", PageIdx);
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String listview = savedInstanceState.getString("SearchResult");
        Integer PageIdx = savedInstanceState.getInt("PageIdx");
        createlistview(listview, PageIdx);
    }
}
