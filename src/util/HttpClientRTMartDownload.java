
package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Cookie;

import com.rsi.connector.dennis.DennisInventoryDownload;
import com.rsi.connector.dennis.DennisSalesDownload;

/**
 * @author Johnny.Shi
 *
 */
public class HttpClientRTMartDownload {

	private static Logger log = LogMgr.getLogger(LogMgr.connector);

	public HttpClientRTMartDownload() {
		super();
		// TODO Auto-generated constructor stub
	}

	public boolean downloadSalesFile(String url, Set<Cookie> cookies, String downloadDirectory, String fileName,
			String host, int curPageNbr, String year, String month, String day, String storeCode, String storeName, int currentStoreIndex) throws Exception {
		long filelongth = 0;
		boolean success= false;
		File file = null;
		BufferedReader br = null;
		FileWriter fw = null;
		InputStreamReader isr = null;
		DefaultHttpClient httpclient = null;
		try {
			// 请求超时时间 1分钟
			// httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
			// 60*1000);
			// 读取超时时间 2分钟

			HttpPost httppost = new HttpPost(url);
			// httpget.addHeader("Host", "supplier.rt-mart.com.cn");
			httppost.addHeader("Host", host);
			httppost.addHeader("Connection", "keep-alive");
			httppost.addHeader("Accept-Language", "ch-ZN");
			httppost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:16.0) Gecko/20100101 Firefox/16.0");

			List<NameValuePair> qparams = new ArrayList<NameValuePair>();
			qparams.add(new BasicNameValuePair("TranDaye", day));
			qparams.add(new BasicNameValuePair("TranDays", day));
			qparams.add(new BasicNameValuePair("TranMonthe", month));
			qparams.add(new BasicNameValuePair("TranMonths", month));
			qparams.add(new BasicNameValuePair("TranYeare", year));
			qparams.add(new BasicNameValuePair("TranYears", year));
			qparams.add(new BasicNameValuePair("mode", "search"));
			qparams.add(new BasicNameValuePair("storecode", storeCode));
			qparams.add(new BasicNameValuePair("whichpage", curPageNbr + ""));

			UrlEncodedFormEntity params = new UrlEncodedFormEntity(qparams, HTTP.UTF_8);

			httppost.setEntity(params);
			HttpContext localContext = new BasicHttpContext();
			CookieStore cs = new BasicCookieStore();
			for (Cookie ck : cookies) {
				BasicClientCookie bcc = new BasicClientCookie(ck.getName(), ck.getValue());
				bcc.setDomain(ck.getDomain());
				bcc.setPath(ck.getPath());
				cs.addCookie(bcc);
			}
			localContext.setAttribute(ClientContext.COOKIE_STORE, cs);
			// Execute the request
			for (int i = 0; i < 3; i++) {
				httpclient = new DefaultHttpClient();
				httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 2 * 60 * 1000);
				try {
					HttpResponse response = httpclient.execute(httppost, localContext);

					Header[] resHeaders = response.getAllHeaders();
					log.info("print all response headers info");
					log.info("*********************");
					for (Header h : resHeaders) {
						log.info(h.getName() + "\t" + h.getValue());
					}
					log.info("print all response headers info done");
					log.info("*********************");
					// Examine the response status
					StatusLine statusLine = response.getStatusLine();
					int statusCode = statusLine.getStatusCode();
					log.info(response.getStatusLine());
					System.out.println(response.getStatusLine());
					HttpEntity entity = response.getEntity();
					
					if (statusCode == 200) {
						if (entity != null) {
							log.info("Content Length " + entity.getContentLength() + " Bytes");
							log.info(entity.getContentType());
							log.info(entity.getContentEncoding());

							file = new File(downloadDirectory + File.separator + fileName);
							if (!(new File(downloadDirectory + File.separator).exists())) {
								new File(downloadDirectory + File.separator).mkdirs();
							}
							/* update by RubyWang 文件中部分中文乱码问题 */
							long t2 = System.currentTimeMillis();
							if (entity != null) {
								BufferedHttpEntity buf = new BufferedHttpEntity(entity);
								isr = new InputStreamReader(buf.getContent(), "GBK");
								br = new BufferedReader(isr);
								fw = new FileWriter(file, false);
								String str = null;
								while ((str = br.readLine()) != null) {
									fw.write(str + "\n");
								}
								fw.flush();
							}
							EntityUtils.consume(entity);
							long t3 = System.currentTimeMillis();
							filelongth = file.length();
							log.info("filelongth: " + filelongth);
							log.info(fileName + " File download successfully!!!");
							success = true;
							DennisSalesDownload.resetIndexes();
							break;
							// do something useful with the response
						}
					}else{
						//  do something
						DennisSalesDownload.currentStoreCode = storeCode;
						DennisSalesDownload.currentStoreName = storeName;
						DennisSalesDownload.currentStoreIndex = currentStoreIndex;
						DennisSalesDownload.currentPageNbr = curPageNbr;
						success=false;
					}
					
				} catch (Exception e) {
					log.info("exception when download file, The name is : " + fileName);
					log.error("download failed", e.fillInStackTrace());
					log.info("current retry count is : " + i);
					httpclient.getConnectionManager().shutdown();
				}
			}
		} catch (Exception e) {
			success=false;
			log.error("exception " + e.fillInStackTrace());
			throw e;
		} finally {
			// Closing the input stream will trigger connection release
			// instream.close();
			// fos.close();
			try {
				if (null != isr) {
					isr.close();
				}
				if (null != br) {
					br.close();
				}
				if (null != fw) {
					fw.close();
				}
				httpclient.getConnectionManager().shutdown();
			} catch (IOException e) {
				throw e;
			}
		}
		return success;
	}

	public boolean downloadInventoryFile(String url, Set<Cookie> cookies, String downloadDirectory, String fileName,
			String host, int curPageNbr, String storeCode, String storeName, int currentStoreIndex) throws Exception {
		long filelongth = 0;
		boolean success= false;
		File file = null;
		BufferedReader br = null;
		FileWriter fw = null;
		InputStreamReader isr = null;
		DefaultHttpClient httpclient = null;
		try {
			// 请求超时时间 1分钟
			// httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
			// 60*1000);
			// 读取超时时间 2分钟

			HttpPost httppost = new HttpPost(url);
			// httpget.addHeader("Host", "supplier.rt-mart.com.cn");
			httppost.addHeader("Host", host);
			httppost.addHeader("Connection", "keep-alive");
			httppost.addHeader("Accept-Language", "ch-ZN");
			httppost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; rv:16.0) Gecko/20100101 Firefox/16.0");

			List<NameValuePair> qparams = new ArrayList<NameValuePair>();
			qparams.add(new BasicNameValuePair("mode", "search"));
			qparams.add(new BasicNameValuePair("storecode", storeCode));
			qparams.add(new BasicNameValuePair("whichpage", curPageNbr + ""));

			UrlEncodedFormEntity params = new UrlEncodedFormEntity(qparams, HTTP.UTF_8);

			httppost.setEntity(params);
			HttpContext localContext = new BasicHttpContext();
			CookieStore cs = new BasicCookieStore();
			for (Cookie ck : cookies) {
				BasicClientCookie bcc = new BasicClientCookie(ck.getName(), ck.getValue());
				bcc.setDomain(ck.getDomain());
				bcc.setPath(ck.getPath());
				cs.addCookie(bcc);
			}
			localContext.setAttribute(ClientContext.COOKIE_STORE, cs);
			// Execute the request
			for (int i = 0; i < 3; i++) {
				httpclient = new DefaultHttpClient();
				httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 2 * 60 * 1000);
				try {
					HttpResponse response = httpclient.execute(httppost, localContext);
					Thread.sleep(1000);
					Header[] resHeaders = response.getAllHeaders();
					log.info("print all response headers info");
					log.info("*********************");
					for (Header h : resHeaders) {
						log.info(h.getName() + "\t" + h.getValue());
					}
					log.info("print all response headers info done");
					log.info("*********************");
					// Examine the response status
					StatusLine statusLine = response.getStatusLine();
					int statusCode = statusLine.getStatusCode();
					log.info(response.getStatusLine());
					System.out.println(response.getStatusLine());
					HttpEntity entity = response.getEntity();
					if (statusCode == 200) {
						if (entity != null) {
							log.info("Content Length " + entity.getContentLength() + " Bytes");
							log.info(entity.getContentType());
							log.info(entity.getContentEncoding());

							file = new File(downloadDirectory + File.separator + fileName);
							if (!(new File(downloadDirectory + File.separator).exists())) {
								new File(downloadDirectory + File.separator).mkdirs();
							}
							/* update by RubyWang 文件中部分中文乱码问题 */
							long t2 = System.currentTimeMillis();
							if (entity != null) {
								BufferedHttpEntity buf = new BufferedHttpEntity(entity);
								isr = new InputStreamReader(buf.getContent(), "GBK");
								br = new BufferedReader(isr);
								fw = new FileWriter(file, false);
								String str = null;
								while ((str = br.readLine()) != null) {
									fw.write(str + "\n");
								}
								fw.flush();
							}
							EntityUtils.consume(entity);
							long t3 = System.currentTimeMillis();
							filelongth = file.length();
							log.info("filelongth: " + filelongth);
							log.info(fileName + " File download successfully!!!");
							success = true;
							DennisInventoryDownload.resetIndexes();
							break;
							// do something useful with the response
						}
					}else{
						//  do something
						DennisInventoryDownload.currentStoreCode = storeCode;
						DennisInventoryDownload.currentStoreName = storeName;
						DennisInventoryDownload.currentStoreIndex = currentStoreIndex;
						DennisInventoryDownload.currentPageNbr = curPageNbr;
						success=false;
					}
				} catch (Exception e) {
					log.info("exception when download file, The name is : " + fileName);
					log.error("download failed", e.fillInStackTrace());
					log.info("current retry count is : " + i);
					httpclient.getConnectionManager().shutdown();
				}
			}
		} catch (Exception e) {
			success=false;
			log.error("exception " + e.fillInStackTrace());
			throw e;
		} finally {
			// Closing the input stream will trigger connection release
			// instream.close();
			// fos.close();
			try {
				if (null != isr) {
					isr.close();
				}
				if (null != br) {
					br.close();
				}
				if (null != fw) {
					fw.close();
				}
				httpclient.getConnectionManager().shutdown();
			} catch (IOException e) {
				throw e;
			}
		}
		return success;
	}

}
