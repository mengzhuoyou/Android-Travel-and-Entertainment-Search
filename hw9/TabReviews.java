package com.example.mengzhuy.hw9;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.mengzhuy.hw9.dummy.DummyContent;
import com.example.mengzhuy.hw9.dummy.DummyContent.DummyItem;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class TabReviews extends Fragment {
    private int mColumnCount = 1;
    ///private OnListFragmentInteractionListener mListener;

    private DetailFunction DF;
    private RecyclerView ReviewView;
    private TextView NoReviews;
    private MyTabReviewsRecyclerViewAdapter Review_Adapter;
    private ArrayList<DetailFunction.Review> reviews;
    private Spinner review_mode;
    private Spinner review_order;
    private Integer mode, order;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TabReviews() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DF = PlaceDetail.DF;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tabreviews_list, container, false);
        Context context = view.getContext();

        ReviewView = view.findViewById(R.id.reviewlist);
        NoReviews =  view.findViewById(R.id.NoReviews);
        context = this.getContext();
        reviews = new ArrayList<>();

        if (DF.reviews.size()==0){
            ReviewView.setVisibility(View.GONE);
            NoReviews.setVisibility(View.VISIBLE);
        }
        else{
            copyReviews(DF.reviews);
            ReviewView.setVisibility(View.VISIBLE);
            NoReviews.setVisibility(View.GONE);
        }
        ReviewView.setLayoutManager(new LinearLayoutManager(context));
        Review_Adapter = new MyTabReviewsRecyclerViewAdapter(this.getContext(), reviews);
        ReviewView.setAdapter(Review_Adapter);

        review_mode = view.findViewById(R.id.review_mode);
        review_order = view.findViewById(R.id.review_order);
        mode = 0;
        order = 0;

        review_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (review_mode.getSelectedItem().toString().compareTo("Google reviews")==0){
                    mode = 0; sort();
                }
                else{
                    mode = 1; sort();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        review_order.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (review_order.getSelectedItem().toString().compareTo("Default Order")==0){
                    order = 0;sort();
                }
                else if (review_order.getSelectedItem().toString().compareTo("Highest Rating")==0){
                    order = 1;sort();
                }
                else if (review_order.getSelectedItem().toString().compareTo("Lowest Rating")==0){
                    order = 2;sort();
                }
                else if (review_order.getSelectedItem().toString().compareTo("Most Recent")==0){
                    order = 3;sort();
                }
                else if (review_order.getSelectedItem().toString().compareTo("Least Recent")==0){
                    order = 4;sort();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }
    private void sort(){
        //System.out.println("mode="+mode+",order="+order);
        if (mode == 0)
            copyReviews(DF.reviews);
        else
            copyReviews(DF.reviewsyelp);
        switch (order){
            case 2:
                Collections.sort(reviews, new Comparator<DetailFunction.Review>() {
                    @Override
                    public int compare(DetailFunction.Review t1, DetailFunction.Review t2)
                    {
                        if (Math.abs(t1.rating-t2.rating) < 0.00000001)
                            return 0;
                        else if (t1.rating<t2.rating)
                            return -1;
                        else
                            return 1;
                    }
                });
                break;
            case 1:
                Collections.sort(reviews, new Comparator<DetailFunction.Review>() {
                    @Override
                    public int compare(DetailFunction.Review t1, DetailFunction.Review t2)
                    {
                        if (Math.abs(t1.rating-t2.rating) < 0.00000001)
                            return 0;
                        else if (t1.rating<t2.rating)
                            return 1;
                        else
                            return -1;
                    }
                });
                break;
            case 4:
                Collections.sort(reviews, new Comparator<DetailFunction.Review>() {
                    @Override
                    public int compare(DetailFunction.Review t1, DetailFunction.Review t2)
                    {
                        return (int)(t1.time - t2.time);
                    }
                });
                break;
            case 3:
                Collections.sort(reviews, new Comparator<DetailFunction.Review>() {
                    @Override
                    public int compare(DetailFunction.Review t1, DetailFunction.Review t2)
                    {
                        return (int)(t2.time - t1.time);
                    }
                });
                break;
            default:
                break;
        }
        if (reviews.size()==0){
            ReviewView.setVisibility(View.GONE);
            NoReviews.setVisibility(View.VISIBLE);
        }
        else{
            ReviewView.setVisibility(View.VISIBLE);
            NoReviews.setVisibility(View.GONE);
            Review_Adapter.notifyDataSetChanged();
        }
        return;
    }

    private void copyReviews(ArrayList<DetailFunction.Review> newReviews){
        reviews.clear();
        for (Integer i = 0; i < newReviews.size(); ++i)
            reviews.add(newReviews.get(i));
        return;
    }

    /*

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }*/
}
