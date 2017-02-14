package edu.rose_hulman.bradylz.saveyourdata.HomePackage;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.rose_hulman.bradylz.saveyourdata.File;
import edu.rose_hulman.bradylz.saveyourdata.FileAdapter;
import edu.rose_hulman.bradylz.saveyourdata.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnHomeFavoritesFileSelectedInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFavoritesTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFavoritesTabFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String FAV_TAG = "Favorites";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FileAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private Context mContext;

    private OnHomeFavoritesFileSelectedInteractionListener mListener;

    public HomeFavoritesTabFragment() {
        // Required empty public constructor
    }

    public void setAdapter(FileAdapter fileAdapter) {
        mAdapter = fileAdapter;
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
    public static HomeFavoritesTabFragment newInstance(String param1, String param2) {
        HomeFavoritesTabFragment fragment = new HomeFavoritesTabFragment();
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
        mAdapter = new FileAdapter(getContext(), true, mListener, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_favorites_tab, container, false);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.home_favorites_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.scrollToPosition(0);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(File file) {
        if (mListener != null) {
            mListener.onHomeFavoritesFileInteraction(file);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnHomeFavoritesFileSelectedInteractionListener) {
            mListener = (OnHomeFavoritesFileSelectedInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnBrowseFileInteractionListener");
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
    public interface OnHomeFavoritesFileSelectedInteractionListener {
        // TODO: Update argument type and name
        void onHomeFavoritesFileInteraction(File file);
        void onLongFavoritesFileInteraction(File file, boolean fav);
    }

    public void setContext(Context context) {
        mContext = context;
    }
}