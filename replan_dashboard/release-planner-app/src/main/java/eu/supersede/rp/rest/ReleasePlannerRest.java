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
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
//http://62.14.219.13:8280/replan/projects/1/releases
//http://localhost:8083/replan/projects/1/features
@RestController
@RequestMapping("/replan/projects/1")
public class ReleasePlannerRest{
	
	@RequestMapping(value = "/**", method = {RequestMethod.GET,  RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
	public ResponseEntity<?> get(HttpServletRequest request, HttpServletResponse httpServletResponse) throws IOException {
		
		CloseableHttpResponse response = null;
		
		//append host
		StringBuilder sb = new StringBuilder("http://62.14.219.13:8280");
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

 		try {
			int code = 500;
			
			
			RequestConfig.Builder requestBuilder = RequestConfig.custom();
			requestBuilder = requestBuilder.setConnectTimeout(10 * 1000);
			requestBuilder = requestBuilder.setConnectionRequestTimeout(10 * 1000);

			HttpClientBuilder builder = HttpClientBuilder.create();     
			builder.setDefaultRequestConfig(requestBuilder.build());
			CloseableHttpClient httpclient = builder.build();
			
			
			if("GET".equals(request.getMethod())){
				//create
				HttpGet httpGet = new HttpGet(sb.toString());			

				//execute
				response = httpclient.execute(httpGet);
			}
			else if("POST".equals(request.getMethod())){
				//create
				HttpPost httpPost = new HttpPost(sb.toString());
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
				HttpPut httpPut = new HttpPut(sb.toString());
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
				HttpDelete httpDelete = new HttpDelete(sb.toString());
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
				return new ResponseEntity<> (bodyResponse, org.springframework.http.HttpStatus.valueOf(code));
			}
		} finally {
			if(response != null){
				response.close();	
			}
		}
	}
	
	//help methods
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
