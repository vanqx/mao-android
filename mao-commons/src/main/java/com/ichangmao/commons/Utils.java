package com.ichangmao.commons;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

/**
 * Created by yangchangmao on 2016/2/16.
 */
public class Utils {
    private static MaoLog log = MaoLog.getLoger("commons.Utils");

    /**
     * 获取账号名，优先返回google账号
     */
    public static String getAccountName(Context context) {
        Account[] accounts = AccountManager.get(context).getAccounts();
        String accountName = null;
        for (Account ac : accounts) {
            if (ac.type.equalsIgnoreCase("com.google")) {
                accountName = ac.name;
                break;
            }
        }

        if (TextUtils.isEmpty(accountName) && accounts.length > 0) {
            accountName = accounts[0].name;
        }

        if (TextUtils.isEmpty(accountName)) {
            accountName = getXAID(context);
        }

        if (TextUtils.isEmpty(accountName)) {
            accountName = "UNKNOWN";
        }

        log.d("AccountName:" + accountName);
        return accountName;
    }

    /**
     * 获取android id
     */
    public static String getXAID(Context context) {
        ContentResolver cr = context.getContentResolver();
        return Settings.Secure.getString(cr, Settings.Secure.ANDROID_ID);
    }
}
