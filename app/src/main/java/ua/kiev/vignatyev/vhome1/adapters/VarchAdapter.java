package ua.kiev.vignatyev.vhome1.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import ua.kiev.vignatyev.vhome1.R;
import ua.kiev.vignatyev.vhome1.models.Varch;

/**
 * Created by vignatyev on 18.08.2015.
 */
public class VarchAdapter extends ArrayAdapter<Varch> {
    private Context context;
    private List<Varch> mVarchList;


    public VarchAdapter(Context context, int resource, List<Varch> objects) {
        super(context, resource, objects);
        this.context = context;
        this.mVarchList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater =
                (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_varch, parent, false);

        Varch varch = mVarchList.get(position);

        TextView tvArchDate = (TextView) view.findViewById(R.id.tvArchDate);
        TextView tvArchTime = (TextView) view.findViewById(R.id.tvArchTime);
        TextView tvArchDuration = (TextView) view.findViewById(R.id.tvArchDuration);

        tvArchDate.setText((CharSequence) varch.date);
        tvArchTime.setText((CharSequence) varch.time);
        tvArchDuration.setText((CharSequence) varch.duration);

        view.setTag(varch);

        return view;
    }
}
