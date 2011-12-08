/*
 * Copyright (C) 2011 Alex Kuiper
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.nightwhistler.pageturner.sync;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenKeyvalProgressService implements ProgressService {
	
	private static final Logger LOG = LoggerFactory.getLogger(OpenKeyvalProgressService.class);	

	private String userId;
	
	private static final String BASE_URL = "http://api.openkeyval.org/";
	private static final int HTTP_SUCCESS = 200;
	
	private HttpClient client;
	private HttpContext context;
	
	public OpenKeyvalProgressService() {				
		this.client = new DefaultHttpClient();
		this.context = new BasicHttpContext();
	}
	
	@Override
	public void setEmail(String email) {
		this.userId = email;
	}
	
	@Override
	public BookProgress getProgress(String fileName) {
						
		if ( "".equals( this.userId) || "".equals(fileName) ) {
			return null;
		}
		
		String key = computeKey(fileName);
		
		LOG.debug( "Doing progress query for key: " + key );
		
		HttpGet get = new HttpGet( BASE_URL + key );
		
		try {
			HttpResponse response = client.execute(get);
			
			int statusCode = response.getStatusLine().getStatusCode();
			
			LOG.debug( "Got status " + statusCode + " from server.");
			
			if ( statusCode != HTTP_SUCCESS ) {
				return null;
			}			
			
			String responseString = EntityUtils.toString(response.getEntity());
			
			String[] parts = responseString.split(":");
			
			if ( parts.length != 2) {
				return null;
			}		
			
			return new BookProgress(fileName, 
					Integer.parseInt(parts[0]),
					Integer.parseInt(parts[1]));
			
		} catch (Exception e) {
			LOG.error( "Got error while querying server", e );			
			return null;
		} 
		
	}
	
	private String computeKey( String fileName ) {		
		
		String filePart = fileName;
		
		if ( fileName.indexOf("/") != -1 ) {
			filePart = fileName.substring( fileName.lastIndexOf('/') );
		}
		
		String plainTextKey = this.userId + ":" + filePart;
		
		String hash = Integer.toHexString( plainTextKey.hashCode() );
		
		return "PageTurner-key-" + hash;		
	}
	
	@Override
	public void storeProgress(String fileName, int index, int progress) {
		
		if ( "".equals( this.userId) ) {
			return;
		}
		
		String key = computeKey(fileName);
		
		LOG.debug("Posting update for key: " + key );
				
		HttpPost post = new HttpPost( BASE_URL + key );
		
		try {
			
			NameValuePair pair = new BasicNameValuePair("data", "" + index + ":" + progress );
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add( pair );
			
			post.setEntity( new UrlEncodedFormEntity(pairs) );
			HttpResponse response = client.execute(post, this.context);
			
			LOG.debug("Got status " + response.getStatusLine().getStatusCode() + " from server.");
			
		} catch (IOException io) {
			LOG.error("Got error while POSTing update:", io);			
		}		
		
	}
	
}
