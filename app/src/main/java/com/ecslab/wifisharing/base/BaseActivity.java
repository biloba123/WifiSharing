package com.ecslab.wifisharing.base;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.ecslab.wifisharing.R;
import com.ecslab.wifisharing.bean.EventCenter;
import com.ecslab.wifisharing.permission.CheckPermissionsActivity;
import com.ecslab.wifisharing.tool.MySweetAlertDialog;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.mikepenz.iconics.typeface.IIcon;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import cn.bmob.v3.Bmob;
import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

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
 * Date：2017/3/15
 * Email：biloba12345@gamil.com
 * Info：活动基类
 */

public abstract class BaseActivity extends CheckPermissionsActivity {
    private static final String TAG = "BaseActivity";
    /**
     * RecyclerView空界面默认布局
     */
    protected View emptyView;
    //加载对话框
    protected MySweetAlertDialog mDialog;

    @Override
    protected void onStart() {
        super.onStart();
        //注冊EventBus
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));
        super.onCreate(savedInstanceState);

        //Beob默认初始化
        Bmob.initialize(this, "eb5dc119e33ffd6f68d4ee6e6bfae698");

        Logger
                .init("lqy")                 // default PRETTYLOGGER or use just init()
                .methodCount(3)                 // default 2
                .logLevel(LogLevel.FULL)        // default LogLevel.FULL
                .methodOffset(2);           // default 0

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!Settings.System.canWrite(this)){
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1);
            }
        }


        //有数据则取出
        Bundle extras = getIntent().getExtras();
        if (null != extras) {
            getBundleExtras(extras);
        }

        initContentView(savedInstanceState);

        initView();
        setListener();
        initData();
        setData();

    }

    /**
     * 替代onCreate的使用
     */
    protected abstract void initContentView(Bundle bundle);

    /**
     * 初始化view
     */
    protected abstract void initView();

    /**
     * 设置监听器
     */
    protected abstract void setListener();

    /**
     * 初始化数据
     */
    protected abstract void initData();


    /**
     * view显示数据
     */
    protected abstract void setData();

    /**
     * Bundle  传递数据
     *
     * @param extras
     */
    protected abstract void getBundleExtras(Bundle extras);


    //初始化Toolbar
    protected Toolbar initToolbar(String title, boolean isDisplayHomeAsUp) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
            actionBar.setDisplayHomeAsUpEnabled(isDisplayHomeAsUp);
        }
        return toolbar;
    }

    //初始化Toolbar
    protected Toolbar initToolbar(int titleId, boolean isDisplayHomeAsUp) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(titleId);
            actionBar.setDisplayHomeAsUpEnabled(isDisplayHomeAsUp);
        }
        return toolbar;
    }


    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


    /**
     * EventBus接收消息
     *
     * @param center 消息接收
     */
    @Subscribe

    public void onEventMainThread(EventCenter center) {

        if (null != center) {
            onEventComing(center);
        }

    }

    /**
     * EventBus接收消息
     *
     * @param center 获取事件总线信息
     */
    protected abstract void onEventComing(EventCenter center);


    /**
     * 显示默认加载动画 默认加载文字
     */
    protected void showLoadingDialog() {
        if (null == mDialog) {
            mDialog = new MySweetAlertDialog(this);
        }
        mDialog.loading(null);
    }


    /**
     * 显示默认加载动画 自定义加载文字
     *
     * @param str
     */
    protected void showLoadingDialog(String str) {
        if (null == mDialog) {
            mDialog = new MySweetAlertDialog(this);
        }
        mDialog.loading(str);
    }

    /**
     * 显示加载动画 自定义加载文字
     *
     * @param strId
     */
    protected void showLoadingDialog(int strId) {
        if (null == mDialog) {
            mDialog = new MySweetAlertDialog(this);
        }
        mDialog.loading(getString(strId));
    }


    /**
     * 取消加载动画
     */
    protected void cancelLoadingDialog() {
        if (null != mDialog) {
            mDialog.success();
            mDialog=null;
        }
    }

    /**
     * 显示错误对话框
     *
     * @param listener
     */
    protected void showErrorDialog(SweetAlertDialog.OnSweetClickListener listener){
        if (null == mDialog) {
            mDialog = new MySweetAlertDialog(this);
        }
        mDialog.error(null,listener);
        mDialog=null;
    }

    /**
     * 显示错误对话框
     *
     * @param content
     * @param listener
     */
    protected void showErrorDialog(String content,SweetAlertDialog.OnSweetClickListener listener){
        if (null == mDialog) {
            mDialog = new MySweetAlertDialog(this);
        }
        mDialog.error(content,listener);
        mDialog=null;
    }

    /**
     * 显示错误对话框
     *
     * @param contentId
     * @param listener
     */
    protected void showErrorDialog(int contentId,SweetAlertDialog.OnSweetClickListener listener){
        if (null == mDialog) {
            mDialog = new MySweetAlertDialog(this);
        }
        mDialog.error(getString(contentId),listener);
        mDialog=null;
    }

    //Toast显示
    protected void showToast(String string) {
        Toasty.normal(this, string, Toast.LENGTH_SHORT).show();
    }

    protected void showToast(int stringId) {
        Toasty.normal(this, getString(stringId), Toast.LENGTH_SHORT).show();
    }

    protected void showToastWithIcon(String string, Drawable icon) {
        Toasty.custom(this, string,icon, Color.WHITE,Toast.LENGTH_SHORT,true).show();
    }

    protected void showToastWithIcon(int  stringId, Drawable icon) {
        Toasty.custom(this, getString(stringId),icon, Color.WHITE, Toast.LENGTH_SHORT,true).show();
    }

    protected void showInfoToast(String string){
        Toasty.info(this,string,Toast.LENGTH_SHORT ).show();
    }

    protected void showInfoToast(int  stringId){
        Toasty.info(this,getString(stringId),Toast.LENGTH_SHORT ).show();
    }

    protected void showSuccToast(String string){
        Toasty.success(this, string,Toast.LENGTH_SHORT).show();
    }


    protected void showSuccToast(int  stringId){
        Toasty.success(this, getString(stringId),Toast.LENGTH_SHORT).show();
    }

    protected void showErrorToast(String string){
        Toasty.error(this, string,Toast.LENGTH_SHORT).show();
    }

    protected void showErrorToast(int  stringId){
        Toasty.error(this, getString(stringId),Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        emptyView = null;
        super.onDestroy();
    }


    /**
     * 界面跳转
     *
     * @param clazz 目标Activity
     */
    protected void readyGo(Class<?> clazz) {
        readyGo(clazz, null);
    }

    /**
     * 跳转界面，  传参
     *
     * @param clazz  目标Activity
     * @param bundle 数据
     */
    protected void readyGo(Class<?> clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (null != bundle)
            intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 跳转界面并关闭当前界面
     *
     * @param clazz 目标Activity
     */
    protected void readyGoThenKill(Class<?> clazz) {
        readyGoThenKill(clazz, null);
    }

    /**
     * @param clazz  目标Activity
     * @param bundle 数据
     */
    protected void readyGoThenKill(Class<?> clazz, Bundle bundle) {
        readyGo(clazz, bundle);
        finish();
    }

    /**
     * startActivityForResult
     *
     * @param clazz       目标Activity
     * @param requestCode 发送判断值
     */
    protected void readyGoForResult(Class<?> clazz, int requestCode) {
        Intent intent = new Intent(this, clazz);
        startActivityForResult(intent, requestCode);
    }

    /**
     * startActivityForResult with bundle
     *
     * @param clazz       目标Activity
     * @param requestCode 发送判断值
     * @param bundle      数据
     */
    protected void readyGoForResult(Class<?> clazz, int requestCode, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        if (null != bundle) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * 获取图标
     */

    public IconicsDrawable getToolbarIcon(IIcon icon) {
        return new IconicsDrawable(this).icon(icon).color(Color.WHITE).sizeDp(18);
    }

    public IconicsDrawable getIcon(IIcon icon,int color) {
        return new IconicsDrawable(this).icon(icon).color(color);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
            finish();
        }
        return true;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode ==1 ) {
            if (Settings.System.canWrite(this)) {
                //检查返回结果
            } else {
                showErrorDialog(R.string.permission_deny, new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        finish();
                    }
                });
            }
        }
    }
}
