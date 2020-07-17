package com.omottec.wechathook;

import android.util.Log;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import java.util.Arrays;

public class WCDB implements IXposedHookLoadPackage {
    public static final String OPEN_DATABASE = "openDatabase";
    public static final String INSERT_WITH_ON_CONFLICT = "insertWithOnConflict";
    public static final String DELETE_TAG = "qbbDelete";
    public static final String DELETE_METHOD = "delete";
    public static final String UPDATE_WITH_ON_CONFLICT = "updateWithOnConflict";
    public static final String RAW_QUERY_WITH_FACTORY = "rawQueryWithFactory";
    private int mInsertCount;
    private int mDeleteCount;
    private int mUpdateCount;
    private int mSelectCount;
    private int mPragmaCount;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!"com.tencent.mm".equals(lpparam.packageName)) return;

        // https://github.com/Tencent/wcdb/blob/master/android/wcdb/src/com/tencent/wcdb/database/SQLiteDatabase.java

        ClassLoader classLoader = lpparam.classLoader;
        XposedHelpers.findAndHookMethod("com.tencent.wcdb.database.SQLiteDatabase",
            classLoader,
            OPEN_DATABASE,
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
                    Log.i(OPEN_DATABASE,"before*********************");
                    Log.i(OPEN_DATABASE, "this:" + param.thisObject);
                    Log.i(OPEN_DATABASE,"path:" + param.args[0]);
                    if (param.args[1] != null) {
                        Log.i(OPEN_DATABASE,"password:" + new String((byte[]) param.args[1], "UTF-8"));
                    }
                    if (param.args[2] != null) {
                        Log.i(OPEN_DATABASE,"kdfIteration:" + XposedHelpers.getIntField(param.args[2], "kdfIteration"));
                        Log.i(OPEN_DATABASE,"hmacEnabled:" + XposedHelpers.getBooleanField(param.args[2], "hmacEnabled"));
                        Log.i(OPEN_DATABASE,"hmacAlgorithm:" + XposedHelpers.getIntField(param.args[2], "hmacAlgorithm"));
                        Log.i(OPEN_DATABASE,"kdfAlgorithm:" + XposedHelpers.getIntField(param.args[2], "kdfAlgorithm"));
                        Log.i(OPEN_DATABASE,"pageSize:" + XposedHelpers.getIntField(param.args[2],"pageSize"));
                    }
                    Log.i(OPEN_DATABASE,"flags:" + Integer.toHexString((Integer) param.args[4]));
                    Log.i(OPEN_DATABASE,"poolSize:" + param.args[6]);
                }
            });

        XposedHelpers.findAndHookMethod("com.tencent.wcdb.database.SQLiteDatabase", classLoader,
            INSERT_WITH_ON_CONFLICT,
            String.class,
            String.class,
            classLoader.loadClass("android.content.ContentValues"),
            int.class,
            new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Log.i(INSERT_WITH_ON_CONFLICT,"before=====================");
                    Log.i(INSERT_WITH_ON_CONFLICT, "this:" + param.thisObject);
                    Log.i(INSERT_WITH_ON_CONFLICT, "table:" + param.args[0]);
                    Log.i(INSERT_WITH_ON_CONFLICT, "nullColumnHack:" + param.args[1]);
                    Log.i(INSERT_WITH_ON_CONFLICT, "initialValues:" + param.args[2]);
                    Log.i(INSERT_WITH_ON_CONFLICT, "conflictAlgorithm:" + param.args[3]);
                    synchronized (this) {
                        Log.i(INSERT_WITH_ON_CONFLICT,"mInsertCount:" + (++mInsertCount));
                    }
                }
            });

        XposedHelpers.findAndHookMethod("com.tencent.wcdb.database.SQLiteDatabase", classLoader,
            DELETE_METHOD,
            String.class,
            String.class,
            String[].class,
            new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Log.i(DELETE_TAG, "before=====================");
                    Log.i(DELETE_TAG, "this:" + param.thisObject);
                    Log.i(DELETE_TAG, "table:" + param.args[0]);
                    Log.i(DELETE_TAG, "whereClause:" + param.args[1]);
                    Log.i(DELETE_TAG, "whereArgs:" + param.args[2]);
                    synchronized (this) {
                        Log.i(DELETE_TAG,"mDeleteCount:" + (++mDeleteCount));
                    }
                }
            });

        XposedHelpers.findAndHookMethod("com.tencent.wcdb.database.SQLiteDatabase", classLoader,
            UPDATE_WITH_ON_CONFLICT,
            String.class,
            classLoader.loadClass("android.content.ContentValues"),
            String.class,
            String[].class,
            int.class,
            new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Log.i(UPDATE_WITH_ON_CONFLICT,"before=====================");
                    Log.i(UPDATE_WITH_ON_CONFLICT, "this:" + param.thisObject);
                    Log.i(UPDATE_WITH_ON_CONFLICT, "table:" + param.args[0]);
                    Log.i(UPDATE_WITH_ON_CONFLICT, "values:" + param.args[1]);
                    Log.i(UPDATE_WITH_ON_CONFLICT, "whereClause:" + param.args[2]);
                    Log.i(UPDATE_WITH_ON_CONFLICT, "whereArgs:" + Arrays.toString(
                        (Object[]) param.args[3]));
                    Log.i(UPDATE_WITH_ON_CONFLICT, "conflictAlgorithm:" + param.args[4]);
                    synchronized (this) {
                        Log.i(UPDATE_WITH_ON_CONFLICT,"mUpdateCount:" + (++mUpdateCount));
                    }
                }
            });

        XposedHelpers.findAndHookMethod("com.tencent.wcdb.database.SQLiteDatabase", classLoader,
            RAW_QUERY_WITH_FACTORY,
            classLoader.loadClass("com.tencent.wcdb.database.SQLiteDatabase$CursorFactory"),
            String.class,
            Object[].class,
            String.class,
            classLoader.loadClass("com.tencent.wcdb.support.CancellationSignal"),
            new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Log.i(RAW_QUERY_WITH_FACTORY,"before=====================");
                    Log.i(RAW_QUERY_WITH_FACTORY, "this:" + param.thisObject);
                    String sql = (String) param.args[1];
                    Log.i(RAW_QUERY_WITH_FACTORY,"sql:" + sql);
                    Log.i(RAW_QUERY_WITH_FACTORY,"selectionArgs:" + Arrays.toString((Object[]) param.args[2]));
                    Log.i(RAW_QUERY_WITH_FACTORY,"editTable:" + param.args[3]);
                    if (sql.toUpperCase().startsWith("SELECT")) {
                        synchronized (this) {
                            mSelectCount++;
                            Log.i(RAW_QUERY_WITH_FACTORY,"mSelectCount:" + mSelectCount + ", mPragmaCount:" + mPragmaCount);
                        }
                    }
                    if (sql.toUpperCase().startsWith("PRAGMA")) {
                        synchronized (this) {
                            mPragmaCount++;
                            Log.i(RAW_QUERY_WITH_FACTORY,"mSelectCount:" + mSelectCount + ", mPragmaCount:" + mPragmaCount);
                        }
                    }
                }
            });
    }
}
