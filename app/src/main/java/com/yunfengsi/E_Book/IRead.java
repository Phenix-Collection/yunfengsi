package com.yunfengsi.E_Book;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.yunfengsi.E_Book.TreeBean.Book;
import com.yunfengsi.E_Book.chapter.ChapterActivity;
import com.yunfengsi.R;
import com.yunfengsi.Utils.AnalyticalJSON;
import com.yunfengsi.Utils.ApisSeUtil;
import com.yunfengsi.Utils.Constants;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.LoginUtil;
import com.yunfengsi.Utils.PreferenceUtil;
import com.yunfengsi.Utils.ProgressUtil;
import com.yunfengsi.Utils.ShareManager;
import com.yunfengsi.Utils.StatusBarCompat;
import com.yunfengsi.Utils.ToastUtil;
import com.yunfengsi.Utils.mApplication;
import com.yunfengsi.View.ReadTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

public class IRead extends AppCompatActivity implements ReadTextView.WindowListener, View.OnClickListener,ReadTextView.MyActionModeCallback.PerformJiuCuoListener {
    MappedByteBuffer mappedByteBuffer;
    RandomAccessFile randomAccessFile;

    ReadTextView page;
    TextView progress;
    PageFactory pageFactory;

    private RelativeLayout head, bottom;
    private ImageView back;
    private TextView txt_chapter, delete, size, add, day, night;
    private ImageView share;
    private int type=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        setContentView(R.layout.activity_iread);
        share=findViewById(R.id.share);
        share.setOnClickListener(this);
        page = findViewById(R.id.read);
        progress = findViewById(R.id.progress);
        head = findViewById(R.id.head);
        bottom = findViewById(R.id.bottom);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        txt_chapter = findViewById(R.id.chapters);
        delete = findViewById(R.id.delete);
        size = findViewById(R.id.textsize);
        add = findViewById(R.id.add);
        day = findViewById(R.id.day);
        night = findViewById(R.id.night);

        txt_chapter.setOnClickListener(this);
        delete.setOnClickListener(this);
        add.setOnClickListener(this);
        day.setOnClickListener(this);
        night.setOnClickListener(this);


//        File file = new File(getExternalFilesDir(getPackageName()), "金刚经.txt");
//        File file=new File(Environment.getExternalStorageDirectory(),"test.txt");
        Book book = (Book) getIntent().getSerializableExtra("book");
        pageFactory = PageFactory.getInstance(page, progress, book);
        page.modeCallback.setListener(this);
        page.setPageFactory(pageFactory);
        page.setProgress(progress);
        page.setWindowListener(this);
        size.setText(pageFactory.getFontSize() + "");
        if (SPHelper.getInstance().isNightMode()) {
            night.performClick();
        } else {
            day.performClick();
        }
        pageFactory.setPosition(SPHelper.getInstance().getBookmarkStart(book.getBookName()));
        type=getIntent().getIntExtra("type",1);

    }

    @Override
    public void handleWindow(boolean isOpened) {
        if (isOpened) {
            head.animate().translationY(head.getHeight()).setDuration(400).start();
            bottom.animate().translationY(-DimenUtils.dip2px(this, 160)).setDuration(400).start();
        } else {
            head.animate().translationY(-head.getHeight()).setDuration(400).start();
            bottom.animate().translationY(DimenUtils.dip2px(this, 160)).setDuration(400).start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ProgressUtil.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share:
                String url=(type==2?"http://a.app.qq.com/o/simple.jsp?pkgname=com.yunfengsi": Constants.FX_host_Ip + "ebook" + "/id/" + getIntent().getStringExtra("id") + "/st/" + (mApplication.isChina ? "s" : "t"));
                UMWeb umWeb=new UMWeb(url);
                umWeb.setDescription("阅读佛经,明心见性,学习佛家的大智慧和大爱。这里有大量的经书,一起来阅读吧!");
                umWeb.setThumb(new UMImage(this,R.drawable.indra_share));
                umWeb.setTitle("我正在阅读"+(type==2?"《大藏经》":("《"+pageFactory.getBook().getBookName()+"》")));
                new ShareManager().shareWeb(umWeb,this);
                break;
            case R.id.chapters:
                Intent intent = new Intent(this, ChapterActivity.class);
                startActivityForResult(intent, 666);

                break;
            case R.id.back:
                finish();
                break;
            case R.id.delete:
                PageFactory.getInstance().decreaseFontSize();
                size.setText(pageFactory.getFontSize() + "");
                break;
            case R.id.add:

                PageFactory.getInstance().increaseFontSize();
                size.setText(pageFactory.getFontSize() + "");
                break;
            case R.id.day:
                v.setEnabled(false);
                night.setEnabled(true);
                page.setBackgroundColor(ContextCompat.getColor(this, R.color.lemonchiffon));
                page.setTextColor(ContextCompat.getColor(this, R.color.black));
                SPHelper.getInstance().setNightMode(false);
                break;
            case R.id.night:
                day.setEnabled(true);
                v.setEnabled(false);
                page.setBackgroundColor(Color.BLACK);
                page.setTextColor(ContextCompat.getColor(this, R.color.ivory));
                SPHelper.getInstance().setNightMode(true);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PageFactory.close();
        pageFactory.saveBookmark();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (666 == requestCode && resultCode == RESULT_OK && data != null) {
            PageFactory.getInstance().setPosition(data.getIntExtra("position", 1));
            //跳转章节后进度也会变化，在此处更新进度值


        }
    }
        //纠错
    @Override
    public void onJiuPerformed(String content) {
        if(new LoginUtil().checkLogin(this)){
            JSONObject js=new JSONObject();
            try {
                js.put("m_id",Constants.M_id);
                js.put("user_id", PreferenceUtil.getUserId(this));
                js.put("book_id",pageFactory.getBook().getId());
                js.put("content",content);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            LogUtil.e("纠错：："+js);
            ApisSeUtil.M m=ApisSeUtil.i(js);
            OkGo.post(Constants.JiuCuo).params("key",m.K())
                    .params("msg",m.M())
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            HashMap<String,String > map= AnalyticalJSON.getHashMap(s);
                            if(map!=null){
                                if("000".equals(map.get("code"))){
                                    ToastUtil.showToastShort("感谢您的反馈，我们会尽快处理书中的错误内容");
                                }else{
                                    ToastUtil.showToastShort("提交失败，请稍后尝试");
                                }
                            }
                        }

                        @Override
                        public void onBefore(BaseRequest request) {
                            super.onBefore(request);
                            ProgressUtil.show(IRead.this,"","正在提交");

                        }

                        @Override
                        public void onAfter(String s, Exception e) {
                            super.onAfter(s, e);
                            ProgressUtil.dismiss();
                        }
                    });
        }


    }
}
