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


public class DennisInventoryDownload_bak1 {
	
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
		System.out.println("log in to RTMart portal");

		
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
		driver.get(INVENTORYURL);
		String currentUrl = driver.getCurrentUrl();
		System.out.println(currentUrl);
//		String fileDate = driver.findElement(By.xpath("//td[@bgcolor='black']/table/tbody/tr[1]")).getText();
		
		Date date = new Date();
		String downloadDir = "D:/ksj/downloads/Dennis/download/inventory";
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr1 = sdf1.format(date);
		String dateStr2 = sdf2.format(date).replace("-", "_").replace(":", "_").replace(" ", "_");
		OutputStreamWriter out = null;
		
		// "KSJ.Vendorname_Inventory_Dennis.20180531.req2018_06_01_15_01_33.txt";
		String fileName = "KSJ.Vendorname_Inventory_Dennis."+dateStr1+".req"+dateStr2+".txt";
		
		
		try {
			StringBuilder line = new StringBuilder("���\t��Ʒ����\t����������\t��Ʒ����/���\t���\t�������\t�ŵ���\t�ŵ�����\tʱ��\r\n");
			File file = new File(downloadDir + File.separator + fileName);
			if (!(new File(downloadDir + File.separator).exists())) {
				new File(downloadDir + File.separator).mkdirs();
			}
			out = new OutputStreamWriter(new java.io.FileOutputStream(file,true), "UTF-8");
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
		System.out.println("�ŵ�����:"+elementsSize);
		for (int i = 1; i < elementsSize; i++) {
			Element element = es.get(i);
			String storeInfo = element.text();
			String storeCode = storeInfo.split("/")[0];
			String storeName = storeInfo.split("/")[1];
			
			System.out.println("storeCode:"+storeCode+",storeName:"+storeName);
			new Select(driver.findElement(By.name("storecode"))).selectByIndex(i);
			driver.findElement(By.name("Submit")).click();
			
			int totalPages;
			int curPageNbr;
			try {
				// ��ȡ��ҳ��Ϣ
				By location = By.xpath("//td[@align='left'][@valign='bottom']/font");
				if (isWebElementExist(driver, location) != true) {
					System.out.println("��û�п������");
					continue;
				}
				String pageInfo = driver.findElement(
						By.xpath("//td[@align='left'][@valign='bottom']/font")).getText();
				totalPages = Integer.parseInt(pageInfo.replace("��ǰ", "").replace("ҳ", "").split("/")[1]);
				curPageNbr = Integer.parseInt(pageInfo.replace("��ǰ", "").replace("ҳ", "").split("/")[0]);
				
				System.out.println("��ǰҳ:"+curPageNbr+",��ҳ��:"+totalPages);
			} catch (Exception e1) {
				System.out.println("Error when parse latest update date from web, ignore this file downloading.");
				return;
			}
			
			
				
			try {
			for (; curPageNbr <= totalPages; curPageNbr++) {
				System.out.println("��ǰ����ҳ��:"+curPageNbr);
				if (curPageNbr % 10 == 0) {
					driver.navigate().refresh();
					System.out.println("refresh current page");
				}

				// ��ȡ��ǰҳ�����������
				int rows = driver.findElements(By.xpath("//td[@bgcolor='black']/table/tbody/tr")).size();
				List<WebElement> trs = driver.findElements(By.xpath("//td[@bgcolor='black']/table/tbody/tr"));
				int pageCount = rows - 3;//ȥ��ǰ���к����һ��

				StringBuilder str = new StringBuilder();

				for (int j = 0; j < pageCount; j++) {
					int row = j + 2;
					WebElement tr = trs.get(row);
					List<WebElement> tds = tr.findElements(By.tagName("td"));
					if(tds.size()==1){
						continue;
					}
					for(WebElement td : tds){
						str.append(td.getText()+"\t");
					}
					str.append(storeCode+"\t"+storeName+"\t"+dateStr1);
					str.append("\r\n");
				}
				out.write(str.toString());
				out.flush();
				System.out.println("curPageNbr:"+curPageNbr+",totalPages:"+totalPages);
				if (curPageNbr < totalPages) {
					System.out.println("Begin to get next page's data");
					driver.findElement(By.linkText("��һҳ")).click();

				}
			}
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		}
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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

