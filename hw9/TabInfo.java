package com.example.janvierzz.hw9;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TabInfo.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TabInfo#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabInfo extends Fragment {

    private View rootview;
    private DetailFunction DF;

    //private OnFragmentInteractionListener mListener;

    public TabInfo() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_tab_info, container, false);
        DF = PlaceDetail.DF;
        if (DF.address.length() > 0)
            ((TextView)rootview.findViewById(R.id.Address_c)).setText(DF.address);
        else
            rootview.findViewById(R.id.Address).setVisibility(View.GONE);
        if (DF.phone.length() > 0)
            ((TextView)rootview.findViewById(R.id.Phone_c)).setText(DF.phone);
        else
            rootview.findViewById(R.id.Phone).setVisibility(View.GONE);
        if (DF.price.length() > 0)
            ((TextView)rootview.findViewById(R.id.Price_c)).setText(DF.price);
        else
            rootview.findViewById(R.id.Price).setVisibility(View.GONE);
        if (DF.rating >= 0)
            ((RatingBar)rootview.findViewById(R.id.Rating_c)).setRating(DF.rating);
        else
            rootview.findViewById(R.id.Rating).setVisibility(View.GONE);
        if (DF.google.length() > 0)
            ((TextView)rootview.findViewById(R.id.Google_c)).setText(DF.google);
        else
            rootview.findViewById(R.id.Google).setVisibility(View.GONE);
        if (DF.website.length() > 0)
            ((TextView)rootview.findViewById(R.id.Website_c)).setText(DF.website);
        else
            rootview.findViewById(R.id.Website).setVisibility(View.GONE);
        return rootview;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /*
    @Override
    public void onStart() {
        super.onStart();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/
}
