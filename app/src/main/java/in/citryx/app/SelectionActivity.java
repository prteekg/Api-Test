package in.citryx.app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import in.citryx.app.selections.ButtonClickInterface;
import in.citryx.app.selections.Cabs;
import in.citryx.app.selections.Food;
import in.citryx.app.selections.Travel;

public class SelectionActivity extends AppCompatActivity implements ButtonClickInterface {

    private final String TAG = "SelectionActivity";
    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private Activity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        thisActivity = this;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(thisActivity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            Log.d(TAG, "Permission requested");
            return;
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED)
            finish();
    }

    @Override
    public void buttonClicked(String s) {
        switch (s){
            case "travel_fragment":
                Intent intent = new Intent(getApplicationContext(), TravelActivity.class);
                startActivity(intent);
                return;
            case "food_fragment":
                Intent intent1 = new Intent(getApplicationContext(), MapsActivity.class);
                intent1.putExtra("type", "food");
                startActivity(intent1);
                return;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            Log.d(TAG, "pos = " + position);
            switch (position) {
                case 0:
                    Travel travel = new Travel();
                    return travel;


                case 1:
                    Food food = new Food();
                    return food;


                case 2:
                    Cabs cabs = new Cabs();
                    return cabs;


            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "TRAVEL";
                case 1:
                    return "CABS";
                case 2:
                    return "FOOD";
            }
            return null;
        }
    }


}
