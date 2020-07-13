package com.omottec.wechathook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class WCDB implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!"com.tencent.mm".equals(lpparam.packageName)) return;

        // https://github.com/Tencent/wcdb/blob/master/android/wcdb/src/com/tencent/wcdb/database/SQLiteDatabase.java

        ClassLoader classLoader = lpparam.classLoader;
        XposedHelpers.findAndHookMethod("com.tencent.wcdb.database.SQLiteDatabase",
            classLoader,
            "openDatabase",
            String.class,
            byte[].class,
            classLoader.loadClass("com.tencent.wcdb.database.SQLiteCipherSpec"),
            classLoader.loadClass("com.tencent.wcdb.database.SQLiteDatabase$CursorFactory"),
            int.class,
            classLoader.loadClass("com.tencent.wcdb.DatabaseErrorHandler"),
            int.class,
            new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("path:" + param.args[0]);
                    if (param.args[1] != null) {
                        XposedBridge.log("password:" + new String((byte[]) param.args[1], "UTF-8"));
                    }
                    if (param.args[2] != null) {
                        XposedBridge.log("kdfIteration:" + XposedHelpers.getIntField(param.args[2], "kdfIteration"));
                        XposedBridge.log("hmacEnabled:" + XposedHelpers.getBooleanField(param.args[2], "hmacEnabled"));
                        XposedBridge.log("hmacAlgorithm:" + XposedHelpers.getIntField(param.args[2], "hmacAlgorithm"));
                        XposedBridge.log("kdfAlgorithm:" + XposedHelpers.getIntField(param.args[2], "kdfAlgorithm"));
                        XposedBridge.log("pageSize:" + XposedHelpers.getIntField(param.args[2],"pageSize"));
                    }
                    XposedBridge.log("flags:" + Integer.toHexString((Integer) param.args[4]));
                    XposedBridge.log("poolSize:" + param.args[6]);
                }
            });
    }
}
