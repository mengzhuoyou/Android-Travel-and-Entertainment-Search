package com.example.janvierzz.hw9;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;


public class Tab_Favorite extends Fragment{
    SharedPreferences sharedFavorites;
    Editor editor;
    FavoriteFunction FF;
    private ListView F_ListView;
    private CustomListViewAdapter F_Adapter;
    ArrayList<JSONObject> Favorites;
    private CommonFunction CF;
    private Activity activity;
    private View rootview;

    private void copyFavorites(ArrayList<JSONObject> newSearchResults){
        Favorites.clear();
        for (Integer i = 0; i < newSearchResults.size(); ++i)
            Favorites.add(newSearchResults.get(i));
        return;
    }

    private void createlistview(){
        CF = new CommonFunction();
        FF = new FavoriteFunction();

        sharedFavorites =  activity.getSharedPreferences("PlaceSearch", Context.MODE_PRIVATE);
        FF.setFavorites(sharedFavorites.getString("Favorites",""));
        editor = sharedFavorites.edit();

        F_ListView = rootview.findViewById(R.id.FavListView_content);
        Favorites = new ArrayList<>();
        F_ListView.setEmptyView(rootview.findViewById(R.id.NoFavorites));

        if (FF.getFavoritesNum() > 0){
            copyFavorites(FF.getFavoritesArrayList());
            F_Adapter = new CustomListViewAdapter(getActivity(), Favorites, true);
            F_ListView.setAdapter(F_Adapter);
        }
        else {
            F_Adapter = new CustomListViewAdapter(getActivity(), Favorites, true);
            F_ListView.setAdapter(F_Adapter);
        }

        F_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position,
                                    long id) {
                try{
                    final String place_id = Favorites.get(position).getString("place_id");
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

    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_tab__favorite, container, false);
        activity = this.getActivity();

        createlistview();

        return rootview;
    }

    @Override
    public void onResume() {
        super.onResume();
        createlistview();
    }

}