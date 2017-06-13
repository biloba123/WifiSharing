package com.ecslab.wifisharing.User;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecslab.wifisharing.Login.LoginActivity;
import com.ecslab.wifisharing.R;
import com.ecslab.wifisharing.base.BaseFragment;
import com.ecslab.wifisharing.bean.EventCenter;
import com.ecslab.wifisharing.bean.User;
import com.ecslab.wifisharing.event.LogoutEvent;
import com.ecslab.wifisharing.view.CardItem;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;

import cn.bmob.v3.BmobUser;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 　　┏┓　　  ┏┓+ +
 * 　┏┛┻━ ━ ━┛┻┓ + +
 * 　┃　　　　　　  ┃
 * 　┃　　　━　　    ┃ ++ + + +
 *     ████━████     ┃+
 * 　┃　　　　　　  ┃ +
 * 　┃　　　┻　　  ┃
 * 　┃　　　　　　  ┃ + +
 * 　┗━┓　　　┏━┛
 * 　　　┃　　　┃
 * 　　　┃　　　┃ + + + +
 * 　　　┃　　　┃
 * 　　　┃　　　┃ +  神兽保佑
 * 　　　┃　　　┃    代码无bug！
 * 　　　┃　　　┃　　+
 * 　　　┃　 　　┗━━━┓ + +
 * 　　　┃ 　　　　　　　┣┓
 * 　　　┃ 　　　　　　　┏┛
 * 　　　┗┓┓┏━┳┓┏┛ + + + +
 * 　　　　┃┫┫　┃┫┫
 * 　　　　┗┻┛　┗┻┛+ + + +
 * ━━━━━━神兽出没━━━━━━
 * Author：LvQingYang
 * Date：2017/3/20
 * Email：biloba12345@gamil.com
 * Info：
 */

public class UserFragment extends BaseFragment {

    /**
    *   View
    */
    private Toolbar mToolbar;
    private android.widget.LinearLayout unloginll;
    private de.hdodenhof.circleimageview.CircleImageView headiv;
    private android.widget.TextView nametext;
    private android.widget.TextView teltext;
    private android.support.constraint.ConstraintLayout logincl;
    private ConstraintLayout mConstraintLayout;
    private CardItem pointitem;
    private CardItem notificationitem;
    private CardItem feedbackitem;
    private CardItem aboutitem;
    /**
    *   Fragment
    */

    /**
    *   Data
    */
    private User mUser;

    /**
    *   Tag
    */
    private static final String TAG = "UserFragment";

    private static final int REQUEST_INFO = 472;

    public static UserFragment newInstance() {

        Bundle args = new Bundle();

        UserFragment fragment = new UserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_uesr,container,false);
    }

    @Override
    protected void initView(View view) {
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        initToolbar(mToolbar,getString(R.string.title_my),false);

        this.logincl = (ConstraintLayout) view.findViewById(R.id.login_cl);
        this.teltext = (TextView) view.findViewById(R.id.tel_text);
        this.nametext = (TextView) view.findViewById(R.id.name_text);
        this.headiv = (CircleImageView) view.findViewById(R.id.head_iv);
        this.unloginll = (LinearLayout) view.findViewById(R.id.unlogin_ll);
        mConstraintLayout = (ConstraintLayout) view.findViewById(R.id.point_cl);
        this.aboutitem = (CardItem) view.findViewById(R.id.about_item);
        this.feedbackitem = (CardItem) view.findViewById(R.id.feedback_item);
        this.notificationitem = (CardItem) view.findViewById(R.id.notification_item);
        this.pointitem = (CardItem) view.findViewById(R.id.point_item);
        aboutitem.setIcon(getIcon(GoogleMaterial.Icon.gmd_info, Color.parseColor("#3a9bfd")));
        feedbackitem.setIcon(getIcon(GoogleMaterial.Icon.gmd_feedback, Color.parseColor("#3a9bfd")));
        notificationitem.setIcon(getIcon(GoogleMaterial.Icon.gmd_more, Color.parseColor("#3a9bfd")));
        pointitem.setIcon(getIcon(GoogleMaterial.Icon.gmd_monetization_on, Color.parseColor("#3a9bfd")));
    }

    @Override
    protected void setListener() {
        //查看资料
        logincl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                BmobUser.logOut();   //清除缓存用户对象
//                BmobUser currentUser = BmobUser.getCurrentUser(); // 现在的currentUser是null了
//                unlogin();
                readyGoForResult(UserInfoActivity.class,REQUEST_INFO);
            }
        });
        //登录
        unloginll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.start(getActivity());
                getActivity().finish();
            }
        });
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void setData() {
        mUser=BmobUser.getCurrentUser(User.class);
        if (mUser!=null) {
            login();
            nametext.setText(mUser.getNick());
            teltext.setText("账号："+mUser.getEmail());
        }else {
            unlogin();
        }
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected void onEventComing(EventCenter center) {
        Log.d(TAG, "onEventComing: ");
        if(center.getData()instanceof LogoutEvent){
            getActivity().finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (logincl!=null) {
            setData();
        }
    }

    //未登录
    private void unlogin(){
        logincl.setVisibility(View.GONE);
        mConstraintLayout.setVisibility(View.GONE);
        unloginll.setVisibility(View.VISIBLE);
    }

    //登录
    private void login(){
        unloginll.setVisibility(View.GONE);
        logincl.setVisibility(View.VISIBLE);
        mConstraintLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
    }
}
