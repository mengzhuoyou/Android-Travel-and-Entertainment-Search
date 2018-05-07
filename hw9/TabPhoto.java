package com.example.janvierzz.hw9;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;


public class TabPhoto extends Fragment {

    private GoogleApiClient mGoogleApiClient;
    private ArrayList<Bitmap> photosDataList_img;
    private DetailFunction DF;
    private RecyclerView PhotosView;
    private TextView NoResult;
    private MyTabPhotosRecyclerViewAdapter Photo_Adapter;
    private  Context context;
    Integer num = 0;

    public TabPhoto() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DF = PlaceDetail.DF;
        photosDataList_img = new ArrayList<>();
        mGoogleApiClient = new GoogleApiClient
                .Builder(this.getContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this.getActivity(), null)
                .build();
        placePhotosAsync(DF.place_id);
        /*OptionalPendingResult<PlacePhotoMetadataResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly. We can try and retrieve an
            // authentication code.

            GoogleSignInResult result = opr.get();
            handleSignInResult(result);}*/

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_photo, container, false);
        Context context = view.getContext();
        PhotosView = view.findViewById(R.id.photolist);
        NoResult =  view.findViewById(R.id.NoResults);
        context = this.getContext();
        PhotosView.setLayoutManager(new LinearLayoutManager(context));
        Photo_Adapter = new MyTabPhotosRecyclerViewAdapter(this.getContext(), photosDataList_img);
        PhotosView.setAdapter(Photo_Adapter);

        if (num==0){
            PhotosView.setVisibility(View.GONE);
            NoResult.setVisibility(View.VISIBLE);
        }
        else{
            PhotosView.setVisibility(View.VISIBLE);
            NoResult.setVisibility(View.GONE);
        }

        /*if (num==0){
            PhotosView.setVisibility(View.GONE);
            NoResult.setVisibility(View.VISIBLE);
        }
        else{
            PhotosView.setVisibility(View.VISIBLE);
            NoResult.setVisibility(View.GONE);
            PhotosView.setLayoutManager(new LinearLayoutManager(context));
            Photo_Adapter = new MyTabPhotosRecyclerViewAdapter(this.getContext(), photosDataList_img);
            PhotosView.setAdapter(Photo_Adapter);
        }*/
        return view;
    }

    public static interface OnCompleteListener {
        public abstract void onComplete();
    }


    private OnCompleteListener mListener;

    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mListener = (OnCompleteListener)context;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnCompleteListener");
        }


    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Load a bitmap from the photos API asynchronously
     * by using buffers and result callbacks.
     */
    private void placePhotosAsync(final String placeId) {
        Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {
                    @Override
                    public void onResult(PlacePhotoMetadataResult photos) {
                        if (!photos.getStatus().isSuccess()) {
                            return;
                        }
                        Integer w = getView().getWidth();

                        PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                        if (photoMetadataBuffer.getCount() > 0) {
                            num = photoMetadataBuffer.getCount();
                            for (Integer curi=0; curi<num; ++curi)
                                photoMetadataBuffer.get(curi)
                                    .getScaledPhoto(mGoogleApiClient, w,
                                            photoMetadataBuffer.get(curi).getMaxHeight()*w/photoMetadataBuffer.get(curi).getMaxWidth())
                                    .setResultCallback(mDisplayPhotoResultCallback);
                        }
                        else mListener.onComplete();
                        photoMetadataBuffer.release();
                    }
                });
    }
    private ResultCallback<PlacePhotoResult> mDisplayPhotoResultCallback
            = new ResultCallback<PlacePhotoResult>() {
        @Override
        public void onResult(PlacePhotoResult placePhotoResult) {
            if (!placePhotoResult.getStatus().isSuccess()) {
                return;
            }
            photosDataList_img.add(placePhotoResult.getBitmap());
            if (photosDataList_img.size()==num){
                PhotosView.setVisibility(View.VISIBLE);
                NoResult.findViewById(R.id.NoResults).setVisibility(View.GONE);
                //Photo_Adapter.notifyDataSetChanged();
                System.out.println("num="+num);
                if (num==0){
                    PhotosView.setVisibility(View.GONE);
                    NoResult.setVisibility(View.VISIBLE);
                }
                else{
                    PhotosView.setVisibility(View.VISIBLE);
                    NoResult.setVisibility(View.GONE);
                    Photo_Adapter.notifyDataSetChanged();
                }
                mListener.onComplete();
            }
        }
    };
}
