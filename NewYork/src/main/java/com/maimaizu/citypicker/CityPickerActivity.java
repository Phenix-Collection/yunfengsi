package com.maimaizu.citypicker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.maimaizu.R;
import com.maimaizu.SideListview.CharacterParser;
import com.maimaizu.Utils.Constants;
import com.maimaizu.Utils.mApplication;
import com.maimaizu.citypicker.adapter.CityListAdapter;
import com.maimaizu.citypicker.adapter.ResultListAdapter;
import com.maimaizu.citypicker.db.DBManager;
import com.maimaizu.citypicker.model.City;
import com.maimaizu.citypicker.model.LocateState;
import com.maimaizu.citypicker.utils.StringUtils;
import com.maimaizu.citypicker.view.SideLetterBar;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Author Bro0cL on 2016/12/16.
 */
public class CityPickerActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String KEY_PICKED_CITY = "picked_city";

    private ListView mListView;
    private ListView mResultListView;
    private SideLetterBar mLetterBar;
    private EditText searchBox;
    private ImageView clearBtn;
    private ImageView backBtn;
    private ViewGroup emptyView;

    private CityListAdapter mCityAdapter;
    private ResultListAdapter mResultAdapter;
    private List<City> mAllCities;
    private DBManager dbManager;

    private AMapLocationClient mLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cp_activity_city_list);

        initData();
        initView();
        initLocation();
    }

    private void initLocation() {
        mLocationClient = new AMapLocationClient(this);
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        option.setNeedAddress(true);
        option.setOnceLocation(true);
        mLocationClient.setLocationOption(option);
        mLocationClient.setLocationListener(new mMapLoacitonListener(mCityAdapter));
        mLocationClient.startLocation();
    }
    public static class mMapLoacitonListener implements AMapLocationListener {
        private CityListAdapter mCityAdapter;
        public mMapLoacitonListener(CityListAdapter adapter) {
            super();
            this.mCityAdapter=adapter;
        }

        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    String city = aMapLocation.getCity();
                    String district = aMapLocation.getDistrict();
                    String location = StringUtils.extractLocation(city, district);
                    mCityAdapter.updateLocateState(LocateState.SUCCESS, location);
                } else {
                    //定位失败
                    mCityAdapter.updateLocateState(LocateState.FAILED, null);
                }
            }
        }
    }

    private void initData() {
        dbManager = new DBManager(this);
//        dbManager.copyDBFile();
//        mAllCities = dbManager.getAllCities();
        SetCitys();
        if(mAllCities!=null){
            mCityAdapter = new CityListAdapter(mAllCities);
            mCityAdapter.setOnCityClickListener(new mCityClickListener(this,mCityAdapter,mLocationClient));

            mResultAdapter = new ResultListAdapter(this, null);
        }

    }
public static  class  mCityClickListener implements CityListAdapter.OnCityClickListener{
    private WeakReference<Activity> w;
    private CityListAdapter mCityAdapter;
    private AMapLocationClient mLocationClient;
    public mCityClickListener(Activity activity, CityListAdapter mCityAdapter,AMapLocationClient mLocationClient) {
        super();
        w=new WeakReference<Activity>(activity);
        this.mCityAdapter=mCityAdapter;
        this.mLocationClient=mLocationClient;
    }

    @Override
        public void onCityClick(String name, String id) {
            back(w.get(),name,id);
        }

        @Override
        public void onLocateClick() {
            mCityAdapter.updateLocateState(LocateState.LOCATING, null);
            mLocationClient.startLocation();
        }
    }


    private void SetCitys() {
        if(!mApplication.citys.equals("")){
            mAllCities=new ArrayList<>();
            try {
                JSONArray jsonArray=new JSONArray(mApplication.citys);
                CharacterParser characterParser=CharacterParser.getInstance();
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject jsonObject= (JSONObject) jsonArray.get(i);
                    String city=jsonObject.getString("name");
                    String pinyin=characterParser.getSelling(city);
                    String id=jsonObject.getString("id");
                    City city1=new City();
                    city1.setId(id);
                    city1.setName(city);
                    city1.setPinyin(pinyin);
                    mAllCities.add(city1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else{
            OkGo.post(Constants.getCitys).params("key", Constants.safeKey)
                    .params("m_id",Constants.M_id).execute(new StringCallback() {
                @Override
                public void onSuccess(String s, Call call, Response response) {
                    EventBus.getDefault().post(s);
                    mApplication.citys=s;
                    initData();
                }
            });
        }
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.listview_all_city);
        mListView.setAdapter(mCityAdapter);

        TextView overlay = (TextView) findViewById(R.id.tv_letter_overlay);
        mLetterBar = (SideLetterBar) findViewById(R.id.side_letter_bar);
        mLetterBar.setOverlay(overlay);
        mLetterBar.setOnLetterChangedListener(new SideLetterBar.OnLetterChangedListener() {
            @Override
            public void onLetterChanged(String letter) {
                int position = mCityAdapter.getLetterPosition(letter);
                mListView.setSelection(position);
            }
        });

        searchBox = (EditText) findViewById(R.id.et_search);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString();
                if (TextUtils.isEmpty(keyword)) {
                    clearBtn.setVisibility(View.GONE);
                    emptyView.setVisibility(View.GONE);
                    mResultListView.setVisibility(View.GONE);
                } else {
                    clearBtn.setVisibility(View.VISIBLE);
                    mResultListView.setVisibility(View.VISIBLE);
                    List<City> result = dbManager.searchCity(keyword,mAllCities);
                    if (result == null || result.size() == 0) {
                        emptyView.setVisibility(View.VISIBLE);
                    } else {
                        emptyView.setVisibility(View.GONE);
                        mResultAdapter.changeData(result);
                    }
                }
            }
        });

        emptyView = (ViewGroup) findViewById(R.id.empty_view);
        mResultListView = (ListView) findViewById(R.id.listview_search_result);
        mResultListView.setAdapter(mResultAdapter);
        mResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                back(CityPickerActivity.this,mResultAdapter.getItem(position).getName(),mResultAdapter.getItem(position).getId());
            }
        });

        clearBtn = (ImageView) findViewById(R.id.iv_search_clear);
        backBtn = (ImageView) findViewById(R.id.back);

        clearBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
    }

    private static  void back(Activity context, String city, String id){

        Intent data = new Intent();
        data.putExtra(KEY_PICKED_CITY, city);
        mApplication.city=Integer.valueOf(id);
        context.setResult(RESULT_OK, data);
        EventBus.getDefault().post(id);
        context.finish();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.iv_search_clear) {
            searchBox.setText("");
            clearBtn.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
            mResultListView.setVisibility(View.GONE);
        } else if (i == R.id.back) {
            finish();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stopLocation();
    }
}
