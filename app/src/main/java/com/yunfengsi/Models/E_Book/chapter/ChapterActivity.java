package com.yunfengsi.Models.E_Book.chapter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.yunfengsi.Models.E_Book.PageFactory;
import com.yunfengsi.Models.E_Book.TreeBean.Chapter;
import com.yunfengsi.R;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.StatusBarCompat;

import java.util.List;


/**
 * Created by will on 2016/11/6.
 */

public class ChapterActivity extends AppCompatActivity implements ChapterFactory.LoadCallback{
    private ProgressDialog progressDialog;
    private ChapterAdapter mAdapter;
    private ChapterFactory chapterFactory;
    private FastScrollRecyclerView recyclerView;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
//        setTheme(SPHelper.getInstance().isNightMode() ? R.style.AppNightTheme : R.style.AppDayTheme);
        StatusBarCompat.compat(this, getResources().getColor(R.color.main_color));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);
        recyclerView = (FastScrollRecyclerView) findViewById(R.id.chapter_activity_recycler_view);

        mAdapter = new ChapterAdapter(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        chapterFactory = new ChapterFactory();
        List<Chapter> data = chapterFactory.getChapterFromDB();
        if(data.size() > 0){
            mAdapter.addData(data);
            int chapterNumber = getChapterNumber(PageFactory.getInstance().getCurrentEnd(),data);
            mAdapter.setCurrentChapter(chapterNumber);
            mAdapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(chapterNumber);
        }else{
            loadChapters();
            LogUtil.e("未发现章节");
        }

        mAdapter.setOnItemClickListener(new ChapterAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Chapter chapter) {
                Intent intent = new Intent();
                intent.putExtra("position",chapter.getChapterBytePosition());
                setResult(RESULT_OK,intent);
                finish();
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.chapter_activity_toolbar);
        toolbar.setTitle(PageFactory.getInstance().getBook().getBookName());
//        toolbar.setNavigationIcon(R.drawable.arrow_back_holo_dark_no_trim_no_padding);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void showDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100);
        progressDialog.setProgress(0);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("正在加载章节中...");
        progressDialog.show();
        progressDialog.setCancelable(false);
    }

    private void loadChapters(){
        mAdapter.clearData();
        mAdapter.notifyDataSetChanged();
        showDialog();
        chapterFactory.setProgressCallback(new ChapterFactory.ProgressCallback() {
            @Override
            public void currentPercentage(int percent) {
                if(progressDialog.getProgress() != percent){
                    progressDialog.setProgress(percent);
                    progressDialog.setMessage("正在加载章节中...");
                }
            }
        });
        chapterFactory.getChapterFromFile(this);
    }

    @Override
    public void onFinishLoad(List<Chapter> list) {
        int chapterNumber = getChapterNumber(PageFactory.getInstance().getCurrentEnd(),list);
        mAdapter.setCurrentChapter(chapterNumber);
        mAdapter.clearData();
        mAdapter.addData(list);
        recyclerView.scrollToPosition(chapterNumber);
        progressDialog.dismiss();
    }

    @Override
    public void onNotFound() {
       LogUtil.e("未发现章节");
        progressDialog.dismiss();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.chapter_menu,menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        String keyword = "";
//        switch (item.getItemId()){
//            case R.id.chapter_menu_search_by_zhang:
//                keyword = ChapterFactory.KEYWORD_ZHANG;
//                break;
//            case R.id.chapter_menu_search_by_jie:
//                keyword = ChapterFactory.KEYWORD_JIE;
//                break;
//            case R.id.chapter_menu_search_by_hui:
//                keyword = ChapterFactory.KEYWORD_HUI;
//                break;
//        }
//        loadChapters(keyword);
//        return true;
//    }
    private int getChapterNumber(int position,List<Chapter> list){
        position -= 2;//因为在获取章节byte位置时往前了一字节，同时position指向的是下一未读字节，故这里回退两个字节
        int begin = 0;
        int end = list.size()-1;
        while (begin <= end){
            int middle = begin + (end-begin)/2;
            if(middle == 0 && list.get(middle).getChapterBytePosition() >= position){
                return 0;
            }
            if(middle == list.size()-1 && list.get(list.size()-1).getChapterBytePosition() <= position){
                return list.size()-1;
            }
            if(list.get(middle).getChapterBytePosition() <= position  && list.get(middle+1).getChapterBytePosition() > position){
                return middle;
            }else if (list.get(middle).getChapterBytePosition() > position && list.get(middle-1).getChapterBytePosition() <= position){
                return middle -1;
            }else if(list.get(middle).getChapterBytePosition() < position && list.get(middle+1).getChapterBytePosition() < position){
                 begin = middle+1;
            }else if(list.get(middle).getChapterBytePosition() > position && list.get(middle-1).getChapterBytePosition() > position){
                end = middle-1;
            }
        }
        return 0;
    }
}
