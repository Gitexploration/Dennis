package com.rsi.connector.dennis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.BrowserVersion;

import util.HttpClientRTMartDownload;

public class DennisSalesDownload {

	private static final String DENNISURL = "http://www.dennis.com.cn:9090/scm_login.asp";
	private static String userName = "11000112";
	private static String password = "pg1001";
	private static final String SALESURL = "http://www.dennis.com.cn:9090/Vend_New/scm_sup2_itemsale.asp";
	// http://www.dennis.com.cn:9090/Vend_New/scm_login.asp?target=scm_sup2_itemsale.asp
	private static final String HOST = "www.dennis.com.cn:9090";

	public static void main(String[] args) throws Exception {
		System.setProperty("webdriver.gecko.driver", "D:/ksj_soft/geckodriver.exe");
		System.setProperty("webdriver.firefox.bin", "C:/Program Files (x86)/Mozilla Firefox/firefox.exe");
		WebDriver driver = null;
		FirefoxOptions options = new FirefoxOptions();
		driver = new FirefoxDriver(options);
		// driver.setJavascriptEnabled(true);
		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		// TODO Auto-generated method stub
		driver.get(DENNISURL);
		System.out.println("log in to RTMart portal");

		try {

			driver.findElement(By.name("Name")).clear();
			driver.findElement(By.name("Name")).sendKeys(userName);
			System.out.println("Input user ID: " + userName);

			driver.findElement(By.name("Password")).clear();
			driver.findElement(By.name("Password")).sendKeys(password);
			System.out.println("Input PWD: " + password);

			// �����¼����
			driver.findElement(By.name("enter")).click();
			Thread.sleep(3000);

			String title = new String(driver.getTitle());
			if (title.contains("���̹���")) {

				System.out.println("��½�ɹ�");

			} else {
				System.out.println("��¼ʧ��");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Connection Refused : " + e);
		}

		fileDownload(driver);

	}

	private static void DownloadSales(WebDriver driver) throws Exception {
		driver.get(SALESURL);
		String salesExportURL = SALESURL;
		String currentUrl = driver.getCurrentUrl();
		System.out.println(currentUrl);

		OutputStreamWriter out = null;
		HttpClientRTMartDownload downloadUtil = new HttpClientRTMartDownload();
		String downloadDir = "D:/ksj/downloads/Dennis/download/sales";
		Calendar c = Calendar.getInstance();
		String tempDir = downloadDir + File.separator + "temp";
		File tempDirectory = new File(tempDir);
		if (!tempDirectory.exists()) {
			tempDirectory.mkdirs();
		}

		// c.add(Calendar.DAY_OF_YEAR, 1);
		for (int d = 0; d < 3; d++) {
			c.add(Calendar.DAY_OF_YEAR, -1);
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateStr = sdf1.format(c.getTime());
			String dateStr1 = dateStr.replaceAll("-", "");
			String dateStr2 = sdf2.format(c.getTime()).replace("-", "_").replace(":", "_").replace(" ", "_");
			System.out.println(dateStr);
			String[] strDate = dateStr.split("-");
			String year = strDate[0];
			String month = strDate[1];
			String day = strDate[2];
			System.out.println(year + "," + month + "," + day);

			driver.findElement(By.name("TranYears")).clear();
			driver.findElement(By.name("TranYears")).sendKeys(year);

			driver.findElement(By.name("TranMonths")).clear();
			driver.findElement(By.name("TranMonths")).sendKeys(month);

			driver.findElement(By.name("TranDays")).clear();
			driver.findElement(By.name("TranDays")).sendKeys(day);

			driver.findElement(By.name("TranYeare")).clear();
			driver.findElement(By.name("TranYeare")).sendKeys(year);

			driver.findElement(By.name("TranMonthe")).clear();
			driver.findElement(By.name("TranMonthe")).sendKeys(month);

			driver.findElement(By.name("TranDaye")).clear();
			driver.findElement(By.name("TranDaye")).sendKeys(day);

			String fileName = "KSJ.PNG_Sales_Dennis." + dateStr1 + ".req" + dateStr2 + ".txt";
			// "KSJ.Vendorname_Sales_Dennis.20180531.req2018_06_01_15_01_33.txt";

			try {
				StringBuilder line = new StringBuilder("��Ʒ����\t����\t��Ʒ����\t���\t��������\t���۽��\t�ŵ���\t�ŵ�����\tʱ��\r\n");
				File file = new File(downloadDir + File.separator + fileName);
				if (!(new File(downloadDir + File.separator).exists())) {
					new File(downloadDir + File.separator).mkdirs();
				}
				out = new OutputStreamWriter(new java.io.FileOutputStream(file, true), "UTF-8");
				out.write(line.toString());
				out.flush();
			} catch (UnsupportedEncodingException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (FileNotFoundException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			String pageSource = driver.getPageSource();
			Document document = Jsoup.parse(pageSource);
			Elements es = document.select("[name=storecode] option");
			int elementsSize = es.size();
			System.out.println("�ŵ�����:" + (elementsSize - 2));
			for (int i = 248; i < elementsSize; i++) {
				Element element = es.get(i);
				String storeInfo = element.text();
				System.out.println("��ǰ��" + (i - 1) + "�ŵ�:" + storeInfo);
				String storeCode = storeInfo.split("/")[0];
				String storeName = storeInfo.split("/")[1];

				System.out.println("storeCode:" + storeCode + ",storeName:" + storeName);
				new Select(driver.findElement(By.name("storecode"))).selectByIndex(i);
				driver.findElement(By.name("Submit")).click();
				Set<Cookie> cookies = driver.manage().getCookies();

				int totalPages;
				int curPageNbr;
				try {

					WebDriverWait wait = new WebDriverWait(driver, 10);

					wait.until(ExpectedConditions.visibilityOfElementLocated(
							By.xpath("//td[@bgcolor='black']/table/tbody/tr[3]/td[1]//font")));

					String isNull = driver.findElement(By.xpath("//td[@bgcolor='black']/table/tbody/tr[3]/td[1]//font"))
							.getText().trim();// "//"��ʾ��ȡ��������font

					System.out.println(isNull);
					// ��ȡ��ҳ��Ϣ
					By location = By.xpath("//td[@align='left'][@valign='bottom']/font");
					if (isWebElementExist(driver, location) != true) {
						if ("��û����������".equals(isNull)) {
							System.out.println("��û����������");
							continue;
						} else {
							totalPages = 1;
							curPageNbr = 1;
							System.out.println(storeName + "�ܹ���1ҳ����");
						}
					} else {
						String pageInfo = driver.findElement(By.xpath("//td[@align='left'][@valign='bottom']/font"))
								.getText();
						totalPages = Integer.parseInt(pageInfo.replace("��ǰ", "").replace("ҳ", "").split("/")[1]);
						curPageNbr = Integer.parseInt(pageInfo.replace("��ǰ", "").replace("ҳ", "").split("/")[0]);

						System.out.println(storeName + "�ܹ���" + totalPages + "ҳ����");
					}
				} catch (Exception e1) {
					System.out.println("Error when parse latest update date from web, ignore this file downloading.");
					return;
				}

				try {
					for (; curPageNbr <= totalPages; curPageNbr++) {
						downloadUtil.downloadMonthlySalesFile(salesExportURL, cookies, tempDir, fileName, HOST,
								curPageNbr, year, month, day, storeCode);
						System.out.println("����ҳ�棺curPageNbr:" + curPageNbr + ",totalPages:" + totalPages);

						parseFile(downloadDir, tempDir, fileName, curPageNbr, storeCode, storeName, dateStr1);
						if (curPageNbr < totalPages) {
							System.out.println("Begin to get next page's data");
							driver.findElement(By.linkText("��һҳ")).click();

						}
					}

				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void fileDownload(WebDriver driver) throws Exception {

		DownloadSales(driver);

	}

	private static boolean isWebElementExist(WebDriver driver, By location) {
		try {
			driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
			driver.findElement(location);
			return true;
		} catch (Exception e) {
			System.out.println("Element:" + location.toString() + " is not exsit!");
			return false;
		} finally {
			driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		}
	}

	public static void parseFile(String downloadDir, String tempDir, String fileName, int curPageNbr, String storeCode,
			String storeName, String dateStr1) {
		char seperator = '\t';
		File tempFile = new File(tempDir + File.separator + fileName);
		File salesFile = new File(downloadDir + File.separator + fileName);

		try {
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(salesFile, true), "UTF-8"));

			Document doc = Jsoup.parse(tempFile, null);

			// doc.select(cssQuery)
			Elements td = doc.select("[bgcolor=black]");
			Elements table = td.get(0).select("table");
			Elements rows = table.select("tr");

			boolean firstRow = true;
			// boolean firstTimeCountRows = true;
			boolean secondRow = true;
			int totalColumns = 0;
			System.out.println("parseFile���ڲ�����ҳ�棺" + curPageNbr);
			for (Element row : rows) {
				String line = "";
				if (firstRow) {
					firstRow = false;
					continue;
				}
				if (secondRow) {
					secondRow = false;
					continue;
				}
				Elements tds = row.select("td");
				if (tds.isEmpty()) {
					continue;
				}

				totalColumns = tds.size();
				// }
				if (totalColumns != 6) {
					break;
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
				line = line + "\t" + storeCode + "\t" + storeName + "\t" + dateStr1;
				out.write(line);
				out.write("\r\n");
				out.flush();
			}
			out.close();

			if (tempFile.exists()) {
				System.out.println("temp file: " + fileName + " deleted:" + tempFile.delete());
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			System.out.println("if file exist, delete file");
			return;
		}
	}

}
