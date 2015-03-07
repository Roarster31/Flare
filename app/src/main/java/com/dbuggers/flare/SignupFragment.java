package com.dbuggers.flare;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by benallen on 07/03/15.
 */
public class SignupFragment extends Fragment {
    private static final String TAG = "Interfaces";
    private SignupInterface signupInterface;

    public interface SignupInterface {
        public void onSignupClicked();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getActivity() instanceof SignupInterface){
            signupInterface = (SignupInterface) getActivity();
        }else{
            throw new IllegalStateException(getActivity().getClass().getName()+" must implement SignupInterface");
        }
    }

    public SignupFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        final Button button = (Button) rootView.findViewById(R.id.signUpBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Log.v(TAG, "Sign up button pressed");
                signupInterface.onSignupClicked();

            }
        });

        return rootView;
    }
}