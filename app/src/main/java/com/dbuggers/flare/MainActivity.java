package com.dbuggers.flare;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class MainActivity extends Activity implements SignupFragment.SignupInterface, GroupMakerFragment.GroupMakerInterface, MessageFragment.MessageInterface {

    private static final String TAG = "Interfaces";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new SignupFragment())
                    .commit();
        }
    }

    @Override
    public void onSignupClicked() {
        Log.v("ah", "Signing up user");
        /*getFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.container, new GroupMakerFragment())
                .commit();*/
        getFragmentManager().beginTransaction()
                .setTransition( FragmentTransaction.TRANSIT_FRAGMENT_FADE )
                .replace(R.id.container, new GroupMakerFragment())
                .commit();
    }
    @Override
    public void onGroupJoin() {
        Log.v(TAG, "The user has either created or found a group");
        /*getFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.container, new GroupMakerFragment())
                .commit();*/
        getFragmentManager().beginTransaction()
                .setTransition( FragmentTransaction.TRANSIT_FRAGMENT_FADE )
                .replace(R.id.container, new MessageFragment())
                .commit();
    }
}
