package eu.supersede.rp.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
/**
 * The ReleasePlannerRest invoke the rest API service provides by Ruby on Rails
 * 
 * @author antonino.cassarino@siemens.com
 * 
 * 
 */
@RestController
@RequestMapping("/replan2")
public class ReleasePlannerRest2{
	
	@Value("${rest.server.url}")
	private String restServerUrl;
	
	@Value("${rest.server.proxy}")
	private String restServerProxy;
	
	@Value("${rest.server.port}")
	private String restServerPort;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());	
	
	@RequestMapping(value = "/hello", method = {RequestMethod.GET})
	public String  hello (HttpServletRequest request, HttpServletResponse httpServletResponse) throws IOException {
		 return "hello from Release Planner Rest Controller"; 
	}

	@RequestMapping(value = "/projects/{projectId}/releases", method = {RequestMethod.GET})
	public ResponseEntity<?> getReleases(@PathVariable String projectId, HttpServletRequest request, HttpServletResponse httpServletResponse) throws IOException {
		
		CloseableHttpResponse response = null;
		
	    int code = 500;
 		
		try {
			CloseableHttpClient httpclient = getCloseableHttpClient();
			if("GET".equals(request.getMethod())){
				//create
				HttpGet httpGet = new HttpGet(getRightUrl(request));			
				//execute
				response = httpclient.execute(httpGet);
			}
			
			return getResponseEntity(httpServletResponse, response);
			
		}catch (Exception e) {
			log.debug("Error Exception in "+ request.getRequestURI() +  ": " + e.getMessage());
			log.error("Error Exception in "+ request.getRequestURI(), e);
			return new ResponseEntity<> (e.getMessage(), org.springframework.http.HttpStatus.valueOf(code));
		} 
 		finally {
			if(response != null){
				response.close();	
			}
		}
	}
	
	@RequestMapping(value = "/projects/{projectId}/features", method = {RequestMethod.GET})
	public ResponseEntity<?> getFeature(HttpServletRequest request, HttpServletResponse httpServletResponse) throws IOException {
		
		CloseableHttpResponse response = null;
		
	    int code = 500;
 		
		try {
			CloseableHttpClient httpclient = getCloseableHttpClient();
			if("GET".equals(request.getMethod())){
				//create
				HttpGet httpGet = new HttpGet(getRightUrl(request));			
				//execute
				response = httpclient.execute(httpGet);
			}
			
			return getResponseEntity(httpServletResponse, response);
			
		}catch (Exception e) {
			log.debug("Error Exception in "+ request.getRequestURI() +  ": " + e.getMessage());
			log.error("Error Exception in "+ request.getRequestURI(), e);
			return new ResponseEntity<> (e.getMessage(), org.springframework.http.HttpStatus.valueOf(code));
		} 
 		finally {
			if(response != null){
				response.close();	
			}
		}
	}
	
	@RequestMapping(value = "/projects/{projectId}/releases/{releaseId}/features", method = {RequestMethod.GET})
	public ResponseEntity<?> getReleaseFeatures(HttpServletRequest request, HttpServletResponse httpServletResponse) throws IOException {
		
		CloseableHttpResponse response = null;
		
	    int code = 500;
 		
		try {
			CloseableHttpClient httpclient = getCloseableHttpClient();
			if("GET".equals(request.getMethod())){
				//create
				HttpGet httpGet = new HttpGet(getRightUrl(request));			
				//execute
				response = httpclient.execute(httpGet);
			}
			
			return getResponseEntity(httpServletResponse, response);
			
		}catch (Exception e) {
			log.debug("Error Exception in "+ request.getRequestURI() +  ": " + e.getMessage());
			log.error("Error Exception in "+ request.getRequestURI(), e);
			return new ResponseEntity<> (e.getMessage(), org.springframework.http.HttpStatus.valueOf(code));
		} 
 		finally {
			if(response != null){
				response.close();	
			}
		}
	}
	
	//help methods
	private String getRightUrl(HttpServletRequest request){
		//append host
		StringBuilder sb = new StringBuilder(restServerUrl);
		//append uri
		String uri = request.getRequestURI();
		
		if(request.getRequestURI().startsWith("//")){
			uri = uri.substring(1, uri.length());
		}	
		sb.append(uri);
		//append query string
		if(request.getQueryString() != null && !request.getQueryString().isEmpty()){
			sb.append("?");
			sb.append(request.getQueryString());
		}
		
		 String replaced = sb.toString().replace("replan2", "replan");
		 return replaced;
	}
	
	private CloseableHttpClient getCloseableHttpClient(){
		
		RequestConfig.Builder requestBuilder = RequestConfig.custom();
		requestBuilder = requestBuilder.setConnectTimeout(10 * 1000);
		requestBuilder = requestBuilder.setConnectionRequestTimeout(10 * 1000);
		if(restServerProxy != null && !restServerProxy.isEmpty() && restServerPort != null && !restServerPort.isEmpty()){
			HttpHost proxy = new HttpHost(restServerProxy, Integer.parseInt(restServerPort));
			requestBuilder = requestBuilder.setProxy(proxy);
       }
	   
		HttpClientBuilder builder = HttpClientBuilder.create();     
		builder.setDefaultRequestConfig(requestBuilder.build());
		CloseableHttpClient httpclient = builder.build();
		return httpclient;
		
	}
	
	private ResponseEntity<?> getResponseEntity(HttpServletResponse httpServletResponse,CloseableHttpResponse response) throws IOException{
	
		int code = 500;
		
		if(response != null && response.getStatusLine() != null){
			code = response.getStatusLine().getStatusCode();
		}
		
		if(	code == 200){

			Header[] headers = response.getAllHeaders();

			for (Header header : headers) {
				if("Content-Type".equals(header.getName())){
					httpServletResponse.setHeader(header.getName(), header.getValue());
					continue;
				}
			}

			try (InputStream inputStream = response.getEntity().getContent();
					OutputStream outputStream = httpServletResponse.getOutputStream();
					)
					{
				IOUtils.copyLarge(inputStream, outputStream);
					}
			return new ResponseEntity<>(HttpStatus.OK);


		}
		else{
			String bodyResponse = getBodyResponse(response);
			log.debug("Error 500 in my rest client(body response): " + bodyResponse);
			return new ResponseEntity<> (bodyResponse, org.springframework.http.HttpStatus.valueOf(code));
		}
		
	}
	
	public static String getBodyRequest(HttpServletRequest request) throws IOException {

		String body = null;
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;

		try {
			InputStream inputStream = request.getInputStream();
			if (inputStream != null) {
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				char[] charBuffer = new char[128];
				int bytesRead = -1;
				while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			} else {
				stringBuilder.append("");
			}
		} catch (IOException ex) {
			throw ex;
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException ex) {
					throw ex;
				}
			}
		}

		body = stringBuilder.toString();
		return body;
	}
	
	public static String getBodyResponse(CloseableHttpResponse response) throws IllegalStateException, IOException{
		StringBuilder sbResponseBody = new StringBuilder();
		BufferedReader d;
		if(response.getEntity() != null){
			if(response.getEntity().getContent() != null){
				d = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				sbResponseBody = new StringBuilder();
				String line = null;
				while ((line = d.readLine()) != null) {
					sbResponseBody.append(line);
				}
			}
		}
		return sbResponseBody.toString();
	}

}
