package com.ecslab.wifisharing.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ecslab.wifisharing.MainActivity;
import com.ecslab.wifisharing.R;
import com.ecslab.wifisharing.base.BaseActivity;
import com.ecslab.wifisharing.bean.EventCenter;
import com.ecslab.wifisharing.bean.User;
import com.ecslab.wifisharing.tool.NetWorkUtils;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

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

public class RegisterActivity extends BaseActivity {
    private android.widget.EditText etusername;
    private android.widget.EditText ettel;
    private android.widget.EditText etpassword;
    private android.widget.Button btgo;
    private android.support.v7.widget.CardView cvadd;
    private android.support.design.widget.FloatingActionButton fab;
    private static final String TAG = "RegisterActivity";


    public static void start(Context context) {
        Intent starter = new Intent(context, RegisterActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }

    @Override
    protected void initContentView(Bundle bundle) {
        setContentView(R.layout.activity_register);
    }

    @Override
    protected void initView() {
        this.fab = (FloatingActionButton) findViewById(R.id.fab);
        this.cvadd = (CardView) findViewById(R.id.cv_add);
        this.btgo = (Button) findViewById(R.id.bt_go);
        this.etpassword = (EditText) findViewById(R.id.et_password);
        this.ettel = (EditText) findViewById(R.id.et_tel);
        this.etusername = (EditText) findViewById(R.id.et_username);
    }

    @Override
    protected void setListener() {
        //跳转登录
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.start(RegisterActivity.this);
                finish();
            }
        });
        //注册
        btgo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetWorkUtils.isNetworkConnected(RegisterActivity.this)) {
                    if (checkInput()) {
                        showLoadingDialog();
                        User bu = new User();
                        bu.setNick(etusername.getText().toString());
                        bu.setUsername(ettel.getText().toString());
                        bu.setPassword(etpassword.getText().toString());
                        bu.setEmail(ettel.getText().toString());
                        bu.setMale(null);
                        bu.setPic(null);
                        bu.signUp(new SaveListener<User>() {
                            @Override
                            public void done(User s, BmobException e) {
                                if(e==null){
                                    cancelLoadingDialog();
                                    showSuccToast(R.string.register_succ);
                                    MainActivity.start(RegisterActivity.this);
                                    finish();
                                }else{
                                    e.printStackTrace();
                                    cancelLoadingDialog();
                                    showInfoToast(R.string.registered);
                                }
                            }
                        });
                    }
                }else {
                    showErrorToast(R.string.no_internet);
                }
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
        if (etusername.getText().toString().isEmpty()) {
            etusername.setError(getString(R.string.empty_user));
            return false;
        }
        if (!User.emailFormat(ettel.getText().toString())) {
            ettel.setError(getString(R.string.wrong_email));
            return false;
        }
        if (etpassword.getText().toString().length()<6) {
            etpassword.setError(getString(R.string.pwd_length));
            return false;
        }
        return true;
    }

}
