package com.wjn.retrofitrxjavasample.view;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wjn.retrofitrxjavasample.R;
import com.wjn.retrofitrxjavasample.model.HttpSubscriber;
import com.wjn.retrofitrxjavasample.model.IPInfo;
import com.wjn.retrofitrxjavasample.network.ApiException;
import com.wjn.retrofitrxjavasample.network.HttpMethod;
import com.wjn.retrofitrxjavasample.network.SubscriberOnNextListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Context mContext;

    private Button btnGetData;
    private TextView tvData;

    private HttpSubscriber httpSubscriber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = MainActivity.this;
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        btnGetData = (Button)this.findViewById(R.id.btn_get_data);
        tvData = (TextView)this.findViewById(R.id.tv_data);
        btnGetData.setOnClickListener(this);
    }

    SubscriberOnNextListener<IPInfo> onNextListener = new SubscriberOnNextListener<IPInfo>() {
        @Override
        public void onStart() {
            DialogMaker.showProgressDialog(mContext, "");
        }

        @Override
        public void onNext(IPInfo ipInfo) {
            DialogMaker.dismissProgressDialog();
            tvData.setText(ipInfo.getArea()+","+ipInfo.getCity());
        }

        @Override
        public void onCompleted() {

        }

        @Override
        public void onApiError(ApiException e) {
            DialogMaker.dismissProgressDialog();
            Toast.makeText(mContext,e.getMessage(),Toast.LENGTH_LONG).show();
        }

        @Override
        public void onNetworkError(Throwable e) {
            DialogMaker.dismissProgressDialog();
            Toast.makeText(mContext,"internet unavailable",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onParseError(Throwable e) {
            DialogMaker.dismissProgressDialog();
            Toast.makeText(mContext,"server error",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onOtherError(Throwable e) {
            DialogMaker.dismissProgressDialog();
        }
    };

    private void getIPInfo(){
        unSub();
        httpSubscriber = new HttpSubscriber<>(onNextListener);
        Map<String,String> params = new HashMap<>();
        params.put("ip","119.123.182.118");
        HttpMethod.getInstance().getIPInfo(httpSubscriber,params);
    }

    /**
     * 释放资源
     */
    private void unSub(){
        if (httpSubscriber!=null){
            httpSubscriber.unsubscribe();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_get_data:
                getIPInfo();
                break;
        }
    }
}
