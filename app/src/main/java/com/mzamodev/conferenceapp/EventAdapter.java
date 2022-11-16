package com.mzamodev.conferenceapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.zip.Inflater;

public class EventAdapter extends RecyclerView.Adapter<EventViewHolder> {

    private final EventsInterface eventsInterface;

    private Context context;
    private List<Events> list;

    public EventAdapter(Context context, List<Events> list,
                        EventsInterface eventsInterface) {
        this.context = context;
        this.list = list;
        this.eventsInterface = eventsInterface;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       LayoutInflater inflater =  LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.event_view_items,parent,false);
        //return new EventViewHolder(LayoutInflater.from(context).inflate(R.layout.event_view_items,parent,false));
        return new EventViewHolder(view,eventsInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        String newTime = list.get(position).getEventDate() +" "+list.get(position).getEventTime();
        holder.eventName.setText(list.get(position).getEventName());
        holder.eventDescription.setText(list.get(position).getEventDescription());
        holder.eventPrice.setText(list.get(position).getPrice());
        holder.eventTime.setText(newTime);
        holder.eventLocation.setText(list.get(position).getLocation());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
