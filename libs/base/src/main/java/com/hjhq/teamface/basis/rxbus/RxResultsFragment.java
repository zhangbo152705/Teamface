/*
package com.hjhq.teamface.basis.rxbus;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.tbruyelle.rxpermissions.Permission;
import com.tbruyelle.rxpermissions.RxPermissions;

import rx.subjects.PublishSubject;

public class RxResultsFragment extends Fragment {

    private static final int PERMISSIONS_REQUEST_CODE = 42;

    private boolean mLogging;

    public RxResultsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        PublishSubject<Integer> subject =
        if (subject == null) {
            // No subject found
            Log.e(RxPermissions.TAG, "RxPermissions.onRequestPermissionsResult invoked but didn't find the corresponding permission request.");
            return;
        }
        mSubjects.remove(permissions[i]);
        boolean granted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
        subject.onNext(new Permission(permissions[i], granted, shouldShowRequestPermissionRationale[i]));
        subject.onCompleted();
        super.onActivityResult(requestCode, resultCode, data);
    }
}*/
