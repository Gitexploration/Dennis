package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MergeFiles {
	public static void  mergeFile(String downloadDirectory,File downloadOrderTempFile,String MergeFileName) throws IOException{

		// get downloadTempFolder directory
		String mergeOrderFolder = downloadDirectory.substring(0, downloadDirectory.length()) + File.separator
				+ MergeFileName;

		String f = null;
		int m = 1;
		boolean flag = false;
		FileInputStream fileIn = null;
		FileWriter fw1 = null;
		BufferedReader bufReader = null;
		BufferedWriter bufWriter = null;

		// list files and merge
		File tempFile = new File(mergeOrderFolder);
		if (!tempFile.exists()) {
			fw1 = new FileWriter(mergeOrderFolder);
			bufWriter = new BufferedWriter(fw1);
		} else {
			flag = true;
			fw1 = new FileWriter(mergeOrderFolder, true);
			bufWriter = new BufferedWriter(fw1);
		}

		java.util.Date gmtDateTime = getGMTDate();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String downloadOrderDate = sdf.format(gmtDateTime);

		// merge the file
		// put the first into the merge file, which will save the head file
		File file1 = downloadOrderTempFile;
		f = file1.toString();
		long fTime = file1.lastModified();
		Date d = new Date(fTime);
		String currentDownloadTime = sdf.format(d);

		if (currentDownloadTime.equals(downloadOrderDate)) {
			fileIn = new FileInputStream(file1);
			bufReader = new BufferedReader(new InputStreamReader(fileIn,"utf-8"));

			String line = "";
			ArrayList<String> arrStr = new ArrayList<String>();
			if (m != 1) {
				bufReader.readLine();
			}
			m++;
			while ((line = bufReader.readLine()) != null) {
				arrStr.add(line);
				/*
				 * bufWriter.write(line); bufWriter.newLine();
				 */
			}

			for (int j = 0; j < arrStr.size(); j++) {
				if (j == 0 && flag) {
					continue;
				}
				bufWriter.write(arrStr.get(j).toString());
				bufWriter.newLine();
			}
			bufReader.close();
		}

		bufWriter.close();
		System.out.println("merge success");
	}
	
	public static java.util.Date getGMTDate(){
		long mTime = System.currentTimeMillis();
	    int offset = Calendar.getInstance().getTimeZone().getRawOffset();
	    Calendar c = Calendar.getInstance();
	    c.setTime(new Date(mTime - offset));
	    //c.add(Calendar.DAY_OF_YEAR, -(downloadDays-1));
	    c.add(Calendar.HOUR_OF_DAY, -c.get(Calendar.HOUR_OF_DAY));
	    c.add(Calendar.MINUTE, -c.get(Calendar.MINUTE));
	    c.add(Calendar.SECOND, -c.get(Calendar.SECOND));
	    c.add(Calendar.MILLISECOND, -c.get(Calendar.MILLISECOND));
	    return c.getTime();
	}
	
	
}
