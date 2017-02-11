package edu.rose_hulman.bradylz.saveyourdata;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ScrollingView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.FileInputStream;

import edu.rose_hulman.bradylz.saveyourdata.ImageTasks.GetImageTask;
import edu.rose_hulman.bradylz.saveyourdata.ImageTasks.LoadImage;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFileDetailSelectedInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_FILE = "file";
    private File mFile;

    private OnFileDetailSelectedInteractionListener mListener;

    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param file Parameter 1.
     * @return A new instance of fragment DetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailFragment newInstance(File file) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_FILE, file);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFile = getArguments().getParcelable(ARG_FILE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        int fileType = mFile.getType();

        TextView fileName = (TextView) view.findViewById(R.id.detail_file_name);
        fileName.setText(mFile.getName());

        // Create a storage reference from our app
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://save-your-data-csse483.appspot.com");

        //Based off file type set certain view to be visible
        switch(fileType) {
            case 0:
                final ImageView image = (ImageView) view.findViewById(R.id.detail_photo_display);
                image.setVisibility(View.VISIBLE);
                storageRef.child("images").child(mFile.getName()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        // Use the bytes to display the image
                        Log.d(Constants.TAG, "Success");
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        image.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        Log.d(Constants.TAG, "Failure");
                    }
                });
                break;
            case 1:
                final VideoView video = (VideoView) view.findViewById(R.id.detail_video_display);
                video.setVisibility(View.VISIBLE);
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //Toast.makeText(Chat.this, uri.toString(), Toast.LENGTH_SHORT).show();
                        // MediaController mc = new MediaController()
                        // video.setMediaController(mc);
                        video.setVideoURI(uri);
                        video.requestFocus();
                        //videoView.start();
                    }
                });
                break;
            case 2:
                //Set the scrollview to be visible
                ScrollView scroll = (ScrollView) view.findViewById(R.id.detail_scroll_view);
                scroll.setVisibility(View.VISIBLE);

                //Capture the actual text within the scroll view
                final TextView text = (TextView) view.findViewById(R.id.detail_text_view);

                storageRef.child("texts").child(mFile.getName()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        // Use the bytes to display the image
                        Log.d(Constants.TAG, "Success");
                        text.setText(new String(bytes));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        Log.d(Constants.TAG, "Failure");
                    }
                });
                break;
        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(File file) {
        if (mListener != null) {
            mListener.onFileDetailInteraction(file);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFileDetailSelectedInteractionListener) {
            mListener = (OnFileDetailSelectedInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFileDetailSelectedInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

//    @Override
//    public void onPicLoaded(File file) {
//        mFile = file;
//
//        new GetImageTask(this).execute(file.getUrl());
//    }
//
//    @Override
//    public void onImageLoaded(Bitmap bitmap) {
//        mPic.setBitmap(bitmap);
//        mImageView.setImageBitmap(bitmap);
//
//        mAttacher.update();
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFileDetailSelectedInteractionListener {
        // TODO: Update argument type and name
        void onFileDetailInteraction(File file);
    }
}
