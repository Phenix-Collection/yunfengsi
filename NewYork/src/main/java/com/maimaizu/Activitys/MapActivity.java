package com.maimaizu.Activitys;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.maimaizu.R;
import com.maimaizu.Utils.ToastUtil;

/**
 * Created by Administrator on 2017/5/3.
 */

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        MapFragment mapFragment = (MapFragment)getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ToastUtil.showToastShort("如设备未安装谷歌基础服务将无法使用使用地图相关功能", Gravity.CENTER);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (PermissionChecker.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE}, 0);
                return;
            }
        }
        LatLng sydney = new LatLng(Double.valueOf(getIntent().getStringExtra("lat")),Double.valueOf(getIntent().getStringExtra("lng")));

        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

        map.addMarker(new MarkerOptions()
//                .title("Sydney")
//                .snippet("The most populous city in Australia.")
                .position(sydney));
    }
}
