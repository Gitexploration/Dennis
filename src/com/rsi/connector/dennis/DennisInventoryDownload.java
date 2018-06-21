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
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.SynchronousQueue;
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

public class DennisInventoryDownload {

	private static final String DENNISURL = "http://www.dennis.com.cn:9090/scm_login.asp";
	private static String userName = "11000112";
	private static String password = "pg1001";
	private static final String INVENTORYURL = "http://www.dennis.com.cn:9090/Vend_New/scm_sup2_stock.asp";
	private static final String HOST = "www.dennis.com.cn:9090";
	private static String DECODE = "ISO8859-1";
	private static String ENCODE = "GBK";
	public static String currentStoreCode = "";
	public static String currentStoreName = "";
	public static int currentPageNbr = 1;
	public static int currentStoreIndex = 1;

	public static void main(String[] args) {
		System.setProperty("webdriver.gecko.driver", "C:/KSJ/data/geckodriver.exe");
		// System.setProperty("webdriver.firefox.bin","C:/Program Files
		// (x86)/Mozilla Firefox/firefox.exe");
		fileDownload();

	}

	// private static void DownloadInventory(WebDriver driver) {
	// driver.get(INVENTORYURL);
	// String currentUrl = driver.getCurrentUrl();
	// System.out.println(currentUrl);
	// // String fileDate =
	// //
	// driver.findElement(By.xpath("//td[@bgcolor='black']/table/tbody/tr[1]")).getText();
	//
	// Date date = new Date();
	// String downloadDir = "D:/ksj/downloads/Dennis/Inventory";
	// String tempDir = downloadDir + File.separator + "temp";
	// File tempDirectory = new File(tempDir);
	// if (!tempDirectory.exists()) {
	// tempDirectory.mkdirs();
	// }
	//
	// SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
	// SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	// String dateStr1 = sdf1.format(date);
	// String dateStr2 = sdf2.format(date).replace("-", "_").replace(":",
	// "_").replace(" ", "_");
	// OutputStreamWriter out = null;
	// HttpClientRTMartDownload downloadUtil = new HttpClientRTMartDownload();
	// String inventoryExportURL = INVENTORYURL;
	// Set<Cookie> cookies = driver.manage().getCookies();
	//
	// // "KSJ.Vendorname_Inventory_Dennis.20180531.req2018_06_01_15_01_33.txt";
	//
	// String pageSource = driver.getPageSource();
	// Document document = Jsoup.parse(pageSource);
	// Elements es = document.select("[name=storecode] option");
	// int elementsSize = es.size();
	// System.out.println("门店总数:" + (elementsSize - 1));
	// for (int i = 1; i < elementsSize; i++) {
	//
	// Element element = es.get(i);
	// String storeInfo = element.text();
	// System.out.println("当前第" + (i - 1) + "门店:" + storeInfo);
	// String storeCode = element.val();
	// String storeName = storeInfo.split("/")[1].trim();
	//
	// String fileName = "KSJ.Vendorname_Inventory_Dennis." + storeCode + "." +
	// dateStr1 + ".req" + dateStr2
	// + ".txt";
	//
	// try {
	// StringBuilder line = new
	// StringBuilder("店别\t商品编码\t主国际条码\t商品名称/规格\t规格\t库存数量\t门店编号\t门店名称\t时间\r\n");
	// File file = new File(downloadDir + File.separator + fileName);
	// if (!(new File(downloadDir + File.separator).exists())) {
	// new File(downloadDir + File.separator).mkdirs();
	// }
	// out = new OutputStreamWriter(new java.io.FileOutputStream(file, true),
	// "UTF-8");
	// out.write(line.toString());
	// out.flush();
	// } catch (UnsupportedEncodingException e2) {
	// e2.printStackTrace();
	// } catch (FileNotFoundException e2) {
	// e2.printStackTrace();
	// } catch (IOException e2) {
	// e2.printStackTrace();
	// }
	//
	// System.out.println("storeCode:" + storeCode + ",storeName:" + storeName);
	// new Select(driver.findElement(By.name("storecode"))).selectByIndex(i);
	// driver.findElement(By.name("Submit")).click();
	//
	// int totalPages;
	// int curPageNbr;
	// try {
	// WebDriverWait wait = new WebDriverWait(driver, 10);
	//
	// wait.until(ExpectedConditions
	// .visibilityOfElementLocated(By.xpath("//td[@bgcolor='black']/table/tbody/tr[3]/td[1]//font")));
	//
	// String isNull =
	// driver.findElement(By.xpath("//td[@bgcolor='black']/table/tbody/tr[3]/td[1]//font"))
	// .getText().trim();// "//"表示获取所有子孙font
	//
	// System.out.println(isNull);
	// // 获取分页信息
	// By location = By.xpath("//td[@align='left'][@valign='bottom']/font");
	// if (isWebElementExist(driver, location) != true) {
	// if ("尚没有库存资料".equals(isNull)) {
	// System.out.println("尚没有库存资料");
	// continue;
	// } else {
	// totalPages = 1;
	// curPageNbr = 1;
	// System.out.println(storeName + "总共有1页数据");
	// }
	// } else {
	// String pageInfo =
	// driver.findElement(By.xpath("//td[@align='left'][@valign='bottom']/font"))
	// .getText();
	// totalPages = Integer.parseInt(pageInfo.replace("当前", "").replace("页",
	// "").split("/")[1]);
	// curPageNbr = Integer.parseInt(pageInfo.replace("当前", "").replace("页",
	// "").split("/")[0]);
	//
	// System.out.println(storeName + "总共有" + totalPages + "页数据");
	// }
	// } catch (Exception e1) {
	// System.out.println("Error when parse latest update date from web, ignore
	// this file downloading.");
	// return;
	// }
	//
	// try {
	// for (; curPageNbr <= totalPages; curPageNbr++) {
	//
	// downloadUtil.downloadInventoryFile(inventoryExportURL, cookies, tempDir,
	// fileName, HOST, curPageNbr,
	// storeCode);
	//
	// parseInventoryFile(downloadDir, tempDir, fileName, curPageNbr, storeCode,
	// storeName, dateStr1);
	//
	// if (curPageNbr < totalPages) {
	// System.out.println("Begin to get "+curPageNbr+" page's data");
	// driver.findElement(By.linkText("下一页")).click();
	// Thread.sleep(5000);
	//
	// }
	// }
	// try {
	// out.close();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// } catch (UnsupportedEncodingException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (FileNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (Exception e) {
	//
	// e.printStackTrace();
	// }
	// }
	//
	//
	//
	// }

