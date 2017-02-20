package com.fuicuiedu.xc.mvp_20170213.mosby;

import com.hannesdorfmann.mosby.mvp.MvpView;


public interface MyView extends MvpView{

    void showPrb();

    void hidePrb();

    void showMsg(String msg);

}
