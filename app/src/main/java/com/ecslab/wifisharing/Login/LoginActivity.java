package com.ecslab.wifisharing.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ecslab.wifisharing.MainActivity;
import com.ecslab.wifisharing.R;
import com.ecslab.wifisharing.base.BaseActivity;
import com.ecslab.wifisharing.bean.EventCenter;
import com.ecslab.wifisharing.bean.User;
import com.ecslab.wifisharing.tool.NetWorkUtils;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 　　┏┓　　  ┏┓+ +
 * 　┏┛┻━ ━ ━┛┻┓ + +
 * 　┃　　　　　　  ┃
 * 　┃　　　━　　    ┃ ++ + + +
 * ████━████     ┃+
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
 * Date：2017/4/1
 * Email：biloba12345@gamil.com
 * Info：
 */

public class LoginActivity extends BaseActivity {
    private android.widget.EditText etusername;
    private android.widget.EditText etpassword;
    private android.widget.Button btgo;
    private android.support.v7.widget.CardView cv;
    private android.support.design.widget.FloatingActionButton fab;
    private static final String TAG = "LoginActivity";

    public static void start(Context context) {
        Intent starter = new Intent(context, LoginActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }

    @Override
    protected void onStart() {
        if ( BmobUser.getCurrentUser(User.class)!=null) {
            MainActivity.start(this);
            finish();
        }
        super.onStart();
    }

    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void initView() {
        this.fab = (FloatingActionButton) findViewById(R.id.fab);
        this.cv = (CardView) findViewById(R.id.cv);
        this.btgo = (Button) findViewById(R.id.bt_go);
        this.etpassword = (EditText) findViewById(R.id.et_password);
        this.etusername = (EditText) findViewById(R.id.et_username);
    }

    @Override
    protected void setListener() {
        //跳转注册
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.start(LoginActivity.this);
                finish();
            }
        });
        //登录
        btgo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetWorkUtils.isNetworkConnected(LoginActivity.this)) {
                    if (checkInput()) {
                        queryUser();
                    }
                }else {
                    showErrorToast(R.string.no_internet);
                }
            }
        });
        //忘记密码
        findViewById(R.id.forget_pwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = etusername.getText().toString();
                BmobUser.resetPasswordByEmail(email, new UpdateListener() {

                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                            showInfoToast("请求成功，请到" + email + "进行密码重置操作");
                        }else{
                            e.printStackTrace();
                            showErrorToast(R.string.error);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void setData() {

    }

    @Override
    protected void getBundleExtras(Bundle extras) {

    }

    @Override
    protected void onEventComing(EventCenter center) {

    }

    //检查输入格式
    private boolean checkInput(){
        if (!User.emailFormat(etusername.getText().toString())) {
            etusername.setError(getString(R.string.wrong_email));
            return false;
        }
        if (etpassword.getText().toString().isEmpty()) {
            etpassword.setError(getString(R.string.empty_password));
            return false;
        }
        return true;
    }

    /**
     * 查询数据
     */
    public void queryUser(){
        showLoadingDialog();
        BmobUser.loginByAccount(etusername.getText().toString(),
                etpassword.getText().toString(), new LogInListener<User>() {

            @Override
            public void done(User user, BmobException e) {
                if(user!=null){
                    Log.i("smile","用户登陆成功");
                    cancelLoadingDialog();
                    showSuccToast(R.string.login_succ);
                    MainActivity.start(LoginActivity.this);
                    finish();
                }else {
                    cancelLoadingDialog();
                    showErrorToast(R.string.error_info);
                }
            }
        });
    }
}
