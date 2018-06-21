package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
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
import org.openqa.selenium.support.ui.Select;

import com.gargoylesoftware.htmlunit.BrowserVersion;


public class inventoryDownload {
	
	private static final String RTMARTURL = "http://www.dennis.com.cn:9090/scm_login.asp";
	private static String userName="11000112";
	private static String password ="pg1001";
	private static final String INVENTORYURL = "http://www.dennis.com.cn:9090/Vend_New/scm_sup2_stock.asp";
	private static final String HOST = "www.dennis.com.cn:9090";
	
	public static void main(String[] args) {
		System.setProperty("webdriver.gecko.driver", "D:/ksj_soft/geckodriver.exe");
		System.setProperty("webdriver.firefox.bin","C:/Program Files (x86)/Mozilla Firefox/firefox.exe");
		WebDriver driver =  null;
		FirefoxOptions options = new FirefoxOptions();
		driver=new FirefoxDriver(options);
//		driver.setJavascriptEnabled(true);
		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		// TODO Auto-generated method stub
		driver.get(RTMARTURL);
		System.out.println("log in to RTMart portal");

		
		try {
			
			driver.findElement(By.name("Name")).clear();
			driver.findElement(By.name("Name")).sendKeys(userName);
			System.out.println("Input user ID: " + userName);

			driver.findElement(By.name("Password")).clear();
			driver.findElement(By.name("Password")).sendKeys(
					password);
			System.out.println("Input PWD: " + password);

			// 点击登录链接
			driver.findElement(By.name("enter")).click();
			Thread.sleep(3000);
			
			String title = new String(driver.getTitle());
			if (title.contains("厂商管理")) {
				
				System.out.println("登陆成功");
				
			} else {
				System.out.println("登录失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Connection Refused : " + e);
		}
		
		fileDownload(driver);
		
	}
	
	
	
	
	private static void DownloadInventory(WebDriver driver) {
		driver.get(INVENTORYURL);
		String currentUrl = driver.getCurrentUrl();
		System.out.println(currentUrl);
		
		
//		new Select(driver.findElement(By.name("storecode"))).selectByValue("1201");
//		
//		driver.findElement(By.name("Submit")).click();
//		
//		String isNull = driver.findElement(
//				By.xpath("//td[@bgcolor='black']/table/tbody/tr[3]/td[1]/font")).getText();
//		System.out.println("是否有库存资料："+isNull);
//		if("尚没有库存资料".equals(isNull)){
//			System.out.println("该门店尚没有库存资料！");
//			return;
//		}
//		
//		
//		String downloadDir = "D:/ksj/downloads/Dennis/download";
//		String tempDir = downloadDir + File.separator + "temp";
//		File tempDirectory = new File(tempDir);
//		if (!tempDirectory.exists()) {
//			tempDirectory.mkdirs();
//		}
//		String fileName = "cgj_dennis.txt";
//		HttpClientRTMartDownload downloadUtil = new HttpClientRTMartDownload();
//
//		Set<Cookie> cookies = driver.manage().getCookies();
//		System.out.println("Cookie nbr " + cookies.size());
//		for (Cookie cke : cookies) {
//			System.out.println(cke.getName() + '\t' + cke.getValue());
//		}
//		String salesExportURL = INVENTORYURL;
//		
//		
//		String pageInfo = driver.findElement(
//				By.xpath("//td[@align='left'][@valign='bottom']/font")).getText();
//		
//		int totalPages = Integer.parseInt(pageInfo.replace("当前", "").replace("页", "").split("/")[1]);
//		int curPageNbr = Integer.parseInt(pageInfo.replace("当前", "").replace("页", "").split("/")[0]);
//	
//		for (; curPageNbr <= totalPages; curPageNbr++) {
//
//			System.out.println("Current Page Number is: " + curPageNbr);
//			try {
//				
//				downloadUtil.downloadInventoryFile(salesExportURL, cookies, tempDir, fileName, HOST,curPageNbr);
//				
//				parseInventoryFile(tempDir,fileName,curPageNbr);
//				
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//			if (curPageNbr < totalPages) {
//				System.out.println("Begin to get next page's data");
//				driver.findElement(By.linkText("下一页")).click();
//
//			}
//		}
		
		
		
		
		
		
		
		
		
		
		
		WebElement select = driver.findElement(By.name("storecode")); 
		List<WebElement> allOptions = select.findElements(By.tagName("option")); 
		System.out.println("门店总数:"+allOptions.size());
		boolean firstOption = true;
		for (WebElement option : allOptions) {
			if(firstOption){
				firstOption = false;
				continue;
			}
			
			String storeInfo = option.getText();
			String storeCode = storeInfo.split("/")[0];
			String storeName = storeInfo.split("/")[1];
		    System.out.println(String.format("Value is: %s", option.getText())); 
		    option.click();
			
			driver.findElement(By.name("Submit")).click();
			
			String isNull = driver.findElement(
					By.xpath("//td[@bgcolor='black']/table/tbody/tr[3]/td[1]/font")).getText();
			
			System.out.println("是否有库存资料："+isNull);
			
			if("尚没有库存资料".equals(isNull)){
				System.out.println("该门店尚没有库存资料！");
				continue;
			}
			
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String dateStr = sdf.format(new Date());
			String downloadDir = "D:/ksj/downloads/Dennis/download";
			String tempDir = downloadDir + File.separator + "temp";
			File tempDirectory = new File(tempDir);
			if (!tempDirectory.exists()) {
				tempDirectory.mkdirs();
			}
			String fileName = storeName + dateStr +".txt";
			HttpClientRTMartDownload downloadUtil = new HttpClientRTMartDownload();
	
			Set<Cookie> cookies = driver.manage().getCookies();
			System.out.println("Cookie nbr " + cookies.size());
			for (Cookie cke : cookies) {
				System.out.println(cke.getName() + '\t' + cke.getValue());
			}
			
			
			String salesExportURL = INVENTORYURL;
			String pageInfo = driver.findElement(
					By.xpath("//td[@align='left'][@valign='bottom']/font")).getText();
			
			int totalPages = Integer.parseInt(pageInfo.replace("当前", "").replace("页", "").split("/")[1]);
			int curPageNbr = Integer.parseInt(pageInfo.replace("当前", "").replace("页", "").split("/")[0]);
		
			for (; curPageNbr <= totalPages; curPageNbr++) {
	
				System.out.println("Current Page Number is: " + curPageNbr);
				try {
					
					downloadUtil.downloadInventoryFile(salesExportURL, cookies, tempDir, fileName, HOST,curPageNbr,storeCode);
					
					parseInventoryFile(tempDir,fileName,curPageNbr);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
	
				if (curPageNbr < totalPages) {
					System.out.println("Begin to get next page's data");
					driver.findElement(By.linkText("下一页")).click();
	
				}
			}
		    
		    
		    
		} 
		
		
		
		
		
	}
	
	public static void fileDownload(WebDriver driver) {
		
		DownloadInventory(driver);
		
	}
	
	public static void parseInventoryFile(String tempDir,String fileName,int curPageNbr){
		char seperator = '\t';
		File tempFile = new File(tempDir + File.separator + fileName);
		File salesFile = new File("D:/ksj/downloads/Dennis/download/" + fileName);
		
		
		try {
			Document doc = Jsoup.parse(tempFile, null);
			
			// doc.select(cssQuery)
			Elements td = doc.select("[bgcolor=black]");
			Elements table =td.get(0).select("table");
			Elements rows = table.select("tr");

			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(salesFile,true), "UTF-8"));
			boolean firstRow = true;
//			boolean firstTimeCountRows = true;
			boolean secondRow = true;
			int totalColumns = 0;
			System.out.println("parseFile现在操作的页面："+curPageNbr);
			for (Element row : rows) {
				String line = "";
				if (firstRow) {
					firstRow = false;
					continue;
				}
				if(curPageNbr>1 && secondRow){
					secondRow = false;
					continue;
				}
				Elements tds = row.select("td");
				totalColumns = tds.size();
				
				if (tds.isEmpty()) {
					continue;
				}
				System.out.println("该行列数："+totalColumns);
				if(totalColumns==1){
//					continue;
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
