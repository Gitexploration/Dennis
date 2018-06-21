package com.rsi.connector.dennis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
//import com.rsi.connector.common.mail.EmailTypes;
//import com.rsi.connector.common.mail.MailUtil;
import org.openqa.selenium.support.ui.Select;


public class DennisFileDownloadThread implements Runnable  {
	String url;
	int num;
	int retryTimes;
	String storeName;
	String storeCode;
	WebDriver driver;
	
	
	
	


	public DennisFileDownloadThread(int num, int retryTimes, String storeName, String storeCode, WebDriver driver) {
		super();
		this.num = num;
		this.retryTimes = retryTimes;
		this.storeName = storeName;
		this.storeCode = storeCode;
		this.driver = driver;
	}




	public DennisFileDownloadThread(String url, int num, int retryTimes, String storeName, String storeCode) {
		this.url = url;
		this.num = num;
		this.retryTimes = retryTimes;
		this.storeName = storeName;
		this.storeCode=storeCode;
	}




	public int getRetryTimes() {
		return retryTimes;
	}




	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}




	public String getStoreName() {
		return storeName;
	}




	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}




	public String getStoreCode() {
		return storeCode;
	}




	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}




	public WebDriver getDriver() {
		return driver;
	}




	public void setDriver(WebDriver driver) {
		this.driver = driver;
	}




	public void run(){
		System.out.println("��ʼִ�и��߳�");
		
//		
//		System.out.println("��ǰΪ��"+i+"���ŵ�:"+storeName);
//		new Select(driver.findElement(By.name("storecode"))).selectByIndex(i);
//		driver.findElement(By.name("Submit")).click();
//		
		
		
		Date date = new Date();
		String downloadDir = "D:/ksj/downloads/Dennis/download/inventory";
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr1 = sdf1.format(date);
		String dateStr2 = sdf2.format(date).replace("-", "_").replace(":", "_").replace(" ", "_");
		OutputStreamWriter out = null;
		
		// "KSJ.Vendorname_Inventory_Dennis.20180531.req2018_06_01_15_01_33.txt";
		String fileName = "KSJ.PNG_Inventory_Dennis."+storeName+dateStr1+".req"+dateStr2+".txt";
		
		boolean downloadSuccess = false;
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
		
		
		int totalPages;
		int curPageNbr;
		for(int i =0;i<retryTimes;i++){
			
			try {
				
				String isNull = driver.findElement(
						By.xpath("//td[@bgcolor='black']/table/tbody/tr[3]/td[1]//font")).getText().trim();//"//"��ʾ��ȡ��������font
				
				System.out.println(isNull);
				// ��ȡ��ҳ��Ϣ
				By location = By.xpath("//td[@align='left'][@valign='bottom']/font");
				if (isWebElementExist(driver, location) != true) {
					if("��û�п������".equals(isNull)){
						System.out.println("��û�п������");
						return;
					}else{
						totalPages =1;
						curPageNbr =1;
						System.out.println(storeName+"�ܹ���1ҳ����");
					}
				}else{
					String pageInfo = driver.findElement(
							By.xpath("//td[@align='left'][@valign='bottom']/font")).getText();
					totalPages = Integer.parseInt(pageInfo.replace("��ǰ", "").replace("ҳ", "").split("/")[1]);
					curPageNbr = Integer.parseInt(pageInfo.replace("��ǰ", "").replace("ҳ", "").split("/")[0]);
					
					System.out.println(storeName+"�ܹ���"+totalPages+"ҳ����");
				}
				//TODO
				for (; curPageNbr <= totalPages; curPageNbr++) {
					System.out.println("��ǰ����ҳ��:"+curPageNbr+",��ҳ��:"+totalPages);
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
					
				
				
			} catch (Exception e1) {
				System.out.println("Error when parse latest update date from web, ignore this file downloading.");
				downloadSuccess = false;
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				continue;
			}
			
			downloadSuccess = true;
			break;
			
	    
		}
		
		if (!downloadSuccess) {
			System.out.println(fileName + "is failed to download after max retry times, sending email...");
//			StringBuffer emailContext = new StringBuffer();
//			emailContext.append("Download failed for file: " + fileName);
//			MailUtil.sendMailByMailType(EmailTypes.VALIDATIONFAILEDMAIL, emailContext.toString());
		}
		
		
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("�߳�ִ�н���");
		
		
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


