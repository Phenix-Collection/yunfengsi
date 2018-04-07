package com.yunfengsi.View;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.yunfengsi.E_Book.PageFactory;
import com.yunfengsi.R;
import com.yunfengsi.Utils.LogUtil;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * 作者：因陀罗网 on 2018/3/29 15:58
 * 公司：成都因陀罗网络科技有限公司
 */
public class ReadTextView extends TextView{
    private Context context;
    private  PageFactory pageFactory;
    private TextView progress;
    private long downTime=0;
    private WindowListener windowListener;
    private boolean isOpened=false;
    public MyActionModeCallback modeCallback;
    public ReadTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setFreezesText(true);
        this.context=context;
        modeCallback=new MyActionModeCallback(this);
        setCustomSelectionActionModeCallback(modeCallback);
    }



    public void setWindowListener(WindowListener windowListener) {
        this.windowListener = windowListener;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }

    public PageFactory getPageFactory() {
        return pageFactory;
    }

    public void setPageFactory(PageFactory pageFactory) {
        this.pageFactory = pageFactory;
    }

    public TextView getProgress() {
        return progress;
    }

    public void setProgress(TextView progress) {
        this.progress = progress;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int width=getWidth();
        int x= (int) event.getX();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downTime=System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_UP:
                if(isOpened){
                    isOpened=!isOpened;
                    windowListener.handleWindow(isOpened);

                }else{
                    if((System.currentTimeMillis()-downTime)<400){
                        if(x<width/3){
                            pageFactory.pageUp();
                        }else if(x>width*2/3){
                            pageFactory.pageDown();
                        }else{
                            // TODO: 2018/3/29 打开弹窗
                            LogUtil.e("当前开始：："+pageFactory.getCurrentBegin()+"   当前结束::"+pageFactory.getCurrentEnd());
                            isOpened=!isOpened;
                            windowListener.handleWindow(isOpened);
                        }
                        progress.setText((pageFactory.getCurrentEnd()*100/pageFactory.getFileLength())+"%");
                    }
                }

                break;
        }

        return super.onTouchEvent(event);
    }


    public interface  WindowListener{
        void handleWindow(boolean isOpened);
    }

    public static class MyActionModeCallback implements ActionMode.Callback {
//        private Menu mMenu;
            private TextView textView;
//            private Context context;
            private PerformJiuCuoListener listener;
            private String content;
        public MyActionModeCallback(TextView textView) {
            super();
            this.textView=textView;
//            this.context=context;

        }
        public void setListener(MyActionModeCallback.PerformJiuCuoListener listener) {
            this.listener = listener;
        }
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            MenuInflater menuInflater = actionMode.getMenuInflater();
            menuInflater.inflate(R.menu.read_book,menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            //菜单创建完成以后获取到其对象，便于后续操作
//            this.mMenu=menu;
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()){
//                case R.id.it_all:
//                    //全选
//                    mTvSelect.selectAll();
//                    Toast.makeText(mContext, "完成全选", Toast.LENGTH_SHORT).show();
//                    break;
//                case R.id.it_copy:
//                    String selectText = getSelectText(context,textView,SelectMode.COPY);
//                    //setText(selectText)是为了后面的this.mMenu.close()起作用
//                    Toast.makeText(context, "文字已复制到剪切板", Toast.LENGTH_SHORT).show();
////                    textView.setText(textView.getText().toString());
////                    this.mMenu.close();
//                    actionMode.finish();
//                    break;
                case R.id.it_jiucuo:
                    //剪切
                    int selectionStart = textView.getSelectionStart();
                    int selectionEnd = textView.getSelectionEnd();
                    LogUtil.e("selectionStart="+selectionStart+",selectionEnd="+selectionEnd);
                    //截取选中的文本
                    String txt =textView.getText().toString();
                    String substring = txt.substring(selectionStart, selectionEnd);
                    listener.onJiuPerformed(substring);
//                    textView.setText(textView.getText().toString());
//                    this.mMenu.close();
                    actionMode.finish();
                    break;

//                case R.id.it_paste:
//                    //获取剪切班管理者
//                    ClipboardManager cbs = (ClipboardManager) mContext.getSystemService(CLIPBOARD_SERVICE);
//                    if (cbs.hasPrimaryClip()){
//                        mTvSelect.setText(cbs.getPrimaryClip().getItemAt(0).getText());
//                    }
//                    this.mMenu.close();
//                    break;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {

        }
        public interface PerformJiuCuoListener{
           void  onJiuPerformed(String content);

        }
    }

    /**
     *  统一处理复制和剪切的操作
     * @param mode 用来区别是复制还是剪切
     * @return
     */
    private static String getSelectText(Context context,TextView textView,SelectMode mode) {
        //获取剪切班管理者
        ClipboardManager cbs = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        //获取选中的起始位置
        int selectionStart = textView.getSelectionStart();
        int selectionEnd = textView.getSelectionEnd();
        LogUtil.e("selectionStart="+selectionStart+",selectionEnd="+selectionEnd);
        //截取选中的文本
        String txt =textView.getText().toString();
        String substring = txt.substring(selectionStart, selectionEnd);
        LogUtil.e("substring="+substring);

        //将选中的文本放到剪切板
        cbs.setPrimaryClip(ClipData.newPlainText(null,substring));
        //如果是复制就不往下操作了
        if (mode==SelectMode.COPY)
            return txt;
        txt = txt.replace(substring, "");
        return txt;
    }

    /**
     * 用枚举来区分是复制还是剪切
     */
    public enum SelectMode{
        COPY,CUT;
    }

}
