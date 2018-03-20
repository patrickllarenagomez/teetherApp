package com.teether.patrick.teetherapp;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Patrick on 2/4/2018.
 */

public class cClinicsAdapter extends RecyclerView.Adapter<cClinicsAdapter.ViewHolder>
{
    private List<cClinics> cClinics;
    private Context context;

    public cClinicsAdapter(List<com.teether.patrick.teetherapp.cClinics> cClinics, Context context) {
        this.cClinics = cClinics;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_clinic_items, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        final cClinics clinics = cClinics.get(position);
        holder.titleHeader.setText(clinics.getTitleHeader());
        holder.addressDescription.setText(clinics.getAddressDescription());
        holder.itemNo.setText(String.valueOf(clinics.getItemNo()));
        holder.linearLayoutList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ClinicDetails.class);
                intent.putExtra("clinic_id", clinics.getItemNo());
                intent.putExtra("clinic_name", clinics.getTitleHeader());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return cClinics.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView titleHeader;
        public TextView addressDescription;
        public TextView itemNo;
        public LinearLayout linearLayoutList;

        public ViewHolder(View itemView)
        {
            super(itemView);

            titleHeader = (TextView) itemView.findViewById(R.id.textViewHeader);
            addressDescription = (TextView) itemView.findViewById(R.id.textViewDescription);
            itemNo = (TextView) itemView.findViewById(R.id.textViewItemNo);
            linearLayoutList = (LinearLayout) itemView.findViewById(R.id.linearLayoutList);
        }
    }
}
