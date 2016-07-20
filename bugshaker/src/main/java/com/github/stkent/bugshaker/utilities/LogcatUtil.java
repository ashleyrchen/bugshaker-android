package com.github.stkent.bugshaker.utilities;

import java.io.File;
import java.io.IOException;

import android.content.Context;

/**
 * Created by aschen on 7/20/16.
 */
public class LogcatUtil {

	private static File logFile;

	public static File saveLogcatToFile(Context context) {
		String fileName = "logcat.txt";
		logFile = new File(context.getExternalCacheDir(),fileName);

		@SuppressWarnings("unused")

		Process process;
		{
			try {
				process = Runtime.getRuntime().exec("logcat -f " + logFile.getAbsolutePath());
			}
			catch (IOException e) {
				System.out.println(e.toString());
			}
		}
		return logFile;
	}

	public static File getLogFile(){
		return logFile;
	}
}
