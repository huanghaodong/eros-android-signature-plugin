package com.example.huanghaodong.signature;

import android.widget.Toast;

import com.alibaba.weex.plugin.annotation.WeexModule;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import java.util.Map;

@WeexModule(name = "sign", lazyLoad = true)
public class SignaturePlugin extends WXModule {

    @JSMethod(uiThread = true)
    public void open(JSCallback resultCallback) {
        // new SignatureModule().setContext(mWXSDKInstance.getContext()).openAssetMusics();
        //Toast.makeText(mWXSDKInstance.getContext(), "Hello Eros test Plugin", Toast.LENGTH_LONG).show();
        new SignatureModule().setContext(mWXSDKInstance.getContext()).setCallback(resultCallback).pushToView();
        SinatureCallback.callback = resultCallback;
    }
}
