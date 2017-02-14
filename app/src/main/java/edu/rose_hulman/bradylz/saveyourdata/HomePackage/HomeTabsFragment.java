package edu.rose_hulman.bradylz.saveyourdata.HomePackage;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.rose_hulman.bradylz.saveyourdata.Constants;
import edu.rose_hulman.bradylz.saveyourdata.File;
import edu.rose_hulman.bradylz.saveyourdata.FileAdapter;
import edu.rose_hulman.bradylz.saveyourdata.R;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeTabsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeTabsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeTabsFragment extends Fragment implements HomeFavoritesTabFragment.OnHomeFavoritesFileSelectedInteractionListener,
        HomeGeneralTabFragment.OnHomeGeneralFileInteractionSelectedListener,
        HomeCloudTabFragment.OnHomeCloudFileInteractionSelectedListener,
        TabHost.OnTabChangeListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private FragmentTabHost mTabHost;
    private Context mContext;
    private FileAdapter mAdapter;
    private String mUid;

    //To identify photo(0) / video(1) / text file (2)
    private int optionsIndex;
    private boolean onCreateViewCalled = false;

    //The tabs of the tab host
    HomeFavoritesTabFragment mFavorites;
    HomeCloudTabFragment mCloud;

    public HomeTabsFragment() {
        // Required empty public constructor
    }

    public void setContext(Context context) {
        mContext = context;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeTabsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeTabsFragment newInstance(String param1, String param2) {
        HomeTabsFragment fragment = new HomeTabsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Creating the adapter to use for each fragment
        mAdapter = new FileAdapter(getContext(), true, null, null);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_tabs, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOptions();
            }
        });

        //Setting up the tabs
        mTabHost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);

        //Adding in the downloads tab
        mFavorites = new HomeFavoritesTabFragment();
        mFavorites.setAdapter(mAdapter);
        mTabHost.addTab(mTabHost.newTabSpec(HomeFavoritesTabFragment.FAV_TAG).setIndicator(getString(R.string.home_favorites_tab)), mFavorites.getClass(), null);

        //Adding in the cloud tab
        mCloud = new HomeCloudTabFragment();
        mCloud.setAdapter(mAdapter);
        mTabHost.addTab(mTabHost.newTabSpec(HomeCloudTabFragment.CLOUD_TAG).setIndicator(getString(R.string.home_cloud_tab)), mCloud.getClass(), null);

        return view;
    }

    public void setUid(String uid) {
        mUid = uid;
    }

    private void showOptions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Choose a file type:");
        optionsIndex = 0;

        final View view = getActivity().getLayoutInflater().inflate(R.layout.add_file_options, null, false);
        builder.setView(view);

        //Photo
        //Set button listeners for each option
        ImageButton photoButton = (ImageButton) view.findViewById(R.id.photoButton);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionsIndex = 0;
                //TODO: Hide the builder
                takeOrChooseImage();
            }
        });

        //Video
        //Set button listeners for each option
        ImageButton videoButton = (ImageButton) view.findViewById(R.id.videoButton);
        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionsIndex = 1;
                takeOrChooseVideo();
            }
        });

        //Text file
        //Set button listeners for each option
        ImageButton textButton = (ImageButton) view.findViewById(R.id.textButton);
        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionsIndex = 2;
                showTextFileInput(optionsIndex);
            }
        });

        builder.create().show();
    }

    private void takeOrChooseImage() {
        long captureTime = System.currentTimeMillis();
        final String photoPath = Environment.getExternalStorageDirectory() + "/" + R.string.app_name + captureTime + ".jpg";

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Take photo or choose existing:");

        builder.setPositiveButton("Take", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(takePicture.resolveActivity(getContext().getPackageManager()) != null) {
                    startActivityForResult(takePicture, 0);
                }
            }
        });

        builder.setNegativeButton("Choose", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 1);
            }
        });

        builder.create().show();
    }

    private void takeOrChooseVideo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Take video or choose existing:");

        builder.setPositiveButton("Take", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent takeVideo = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(takeVideo, 3);
            }
        });

        builder.setNegativeButton("Choose", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent pickVideo = new Intent(Intent.ACTION_PICK,
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickVideo, 4);
            }
        });

        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent fileReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, fileReturnedIntent);

        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Bundle extras = fileReturnedIntent.getExtras();
                    Bitmap bitmap = (Bitmap) extras.get("data");

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bitmap, "Title", null);
                    Uri uri = Uri.parse(path);

                    addPhotoDialog(null, optionsIndex, getActivity(), uri);
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = fileReturnedIntent.getData();
                    addPhotoDialog(null, optionsIndex, getActivity(), selectedImage);
                }
                break;
            case 3:
                if (resultCode == RESULT_OK) {
                    Uri selectedVideo = fileReturnedIntent.getData();
                    addVideoDialog(null, optionsIndex, getActivity(), selectedVideo);
                }
                break;
            case 4:
                if (resultCode == RESULT_OK) {
                    Uri selectedVideo = fileReturnedIntent.getData();
                    addVideoDialog(null, optionsIndex, getActivity(), selectedVideo);
                }
                break;
            default:
                Log.d(Constants.TAG, "Invalid file request code");
                break;
        }
    }


    private void showTextFileInput(final int optionsIndex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.input_text_title);

        View view = getActivity().getLayoutInflater().inflate(R.layout.text_input, null, false);
        builder.setView(view);

        final EditText textInput = (EditText) view.findViewById(R.id.text_input);
        final EditText titleEditText = (EditText) view.findViewById(R.id.text_file_title);
        final EditText descriptionEditText = (EditText) view.findViewById(R.id.text_file_description);

        builder.setNegativeButton(android.R.string.cancel, null);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Creating the new file to add
                String name = titleEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                String text = textInput.getText().toString();

                mAdapter.firebasePushText(name, description, optionsIndex, text);
            }
        });
        builder.create().show();
    }

    public void addPhotoDialog(final File file, final int optionsIndex, Context context, final Uri image) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(file == null ? R.string.dialog_add_title : R.string.dialog_update_title));
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_add, null, false);
        builder.setView(view);
        final EditText titleEditText = (EditText) view.findViewById(R.id.dialog_add_title);
        final EditText descriptionEditText = (EditText) view.findViewById(R.id.dialog_add_description);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (file == null) {
                    //Creating the new file to add
                    String name = titleEditText.getText().toString();
                    String description = descriptionEditText.getText().toString();
                    mAdapter.firebasePushPhoto(name, description, optionsIndex, image);
                }
            }
        });

        builder.setNegativeButton(android.R.string.cancel, null);

        builder.create().show();
    }

    public void addVideoDialog(final File file, final int optionsIndex, Context context, final Uri video) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(file == null ? R.string.dialog_add_title : R.string.dialog_update_title));
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_add, null, false);
        builder.setView(view);
        final EditText titleEditText = (EditText) view.findViewById(R.id.dialog_add_title);
        final EditText descriptionEditText = (EditText) view.findViewById(R.id.dialog_add_description);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (file == null) {
                    //Creating the new file to add
                    String name = titleEditText.getText().toString();
                    String description = descriptionEditText.getText().toString();
                    mAdapter.firebasePushVideo(name, description, optionsIndex, video);
                }
            }
        });

        builder.setNegativeButton(android.R.string.cancel, null);

        builder.create().show();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);

        mTabHost.setOnTabChangedListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRoomFileInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onHomeCloudFileInteraction(File file) {
        Log.d(Constants.TAG, "YESSS!!!");
    }

    @Override
    public void onLongCloudFileInteraction(File file, boolean fav) {

    }

    @Override
    public void onHomeFavoritesFileInteraction(File file) {
        Log.d(Constants.TAG, "DOUBLE YESSS!!!");
    }

    @Override
    public void onLongFavoritesFileInteraction(File file, boolean fav) {

    }

    @Override
    public void onHomeGeneralFileInteraction(File file) {

    }

    @Override
    public void onTabChanged(String tabId) {
        switch (tabId) {
            case HomeFavoritesTabFragment.FAV_TAG:
                switchFragment(mFavorites);
                break;
            case HomeCloudTabFragment.CLOUD_TAG:
                switchFragment(mCloud);
                break;
            default:
                Log.d(Constants.TAG, "Couldn't get tag");
                break;
        }

    }

    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(android.R.id.tabcontent, fragment)
                .commit();
        fragmentManager.executePendingTransactions();
    }

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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
