package edu.rose_hulman.bradylz.saveyourdata;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.rose_hulman.bradylz.saveyourdata.HomePackage.HomeCloudTabFragment;
import edu.rose_hulman.bradylz.saveyourdata.HomePackage.HomeFavoritesTabFragment;
import edu.rose_hulman.bradylz.saveyourdata.HomePackage.HomeTabsFragment;
import edu.rose_hulman.bradylz.saveyourdata.BrowsePackage.BrowseFragment;

public class NavActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        HomeTabsFragment.OnFragmentInteractionListener,
        HomeFavoritesTabFragment.OnHomeFavoritesFileSelectedInteractionListener,
        HomeCloudTabFragment.OnHomeCloudFileInteractionSelectedListener,
        LoginFragment.OnLoginListener,
        GoogleApiClient.OnConnectionFailedListener,
        BrowseFragment.OnBrowseFileInteractionListener,
        DetailFragment.OnFileDetailSelectedInteractionListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private OnCompleteListener mOnCompleteListener;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private String mUid;
    public static String PREFS = "PREFS";
    public static String KEY_UID = "KEY_UID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        initializeListeners();

        //Setting up the navigation drawer
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initializeListeners() {
        Log.d(Constants.TAG, "Top of initialize listeners");
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                //Get user and uid to pass into fragments
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    mUid = user.getUid();
                    SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(KEY_UID, mUid);
                    editor.commit();

                    switchToHomeTabsFragment(mUid);
                } else {
                    switchToLoginFragment();
                }
            }
        };
        mOnCompleteListener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    showLoginError("Authentication Failed.");
                }
            }
        };
    }

    private void showNavBar(boolean show) {
        int lockMode = show ? DrawerLayout.LOCK_MODE_UNLOCKED :
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
        drawer.setDrawerLockMode(lockMode);
        toggle.setDrawerIndicatorEnabled(show);
    }

    private void switchToLoginFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_nav, new LoginFragment(), "Login");

        //Hide the nav bar if currently enabled
        showNavBar(false);
        ft.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onLogin(String email, String password) {
        //DONE: Log user in with username & password
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(mOnCompleteListener);
    }

    @Override
    public void onRegister(final String email, final String password) {
        //create user
        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (authResult.getUser() == null) {
                    Toast.makeText(NavActivity.this, "Registration failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showLoginError(String message) {
        LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag("Login");
        loginFragment.onLoginError(message);
    }

    public void onLogout() {
        mAuth.signOut();
        showNavBar(false);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            onLogout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void switchToHomeTabsFragment(String uid) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        HomeTabsFragment htf = new HomeTabsFragment();
        htf.setContext(this);
        ft.replace(R.id.content_nav, htf, "Home Tabs");
        ft.addToBackStack("home tabs");

        //Show the nav bar if currently disabled
        showNavBar(true);
        htf.setUid(uid);

        ft.commit();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment switchTo = null;
        switch (item.getItemId()) {
            case R.id.nav_home:
                switchTo = new HomeTabsFragment();
                break;
            case R.id.nav_browse:
                switchTo = new BrowseFragment();
                break;
            case R.id.nav_settings:
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                break;
        }

        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); ++i) {
            getSupportFragmentManager().popBackStackImmediate();
        }

        if (switchTo != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_nav, switchTo);
            ft.addToBackStack("previous");
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        showLoginError(connectionResult.getErrorMessage());
    }

    @Override
    public void onBrowseFileInteraction(File file) {

    }

    @Override
    public void onHomeCloudFileInteraction(File file) {
        switchToDetailView(file);
    }

    @Override
    public void onLongCloudFileInteraction(File file, boolean fav) {
        editFileDialog(file, fav);
    }

    @Override
    public void onHomeFavoritesFileInteraction(File file) {
        switchToDetailView(file);
    }

    @Override
    public void onLongFavoritesFileInteraction(File file, boolean fav) {
        editFileDialog(file, fav);
    }

    private void switchToDetailView(File file) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        DetailFragment fragment = DetailFragment.newInstance(file);
        ft.replace(R.id.content_nav, fragment);
        ft.addToBackStack("detail");
        ft.commit();
    }

    private void editFileDialog(final File file, boolean favorited) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit " + file.getName());

        final DatabaseReference fileRef = FirebaseDatabase.getInstance().getReference().child("file");
        final DatabaseReference ownerRef = FirebaseDatabase.getInstance().getReference().child("owner").child(mUid);

        builder.setPositiveButton(R.string.edit_favorite, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fileRef.child(file.getKey()).child(File.FILE_FAVORITEDBY).child(mUid).setValue(true);
                fileRef.keepSynced(true);
            }
        });

        builder.setNegativeButton(R.string.edit_unfavorite, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fileRef.child(file.getKey()).child(File.FILE_FAVORITEDBY).child(mUid).removeValue();
                fileRef.keepSynced(true);
            }
        });

        builder.setNeutralButton(R.string.delete_file_string, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fileRef.child(file.getKey()).child("owners/" + mUid).removeValue();
                ownerRef.child("files/" + file.getKey()).removeValue();
                ownerRef.keepSynced(true);
            }
        });

        builder.create().show();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFileDetailInteraction(File file) {

    }
}
