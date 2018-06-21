package com.rsi.connector.dennis;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;

//import com.ksj.connector.watsons.operation.WatsonsInventoryHandler;
//import com.rsi.connector.common.mail.EmailTypes;
//import com.rsi.connector.common.mail.MailUtil;
//import com.rsi.connector.common.util.ExcelXMlSAXHandler;
//import com.rsi.connector.common.util.Utility;
//import com.rsi.connector.status.ReportConfigType;
//import com.rsi.datatools.connectors.entities.ProcessInstance;
//import com.rsi.datatools.connectors.entities.Report;
//
//import sun.misc.CharacterEncoder;

public class FileDownloadThread implements Runnable {
	public static final Logger LOG = LogManager.getLogger(FileDownloadThread.class);

	String URL;
	CookieStore cookieStore;
	HashMap<String, String> headers;
	List<NameValuePair> qparams;
	String downloadDir;
	int validFileSize;
	int retryTimes;
	String reportType;

	public FileDownloadThread(String uRL, CookieStore cookieStore, HashMap<String, String> headers,
			List<NameValuePair> qparams, String downloadDir, int validFileSize,
			int retryTimes, String reportType) {
		super();
		URL = uRL;
		this.cookieStore = cookieStore;
		this.headers = headers;
		this.qparams = qparams;
		this.downloadDir = downloadDir;
		this.validFileSize = validFileSize;
		this.retryTimes = retryTimes;
		this.reportType = reportType;
	}

	@Override
	public void run() {
		try {
			boolean downloadSuccess = false;
			String fileName = report.getFilename();
			for (int i = 0; i < retryTimes; i++) {
				DefaultHttpClient client = null;
				client = new DefaultHttpClient();
				client = (DefaultHttpClient) Utility.wrapClient(client);
				HttpContext localContext = new BasicHttpContext();
				localContext.setAttribute(ClientContext.COOKIE_STORE, this.cookieStore);

				HttpPost httppost = new HttpPost(this.URL);
				Set<String> keys = headers.keySet();
				for (String key : keys) {
					httppost.addHeader(key, headers.get(key));
				}
				UrlEncodedFormEntity params = new UrlEncodedFormEntity(qparams, HTTP.UTF_8);
				httppost.setEntity(params);

				CloseableHttpResponse fileDownResponse = (CloseableHttpResponse) client.execute(httppost, localContext);
				Thread.sleep(3000);
				HttpEntity entity = fileDownResponse.getEntity();
				File fileToWrite = new File(downloadDir + File.separator + fileName);
				if(fileToWrite.exists()){
					fileToWrite.delete();
				}
				FileOutputStream fos = new java.io.FileOutputStream(fileToWrite);
				entity.writeTo(fos);
				fos.flush();
				fos.close();
				EntityUtils.consume(entity);
				fileDownResponse.close();

				try {
					File downloadedFile = new File(downloadDir + File.separator + fileName);
					if (downloadedFile.exists()) {
						if (downloadedFile.length() < validFileSize) {
							downloadedFile.delete();
							LOG.info("Instance: " + ins.getiId() + " - " + "File: " + fileName
									+ " size is 0, not valid, delete, will try again");
							downloadSuccess = false;
							continue;
						} else {
							if (ReportConfigType.CFG_WATSONS_INVENTORY.equals(reportType)) {
								SAXParserFactory parserFactor = SAXParserFactory.newInstance();
								SAXParser parser = parserFactor.newSAXParser();
								ExcelXMlSAXHandler handler = new ExcelXMlSAXHandler();
								try {
									String fileContent = IOUtils.toString(new FileInputStream(downloadedFile),"UTF-8");
									boolean containsAnd = fileContent.contains("&");
									if (containsAnd) {
										fileContent = fileContent.replaceAll("&", "and");
									}
//									
									ByteArrayInputStream bis = new ByteArrayInputStream(fileContent.getBytes());
									parser.parse(bis, handler);
//									parser.parse(downloadedFile, handler);
									
									if (containsAnd) {
										BufferedWriter out = null;

										try {
											out = new BufferedWriter(new OutputStreamWriter(
													new FileOutputStream(downloadedFile), "UTF-8"));
											out.write(fileContent);

											// System.out.println("Done");

										} catch (IOException e) {
											e.printStackTrace();
										} finally {
											try {
												if (out != null)
													out.close();
											} catch (IOException ex) {
											}

										}
									}
//									DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//									DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//									Document doc = dBuilder.parse(downloadedFile);
								} catch (Exception e) {
//									downloadedFile.delete();
									downloadSuccess = false;
									System.out.println("第"+(i+1)+"次下载过程出现错误，将重新下载");
									Thread.sleep(5000);
									continue;
								}
							}
							LOG.info("Instance: " + ins.getiId() + " - " + "File: " + fileName
									+ " size is valid, downloading next...");
							downloadSuccess = true;
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (!downloadSuccess) {
				LOG.info("Instance: " + ins.getiId() + " - " + "file: " + fileName
						+ " failed to download after max retry times, sending email...");
				StringBuffer emailContext = new StringBuffer();
				emailContext.append("Download failed for file: " + fileName);
				MailUtil.sendMailByMailType(EmailTypes.VALIDATIONFAILEDMAIL, emailContext.toString());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public CookieStore getCookieStore() {
		return cookieStore;
	}

	public void setCookieStore(CookieStore cookieStore) {
		this.cookieStore = cookieStore;
	}

	public String getURL() {
		return URL;
	}

	public HashMap<String, String> getHeaders() {
		return headers;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

	public void setHeaders(HashMap<String, String> headers) {
		this.headers = headers;
	}

	public List<NameValuePair> getQparams() {
		return qparams;
	}

	public void setQparams(List<NameValuePair> qparams) {
		this.qparams = qparams;
	}

	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public ProcessInstance getIns() {
		return ins;
	}

	public void setIns(ProcessInstance ins) {
		this.ins = ins;
	}

	public String getDownloadDir() {
		return downloadDir;
	}

	public void setDownloadDir(String downloadDir) {
		this.downloadDir = downloadDir;
	}

	public int getValidFileSize() {
		return validFileSize;
	}

	public void setValidFileSize(int validFileSize) {
		this.validFileSize = validFileSize;
	}

	public int getRetryTimes() {
		return retryTimes;
	}

	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

}
