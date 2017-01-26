package edu.rose_hulman.bradylz.saveyourdata;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import edu.rose_hulman.bradylz.saveyourdata.HomePackage.HomeCloudTabFragment;
import edu.rose_hulman.bradylz.saveyourdata.HomePackage.HomeDownloadsTabFragment;
import edu.rose_hulman.bradylz.saveyourdata.HomePackage.HomeGeneralTabFragment;
import edu.rose_hulman.bradylz.saveyourdata.HomePackage.HomeTabsFragment;
import edu.rose_hulman.bradylz.saveyourdata.RoomPackage.RoomContentTabFragment;
import edu.rose_hulman.bradylz.saveyourdata.RoomPackage.RoomPeopleTabFragment;
import edu.rose_hulman.bradylz.saveyourdata.RoomPackage.RoomTabsFragment;

public class NavActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
                                                                HomeTabsFragment.OnFragmentInteractionListener,
                                                                HomeDownloadsTabFragment.OnFragmentInteractionListener,
                                                                HomeGeneralTabFragment.OnFragmentInteractionListener,
                                                                HomeCloudTabFragment.OnFragmentInteractionListener,
                                                                RoomPeopleTabFragment.OnFragmentInteractionListener,
                                                                RoomContentTabFragment.OnFragmentInteractionListener,
                                                                RoomTabsFragment.OnFragmentInteractionListener {

    public static final int REQUEST_CODE_INPUT = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Setting up the navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(savedInstanceState == null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            HomeTabsFragment htf = new HomeTabsFragment();
            htf.setContext(this);
            ft.replace(R.id.content_nav, htf);
            for(int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); ++i) {
                getSupportFragmentManager().popBackStackImmediate();
            }
            ft.commit();
        }
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Fragment switchTo = null;
        switch(item.getItemId()) {
            case R.id.nav_home:
                HomeTabsFragment htf = new HomeTabsFragment();
                htf.setContext(this);
                switchTo = htf;
                break;
            case R.id.nav_room:
                switchTo = new RoomTabsFragment();
                break;
            case R.id.nav_settings:
                //Empty
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                if(intent.resolveActivity(getPackageManager()) != null){
                    startActivity(intent);
                }
                break;
        }

        if(switchTo != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            for(int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); ++i) {
                getSupportFragmentManager().popBackStackImmediate();
            }
            ft.addToBackStack("previous");

            ft.replace(R.id.content_nav, switchTo);

            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
