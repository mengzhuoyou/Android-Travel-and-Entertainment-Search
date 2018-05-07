package com.example.mengzhuy.hw9;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Places;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CustomListViewAdapter extends ArrayAdapter {

    private ArrayList<JSONObject> dataList;
    private Context mContext;
    private SharedPreferences sharedFavorites;
    private Editor editor;
    private FavoriteFunction FF;
    private final boolean isFav;

    public CustomListViewAdapter(Context context,  ArrayList<JSONObject> CdataList, boolean CisFav) {
        //super(context, android.R.layout.simple_dropdown_item_1line, new ArrayList<MyPlace>());
        super(context, R.layout.list_items, CdataList);
        mContext = context;
        dataList = CdataList;
        isFav = CisFav;

        FF = new FavoriteFunction();
        sharedFavorites =  mContext.getSharedPreferences("PlaceSearch", Context.MODE_PRIVATE);
        //if(!sharedFavorites.contains("Favorites"))
        FF.setFavorites(sharedFavorites.getString("Favorites",""));
        editor = sharedFavorites.edit();

    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_items, parent, false);
        }

        TextView text1 = view.findViewById(R.id.ItemTitle);
        try{
            text1.setText(dataList.get(position).get("name").toString());
        }
        catch (JSONException e) {
            System.out.println("JSON Parse Error in Getting Name");
            text1.setText("");
        }
        TextView text2 = view.findViewById(R.id.ItemText);
        try{
            text2.setText(dataList.get(position).get("vicinity").toString());
        }
        catch (JSONException e) {
            System.out.println("JSON Parse Error in Getting Vicinity");
            text2.setText("");
        }
        ImageView img1 = view.findViewById(R.id.ItemImage);
        try{
            Picasso.get().load(dataList.get(position).get("icon").toString()).into(img1);
        }
        catch (JSONException e) {
            System.out.println("JSON Parse Error in Getting Icon");
        }
        final ImageView img2 = view.findViewById(R.id.ItemFav);
        if (FF.inFavorites(dataList.get(position))){
            img2.setTag(R.drawable.heart_fill_red_icon);
            img2.setImageResource(R.drawable.heart_fill_red_icon);
        }
        else {
            img2.setTag(R.drawable.heart_outline_black_icon);
            img2.setImageResource(R.drawable.heart_outline_black_icon);
        }

        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = "";
                try {
                    s = dataList.get(position).get("name").toString();
                }
                catch (JSONException e){
                    System.out.println("Favorite Click Failed");
                    return;
                }
                if ((Integer)img2.getTag() == R.drawable.heart_outline_black_icon){
                    FF.addtoFavorites(dataList.get(position));
                    img2.setImageResource(R.drawable.heart_fill_red_icon);
                    img2.setTag(R.drawable.heart_fill_red_icon);
                    Toast.makeText(getContext(), s+"was added to favorites", Toast.LENGTH_SHORT).show();
                }
                else{
                    FF.removefromFavorites(dataList.get(position));
                    img2.setTag(R.drawable.heart_outline_black_icon);
                    img2.setImageResource(R.drawable.heart_outline_black_icon);
                    Toast.makeText(getContext(), s+"was removed from favorites", Toast.LENGTH_SHORT).show();
                    //if it's favorite fragement
                    if (isFav){
                        dataList.remove(position);
                        notifyDataSetChanged();
                    }
                }
                FF.UpdateFavorites(editor);
            }
        });

        return view;
    }

}
