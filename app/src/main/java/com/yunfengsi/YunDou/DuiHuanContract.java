package com.yunfengsi.YunDou;

import android.support.v4.app.FragmentManager;

/**
 * 作者：因陀罗网 on 2018/4/20 13:23
 * 公司：成都因陀罗网络科技有限公司
 */
public class DuiHuanContract {

    //兑换中心view
    public interface IView {
        FragmentManager getIFragmentManager();//

        void showTabs(tabPagerAdapter adapter);//显示tabs

        void onNetWorkBefore();//提示

        void onNetWorkAfter();//关闭提示
    }

    //兑换中心presenter
    public interface IPresenter {
        void getTitles();//获取分类名称
    }

    //兑换中心子fragment   presenter
    public interface IFPresenter {
        void getDuiHuanList();//获取分类列表

    }

    //兑换中心子fragment   view
    public interface IFView {
        String getSortId();//获取分类id

        void hideSwip();//隐藏swip

        QuanFragment.MessageAdapter getAdapter();//获取列表adpter
    }


    public interface  MyQuanView{
        void hideSwip();//隐藏swip
        MyQuan.MessageAdapter getAdapter();//获取列表adpter
    }
    public interface  MyQuanPresenter{
        void getDuiHuanList();//获取我的兑换券列表
    }


}
