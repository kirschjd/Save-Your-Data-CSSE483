package edu.rose_hulman.bradylz.saveyourdata;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.rose_hulman.bradylz.saveyourdata.HomePackage.HomeCloudTabFragment;
import edu.rose_hulman.bradylz.saveyourdata.HomePackage.HomeDownloadsTabFragment;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by kirschjd on 1/22/2017.
 */

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<File> mFiles;
    private RecyclerView mRecyclerView;

    //References to our firebase
    private DatabaseReference mFileRef;
    private DatabaseReference mOwnerRef;

    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private StorageReference mImagesRef;
    private Query mQuery;
    private String mUid;

    private HomeDownloadsTabFragment.OnHomeDownloadsFileSelectedInteractionListener mListener;
    //private HomeCloudTabFragment.OnHomeCloudFileInteractionSelectedListener mCListener;

    //TODO: create adapter within the hometabsfragment and override the on tab changes to set the adapter and the listener
    // TODO: Alterntaive is to create multiple adapters
    public void setListener(HomeDownloadsTabFragment.OnHomeDownloadsFileSelectedInteractionListener mDListener) {
        this.mListener = mDListener;
    }

    public FileAdapter(Context context) {//, RecyclerView recyclerView) {
        mContext = context;
        //mRecyclerView = recyclerView;
        mFiles = new ArrayList<>();
        //mListener = listener;

        SharedPreferences prefs = context.getSharedPreferences(NavActivity.PREFS, MODE_PRIVATE);
        mUid = prefs.getString(NavActivity.KEY_UID, ""); //TODO: Means there is no uid

        // Hard coded uid for testing purposes
        // mUid = "RgTWQA1UhVXQcdhUiE8gLFSXbXA2";

        //Linking to firebase
        mFileRef = FirebaseDatabase.getInstance().getReference().child("file");
        // mFileRef.addChildEventListener(new FileEventListener());

        //Initializing the query to get our own files showing only
        mQuery = mFileRef.orderByChild("owners/" + mUid).equalTo(true);
        mQuery.addChildEventListener(new FileEventListener());

        mOwnerRef = FirebaseDatabase.getInstance().getReference().child("owner").child(mUid);

        // Create a storage reference from our app
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReferenceFromUrl("gs://save-your-data-csse483.appspot.com");

        // Create a child reference
        // imagesRef now points to "images"
        mImagesRef = mStorageRef.child("images");
    }



    public void setRecyclerView (RecyclerView rview) {
        mRecyclerView = rview;
    }

    public String getPath(File file) {
//        return mImagesRef.child(file.getFilePath()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                // Got the download URL for 'users/me/profile.png'
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle any errors
//            }
//        });//file.getFilePath());
        mStorageRef.child("images/Yankees Image.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

            }
        });
        return file.getFilePath();
    }

    public class FileEventListener implements ChildEventListener {

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

    public void add(File file) {
        Log.d(Constants.TAG, "File being inserted : " + file.toString());
        mFileRef.push().setValue(file);
        mFileRef.keepSynced(true);
    }

    public void remove(File file) {
        mFileRef.child(file.getKey()).removeValue();
    }

    public void update(File file, String newCaption, String newURL) {
//        pic.setCaption(newCaption);
//        pic.setUrl(newURL);
//        mPicsRefs.child(pic.getKey()).setValue(pic);
//        mPicsRefs.child(pic.getKey()).keepSynced(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(this.mContext).inflate(R.layout.file_row_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final File file = mFiles.get(position);
        holder.nameTextView.setText(file.getName());
        holder.descriptionTextView.setText(file.getDescription());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.OnHomeDownloadsFileInteraction(file);
            }
        });
    }

    public void firebasePush(String fileName, String fileDescription, int fileType) {
        // Create a new auto-ID for a course in the courses path
        DatabaseReference ref = mFileRef.push();
        Log.d(Constants.TAG, "file ref push key: " + ref);
        Log.d(Constants.TAG, "mUid: " + mUid);

        // Add the course to the courses path
        ref.setValue(new File(fileName, fileDescription, mUid, fileType));

        // Add the course to the owners path
        Map<String, Object> map = new HashMap<>();
        map.put(ref.getKey(), true);
        mOwnerRef.child(Owner.FILES).updateChildren(map);
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
            nameTextView = (TextView) itemView.findViewById(R.id.home_file_title);
            descriptionTextView = (TextView) itemView.findViewById(R.id.home_file_description);
        }
    }
}