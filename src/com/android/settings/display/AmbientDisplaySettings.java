/*
 * Copyright (C) 2020 shagbag913
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

package com.android.settings.display;

import android.app.settings.SettingsEnums;
import android.content.Context;
import android.hardware.display.AmbientDisplayConfiguration;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;

import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.search.SearchIndexable;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SearchIndexable
public class AmbientDisplaySettings extends DashboardFragment {

    private static final String TAG = "AmbientDisplaySettings";
    private static final String FAKE_PREF_KEY = "fake_key_only_for_get_available";

    private static Boolean mIsAmbientAvailable;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle());
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(Context context,
            Lifecycle lifecycle) {
        List<AbstractPreferenceController> controllers = new ArrayList<>();
        if (isAmbientAvailable(context)) {
            controllers.add(new AmbientDisplayPreferenceController(context, lifecycle));
            controllers.add(new AmbientDisplaySwitchPreferenceController(context, lifecycle));
        }
        return controllers;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.REVENGEOS;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.ambient_display_settings;
    }

    public static final SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider() {
                @Override
                public List<SearchIndexableResource> getXmlResourcesToIndex(
                        Context context, boolean enabled) {
                    final SearchIndexableResource sir = new SearchIndexableResource(context);
                    if (isAmbientAvailable(context)) {
                        sir.xmlResId = R.xml.ambient_display_settings;
                    }
                    return Arrays.asList(sir);
                }

                @Override
                public List<AbstractPreferenceController> createPreferenceControllers(
                        Context context) {
                    return buildPreferenceControllers(context, null);
                }
            };

    private static Boolean isAmbientAvailable(Context context) {
        if (mIsAmbientAvailable == null) {
            AmbientDisplayConfiguration config = new AmbientDisplayConfiguration(context);
            mIsAmbientAvailable = config.alwaysOnAvailableForUser(UserHandle.USER_CURRENT);
        }
        return mIsAmbientAvailable;
    }
}
