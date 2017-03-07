package eu.supersede.rp.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.supersede.fe.security.DatabaseUser;
/**
 * The ReleasePlannerRest invoke the rest API service provides by Ruby on Rails
 * 
 * @author antonino.cassarino@siemens.com
 * 
 * 
 */
@RestController
@RequestMapping("/replan/projects")
public class ReleasePlannerRest{
	
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

	@RequestMapping(value = "/tenant/**", method = {RequestMethod.GET,  RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
	public ResponseEntity<?> get(HttpServletRequest request, HttpServletResponse httpServletResponse) throws IOException {
	
			
		CloseableHttpResponse response = null;
		
		int code = 500;
 		
		try {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if(auth == null){
				log.error("Authentication is null");
				return new ResponseEntity<>(org.springframework.http.HttpStatus.valueOf(code));
			}
			
			DatabaseUser currentUser = (DatabaseUser) auth.getPrincipal();
			if(currentUser == null){
				log.error("DatabaseUser is null");
				return new ResponseEntity<>("Logged user not found", org.springframework.http.HttpStatus.valueOf(code));
			}
			
			String tenantId = currentUser.getTenantId();
			if(tenantId == null || tenantId.isEmpty()){
				log.error("tenantId not found ");
				return new ResponseEntity<>("TenantId not found", org.springframework.http.HttpStatus.valueOf(code));
			}
			
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
			
			
			if("GET".equals(request.getMethod())){
				//create
				HttpGet httpGet = new HttpGet(getRightUrl(request, tenantId));			

				//execute
				response = httpclient.execute(httpGet);
			}
			else if("POST".equals(request.getMethod())){
				//create
				HttpPost httpPost = new HttpPost(getRightUrl(request, tenantId));
				//add headers
				// add only content-type header from request
				@SuppressWarnings("rawtypes")
				Enumeration headerNames = request.getHeaderNames();
				while (headerNames.hasMoreElements()) {
					String key = (String) headerNames.nextElement();
					String value = request.getHeader(key);
					if("content-type".equals(key)){
						httpPost.setHeader(key, value);
					}
				}
				//add body request
				String body = getBodyRequest(request);
				StringEntity bodyStringEntity = new StringEntity(body);
				httpPost.setEntity(bodyStringEntity);
				
				//execute
				response = httpclient.execute(httpPost);
			}
			else if("PUT".equals(request.getMethod())){
				//create
				HttpPut httpPut = new HttpPut(getRightUrl(request, tenantId));
				//add headers
				// add only content-type header from request
				@SuppressWarnings("rawtypes")
				Enumeration headerNames = request.getHeaderNames();
				while (headerNames.hasMoreElements()) {
					String key = (String) headerNames.nextElement();
					String value = request.getHeader(key);
					if("content-type".equals(key)){
						httpPut.setHeader(key, value);
					}
				}
				//add body request
				String body = getBodyRequest(request);
				StringEntity bodyStringEntity = new StringEntity(body);
				httpPut.setEntity(bodyStringEntity);
				
				//execute
				response = httpclient.execute(httpPut);
			}
			else if("DELETE".equals(request.getMethod())){
				//create
				HttpDelete httpDelete = new HttpDelete(getRightUrl(request, tenantId));
				//execute
				response = httpclient.execute(httpDelete);			
			}
			else{
				return new ResponseEntity<> ("request method  not implemented", org.springframework.http.HttpStatus.NOT_IMPLEMENTED);
			} 
			
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
				log.debug("Error "+ code + " in my rest client(body response): " + bodyResponse);
				return new ResponseEntity<> (bodyResponse, org.springframework.http.HttpStatus.valueOf(code));
			}
		}catch (Exception e) {
			log.debug("Error Exception in my rest client: " + e.getMessage());
			log.error("Error Exception in my rest client", e);
			return new ResponseEntity<> (e.getMessage(), org.springframework.http.HttpStatus.valueOf(code));
		} 
 		finally {
			if(response != null){
				response.close();	
			}
		}
	}
	
	//help methods
	
	//help methods
	private String getRightUrl(HttpServletRequest request, String tenant){
		//append host
		StringBuilder sb = new StringBuilder(restServerUrl);
		//append uri
		String uri = request.getRequestURI();
		//request.getRequestURL();
		String replacedURI ="";
		if(uri.contains("/release-planner-app")){
			replacedURI = uri.replace("/release-planner-app", "");
		}else{
			replacedURI = uri;
		}
		if(request.getRequestURI().startsWith("//")){
			replacedURI = replacedURI.substring(1, replacedURI.length());
		}	
		sb.append(replacedURI);
		//append query string
		if(request.getQueryString() != null && !request.getQueryString().isEmpty()){
			sb.append("?");
			sb.append(request.getQueryString());
		}
		
		String replaced = sb.toString().replace("tenant", tenant);
		 
		log.debug("replaced url: " + replaced);
		 
		 return replaced;
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
