package com.example.goforlunch.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.goforlunch.HeaderViewHolder;
import com.example.goforlunch.R;
import com.example.goforlunch.controller.fragment.BaseFragment;
import com.example.goforlunch.controller.fragment.RestaurantListFragment;
import com.example.goforlunch.controller.fragment.WorkmatesListFragment;
import com.example.goforlunch.model.firestore.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.activity_home_drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.activity_home_toolbar)
    Toolbar toolbar;
    @BindView(R.id.activity_home_nav_view)
    NavigationView navigationView;
    @BindView(R.id.navigation)
    BottomNavigationView navigation;
    @BindView(R.id.activity_main_progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.card_view)
    CardView mCardView;
    @BindView(R.id.edittext_autocomplete)
    EditText mEditText;
    @BindView(R.id.close_autocomplete)
    ImageButton mCloseAutocomplete;

    private BaseFragment fragment;
    private ProgressDialog mProgress;
    private ActionBarDrawerToggle toggle;

    private Disposable disposable;

    private Boolean isUsersListIsReady = false;
    private Boolean isRestaurantsListIsReady = false;
    private Double currentLat;
    private Double currentLng;

    private List<Bitmap> mBitmapList;
    private ArrayList<User> usersList;

    private long MIN_TIME_FOR_UPDATES = 10000;
    private long MIN_DISTANCE_FOR_UPDATES = 50;

    private PlacesClient placesClient;

    private String stringLocation;
    private int radius = 1000;
    private String type = "restaurant";


    private int i;
    private int k;

    public static final String INTENT_EXTRA_RESULT = "INTENT_EXTRA_RESULT";
    public static final String INTENT_EXTRA_PLACEDETAILSRESPONSE = "INTENT_EXTRA_PLACEDETAILSRESPONSE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        this.configureToolbar();

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        this.configureDrawerLayout();
        this.configureNavigationView();

        this.initMapsFragment();

        mProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Close the NavigationDrawer or the Autocomplete bar with the button back
     */
    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }


    //TOOLBAR\\
    private void configureToolbar() {
        setSupportActionBar(toolbar);
    }

    //MENU TOOLBAR\\

    /**
     * Inflate the menu and add it to the Toolbar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    /**
     * Handle actions on menu items.
     *
     * @param item Item selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_activity_home_search) {
            if (mCardView.getVisibility() == View.GONE) {
                //this.buttonSearch();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Call when the user click on the cross of the autocomplete bar.
     * Hide the autocomplete bar and notify the fragment to be update.
     */
    @OnClick(R.id.close_autocomplete)
    public void closeAutocomplete() {
        if (mCardView.getVisibility() == View.VISIBLE) {
            mCardView.setVisibility(View.GONE);
            toggle.setDrawerIndicatorEnabled(true);
            //   revertParcelableRestaurantDetails();
            // fragment.setParcelableRestaurantDetails(mParcelableRestaurantDetails);
        }
    }

    //BOTTOM TOOLBAR\\
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
        switch (item.getItemId()) {
            case R.id.navigation_map:
                initMapsFragment();
                return true;
            case R.id.navigation_list_restaurant:
                fragment = RestaurantListFragment.newInstance();
                addFragment();
                return true;
            case R.id.navigation_workmates:
                fragment = WorkmatesListFragment.newInstance();
                addFragment();
                return true;
        }
        return false;
    };

    //FRAGMENT\\
    private void initMapsFragment() {

        addFragment();
    }

    private void addFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, fragment).commit();
    }


    /**
     * Call when the user click on a restaurant or a workmate. Launch a new activity to see
     * restaurant details.
     */

    //MAIN MENU\\
    private void configureDrawerLayout() {
        toggle = new ActionBarDrawerToggle(this, drawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void configureNavigationView() {
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        HeaderViewHolder headerViewHolder = new HeaderViewHolder(this, header);
        headerViewHolder.updateMainMenuWithUserInfo();
    }

    /**
     * Call when an item is selected. Display the associate activity.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.activity_home_drawer_your_lunch:
                //  this.retrievesTheRestaurant();
                break;
            case R.id.activity_home_drawer_settings:
                this.launchSettingsActivity();
                break;
            case R.id.activity_home_drawer_logout:
                this.signOutUserFromFirebase();
                break;
            default:
                break;
        }
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    private void launchSettingsActivity() {
        Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    private void signOutUserFromFirebase() {
        mProgress.show();
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        mProgress.dismiss();
                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(HomeActivity.this, getString(R.string.fetch_failed),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void saveNewLocationAndShareItToTheFragment(Double newLat, Double newLng) {
        currentLat = newLat;
        currentLng = newLng;
        stringLocation = currentLat + "," + currentLng;
        fragment.setPosition(currentLat, currentLng);
    }

}














