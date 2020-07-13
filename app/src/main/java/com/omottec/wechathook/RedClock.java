package com.omottec.wechathook;

import android.graphics.Color;
import android.widget.TextView;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class RedClock implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.log("Loaded app: " + lpparam.packageName);
        if (!lpparam.packageName.equals("com.android.systemui")) return;
        XposedHelpers.findAndHookMethod("com.android.systemui.statusbar.policy.Clock",
            lpparam.classLoader,
            "updateClock",
            new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    TextView tv = (TextView) param.thisObject;
                    tv.setText(tv.getText() + " :)");
                    tv.setTextColor(Color.RED);
                }
            });
    }
}
