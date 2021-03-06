package com.guoxiaoxing.kitty.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.guoxiaoxing.kitty.R;
import com.guoxiaoxing.kitty.model.SimpleBackPage;
import com.guoxiaoxing.kitty.emoji.OnSendClickListener;
import com.guoxiaoxing.kitty.ui.base.BaseActivity;
import com.guoxiaoxing.kitty.ui.base.BaseFragment;
import com.guoxiaoxing.kitty.ui.fragment.MessageDetailFragment;
import com.guoxiaoxing.kitty.ui.fragment.TalkPubFragment;
import com.guoxiaoxing.kitty.ui.fragment.TalksFragment;
import com.guoxiaoxing.kitty.util.UIHelper;

import java.lang.ref.WeakReference;

import butterknife.Bind;

/**
 * 跳转Activity,对跳转界面的封装。
 */

public class SimpleBackActivity extends BaseActivity implements
        OnSendClickListener {

    public static final String TAG = "FLAG_TAG";
    public final static String BUNDLE_KEY_PAGE = "BUNDLE_KEY_PAGE";
    public final static String BUNDLE_KEY_ARGS = "BUNDLE_KEY_ARGS";

    protected WeakReference<Fragment> mFragment;
    protected int mPageValue = -1;
    @Bind(R.id.tb_simple_back_activity)
    Toolbar mToolbar;
    @Bind(R.id.tv_tb_title)
    TextView mTvTbTitle;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_simple_back;
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        if (mPageValue == -1) {
            mPageValue = getIntent().getIntExtra(BUNDLE_KEY_PAGE, 0);
        }
        initFromIntent(mPageValue, getIntent());
    }

    protected void initFromIntent(int pageValue, Intent data) {
        if (data == null) {
            throw new RuntimeException(
                    "you must provide a page info to display");
        }
        SimpleBackPage page = SimpleBackPage.getPageByValue(pageValue);
        if (page == null) {
            throw new IllegalArgumentException("can not find page by value:"
                    + pageValue);
        }

        //设置AppBar标题
        mTvTbTitle.setText(page.getTitle());

        try {
            Fragment fragment = (Fragment) page.getClz().newInstance();

            Bundle args = data.getBundleExtra(BUNDLE_KEY_ARGS);
            if (args != null) {
                fragment.setArguments(args);
            }

            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.replace(R.id.container, fragment, TAG);
            transaction.commitAllowingStateLoss();

            mFragment = new WeakReference<Fragment>(fragment);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(
                    "generate fragment error. by value:" + pageValue);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mFragment.get() instanceof TalksFragment) {
            setActionBarTitle("话题");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.public_menu_send:
                if (mFragment.get() instanceof TalksFragment) {
                    sendTopic();
                } else {
                    return super.onOptionsItemSelected(item);
                }
                break;
            case R.id.chat_friend_home:
                if (mFragment.get() instanceof MessageDetailFragment) {
                    ((MessageDetailFragment) mFragment.get()).showFriendUserCenter();
                } else {
                    return super.onOptionsItemSelected(item);
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mFragment.get() instanceof TalksFragment) {
            getMenuInflater().inflate(R.menu.pub_topic_menu, menu);
        } else if (mFragment.get() instanceof MessageDetailFragment) {
            getMenuInflater().inflate(R.menu.chat_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 发送话题
     */
    private void sendTopic() {
        Bundle bundle = new Bundle();
        bundle.putInt(TalkPubFragment.TALK_TYPE,
                TalkPubFragment.TALK_TYPE_TOPIC);
        bundle.putString("tweet_topic", "#"
                + ((TalksFragment) mFragment.get()).getTopic() + "# ");
        UIHelper.showTalkActivity(this, SimpleBackPage.TALK_PUB, bundle);
    }

    @Override
    public void onBackPressed() {
        if (mFragment != null && mFragment.get() != null
                && mFragment.get() instanceof BaseFragment) {
            BaseFragment bf = (BaseFragment) mFragment.get();
            if (!bf.onBackPressed()) {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.ACTION_DOWN
                && mFragment.get() instanceof BaseFragment) {
            ((BaseFragment) mFragment.get()).onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);
    }

    @Override
    public void onClick(View v) {
    }


    @Override
    protected void setActionBar() {
        setSupportActionBar(mToolbar);
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
    }

    @Override
    public void onClickSendButton(Editable str) {
        if (mFragment.get() instanceof MessageDetailFragment) {
            ((OnSendClickListener) mFragment.get()).onClickSendButton(str);
            ((MessageDetailFragment) mFragment.get()).emojiFragment.clean();
        }
    }

    @Override
    public void onClickFlagButton() {
    }
}
