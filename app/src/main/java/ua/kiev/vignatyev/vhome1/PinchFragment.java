package ua.kiev.vignatyev.vhome1;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.io.InputStream;
import java.net.URL;


public class PinchFragment extends Fragment {
    /**
     * PRIVATE VARS
     */
    private String mImageUrl;
    private TouchImageView mTouchImageView;
    private OnFragmentInteractionListener mListener;

    public static PinchFragment newInstance(String imageUrl) {
        PinchFragment fragment = new PinchFragment();
        Bundle args = new Bundle();
        args.putString(PinchActivity.ARG_IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    public PinchFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pinch, container, false);
        mTouchImageView = (TouchImageView) v.findViewById(R.id.tvImage);

        if (mListener != null) {
            mImageUrl = MainActivity.SERVER_URL + mListener.getImageUrl();
            if(mImageUrl != null ) {
                Log.d("MyApp", "PinchFragment uri: " + mImageUrl);

                RequestQueue queue = Volley.newRequestQueue(getActivity());

                ImageRequest imageRequest = new ImageRequest(mImageUrl,
                        new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap bitmap) {
                                mTouchImageView.setImageBitmap(bitmap);
                            }
                        }, 0, 0,
                        Bitmap.Config.ARGB_8888,
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Log.d("MyApp", volleyError.getMessage());
                            }
                        }
                );
                queue.add(imageRequest);
            }
        }
        return v;
    }

    @Override
    public void onDestroyView() {
        Drawable drawable = mTouchImageView.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            Log.d("MyApp", "Recycling image");
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            bitmap.recycle();
        }
        super.onDestroyView();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
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
