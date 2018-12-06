package it.almawave.gateway.crm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.logging.Logger;
@Stateless
@LocalBean
public class CRMClient{
	private static final Logger LOGGER = Logger.getLogger(CRMClient.class);
	private CloseableHttpClient httpclient;	
	private HttpHost targetHost;
	private HttpPost httppost;
	private HttpClientContext context;


	/**
	 * Default constructor. 
	 */
	public CRMClient() {
	}

	public void initClient(String host, int port, String username, String password) {
		initClient(host,port,username,password,true);
	}

	public void initClient(String host, int port, String username, String password,boolean authenticationNeed) {
		httpclient = HttpClientBuilder.create().build();
		targetHost = new HttpHost(host, port, "http");
		context = HttpClientContext.create();

		if (authenticationNeed) {
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(
					new AuthScope(targetHost.getHostName(), targetHost.getPort()),
					new UsernamePasswordCredentials(username,password));
			AuthCache authCache = new BasicAuthCache();
			BasicScheme basicAuth = new BasicScheme();
			authCache.put(targetHost, basicAuth);
			context.setCredentialsProvider(credsProvider);
			context.setAuthCache(authCache);
		}

	}

	public CloseableHttpResponse doPostJson(String query, String endpoint) {
		StringEntity entity;		
		CloseableHttpResponse response = null;
		httppost = new HttpPost(endpoint);
		httppost.setHeader("Content-Type", "application/json");
		httppost.setHeader("Accept-Encoding", "application/json");
		httppost.setHeader("Accept", "application/json");
		httppost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");

		try {
			entity = new StringEntity(query);
			httppost.setEntity(entity);
			try {
				response = httpclient.execute(targetHost, httppost, context);
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			}
		}catch (UnsupportedEncodingException e1) {
			LOGGER.error(e1.getMessage());
		}
		return response;
	}

	public void CloseHTTPConnection(CloseableHttpResponse response){
		try {
			response.close();
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	}

	public String[] getUserPawwsord(String token) {
		//Tolgo "Basic "
		String base64String=token.substring(6);

		String decodedString=decodeBase64(base64String);
		String[] result=decodedString.split(":");
		return result;
	}

	private String decodeBase64(String encodedString) {
		byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
		String decodedString = new String(decodedBytes);
		return decodedString;
	}
}
