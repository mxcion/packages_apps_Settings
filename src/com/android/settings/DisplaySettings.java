/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.android.settings;

import android.app.settings.SettingsEnums;
import android.content.Context;
import android.os.Bundle;

import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.display.BrightnessLevelPreferenceController;
import com.android.settings.display.CameraGesturePreferenceController;
import com.android.settings.display.LiftToWakePreferenceController;
import com.android.settings.display.PocketJudgePreferenceController;
import com.android.settings.display.ShowOperatorNamePreferenceController;
import com.android.settings.display.TapToWakePreferenceController;
import com.android.settings.display.EnableBlursPreferenceController;
import com.android.settings.display.ThemePreferenceController;
import com.android.settings.display.VrDisplayPreferenceController;
import com.android.settings.display.EmulateDisplayCutoutPreferenceController;
import com.android.settings.display.OverlayCategoryPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.search.SearchIndexable;

import java.util.ArrayList;
import java.util.List;

import android.content.om.IOverlayManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;

import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import androidx.preference.Preference.OnPreferenceChangeListener;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class DisplaySettings extends DashboardFragment implements OnPreferenceChangeListener {
    private static final String TAG = "DisplaySettings";

    private SwitchPreference mPitchPreference;
    IOverlayManager mOverlayManager;

    @Override
    public int getMetricsCategory() {
        return SettingsEnums.DISPLAY;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        super.onCreatePreferences(savedInstanceState, rootKey);
        mOverlayManager = IOverlayManager.Stub.asInterface(ServiceManager.getService("overlay"));
        mPitchPreference = findPreference("pitch_theme");
        try {
            mPitchPreference.setChecked(mOverlayManager.getOverlayInfo("com.kraken.pitchblacksystem", UserHandle.USER_CURRENT).isEnabled());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mPitchPreference.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mPitchPreference) {
            setOverlay("com.kraken.pitchblacksystem", (Boolean) newValue);
            setOverlay("com.kraken.pitchblacksettings", (Boolean) newValue);
            setOverlay("com.kraken.pitchblacksystemui", (Boolean) newValue);
            return true;
        }
        return false;
    }

    private void setOverlay(String overlay, boolean status) {
       try {
            mOverlayManager.setEnabled(overlay, status, UserHandle.USER_CURRENT);
        } catch (RemoteException | IllegalStateException | SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.display_settings;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
    }

    @Override
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle());
    }

    @Override
    public int getHelpResource() {
        return R.string.help_uri_display;
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(
            Context context, Lifecycle lifecycle) {
        final List<AbstractPreferenceController> controllers = new ArrayList<>();
        controllers.add(new CameraGesturePreferenceController(context));
        controllers.add(new LiftToWakePreferenceController(context));
        controllers.add(new PocketJudgePreferenceController(context));
        controllers.add(new TapToWakePreferenceController(context));
        controllers.add(new EnableBlursPreferenceController(context));
        controllers.add(new VrDisplayPreferenceController(context));
        controllers.add(new ShowOperatorNamePreferenceController(context));
        controllers.add(new ThemePreferenceController(context));
        controllers.add(new EmulateDisplayCutoutPreferenceController(context));
        controllers.add(new BrightnessLevelPreferenceController(context, lifecycle));
        controllers.add(new OverlayCategoryPreferenceController(context,
                "android.theme.customization.font"));
        controllers.add(new OverlayCategoryPreferenceController(context,
                "android.theme.customization.adaptive_icon_shape"));
        controllers.add(new OverlayCategoryPreferenceController(context,
                "android.theme.customization.icon_pack"));
        return controllers;
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.display_settings) {

                @Override
                public List<AbstractPreferenceController> createPreferenceControllers(
                        Context context) {
                    return buildPreferenceControllers(context, null);
                }
            };
}
