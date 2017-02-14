package edu.rose_hulman.bradylz.saveyourdata;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.rose_hulman.bradylz.saveyourdata.HomePackage.HomeCloudTabFragment;
import edu.rose_hulman.bradylz.saveyourdata.HomePackage.HomeFavoritesTabFragment;

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

    //References to Firebase storage
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private StorageReference mImagesRef;
    private StorageReference mVideosRef;
    private StorageReference mTextRef;

    //Query to sort viewable files in Firebase
    private Query mQuery;

    //Uid
    private String mUid;

    //Boolean to keep track of what tab we're in (i.e. Favorites or Cloud)
    private boolean mInFavs;

    //Listeners for on files selected
    private HomeFavoritesTabFragment.OnHomeFavoritesFileSelectedInteractionListener mFListener;
    private HomeCloudTabFragment.OnHomeCloudFileInteractionSelectedListener mCListener;

    public FileAdapter(Context context, boolean inFavs,
                       HomeFavoritesTabFragment.OnHomeFavoritesFileSelectedInteractionListener mfl,
                       HomeCloudTabFragment.OnHomeCloudFileInteractionSelectedListener mcl) {
        mInFavs = inFavs;
        mContext = context;
        mFiles = new ArrayList<>();

        SharedPreferences prefs = context.getSharedPreferences(NavActivity.PREFS, MODE_PRIVATE);
        mUid = prefs.getString(NavActivity.KEY_UID, ""); //Blank default means there is no uid

        //Linking to firebase
        mFileRef = FirebaseDatabase.getInstance().getReference().child("file");
        mOwnerRef = FirebaseDatabase.getInstance().getReference().child("owner").child(mUid);

        //Initializing the query to get our own files showing only
        mFListener = mfl;
        mCListener = mcl;

        //Changing the query based off what tab we're in, add listener either way
        if (inFavs) {
            mQuery = mFileRef.orderByChild("favoritedBy/" + mUid).equalTo(true);
        } else {
            mQuery = mFileRef.orderByChild("owners/" + mUid).equalTo(true);
        }
        mQuery.addChildEventListener(new FileEventListener());

        // Create a storage reference from our app
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReferenceFromUrl("gs://save-your-data-csse483.appspot.com");

        // Create a child reference to folders within storage
        mImagesRef = mStorageRef.child("images");
        mVideosRef = mStorageRef.child("videos");
        mTextRef = mStorageRef.child("texts");
    }

    public void setRecyclerView(RecyclerView rview) {
        mRecyclerView = rview;
    }

    //Child event listener for the firebase reference query
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

            for (int i = 0; i < mFiles.size(); i++) {
                if (mFiles.get(i).getKey().equals(keyToFind)) {
                    mFiles.set(i, file);
                    notifyDataSetChanged();
                    return;
                }
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String keyToRemove = dataSnapshot.getKey();

            for (int i = 0; i < mFiles.size(); i++) {
                if (mFiles.get(i).getKey().equals(keyToRemove)) {
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
        mFileRef.push().setValue(file);
        mFileRef.keepSynced(true);
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
                if (mInFavs) {
                    mFListener.onHomeFavoritesFileInteraction(file);
                } else {
                    mCListener.onHomeCloudFileInteraction(file);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //Checking to see if the file is favorited by adding a single value event listener
//                final boolean[] isFaved = new boolean[1];
//
//                Query favorited = mFileRef.child(file.getKey()).orderByChild("favoritedBy/" + mUid).equalTo(true);
//                favorited.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        Log.d(Constants.TAG, "data snapshot" + dataSnapshot.getKey());
//                        if(dataSnapshot.getValue() == null) {
//                            Log.d(Constants.TAG, "NULL Boolean is " + isFaved[0]);
//                            isFaved[0] = false;
//                        } else {
//                            Log.d(Constants.TAG, "Boolean is " + isFaved[0]);
//                            isFaved[0] = true;
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });

                //boolean isFaved = file.inFavorites(mUid);
                boolean isFaved = (mFileRef.child(file.getKey()).child(File.FILE_FAVORITEDBY).child(mUid).getRoot() != null);
                Log.d(Constants.TAG, "Boolean value: " + isFaved);

                if (mInFavs) {
                    mFListener.onLongFavoritesFileInteraction(file, isFaved);
                } else {
                    mCListener.onLongCloudFileInteraction(file, isFaved);
                }
                mFileRef.keepSynced(true);
                mOwnerRef.keepSynced(true);
                return true;
            }
        });
    }

    //Pushes a video file to Firebase
    public void firebasePushVideo(String fileName, String fileDescription, int fileType, Uri video) {
        // Create a new auto-ID for a course in the courses path
        DatabaseReference ref = mFileRef.push();

        // Add the file to the files path
        ref.setValue(new File(fileName, fileDescription, mUid, fileType));
        // Add the current user to the owners attribute
        ref.child(File.FILE_OWNERS).child(mUid).setValue(true);
        // Add the current user to the favoritedBy attribute, but set to false
        // ref.child(File.FILE_FAVORITEDBY).child(mUid).setValue(false);

        //Add the file to storage
        StorageReference videoRef = mVideosRef.child(fileName);
        videoRef.putFile(video);

        ref.child("path").setValue(videoRef.getPath());

        // Add the file to the owners path
        Map<String, Object> map = new HashMap<>();
        map.put(ref.getKey(), true);
        mOwnerRef.child(Owner.FILES).updateChildren(map);
    }

    //Pushes a text file to Firebase
    public void firebasePushText(String fileName, String fileDescription, int fileType, String textInput) {
        // Create a new auto-ID for a course in the courses path
        DatabaseReference ref = mFileRef.push();

        // Add the file to the files path
        ref.setValue(new File(fileName, fileDescription, mUid, fileType));
        // Add the current user to the owners attribute
        ref.child(File.FILE_OWNERS).child(mUid).setValue(true);
        // Add the current user to the favoritedBy attribute, but set to false
        // ref.child(File.FILE_FAVORITEDBY).child(mUid).setValue(false);

        //Add the file to storage
        StorageReference textRef = mTextRef.child(fileName);
        textRef.putBytes(textInput.getBytes());

        ref.child("path").setValue(textRef.getPath());

        // Add the file to the owners path
        Map<String, Object> map = new HashMap<>();
        map.put(ref.getKey(), true);
        mOwnerRef.child(Owner.FILES).updateChildren(map);
    }

    //Pushes an image file to Firebase
    public void firebasePushPhoto(String fileName, String fileDescription, int fileType, Uri image) {
        // Create a new auto-ID for a course in the courses path
        DatabaseReference ref = mFileRef.push();

        // Add the file to the files path
        ref.setValue(new File(fileName, fileDescription, mUid, fileType));
        // Add the current user to the owners attribute
        ref.child(File.FILE_OWNERS).child(mUid).setValue(true);
        // Add the current user to the favoritedBy attribute, but set to false
        // ref.child(File.FILE_FAVORITEDBY).child(mUid).setValue(false);

        //Add the file to storage
        StorageReference imageRef = mImagesRef.child(fileName);
        imageRef.putFile(image);

        ref.child("path").setValue(imageRef.getPath());

        // Add the file to the owners path
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
