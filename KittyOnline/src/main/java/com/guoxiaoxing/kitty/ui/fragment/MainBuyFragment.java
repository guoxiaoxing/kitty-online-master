package com.guoxiaoxing.kitty.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.guoxiaoxing.kitty.R;
import com.guoxiaoxing.kitty.adapter.MainBuyAdapter;
import com.guoxiaoxing.kitty.model.Goods;
import com.guoxiaoxing.kitty.model.SimpleBackPage;
import com.guoxiaoxing.kitty.ui.base.BaseFragment;
import com.guoxiaoxing.kitty.util.UIHelper;
import com.guoxiaoxing.kitty.util.log.Logger;
import com.guoxiaoxing.kitty.widget.banner.ConvenientBanner;
import com.guoxiaoxing.kitty.widget.banner.holder.CBViewHolderCreator;
import com.guoxiaoxing.kitty.widget.banner.holder.LocalImageHolderView;
import com.guoxiaoxing.kitty.widget.banner.listener.OnItemClickListener;
import com.guoxiaoxing.kitty.widget.banner.transforms.FlipHorizontalTransformer;
import com.guoxiaoxing.kitty.widget.timecounter.CountdownView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;


public class MainBuyFragment extends BaseFragment implements AdapterView.OnItemClickListener,
        ViewPager.OnPageChangeListener, OnItemClickListener {

    private static final String TAG = "MainShoppingFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Context mContext;
    private String mParam1;
    private String mParam2;

    @Bind(R.id.tb_main_buy_fragment)
    Toolbar mToolbar;
    @Bind(R.id.et_search)
    EditText mEtSearch;
    @Bind(R.id.rv_main_buy_fragment)
    RecyclerView mRecyclerView;

    ConvenientBanner mConvenientBanner;
    CountdownView mCountdownView;

    private OnFragmentInteractionListener mListener;
    private MainBuyAdapter mAdapter;
    private GridLayoutManager mGridLayoutManager;

    int moreNum = 2;
    boolean isDrag = true;

    private ArrayList<Integer> localImages = new ArrayList<>();
    private View headerView;

    public MainBuyFragment() {
    }

    public static MainShoppingFragment newInstance(String param1, String param2) {
        MainShoppingFragment fragment = new MainShoppingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.startBannerTurning();

    }

    @Override
    public void onPause() {
        super.onPause();
//        mConvenientBanner.stopTurning();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAdapter.stopBannerTurning();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onItemClick(int position) {

    }


    @Override
    public void onClick(View v) {

        super.onClick(v);

        switch (v.getId()) {
            //搜索框
            case R.id.et_search:
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.SEARCH);
                break;
            default:
                break;

        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_buy;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
    }

    @Override
    public void initView(View view) {
        initToolbar();
        initContentView();
    }

    @Override
    public void initData() {
        super.initData();

        AVQuery<Goods> query = AVQuery.getQuery(Goods.class);
        query.findInBackground(new FindCallback<Goods>() {
            @Override
            public void done(List<Goods> list, AVException e) {
                if (e == null) {
                    mAdapter.setData(new ArrayList<>(list));
                    mAdapter.notifyDataSetChanged();
                } else {

                }
            }
        });
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onHomeFragmentInteraction(uri);
        }
    }

    public interface OnFragmentInteractionListener {
        void onHomeFragmentInteraction(Uri uri);
    }

    public int getScreenHeight() {
        return getActivity().findViewById(android.R.id.content).getHeight();
    }

    public static int getResId(String variableName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void initToolbar() {
        mContext = getActivity();
        mEtSearch.setFocusable(false);
        mEtSearch.setOnClickListener(this);
    }

    private void initHeaderView() {

        //设置HeaderView
        headerView = LayoutInflater.from(mContext).inflate(R.layout.item_buy_fragment_header, null, false);
        mCountdownView = (CountdownView) headerView.findViewById(R.id.cv_sale);


        for (int position = 0; position < 7; position++) {
            localImages.add(getResId("ic_buy_banner_" + position, R.drawable.class));
        }


        mConvenientBanner = (ConvenientBanner) headerView.findViewById(R.id.cb_sale_ad);
        mConvenientBanner.getViewPager().setPageTransformer(true, new FlipHorizontalTransformer());
        //本地图片例子
        mConvenientBanner.setPages(
                new CBViewHolderCreator<LocalImageHolderView>() {
                    @Override
                    public LocalImageHolderView createHolder() {
                        return new LocalImageHolderView();
                    }
                }, localImages)
//                .setOnPageChangeListener(this)//监听翻页事件
                .setOnItemClickListener(this);

//        mCbHomeAd.setManualPageable(false);//设置不能手动影响


    }

    private void initContentView() {
        mRecyclerView.setHasFixedSize(false);
        mGridLayoutManager = new GridLayoutManager(mContext, 2);
        //设置头部及底部占据整行空间
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                return (mAdapter.isHeaderView(position) | mAdapter.isBottomView(position))
                        ? mGridLayoutManager.getSpanCount() : 1;
            }
        });
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mAdapter = new MainBuyAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setmOnRecyclerViewItemClickListener(new MainBuyAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, Goods goods) {

                UIHelper.showGoodsDetail(view.getContext(), goods);
            }
        });
    }

}