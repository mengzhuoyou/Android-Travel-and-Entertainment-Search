package com.example.janvierzz.hw9;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.media.Rating;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class MyTabReviewsRecyclerViewAdapter extends RecyclerView.Adapter<MyTabReviewsRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<DetailFunction.Review>  mValues;
    private Context context;

    public MyTabReviewsRecyclerViewAdapter(Context context, ArrayList<DetailFunction.Review> items) {
        mValues = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_tabreviews, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        if (mValues.get(position).profile_photo_url.length()>0){
            Picasso.get().load(mValues.get(position).profile_photo_url).into(holder.mImg);
        }
        holder.mViewName.setText(mValues.get(position).author_name);
        holder.mViewTime.setText(mValues.get(position).time_text);
        if (mValues.get(position).text.length()>0)
            holder.mViewText.setText(mValues.get(position).text);
        else
            holder.mViewText.setText(mValues.get(position).text);
        holder.mViewRating.setRating(mValues.get(position).rating);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(mValues.get(position).author_url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mViewName;
        public final TextView mViewTime;
        public final TextView mViewText;
        public final RatingBar mViewRating;
        public final ImageView mImg;
        public DetailFunction.Review mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImg = view.findViewById(R.id.photo);
            mViewName =  view.findViewById(R.id.item_name);
            mViewTime =  view.findViewById(R.id.item_time);
            mViewText =  view.findViewById(R.id.item_text);
            mViewRating =  view.findViewById(R.id.Rating_c);
        }
    }
}
