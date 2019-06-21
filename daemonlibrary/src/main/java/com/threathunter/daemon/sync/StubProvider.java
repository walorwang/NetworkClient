package com.threathunter.daemon.sync;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import androidx.annotation.Nullable;

/**
 *
 * @ProjectName:    PrivacySecurity
 * @Package:        com.threathunter.daemon.sync
 * @ClassName:      StubProvider
 * @Description:    java类作用描述
 * @Author:         walorwang
 * @CreateDate:     2019/3/25 11:05
 * @UpdateUser:     更新者
 * @UpdateDate:     2019/3/25 11:05
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
public class StubProvider extends ContentProvider {
    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}