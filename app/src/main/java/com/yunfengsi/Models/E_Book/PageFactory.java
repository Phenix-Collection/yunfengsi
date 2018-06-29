package com.yunfengsi.Models.E_Book;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

import com.yunfengsi.Models.E_Book.TreeBean.Book;
import com.yunfengsi.Utils.DimenUtils;
import com.yunfengsi.Utils.LogUtil;
import com.yunfengsi.Utils.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * Created by Will on 2016/2/2.
 */
public class PageFactory {
    private int pageHeight;
    private int lineNumber;//行数
    private int fileLength;//映射到内存中Book的字节数
    private int fontSize;
    //    private static final int margin = DimenUtils.dip2px(mApplication.getInstance(),5);//文字显示距离屏幕实际尺寸的偏移量
    private int begin;//当前阅读的字节数_开始
    private int end;//当前阅读的字节数_结束
    private MappedByteBuffer mappedFile;//映射到内存中的文件
    private RandomAccessFile randomFile;//关闭Random流时使用

    private String encoding;
    private Context mContext;

    private SPHelper spHelper = SPHelper.getInstance();
    private boolean isNightMode = spHelper.isNightMode();
    private TextView mView, progress;
    private ArrayList<String> content = new ArrayList<>();
    private Book book;

    private static PageFactory instance;


    private String currentText = "";

    public static PageFactory getInstance(TextView view, TextView progress, Book book) {
        if (instance == null) {
            synchronized (PageFactory.class) {
                if (instance == null) {
                    instance = new PageFactory(view, progress);
                    instance.openBook(book);
                }
            }
        }
        return instance;
    }

    public static PageFactory getInstance() {
        return instance;
    }

    private PageFactory(TextView view, TextView progress) {
        DisplayMetrics metrics = new DisplayMetrics();
        mContext = view.getContext();
        mView = view;
        this.progress = progress;
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenHeight = metrics.heightPixels;
        int screenWidth  = metrics.widthPixels;
        fontSize = spHelper.getFontSize();
        mView.setTextSize(fontSize);
        pageHeight = screenHeight - DimenUtils.dip2px(mContext, 25);
        int pageWidth = screenWidth;
        lineNumber = pageHeight / mView.getLineHeight() - 1;


    }

