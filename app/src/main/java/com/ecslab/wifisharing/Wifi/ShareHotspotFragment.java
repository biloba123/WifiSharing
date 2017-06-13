package com.ecslab.wifisharing.Wifi;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecslab.wifisharing.R;
import com.ecslab.wifisharing.Wifi.bean.ConnectDevice;
import com.ecslab.wifisharing.base.BaseFragment;
import com.ecslab.wifisharing.bean.EventCenter;
import com.ecslab.wifisharing.i.WiFiAPListener;
import com.ecslab.wifisharing.service.WiFiAPService;
import com.ecslab.wifisharing.tool.SolidRVBaseAdapter;
import com.ecslab.wifisharing.tool.WifiHotUtil;

import java.util.List;

import static com.ecslab.wifisharing.tool.WifiHotUtil.WIFI_AP_STATE_ENABLED;
import static com.ecslab.wifisharing.tool.WifiHotUtil.getInstance;

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
 * Date：2017/3/18
 * Email：biloba12345@gamil.com
 * Info：
 */

public class ShareHotspotFragment extends BaseFragment {

    /**
    *   View
    */
    private android.widget.TextView apnametext;
    private android.widget.LinearLayout openedll;
    private android.widget.LinearLayout closedll;
    private android.support.v4.widget.SwipeRefreshLayout refreshlayout;
    private TextView mConnectCountText;
    private RecyclerView mRecyclerView;

    /**
    *   Fragment
    */

    /**
    *   Data
    */
    private WifiHotUtil mWifiHotUtil;
    private List<ConnectDevice> mConnectDeviceList;
    private SolidRVBaseAdapter mAdapter;

    /**
    *   Tag
    */
    private static final String TAG = "ShareHotspotFragment";
    private TextView mNoConnectText;

    public static ShareHotspotFragment newInstance() {

        Bundle args = new Bundle();

        ShareHotspotFragment fragment = new ShareHotspotFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_share_hotspot,container,false);
    }

    @Override
    protected void initView(View view) {
        this.refreshlayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        refreshlayout.setColorSchemeColors(Color.RED,Color.BLUE);
        this.closedll = (LinearLayout) view.findViewById(R.id.closed_ll);
        this.openedll = (LinearLayout) view.findViewById(R.id.opened_ll);
        this.apnametext = (TextView) view.findViewById(R.id.ap_name_text);
        mConnectCountText = (TextView) view.findViewById(R.id.connect_count);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.connect_recycler_view);
        mNoConnectText = (TextView) view.findViewById(R.id.no_connect_text);
    }

    @Override
    protected void setListener() {
        //热点监听
        WiFiAPService.addWiFiAPListener(new WiFiAPListener() {
            @Override
            public void stateChanged(int state) {
                switch (state) {
                    case WiFiAPListener.WIFI_AP_CLOSE_SUCCESS:
                        hotspotClosed();
                        mConnectDeviceList.clear();
                        checkNoConnectDevice();
                        break;
                    case WiFiAPListener.WIFI_AP_OPEN_SUCCESS:
                        hotspotOpened();
                        break;
                }
            }
        });


        //刷新连接设备
        refreshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mConnectDeviceList=mWifiHotUtil.getConnectedDevices();
                mAdapter.notifyDataSetChanged();
                checkNoConnectDevice();
                refreshlayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void initData() {
        mWifiHotUtil=getInstance(getActivity());

        mConnectDeviceList=mWifiHotUtil.getConnectedDevices();
        mAdapter=new SolidRVBaseAdapter<ConnectDevice>(getActivity(),mConnectDeviceList ) {
            @Override
            protected void onBindDataToView(SolidCommonViewHolder holder, ConnectDevice bean) {
                holder.setText(R.id.name,bean.getIp());
            }

            @Override
            public int getItemLayoutID(int viewType) {
                return R.layout.item_connect;
            }

            @Override
            protected void onItemClick(int position, ConnectDevice bean) {
                super.onItemClick(position, bean);
                showInfoToast(bean.getMac());
            }
        };

        checkNoConnectDevice();
    }

    @Override
    protected void setData() {
        if (mWifiHotUtil.getWifiAPState()==WIFI_AP_STATE_ENABLED) {
            hotspotOpened();
        }else {
            hotspotClosed();
        }

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    @Override
    protected void onEventComing(EventCenter center) {

    }

    //热点关闭
    private void hotspotClosed(){
        openedll.setVisibility(View.GONE);
        closedll.setVisibility(View.VISIBLE);
    }

    //打开
    private void hotspotOpened(){
        closedll.setVisibility(View.GONE);
        openedll.setVisibility(View.VISIBLE);
        apnametext.setText(WifiHotUtil.getWifiName(getActivity(), mWifiHotUtil.getValidApSsid()));
    }

    //无设备连接
    private void checkNoConnectDevice(){
        if (mConnectDeviceList.size()>0) {
            mNoConnectText.setVisibility(View.GONE);
            mConnectCountText.setVisibility(View.VISIBLE);
            mConnectCountText.setText(mConnectDeviceList.size()+"台");
            mRecyclerView.setVisibility(View.VISIBLE);
        }else {
            mConnectCountText.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
            mNoConnectText.setVisibility(View.VISIBLE);
        }

    }

}
