package ua.kiev.vignatyev.vhome1;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class VarchPlayerActivity extends FragmentActivity {

    Bundle mExtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_varch_player);

        Bundle mExtras = getIntent().getExtras();
        VarchPlayerFragment f = VarchPlayerFragment.newInstance(mExtras);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment, f).commit();

    }
}
