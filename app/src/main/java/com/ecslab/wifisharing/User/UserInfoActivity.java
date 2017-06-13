package com.ecslab.wifisharing.User;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.ecslab.wifisharing.Login.LoginActivity;
import com.ecslab.wifisharing.R;
import com.ecslab.wifisharing.base.BaseActivity;
import com.ecslab.wifisharing.bean.EventCenter;
import com.ecslab.wifisharing.bean.User;
import com.ecslab.wifisharing.event.LogoutEvent;
import com.ecslab.wifisharing.view.CardItem;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;

import org.greenrobot.eventbus.EventBus;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends BaseActivity {
    /**
    *   View
    */
    private de.hdodenhof.circleimageview.CircleImageView headiv;
    private android.widget.Button signbt;
    private CardItem accountitem,pwditem,sexitem,nickitem;


    /**
    *   Fragment
    */

    /**
    *   Data
    */
    private User mUser;
    private boolean mIsResetInfo;
    /**
    *   Tag
    */
    private static final String TAG = "UserInfoActivity";
    private View mLogoutText;


    public static void start(Context context) {
        Intent starter = new Intent(context, UserInfoActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, UserInfoActivity.class);
//        intent.putExtra();
        return intent;
    }

    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_user_info);

    }

    @Override
    protected void initView() {
        initToolbar(R.string.account_info,true);
        this.pwditem = (CardItem) findViewById(R.id.pwd_item);
        this.accountitem = (CardItem) findViewById(R.id.account_item);
        this.signbt = (Button) findViewById(R.id.sign_bt);
        this.headiv = (CircleImageView) findViewById(R.id.head_iv);
        this.sexitem = (CardItem) findViewById(R.id.sex_item);
        this.nickitem = (CardItem) findViewById(R.id.nick_item);
        mLogoutText = findViewById(R.id.logout_text);
    }

    @Override
    protected void setListener() {
        //修改昵称
        nickitem.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showResetNickDialog();
            }
        });
        //修改性别
        sexitem.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showResetSexDialog();
            }
        });
        //修改密码
        pwditem.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showResetPwdDialog();
            }
        });
        //退出登录
        mLogoutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobUser.logOut();   //清除缓存用户对象
                LoginActivity.start(UserInfoActivity.this);
                EventBus.getDefault()
                        .post(new EventCenter<>(0,new LogoutEvent()));
                finish();
            }
        });
    }

    @Override
    protected void initData() {
        mUser= BmobUser.getCurrentUser(User.class);
    }

    @Override
    protected void setData() {
        accountitem.setIcon(getIcon(GoogleMaterial.Icon.gmd_contact_mail, Color.parseColor("#3a9bfd")))
                .setContent(mUser.getEmail());
        nickitem.setIcon(getIcon(GoogleMaterial.Icon.gmd_account_circle, Color.parseColor("#3a9bfd")))
                .setContent(mUser.getNick());
        pwditem.setIcon(getIcon(GoogleMaterial.Icon.gmd_visibility, Color.parseColor("#3a9bfd")));
        if (mUser.getMale() != null) {
            sexitem.setContent(mUser.getMale()?"男":"女");
        }
        sexitem.setIcon(getIcon(GoogleMaterial.Icon.gmd_hdr_strong, Color.parseColor("#3a9bfd")));

    }

    @Override
    protected void getBundleExtras(Bundle extras) {

    }

    @Override
    protected void onEventComing(EventCenter center) {

    }

    private void showResetNickDialog(){
        View v= LayoutInflater.from(this)
                .inflate(R.layout.dialog_reset_nick,null);
        final EditText nameEt = (EditText) v.findViewById(R.id.name);
        nameEt.setText(nickitem.getContent());
        new AlertDialog.Builder(this)
                .setView(v)
                .setPositiveButton(R.string.reset, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nick=nameEt.getText().toString();
                        nickitem.setContent(nick);
                        mUser.setNick(nick);
                        mIsResetInfo=true;
                    }
                })
                .setNegativeButton(android.R.string.cancel,null)
                .show();
    }

    private void showResetSexDialog(){
        View v= LayoutInflater.from(this)
                .inflate(R.layout.dialog_reset_sex,null);
        final RadioGroup group= (RadioGroup) v.findViewById(R.id.group);
        if (mUser.getMale()!=null) {
            group.check(sexitem.getContent().equals("男")?R.id.male:R.id.female);
        }
        new AlertDialog.Builder(this)
                .setView(v)
                .setPositiveButton(R.string.reset, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (group.getCheckedRadioButtonId()) {
                            case R.id.male:
                                mIsResetInfo=true;
                                sexitem.setContent("男");
                                mUser.setMale(true);
                                break;
                            case R.id.female:
                                mIsResetInfo=true;
                                sexitem.setContent("女");
                                mUser.setMale(false);
                                break;
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel,null)
                .show();
    }

    private void showResetPwdDialog(){
        View v= LayoutInflater.from(this)
                .inflate(R.layout.dialog_reset_pwd,null);
        final EditText curPwdEt= (EditText) v.findViewById(R.id.current_pwd);
        final EditText newPwdEt= (EditText) v.findViewById(R.id.new_pwd);
        new AlertDialog.Builder(this)
                .setView(v)
                .setPositiveButton(R.string.reset, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mUser.updateCurrentUserPassword(curPwdEt.getText().toString(),
                                newPwdEt.getText().toString() , new UpdateListener() {

                            @Override
                            public void done(BmobException e) {
                                if(e==null){
                                    showSuccToast(R.string.reset_pwd_succ);
                                }else{
                                    showErrorToast(R.string.error_pwd);
                                }
                            }

                        });
                    }
                })
                .setNegativeButton(android.R.string.cancel,null)
                .show();
    }


    @Override
    protected void onDestroy() {
        if (mIsResetInfo) {
            mUser.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null){
                        showSuccToast(R.string.reset_info_succ);
                    }else{
                        showErrorToast(R.string.reset_info_error);
                    }
                }
            });
        }
        super.onDestroy();
    }
}
