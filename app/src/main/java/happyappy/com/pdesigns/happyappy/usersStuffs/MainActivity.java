/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package happyappy.com.pdesigns.happyappy.usersStuffs;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;


import happyappy.com.pdesigns.happyappy.ApplicationResources.AppStatus;
import happyappy.com.pdesigns.happyappy.BuildConfig;
import happyappy.com.pdesigns.happyappy.ApplicationResources.OsUtils;
/**
 * Simple FragmentActivity to hold the main {@link MainFragment} and not much else.
 */
public class MainActivity extends FragmentActivity {
    private static final String TAG = "ImageGridActivity";
    ProgressDialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (BuildConfig.DEBUG) {
            OsUtils.enableStrictMode();
        }

        super.onCreate(savedInstanceState);

        if (AppStatus.getInstance(this).isOnline()) {

        if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, new MainFragment(), TAG);
            ft.commit();
        }
        } else {
                myDialog = new ProgressDialog(MainActivity.this);
                myDialog.setMessage("Please check your network connection and restart the app");
                myDialog.setIndeterminate(false);
                myDialog.setCancelable(true);
                myDialog.show();
    }
    }
}
