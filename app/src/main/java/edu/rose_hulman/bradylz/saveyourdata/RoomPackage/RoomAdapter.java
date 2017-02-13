package edu.rose_hulman.bradylz.saveyourdata.RoomPackage;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import edu.rose_hulman.bradylz.saveyourdata.Constants;
import edu.rose_hulman.bradylz.saveyourdata.File;
import edu.rose_hulman.bradylz.saveyourdata.NavActivity;
import edu.rose_hulman.bradylz.saveyourdata.R;

import static android.content.Context.MODE_PRIVATE;

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

    private BluetoothAdapter mBluetoothAdapter;
    private static int REQUEST_ENABLE_BT = 1;
    private BluetoothServerSocket mSocket;
    private UUID MY_UUID = UUID.fromString("93e6dde0-f174-11e6-9598-0800200c9a66");
    BluetoothDevice mDevice;

    public RoomAdapter(Context context, RoomFragment.OnRoomFileInteractionListener listener) {
        mFiles = new ArrayList<>();
        mContext = context;
        mListener = listener;

        SharedPreferences prefs = context.getSharedPreferences(NavActivity.PREFS, MODE_PRIVATE);
        mUid = prefs.getString(NavActivity.KEY_UID, ""); //TODO: Means there is no uid

        //Initializing firebase references
        //TODO: Change to deal with the uids retrieved from bluetooth
        mFileRef = FirebaseDatabase.getInstance().getReference().child("file");
        mOwnerRef = FirebaseDatabase.getInstance().getReference().child("owner/" + mUid);
        mQuery = mFileRef.orderByChild("owners/" + mUid).equalTo(true);
        mQuery.addChildEventListener(new RoomFileEventListener());

        //Initializing bluetooth and checking if its supported
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String status;
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            //Toast indicating bluetooth is not working
            status = "Bluetooth is not enabled";
            Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
        } else {
            Log.d(Constants.BT_TAG, "Top of bluetooth");
            //Toast indicating bluetooth is working
            String address = mBluetoothAdapter.getAddress();
            String name = mBluetoothAdapter.getName();
            status = name + " : " + address + " bluetooth enabled";
            Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
            Log.d(Constants.BT_TAG, "After toast");

            //Checking to see if it's enabled
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                ((Activity) context).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }

            //Querying which bluetooth devices we are paired with
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                }
            }

            if (mBluetoothAdapter.isDiscovering()) {
                mBluetoothAdapter.cancelDiscovery();
            }
            mBluetoothAdapter.startDiscovery();

            // Register for broadcasts when a device is discovered.
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            context.registerReceiver(mReceiver, filter);

            Intent discoverableIntent =
                    new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            context.startActivity(discoverableIntent);

            // TelephonyManager tManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            // MY_UUID = UUID.fromString(tManager.getDeviceId());

            Log.d(Constants.BT_TAG, "UUID: " + MY_UUID);

            for (BluetoothDevice device : pairedDevices) {
                ConnectThread connectThread = new ConnectThread(device);
                connectThread.run();
                Log.d(Constants.BT_TAG, "Accepted and running connected thread");
            }

            AcceptThread acceptThread = new AcceptThread();
            acceptThread.run();

            Log.d(Constants.BT_TAG, "Accepted and running thread");

//            try {
//                mSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("SYD", MY_UUID);
//            } catch (IOException e) {
//                Log.d(Constants.BT_TAG, "IOException from uuid");
//            }
//
//            try {
//                mSocket.accept();
//            } catch (IOException e) {
//                Log.d(Constants.BT_TAG, "Error near accept");
//            }
        }
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(mContext.getString(R.string.app_name), MY_UUID);
            } catch (IOException e) {
                Log.d(Constants.TAG, "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.d(Constants.TAG, "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
                    //TODO: manageMyConnectedSocket(socket);
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.d(Constants.TAG, "Could not close the connect socket", e);
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.d(Constants.BT_TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.d(Constants.BT_TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            // manageMyConnectedSocket(mmSocket);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.d(Constants.BT_TAG, "Could not close the client socket", e);
            }
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
            }
        }
    };

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
