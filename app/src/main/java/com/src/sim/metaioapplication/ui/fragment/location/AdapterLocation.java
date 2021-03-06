package com.src.sim.metaioapplication.ui.fragment.location;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.src.sim.metaioapplication.logic.resource.Location;
import com.src.sim.metaioapplication.ui.activitiy.start.StartMenuActivity;
import com.src.sim.metaioapplication.listener.CustomListener;

import java.util.List;

public class AdapterLocation extends  RecyclerView.Adapter<ViewHolderLocation> {
    private final List<Location> locationList;
    private final int layout;
    private View inflatedView;
    private CustomListener customListener;


    public AdapterLocation(int layout, Activity activity){
        this.locationList = ((StartMenuActivity)activity).getDatabase().getLocationList();

        this.layout = layout;
        this.customListener = (CustomListener) activity;
    }

    @Override
    public ViewHolderLocation onCreateViewHolder(ViewGroup viewGroup, int position) {
        inflatedView = LayoutInflater.from(viewGroup.getContext()).inflate(layout, viewGroup, false);
        return new ViewHolderLocation(inflatedView);
    }

    @Override
    public void onBindViewHolder(ViewHolderLocation holder, int position) {
        holder.assignData(locationList.get(position));
        inflatedView.setOnClickListener(customListener.handleCardLocationClick(locationList.get(position)));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }
}
