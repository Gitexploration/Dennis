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
import org.openqa.selenium.support.ui.Select;

import com.gargoylesoftware.htmlunit.BrowserVersion;

import util.HttpClientRTMartDownload;


public class DennisSalesDownload_bak1 {
	
	private static final String DENNISURL = "http://www.dennis.com.cn:9090/scm_login.asp";
	private static String userName="11000112";
	private static String password ="pg1001";
	private static final String SALESURL = "http://www.dennis.com.cn:9090/Vend_New/scm_sup2_itemsale.asp";
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
	
	
	
	
	private static void DownloadSales(WebDriver driver) {
		driver.get(SALESURL);
		String currentUrl = driver.getCurrentUrl();
		System.out.println(currentUrl);
		
		OutputStreamWriter out = null;
		String downloadDir = "D:/ksj/downloads/Dennis/download/sales";
		Calendar c = Calendar.getInstance();
//		c.add(Calendar.DAY_OF_YEAR, 1);
		for(int d =0;d<3;d++){
			c.add(Calendar.DAY_OF_YEAR, -1);
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String dateStr = sdf1.format(c.getTime());
			String dateStr1= dateStr.replaceAll("-", "");
			String dateStr2 = sdf2.format(c.getTime()).replace("-", "_").replace(":", "_").replace(" ", "_");
			System.out.println(dateStr);
			String[] strDate = dateStr.split("-");
			String year =strDate[0];
			String month=strDate[1];
			String day =strDate[2];
			System.out.println(year+","+month+","+day);
			
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
			
			String fileName = "KSJ.PNG_Sales_Dennis."+dateStr1+".req"+dateStr2+".txt";
			// "KSJ.Vendorname_Sales_Dennis.20180531.req2018_06_01_15_01_33.txt";
			
			
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
			System.out.println("门店总数:"+(elementsSize-2));
			for (int i = 2; i < elementsSize; i++) {
				Element element = es.get(i);
				String storeInfo = element.text();
				System.out.println("当前第"+(i-1)+"门店:"+storeInfo);
				String storeCode = storeInfo.split("/")[0];
				String storeName = storeInfo.split("/")[1];
				
				System.out.println("storeCode:"+storeCode+",storeName:"+storeName);
				new Select(driver.findElement(By.name("storecode"))).selectByIndex(i);
				driver.findElement(By.name("Submit")).click();
				
				
				int totalPages;
				int curPageNbr;
				try {
					
					String isNull = driver.findElement(
							By.xpath("//td[@bgcolor='black']/table/tbody/tr[3]/td[1]//font")).getText().trim();//"//"表示获取所有子孙font
					
					System.out.println(isNull);
					// 获取分页信息
					By location = By.xpath("//td[@align='left'][@valign='bottom']/font");
					if (isWebElementExist(driver, location) != true) {
						if("尚没有销售资料".equals(isNull)){
							System.out.println("尚没有销售资料");
							continue;
						}else{
							totalPages =1;
							curPageNbr =1;
							System.out.println(storeName+"总共有1页数据");
						}
					}else{
						String pageInfo = driver.findElement(
								By.xpath("//td[@align='left'][@valign='bottom']/font")).getText();
						totalPages = Integer.parseInt(pageInfo.replace("当前", "").replace("页", "").split("/")[1]);
						curPageNbr = Integer.parseInt(pageInfo.replace("当前", "").replace("页", "").split("/")[0]);
						
						System.out.println(storeName+"总共有"+totalPages+"页数据");
					}
				} catch (Exception e1) {
					System.out.println("Error when parse latest update date from web, ignore this file downloading.");
					return;
				}
				
				
				try {
					for (; curPageNbr <= totalPages; curPageNbr++) {
						System.out.println("当前操作页面:"+curPageNbr);
						if (curPageNbr % 10 == 0) {
							driver.navigate().refresh();
							System.out.println("refresh current page");
						}

						// 获取当前页面的数据行数
						int rows = driver.findElements(By.xpath("//td[@bgcolor='black']/table/tbody/tr")).size();
						List<WebElement> trs = driver.findElements(By.xpath("//td[@bgcolor='black']/table/tbody/tr"));
						int pageCount = rows - 3;//去除前两行和最后一行

						StringBuilder str = new StringBuilder();

						for (int j = 0; j < pageCount; j++) {
							int row = j + 2;
							WebElement tr = trs.get(row);
							List<WebElement> tds = tr.findElements(By.tagName("td"));
							if(tds.size()==1){
								break;
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
							driver.findElement(By.linkText("下一页")).click();

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
			
			
			
			
			
		}
		
		
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
	}
		
		
	public static void fileDownload(WebDriver driver) {
		
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
	
	
	

}
