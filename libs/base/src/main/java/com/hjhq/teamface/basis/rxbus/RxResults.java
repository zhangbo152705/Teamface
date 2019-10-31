/*
package com.hjhq.teamface.basis.rxbus;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.tbruyelle.rxpermissions.Permission;

import rx.Observable;
import rx.functions.Func1;

public class RxResults {

    static final String TAG = "RxPermissions";

    RxResultsFragment mRxPermissionsFragment;
    Activity mActivity;

    public RxResults(@NonNull Activity activity) {
        mRxPermissionsFragment = getRxPermissionsFragment(activity);
        mActivity = activity;
    }

    private RxResultsFragment getRxPermissionsFragment(Activity activity) {
        RxResultsFragment rxPermissionsFragment = findRxPermissionsFragment(activity);
        boolean isNewInstance = rxPermissionsFragment == null;
        if (isNewInstance) {
            rxPermissionsFragment = new RxResultsFragment();
            FragmentManager fragmentManager = activity.getFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .add(rxPermissionsFragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return rxPermissionsFragment;
    }

    private RxResultsFragment findRxPermissionsFragment(Activity activity) {
        return (RxResultsFragment) activity.getFragmentManager().findFragmentByTag(TAG);
    }

    */
/**
     * Map emitted items from the source observable into {@code true} if permissions in parameters
     * are granted, or {@code false} if not.
     * <p>
     * If one or several permissions have never been requested, invoke the related framework method
     * to ask the user if he allows the permissions.
     *//*

    @SuppressWarnings("WeakerAccess")
    public Observable.Transformer<Object, Boolean> ensure(final String... permissions) {
        return null;
    }


   */
/* @SuppressWarnings("WeakerAccess")
    public Observable.Transformer<Integer, Intent> ensureEach(final String... permissions) {
        return new Observable.Transformer<Integer, Intent>() {
            @Override
            public Observable<Intent> call(Observable<Integer> o) {



            }
        };
    }*//*


    */
/**
     * Request permissions immediately, <b>must be invoked during initialization phase
     * of your application</b>.
     *//*

    @SuppressWarnings({"WeakerAccess", "unused"})
    public Observable<Boolean> request(final String... permissions) {
        return Observable.just(null).compose(ensure(permissions));
    }

    */
/**
     * Request permissions immediately, <b>must be invoked during initialization phase
     * of your application</b>.
     *//*


    private Observable<Permission> startActivityForResult(int reqCode, Class clazz, Bundle bundle) {
        Intent intent = new Intent(mActivity, clazz.getClass());
        mRxPermissionsFragment.startActivityForResult(intent, reqCode);


        return Observable.just(null).flatMap()

    }


}*/
