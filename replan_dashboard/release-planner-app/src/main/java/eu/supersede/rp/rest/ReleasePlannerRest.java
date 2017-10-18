package eu.supersede.rp.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

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
import org.springframework.context.annotation.PropertySource;
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
@PropertySource("classpath:release_planner_app.properties")
public class ReleasePlannerRest{
	
//	@Autowired
//	private Environment env;
	
	@Value("${rest.server.url.development}")
	private String restServerUrlDevelopment;
	
	@Value("${rest.server.url.production}")
	private String restServerUrlProduction;
	
	@Value("${rest.server.proxy}")
	private String restServerProxy;
	
	@Value("${rest.server.port}")
	private String restServerPort;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());	
	
	@RequestMapping(value = "/hello", method = {RequestMethod.GET})
	public String  hello (HttpServletRequest request, HttpServletResponse httpServletResponse) throws IOException {
		
		StringBuilder sb = new StringBuilder();
		
		log.debug("[" + restServerUrlDevelopment+ "]");
		sb.append("[" + restServerUrlDevelopment+ "]");
		
		
		RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
		if(runtimeMxBean != null){
			List<String> arguments = runtimeMxBean.getInputArguments();
			if(arguments != null){
				log.debug(" ------ JVM arguments ------");
				sb.append(" ------ JVM arguments ------");
				sb.append("\n");
				for (String arg : arguments) {
					log.debug("[" + arg+ "]");
					sb.append("[" + arg+ "]");
					sb.append("\n");
				}
				log.debug(" ------ -- ------");
				sb.append(" ------ -- ------");
				sb.append("\n");
			}
		}
		
		Map<String, String> envMap = System.getenv();
		SortedMap<String, String> sortedEnvMap = new TreeMap<String, String>(envMap);
		//Set<String> keySet = sortedEnvMap.keySet();
		log.debug(" ------ SYSTEM variables ------");
		sb.append(" ------ SYSTEM variables ------");
		sb.append("\n");
		for (Entry<String, String> entry : sortedEnvMap.entrySet()) {
			
			log.debug("[" +  entry.getKey() + "] " +  entry.getValue());
			sb.append("[" +  entry.getKey() + "] " +  entry.getValue());
			sb.append("\n");
		}
		log.debug(" ------ -- ------");
		sb.append(" ------ -- ------");
		sb.append("\n");
		
		return sb.toString(); 
	}
	

