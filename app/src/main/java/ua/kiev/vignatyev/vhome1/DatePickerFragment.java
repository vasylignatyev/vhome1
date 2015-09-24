package ua.kiev.vignatyev.vhome1;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DatePickerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DatePickerFragment extends DialogFragment {
    private static final String ARG_YEAR = "YEAR";
    private static final String ARG_MONTH = "MONTH";
    private static final String ARG_DAY = "DAY";

    private int mYear;
    private int mMonth;
    private int mDay;

    private DatePickerDialog.OnDateSetListener mOnDateSetListener;

    /**
     *
     */
    public DatePickerFragment() {
        // Required empty public constructor
    }

    /**
     *
     * @param onDateSetListener
     */
    public void setCallBack(DatePickerDialog.OnDateSetListener onDateSetListener) {
        mOnDateSetListener = onDateSetListener;
    }

    /**
     *
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static DatePickerFragment newInstance( int year, int month, int day) {
        DatePickerFragment fragment = new DatePickerFragment();
        Bundle args = new Bundle();
         args.putInt(ARG_YEAR, year);
        args.putInt(ARG_MONTH, month);
        args.putInt(ARG_DAY, day);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     *
     * @param savedInstanceState
     * @return
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mYear = getArguments().getInt(ARG_YEAR);
            mMonth = getArguments().getInt(ARG_MONTH);
            mDay = getArguments().getInt(ARG_DAY);
        }
        return new DatePickerDialog(getActivity(), mOnDateSetListener, mYear, mMonth, mDay);
        //return super.onCreateDialog(savedInstanceState);
    }
}
