package com.zacharee1.systemuituner.fragmenthelpers;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.SwitchPreference;
import android.provider.Settings;

import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.activites.apppickers.AppsListActivity;
import com.zacharee1.systemuituner.fragments.ItemDetailFragment;
import com.zacharee1.systemuituner.util.SettingsUtils;
import com.zacharee1.systemuituner.prefs.IconPreference;

public class LockHelper extends BaseHelper {
    public LockHelper(ItemDetailFragment fragment) {
        super(fragment);
        setEnabled();
        setLockIconStuff();
        setShortcutSwitchListeners();
        setResetListeners();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onResume() {
        setLockSummaryAndIcon();
    }

    private void setEnabled() {
        PreferenceCategory shortcuts = (PreferenceCategory) findPreference("lockscreen_shortcuts");
        Preference oreoMsg = findPreference("oreo_needed");
        boolean isOreo = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1;

        shortcuts.setEnabled(isOreo);
        if (isOreo) shortcuts.removePreference(oreoMsg);
    }

    private void setShortcutSwitchListeners() {
        SwitchPreference left = (SwitchPreference) findPreference("sysui_keyguard_left_unlock");
        SwitchPreference right = (SwitchPreference) findPreference("sysui_keyguard_right_unlock");
        Preference.OnPreferenceChangeListener    listener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SettingsUtils.writeSecure(getContext(), preference.getKey(), newValue.toString());
                return true;
            }
        };

        left.setOnPreferenceChangeListener(listener);
        right.setOnPreferenceChangeListener(listener);
    }

    private void setLockIconStuff() {
        setLockSummaryAndIcon();

        final Preference left = findPreference("choose_left");
        left.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent activity = new Intent(getContext(), AppsListActivity.class);
                activity.putExtra("isLeft", true);
                getActivity().startActivityForResult(activity, 1337);
                return true;
            }
        });

        Preference right = findPreference("choose_right");
        right.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent activity = new Intent(getContext(), AppsListActivity.class);
                activity.putExtra("isLeft", false);
                getActivity().startActivityForResult(activity, 1337);
                return true;
            }
        });
    }

    private void setLockSummaryAndIcon() {
        IconPreference leftLock = (IconPreference) findPreference("choose_left");
        IconPreference rightLock = (IconPreference) findPreference("choose_right");

        String leftSum = Settings.Secure.getString(getContext().getContentResolver(), "sysui_keyguard_left");
        String rightSum = Settings.Secure.getString(getContext().getContentResolver(), "sysui_keyguard_right");

        String[] leftStuff = null;
        String[] rightStuff = null;

        if (leftSum != null) leftStuff = leftSum.split("[/]");
        if (rightSum != null) rightStuff = rightSum.split("[/]");

//        if (leftSum != null) leftSum = leftSum.replace("/.", "");
//        if (rightSum != null) rightSum = rightSum.replace("/.", "");

        leftLock.setSummary(leftSum);
        rightLock.setSummary(rightSum);

        PackageManager pm = getActivity().getPackageManager();

        Drawable unknown = getContext().getResources().getDrawable(R.drawable.ic_help_outline_black_24dp, null);
        unknown.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        try {
            leftLock.setIcon(pm.getActivityIcon(new ComponentName(leftStuff[0], leftStuff[1])));
        } catch (Exception e) {
            leftLock.setIcon(unknown);
        }

        try {
            rightLock.setIcon(pm.getActivityIcon(new ComponentName(rightStuff[0], rightStuff[1])));
        } catch (Exception e) {
            rightLock.setIcon(unknown);
        }
    }

    private void setResetListeners() {
        Preference resetLeft = findPreference("reset_left");
        Preference resetRight = findPreference("reset_right");

        resetLeft.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SettingsUtils.writeSecure(getContext(), "sysui_keyguard_left", "");
                setLockSummaryAndIcon();
                return true;
            }
        });
        resetRight.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SettingsUtils.writeSecure(getContext(), "sysui_keyguard_right", "");
                setLockSummaryAndIcon();
                return true;
            }
        });
    }
}
