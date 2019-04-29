package cn.msuno.commons.http;

import cn.msuno.commons.Constants;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * HttpClient工具类
 *
 * @author msuno
 * @version 1.0-SNAPSHOT
 * @since 19
 **/
public class HttpClientUtil {
	
	private static Logger log = LoggerFactory.getLogger(HttpClientUtil.class);

	/**
	 * Http GET请求
	 *
	 * @author moshunwei
	 * @version 1.0-SNAPSHOT
	 * @param url 请求地址
	 * @param params 请求参数key-value
	 **/
	public static JSONObject doGet(String url, Map<String,String> params) throws HttpClientException{
		CloseableHttpClient httpClient = HttpClients.createDefault();
		String result = null;
		CloseableHttpResponse response = null;
		try {
			URIBuilder builder = new URIBuilder(url);
			if(params != null && params.size() > 0){
				for(Entry<String, String> entry : params.entrySet()){
					builder.addParameter(entry.getKey(),entry.getValue());
				}
			}
			URI uri = builder.build();
			HttpGet httpGet = new HttpGet(uri);
			response = httpClient.execute(httpGet);

			if(response.getStatusLine().getStatusCode() == Constants.HTTPSTATUSCODE){
				result = EntityUtils.toString(response.getEntity(),Constants.DEFAULTCHARTSET);
			}
		} catch (URISyntaxException e) {
			throw new HttpClientException(e.getMessage(),e);
		} catch (IOException e) {
			throw new HttpClientException(e.getMessage(),e);
		} finally {
			closeHttpRequest(response, httpClient);
		}
		return toJson(result);
	}

	/**
	 * Http POST请求
	 *
	 * @author moshunwei
	 * @version 1.0-SNAPSHOT
	 * @param url 请求地址
	 * @param params 请求参数key-value
	 **/
	public static JSONObject doPost(String url, Map<String, String> params) throws HttpClientException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String result;
        try {
            HttpPost httpPost = new HttpPost(url);
            if (params != null) {
                List<NameValuePair> paramList = new ArrayList<NameValuePair>();
                for (String key : params.keySet()) {
                    paramList.add(new BasicNameValuePair(key, params.get(key)));
                }
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList);
                httpPost.setEntity(entity);
            }
            response = httpClient.execute(httpPost);
            result = EntityUtils.toString(response.getEntity(), Constants.DEFAULTCHARTSET);
        } catch (Exception e) {
			throw new HttpClientException(e.getMessage(),e);
        } finally {
			closeHttpRequest(response, httpClient);
        }
		return toJson(result);
    }

	/**
	 * Http POST请求
	 *
	 * @author moshunwei
	 * @version 1.0-SNAPSHOT
	 * @param url 请求地址
	 * @param json 请求参数,JSON请求
	 **/
	public static JSONObject doPostJSON(String url, String json) throws HttpClientException{
		if(json == null)
			return HttpClientUtil.doPost(url);
		CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String result;
        try {
        	HttpPost httpPost = new HttpPost(url);
        	StringEntity entity = new StringEntity(json,ContentType.APPLICATION_JSON);
        	httpPost.setEntity(entity);
			response = httpClient.execute(httpPost);
			result = EntityUtils.toString(response.getEntity(),Constants.DEFAULTCHARTSET);
		} catch ( Exception e) {
			throw new HttpClientException(e.getMessage(),e);
		} finally {
			closeHttpRequest(response, httpClient);
        }
       return toJson(result);
	}

	public static JSONObject toJson(String result) throws HttpClientException{
		JSONObject obj;
		try{
			obj = JSONObject.parseObject(result);
		}catch (JSONException e) {
			throw new HttpClientException(e.getMessage(),e);
		}
		return obj;
	}

	public static void closeHttpRequest(CloseableHttpResponse response, CloseableHttpClient httpClient) throws HttpClientException {
		try {
			if(response != null)
				response.close();
			httpClient.close();
		} catch (IOException e) {
			throw new HttpClientException(e.getMessage(),e);
		}
	}
	
	public static JSONObject doGet(String url){
		try {
			return HttpClientUtil.doGet(url, null);
		} catch (HttpClientException e){
			log.error(e.getMessage(),e);
			return null;
		}
	}
	
	public static JSONObject doPost(String url){
		try {
			return HttpClientUtil.doPost(url, null);
		} catch (HttpClientException e) {
			log.error(e.getMessage(),e);
			return null;
		}
	}
	
	public static JSONObject doPostJSON(String url, JSONObject json){
		try {
			return HttpClientUtil.doPostJSON(url, json.toString());
		} catch (HttpClientException e) {
			log.error(e.getMessage(),e);
			return null;
		}
	}
}
