package com.example.mengzhuy.hw9;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
//import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;


import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;


public class Tab_Search extends Fragment {

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_tab__search, container, false);
        return rootview;
    }*/

    private View rootview;
    private Button btn_search, btn_clear;
    private RadioGroup btn_locations;
    private boolean NoError = true;
    private EditText keyword, distance;
    private AutoCompleteTextView location;
    private Spinner category;
    private RadioButton btn_location1, btn_location2;
    private String str_keyword, str_distance, str_location, str_category;
    private Integer num_distance;

    private Intent intent;
    private String message;
    private static ProgressDialog progressDialog;
    private CommonFunction CF = new CommonFunction();
    //private DataTransfer DT = new DataTransfer();


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        rootview = inflater.inflate(R.layout.fragment_tab__search, container, false);
        keyword = rootview.findViewById(R.id.input_keyword);
        category = rootview.findViewById(R.id.input_category);
        distance = rootview.findViewById(R.id.input_distance);
        location = rootview.findViewById(R.id.input_location);
        btn_locations = rootview.findViewById(R.id.btn_locations);
        btn_location1 = rootview.findViewById(R.id.btn_location1);
        btn_location2 = rootview.findViewById(R.id.btn_location2);
        btn_search = rootview.findViewById(R.id.btn_search);
        btn_clear = rootview.findViewById(R.id.btn_clear);

        CustomAutoCompleteAdapter adapter =  new CustomAutoCompleteAdapter(getActivity());
        location.setAdapter(adapter);
        location.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                /*Toast.makeText(getActivity(),
                        "selected place "
                                + ((MyPlace)adapterView.
                                getItemAtPosition(i)).getPlaceText()
                        , Toast.LENGTH_SHORT).show();*/
            }
        });
        return rootview;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();


        btn_locations.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group,int checkedId){
                if (btn_location1.getId() == checkedId){
                    rootview.findViewById(R.id.error_location).setVisibility(View.GONE);
                    rootview.findViewById(R.id.input_location).setFocusable(false);
                }
                if (btn_location2.getId() == checkedId){
                    rootview.findViewById(R.id.input_location).setFocusableInTouchMode(true);
                }
            }
        });

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CF.initSearchAllResults();
                keyword.setText(null);
                rootview.findViewById(R.id.error_keyword).setVisibility(View.GONE);
                category.setSelection(0);
                distance.setText(null);
                location.setText(null);
                btn_location2.setChecked(false);
                btn_location1.setChecked(true);
                rootview.findViewById(R.id.error_location).setVisibility(View.GONE);
                rootview.findViewById(R.id.input_location).setFocusable(false);
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoError = true;

                str_keyword= keyword.getText().toString();
                if (str_keyword.matches("")) {
                    rootview.findViewById(R.id.error_keyword).setVisibility(View.VISIBLE);
                    NoError = false;
                }
                else{
                    rootview.findViewById(R.id.error_keyword).setVisibility(View.GONE);
                }
                System.out.println(str_keyword);

                str_category = category.getSelectedItem().toString();
                System.out.println(str_category);

                str_distance = distance.getText().toString();
                if (str_distance.matches(""))
                    num_distance = 10;
                else
                    num_distance = Integer.parseInt(str_distance);
                System.out.println(num_distance);

                str_location = "Here";
                btn_location1 = rootview.findViewById(R.id.btn_location1);
                btn_location2 = rootview.findViewById(R.id.btn_location2);
                if (btn_location1.isChecked()){
                    str_location = "Here";
                }
                else if (btn_location2.isChecked()){
                    str_location = location.getText().toString();
                    if (str_location.matches("")) {
                        rootview.findViewById(R.id.error_location).setVisibility(View.VISIBLE);
                        NoError = false;
                    }
                    else{
                        rootview.findViewById(R.id.error_location).setVisibility(View.GONE);
                    }
                }
                System.out.println(str_location);

                if (NoError){
                    message = "keyword="+str_keyword+"&category="+str_category+"&distance="+num_distance.toString()
                            +"&location="+str_location;
                    //Show ProgressDialog
                    progressDialog = ProgressDialog.show(getActivity(), "", "Fetching results", true, false);

                    //link to node.js server
                    //Toast.makeText(getActivity(),"linking", Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            HttpURLConnection connection = null;
                            URL url = null;
                            try {
                                url = new URL(CF.baseURL+"/search?"+message);
                                connection = (HttpURLConnection) url.openConnection();
                                connection.setRequestMethod("GET");
                                connection.setConnectTimeout(8000);
                                connection.setReadTimeout(8000);
                                connection.setRequestProperty("Content-Type","application/json");
                                String results = CF.getResponseJava(connection);
                                if (results.length()>0){
                                    intent = new Intent(getActivity(),SearchResult.class);
                                    intent.putExtra("SearchString", results);
                                    progressDialog.dismiss();
                                    //startActivity(intent);
                                    startActivityForResult(intent, 1);
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
                else {
                    Toast.makeText(getActivity(), "Please fix all fields with errors", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
