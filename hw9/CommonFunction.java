package com.example.janvierzz.hw9;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class CommonFunction {
    private static ArrayList<ArrayList<JSONObject>> SearchAllResults;
    private static String location, next_page_token;
    public static String baseURL="http://10.123.5.19:8080";//"http://10.0.39.211:8080";//

    public CommonFunction(){
        initSearchAllResults();
    }

    public void initSearchAllResults(){
        SearchAllResults = new ArrayList<>();
        location = "";
        next_page_token = "";
    }
    /*public void setSearchAllResults(ArrayList<ArrayList<JSONObject>> CurSearchAllResults){
        SearchAllResults = CurSearchAllResults;
    }
    public ArrayList<ArrayList<JSONObject>> getSearchAllResults(){
        return SearchAllResults;
    }*/
    public void setSearchAllResults(String CurSearchAllResults){
        SearchAllResults = new ArrayList<>();
        try {
            JSONArray tmp1 = new JSONArray(CurSearchAllResults);
            for (Integer i=0; i<tmp1.length(); ++i){
                JSONArray tmp2 = new JSONArray(tmp1.getJSONArray(i));
                ArrayList<JSONObject> tmp = new ArrayList<>();
                for (Integer j=0; j<tmp2.length(); ++j)
                    tmp.add(tmp2.getJSONObject(j));
                SearchAllResults.add(tmp);
            }
        }
        catch(JSONException e){
            System.out.println("JSON Parse Error in setSearchAllResults");
        }
    }
    public String getSearchAllResults(){
        return SearchAllResults.toString();
    }

    public Integer getSearchAllResultsNum(){
        return SearchAllResults.size();
    }
    public ArrayList<JSONObject> getSearchResults(int idx){
        if (SearchAllResults.size() == 0)
            return new ArrayList<>();
        else
            return SearchAllResults.get(idx);
    }
    public void addSearchAllResults(ArrayList<JSONObject> SearchResults){
        SearchAllResults.add(SearchResults);
    }
    public void setlocation(String lat, String lon){
        location = lat+","+lon;
    }
    public String getLocation(){
        return location;
    }
    public void setNext_page_token(String Curnext_page_token){
        next_page_token = Curnext_page_token;
    }
    public String getNext_page_token(){
        return next_page_token;
    }

    public String getResponseJava(HttpURLConnection urlConnection) {
        InputStream in = null;
        try {
            in = urlConnection.getInputStream();//响应
        } catch (IOException e) {
            urlConnection.disconnect();
            return "";
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        final StringBuilder result = new StringBuilder();
        String tmp = null;
        try {
            while((tmp = reader.readLine()) != null){
                result.append(tmp);
            }
        } catch (IOException e) {
            //textView.setText(e.getMessage());
            return "";
        } finally {
            try {
                reader.close();
                urlConnection.disconnect();
            } catch (IOException e) {
            }
        }
        return result.toString();
    }

    public void AddtoSearchResults(JSONObject SearchResult){
        ArrayList<JSONObject> SearchResults = new ArrayList<>();
        JSONArray results;
        try{
            results = SearchResult.getJSONArray("results");
        }
        catch (JSONException e) {
            System.out.println("JSON Parse Error in Getting Results");
            return;
        }
        if (results.length()==0)
            return;
        for (Integer i = 0; i < results.length(); ++i) {
            try {
                SearchResults.add(results.getJSONObject(i));
            } catch (JSONException e) {
                System.out.println("JSON Parse Error in Getting Results Items at " + i.toString());
                return;
            }
        }
        addSearchAllResults(SearchResults);
        setNext_page_token("");
        try{
            if (SearchResult.has("next_page_token")){
                setNext_page_token(SearchResult.getString("next_page_token"));
            }
            else {
                System.out.println("NO More next_page_token ");
                return;
            }
        }
        catch (JSONException e) {
            System.out.println("JSON Parse Error in Getting next_page_token");
            return;
        }
        //DecodeNextSearchResults(next_page_token);
    }
    public void DecodeSearchResults(String result){
        initSearchAllResults();
        JSONObject SearchResult;
        try {
            SearchResult = new JSONObject(result);
        }
        catch (JSONException e) {
            System.out.println("JSON Parse Error");
            e.printStackTrace();
            return;
        }
        try{
            String lat = SearchResult.getString("lat");
            String lon = SearchResult.getString("lon");
            setlocation(lat, lon);
            SearchResult = SearchResult.getJSONObject("res");
            AddtoSearchResults(SearchResult);
        }
        catch (JSONException e) {
            System.out.println("JSON Parse Error in Getting res&lat&lon");
            return;
        }
    }
    public void DecodeNextSearchResults(final String next_page_token, final CountDownLatch countDownLatch){
        new Thread(new Runnable() {
            @Override
            public void run() {HttpURLConnection connection = null;
                URL url = null;
                try {
                    url = new URL(baseURL+"/search/next?next_page_token="+next_page_token);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setRequestProperty("Content-Type","application/json");
                    String results = getResponseJava(connection);
                    if (results.length()>0){
                        JSONObject SearchResult;
                        try {
                            SearchResult = new JSONObject(results);
                            AddtoSearchResults(SearchResult);
                        }
                        catch (JSONException e) {
                            System.out.println("JSON Parse Error on nextPage");
                            e.printStackTrace();
                            return;
                        }
                    }
                    else{
                        //No results
                        System.out.println("No results: "+results);
                        setNext_page_token("");
                        return;
                    }
                } catch (MalformedURLException e) {
                    System.out.println("MalformedURLException");
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    System.out.println("ProtocolException");
                    e.printStackTrace();
                } catch (IOException e) {
                    System.out.println("IOException");
                    e.printStackTrace();
                }
                countDownLatch.countDown();
                //progressDialog.dismiss();
            }
        }).start();
    }

}
