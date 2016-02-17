package com.ichangmao.app.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.ichangmao.app.R;
import com.ichangmao.commons.MaoLog;
import com.ichangmao.app.ui.MainFragment.OnMainFragmentInteraction;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnMainFragmentInteraction {

    private MaoLog log = MaoLog.getLoger(this.getClass().getSimpleName());

    private static final List<FuncItem> FUNC_ITEMS = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log.i("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            MainFragment mainFragment = new MainFragment();
            mainFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mainFragment).commit();
        }
    }

    @Override
    protected void onResume() {
        log.i("onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        log.i("onPause");
        super.onPause();
    }

    @Override
    protected void onStart() {
        log.i("onStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        log.i("onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        log.i("onDestroy");
        super.onDestroy();
    }

    @Override
    public void onFuncItemClicked(FuncItem item) {
        log.i("onFuncItemClicked " + item.name);

        //根据item决定跳转到哪个Fragment
        Fragment fragment = null;
        if (nsdItem == item) {
            //Network Service Discovery
            fragment = new NsdFragment();

        }

        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public List<FuncItem> getFuncItems() {
        return FUNC_ITEMS;
    }


    //定义功能项
    static final FuncItem nsdItem = new FuncItem("nsd");

    //将功能项加入到FUNC_ITEMS
    static {
        FUNC_ITEMS.add(nsdItem);
    }
}
