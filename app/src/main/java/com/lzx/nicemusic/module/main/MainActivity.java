package com.lzx.nicemusic.module.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzx.nicemusic.R;
import com.lzx.nicemusic.base.BaseMvpActivity;
import com.lzx.nicemusic.base.mvp.factory.CreatePresenter;
import com.lzx.nicemusic.bean.HomeInfo;
import com.lzx.nicemusic.callback.BaseDiffUtilCallBack;
import com.lzx.nicemusic.module.main.adapter.MainAdapter;
import com.lzx.nicemusic.module.main.presenter.MainContract;
import com.lzx.nicemusic.module.main.presenter.MainPresenter;
import com.lzx.nicemusic.module.search.SearchActivity;
import com.lzx.nicemusic.utils.DisplayUtil;
import com.lzx.nicemusic.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

@CreatePresenter(MainPresenter.class)
public class MainActivity extends BaseMvpActivity<MainContract.View, MainPresenter> implements MainContract.View, View.OnClickListener {

    private RecyclerView mRecyclerView;
    private TextView mEdSearch;
    private View mBgSearch;
    private MainAdapter mMainAdapter;

    private float mDistanceY = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mEdSearch = findViewById(R.id.ed_search);
        mBgSearch = findViewById(R.id.bg_search);
        mRecyclerView = findViewById(R.id.recycle_view);
        mMainAdapter = new MainAdapter(this);
        GridLayoutManager glm = new GridLayoutManager(this, 12);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mMainAdapter.getItemViewType(position)) {
                    case HomeInfo.TYPE_ITEM_BANNER:
                    case HomeInfo.TYPE_ITEM_TITLE:
                    case HomeInfo.TYPE_ITEM_ONE:
                    case HomeInfo.TYPE_ITEM_LONGLEGS:
                    case HomeInfo.TYPE_ITEM_ARTS:
                        return 12;
                    case HomeInfo.TYPE_ITEM_TWO:
                        return 6;
                    case HomeInfo.TYPE_ITEM_THREE:
                        return 4;
                    default:
                        return 12;
                }
            }
        });
        mRecyclerView.setLayoutManager(glm);
        mRecyclerView.setAdapter(mMainAdapter);
        int bgSearchHeight = DisplayUtil.dip2px(this, 80);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mDistanceY += dy;
                if (mDistanceY <= bgSearchHeight) {
                    if (mDistanceY < 0) {
                        mDistanceY = 0;
                    }
                    float scale = mDistanceY / bgSearchHeight;
                    mBgSearch.setAlpha(scale);
                } else {
                    mDistanceY = bgSearchHeight;
                    mBgSearch.setAlpha(1f);
                }
            }
        });
        getPresenter().requestMusicList();


        //搜索
        mEdSearch.setOnClickListener(this);
    }

    @Override
    public void requestMainDataSuccess(List<HomeInfo> dataList) {
        BaseDiffUtilCallBack<HomeInfo> callBack = new BaseDiffUtilCallBack<>(mMainAdapter.getHomeInfos(), dataList);
        callBack.setOnAreItemsTheSameListener((oldData, newData) -> oldData.getFlag().equals(newData.getFlag()));
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callBack, true);
        mMainAdapter.setHomeInfos(dataList);
        diffResult.dispatchUpdatesTo(mMainAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ed_search:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;
        }
    }
}
