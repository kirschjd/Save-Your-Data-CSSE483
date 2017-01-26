package edu.rose_hulman.bradylz.saveyourdata;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by kirschjd on 1/22/2017.
 */

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<File> mFiles = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private DatabaseReference mUserRef;
    private DatabaseReference mFileRef;

    public FileAdapter(Context context, RecyclerView recyclerView){
        mContext = context;
        mRecyclerView = recyclerView;
        mUserRef = FirebaseDatabase.getInstance().getReference().child("users");
        mFileRef = mUserRef.child("files");
//        mMovieQoutesRef.addChildEventListener(new QuotesChildEventListener());
        for(int i = 0; i < 5; i++) {
            mFiles.add(new File("File " + i, "Description", android.R.drawable.btn_default));
        }
        mFiles.add(new File("File ", "Description", android.R.drawable.btn_default));
    }

    public void update(File file, String title, String description) {
        file.setTitle(title);
        file.setDescription(description);
        mFileRef.child(file.getKey()).setValue(file);
    }

    public void add(File file) {
        mFileRef.push().setValue(file);
    }

    class FilesChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            File file = dataSnapshot.getValue(File.class);
            file.setKey(dataSnapshot.getKey());
            mFiles.add(0, file);
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//            String key = dataSnapshot.getKey();
//            MovieQuote updatedMovieQuote = dataSnapshot.getValue(MovieQuote.class);
//            for (MovieQuote mq : mMovieQuotes){
//                if(mq.getKey().equals(key)){
//                    mq.setValues(updatedMovieQuote);
//                    notifyDataSetChanged();
//                    return;
//                }
//            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
//            String key = dataSnapshot.getKey();
//            for (MovieQuote mq : mMovieQuotes){
//                if(mq.getKey().equals(key)){
//                    mMovieQuotes.remove(mq);
//                    notifyDataSetChanged();
//                    return;
//                }
//            }
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(Constants.TAG, "Database error: " + databaseError);
        }
    }





    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(this.mContext).inflate(R.layout.file_row_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        File file = mFiles.get(position);
        holder.nameTextView.setText(file.getTitle());
        holder.pictureImageView.setImageResource(file.getImageID());
        holder.descriptionTextView.setText(file.getDescription());
//        holder.mPositionTextView.setText(String.format("I'm #%d", (position+1)));
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView;
        private TextView descriptionTextView;
        private ImageView pictureImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.file_title);
            descriptionTextView = (TextView) itemView.findViewById(R.id.file_description);
            pictureImageView = (ImageView) itemView.findViewById(R.id.file_thumbnail);
        }
    }
}