package com.warm.livelive.ui.livelist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;

import com.warm.livelive.R;
import com.warm.livelive.base.fragment.LazyMvpFragment;
import com.warm.livelive.data.bean.LiveRoom;
import com.warm.livelive.data.bean.SubChannel;
import com.warm.livelive.mvp.LiveRoomContract;
import com.warm.livelive.mvp.LiveRoomPresenter;
import com.warm.livelive.widget.recyadapter.GridItemDecoration;
import com.warm.livelive.widget.recyadapter.LoadRecycleView;

import java.util.List;

import butterknife.BindView;

/**
 * 作者：warm
 * 时间：2017-12-26 08:42
 * 描述：
 */

public class LiveRoomFragment extends LazyMvpFragment<LiveRoomPresenter> implements LiveRoomContract.View{

    private RoomsListAdapter mListAdapter;
    private SubChannel mSubChannel;

    private int page;


    public static LiveRoomFragment newInstance(SubChannel subChannel) {

        Bundle args = new Bundle();
        args.putParcelable("subChannel", subChannel);
        LiveRoomFragment fragment = new LiveRoomFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSubChannel = getArguments().getParcelable("subChannel");
    }

    @Override
    public LiveRoomPresenter initPresenter() {
        return new LiveRoomPresenter();
    }

    @Override
    protected void doInVisible() {

    }

    @Override
    protected void doFirstVisible() {
        mPresenter.getLiveRooms(mSubChannel.getTag_id(), page);
    }

    @Override
    protected void doVisible() {

    }

    @Override
    public void getLiveRooms(List<LiveRoom> liveRooms) {
        if (mListAdapter==null){
            mListAdapter=new RoomsListAdapter(liveRooms);
            recycleRooms.setAdapter(mListAdapter);

            final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);

            //第一列单独占一行
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (position == 0) ? gridLayoutManager.getSpanCount() : 1;
                }
            });
            recycleRooms.setLayoutManager(gridLayoutManager);
            recycleRooms.addItemDecoration(new GridItemDecoration(8,2));

        }else {

        }


    }


    @BindView(R.id.recycle_rooms)
    LoadRecycleView recycleRooms;


    @Override
    public int layoutResId() {
        return R.layout.fragment_live_list;
    }





}
