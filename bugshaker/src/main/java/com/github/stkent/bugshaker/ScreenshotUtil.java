package com.github.stkent.bugshaker;

import java.io.File;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.github.stkent.bugshaker.flow.email.screenshot.BasicScreenShotProvider;
import com.github.stkent.bugshaker.flow.email.screenshot.ScreenshotProvider;
import com.github.stkent.bugshaker.flow.email.screenshot.maps.MapScreenshotProvider;
import com.github.stkent.bugshaker.utilities.Logger;


public class ScreenshotUtil {

	private static final String DEFAULT_SCREENSHOT_NAME = "bug-reports";
	private static final String DEFAULT_SCREENSHOT = "latest-screenshot.jpg";
	public static final String ANNOTATED_SCREENSHOT = "Screenshot.png";
	public static final String ANNOTATED_SCREENSHOT_NAME = "drawing";

	public static File getScreenshotFile(@NonNull final Context applicationContext) {
		final File screenshotsDir = new File(
			applicationContext.getFilesDir(), DEFAULT_SCREENSHOT_NAME);

		screenshotsDir.mkdirs();

		return new File(screenshotsDir, DEFAULT_SCREENSHOT);
	}

	public static ScreenshotProvider getScreenshotProvider(Application application) {
		Logger logger = new Logger(true);
		try {
			Class.forName(
				"com.google.android.gms.maps.GoogleMap",
				false,
				BugShaker.class.getClassLoader());

			logger.d("Detected that embedding app includes Google Maps as a dependency.");

			return new MapScreenshotProvider(application, logger);
		}
		catch (final ClassNotFoundException e) {
			logger.d("Detected that embedding app does not include Google Maps as a dependency.");

			return new BasicScreenShotProvider(application, logger);
		}
	}
}
