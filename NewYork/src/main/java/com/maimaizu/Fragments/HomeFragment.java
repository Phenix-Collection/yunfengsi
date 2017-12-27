package com.maimaizu.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.request.BaseRequest;
import com.maimaizu.Activitys.Home2_Detail;
import com.maimaizu.Activitys.NewHouseActivity;
import com.maimaizu.Activitys.Search;
import com.maimaizu.Activitys.ZiXun_Detail;
import com.maimaizu.Activitys.ZuFangActivity;
import com.maimaizu.Adapter.mBaseAdapter;
import com.maimaizu.Base.BaseFragment;
import com.maimaizu.R;
import com.maimaizu.Utils.AnalyticalJSON;
import com.maimaizu.Utils.Constants;
import com.maimaizu.Utils.DimenUtils;
import com.maimaizu.Utils.LogUtil;
import com.maimaizu.Utils.Network;
import com.maimaizu.Utils.ProgressUtil;
import com.maimaizu.Utils.ToastUtil;
import com.maimaizu.Utils.mApplication;
import com.maimaizu.View.mHeadView;
import com.maimaizu.View.mItemDecoration;
import com.maimaizu.citypicker.CityPickerActivity;
import com.maimaizu.citypicker.adapter.CityListAdapter;
import com.youth.banner.listener.OnBannerListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Administrator on 2017/4/25.
 */

public class HomeFragment extends BaseFragment implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private TextView location;//定位
    private TextView search;//搜索
    private RecyclerView recycler;
    private mBaseAdapter adapter;
    private AMapLocationClient mLocationClient;//高德定位
    private static final int REQUEST_CODE_PICK_CITY = 233;//城市选择页面请求码
    private boolean Defaut = true;//是否使用默认城市
    private mHeadView headView;

    // TODO: 2017/5/2 banner 点击检测
    private static final int AD = 1;
    private static final int NEW = 2;
    private static final int ERSHOU = 3;
    private static final int ZULIN = 4;
    private static final int ZIXUN = 5;

    private ArrayList<String> images;
    private ArrayList<HashMap<String, String>> imgInfos;//图片参数
    private static final String Nothing = "该地区暂无房源推荐\n点击切换地区";
    private static final String NoNetWork = "网络连接失败，请检查网络";

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home;
    }

    private TextView nothing;
    GoogleApiClient mGoogleApiClient;

    @Override
    public void initView(View view) {

        location = (TextView) view.findViewById(R.id.mine_location);
        search = (TextView) view.findViewById(R.id.home_search);
        Drawable d = ContextCompat.getDrawable(getActivity(), R.drawable.search_black);
        d.setBounds(0, 0, DimenUtils.dip2px(getActivity(), 20), DimenUtils.dip2px(getActivity(), 20));
        search.setCompoundDrawables(d, null, null, null);
        search.setHint(mApplication.ST("搜楼盘/小区"));
        search.setFocusable(false);
        search.setCompoundDrawablePadding(DimenUtils.dip2px(getActivity(), 5));


        recycler = (RecyclerView) view.findViewById(R.id.home_recycler);
        RecyclerView.LayoutManager rl = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(rl);
        headView = new mHeadView(getActivity());
        headView.setBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                HashMap<String, String> map = imgInfos.get(position);
                LogUtil.e(map + "    " + position);
                if (map != null) {
                    Intent intent = new Intent();
                    switch (Integer.valueOf(map.get("type"))) {
                        case AD:
                            intent.setClass(getActivity(), com.maimaizu.Activitys.AD.class);
                            intent.putExtra("url", map.get("url"));
                            startActivity(intent);
                            break;
                        case NEW:
                            intent.setClass(getActivity(), NewHouseActivity.class);
                            intent.putExtra("id", map.get("aid"));
                            startActivity(intent);
                            break;
                        case ERSHOU:
                            intent.setClass(getActivity(), Home2_Detail.class);
                            intent.putExtra("id", map.get("aid"));
                            startActivity(intent);
                            break;
                        case ZULIN:
                            intent.setClass(getActivity(), ZuFangActivity.class);
                            intent.putExtra("id", map.get("aid"));
                            startActivity(intent);
                            break;
                        case ZIXUN:
                            intent.setClass(getActivity(), ZiXun_Detail.class);
                            intent.putExtra("id", map.get("aid"));
                            startActivity(intent);
                            break;

                    }
                }
            }
        });


        adapter = new mBaseAdapter(getActivity(), new ArrayList<mBaseAdapter.OneMulitem>());
        adapter.addHeaderView(headView);
        TextView textView = new TextView(getActivity());
        textView.setText(mApplication.ST("猜你喜欢"));
        textView.setTextSize(16);
        textView.setTextColor(Color.BLACK);
        textView.setBackgroundColor(Color.WHITE);
        textView.setPadding(DimenUtils.dip2px(getActivity(), 15), DimenUtils.dip2px(getActivity(), 10), DimenUtils.dip2px(getActivity(), 5), DimenUtils.dip2px(getActivity(), 10));

        View l1 = new View(getActivity());
        l1.setBackgroundColor(Color.parseColor("#eeeeee"));
        l1.setMinimumHeight(DimenUtils.dip2px(getActivity(), 4));
        adapter.addHeaderView(l1, 1);
        adapter.addHeaderView(textView, 2);
        View l2 = new View(getActivity());
        l2.setMinimumHeight(DimenUtils.dip2px(getActivity(), 3));
        l2.setBackgroundColor(Color.parseColor("#eeeeee"));
        adapter.addHeaderView(l2, 3);
        recycler.addItemDecoration(new mItemDecoration(getActivity()));

        nothing = new TextView(getActivity());
        nothing.setText(mApplication.ST(Nothing));
        Drawable drawable = ActivityCompat.getDrawable(getActivity(), R.drawable.load_nothing);
        drawable.setBounds(0, 0, DimenUtils.dip2px(getActivity(), 120), DimenUtils.dip2px(getActivity(), 120));
        nothing.setCompoundDrawables(null, drawable, null, null);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, DimenUtils.dip2px(getActivity(), 10), 0, DimenUtils.dip2px(getActivity(), 10));
        nothing.setLayoutParams(layoutParams);
        nothing.setGravity(Gravity.CENTER);
        adapter.setEmptyView(false, true, nothing);
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        recycler.setAdapter(adapter);
        mGoogleApiClient = new GoogleApiClient
                .Builder(getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), this)
                .build();
        mGoogleApiClient.connect();


        // TODO: 2017/5/2 地址缓存 未做
