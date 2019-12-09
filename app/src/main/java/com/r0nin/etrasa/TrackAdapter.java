package com.r0nin.etrasa;


import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TrackAdapter extends RecyclerView.Adapter{
    private ArrayList<Track> mDataset;
    private ArrayList<String> mKeys;

    private MainActivity mActivity;
    protected final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("tracks");

    private String TAG = "TrackAdapter";
    private class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public TextView mDescription;
        public TextView mCreatedBy, mRate;
        public Button buttonPlay;
        public Button buttonChangeTrack;
        public Button buttonRate;
        public Button buttonDeleteTrack;

        protected DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        public MyViewHolder(View pItem) {
            super(pItem);
            mTitle = pItem.findViewById(R.id.trackTitle);
            mDescription =  pItem.findViewById(R.id.trackDescription);
            mCreatedBy =  pItem.findViewById(R.id.trackCreatedBy);
            mRate =  pItem.findViewById(R.id.trackRating);
            buttonPlay = pItem.findViewById(R.id.buttonPlay);
            buttonChangeTrack = pItem.findViewById(R.id.buttonChangeTrack);
            buttonRate = pItem.findViewById(R.id.buttonRating);
            buttonDeleteTrack = pItem.findViewById(R.id.buttonDeleteTrack);
        }
    }

    public TrackAdapter(ArrayList<String>k,ArrayList<Track> tracks, MainActivity mainActivity){
        mDataset = tracks;
        mKeys = k;
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final Track track =  mDataset.get(position);
        if(track.getUserId() != null) {
            ((MyViewHolder) holder).mTitle.setText("Title: " +track.getTitle());
            ((MyViewHolder) holder).mDescription.setText("Description: "+track.getDescription());
            ((MyViewHolder) holder).mCreatedBy.setText("Created by: "+track.getDisplayName());
            Map<String,Float> usersWhichHaveRated = track.getUsersWhichHaveRated();
            float rating = 0;
            int size = 1;
            if(usersWhichHaveRated != null) {
                for (Map.Entry<String, Float> entry : usersWhichHaveRated.entrySet()) {
                    rating += entry.getValue();
                }
                size = usersWhichHaveRated.size() - 1;
                if(size == 0)
                    size = 1;
            }
            else{
                rating = 0;
                size = 1;
            }
            ((MyViewHolder) holder).mRate.setText("Avg rating: "+rating/size);
            ((MyViewHolder) holder).buttonPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map<String, Point> points = track.getPoints();
                    ArrayList<String> lat = new ArrayList<>();
                    ArrayList<String> lng = new ArrayList<>();
                    ArrayList<String> radius = new ArrayList<>();
                    ArrayList<Integer> numer = new ArrayList<>();
                    ArrayList<String> title = new ArrayList<>();
                    ArrayList<String> description = new ArrayList<>();
                    String trackDescription = track.getDescription();
                    String trackTitle = track.getTitle();
                    String keyTrack = track.getKeyTrack();
                    for (Map.Entry<String, Point> entry : points.entrySet()) {
                        Point p = entry.getValue();
                        lat.add(""+p.getLat());
                        lng.add(""+p.getLng());
                        radius.add(""+p.getRadius());
                        numer.add(p.getNumer());
                        title.add(p.getTitle());
                        description.add(p.getDescription());
                    }
                    Context context = v.getContext();
                    Intent intent = new Intent(context,PlayTrack.class);
                    if(!numer.isEmpty())
                        intent.putIntegerArrayListExtra("numer",numer);
                    if(!title.isEmpty())
                        intent.putStringArrayListExtra("title",title);
                    if(!description.isEmpty())
                        intent.putStringArrayListExtra("description",description);
                    if(!radius.isEmpty())
                        intent.putStringArrayListExtra("radius",radius);
                    if(!lat.isEmpty())
                        intent.putStringArrayListExtra("lat",lat);
                    if(!lng.isEmpty())
                        intent.putStringArrayListExtra("lng",lng);
                    if(!TextUtils.isEmpty(trackDescription))
                        intent.putExtra("trackDescription",trackDescription);
                    if(!TextUtils.isEmpty(trackTitle))
                        intent.putExtra("trackTitle",trackTitle);
                    if(!TextUtils.isEmpty(trackTitle))
                        intent.putExtra("keyTrack",keyTrack);
                    Log.i(TAG,"All set");
                    context.startActivity(intent);
                }
            });
            if(track.getUserId().equals(firebaseUser.getUid())){
                ((MyViewHolder) holder).buttonChangeTrack.setVisibility(View.VISIBLE);
                ((MyViewHolder) holder).buttonChangeTrack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String, Point> points = track.getPoints();
                        ArrayList<String> lat = new ArrayList<>();
                        ArrayList<String> lng = new ArrayList<>();
                        ArrayList<String> radius = new ArrayList<>();
                        ArrayList<Integer> numer = new ArrayList<>();
                        ArrayList<String> title = new ArrayList<>();
                        ArrayList<String> description = new ArrayList<>();
                        String trackDescription = track.getDescription();
                        String trackTitle = track.getTitle();
                        String keyTrack = track.getKeyTrack();
                        for (Map.Entry<String, Point> entry : points.entrySet()) {
                            Point p = entry.getValue();
                            lat.add(""+p.getLat());
                            lng.add(""+p.getLng());
                            radius.add(""+p.getRadius());
                            numer.add(p.getNumer());
                            title.add(p.getTitle());
                            description.add(p.getDescription());
                        }
                        Context context = v.getContext();
                        Intent intent = new Intent(context,TrackActivity.class);
                        if(!numer.isEmpty())
                            intent.putIntegerArrayListExtra("numer",numer);
                        if(!title.isEmpty())
                            intent.putStringArrayListExtra("title",title);
                        if(!description.isEmpty())
                            intent.putStringArrayListExtra("description",description);
                        if(!radius.isEmpty())
                            intent.putStringArrayListExtra("radius",radius);
                        if(!lat.isEmpty())
                            intent.putStringArrayListExtra("lat",lat);
                        if(!lng.isEmpty())
                            intent.putStringArrayListExtra("lng",lng);
                        if(!TextUtils.isEmpty(trackDescription))
                            intent.putExtra("trackDescription",trackDescription);
                        if(!TextUtils.isEmpty(trackTitle))
                            intent.putExtra("trackTitle",trackTitle);
                        if(!TextUtils.isEmpty(trackTitle))
                            intent.putExtra("keyTrack",keyTrack);
                        Log.i(TAG,"All set");
                        context.startActivity(intent);

                    }
                });
            }
            if(track.getUserId().equals(firebaseUser.getUid())) {
                ((MyViewHolder) holder).buttonDeleteTrack.setVisibility(View.VISIBLE);
                ((MyViewHolder) holder).buttonDeleteTrack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            myRef.child(track.getKeyTrack()).removeValue();
                            deleteTrack(position);
                            Log.d(TAG, "Remove value.");
                        }catch (Exception ex){
                            ex.printStackTrace();
                            Log.d(TAG, "Failed Remove value.");
                        }
                    }
                });
            }
                if(!track.getUserId().equals(firebaseUser.getUid())){
                ((MyViewHolder) holder).buttonRate.setVisibility(View.VISIBLE);
                final float sumOfRates = track.getSumOfRates();
                final float howMuchPeople = track.getHowMuchPeople();
                final String trackId = track.getKeyTrack();
                ((MyViewHolder) holder).buttonRate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Context context = view.getContext();
                        Intent intent = new Intent(context,Rating.class);
                        intent.putExtra("trackId",trackId);
                        intent.putExtra("sumOfRates",sumOfRates);
                        intent.putExtra("howMuchPeople",howMuchPeople);
                        context.startActivity(intent);
                    }
                });
            }
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

    public void deleteTrack(int position) {
        ArrayList<Track> tempTracks = new ArrayList<>();
        mDataset.remove(position);
        mKeys.remove(position);
        tempTracks.addAll(mDataset);
        mDataset.clear();
        notifyItemRangeChanged(position, mDataset.size());
        notifyItemRemoved(position);
        notifyDataSetChanged();
        Toast.makeText(mActivity,mActivity.getText(R.string.track_deleted),Toast.LENGTH_SHORT).show();
    }

}
