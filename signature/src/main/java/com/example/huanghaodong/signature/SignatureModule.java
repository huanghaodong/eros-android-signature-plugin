package com.example.huanghaodong.signature;

import android.content.Context;
import android.content.Intent;


import com.taobao.weex.bridge.JSCallback;

public class SignatureModule {
    private Context context;
    private JSCallback callback;

    public SignatureModule setContext(Context context) {
        this.context = context;
        return this;
    }

    public SignatureModule setCallback(final JSCallback callback) {

        this.callback = callback;
        return this;

    }

    public void pushToView() {
        Intent intentScan = new Intent(this.context, SignActivity.class);
        this.context.startActivity(intentScan);
    }
}
