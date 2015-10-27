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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PinchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PinchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PinchFragment extends Fragment {
    /**
     * PRIVATE VARS
     */
    private String mImageUrl;
    private ImageView mPinchView;
    private TouchImageView mTouchImageView;

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
        if (getArguments() != null) {
            mImageUrl = MainActivity.SERVER_URL + getArguments().getString(PinchActivity.ARG_IMAGE_URL);


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pinch, container, false);
        //mPinchView = (ImageView) v.findViewById(R.id.ivPinchView);

        RequestQueue queue = Volley.newRequestQueue(getActivity());

        mTouchImageView = new TouchImageView(getActivity().getApplicationContext());
        mTouchImageView.setTag(mImageUrl);

        //container.addView(mTouchImageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ((ViewGroup) v).addView(mTouchImageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        Log.d("MyApp", "PinchFragment uri: " + mImageUrl);

        ImageRequest imageRequest = new ImageRequest( mImageUrl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        mTouchImageView.setImageBitmap(bitmap);
                    }
                }, 0,0,
                Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("MyApp",volleyError.getMessage());
                    }
                }
        );
        queue.add(imageRequest);

        return v;
    }

    @Override
    public void onDestroyView() {
        ViewGroup v = (ViewGroup) getView();

        ImageView imageView = (ImageView) v.getChildAt(0);
        if(imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof BitmapDrawable) {
                Log.d("MyApp", "Recycling image: " + imageView.getTag());
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                bitmap.recycle();
            }
        }
        //v.removeView((ImageView) object);
        super.onDestroyView();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
     }

    @Override
    public void onDetach() {
        super.onDetach();
     }
}
