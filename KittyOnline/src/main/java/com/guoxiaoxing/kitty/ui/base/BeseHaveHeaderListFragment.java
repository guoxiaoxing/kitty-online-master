package com.guoxiaoxing.kitty.ui.base;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.guoxiaoxing.kitty.model.BaseObject;
import com.guoxiaoxing.kitty.cache.CacheManager;
import com.guoxiaoxing.kitty.ui.empty.EmptyLayout;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * 需要加入header的BaseListFragment
 *
 * @author 火蚁(http://my.oschina.net/u/253900)
 * @desc 应用场景：如动弹详情、团队任务详情这些， 即是头部显示详情，然后下面显示评论列表的
 * <p/>
 * BeseHaveHeaderListFragment.java
 * @author guoxiaoxing
 */
public abstract class BeseHaveHeaderListFragment<T1 extends BaseObject, T2 extends Serializable>
        extends BaseListFragment<T1> {

    protected T2 detailBean;// list 头部的详情实体类

    protected Activity aty;

    protected final AsyncHttpResponseHandler mDetailHandler = new AsyncHttpResponseHandler() {

        @Override
        public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
            try {
                if (arg2 != null) {
                    T2 detail = getDetailBean(new ByteArrayInputStream(arg2));
                    if (detail != null) {
                        requstListData();
                        executeOnLoadDetailSuccess(detail);
                        new SaveCacheTask(getActivity(), detail,
                                getDetailCacheKey()).execute();
                    } else {
                        onFailure(arg0, arg1, arg2, null);
                    }
                } else {
                    throw new RuntimeException("load detail error");
                }
            } catch (Exception e) {
                e.printStackTrace();
                onFailure(arg0, arg1, arg2, e);
            }
        }

        @Override
        public void onFailure(int arg0, Header[] arg1, byte[] arg2,
                              Throwable arg3) {
            readDetailCacheData(getDetailCacheKey());
        }
    };

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // 通过注解绑定控件
        ButterKnife.bind(this, view);
        mListView.addHeaderView(initHeaderView());
        aty = getActivity();
        super.initView(view);
        requestDetailData(isRefresh());
    }

    protected boolean isRefresh() {
        return false;
    }

    protected abstract void requestDetailData(boolean isRefresh);

    protected abstract View initHeaderView();

    protected abstract String getDetailCacheKey();

    protected abstract void executeOnLoadDetailSuccess(T2 detailBean);

    protected abstract T2 getDetailBean(ByteArrayInputStream is);

    @Override
    protected boolean requestDataIfViewCreated() {
        return false;
    }

    private void requstListData() {
        mState = STATE_REFRESH;
        mAdapter.setState(ListBaseAdapter.STATE_LOAD_MORE);
        sendRequestData();
    }

    /***
     * 带有header view的listfragment不需要显示是否数据为空
     */
    @Override
    protected boolean needShowEmptyNoData() {
        return false;
    }

    protected void readDetailCacheData(String cacheKey) {
        new ReadCacheTask(getActivity()).execute(cacheKey);
    }

    private class SaveCacheTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<Context> mContext;
        private final Serializable seri;
        private final String key;

        private SaveCacheTask(Context context, Serializable seri, String key) {
            mContext = new WeakReference<Context>(context);
            this.seri = seri;
            this.key = key;
        }

        @Override
        protected Void doInBackground(Void... params) {
            CacheManager.saveObject(mContext.get(), seri, key);
            return null;
        }
    }

    private class ReadCacheTask extends AsyncTask<String, Void, T2> {
        private final WeakReference<Context> mContext;

        private ReadCacheTask(Context context) {
            mContext = new WeakReference<Context>(context);
        }

        @Override
        protected T2 doInBackground(String... params) {
            if (mContext.get() != null) {
                Serializable seri = CacheManager.readObject(mContext.get(),
                        params[0]);
                if (seri == null) {
                    return null;
                } else {
                    return (T2) seri;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(T2 t) {
            super.onPostExecute(t);
            if (t != null) {
                requstListData();
                executeOnLoadDetailSuccess(t);
            }
        }
    }

    @Override
    protected void executeOnLoadDataError(String error) {
        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        mAdapter.setState(ListBaseAdapter.STATE_NETWORK_ERROR);
        mAdapter.notifyDataSetChanged();
    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T findHeaderView(View headerView, int viewId) {
        return (T) headerView.findViewById(viewId);
    }
}