//	@RequestMapping(value = "/dev", method = {RequestMethod.GET})
//	public ResponseEntity<?>  dev (HttpServletRequest request, HttpServletResponse httpServletResponse) throws IOException {
//		int code = 500;
//		
//		CloseableHttpResponse response = null;
//		
//		try {
//			
//			RequestConfig.Builder requestBuilder = RequestConfig.custom();
//			requestBuilder = requestBuilder.setConnectTimeout(60 * 1000);
//			requestBuilder = requestBuilder.setConnectionRequestTimeout(60 * 1000);
//			if(restServerProxy != null && !restServerProxy.isEmpty() && restServerPort != null && !restServerPort.isEmpty()){
//				HttpHost proxy = new HttpHost(restServerProxy, Integer.parseInt(restServerPort));
//				requestBuilder = requestBuilder.setProxy(proxy);
//	       }
//		   
//			HttpClientBuilder builder = HttpClientBuilder.create();     
//			builder.setDefaultRequestConfig(requestBuilder.build());
//			CloseableHttpClient httpclient = builder.build();
//			//create
//			HttpGet httpGet = new HttpGet("http://62.14.219.13:8280/replan/projects/siemens/features?status=pending");			
//
//			//execute
//			response = httpclient.execute(httpGet);
//			if(response != null && response.getStatusLine() != null){
//				code = response.getStatusLine().getStatusCode();
//			}
//			
//			if(	code == 200){
//
//				Header[] headers = response.getAllHeaders();
//
//				for (Header header : headers) {
//					if("Content-Type".equals(header.getName())){
//						httpServletResponse.setHeader(header.getName(), header.getValue());
//						continue;
//					}
//				}
//
//				try (InputStream inputStream = response.getEntity().getContent();
//						OutputStream outputStream = httpServletResponse.getOutputStream();
//						)
//						{
//					IOUtils.copyLarge(inputStream, outputStream);
//						}
//				log.debug("Reponse code: " + HttpStatus.OK.toString());
//				return new ResponseEntity<>(HttpStatus.OK);
//
//
//			}
//			else{
//				String bodyResponse = getBodyResponse(response);
//				log.debug("Error "+ code + " in my rest client(body response): " + bodyResponse);
//				return new ResponseEntity<> (bodyResponse, org.springframework.http.HttpStatus.valueOf(code));
//			}
//		}catch (Exception e) {
//			log.debug("Error Exception in my rest client: " + e.getMessage());
//			log.error("Error Exception in my rest client", e);
//			return new ResponseEntity<> (e.getMessage(), org.springframework.http.HttpStatus.valueOf(code));
//		} 
// 		finally {
//			if(response != null){
//				response.close();	
//			}
//		}
//	
//	}
	
	
	
	
//	@RequestMapping(value = "/production", method = {RequestMethod.GET})
//	public ResponseEntity<?>  production (HttpServletRequest request, HttpServletResponse httpServletResponse) throws IOException {
//		int code = 500;
//		
//		CloseableHttpResponse response = null;
//		
//		try {
//			
//			RequestConfig.Builder requestBuilder = RequestConfig.custom();
//			requestBuilder = requestBuilder.setConnectTimeout(60 * 1000);
//			requestBuilder = requestBuilder.setConnectionRequestTimeout(60 * 1000);
//			if(restServerProxy != null && !restServerProxy.isEmpty() && restServerPort != null && !restServerPort.isEmpty()){
//				HttpHost proxy = new HttpHost(restServerProxy, Integer.parseInt(restServerPort));
//				requestBuilder = requestBuilder.setProxy(proxy);
//	       }
//		   
//			HttpClientBuilder builder = HttpClientBuilder.create();     
//			builder.setDefaultRequestConfig(requestBuilder.build());
//			CloseableHttpClient httpclient = builder.build();
//			//create
//			
//			HttpGet httpGet = new HttpGet("http://platform.supersede.eu:8280/replan/projects/siemens/features?status=pending");			
//
//			//execute
//			response = httpclient.execute(httpGet);
//			if(response != null && response.getStatusLine() != null){
//				code = response.getStatusLine().getStatusCode();
//			}
//			
//			if(	code == 200){
//
//				Header[] headers = response.getAllHeaders();
//
//				for (Header header : headers) {
//					if("Content-Type".equals(header.getName())){
//						httpServletResponse.setHeader(header.getName(), header.getValue());
//						continue;
//					}
//				}
//
//				try (InputStream inputStream = response.getEntity().getContent();
//						OutputStream outputStream = httpServletResponse.getOutputStream();
//						)
//						{
//					IOUtils.copyLarge(inputStream, outputStream);
//						}
//				log.debug("Reponse code: " + HttpStatus.OK.toString());
//				return new ResponseEntity<>(HttpStatus.OK);
//
//
//			}
//			else{
//				String bodyResponse = getBodyResponse(response);
//				log.debug("Error "+ code + " in my rest client(body response): " + bodyResponse);
//				return new ResponseEntity<> (bodyResponse, org.springframework.http.HttpStatus.valueOf(code));
//			}
//		}catch (Exception e) {
//			log.debug("Error Exception in my rest client: " + e.getMessage());
//			log.error("Error Exception in my rest client", e);
//			return new ResponseEntity<> (e.getMessage(), org.springframework.http.HttpStatus.valueOf(code));
//		} 
// 		finally {
//			if(response != null){
//				response.close();	
//			}
//		}
//	
//	}
	

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
			requestBuilder = requestBuilder.setConnectTimeout(60 * 1000);
			requestBuilder = requestBuilder.setConnectionRequestTimeout(60 * 1000);
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
				String rightUrl = getRightUrl(request, tenantId);
				//create
				HttpPost httpPost = new HttpPost(rightUrl);
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
				log.debug("body: " + body);
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
				log.debug("body: " + body);
				
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
				log.debug("Reponse code: " + HttpStatus.NOT_IMPLEMENTED.toString());
				return new ResponseEntity<> ("request method  not implemented", HttpStatus.NOT_IMPLEMENTED);
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
				log.debug("Reponse code: " + HttpStatus.OK.toString());
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
	
//	public static void main(String[] args) {
//		String blabla= "-Dsupersede.if.properties=if.production.properties";
//		if(blabla.contains("Dsupersede.if.properties")){
//			String splitted [] = blabla.split("=");
//			String supersedeIfProperties = splitted[1];
//			if("if.production.properties".equals(supersedeIfProperties)){
//				System.out.println(splitted.length);
//			}
//				
//		}
//		
//		
//	}
	//help methods
	
	//help methods
	private String getRightUrl(HttpServletRequest request, String tenant){
		
		String restControllerURL = restServerUrlDevelopment;
		RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
		List<String> arguments = runtimeMxBean.getInputArguments();
		String supersedeIfProperties = "";
		
		for (String arg : arguments) {
			if(arg.contains("Dsupersede.if.properties")){
				String splitted [] = arg.split("=");
				if(splitted.length >= 2){
					supersedeIfProperties = splitted[1];
					break;
				}
			}
		}
				
		if(supersedeIfProperties != null && "if.production.properties".equals(supersedeIfProperties)){
			log.debug("supersede.if.properties " + supersedeIfProperties);
			restControllerURL = restServerUrlProduction;
		}
		log.debug("restController URL " + restControllerURL);
		
		//append host
		StringBuilder sb = new StringBuilder(restControllerURL);
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
		log.debug("method: " + request.getMethod()); 
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
