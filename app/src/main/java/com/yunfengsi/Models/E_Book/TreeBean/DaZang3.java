package com.yunfengsi.Models.E_Book.TreeBean;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * 作者：因陀罗网 on 2018/3/15 15:38
 * 公司：成都因陀罗网络科技有限公司
 */

public class DaZang3 extends AbstractExpandableItem<DaZang4>implements MultiItemEntity {
    private String id;
    private String title;
    private boolean isLoaded=false;

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override

    public int getLevel() {
        return 2;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    @Override
    public int getItemType() {
        return 2;
    }
}
