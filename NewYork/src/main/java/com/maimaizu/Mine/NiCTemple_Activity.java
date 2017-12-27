package com.maimaizu.Mine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.maimaizu.R;
import com.maimaizu.Utils.AnalyticalJSON;
import com.maimaizu.Utils.Constants;
import com.maimaizu.Utils.StatusBarCompat;
import com.maimaizu.Utils.mApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NiCTemple_Activity extends AppCompatActivity implements View.OnClickListener{
private TextView mtvtitle;
    private EditText medit;
    private String title;
    private Intent intent;
//    private Spinner mspinner;  //寺庙的下拉列表
    private RelativeLayout mrelativelayout; //昵称的RelativeLayout
   private String word;  //Edittext的值
//    private String templename;  //Spinner选中的寺庙
    private String httpcanshu; //请求的参数
    private String httpjk;  //请求的接口名
    private SharedPreferences sp;
    private List<HashMap<String, String>> names=new ArrayList<>();
//    private Hodler hodler;
//    private BaseAdapter adapter=new BaseAdapter() {
//        @Override
//        public int getCount() {
//            return names.size() > 0 ? names.size() : 0;
//        }
//        @Override
//        public Object getItem(int i) {
//            return names.get(i);
//        }
//
//        @Override
//        public long getItemId(int i) {
//            return i;
//        }
//
//        @Override
//        public View getView(int i, View view, ViewGroup viewGroup) {
//            if(view==null){
//                hodler=new Hodler();
//                view= LayoutInflater.from(NiCTemple_Activity.this).inflate(R.layout.temple_itme,null);
//                hodler.mtvtemplename=(TextView) view.findViewById(R.id.temple_itme_tv);
//                view.setTag(hodler);
//            }else {
//                hodler=(Hodler) view.getTag();
//            }
//            hodler.mtvtemplename.setText(names.get(i).get("te_name"));
//            return view;
//        }
//    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, ContextCompat.getColor(this, R.color.main_color));
        setContentView(R.layout.activity_ni_cqian_mhua_t_);
        intent=getIntent();
        sp=getSharedPreferences("user",MODE_PRIVATE);
        mtvtitle=(TextView) findViewById(R.id.ncqmht_title);
        medit=(EditText) findViewById(R.id.ncqmht_edittext);
//        mspinner=(Spinner)findViewById(R.id.spinner_temple);
        mrelativelayout=(RelativeLayout)findViewById(R.id.ncqmht_relativelayout_nc);
        title= getIntent().getStringExtra("title");
        if(title.equals("昵称")){
            httpcanshu="pet_name";
            mrelativelayout.setVisibility(View.VISIBLE);
            httpjk= Constants.User_info_xiugainc;
            medit.setText(sp.getString("pet_name",""));
        }
        mtvtitle.setText(mApplication.ST(title));
        ((TextView) findViewById(R.id.ncqmht_baochun)).setText(mApplication.ST("保存"));
    }
    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.ncqmht_baochun:  //保存
                if(title.equals("昵称")){
                    if(medit.getText().toString().equals("")){
                        Toast.makeText(NiCTemple_Activity.this,mApplication.ST("请输入保存内容"),Toast.LENGTH_SHORT).show();
                    }
                    else {
                        word=medit.getText().toString();    //保存的内容
                        modification() ; //修改昵称或寺庙的方法
                    }
                    Intent intent=new Intent("Mine");

                    sendBroadcast(intent);

                }
                break;
            case R.id.ncqmht_back:
                finish();
                break;
            case R.id.ncqmht_qingchu:  //清除
                medit.setText("");
                Toast.makeText(NiCTemple_Activity.this,mApplication.ST("清除成功"),Toast.LENGTH_SHORT).show();
                break;
        }
    }
    public void modification(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String data = OkGo.post(httpjk)
                            .params("user_id",sp.getString("user_id","") )
                            .params(httpcanshu, word)
                            .params("key",Constants.safeKey)
                            .execute().body().string();
                    Log.d("ggggggggg",data);
                    HashMap<String,String> retur= AnalyticalJSON.getHashMap(data);
                    Log.d("hhhhhhhhhh",retur.toString());
                   if (retur.get("code").equals("000")){
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               Toast.makeText(NiCTemple_Activity.this,mApplication.ST("修改信息成功"),Toast.LENGTH_SHORT).show();
                               intent.putExtra("edit",word);
                               NiCTemple_Activity.this.setResult(2,intent);
                                finish();
                           }
                       });
                   }else {
                       runOnUiThread(new Runnable() {
                           @Override
                           public void run() {
                               Toast.makeText(NiCTemple_Activity.this,mApplication.ST("修改信息失败"),Toast.LENGTH_SHORT).show();
                           }
                       });
                   }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
