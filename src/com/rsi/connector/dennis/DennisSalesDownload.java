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

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
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
import util.MergeFiles;

public class DennisSalesDownload {

	private static final String DENNISURL = "http://www.dennis.com.cn:9090/scm_login.asp";
	private static String userName = "11000112";
	private static String password = "pg1001";
	private static final String SALESURL = "http://www.dennis.com.cn:9090/Vend_New/scm_sup2_itemsale.asp";
	private static final String HOST = "www.dennis.com.cn:9090";
	public static String currentStoreCode = "";
	public static String currentStoreName = "";
	public static int currentPageNbr = 1;
	public static int currentStoreIndex = 2;

	public static void main(String[] args) throws Exception {
		System.setProperty("webdriver.gecko.driver", "D:/ksj_soft/geckodriver.exe");
		fileDownload();
	}

	private static boolean DownloadSales(WebDriver driver) {
		boolean success = false;
		driver.get(SALESURL);
		String salesExportURL = SALESURL;
		String currentUrl = driver.getCurrentUrl();
		System.out.println(currentUrl);
		try {
			String downloadDir = "D:/ksj/downloads/Dennis/Sales";
			Calendar c = Calendar.getInstance();
			String tempDir = downloadDir + File.separator + "temp";
			File tempDirectory = new File(tempDir);
			if (!tempDirectory.exists()) {
				tempDirectory.mkdirs();
			}

			c.add(Calendar.DAY_OF_YEAR, -1);
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateStr = sdf1.format(c.getTime());
			String dateStr1 = dateStr.replaceAll("-", "");
			String dateStr2 = sdf2.format(c.getTime()).replace("-", "_").replace(":", "_").replace(" ", "_");
			
			String mergeFile = "KSJ.PNG_Sales_Dennis."+dateStr1+".req"+dateStr2+".txt";
			
			OutputStreamWriter out = null;
			HttpClientRTMartDownload downloadUtil = new HttpClientRTMartDownload();
			Set<Cookie> cookies = driver.manage().getCookies();

			String[] strDate = dateStr.split("-");
			String year = strDate[0];
			String month = strDate[1];
			String day = strDate[2];
//			System.out.println(year + "," + month + "," + day);

			String pageSource = driver.getPageSource();
			Document document = Jsoup.parse(pageSource);
			Elements es = document.select("[name=storecode] option");
			int elementsSize = es.size();
			System.out.println("门店总数:" + (elementsSize - 2));
			int i = 2;
			i = currentStoreIndex;
			for (; i < elementsSize; i++) {
				int curPageNbr = currentPageNbr;
				Element element = es.get(i);
				String storeInfo = element.text();
				System.out.println("当前第" + (i - 1) + "门店:" + storeInfo);
				String storeCode = element.val().toString().trim();
				String storeName = storeInfo.split("/")[1].trim();

				String fileName = "KSJ.PNG_Sales_Dennis." + storeCode + "." + dateStr1 + ".req" + dateStr2 + ".txt";
				
				addSalesHeader(fileName, downloadDir, out);
				
				String url = "http://www.dennis.com.cn:9090/Vend_New/scm_sup2_itemsale.asp";
				url = url + "?mode=search";
				url = url + "&tranyears=" + year;
				url = url + "&tranmonths=" + month;
				url = url + "&trandays=" + day;
				url = url + "&tranyeare=" + year;
				url = url + "&tranmonthe=" + month;
				url = url + "&trandaye=" + day;
				url = url + "&storecode=" + storeCode;
				url = url + "&whichpage=" + 1;

				DefaultHttpClient client = null;
				client = new DefaultHttpClient();
				CookieStore cs = new BasicCookieStore();
				for (Cookie ck : cookies) {
					BasicClientCookie bcc = new BasicClientCookie(ck.getName(), ck.getValue());
					bcc.setDomain(ck.getDomain());
					bcc.setPath(ck.getPath());
					cs.addCookie(bcc);
				}
				HttpContext localContext = new BasicHttpContext();
				localContext.setAttribute(ClientContext.COOKIE_STORE, cs);
				// response = httpclient.execute(httpget,localContext);

				HttpGet httpGet = new HttpGet(url);
				// HttpClient client = new DefaultHttpClient();
				CloseableHttpResponse firstPageResult = (CloseableHttpResponse) client.execute(httpGet, localContext);
				Thread.sleep(3000);
				StatusLine statusLine = firstPageResult.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				System.out.println("status code: " + statusCode);
				if (statusCode == 200) {
					HttpEntity firstpageEntity = firstPageResult.getEntity();
					String firstPageResString = EntityUtils.toString(firstpageEntity);
					Document firstResDoc = Jsoup.parse(firstPageResString);
					Elements whicPageOptions = firstResDoc.select("[name=whichpage] option");
					int totalNumofPages = whicPageOptions.size();
					EntityUtils.consume(firstpageEntity);
					firstPageResult.close();

					System.out.println("storeCode:" + storeCode + ",storeName:" + storeName);
					boolean contain = firstPageResString.contains("尚没有销售资料");
					if (totalNumofPages == 0 && contain) {
						continue;
					}
					if (totalNumofPages == 0) {
						totalNumofPages = 1;
					}

					System.out.println("total pages:" + totalNumofPages);

					try {
						for (; curPageNbr <= totalNumofPages; curPageNbr++) {
							System.out.println("storeCode:" + storeCode + ", Page:" + curPageNbr);
							if (!downloadUtil.downloadSalesFile(salesExportURL, cookies, tempDir, fileName, HOST,
									curPageNbr, year, month, day, storeCode, storeName, i)) {
								currentStoreCode = storeCode;
								currentStoreName = storeName;
								currentStoreIndex = i;
								driver.quit();
								driver = null;
								// driver = login();
								// if (driver != null) {
								// fileDownload(driver);
								// }
								success = false;
								return success;
							}
							Thread.sleep(1000);

							parseSalesFile(downloadDir, tempDir, fileName, curPageNbr, storeCode, storeName, dateStr1);
							

						}
						try {
							if (out != null) {
								out.close();
							}
						} catch (IOException e) {
							e.printStackTrace();
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
					resetIndexes();
				} else {
					if (driver != null) {
						try {
							driver.quit();
						} catch (Exception e) {
						}
					}
					currentStoreCode = storeCode;
					currentStoreName = storeName;
					currentStoreIndex = i;
					driver.quit();
					driver = null;
					// driver = login();
					// if (driver != null) {
					// fileDownload(driver);
					// }
					client.close();
					success = false;
					return success;
					// currentPage = currentPage;

				}
				
				File waitMergeFile = new File(downloadDir+"/"+fileName);
				
				MergeFiles.mergeFile(downloadDir, waitMergeFile, mergeFile);
				
				
			}
			
			
			
			success = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return success;
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

	public static void parseSalesFile(String downloadDir, String tempDir, String fileName, int curPageNbr,
			String storeCode, String storeName, String dateStr1) {
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
//			System.out.println("parseFile现在操作的页面：" + curPageNbr);
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
//				System.out.println("temp file: " + fileName + " deleted:" + tempFile.delete());
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			System.out.println("if file exist, delete file");
			return;
		}
	}

	public static void fileDownload() {
		WebDriver driver = login();
		boolean success = false;
		if (driver != null) {
			success = DownloadSales(driver);
		}

		if (!success) {
			System.out.println("download not completed, retrying/resuming!");
			fileDownload();
		} else {
			System.out.println("Finished downloading!");
		}

	}

	public static WebDriver login() {
		FirefoxOptions options = new FirefoxOptions();
		WebDriver driver = null;
		driver = new FirefoxDriver(options);
		// driver.setJavascriptEnabled(true);
		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		// TODO Auto-generated method stub
		driver.get(DENNISURL);
		System.out.println("log in to Dennis portal");

		try {

			driver.findElement(By.name("Name")).clear();
			driver.findElement(By.name("Name")).sendKeys(userName);
			System.out.println("Input user ID: " + userName);

			driver.findElement(By.name("Password")).clear();
			driver.findElement(By.name("Password")).sendKeys(password);
			System.out.println("Input PWD: " + password);

			// 点击登录链接
			driver.findElement(By.name("enter")).click();
			Thread.sleep(3000);

			String title = new String(driver.getTitle());
			if (title.contains("厂商管理")) {

				System.out.println("login success");

			} else {
				System.out.println("login failed");
				driver.quit();
				driver = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Connection Refused : " + e);
			driver.quit();
			driver = null;
		}
		return driver;
	}

	public static void resetIndexes() {
		currentStoreCode = "";
		currentStoreName = "";
		currentStoreIndex = 1;
		currentPageNbr = 1;
	}
	
	
	private static void addSalesHeader(String fileName, String downloadDir, OutputStreamWriter out) {
		try {
			StringBuilder line = new StringBuilder("商品代号\t条码\t商品名称\t规格\t销售数量\t销售金额\t门店编号\t门店名称\t时间\r\n");
			File file = new File(downloadDir + File.separator + fileName);
			if (!(new File(downloadDir + File.separator).exists())) {
				new File(downloadDir + File.separator).mkdirs();
			}
			out = new OutputStreamWriter(new java.io.FileOutputStream(file,true), "UTF-8");
			out.write(line.toString());
			out.flush();
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}
	

}
