package it.almawave.gateway.crm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.almawave.gateway.bean.GatewayResponse;
import it.almawave.gateway.bean.Tuple;
import it.almawave.gateway.configuration.PropertiesBean;
import it.almawave.gateway.crm.bean.StartClassficationVOOut;
import it.almawave.gateway.db.bean.CRMRequestBean;
@Stateless
@LocalBean
public class CRMClient{
	@EJB
	PropertiesBean propertiesBean;
	
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

	public GatewayResponse startClassification() throws HttpResponseException, IOException {
		GatewayResponse gr=new GatewayResponse();
		String testo = "abrasioni su piano rotolamento corda alt dal chilometro 206+470 206+570";
		
		CRMRequestBean bean = new CRMRequestBean();
		List<String> classificationLogicList = new ArrayList<String>();
		//TODO: comporre il classificationLogicList
		classificationLogicList.add("Visita Al Binario a Piedi");
		bean.setClassificationLogicList(classificationLogicList);
		bean.setTextMessage(testo);
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonString = objectMapper.writeValueAsString(bean);

		CloseableHttpResponse crmResponse = doPostJson(jsonString, propertiesBean.getCrmClassificationEndPoint());
		String responseString=new BasicResponseHandler().handleResponse(crmResponse);
		ObjectMapper om=new ObjectMapper();
		StartClassficationVOOut startClssificationObject=om.readValue(responseString, StartClassficationVOOut.class);

		Map<String,Object> addProp=startClssificationObject.getAdditionalProperties();
		Iterator<String> keyIterator=addProp.keySet().iterator();
		while(keyIterator.hasNext()) {
			System.out.println("-----------------"+keyIterator.next());
		}

		ArrayList<LinkedHashMap<String,Object>> tupleScores=(ArrayList<LinkedHashMap<String,Object>>)addProp.get("tupleScores");
		
        
        List<Tuple> tupleList=new ArrayList<Tuple>();
        
		tupleScores.forEach(item->{
			Tuple tuple=new Tuple();
			tuple.setValue((String)item.get("label"));
			tuple.setRank((Integer)item.get("rankPosition"));
			System.out.println(item.get("label"));
			System.out.println(item.get("rankPosition"));
			tupleList.add(tuple);
		}
				);
		
		String plainText=(String)addProp.get("plainText");
		System.out.println(plainText);
		gr.setTuples(tupleList);
		gr.setPlainText(plainText);

		return gr;
	}

	public String[] getUserPawwsord(String token) {
		//Tolgo "Basic "
		String base64String=token.substring(6);

		String decodedString=decodeBase64(base64String);
		String[] result=decodedString.split(":");
		return result;
	}

	public String decodeBase64(String encodedString) {
		byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
		String decodedString = new String(decodedBytes);
		return decodedString;
	}
	
	public static void main(String[] args) {
		
		CRMClient client =  new CRMClient();
		System.out.println(client.decodeBase64("YWJveDphYjB4"));
		
	}
}
