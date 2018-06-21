package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {

	public static void main(String ar[]) {
		Date date = new Date();
		try {
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateStr1 = sdf1.format(date);
			String dateStr2 = sdf2.format(date).replace("-", "_").replace(":", "_").replace(" ", "_");
			String mergeFileName = "KSJ.PNG_Inventory_Dennis." + dateStr1 + ".req" + dateStr2 + ".txt";

			String downloadDir = "D:/ksj/downloads/Dennis/Inventory";
			String formatDirectory = "D:/ksj/downloads/Dennis/Inventory/final";
			File finalDir = new File(formatDirectory);
			File downloadDirectory = new File(downloadDir);
			if (!finalDir.exists()) {
				finalDir.mkdirs();
			}

			File mergedFile = new File(formatDirectory+ File.separator+ mergeFileName);
			mergedFile.createNewFile();
			StringBuilder header = new StringBuilder("店别\t商品编码\t主国际条码\t商品名称/规格\t规格\t库存数量\t门店编号\t门店名称\t时间\r\n");
			String fileHeader = header.toString();
			FileOutputStream fileOutput = new FileOutputStream(mergedFile);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fileOutput, "UTF-8"));
			writer.write(new String(fileHeader.getBytes("UTF-8"), "UTF-8"));
//			writer.newLine();
			File[] txtFiles = downloadDirectory.listFiles();
			for (File file : txtFiles) {
				if (file.isFile()) {
					writeTXTtoMergeFile(file.getAbsolutePath(), writer);
				}
			}
			if (writer != null) {
				try {
					writer.flush();
					writer.close();
				} catch (IOException e) {
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static boolean writeTXTtoMergeFile(String txtFileWithPath, BufferedWriter writer) {
		boolean success = false;
		try {
			File rawFile = new File(txtFileWithPath);
			if (rawFile.exists()) {
				try (BufferedReader br = new BufferedReader(
						new InputStreamReader(new FileInputStream(rawFile), "UTF-8"));) {
					boolean firstLine = true;
					String line = "";
					while ((line = br.readLine()) != null) {
						if (firstLine) {
							firstLine = false;
							continue;
						}
						writer.write(line);
						writer.write("\r\n");

					}
					try {
						if (br != null) {
							br.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
			// if (rawFile.exists()) {
			// rawFile.delete();
			// LOG.info("deleted txt file: " + rawFile.getName());
			// }
			success = true;
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		}
		return success;
	}

}
