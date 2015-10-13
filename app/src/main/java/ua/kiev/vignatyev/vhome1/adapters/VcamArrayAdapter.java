package ua.kiev.vignatyev.vhome1.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import ua.kiev.vignatyev.vhome1.R;
import ua.kiev.vignatyev.vhome1.models.Vcam;

/**
 * Created by vignatyev on 01.07.2015.
 */
public class VcamArrayAdapter extends ArrayAdapter<Vcam> {
    private Context context;
    private static String THUMB_NAIL_URL = "http://vhome.dev.oscon.com.ua/vcam_thumbnail/";


    private List<Vcam> vcamList;

    public VcamArrayAdapter(Context context, int resource, List<Vcam> objects) {
        super(context, resource, objects);
        this.context = context;
        this.vcamList = objects;
        //setOnAdapterInteractionListener();
    }
    public interface OnAdapterInteractionListener {
        void onArchButtonClick(View view);
    }

    private OnAdapterInteractionListener listener;

    public void setOnAdapterInteractionListener(OnAdapterInteractionListener listener) {
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_vcam, parent, false);
        }
        Vcam vcam = vcamList.get(position);
        TextView vcamName = (TextView) convertView.findViewById(R.id.vcamName);
        vcamName.setText(vcam.VCAM_NAME);
        TextView tvVcamLocation = (TextView) convertView.findViewById(R.id.vcamLocation);
        tvVcamLocation.setText(vcam.VCAM_LOCATION);
        Button vacmArchiveButton = (Button) convertView.findViewById(R.id.vacmArchiveButton);

        vacmArchiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MyApp", "Button Click");
                if (listener != null) {
                    listener.onArchButtonClick(v);
                }
            }
        });

        if(vcam.THUMBNAIL != null) {
            ImageView ivThumb = (ImageView)  convertView.findViewById(R.id.ivThumb);
            ivThumb.setImageBitmap(vcam.THUMBNAIL);
        } else {
            final VcamAndThumb container = new VcamAndThumb(vcam, convertView);
            ThumbLoader loader = new ThumbLoader();
            loader.execute(container);
        }

        convertView.setTag(position);
        vacmArchiveButton.setTag(vcam.TOKEN);

        return convertView;
    }
    private class VcamAndThumb {
        public VcamAndThumb(Vcam vcam, View view) {
            this.vcam = vcam;
            this.view = view;
        }

        public Vcam vcam;
        public View view;
        public Bitmap thumb;

    }
    private class ThumbLoader extends AsyncTask<VcamAndThumb, Void, VcamAndThumb> {

        @Override
        protected VcamAndThumb doInBackground(VcamAndThumb... params) {
            VcamAndThumb container = params[0];
            Vcam vcam = container.vcam;
            InputStream in;
            try{
                String thumbNailUrl = THUMB_NAIL_URL + vcam.TOKEN + ".jpg";
                in = (InputStream) new URL(thumbNailUrl).getContent();
                vcam.THUMBNAIL = BitmapFactory.decodeStream(in);
                container.thumb = vcam.THUMBNAIL;
                in.close();
                return container;
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(VcamAndThumb vcamAndThumb) {
            if( vcamAndThumb!=null ) {
                ImageView ivThumb = (ImageView) vcamAndThumb.view.findViewById(R.id.ivThumb);
                ivThumb.setImageBitmap(vcamAndThumb.thumb);
                vcamAndThumb.vcam.THUMBNAIL = vcamAndThumb.thumb;
            }
        }
    }
}
