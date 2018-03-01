package com.ldsight.util;

import android.app.ProgressDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadFilesTask {

	/**
	 * 下载更新apk包
	 *
	 * @param path
	 *            下载地址
	 * @param filePath
	 *            sd卡文件目录地址
	 * @throws Exception
	 */
	public static File DownloadFiles(String path, String filePath,
									 ProgressDialog pd) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(5000);
		conn.setRequestMethod("GET");
		if (conn.getResponseCode() == 200) {
			InputStream is = conn.getInputStream();
			pd.setMax(conn.getContentLength());
			File file = new File(filePath);
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int len = 0;
			int progressDialog = 0;

			while ((len = is.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
				progressDialog += len;
				pd.setProgress(progressDialog);

			}
			fos.flush();
			fos.close();
			is.close();

			return file;
		}
		return null;
	}

}
