package com.teether.patrick.teetherapp;

import android.content.Context;
import android.content.Intent;
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


public class cUserAppointmentsAdapter extends RecyclerView.Adapter<cUserAppointmentsAdapter.ViewHolder>
{
    private List<cUserAppointments> cUserAppointments;
    private Context context;

    public cUserAppointmentsAdapter(List<com.teether.patrick.teetherapp.cUserAppointments> cUserAppointments, Context context) {
        this.cUserAppointments = cUserAppointments;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_user_appointments, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        final cUserAppointments userAppointments = cUserAppointments.get(position);
        holder.textViewAppointmentId.setText(String.valueOf(userAppointments.getAppointment_id()));
        holder.textViewClinicName.setText(userAppointments.getClinic_name());
        holder.textViewClinicAddress.setText(userAppointments.getClinic_address());
        holder.textViewAppointmentDayAndTime.setText(userAppointments.getClinic_date());
        holder.textViewStatus.setText(userAppointments.getClinic_status());

        if(!userAppointments.getClinic_status().equals("Rejected") && !userAppointments.getClinic_status().equals("Cancelled")){
            holder.linearLayoutUserAppointment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, UserAppointment.class);
                    intent.putExtra("appointment_id", userAppointments.getAppointment_id());
                    intent.putExtra("clinic_date", userAppointments.getClinic_date());
                    intent.putExtra("clinic_name", userAppointments.getClinic_name());
                    intent.putExtra("clinic_address", userAppointments.getClinic_address());
                    intent.putExtra("status", userAppointments.getClinic_status());
                    context.startActivity(intent);
                }
            });
        }


    }

    @Override
    public int getItemCount()
    {
        return cUserAppointments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textViewAppointmentId;
        public TextView textViewClinicName;
        public TextView textViewClinicAddress;
        public TextView textViewAppointmentDayAndTime;
        public TextView textViewStatus;
        public LinearLayout linearLayoutUserAppointment;

        public ViewHolder(View itemView)
        {
            super(itemView);

            textViewAppointmentId = (TextView) itemView.findViewById(R.id.textViewAppointmentId);
            textViewClinicName = (TextView) itemView.findViewById(R.id.textViewClinicName);
            textViewClinicAddress = (TextView) itemView.findViewById(R.id.textViewClinicAddress);
            textViewAppointmentDayAndTime = (TextView) itemView.findViewById(R.id.textViewAppointmentDayAndTime);
            textViewStatus = (TextView) itemView.findViewById(R.id.textViewStatus);
            linearLayoutUserAppointment = (LinearLayout) itemView.findViewById(R.id.linearLayoutUserAppointment);
        }
    }
}
