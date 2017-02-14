package edu.rose_hulman.bradylz.saveyourdata.BrowsePackage;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import edu.rose_hulman.bradylz.saveyourdata.Constants;
import edu.rose_hulman.bradylz.saveyourdata.File;
import edu.rose_hulman.bradylz.saveyourdata.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnBrowseFileInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BrowseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BrowseFragment extends Fragment implements SearchView.OnQueryTextListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnBrowseFileInteractionListener mListener;
    private String mUid;
    private BrowseAdapter mAdapter;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private SearchView mSearchView;
    private DatabaseReference mFileRef;

    public BrowseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BrowseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BrowseFragment newInstance(String param1, String param2) {
        BrowseFragment fragment = new BrowseFragment();
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
        View view = inflater.inflate(R.layout.fragment_browse, container, false);

        mSearchView = (SearchView) view.findViewById(R.id.browse_search_bar);
        mSearchView.setOnQueryTextListener(this);
        Log.d(Constants.BT_TAG, "Test log");

        mRecyclerView = (RecyclerView)view.findViewById(R.id.browse_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);
        mFileRef = FirebaseDatabase.getInstance().getReference().child("file");
        mAdapter = new BrowseAdapter(getContext(), new OnBrowseFileInteractionListener() {
            @Override
            public void onBrowseFileInteraction(final File file) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.download_prompt_title);
                builder.setNegativeButton(android.R.string.cancel, null);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAdapter.addOwner(file);
                        Toast.makeText(getContext(), "File Downloaded", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.create().show();
            }
        }, mFileRef.orderByChild("owners"));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.scrollToPosition(0);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(File file) {
        if (mListener != null) {
            mListener.onBrowseFileInteraction(file);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBrowseFileInteractionListener) {
            mListener = (OnBrowseFileInteractionListener) context;
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

    public void setContext(Context context) {
        mContext = context;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d(Constants.BT_TAG, "In onQueryTextChange");

        Query temp;

        if(newText.isEmpty()) {
            temp = mFileRef.orderByChild("name");
        } else {
            temp = mFileRef.orderByChild("name").endAt(newText);
        }

        mAdapter = new BrowseAdapter(getContext(), new OnBrowseFileInteractionListener() {
            @Override
            public void onBrowseFileInteraction(final File file) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.download_prompt_title);
                builder.setNegativeButton(android.R.string.cancel, null);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAdapter.addOwner(file);
                        Toast.makeText(getContext(), "File Downloaded", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.create().show();
            }
        }, temp);
        Log.d(Constants.BT_TAG, "New Text: " +  newText);
        mRecyclerView.setAdapter(mAdapter);
        return true;
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
    public interface OnBrowseFileInteractionListener {
        void onBrowseFileInteraction(File file);
    }
}