//        images = new ArrayList<>();
        headView.setData(null);

        nothing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkNetWork();
                if (nothing.getText().toString().equals(NoNetWork)) {
                    ToastUtil.showToastShort( "请检查网络", Gravity.CENTER);
                } else if (nothing.getText().toString().equals(Nothing)) {
                    mApplication.city = 1;
                    location.setText(mApplication.ST("纽约"));
                    Intent intent = new Intent(getActivity(), CityPickerActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_PICK_CITY);
                    EventBus.getDefault().post("1");
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        checkNetWork();
    }

    private void checkNetWork() {
        if (!Network.HttpTest(getActivity())) {
            nothing.setText(NoNetWork);
        } else {
            nothing.setText(Nothing);
        }
    }

    @Override
    public void setOnClick() {
        location.setOnClickListener(this);
        search.setOnClickListener(this);
        adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                int type = Integer.valueOf(((mBaseAdapter.OneMulitem) adapter.getItem(i)).getItemType());
                switch (type) {
                    case 1:
                        Intent intent1 = new Intent(getActivity(), NewHouseActivity.class);
                        intent1.putExtra("id", ((mBaseAdapter.OneMulitem) adapter.getItem(i)).getId());
                        startActivity(intent1);
                        break;
                    case 2:
                        Intent intent = new Intent(getActivity(), Home2_Detail.class);
                        intent.putExtra("id", ((mBaseAdapter.OneMulitem) adapter.getItem(i)).getId());
                        startActivity(intent);
                        break;
                    case 3:
                        Intent intent2 = new Intent(getActivity(), ZuFangActivity.class);
                        intent2.putExtra("id", ((mBaseAdapter.OneMulitem) adapter.getItem(i)).getId());
                        startActivity(intent2);
                        break;
                }

            }
        });

    }

    @Override
    public boolean setEventBus() {
        return true;
    }

    @Override
    public void doThings() {
//        getData();
        initLoaction();//定位
        getBanner();
        getData();
        location.setText("纽约");
    }

    // TODO: 2017/5/2 获取轮播图
    private void getBanner() {
        OkGo.post(Constants.getBanner)
                .params("key", Constants.safeKey)
                .params("m_id", Constants.M_id)
                .execute(new AbsCallback<ArrayList<HashMap<String, String>>>() {
                    @Override
                    public void onSuccess(ArrayList<HashMap<String, String>> list, Call call, Response response) {
                        imgInfos = list;
                        if (imgInfos != null) {
                            if (images == null) {
                                images = new ArrayList<String>();
                            } else {
                                images.clear();
                            }
                            for (HashMap<String, String> map : imgInfos) {
                                images.add(map.get("image"));
                            }
                            headView.setData(images);

                        } else {
                            getBanner();
                        }
                    }

                    @Override
                    public ArrayList<HashMap<String, String>> convertSuccess(Response response) throws Exception {
                        return AnalyticalJSON.getList_zj(response.body().string());
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetCitys(String id) {//id
        getData();
//        if(imgInfos==null) {
        getBanner();
//        }
    }

    // TODO: 2017/4/26 获取数据
    private void getData() {
        OkGo.post(Constants.getHomeMore).tag(this).params("key", Constants.safeKey)
                .params("m_id", Constants.M_id)
                .params("page", "1")
                .params("city", mApplication.city)
                .execute(new AbsCallback<ArrayList<mBaseAdapter.OneMulitem>>() {
                    @Override
                    public ArrayList<mBaseAdapter.OneMulitem> convertSuccess(Response response) throws Exception {
                        if (response != null) {
                            String data = response.body().string();
                            JSONArray jsonArray = new JSONArray(data);
                            ArrayList<mBaseAdapter.OneMulitem> oneList = new ArrayList<mBaseAdapter.OneMulitem>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                                mBaseAdapter.OneMulitem o = adapter.getOneMulitem();
                                o.setArea(jsonObject.getString("area"));
                                o.setHousetype(jsonObject.getString("housetype"));
                                o.setId(jsonObject.getString("id"));
                                o.setImage(jsonObject.getString("image"));
                                o.setMoney(jsonObject.getString("money"));
                                o.setPoint(jsonObject.getString("point"));
                                o.setTags(new JSONArray(jsonObject.getString("bq")));
                                o.setVillage(jsonObject.getString("village"));
                                o.setTitle(jsonObject.getString("title"));
                                o.setItemType(Integer.valueOf(jsonObject.getString("type")));
                                oneList.add(o);
                            }
                            return oneList;
                        }
                        return null;
                    }

                    @Override
                    public void onSuccess(ArrayList<mBaseAdapter.OneMulitem> list, Call call, Response response) {
                        if (list != null) {
                            adapter.setNewData(list);
                        }
                        ProgressUtil.dismiss();
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        ProgressUtil.show(getActivity(), "", "请稍等");
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        ProgressUtil.dismiss();
                        checkNetWork();
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mine_location:
                startActivityForResult(new Intent(getActivity(), CityPickerActivity.class), REQUEST_CODE_PICK_CITY);
                break;
            case R.id.home_search:
                startActivity(new Intent(getActivity(), Search.class));
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_CITY && resultCode == RESULT_OK) {
            if (data != null) {
                String city = data.getStringExtra(CityPickerActivity.KEY_PICKED_CITY);
                location.setText(mApplication.ST(city));
            }
        }
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, getActivity());
                String toastMsg = String.format("Place: %s", place.getName());
                LogUtil.e("types::" + place.getPlaceTypes() + " 地址：：" + place.getAddress() + "  Locale  " +
                        "" + place.getLocale() +
                        "    getAttributions    " + place.getAttributions());
                ;
                Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    // TODO: 2017/4/25 页面销毁
    @Override
    public void onDestroy() {
        super.onDestroy();
//        mLocationClient.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }

    // TODO: 2017/4/25 定位
    private void initLoaction() {
//        LogUtil.e("定位");
//        ToastUtil.showToastShort(getActivity(), "谷歌地图结果回调   " + mGoogleApiClient.isConnected() + "    " + mGoogleApiClient.isConnecting(), Gravity.TOP);
//        location.post(new Runnable() {
//            @Override
//            public void run() {
//                if (mGoogleApiClient.isConnected()) {
//                    if (Build.VERSION.SDK_INT >= 23) {
//                        if (PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                                || PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE}, 0);
//                            return;
//                        }
//                    }
//                    PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi.getCurrentPlace(mGoogleApiClient, null);
//                    LogUtil.e("谷歌ITU" + result);
//                    result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
//                        @Override
//                        public void onResult(@NonNull PlaceLikelihoodBuffer placeLikelihoods) {
//                            LogUtil.e(placeLikelihoods + "      ~~!~!~!~");
//                            for (PlaceLikelihood placeLikelihood : placeLikelihoods) {
//                                ToastUtil.showToastLong(getActivity(), String.format("Place '%s' has likelihood: %g",
//                                        placeLikelihood.getPlace().getName(),
//                                        placeLikelihood.getLikelihood()), Gravity.CENTER);
//                            }
//                            placeLikelihoods.release();
//                        }
//                    });
//                }
//            }
//        });


//        PendingResult<Status> pendingResult=
//        int PLACE_PICKER_REQUEST = 1;
//        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//
//        try {
//            startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
//        } catch (GooglePlayServicesRepairableException e) {
//            e.printStackTrace();
//        } catch (GooglePlayServicesNotAvailableException e) {
//            e.printStackTrace();
//        }

        if (Build.VERSION.SDK_INT >= 23) {
            if (PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE}, 0);
                return;
            }
        }
        mLocationClient = new AMapLocationClient(getActivity());
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        option.setNeedAddress(true);
        option.setOnceLocation(true);
        mLocationClient.setLocationOption(option);
        mLocationClient.setLocationListener(new mMapLoacitonListener());
        mLocationClient.startLocation();
    }

    public static class mMapLoacitonListener implements AMapLocationListener {
        private CityListAdapter mCityAdapter;

        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
                    String city = aMapLocation.getCity();
                    String district = aMapLocation.getDistrict();
                    ToastUtil.showToastShort("当前定位: " + city);
                } else {
                    //定位失败
                    LogUtil.w(aMapLocation.getErrorInfo() + "   " + aMapLocation.getErrorCode() + "\n");
                    ToastUtil.showToastLong( "定位失败,请检查网络连接"
                            , Gravity.CENTER);
                }
            }
        }
    }

    // TODO: 2017/4/25 权限回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                initLoaction();
            }
        }
    }

    // TODO: 2017/4/27 谷歌服务连接回调
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        ToastUtil.showToastLong("谷歌服务连接失败", Gravity.CENTER);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        ToastUtil.showToastShort( "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        ToastUtil.showToastShort( "onConnectionSuspended");
    }
}
