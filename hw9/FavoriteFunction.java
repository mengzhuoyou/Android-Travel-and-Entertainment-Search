package com.example.mengzhuy.hw9;

import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FavoriteFunction {
    /*
     * in main.activity, start->init sharedF
     * in CF, when activity/... get str_F
     *   -> set JSONArray F in CF
     *   -> implement isIteminF, implement add new FItem, delete FItem
     *   -> back JSONArray -> String
     * in activity refresh sharedF
     * */

    private JSONArray Favorites;
    private String str_Favorites;

    public void FavoriteFunction(){
        Favorites = new JSONArray();
        str_Favorites = "";
    }
    public void setFavorites(String str_Favorites){
        this.str_Favorites = str_Favorites;
        if (str_Favorites.length() > 0)
            try {
                Favorites = new JSONArray(str_Favorites);
                return;
            }
            catch(JSONException e){
                System.out.println("JSONArray Parse Error in Getting Favorites");
            }
        Favorites = new JSONArray();
        return;
    }
    public Integer getFavoritesNum(){
        if (Favorites==null)
            return -1;
        return Favorites.length();
    }
    public ArrayList<JSONObject> getFavoritesArrayList(){
        ArrayList<JSONObject> arr = new ArrayList<>();
        if (Favorites==null)
            return arr;
        for (Integer i=0; i < Favorites.length(); ++i){
            try{
                arr.add(Favorites.getJSONObject(i));
            }
            catch (JSONException e){
                System.out.println("getFavoritesArrayList Error in "+i);
            }
        }
        return arr;
    }
    public boolean inFavorites(JSONObject obj){
        try {
            String place_id = obj.getString("place_id");
            for(int i=0 ; i < Favorites.length() ;i++) {
                JSONObject Jobj = Favorites.getJSONObject(i);
                if (place_id.equals(Jobj.getString("place_id")))
                    return true;
            }
        }
        catch (JSONException e){
            System.out.println("JSON get place_id Error");
            e.printStackTrace();
        }
        return false;
    }
    public boolean inFavoritesStr(String place_id){
        try {
            for(int i=0 ; i < Favorites.length() ;i++) {
                JSONObject Jobj = Favorites.getJSONObject(i);
                if (place_id.equals(Jobj.getString("place_id")))
                    return true;
            }
        }
        catch (JSONException e){
            System.out.println("JSON get place_id Error");
            e.printStackTrace();
        }
        return false;
    }
    public void addtoFavorites(JSONObject obj){
        try{
            String place_id = obj.getString("place_id");
            String name = obj.getString("name");
            String icon = obj.getString("icon");
            String vicinity = obj.getString("vicinity");
            JSONObject Jobj = new JSONObject();
            Jobj.put("place_id",place_id);
            Jobj.put("name", name);
            Jobj.put("icon", icon);
            Jobj.put("vicinity", vicinity);
            Favorites.put(Jobj);
            str_Favorites = Favorites.toString();
        }
        catch (JSONException e){
            System.out.println("JSON addtoFavorites Error");
        }
        return;
    }
    public void addtoFavoritesStr(String place_id, String name, String icon, String vicinity){
        try{
            JSONObject Jobj = new JSONObject();
            Jobj.put("place_id",place_id);
            Jobj.put("name", name);
            Jobj.put("icon", icon);
            Jobj.put("vicinity", vicinity);
            Favorites.put(Jobj);
            str_Favorites = Favorites.toString();
        }
        catch (JSONException e){
            System.out.println("JSON addtoFavorites Error");
        }
        return;
    }
    public void removefromFavorites(JSONObject obj){
        try{
            String place_id = obj.getString("place_id");
            for(int i=0 ; i < Favorites.length() ;i++) {
                JSONObject Jobj = Favorites.getJSONObject(i);
                if (place_id.equals(Jobj.getString("place_id"))){
                    Favorites.remove(i);
                    break;
                }
            }
            str_Favorites = Favorites.toString();
            System.out.println("new Fav:"+str_Favorites);
        }
        catch (JSONException e){
            System.out.println("JSON addtoFavorites Error");
        }
        return;
    }
    public void removefromFavoritesStr(String place_id){
        try{
            for(int i=0 ; i < Favorites.length() ;i++) {
                JSONObject Jobj = Favorites.getJSONObject(i);
                if (place_id.equals(Jobj.getString("place_id"))){
                    Favorites.remove(i);
                    break;
                }
            }
            str_Favorites = Favorites.toString();
            System.out.println("new Fav:"+str_Favorites);
        }
        catch (JSONException e){
            System.out.println("JSON addtoFavorites Error");
        }
        return;
    }

    public void UpdateFavorites(SharedPreferences.Editor editor){
        editor.putString("Favorites", str_Favorites);
        editor.commit();
        return;
    }
}
