package com.example.mengzhuy.hw9;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import static com.example.mengzhuy.hw9.PlaceDetail.resultsyelp;

public class DetailFunction {
    String place_id, address, phone, price, google, website, name, icon;
    Integer price_level;
    float rating;
    /*class Photo{
        Integer h, w;
        String url;
    }
    ArrayList<Photo> photos;*/
    class Review{
        String author_name, author_url, profile_photo_url, text;
        float rating = 0.0f;
        long time;
        String time_text;
    }
    ArrayList<Review> reviews, reviewsyelp;
    public DetailFunction(String results){
        address = "";
        phone = "";
        price = "";
        google = "";
        website = "";
        rating = -1;
        reviews = new ArrayList<>();
        reviewsyelp = new ArrayList<>();
        name = "";
        price  = "";
        icon = "";
        try{
            JSONObject obj = new JSONObject(results);
            obj = obj.getJSONObject("result");
            if (obj.length() == 0)
                return;
            if (obj.has("name"))
                name = obj.getString("name");
            if (obj.has("icon"))
                icon = obj.getString("icon");
            if (obj.has("formatted_address"))
                address = obj.getString("formatted_address");
            if (obj.has("formatted_phone_number"))
                phone = obj.getString("formatted_phone_number");
            if (obj.has("price_level")){
                price_level = obj.getInt("price_level");
                for (Integer i=0; i<price_level; ++i)
                    price += "$";
            }
            if (obj.has("place_id"))
                place_id = obj.getString("place_id");
            if (obj.has("rating"))
                rating = Float.parseFloat(obj.getString("rating"));
            if (obj.has("url"))
                google = obj.getString("url");
            if (obj.has("website"))
                website = obj.getString("website");
            /*if (obj.has("photos")){
                JSONArray arr = (obj.getJSONArray("photos"));
                for (Integer i=0; i<arr.length(); ++i){
                    Photo photo = new Photo();
                    photo.h = arr.getJSONObject(i).getInt("height");
                    photo.w = arr.getJSONObject(i).getInt("width");
                    photo.url = arr.getJSONObject(i).getString("photo_reference");
                    photos.add(photo);
                }
            }*/
            if (obj.has("reviews")){
                JSONArray arr = new JSONArray(obj.getString("reviews"));
                for (Integer i=0; i<arr.length(); ++i){
                    Review review = new Review();
                    JSONObject cobj = arr.getJSONObject(i);
                    if (cobj.has("author_name"))
                        review.author_name = cobj.getString("author_name");
                    else
                        review.author_name = "";
                    if (cobj.has("author_url"))
                        review.author_url = cobj.getString("author_url");
                    else
                        review.author_url = "";

                    if (cobj.has("profile_photo_url"))
                        review.profile_photo_url = cobj.getString("profile_photo_url");
                    else
                        review.profile_photo_url = "";
                    if (cobj.has("text"))
                        review.text = cobj.getString("text");
                    else
                        review.text = "";
                    if (cobj.has("rating"))
                        review.rating = Float.parseFloat(cobj.getString("rating"));
                    else
                        review.rating = -1;
                    if (cobj.has("time")){
                        review.time = (long)cobj.getInt("time");
                        Date date = new Date();
                        date.setTime(review.time*1000);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss aaa");
                        review.time_text = sdf.format(date);
                    }
                    else {
                        review.time = -1;
                        review.time_text = "";
                    }
                    reviews.add(review);
                }
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return;
    }
    public String getYelpreviews() {
        String city = address.split(",")[1];
        city = city.substring(1);
        String state = address.split(",")[2];
        //String name = name;
        String address1 = address.split(",")[0];
        String address2 = city + ", " + state.substring(1);
        //city
        state = state.split(" ")[1];
        String country = "US";
        String str = "name=" + name + "&address1=" + address1 + "&address2="
                + address2 + "&city=" + city + "&state=" + state + "&country=" + country;
        str = str.replace(' ', '+');
        /*System.out.println(address);
        System.out.println("rrrrrr1="+str);*/
        return str;
    }
    public void setYelpreviews(String resultsyelp){
        try{
            JSONArray arr = new JSONArray(resultsyelp);
            for (Integer i = 0; i<arr.length(); ++i) {
                JSONObject cobj = arr.getJSONObject(i);
                DetailFunction.Review review = new DetailFunction.Review();
                if (cobj.has("rating"))
                    review.rating = Float.parseFloat(cobj.getString("rating"));
                else
                    review.rating = -1;
                if (cobj.getJSONObject("user").has("name"))
                    review.author_name = cobj.getJSONObject("user").getString("name");
                else
                    review.author_name = "";
                if (cobj.getJSONObject("user").has("image_url"))
                    review.profile_photo_url = cobj.getJSONObject("user").getString("image_url");
                else
                    review.profile_photo_url = "";
                if (cobj.has("url"))
                    review.author_url = cobj.getString("url");
                else
                    review.author_url = "";
                if (cobj.has("text"))
                    review.text = cobj.getString("text");
                else
                    review.text = "";

                review.time_text = cobj.getString("time_created");

                final String OLD_FORMAT = "yyyy-MM-dd HH:mm:ss";
                final String NEW_FORMAT = "yyyy-MM-dd HH:mm:ss aaa";
                String oldDateString = review.time_text;
                SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
                Date d = sdf.parse(oldDateString);
                sdf.applyPattern(NEW_FORMAT);
                review.time_text = sdf.format(d);
                review.time = d.getTime() / 1000;
                reviewsyelp.add(review);

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}