	private static boolean DownloadInventory(WebDriver driver) {
		boolean success = false;
		driver.get(INVENTORYURL);
		String currentUrl = driver.getCurrentUrl();
		System.out.println(currentUrl);
		// String fileDate =
		// driver.findElement(By.xpath("//td[@bgcolor='black']/table/tbody/tr[1]")).getText();
		try {
			Date date = new Date();
			String downloadDir = "D:/ksj/downloads/Dennis/Inventory";
			String tempDir = downloadDir + File.separator + "temp";
			File tempDirectory = new File(tempDir);
			if (!tempDirectory.exists()) {
				tempDirectory.mkdirs();
			}

			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateStr1 = sdf1.format(date);
			String dateStr2 = sdf2.format(date).replace("-", "_").replace(":", "_").replace(" ", "_");
			OutputStreamWriter out = null;
			HttpClientRTMartDownload downloadUtil = new HttpClientRTMartDownload();
			String inventoryExportURL = INVENTORYURL;
			Set<Cookie> cookies = driver.manage().getCookies();

			String pageSource = driver.getPageSource();
			Document document = Jsoup.parse(pageSource);
			Elements es = document.select("[name=storecode] option");
			int elementsSize = es.size();
			System.out.println("门店总数:" + (elementsSize - 1));
			int i = 1;
			i = currentStoreIndex;
			for (; i < elementsSize; i++) {
				int curPageNbr = currentPageNbr;
				Element element = es.get(i);
				String storeInfo = element.text();
				System.out.println("当前第" + (i - 1) + "门店:" + storeInfo);
				String storeCode = element.val().toString().trim();
				String storeName = storeInfo.split("/")[1].trim();

				String fileName = "KSJ.PNG_Inventory_Dennis." + storeCode + "." + dateStr1 + ".req" + dateStr2 + ".txt";

				addInventoryHeader(fileName, downloadDir, out);

				String url = "http://www.dennis.com.cn:9090/Vend_New/scm_sup2_stock.asp";
				url = url + "?mode=search";
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
				System.out.print("status code: " + statusCode);
				if (statusCode == 200) {
					HttpEntity firstpageEntity = firstPageResult.getEntity();
					String firstPageResString = EntityUtils.toString(firstpageEntity);
					Document firstResDoc = Jsoup.parse(firstPageResString);
					Elements whicPageOptions = firstResDoc.select("[name=whichpage] option");
					int totalNumofPages = whicPageOptions.size();
					EntityUtils.consume(firstpageEntity);
					firstPageResult.close();

					System.out.println("storeCode:" + storeCode + ",storeName:" + storeName);
					System.out.println("total pages:" + totalNumofPages);
					// new
					// Select(driver.findElement(By.name("storecode"))).selectByIndex(i);
					// driver.findElement(By.name("Submit")).click();
					if (totalNumofPages == 0 && firstPageResString.contains("尚没有库存资料")) {
						continue;
					}
					if (totalNumofPages == 0) {
						totalNumofPages = 1;
					}

					try {
						for (; curPageNbr <= totalNumofPages; curPageNbr++) {
							System.out.println("storeCode:" + storeCode + ", Page:" + curPageNbr);

							if (!downloadUtil.downloadInventoryFile(inventoryExportURL, cookies, tempDir, fileName,
									HOST, curPageNbr, storeCode, storeName, i)) {
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

							parseInventoryFile(downloadDir, tempDir, fileName, curPageNbr, storeCode, storeName,
									dateStr1, i);

							// if (curPageNbr < totalNumofPages) {
							// System.out.println("Begin to get " + curPageNbr +
							// "
							// page's data");
							// driver.findElement(By.linkText("下一页")).click();
							// Thread.sleep(5000);
							//
							// }
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
			}
			success = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return success;
	}

	public static void resetIndexes() {
		currentStoreCode = "";
		currentStoreName = "";
		currentStoreIndex = 1;
		currentPageNbr = 1;
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

	private static void addInventoryHeader(String fileName, String downloadDir, OutputStreamWriter out) {
		try {
			StringBuilder line = new StringBuilder("店别\t商品编码\t主国际条码\t商品名称/规格\t规格\t库存数量\t门店编号\t门店名称\t时间\r\n");
			File file = new File(downloadDir + File.separator + fileName);
			if (!(new File(downloadDir + File.separator).exists())) {
				new File(downloadDir + File.separator).mkdirs();
			}
			out = new OutputStreamWriter(new java.io.FileOutputStream(file, true), "UTF-8");
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

	public static void parseInventoryFile(String downloadDir, String tempDir, String fileName, int curPageNbr,
			String storeCode, String storeName, String dateStr1, int currentStoreIndex) {
		char seperator = '\t';
		File tempFile = new File(tempDir + File.separator + fileName);
		File inventoryFile = new File(downloadDir + File.separator + fileName);

		try {
			Document doc = Jsoup.parse(tempFile, null);

			// doc.select(cssQuery)
			Elements td = doc.select("[bgcolor=black]");
			Elements table = td.get(0).select("table");
			Elements rows = table.select("tr");

			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(inventoryFile, true), "UTF-8"));
			boolean firstRow = true;
			// boolean firstTimeCountRows = true;
			boolean secondRow = true;
			int totalColumns = 0;
			// System.out.println("parseFile现在操作的页面："+curPageNbr);
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

				// System.out.println("该行列数："+totalColumns);
				if (totalColumns != 6) {
					// continue;
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
				// System.out.println("temp file: " + fileName + " deleted:" +
				// tempFile.delete());
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
			success = DownloadInventory(driver);
		}

		if (!success) {
			System.out.println("download not completed, retrying/resuming!");
			fileDownload();
		} else {
			System.out.println("Finished downloading!");
		}

	}

}
