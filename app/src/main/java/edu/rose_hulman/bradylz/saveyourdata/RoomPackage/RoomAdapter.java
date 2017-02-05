package edu.rose_hulman.bradylz.saveyourdata.RoomPackage;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import edu.rose_hulman.bradylz.saveyourdata.Constants;
import edu.rose_hulman.bradylz.saveyourdata.File;
import edu.rose_hulman.bradylz.saveyourdata.R;

/**
 * Created by bradylz on 2/5/2017.
 */

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {

    private final RoomFragment.OnRoomFileInteractionListener mListener;
    private ArrayList<File> mFiles;
    private Context mContext;
    private DatabaseReference mFileRef;
    private DatabaseReference mOwnerRef;
    private Query mQuery;
    private String mUid;

    public RoomAdapter(Context context, RoomFragment.OnRoomFileInteractionListener listener) {
        mFiles = new ArrayList<>();
        mContext = context;
        mListener = listener;

        //Initializing firebase references
        //TODO: Change to deal with the uids retrieved from bluetooth
        mFileRef = FirebaseDatabase.getInstance().getReference().child("file");
        mOwnerRef = FirebaseDatabase.getInstance().getReference().child("owner/" + mUid);
        mQuery = mFileRef.orderByChild("owners/owner1").equalTo(true);
        mQuery.addChildEventListener(new RoomFileEventListener());
    }

    public void setUid(String uid) {
        mUid = uid;
        Log.d(Constants.TAG, "Room adapter: " + mUid);
    }

    public class RoomFileEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            File file = dataSnapshot.getValue(File.class);
            file.setKey(dataSnapshot.getKey());
            mFiles.add(0, file);
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            File file = dataSnapshot.getValue(File.class);
            String keyToFind = dataSnapshot.getKey();
            file.setKey(keyToFind);

            for(int i = 0; i < mFiles.size(); i++) {
                if(mFiles.get(i).getKey().equals(keyToFind)) {
                    mFiles.set(i, file);
                    notifyDataSetChanged();
                    return;
                }
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String keyToRemove = dataSnapshot.getKey();

            for(int i = 0; i < mFiles.size(); i++) {
                if(mFiles.get(i).getKey().equals(keyToRemove)) {
                    mFiles.remove(i);
                    notifyDataSetChanged();
                    return;
                }
            }
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(this.mContext).inflate(R.layout.room_row_view, parent, false);
        return new RoomAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final File file = mFiles.get(position);
        holder.nameTextView.setText(file.getName());
        holder.descriptionTextView.setText(file.getDescription());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRoomFileInteraction(file);
            }
        });
    }

    public void addOwner(File file) {
        mFileRef.child(file.getKey()).child("owners/" + mUid).setValue(true);
        mOwnerRef.child("files/" + file.getKey()).setValue(true);
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView;
        private TextView descriptionTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.room_file_title);
            descriptionTextView = (TextView) itemView.findViewById(R.id.room_file_description);
        }
    }
}
