package ua.kiev.vignatyev.vhome1;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

/**
 * Created by vignatyev on 19.09.2015.
 */
public class MyAlertDialogFragment extends DialogFragment {
    public static MyAlertDialogFragment newInstance(int title) {
        MyAlertDialogFragment frag = new MyAlertDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");

        return new AlertDialog.Builder(getActivity())
            //.setIcon(R.drawable.alert_dialog_icon)
            .setTitle(title)
            .setPositiveButton(R.string.exitOk,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ((MainActivity)getActivity()).doPositiveClick();
                        }
                    }
            )
            .setNegativeButton(R.string.exitCancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ((MainActivity)getActivity()).doNegativeClick();
                        }
                    }
            )
            .create();
    }

}
