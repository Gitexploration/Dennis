package util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FileNamed {
	public static String formatItemFileName(String reportName, String fileExt, String userName, String prefix) {
		String day = getDayforFileName();
		String finalName = "";
		fileExt = fileExt.replace(".", "").trim();
		if (reportName.contains(":")) {
			reportName = reportName.replace(":", "");
		}
		if (prefix != null) {
			if (prefix.endsWith(".")) {
				prefix = prefix.substring(0, prefix.length() - 1).trim();
			}
		}
		if (!fileExt.equals("")) {
			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:Ss").create();
			// prefix = reportName.substring(0, reportName.indexOf("."));

			if (null != prefix && !reportName.startsWith(prefix) && !"".equals(prefix.trim())
					&& !prefix.contains("null")) {
				if (!reportName.contains(".req")) {
					finalName = prefix + "." + reportName + "." + userName + "." + "req" + day + "." + fileExt;
				}
			} else {
				if (!reportName.contains(".req")) {
					finalName = reportName + "." + userName + "." + "req" + day + "." + fileExt;
				}
			}

			// return reportName + "_" + day + "." + fileExt;
			return finalName;
		} else {
			if (prefix != null && !prefix.equals("")) {
				finalName = prefix + "." + reportName + "." + userName + "." + "req" + day;
			} else {
				finalName = reportName + "." + userName + "." + "req" + day;
			}
			return finalName;
		}
	}

	public static String getDayforFileName() {
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
		String today = sdf2.format(getDateTime(0));
		String day = today.replace("-", "_").replace(":", "_").replace(" ", "_").replace(".", "_");
		return day;
	}
	
	public static Date getDateTime(int preDays) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_YEAR, -preDays);
		return c.getTime();
	}
}
