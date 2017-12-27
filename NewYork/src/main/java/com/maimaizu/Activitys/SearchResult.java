package com.maimaizu.Activitys;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.MenuPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.HttpParams;
import com.maimaizu.Adapter.mBaseAdapter;
import com.maimaizu.Base.BaseActivity;
import com.maimaizu.Fragments.search_fragments.fragment_zongjia;
import com.maimaizu.Fragments.search_fragments.onSelectedListener;
import com.maimaizu.R;
import com.maimaizu.Utils.Constants;
import com.maimaizu.Utils.DimenUtils;
import com.maimaizu.Utils.mApplication;
import com.maimaizu.View.mItemDecoration;
import com.yyydjk.library.DropDownMenu;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/4/30.
 */

public class SearchResult extends BaseActivity implements View.OnClickListener, onSelectedListener,SwipeRefreshLayout.OnRefreshListener {
    private static final int QUERY = 0;
    private static final int JIAGE = 1;
    private static final int FANGXING = 2;
    private static  final int FENLEI=3;
   private boolean isFenLei=false;

    private MenuPopupWindow.MenuDropDownListView menuDropDownListView;
    private String headers[];
    private List<View> popupviews;
    private ImageView back, search;
    private DropDownMenu dropDownMenu;

    private SwipeRefreshLayout swip;
    private RecyclerView result;//内容view
    private String content;
    private int type;
    private String money1 = "";
    private String money2 = "";
    private String housetype = "";

    private int  page = 1;
    private int endPage=-1;
    private static final int PAGESIZE = 10;
    private boolean isLoadMore = false;//是否是加载更多
    private boolean isRefresh = false;//是否是刷新加载
    private mBaseAdapter adapter;
    private int currentType=-1;
    private ArrayList<mBaseAdapter.OneMulitem> oneMulitems;
    private TextView loadNothing;

    @Override
    public int getLayoutId() {
        return R.layout.search_result;
    }

