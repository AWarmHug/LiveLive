package com.warm.livelive.base;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 作者：warm
 * 时间：2017-07-26 17:59
 * 描述：
 */

public class RxPresenter<V extends BaseView> extends BasePresenter<V> {
    /**
     * 管理所有添加进来的Disposable；
     */
    protected CompositeDisposable mCompositeDisposable = new CompositeDisposable();


    protected void addDisposable(Disposable disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = new CompositeDisposable();
        }
        mCompositeDisposable.add(disposable);
    }


    @Override
    public void detach() {
        super.detach();
        if (mCompositeDisposable != null) {
            mCompositeDisposable.clear();
        }
    }


}
