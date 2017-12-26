package com.warm.livelive.data.http;

import com.warm.livelive.data.bean.LiveRoom;
import com.warm.livelive.data.bean.SubChannel;
import com.warm.livelive.data.http.api.LiveApis;
import com.warm.livelive.data.http.retrofit.RetrofitHelper;
import com.warm.livelive.utils.rx.RxUtils;

import java.util.List;

import io.reactivex.Observable;

/**
 * 作者：warm
 * 时间：2017-12-25 17:11
 * 描述：
 */

public class HttpHelperImpl implements HttpHelper {

    private static final HttpHelperImpl ourInstance = new HttpHelperImpl();

    public static HttpHelperImpl newInstance() {
        return ourInstance;
    }

    private LiveApis mLiveApis;


    public HttpHelperImpl() {
        mLiveApis = RetrofitHelper.provideApi(LiveApis.class);
    }

    @Override
    public Observable<List<SubChannel>> getSubChannel(String shortName) {
        return mLiveApis.getSubChannel(shortName).compose(RxUtils.<List<SubChannel>>handleResult());
    }

    @Override
    public Observable<List<LiveRoom>> getLiveRooms(String tagId, int page) {
        return mLiveApis.getLiveRooms(tagId, 20, page * 20).compose(RxUtils.<List<LiveRoom>>handleResult());
    }
}
