package com.rsi.connector.dennis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.Select;


import util.LogMgr;


public class DennisInventoryDownload_bak2 {
	
//	private static Logger log = LogMgr.getLogger(LogMgr.connector);
	private static final String DENNISURL = "http://www.dennis.com.cn:9090/scm_login.asp";
	private static String userName="11000112";
	private static String password ="pg1001";
	private static final String INVENTORYURL = "http://www.dennis.com.cn:9090/Vend_New/scm_sup2_stock.asp";
	private static final String HOST = "www.dennis.com.cn:9090";
	private static String DECODE = "ISO8859-1";
	private static String ENCODE = "GBK";
	
	public static void main(String[] args) {
		System.setProperty("webdriver.gecko.driver", "D:/ksj_soft/geckodriver.exe");
		System.setProperty("webdriver.firefox.bin","C:/Program Files (x86)/Mozilla Firefox/firefox.exe");
		WebDriver driver =  null;
		FirefoxOptions options = new FirefoxOptions();
		driver=new FirefoxDriver(options);
//		driver.setJavascriptEnabled(true);
		driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
		// TODO Auto-generated method stub
		driver.get(DENNISURL);
		System.out.println("log in to Dennis portal");

		
		try {
			
			driver.findElement(By.name("Name")).clear();
			driver.findElement(By.name("Name")).sendKeys(userName);
			System.out.println("Input user ID: " + userName);

			driver.findElement(By.name("Password")).clear();
			driver.findElement(By.name("Password")).sendKeys(
					password);
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
	
	
	
	
	private static void DownloadInventory(WebDriver driver) {
		try {
			driver.get(INVENTORYURL);
			String currentUrl = driver.getCurrentUrl();
			System.out.println(currentUrl);
			
			int retryTimes = 10;
			String url = INVENTORYURL;
			ExecutorService executor = Executors.newFixedThreadPool(10);
			
			String pageSource = driver.getPageSource();
			Document document = Jsoup.parse(pageSource);
			Elements es = document.select("[name=storecode] option");
			int elementsSize = es.size();
			System.out.println("�ŵ�����:"+elementsSize);
			for (int num = 1;num < elementsSize; num++) {
				Element element = es.get(num);
				String storeInfo = element.text();
				String storeCode = storeInfo.split("/")[0];
				String storeName = storeInfo.split("/")[1];
				
				System.out.println("��ǰΪ��"+num+"���ŵ�:"+storeName);
				new Select(driver.findElement(By.name("storecode"))).selectByIndex(num);
				driver.findElement(By.name("Submit")).click();
				
				DennisFileDownloadThread fileDownThread = new DennisFileDownloadThread(url,num,retryTimes, storeName, storeCode);
				Thread.sleep(1000);
				executor.execute(fileDownThread);
				
				
			
			}
			
			executor.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
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
	
	
	
	public static void fileDownload(WebDriver driver) {
		
		DownloadInventory(driver);
		
	}
	

	

}

