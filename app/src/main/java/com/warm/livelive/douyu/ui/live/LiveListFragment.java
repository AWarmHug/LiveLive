package com.warm.livelive.douyu.ui.live;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;

import com.warm.livelive.R;
import com.warm.livelive.base.fragment.LazyFragment;
import com.warm.livelive.douyu.data.bean.live.LiveRoomItem;
import com.warm.livelive.douyu.mvp.LiveTabItemContract;
import com.warm.livelive.douyu.mvp.LiveTabItemPresenter;
import com.warm.livelive.douyu.ui.PlayActivity;
import com.warm.livelive.douyu.ui.live.adapter.LiveAllListAdapter;
import com.warm.livelive.error.KnownException;
import com.warm.livelive.utils.DisplayUtil;
import com.warm.livelive.widget.recycleview2.LoadRecyclerView;
import com.warm.livelive.widget.recycleview2.decoration.GridItemDecoration;

import java.util.List;

import butterknife.BindView;

/**
 * 作者：warm
 * 时间：2018-06-13 11:46
 * 描述：
 */
public class LiveListFragment extends LazyFragment implements LiveTabItemContract.ListView {

    @BindView(R.id.refresh)
    SwipeRefreshLayout mRefresh;
    @BindView(R.id.list)
    LoadRecyclerView mList;

    public static final int SPAN_COUNT = 2;
    private static final int LIMIT = 20;
    private static final String KEY_LEVEL = "level";
    private static final String KEY_CATE_ID = "cate_id";

    private LiveTabItemPresenter mPresenter;
    private int level;
    private int cate_id;
    private int offset;
    private LiveAllListAdapter mAllListAdapter;


    public static LiveListFragment newInstance(int level, int cate_id) {
        Bundle args = new Bundle();
        args.putInt(KEY_LEVEL, level);
        args.putInt(KEY_CATE_ID, cate_id);
        LiveListFragment fragment = new LiveListFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public LiveListFragment() {
        mPresenter = new LiveTabItemPresenter();
        mPresenter.attachListView(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            level = bundle.getInt(KEY_LEVEL, 0);
            cate_id = bundle.getInt(KEY_CATE_ID, 0);
        }
    }

    @Override
    protected void doInVisible() {

    }

    @Override
    protected void doFirstVisible() {

        mAllListAdapter = new LiveAllListAdapter(this);
        mAllListAdapter.setOnItemClickListener(position -> {
            startActivity(PlayActivity.goPlay(getBVContext(),mAllListAdapter.getDatas().get(position)));

        });
        mList.setAdapter(mAllListAdapter);
        GridLayoutManager grid = new GridLayoutManager(getBVContext(), SPAN_COUNT);
        grid.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == mAllListAdapter.getItemCount() - 1 ? SPAN_COUNT : 1;
            }
        });
        mList.setLayoutManager(grid);
        mList.addItemDecoration(new GridItemDecoration(DisplayUtil.dp2px(getBVContext(), 4), SPAN_COUNT));
        mList.setOnLoadMoreListener(() -> {
            loadData();
        });
        mPresenter.getRoomList(level, cate_id, offset, LIMIT);
        mRefresh.setOnRefreshListener(() -> {
            loadData(0);
        });
    }


    private void loadData() {
        mAllListAdapter.loadBegan("正在加载,请稍后");
        mPresenter.getRoomList(level, cate_id, offset, LIMIT);
    }

    private void loadData(int offset) {
        this.offset = offset;
        loadData();
    }


    @Override
    protected void doVisible() {

    }

    @Override
    public void showRoomList(List<LiveRoomItem> liveRoomItems) {
        if (offset == 0) {
            mAllListAdapter.refreshAll(liveRoomItems);
        } else {
            mAllListAdapter.insertRange(liveRoomItems);
        }
        if (liveRoomItems.size() == LIMIT) {
            offset += LIMIT;
            mList.setLoadMoreAble(true);
            mAllListAdapter.loadSuccess("加载成功");
        } else {
            mAllListAdapter.loadSuccess("全部加载完成");
        }

        mRefresh.setRefreshing(false);
    }

    @Override
    public void showError(KnownException exception) {
        mList.setLoadMoreAble(true);
        mAllListAdapter.loadFail(exception.getMessage(), v -> {
            loadData();
        });
        mRefresh.setRefreshing(false);
    }

    @Override
    public int layoutResId() {
        return R.layout.fragment_live_all;
    }

}
