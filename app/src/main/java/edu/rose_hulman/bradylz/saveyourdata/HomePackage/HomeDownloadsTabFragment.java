package edu.rose_hulman.bradylz.saveyourdata.HomePackage;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.rose_hulman.bradylz.saveyourdata.Constants;
import edu.rose_hulman.bradylz.saveyourdata.File;
import edu.rose_hulman.bradylz.saveyourdata.FileAdapter;
import edu.rose_hulman.bradylz.saveyourdata.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeDownloadsTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeDownloadsTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeDownloadsTabFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mUid;
    private FileAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private OnFragmentInteractionListener mListener;

    public HomeDownloadsTabFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeCloudTabFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeDownloadsTabFragment newInstance(String param1, String param2) {
        HomeDownloadsTabFragment fragment = new HomeDownloadsTabFragment();
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

    public void setUid(String uid) {
        mUid = uid;
        Log.d(Constants.TAG, "Adapter: " + mAdapter);
        Log.d(Constants.TAG, "Downloads Uid: " + mUid);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_downloads_tab, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.home_downloads_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new FileAdapter(getContext(), mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.scrollToPosition(0);
        Log.d(Constants.TAG, "Right before Uid: " + mUid);

        //mAdapter.setUid(mUid);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void add(File file) {
        Log.d(Constants.TAG, "Adapter for adding: " + mAdapter);
        mAdapter.add(file);
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