    private void openBook(final Book book) {
        this.book = book;
        encoding = book.getEncoding();
        begin = spHelper.getBookmarkStart(book.getBookName());
        end = spHelper.getBookmarkEnd(book.getBookName());
        File file = new File(book.getPath());
        fileLength = (int) file.length();
        try {
            randomFile = new RandomAccessFile(file, "r");
            mappedFile = randomFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, (long) fileLength);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("打开失败！");
        }
        Log.e(book.getBookName(), encoding);
    }
    //向后读取一个段落，返回bytes

    private byte[] readParagraphForward(int end) {
        byte b0;
        int before = 0;
        int i = end;
        while (i < fileLength) {
            b0 = mappedFile.get(i);
            if (encoding.equals("UTF-16LE")) {
                if (b0 == 0 && before == 10) {
                    break;
                }
            } else {
                if (b0 == 10) {
                    break;
                }
            }
            before = b0;
            i++;
        }

        i = Math.min(fileLength - 1, i);
        int nParaSize = i - end + 1;

        byte[] buf = new byte[nParaSize];
        for (i = 0; i < nParaSize; i++) {
            buf[i] = mappedFile.get(end + i);
        }

        return buf;
    }

    //向前读取一个段落
    private byte[] readParagraphBack(int begin) {
        byte b0;
        byte before = 1;
        int i = begin - 1;
        while (i > 0) {
            b0 = mappedFile.get(i);
            if (encoding.equals("UTF-16LE")) {
                if (b0 == 10 && before == 0 && i != begin - 2) {
                    i += 2;
                    break;
                }
            } else {
                if (b0 == 0x0a && i != begin - 1) {
                    i++;
                    break;
                }
            }
            i--;
            before = b0;
        }
        int nParaSize = begin - i;
        byte[] buf = new byte[nParaSize];
        for (int j = 0; j < nParaSize; j++) {
            buf[j] = mappedFile.get(i + j);
        }
        return buf;

    }

    //获取后一页的内容
    public void pageDown() {

        if (end >= fileLength) {

            return;
        }

            begin=end;

        String strParagraph = "";
        int lines = 0;
        mView.setText("");
        currentText = "";
        LogUtil.e("当前页开始：：" + end + "   文本长度：：" + fileLength);
        while ((lines < lineNumber) && (end < fileLength)) {
            byte[] byteTemp = readParagraphForward(end);
            end += byteTemp.length;
            try {
                strParagraph = new String(byteTemp, encoding);//获取每段的文字
                strParagraph = strParagraph.replaceAll("\r\n", "\n");
                LogUtil.e("向后读取一段文字：：；" + strParagraph + "  当前占用：" + lines + "行，最大" + lineNumber + "行");
                mView.append(strParagraph);//设置文字
                lines = mView.getLineCount();//获取最新的已添加文字行数
                if (end >= fileLength) {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        currentText = mView.getText().toString();//所有文字；
        mView.post(new Runnable() {
            @Override
            public void run() {
                cutUselessString();
            }
        });


    }

    //上翻页
    public void pageUp() {

        if (begin <= 0) {
            begin = 0;
            ToastUtil.showToastShort("已经是第一页啦");
            return;
        }

            end=begin;


        String strParagraph = "";
        int lines = 0;
        mView.setText("");
        currentText = "";
        LogUtil.e("当前页结束下标：：" + end + "   文本长度：：" + fileLength);
        StringBuilder builder = new StringBuilder();
        while ((lines < lineNumber) && begin > 0) {
            byte[] byteTemp = readParagraphBack(begin);
            begin -= byteTemp.length;

            try {
                strParagraph = new String(byteTemp, encoding);
                builder.insert(0, strParagraph);
                LogUtil.e("向前读取一段文字：：；" + strParagraph + "  当前占用：" + lines + "行，最大" + lineNumber + "行");
                mView.setText(builder.toString());
                lines = mView.getLineCount();
                if (begin <= 0) {
                    begin = 0;
                    ToastUtil.showToastShort("已经是第一页啦");
                    break;
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
//
//            strParagraph = strParagraph.replaceAll("\n", "  ");
        currentText = mView.getText().toString();//所有文字；
//        int MaxChar = 0;
        int needCutNum = lines - lineNumber;
        if (lines > lineNumber) {
            try {
                begin += currentText.substring(0, mView.getLayout().getLineEnd(needCutNum - 1)).getBytes(encoding).length;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (needCutNum < 0) {
            needCutNum = 0;
        }
        currentText = currentText.substring(mView.getLayout().getLineStart(needCutNum));
        mView.setText(currentText);
        LogUtil.e("  当前所有文字字数：" + currentText.length());


    }


    private String getBatteryLevel() {
        Intent batteryIntent = mContext.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int scaledLevel = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        return "电量：" + String.valueOf(scaledLevel * 100 / scale);
    }

    public void nextPage() {
        if (end >= fileLength) {
            return;
        } else {
            content.clear();
            begin = end;
            pageDown();
        }
//        printPage();
    }

    public void prePage() {
        if (begin <= 0) {
            return;
        } else {
            content.clear();
            pageUp();
            end = begin;
            pageDown();
        }
//        printPage();
    }

    public void saveBookmark() {
        SPHelper.getInstance().setBookmarkEnd(book.getBookName(), end);
        SPHelper.getInstance().setBookmarkStart(book.getBookName(), begin);
    }

    public void setFontSize(int size) {
//        if(size < 15){
//            return;
//        }
//        fontSize = size;
//        mPaint.setTextSize(fontSize);
//        pageHeight =  screenHeight - margin*2 - fontSize;
//        lineNumber = pageHeight/(fontSize+lineSpace);
//        end = begin;
//        nextPage();
        if (size < 15) {
            ToastUtil.showToastShort("字体过小会影响阅读哦~");
            return;
        }
        if (size > 60) {
            ToastUtil.showToastShort("字体过大~");
            return;
        }
        fontSize = size;
        mView.setTextSize(size);
        mView.post(new Runnable() {
            @Override
            public void run() {
                lineNumber = pageHeight / mView.getLineHeight() - 1;
                int line = mView.getLineCount();
                LogUtil.e("当前行数：："+line);
                if (line > lineNumber) {
                    cutUselessString();
                } else if (line < lineNumber) {
                    while (line < lineNumber && end < fileLength) {
                        byte[] byteTemp = readParagraphForward(end);
                        end += byteTemp.length;
                        try {
                            String strParagraph = new String(byteTemp, encoding);//获取每段的文字
                            mView.append(strParagraph);//设置文字
                            currentText=mView.getText().toString();
                            line = mView.getLineCount();//获取最新的已添加文字行数
                            if (end >= fileLength) {
                                end = fileLength - 1;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (line > lineNumber) {
                        cutUselessString();
                    }
                }
                progress.setText((getCurrentBegin() * 100 / getFileLength()) + "%");
            }
        });



        SPHelper.getInstance().setFontSize(size);
    }

    private void cutUselessString() {

        int MaxChar = 0;
        try {
            MaxChar = mView.getLayout().getLineVisibleEnd(lineNumber - 1);//该页文字字数
            LogUtil.e("当前字数：；"+MaxChar);
        } catch (IndexOutOfBoundsException e) {
            LogUtil.e("最大字数：："+MaxChar);
            MaxChar = currentText.length();
        }


        if (MaxChar < currentText.length()) {
            String strParagraph = currentText.substring(MaxChar, currentText.length() - 1);//截取多余的文字
            if (strParagraph.length() > 0) {
                try {
                    end -= (strParagraph).getBytes(encoding).length;//重设该页字节结束位置
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            currentText=currentText.substring(0, MaxChar);
        }

        mView.setText(currentText);//设置适配文字

    }

    public void increaseFontSize() {
        setFontSize(fontSize + 1);
    }

    public void decreaseFontSize() {
        setFontSize(fontSize - 1);
    }

    public int getFontSize() {
        return fontSize;
    }

    public int getFileLength() {
        return fileLength;
    }

    public MappedByteBuffer getMappedFile() {
        return mappedFile;
    }

    public void setPosition(int position) {

        end = position;
        pageDown();
        if(getFileLength()>0){
            progress.setText((getCurrentBegin() * 100 / getFileLength()) + "%");
        }

    }

    public int getProgress() {
        return begin * 100 / fileLength;
    }

    public int setProgress(int i) {
        int origin = begin;
        end = fileLength * i / 100;
        if (end == fileLength) {
            end--;
        }
        if (end == 0) {
            nextPage();
        } else {
            nextPage();
            prePage();
            nextPage();
        }
        return origin;
    }


    public Book getBook() {
        return book;
    }

    public String getEncoding() {
        return encoding;
    }

    public int getCurrentEnd() {
        return end;
    }

    public int getCurrentBegin() {
        return begin;
    }

    public static void close() {
        if (instance != null) {
            try {
                instance.randomFile.close();
            } catch (IOException i) {
                i.printStackTrace();
            }
            instance = null;
        }
    }
}