    @Override
    public void initView() {
        back = (ImageView) findViewById(R.id.back);
        search = (ImageView) findViewById(R.id.search);
        dropDownMenu = (DropDownMenu) findViewById(R.id.dropDownMenu);
        popupviews = new ArrayList<>();
        type = getIntent().getIntExtra("type", 1);
        isFenLei=getIntent().getBooleanExtra("flag",false);
        if(isFenLei){
            currentType=FENLEI;
            TextView title= (TextView) findViewById(R.id.title);
            switch (type){
                case 1:
                    title.setText("新房");
                    break;
                case 2:
                    title.setText("二手房");
                    break;
                case 3:
                    title.setText("租房");
                    break;
            }
        }else{
            currentType=QUERY;
        }
        content = getIntent().getStringExtra("content");
        oneMulitems = new ArrayList<>();
        adapter = new mBaseAdapter(this, oneMulitems);
        loadNothing = new TextView(this);
        loadNothing.setText(mApplication.ST("未搜索到相关房源\n下拉刷新"));
        Drawable drawable = ActivityCompat.getDrawable(this, R.drawable.load_nothing);
        drawable.setBounds(0, 0, DimenUtils.dip2px(this, 120), DimenUtils.dip2px(this, 120));
        loadNothing.setCompoundDrawables(null, drawable, null, null);
        loadNothing.setGravity(Gravity.CENTER_HORIZONTAL);
        loadNothing.setPadding(0, DimenUtils.dip2px(this, 20), 0, 0);
        ViewGroup.LayoutParams vl = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadNothing.setLayoutParams(vl);
        adapter.setEmptyView(loadNothing);
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_RIGHT);

    }

    @Override
    public void setOnClick() {
        back.setOnClickListener(this);
        search.setOnClickListener(this);
        adapter.openLoadMore(PAGESIZE, true);
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                result.post(new Runnable() {
                    @Override
                    public void run() {
                        if(endPage!=page){
                            isLoadMore = true;
                            page++;
                            getData(currentType);
                        }
                    }

                });
            }
        });
        adapter.setOnRecyclerViewItemClickListener(new BaseQuickAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                int type=Integer.valueOf(((mBaseAdapter.OneMulitem) adapter.getItem(i)).getItemType());
                switch (type){
                    case 1:
                        Intent intent1=new Intent(SearchResult.this, NewHouseActivity.class);
                        intent1.putExtra("id", ((mBaseAdapter.OneMulitem) adapter.getItem(i)).getId());
                        startActivity(intent1);
                        break;
                    case 2:
                        Intent intent=new Intent(SearchResult.this, Home2_Detail.class);
                        intent.putExtra("id", ((mBaseAdapter.OneMulitem) adapter.getItem(i)).getId());
                        startActivity(intent);
                        break;
                    case 3:
                        Intent intent2=new Intent(SearchResult.this, ZuFangActivity.class);
                        intent2.putExtra("id", ((mBaseAdapter.OneMulitem) adapter.getItem(i)).getId());
                        startActivity(intent2);
                        break;
                }
            }
        });
    }

    @Override
    public boolean setEventBus() {
        return false;
    }

    @Override
    public void doThings() {

        LayoutInflater inflater = LayoutInflater.from(this);
        View l=inflater.inflate(R.layout.swip_recycler,null);
        result= (RecyclerView) l.findViewById(R.id.recycle);
        swip= (SwipeRefreshLayout) l.findViewById(R.id.swip);
        swip.setColorSchemeResources(R.color.main_color);
        swip.setOnRefreshListener(this);
        result.setLayoutManager(new LinearLayoutManager(this));
        result.addItemDecoration(new mItemDecoration(this));
        result.setAdapter(adapter);
//        View quyu = inflater.inflate(R.layout.view_quyu, null);
        View zongjia = inflater.inflate(R.layout.view_zongjia, null);
        View fangxing = inflater.inflate(R.layout.view_fangxing, null);
//        View gengduo = inflater.inflate(R.layout.view_gengduo, null);
//        popupviews.add(quyu);
        popupviews.add(zongjia);
        popupviews.add(fangxing);
//        popupviews.add(gengduo);
        if(type==3){
            fragment_zongjia.ZongjiaType zongjiaType=new fragment_zongjia.ZongjiaType();
            zongjiaType.setZulin(true);
            EventBus.getDefault().post(zongjiaType);
            headers= new String[]{"租金", "房型"};
        }else{
            headers= new String[]{ "价格", "房型",};
        }

        dropDownMenu.setDropDownMenu(Arrays.asList(headers), popupviews, l);

        isLoadMore=false;
        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                onRefresh();
            }
        });



    }

    // TODO: 2017/4/30 搜索
    private void getData(int query) {
        String url = "";
        HttpParams httpParams = new HttpParams();
        switch (query) {
            case QUERY:
                url = Constants.Query;
                httpParams.put("content", content);
                break;
            case JIAGE:
                url = Constants.JiaGeQuery;
                httpParams.put("money1", money1);
                httpParams.put("money2", money2);
                break;
            case FANGXING:
                url = Constants.FangXingQuery;
                httpParams.put("housetype", housetype);
                break;
            case FENLEI:
                url=Constants.getHouses;
                break;

        }
        httpParams.put("city", mApplication.city);
        httpParams.put("type", String.valueOf(type));
        httpParams.put("page", page);
        OkGo.post(url).tag(this).params("key", Constants.safeKey)
                .params("m_id", Constants.M_id)
                .params(httpParams).execute(new AbsCallback<ArrayList<mBaseAdapter.OneMulitem>>() {
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
                if (isRefresh) {
                    adapter.setNewData(list);
                    isRefresh = false;
                    swip.setRefreshing(false);
                } else if(isLoadMore){
                    if(list.size()<PAGESIZE){
                        endPage=page;
                        adapter.notifyDataChangedAfterLoadMore(list,false);
                    }else{
                        adapter.notifyDataChangedAfterLoadMore(list,true);
                    }
                    isLoadMore=false;
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelTag(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent=new Intent();
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.search:
                intent.setClass(this,Search.class);
                startActivity(intent);
                break;

        }
    }

    @Override
    public void onBackPressed() {
        if (dropDownMenu.isShowing()) {
            dropDownMenu.closeMenu();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public void onQuYuSelected(Object o) {

    }

    @Override
    public void onZongJiaSelected(Object o, String money1, String money2) {
        currentType=JIAGE;
        isLoadMore=false;
        page=1;
        endPage=-1;
        dropDownMenu.setTabText(o.toString());
        dropDownMenu.closeMenu();
        this.money1 = money1;
        this.money2 = money2;
        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                onRefresh();
            }
        });
    }

    @Override
    public void onFangXingSelected(Object o) {
        currentType=FANGXING;
        isLoadMore=false;
        page=1;
        endPage=-1;
        housetype = o.toString();
        dropDownMenu.setTabText(housetype);
        dropDownMenu.closeMenu();
        swip.post(new Runnable() {
            @Override
            public void run() {
                swip.setRefreshing(true);
                onRefresh();
            }
        });
    }

    @Override
    public void onGengDuoSelected(Object o) {

    }

    @Override
    public void onRefresh() {
        page=1;
        endPage=-1;
        isRefresh=true;
        getData(currentType);
    }
}
