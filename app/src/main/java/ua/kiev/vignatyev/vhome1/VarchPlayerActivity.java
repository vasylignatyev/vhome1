package ua.kiev.vignatyev.vhome1;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

public class VarchPlayerActivity extends FragmentActivity {

    Bundle mExtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_varch_player);

        Bundle mExtras = getIntent().getExtras();
        ScrollBarFragment f = ScrollBarFragment.newInstance(mExtras);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment, f).commit();

    }
}
