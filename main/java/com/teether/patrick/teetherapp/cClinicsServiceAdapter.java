package com.teether.patrick.teetherapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Patrick on 2/12/2018.
 */

public class cClinicsServiceAdapter extends RecyclerView.Adapter<cClinicsServiceAdapter.ViewHolder> {

    private List<cClinicsService> cClinicsService;
    private Context context;

    public cClinicsServiceAdapter(List<com.teether.patrick.teetherapp.cClinicsService> cClinicsService, Context context) {
        this.cClinicsService = cClinicsService;
        this.context = context;
    }

    @Override
    public cClinicsServiceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_clinic_services, parent, false);
        return new cClinicsServiceAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(cClinicsServiceAdapter.ViewHolder holder, int position)
    {
        final cClinicsService clinics = cClinicsService.get(position);
        holder.serviceName.setText(clinics.getServiceName());
        holder.servicePrice.setText(clinics.getServicePrice());
    }

    @Override
    public int getItemCount()
    {
        return cClinicsService.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView serviceName;
        public TextView servicePrice;
        public LinearLayout linearLayoutListService;

        public ViewHolder(View itemView)
        {
            super(itemView);

            serviceName = (TextView) itemView.findViewById(R.id.textViewServiceName);
            servicePrice = (TextView) itemView.findViewById(R.id.textPrice);
            linearLayoutListService = (LinearLayout) itemView.findViewById(R.id.linearLayoutService);
        }
    }

}
