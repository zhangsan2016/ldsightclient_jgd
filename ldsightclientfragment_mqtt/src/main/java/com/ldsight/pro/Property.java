package com.ldsight.pro;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Property {
	String DB_NAME = "error.property.db";
	private SQLiteDatabase database;
	private Context context;
	File extDir = null;

	public Property(Context context) {
		this.context = context;
		String pkName = context.getPackageName();
		extDir = new File("/data/data/"+ pkName + "/databases/");
		File f = new File(extDir, DB_NAME);
		File file = new File(extDir, DB_NAME);
		// System.out.println("property_" + file);
		if (!file.exists()) {
			prepareDB();
		}
		database = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE,
				null);
	}

	public String getContent(String columns) {
		String sql = "select " + columns + " from user_table";
		Cursor c = database.rawQuery(sql, null);
		String uuid = null;
		while (c.moveToNext()) {
			uuid = c.getString(c.getColumnIndex(columns));
		}
		return uuid;
	}

	/**
	 * 准备数据库
	 */
	public void prepareDB() {
		if ((new File(extDir, DB_NAME)).exists() == false) {
			// 如 SQLite 数据库文件不存在，再检查一下 database 目录是否存在
			// File f = new File(extDir, DB_NAME);
			// 如 database 目录不存在，新建该目录
			if (!extDir.exists()) {
				extDir.mkdir();
			}
			try {
				// 得到 assets 目录下我们实现准备好的 SQLite 数据库作为输入流
				InputStream is = this.context.getAssets().open(DB_NAME);
				// 输出流
				OutputStream os = new FileOutputStream(
						new File(extDir, DB_NAME));

				// 文件写入
				byte[] buffer = new byte[1024];
				int length;
				while ((length = is.read(buffer)) > 0) {
					os.write(buffer, 0, length);
				}
				System.out.println("3");
				// 关闭文件流
				os.flush();
				os.close();
				is.close();

			} catch (Exception e) {
				System.out.println("4");
				e.printStackTrace();
			}
		} else {
		}
	}


}