package com.mzamodev.conferenceapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EventViewHolder extends RecyclerView.ViewHolder {
    public TextView eventName,eventLocation,eventTime,eventDescription,eventPrice;
    public EventViewHolder(@NonNull View itemView, EventsInterface eventsInterface) {
        super(itemView);

        //initialize values
        eventName = (TextView) itemView.findViewById(R.id.eventName);
        eventLocation = (TextView) itemView.findViewById(R.id.location);
        eventDescription = (TextView) itemView.findViewById(R.id.eventDescription);
        eventTime = (TextView) itemView.findViewById(R.id.eventTime);
        eventPrice = (TextView) itemView.findViewById(R.id.eventPrice);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(eventsInterface != null){
                    int pos = getAdapterPosition();
                    eventsInterface.onItemClick(pos);
                }
            }
        });

    }
}
