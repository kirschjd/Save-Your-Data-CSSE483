package edu.rose_hulman.bradylz.saveyourdata.HomePackage;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import edu.rose_hulman.bradylz.saveyourdata.Constants;
import edu.rose_hulman.bradylz.saveyourdata.File;
import edu.rose_hulman.bradylz.saveyourdata.FileAdapter;
import edu.rose_hulman.bradylz.saveyourdata.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeTabsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeTabsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeTabsFragment extends Fragment implements HomeDownloadsTabFragment.OnFragmentInteractionListener,
                                                            HomeGeneralTabFragment.OnFragmentInteractionListener,
                                                            HomeCloudTabFragment.OnFragmentInteractionListener{
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_tabs, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Setting up the tabs
        mTabHost = (FragmentTabHost) view.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.realtabcontent);

        //Adding in the downloads tab
        mTabHost.addTab(mTabHost.newTabSpec("downloads").setIndicator(getString(R.string.home_downloads_tab)), HomeDownloadsTabFragment.class, null);

        //Adding in the cloud tab
        mTabHost.addTab(mTabHost.newTabSpec("cloud").setIndicator(getString(R.string.home_cloud_tab)), HomeCloudTabFragment.class, null);

        //Adding in the cloud tab
        mTabHost.addTab(mTabHost.newTabSpec("general").setIndicator(getString(R.string.home_general_tab)), HomeGeneralTabFragment.class, null);

        updateAdapter();

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        updateAdapter();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void updateAdapter() {
        String tag = mTabHost.getTag().toString();
        Fragment ft = getChildFragmentManager().findFragmentById(mTabHost.getCurrentTab());

        switch (tag){
            case "downloads":
//                HomeDownloadsTabFragment hdtf = (HomeDownloadsTabFragment)
//                        getChildFragmentManager().findFragmentById(mTabHost.getCurrentTab());
//                mAdapter = hdtf.getAdapter();
                break;
            case "cloud":

                break;
            case "general":
                HomeGeneralTabFragment hgtf = (HomeGeneralTabFragment)
                        getChildFragmentManager().findFragmentById(mTabHost.getCurrentTab());
                mAdapter = hgtf.getAdapter();
                break;
            default:
                Log.d(Constants.TAG, "couldn't get tag");
        }
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

    private void showAddEditDialog(final File file) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(getString(file == null ? R.string.dialog_add_title : R.string.dialog_update_title));
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_add, null, false);
        builder.setView(view);
        final EditText titleEditText = (EditText) view.findViewById(R.id.dialog_add_title);
        final EditText descriptionEditText = (EditText) view.findViewById(R.id.dialog_add_description);
        if (file != null) {
            // pre-populate
            titleEditText.setText(file.getTitle());
            descriptionEditText.setText(file.getDescription());

            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // empty
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // empty
                }

                @Override
                public void afterTextChanged(Editable s) {
                    String name = titleEditText.getText().toString();
                    String description = descriptionEditText.getText().toString();
                    updateAdapter();
                    mAdapter.update(file, name, description);
                }
            };

            titleEditText.addTextChangedListener(textWatcher);
            descriptionEditText.addTextChangedListener(textWatcher);
        }

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (file == null) {
                    String name = titleEditText.getText().toString();
                    String description = descriptionEditText.getText().toString();
                    mAdapter.add(new File(name, description, android.R.drawable.btn_star));
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, null);

        builder.create().show();
    }
}