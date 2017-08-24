/*
 * To change this license header,  choose License Headers in Project Properties.
 * To change this template file,  choose Tools | Templates
 * and open the template in the editor.
 */

package com.iflytek.cssp.SwiftAPI;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import com.iflytek.cssp.common.CSSPSignature;
import com.iflytek.cssp.common.ParameterHandler;
import com.iflytek.cssp.exception.CSSPException;

import org.apache.log4j.*;

/**
 *
 * @author ttsun4
 */
public class SwiftClient {
	static Logger logger = Logger.getLogger(SwiftClient.class.getName());
	private static final String CONTENT_TYPE ="Content-Type";
    //private final String AUTH_TOKEN = "X-Auth-Token";
    private final String DATE = "Date";
    private final String AUTH = "Authorization";
    private final String CSSP = "CSSP ";
    private final int connectionTimeout = 6000;
    //����һ��url�����ӵȴ�ʱ��, �׳��쳣ΪIOException������org.apache.http.conn.ConnectTimeoutException
    private final int soTimeout = 10000;
    //������һ��url����ȡresponse�ķ��صȴ�ʱ�䣬�׳��쳣ΪIOException������ java.net.SocketTimeoutException
    public SwiftClient(){
        
    }
    public SwiftClientResponse GetContainer(String url, boolean full_listing, 
    		String Container, Map<String, String> query, String accesskey, String secretkey) throws IOException, CSSPException
    {
        List<LinkedHashMap<String, String>> Objects_list = new ArrayList<LinkedHashMap<String, String>>();
        Map<String, String> headers = new LinkedHashMap<String,  String>();
        ParameterHandler add_query = new ParameterHandler();
        String query_string;
        query.put("format",  "json");
        query_string = add_query.add_query(query);
        HttpGet httpget = new HttpGet(url + "/"+ encode(Container) + query_string);
        
        CSSPSignature signature = new CSSPSignature("GET", null, "/"+ Container, null, null);
        signature.getDate();
        String auth = signature.getSinature();
        String signa = signature.HmacSHA1Encrypt(auth,  secretkey);
        httpget.addHeader(DATE,  signature.Date);
        httpget.addHeader(AUTH,  CSSP + accesskey + ":" + signa);
        
        //httpget.addHeader(AUTH_TOKEN,  token);
        HttpResponse response;
        BasicHttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, connectionTimeout);
        HttpConnectionParams.setSoTimeout(httpParameters, soTimeout);      
        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        response = httpClient.execute(httpget);
        Header[] Headers = response.getAllHeaders();
        for (Header Header : Headers) {
            headers.put(Header.getName(), Header.getValue());
        }
        if(isMatch_2XX(response.getStatusLine().getStatusCode()))
        {
        	HttpEntity tmpEntity = response.getEntity();
            if(tmpEntity != null)
            {
                String json_Container = EntityUtils.toString(tmpEntity);
                Objects_list = json_getObject(json_Container); 
                if(Objects_list.size() != 0)
                {
                    if(full_listing == true)
                    {
                        do
                        {
                        	query.remove("format");
                            int nmarker = Objects_list.size() - 1;
                            query.put("marker",  (Objects_list.get(nmarker).get("name") != null)?Objects_list.get(nmarker).get("name"):Objects_list.get(nmarker).get("subdir"));
                            SwiftClientResponse tmp = GetContainer(url, full_listing, Container, query, accesskey, secretkey);
                            if(tmp.Lists.size() > 0)
                            {
                                Objects_list.addAll(tmp.Lists);
                            }
                            else
                            {
                                break;
                            }
                        }while(true);
                    }
                }
            }
        }
        else 
        {
        	logger.error("[get object list: error,  StatusCode is ]" 
        			+ response.getStatusLine().getStatusCode());
            return new ExceptionHandle().ContainerExceptionHandle(response,  headers);
        }
        return new SwiftClientResponse(headers, response.getStatusLine().getStatusCode(), response.getStatusLine(), Objects_list, null);
    }
    
    public SwiftClientResponse PostContainer(String url, String Container
    		, Map<String, String> headers, String accesskey, String secretkey) throws IOException, CSSPException
    {
        HttpPost post = new HttpPost(url + "/"+ encode(Container)); 
        
        CSSPSignature signature = new CSSPSignature("POST", headers, "/"+ Container, null, null);
        signature.getDate();
        String auth = signature.getSinature();
        String signa = signature.HmacSHA1Encrypt(auth,  secretkey);
        post.addHeader(DATE,  signature.Date);
        post.addHeader(AUTH,  CSSP + accesskey + ":" + signa);
        
        //post.addHeader(AUTH_TOKEN,  token);
        post.addHeader("Content-Lenght", "0");
        for(Map.Entry<String, String> entry:headers.entrySet())
        {
            post.addHeader(entry.getKey(),  entry.getValue());
        }
        BasicHttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, connectionTimeout);
        HttpConnectionParams.setSoTimeout(httpParameters, soTimeout);
        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        HttpResponse response =  httpClient.execute(post);
        if(isMatch_2XX(response.getStatusLine().getStatusCode()))//2xx 401
        { 
            return new SwiftClientResponse(headers, response.getStatusLine().getStatusCode(), response.getStatusLine(), null, null);
        }
        else
        {
        	logger.error("[set or remove metadata for Container: error,  StatusCode is ]" 
        			+ response.getStatusLine().getStatusCode());
            return new ExceptionHandle().ContainerExceptionHandle(response,  headers);
        }
    }
    
    public SwiftClientResponse HeadContainer(String url, String Container
    		, String accesskey, String secretkey) throws IOException, CSSPException
    {
        Map<String, String> headers = new LinkedHashMap<String,  String>();
        BasicHttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters,  connectionTimeout); 
        HttpConnectionParams.setSoTimeout(httpParameters,  soTimeout);
        //default enable auto redirect
        httpParameters.setBooleanParameter(ClientPNames.HANDLE_REDIRECTS,  Boolean.TRUE);
        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        HttpHead httphead = new HttpHead(url + "/"+ encode(Container));
        
        CSSPSignature signature = new CSSPSignature("HEAD", null, "/"+ Container, null, null);
        signature.getDate();
        String auth = signature.getSinature();
        String signa = signature.HmacSHA1Encrypt(auth,  secretkey);
        httphead.addHeader(DATE,  signature.Date);
        httphead.addHeader(AUTH,  CSSP + accesskey + ":" + signa);
        
        //httphead.addHeader(AUTH_TOKEN,  token);
        HttpResponse response = httpClient.execute(httphead);
        Header[] Headers = response.getAllHeaders();
        for (Header Header : Headers) {
             headers.put(Header.getName(),  Header.getValue());
        }
        if(isMatch_2XX(response.getStatusLine().getStatusCode()))//2xx 401
        {  
            return new SwiftClientResponse(headers, response.getStatusLine().getStatusCode(), response.getStatusLine(), null, null);
        }
        else
        {
        	logger.error("[get metadata for Container: error,  StatusCode is ]" 
        			+ response.getStatusLine().getStatusCode());
            return new ExceptionHandle().ContainerExceptionHandle(response,  headers);
        }
    }
    private String gettime(String newtime)
    {
    	String test4 = newtime.substring(0,  newtime.indexOf("."));
		String test5 = test4.replaceAll("T",  " ");
		DateFormat format2= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Calendar rightNow1 = Calendar.getInstance();
		Date date3 = new Date();  
		try {
			date3 = format2.parse(test5);
		} catch (ParseException e1) {
			logger.error("[change the GMT to yyyy-MM-dd HH:mm:ss: error]" );
		}
        rightNow1.setTime(date3);
        rightNow1.add(Calendar.HOUR,  8);
        Date dt2=rightNow1.getTime();
        SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        return sdf1.format(dt2);
    }
    private List<LinkedHashMap<String, String>> json_getObject(String json)
    {
        JSONArray array_Objects = JSONArray.fromObject(json);
        List<LinkedHashMap<String, String>> objects = new ArrayList<LinkedHashMap<String, String>>();
        for (java.lang.Object array_Container : array_Objects) {
        	LinkedHashMap<String,  String> info = new LinkedHashMap<String,  String>();
            JSONObject e = (JSONObject) array_Container;
            try
            {
                info.put("hash",  e.getString("hash"));
                info.put("last_modified", gettime(e.getString("last_modified")));
                info.put("bytes",  e.getString("bytes"));
                info.put("name",  e.getString("name"));
                info.put("content_type",  e.getString("content_type"));
                info.put("subdir",  null);
                objects.add(info);
            }
            catch(Exception ex)
            {
                info.put("hash",  null);
                info.put("last_modified", null);
                info.put("bytes",  null);
                info.put("name",  null);
                info.put("content_type",  null);
                info.put("subdir",  e.getString("subdir"));
                objects.add(info);
            }
        }
        return objects;
    }
   
     public SwiftClientResponse GetObject(String url, String Container, String object_name, 
    		 Map<String, String> query, String accesskey, String secretkey) throws IOException, CSSPException
     {
    	PoolingClientConnectionManager cm = new PoolingClientConnectionManager();
    	cm.setMaxTotal(200);//�ͻ����ܲ������������   
    	cm.setDefaultMaxPerRoute(20);//ÿ������������������� 

        Map<String, String> headers = new LinkedHashMap<String,  String>();
        HttpGet httpget = new HttpGet(url + "/"+ encode(Container) + "/" + encode(object_name) );
        BasicHttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters,  connectionTimeout);
        HttpConnectionParams.setSoTimeout(httpParameters,  soTimeout);
        HttpClient httpClient = new DefaultHttpClient(cm, httpParameters);
        CSSPSignature signature = new CSSPSignature("GET", null, "/"+ Container + "/" + object_name, null, null);
        signature.getDate();
        String auth = signature.getSinature();
        String signa = signature.HmacSHA1Encrypt(auth,  secretkey);
        httpget.addHeader(DATE,  signature.Date);
        httpget.addHeader(AUTH,  CSSP + accesskey + ":" + signa);
        
        if(query !=null)
        {
        	for(Map.Entry<String, String> entry:query.entrySet())
        	{
        		httpget.addHeader(entry.getKey(),  entry.getValue());
        	}
        }
        HttpResponse response;
        response = httpClient.execute(httpget);
        Header[] Headers = response.getAllHeaders();
        for (Header Header : Headers) {
            headers.put(Header.getName(), Header.getValue());
        }
        if(isMatch_2XX(response.getStatusLine().getStatusCode()))//2xx 401
        {  
            return new SwiftClientResponse(headers, response.getStatusLine().getStatusCode(), response.getStatusLine(), null, new ContentStream(response.getEntity().getContent(), response.getEntity().getContentLength()));
        }
        else
        {
        	logger.error("[get object: error,  StatusCode is ]" 
        			+ response.getStatusLine().getStatusCode());
            return new ExceptionHandle().ObjectExceptionHandle(response,  headers);
        }
    }
    
    public SwiftClientResponse PutObject(String url, String Container, String object_name, 
    		byte[] content, long content_length, String md5sum,  String content_type, Map<String, String> object_metadata
    		, String accesskey, String secretkey) throws IOException, CSSPException
    {
    	BasicHttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters,  connectionTimeout);
        HttpConnectionParams.setSoTimeout(httpParameters,  soTimeout);
        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        Map<String, String> headers = new LinkedHashMap<String,  String>();
        String url_storage = url;
        if(Container != null )
        {
            url_storage +=  "/" + encode(Container);
        }
        if(object_name != null)
        {
            
            url_storage += "/" + encode(object_name);
        }
        HttpPut httpput = new HttpPut(url_storage);
        //httpput.addHeader(AUTH_TOKEN,  token);
        if(content != null)
        {
          //  InputStreamEntity body = new InputStreamEntity (content, content_length);
        	ByteArrayEntity body = new ByteArrayEntity (content);
            body.setChunked(false);
            httpput.setEntity(body);
        }
        for(Map.Entry<String, String> entry:object_metadata.entrySet())
        {
            httpput.addHeader(entry.getKey(),  entry.getValue());
        }
        String content_type_string = null;
        if(content_type != null) {
            httpput.addHeader(CONTENT_TYPE,  content_type);
            content_type_string = content_type;
        }
        String mD5 = null;
        if(md5sum != null) {
            httpput.setHeader(HttpHeaders.ETAG,  md5sum);
            mD5 = md5sum;
        }
        CSSPSignature signature = new CSSPSignature("PUT", object_metadata, "/"+ Container + "/" + object_name, mD5, content_type_string);
        signature.getDate();
        String auth = signature.getSinature();
        String signa = signature.HmacSHA1Encrypt(auth,  secretkey);
        httpput.addHeader(DATE,  signature.Date);
        httpput.addHeader(AUTH,  CSSP + accesskey + ":" + signa);
        HttpResponse response = httpClient.execute(httpput); 
        Header[] Headers = response.getAllHeaders();
        for (Header Header : Headers) {
            headers.put(Header.getName(), Header.getValue());
        }
        if(isMatch_2XX(response.getStatusLine().getStatusCode()))
        { 
            return new SwiftClientResponse(headers, response.getStatusLine().getStatusCode(), response.getStatusLine(), null, null);
        }
        else
        {
        	logger.error("[put object: error,  StatusCode is ]" 
        			+ response.getStatusLine().getStatusCode());
            return new ExceptionHandle().ContainerExceptionHandle(response,  headers);
        }
    	
    }
    
    public SwiftClientResponse PutFaceVerity(String url,  String Container, String object_name, 
    		InputStream content, long content_length, Map<String, String> query, String accesskey, String secretkey) throws IOException, CSSPException
    {
    	BasicHttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters,  connectionTimeout);
        HttpConnectionParams.setSoTimeout(httpParameters,  soTimeout);
        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        Map<String, String> headers = new LinkedHashMap<String,  String>();
        ParameterHandler add_query = new ParameterHandler();
        String query_string = "";
        query_string= add_query.add_query(query);
        String url_storage = url;
        if(Container != null )
        {
            url_storage +=  "/" + encode(Container);
        }
        if(object_name != null)
        {
            
            url_storage += "/" + encode(object_name) + query_string;
        }
        HttpPut httpput = new HttpPut(url_storage);
        //httpput.addHeader(AUTH_TOKEN,  token);
        
        CSSPSignature signature = new CSSPSignature("PUT", null, "/"+ Container + "/" + object_name, null, null);
        signature.getDate();
        String auth = signature.getSinature();
        String signa = signature.HmacSHA1Encrypt(auth,  secretkey);
        httpput.addHeader(DATE,  signature.Date);
        httpput.addHeader(AUTH,  CSSP + accesskey + ":" + signa);
        if(content != null)
        {
            InputStreamEntity body = new InputStreamEntity (content, content_length);
            body.setChunked(false);
            httpput.setEntity(body);
        }
        HttpResponse response = httpClient.execute(httpput); 
        Header[] Headers = response.getAllHeaders();
        for (Header Header : Headers) {
            headers.put(Header.getName(), Header.getValue());
        }
        if(isMatch_2XX(response.getStatusLine().getStatusCode()))
        { 
            return new SwiftClientResponse(headers, response.getStatusLine().getStatusCode(), response.getStatusLine(), null, null);
        }
        else
        {
        	logger.error("[put object: error,  StatusCode is ]" 
        			+ response.getStatusLine().getStatusCode());
            return new ExceptionHandle().ContainerExceptionHandle(response,  headers);
        }
    	
    }
    public SwiftClientResponse PutFaceVerity_test(String url,  String Container, String object_name, 
    		InputStream content, long content_length, Map<String, String> query, String accesskey, String secretkey) throws IOException, CSSPException
    {
    	BasicHttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters,  connectionTimeout);
        HttpConnectionParams.setSoTimeout(httpParameters,  soTimeout);
        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        Map<String, String> headers = new LinkedHashMap<String,  String>();
        ParameterHandler add_query = new ParameterHandler();
        String query_string = "";
        query_string= add_query.add_query(query);
        String url_storage = url;
        if(Container != null )
        {
            url_storage +=  "/" + encode(Container);
        }
        if(object_name != null)
        {
            
            url_storage += "/" + encode(object_name) + query_string;
        }
        HttpPut httpput = new HttpPut(url_storage);
        //httpput.addHeader(AUTH_TOKEN,  token);
        
        CSSPSignature signature = new CSSPSignature("PUT", null, "/"+ Container + "/" + object_name, null, null);
        signature.getDate();
        String auth = signature.getSinature();
        String signa = signature.HmacSHA1Encrypt(auth,  secretkey);
        httpput.addHeader(DATE,  signature.Date);
        httpput.addHeader(AUTH,  CSSP + accesskey + ":" + signa);
        if(content != null)
        {
            InputStreamEntity body = new InputStreamEntity (content, content_length);
            body.setChunked(false);
            httpput.setEntity(body);
        }
        HttpResponse response = httpClient.execute(httpput); 
        Header[] Headers = response.getAllHeaders();
        for (Header Header : Headers) {
            headers.put(Header.getName(), Header.getValue());
        }
        if(isMatch_2XX(response.getStatusLine().getStatusCode()))
        { 
            return new SwiftClientResponse(headers, response.getStatusLine().getStatusCode(), response.getStatusLine(), null, null);
        }
        else
        {
        	logger.error("[put object: error,  StatusCode is ]" 
        			+ response.getStatusLine().getStatusCode());
            return new ExceptionHandle().ContainerExceptionHandle(response,  headers);
        }
    	
    }
    public SwiftClientResponse DeleteObject(String url,  String Container, 
    		String object_name, String accesskey, String secretkey) throws IOException, CSSPException
    {
        Map<String, String> headers = new LinkedHashMap<String,  String>();
        BasicHttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters,  connectionTimeout);       
        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        HttpDelete delete = new HttpDelete(url + "/"+ encode(Container) + "/" + encode(object_name) );
        //delete.addHeader(AUTH_TOKEN,  token);
        CSSPSignature signature = new CSSPSignature("DELETE", null, "/"+ Container + "/" + object_name, null, null);
        signature.getDate();
        String auth = signature.getSinature();
        String signa = signature.HmacSHA1Encrypt(auth,  secretkey);
        delete.addHeader(DATE,  signature.Date);
        delete.addHeader(AUTH,  CSSP + accesskey + ":" + signa);
        
        HttpResponse response = httpClient.execute(delete);
        Header[] Headers = response.getAllHeaders();
        for (Header Header : Headers) {
            headers.put(Header.getName(), Header.getValue());
        }
        if(isMatch_2XX(response.getStatusLine().getStatusCode()))//2xx 401
        { 
            return new SwiftClientResponse(headers, response.getStatusLine().getStatusCode(), response.getStatusLine(), null, null);
        }
        else
        {
        	logger.error("[delete object: error,  StatusCode is ]" 
        			+ response.getStatusLine().getStatusCode());
            return new ExceptionHandle().ObjectExceptionHandle(response,  headers);
        }
    }
     public SwiftClientResponse HeadObject(String url, String Container, String object_name
    		 , String accesskey, String secretkey) throws IOException, CSSPException
    {
        Map<String, String> headers = new LinkedHashMap<String,  String>();
        BasicHttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters,  connectionTimeout);       
        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        HttpHead httphead = new HttpHead(url + "/"+ encode(Container) + "/" + encode(object_name) );
        
        CSSPSignature signature = new CSSPSignature("HEAD", null, "/"+ Container + "/" + object_name, null, null);
        signature.getDate();
        String auth = signature.getSinature();
        String signa = signature.HmacSHA1Encrypt(auth,  secretkey);
        
        httphead.addHeader(DATE,  signature.Date);
        httphead.addHeader(AUTH,  CSSP + accesskey + ":" + signa);
        HttpResponse response = httpClient.execute(httphead);
        Header[] Headers = response.getAllHeaders();
        for (Header Header : Headers) {
            headers.put(Header.getName(), Header.getValue());
        }
        if(isMatch_2XX(response.getStatusLine().getStatusCode()))//2xx 401
        { 
            return new SwiftClientResponse(headers, response.getStatusLine().getStatusCode(), response.getStatusLine(), null, null);
        }
        else
        {
        	logger.error("[get metadata for object: error,  StatusCode is ]" 
        			+ response.getStatusLine().getStatusCode());
            return new ExceptionHandle().ObjectExceptionHandle(response,  headers);
        }
    }
    
    public SwiftClientResponse PostObject(String url, String Container, String object_name, 
    		Map<String, String> headers, String content_type, String accesskey, String secretkey) throws IOException, CSSPException
    {
        HttpPost post = new HttpPost(url + "/"+ encode(Container) + "/" + encode(object_name) ); 
        //post.addHeader(AUTH_TOKEN,  token);
        for(Map.Entry<String, String> entry:headers.entrySet())
        {
            post.addHeader(entry.getKey(),  entry.getValue());
        }
        String content_type_string = null;
        if(content_type != null) {
        	post.addHeader(CONTENT_TYPE,  content_type);
            content_type_string = content_type;
        }
        CSSPSignature signature = new CSSPSignature("POST", headers, "/"+ Container + "/" + object_name, null, content_type_string);
        signature.getDate();
        String auth = signature.getSinature();
        String signa = signature.HmacSHA1Encrypt(auth,  secretkey);
        post.addHeader(DATE,  signature.Date);
        post.addHeader(AUTH,  CSSP + accesskey + ":" + signa);
        
        BasicHttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters,  connectionTimeout);       
        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        HttpResponse response =  httpClient.execute(post);
        if(isMatch_2XX(response.getStatusLine().getStatusCode()))//2xx 401
        { 
            return new SwiftClientResponse(headers, response.getStatusLine().getStatusCode(), response.getStatusLine(), null, null);
        }
        else
        {
        	logger.error("[set or remove metadata for object: error,  StatusCode is ]" 
        			+ response.getStatusLine().getStatusCode());
            return new ExceptionHandle().ObjectExceptionHandle(response,  headers);
        }
    }
   
    public SwiftClientResponse CopyObject(String url,  String source_Container, 
    		String source_object, String dest_Container, String dest_object, String accesskey, String secretkey) throws IOException, CSSPException
    {
        BasicHttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters,  connectionTimeout);       
        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        Map<String, String> headers = new LinkedHashMap<String,  String>();
        HttpPut httpput = new HttpPut(url + "/"+ encode(dest_Container) + "/" + encode(dest_object) );
        //httpput.addHeader(AUTH_TOKEN,  token);
        httpput.setHeader("X-Copy-From",  "/"+ encode(source_Container) + "/" + encode(source_object));
       
        CSSPSignature signature = new CSSPSignature("PUT", null, "/"+ dest_Container + "/" + dest_object, null, null);
        signature.getDate();
        String auth = signature.getSinature();
        String signa = signature.HmacSHA1Encrypt(auth,  secretkey);
        httpput.addHeader(DATE,  signature.Date);
        httpput.addHeader(AUTH,  CSSP + accesskey + ":" + signa);
        
        HttpResponse response = httpClient.execute(httpput); 
        Header[] Headers = response.getAllHeaders();
        for (Header Header : Headers) {
            headers.put(Header.getName(), Header.getValue());
        }
        if(isMatch_2XX(response.getStatusLine().getStatusCode()))//2xx 401
        { 
            return new SwiftClientResponse(headers, response.getStatusLine().getStatusCode(), response.getStatusLine(), null, null);
        }
        else
        {
        	logger.error("[copy object: error,  StatusCode is ]" 
        			+ response.getStatusLine().getStatusCode());
            return new ExceptionHandle().ObjectExceptionHandle(response,  headers);
        }
    }
    /**
     * Encode any unicode characters that will cause us problems.
     *
     * @param name URI to encode
     * @return The string encoded for a URI
     */
    private static String encode(String name) {
        return encode(name,  false);
    }
    private static String encode(String object,  boolean preserveslashes) {
        URLCodec codec = new URLCodec();
        try {
            final String encoded = codec.encode(object).replaceAll("\\+",  "%20");
            if(preserveslashes) {
                return encoded.replaceAll("%2F",  "/");
            }
            return encoded;
        }
        catch(EncoderException ee) {
            return object;
        }
    }
    private boolean isMatch_2XX(int status)
    {
        return status == HttpStatus.SC_NO_CONTENT || status == HttpStatus.SC_OK
                || status == HttpStatus.SC_CREATED|| status == HttpStatus.SC_ACCEPTED
                || status == HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION|| status == HttpStatus.SC_RESET_CONTENT
                || status == HttpStatus.SC_PARTIAL_CONTENT|| status == HttpStatus.SC_MULTI_STATUS;
    }
}
