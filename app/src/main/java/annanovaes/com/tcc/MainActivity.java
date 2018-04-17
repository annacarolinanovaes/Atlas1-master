package annanovaes.com.tcc;

import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;
import com.indooratlas.android.sdk.IARegion;

import static android.widget.Toast.*;

public class MainActivity extends AppCompatActivity implements IALocationListener  {

    private final int CODE_PERMISSIONS = 0;
    private IALocationManager mIALocationManager;
    private IARegion IARegion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String[] neededPermissions = {
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        ActivityCompat.requestPermissions(this, neededPermissions, CODE_PERMISSIONS);

        mIALocationManager = IALocationManager.create(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onLocationChanged(IALocation location) {
        makeText(this, "Latitude: " + location.getLatitude() +
                                         "Longitude: " + location.getLongitude() +
                                         "Floor number: " + location.getFloorLevel(), LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mIALocationManager.requestLocationUpdates(IALocationRequest.create(), this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIALocationManager.requestLocationUpdates(IALocationRequest.create(), this);
    }

    private IARegion.Listener mRegionListener = new IARegion.Listener() {
        IARegion mCurrentFloorPlan = null;

        @Override
        public void onEnterRegion(IARegion region) {
            if (region.getType() == IARegion.TYPE_FLOOR_PLAN) {
                makeText(getApplicationContext(), "Entered: " + region.getName() + " Floor plan ID: " + region.getId(), LENGTH_LONG).show();
                mCurrentFloorPlan = region;
            }
        }

        @Override
        public void onExitRegion(IARegion region) {}
    };

    private IARegion.Listener mRegionListener = new IARegion.Listener() {
        // when null, we are not on any mapped area
        // this information can be used for indoor-outdoor detection
        IARegion mCurrentFloorPlan = null;

        @Override
        public void onEnterRegion(IARegion region) {
            if (region.getType() == IARegion.TYPE_FLOOR_PLAN) {
                // triggered when entering the mapped area of the given floor plan
                makeText(getApplicationContext(), "Entered " + region.getName(), LENGTH_LONG).show();
                makeText(getApplicationContext(),  "floor plan ID: " + region.getId(), LENGTH_LONG).show();
                mCurrentFloorPlan = region;
            }
            else if (region.getType() == IARegion.TYPE_VENUE) {
                // triggered when near a new location
                makeText(getApplicationContext(), "Location changed to " + region.getId(), LENGTH_LONG).show();
            }
        }

        @Override
        public void onExitRegion(IARegion region) {
            // leaving a previously entered region
            if (region.getType() == IARegion.TYPE_FLOOR_PLAN) {
                mCurrentFloorPlan = null;
                // notice that a change of floor plan (e.g., floor change)
                // is signaled by an exit-enter pair so ending up here
                // does not yet mean that the device is outside any mapped area
            }
        }
    };
}
