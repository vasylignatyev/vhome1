package ua.kiev.vignatyev.vhome1;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class VarchPlayerActivityFragment extends Fragment {

    private static final String TAG = "VarchPlayerActivityFragment";

    private static final String ARG_VARCH_NAME = "varchName";
    private static final String ARG_USER_TOKEN = "userToken";
    private static final String ARG_VCAM_TOKEN = "vcamToken";

    private String mVarchName;
    private String mUserToken;
    private String mVcamToken;


    private OnFragmentInteractionListener mListener;

    public VarchPlayerActivityFragment() {
    }

    public static VarchPlayerActivityFragment newInstance(String varchName,String userToken,String vcamToken) {
        VarchPlayerActivityFragment fragment = new VarchPlayerActivityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_VARCH_NAME, varchName);
        args.putString(ARG_USER_TOKEN, userToken);
        args.putString(ARG_VCAM_TOKEN, vcamToken);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity;
        try {
            if (context instanceof Activity){
                activity = (Activity) context;
                mListener = (OnFragmentInteractionListener) activity;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException("Must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mVarchName = getArguments().getString(ARG_VARCH_NAME);
            mUserToken = getArguments().getString(ARG_USER_TOKEN);
            mVcamToken = getArguments().getString(ARG_VCAM_TOKEN);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_VARCH_NAME, mVarchName);
        outState.putString(ARG_USER_TOKEN, mUserToken);
        outState.putString(ARG_VCAM_TOKEN, mVcamToken);
    }


    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        mVarchName = savedInstanceState.getString(ARG_VARCH_NAME, null);
        mUserToken = savedInstanceState.getString(ARG_USER_TOKEN, null);
        mVcamToken = savedInstanceState.getString(ARG_VCAM_TOKEN, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_varch_player, container, false);
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        String getImageUrl();
    }
}
