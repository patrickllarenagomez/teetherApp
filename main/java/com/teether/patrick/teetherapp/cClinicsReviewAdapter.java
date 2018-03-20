package com.teether.patrick.teetherapp;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.media.Rating;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Patrick on 2/4/2018.
 */

public class cClinicsReviewAdapter extends RecyclerView.Adapter<cClinicsReviewAdapter.ViewHolder>
{
    private List<cClinicsReview> cClinicsReview;
    private Context context;

    public cClinicsReviewAdapter(List<com.teether.patrick.teetherapp.cClinicsReview> cClinicsReview, Context context) {
        this.cClinicsReview = cClinicsReview;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_clinic_reviews, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        final cClinicsReview clinics = cClinicsReview.get(position);
        holder.fullName.setText(clinics.getFullName());
        holder.comment.setText(clinics.getComment());
        holder.rating.setRating(clinics.getRating());
    }

    @Override
    public int getItemCount()
    {
        return cClinicsReview.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView fullName;
        public TextView comment;
        public RatingBar rating;
        public LinearLayout linearLayoutListReview;

        public ViewHolder(View itemView)
        {
            super(itemView);

            fullName = (TextView) itemView.findViewById(R.id.textViewFullName);
            comment = (TextView) itemView.findViewById(R.id.textViewUserComment);
            rating = (RatingBar) itemView.findViewById(R.id.rating);
            linearLayoutListReview = (LinearLayout) itemView.findViewById(R.id.linearLayoutListReview);
        }
    }
}
