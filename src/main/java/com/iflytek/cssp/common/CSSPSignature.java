package com.iflytek.cssp.common;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;



public class CSSPSignature {
	private static final String ENCODING = "UTF-8";
	private static String OBJECT_META = "x-object-meta-";
	public String Operate_Type;
	public String Date;
	public Map<String,String> meta;
	public TreeMap<String,String> meta_sort = new TreeMap<String,String>();
	public String CanonicalizedResource;
	public String CONTENT_MD5;
	public String CONTENT_TYPE;
	public String CanonicalizedCSSPHeaders;
	
	public CSSPSignature(String type,Map<String, String> meta,String CanonicalizedResource,
			String CONTENT_MD5,String CONTENT_TYPE){
		this.Operate_Type = type;
		this.meta = meta;
		this.CanonicalizedResource = CanonicalizedResource;
		this.CONTENT_MD5 = CONTENT_MD5;
		this.CONTENT_TYPE = CONTENT_TYPE;
	}
	public void SetDate(String date)
	{
		this.Date = date;
	} 
	public void getDate(){
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);  
	    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date d = new Date();
		this.Date = sdf.format(d);
	}
	
	public void setHeaders()
	{
		if(this.meta !=null){
			for(Map.Entry<String,String>entry: this.meta.entrySet())
			{
				String key = entry.getKey();
				String key_lower = key.toLowerCase();
				if(key_lower.startsWith(OBJECT_META)){
				String test2 = entry.getValue();
				this.meta_sort.put(key_lower,test2);
				}
			}
			if(this.meta_sort != null){
				CanonicalizedCSSPHeaders = "";
				for(Map.Entry<String, String>entry: this.meta_sort.entrySet())
				{
					if(CanonicalizedCSSPHeaders =="")
					{
						CanonicalizedCSSPHeaders += entry.getKey() + ":" + entry.getValue();
					}
					else
					{
						CanonicalizedCSSPHeaders += "\n" + entry.getKey() + ":" + entry.getValue();
					}
				}
			}
		}
	}
	
	public String getSinature()
	{
		String value = "";
		if(this.Operate_Type != null)
		{
			value += this.Operate_Type + "\n";
		}
		
		if(this.CONTENT_MD5 != null)
		{
			value += this.CONTENT_MD5 + "\n";
		}
		else
		{
			value += "\n";
		}
		
		if(this.CONTENT_TYPE != null)
		{
			value += this.CONTENT_TYPE + "\n";
		}
		else
		{
			value += "\n";
		}
		if(this.Date != null)
		{
			value += this.Date + "\n";
		}
		setHeaders();
		if(this.CanonicalizedCSSPHeaders != null && this.CanonicalizedCSSPHeaders != "")
		{
			value += this.CanonicalizedCSSPHeaders + "\n";
		}
//		else
//		{
//			value += "\n";
//		}
		if(this.CanonicalizedResource != null)
		{
			value += this.CanonicalizedResource;
		}
		//return encode_utf8(value);
		return value;
	}
	
	/** 
     * ʹ�� HMAC-SHA1 ǩ�������Զ�encryptText����ǩ�� 
     * @param encryptText ��ǩ�����ַ��� 
     * @param encryptKey  ��Կ 
     * @return 
     * @throws Exception 
     */  
    public String HmacSHA1Encrypt(String encryptText, String encryptKey)  
    {         
    	byte[] rawHmac = null;
    	try{
	    	//byte[] data=encryptKey.getBytes(ENCODING);
	    	byte[] data=encryptKey.getBytes();
	    	//���ݸ������ֽ����鹹��һ����Կ,�ڶ�����ָ��һ����Կ�㷨������
	    	SecretKeySpec secretKey = new SecretKeySpec(data, "HmacSHA1"); 
	        //����һ��ָ�� Mac �㷨 �� Mac ����
	        Mac mac = Mac.getInstance("HmacSHA1"); 
	        //�ø�����Կ��ʼ�� Mac ����
	        mac.init(secretKey);  
	        byte[] text = encryptText.getBytes(ENCODING);  
	        //byte[] text = encryptText.getBytes();
	        //��� Mac ���� 
	        rawHmac = mac.doFinal(text); 
    	} catch (InvalidKeyException e) {  
    		 System.out.println(e);
    	} catch (NoSuchAlgorithmException ignore) {  
    		System.out.println(ignore);
    	}
    	catch(UnsupportedEncodingException e)
    	{
    		System.out.println(e.getMessage());
    	}
        BASE64Encoder encoder = new BASE64Encoder();
        String oauth = encoder.encode(rawHmac);    
        return oauth;
    } 
}
