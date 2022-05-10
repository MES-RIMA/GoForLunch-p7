package com.example.goforlunch.controller.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.data.model.User;

import java.util.ArrayList;

public abstract class BaseFragment extends Fragment {

    protected Double currentLat;
    protected Double currentLng;
    protected ArrayList<User> usersList;

    //CUSTOM\\
    protected abstract void notifyFragment();
    protected abstract void updateWithPosition();


    public void setPosition(Double currentLat, Double currentLng) {
        this.currentLat = currentLat;
        this.currentLng = currentLng;
        updateWithPosition();
    }

    public void setUsersList(ArrayList<User> usersList) {
        this.usersList.clear();
        this.usersList.addAll(usersList);
        notifyFragment();
    }

    //OVERRIDE\\

    /**
     * Call at the creation of the fragment.
     * @param savedInstanceState Bundle who contains extra data for the fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.usersList = new ArrayList<>();
    }



}
