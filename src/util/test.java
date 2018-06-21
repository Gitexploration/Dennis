package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class test {
	public static void main(String[] args) {
		char seperator = '\t';
		String tempDir = "D:/ksj/downloads/Dennis/download/temp";
		String fileName = "cgj_dennis.txt";
		File tempFile = new File(tempDir + File.separator + fileName);
		File salesFile = new File("D:/ksj/downloads/Dennis/download/" + fileName);
		
		
		try {
			Document doc = Jsoup.parse(tempFile, null);
			
			// doc.select(cssQuery)
			Elements td = doc.select("[bgcolor=black]");
			Elements table =td.get(0).select("table");
			Elements rows = table.select("tr");

			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(salesFile,true), "UTF-8"));//true:解析的数据追加到file里
			boolean firstRow = true;
//			boolean firstTimeCountRows = true;
			int totalColumns = 0;
			for (Element row : rows) {
				String line = "";
				if (firstRow) {
					firstRow = false;
					continue;
				}
				Elements tds = row.select("td");
//				if (firstTimeCountRows) {
					totalColumns = tds.size();
//					firstTimeCountRows = false;
//				}
				if (tds.isEmpty()) {
					continue;
				}
				if(totalColumns==2){
					continue;
				}
				
				for (int i = 0; i < totalColumns; i++) {
					if ("".equals(line)) {
						if (tds.get(i) != null) {
							line = line + tds.get(i).text();
						} else {
							line = line + seperator + "";
						}
					} else {
						if (tds.get(i) != null) {
							line = line + seperator + tds.get(i).text();
						} else {
							line = line + seperator + "";
						}
					}
				}
				out.write(line);
				out.write("\r\n");
				out.flush();
			}
			out.close();

			if (tempFile.exists()) {
				System.out.println("temp file: " + fileName + " deleted:" + tempFile.delete());
			}
		}catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			System.out.println("if file exist, delete file");
			return;
		} 
	}
}
