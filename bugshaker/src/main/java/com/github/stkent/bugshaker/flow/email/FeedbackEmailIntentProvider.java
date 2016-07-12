/**
 * Copyright 2016 Stuart Kent
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.github.stkent.bugshaker.flow.email;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.stkent.bugshaker.utilities.StringUtils;

public final class FeedbackEmailIntentProvider {

    private static final String DEFAULT_EMAIL_SUBJECT_LINE_SUFFIX = " Android App Feedback";

    @NonNull
    private final Context applicationContext;

    @NonNull
    private final GenericEmailIntentProvider genericEmailIntentProvider;

    public FeedbackEmailIntentProvider(
            @NonNull final Context applicationContext,
            @NonNull final GenericEmailIntentProvider genericEmailIntentProvider) {

        this.applicationContext = applicationContext;
        this.genericEmailIntentProvider = genericEmailIntentProvider;
    }

    @NonNull
    Intent getFeedbackEmailIntent(
            @NonNull final String[] emailAddresses,
            @Nullable final String userProvidedEmailSubjectLine) {

        final String appInfo = getApplicationInfoString();
        final String emailSubjectLine = getEmailSubjectLine(userProvidedEmailSubjectLine);

        return genericEmailIntentProvider
                .getEmailIntent(emailAddresses, emailSubjectLine, appInfo);
    }

    @NonNull
    Intent getFeedbackEmailIntent(
            @NonNull final String[] emailAddresses,
            @Nullable final String userProvidedEmailSubjectLine,
            @NonNull final Uri screenshotUri,
            @NonNull final Uri fileName
        ) {

        final String appInfo = getApplicationInfoString();
        final String emailSubjectLine = getEmailSubjectLine(userProvidedEmailSubjectLine);

        return genericEmailIntentProvider
                .getEmailWithAttachmentIntent(
                        emailAddresses, emailSubjectLine, appInfo, screenshotUri, fileName);
    }

    @NonNull
    Intent getFeedbackEmailIntent(
        @NonNull final String[] emailAddresses,
        @Nullable final String userProvidedEmailSubjectLine,
        @NonNull final Uri screenshotUri
    ) {

        final String appInfo = getApplicationInfoString();
        final String emailSubjectLine = getEmailSubjectLine(userProvidedEmailSubjectLine);

        return genericEmailIntentProvider
            .getEmailWithAttachmentIntent(
                emailAddresses, emailSubjectLine, appInfo, screenshotUri);
    }

    @NonNull
    private CharSequence getApplicationName() {
        return applicationContext.getApplicationInfo()
                .loadLabel(applicationContext.getPackageManager());
    }

    @NonNull
    private String getApplicationInfoString() {
        return    "My Device: " + getDeviceName()
                + "\n"
                + "App Version: " + getVersionDisplayString()
                + "\n"
                + "Android Version: " + getAndroidOsVersionDisplayString()
                + "\n"
                + "Time Stamp: " + getCurrentUtcTimeStringForDate(new Date())
                + "\n"
                + "Log: " + Log.d("BugShaker-Library", "Test".toString())
                + "\n"
                + "ID: " + Build.ID
                + "\n"
                + "Display: " + Build.DISPLAY
                + "---------------------"
                + "\n\n";
    }

    @NonNull
    private String getEmailSubjectLine(@Nullable final String userProvidedEmailSubjectLine) {
        if (userProvidedEmailSubjectLine != null) {
            return userProvidedEmailSubjectLine;
        }

        return getApplicationName() + DEFAULT_EMAIL_SUBJECT_LINE_SUFFIX;
    }

    @NonNull
    private String getDeviceName() {
        final String manufacturer = Build.MANUFACTURER;
        final String model = Build.MODEL;

        String deviceName;

        if (model.startsWith(manufacturer)) {
            deviceName = model;
        } else {
            deviceName = manufacturer + " " + model;
        }

        return StringUtils.capitalizeFully(deviceName);
    }

    @NonNull
    private String getVersionDisplayString() {
        try {
            final PackageManager packageManager = applicationContext.getPackageManager();
            final PackageInfo packageInfo
                    = packageManager.getPackageInfo(applicationContext.getPackageName(), 0);

            final String applicationVersionName = packageInfo.versionName;
            final int applicationVersionCode = packageInfo.versionCode;

            return String.format("%s (%s)", applicationVersionName, applicationVersionCode);
        } catch (final PackageManager.NameNotFoundException e) {
            return "Unknown Version";
        }
    }

    @NonNull
    private String getAndroidOsVersionDisplayString() {
        return Build.VERSION.RELEASE + " (" + Build.VERSION.SDK_INT + ")";
    }

    @NonNull
    private String getCurrentUtcTimeStringForDate(final Date date) {
        final SimpleDateFormat simpleDateFormat
                = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss z", Locale.getDefault());

        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        return simpleDateFormat.format(date);
    }

}
