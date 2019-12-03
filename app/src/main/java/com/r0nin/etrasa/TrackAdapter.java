package com.r0nin.etrasa;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TrackAdapter extends RecyclerView.Adapter{
    private ArrayList<Track> mDataset;
    private MainActivity mActivity;


    private class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public TextView mDescription;
        public Button buttonPlay;

        public MyViewHolder(View pItem) {
            super(pItem);
            mTitle = (TextView) pItem.findViewById(R.id.textViewTrackTitle);
            mDescription = (TextView) pItem.findViewById(R.id.textViewTrackDescription);
            buttonPlay = (Button) pItem.findViewById(R.id.buttonPlayTrack);
        }
    }

    public TrackAdapter(ArrayList<Track> tracks, MainActivity mainActivity){
        mDataset = tracks;
        mActivity = mainActivity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_view_element,parent,false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Track track =  mDataset.get(position);
        if(track.getUserId() != null) {
            ((MyViewHolder) holder).mTitle.setText(track.getTitle());
            ((MyViewHolder) holder).mDescription.setText("No points: "+track.getPoints());
            ((MyViewHolder) holder).buttonPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mActivity.getApplicationContext(),"This function is not yet programmed here",Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void addTrack(Track track) {
        mDataset.add(0, track);
        notifyDataSetChanged();
    }
}
