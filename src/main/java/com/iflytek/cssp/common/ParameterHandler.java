/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iflytek.cssp.common;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.iflytek.cssp.exception.ParameterException;
import com.iflytek.cssp.model.ContainerMetadata;
import com.iflytek.cssp.model.ObjectMetadata;

/**
 *
 * @author ttsun4
 */
public class ParameterHandler {
    private static final String PARAMETER_ERROR = "PARAMETER_ERROR";
    private static final String OBJECT_META = "X-Object-Meta-";
    private static final String Container_META = "X-Container-Meta-";
    private static final String TYPE_DETECT ="X-Detect-Content-Type";
    private final String metakey_regex = "^[a-zA-Z0-9][a-zA-Z0-9]{0,15}$";//metadata:1~16 bytes
    private final String metavalue_regex = "^[a-zA-Z0-9][a-zA-Z0-9]{0,15}";
    private final String list_regex = "^[a-z0-9][a-z0-9.]{0,253}";//256
    
    public ParameterHandler()
    {
        
    }
    public boolean judge_list(String key)
    {
        boolean is_match = true;
        Pattern pattern = Pattern.compile(list_regex);  
        Matcher matcher=pattern.matcher(key); 
        if( !matcher.matches())
        {
            is_match = false;
        }
        return is_match;
    }
    
    
    public boolean Container_meta(Map<String, String> meta)
    {
    	boolean meta_is_ok = true;
    	if(meta != null)
    	{
    		Pattern pattern_key = Pattern.compile(metakey_regex);
			Pattern pattern_value = Pattern.compile(metavalue_regex);
    		for (Map.Entry<String, String> entry: meta.entrySet()) 
            {
    			if(!pattern_key.matcher(entry.getKey()).matches() || !pattern_value.matcher(entry.getValue()).matches())
    			{
    				meta_is_ok = false;
    				break;
    			}
            }
    	}
    	return meta_is_ok;
    }
    public String add_query(Map<String, String> query)
    { 
        int count =0;
        String query_String = "";
        if(query.size() > 0)
        {
            query_String = "?";
            for (Map.Entry<String, String> entry: query.entrySet()) 
            {
                ++count;
                query_String += entry.getKey() + "=" + entry.getValue();
                if(count < query.size())
                {
                    query_String += "&";
                }
            }
        }
        return query_String;
    }
    public String getdata_from_header(Map<String, String> header,String source)
    {
        String return_value = null;
        if(header != null)
        {
             for(Map.Entry<String, String> entry: header.entrySet())
                {
                    if(entry.getKey() == null ? source == null : entry.getKey().equals(source))
                    {
                        return_value = entry.getValue();
                        break;
                    }
                }
        }
        return return_value;
    }
    public Map<String, String> getMetadata_from_header(Map<String, String> header, String source)
    {
        Map<String, String> return_value = new LinkedHashMap<String, String>();
        if(header != null)
        {
            if(source.length() > 0)
            {
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    if(entry.getKey().startsWith(source))
                    {
                        return_value.put(entry.getKey().substring(source.length()), entry.getValue());
                    }
                }
            }
        }
        //System.out.print(return_value.size());
        return return_value;
    }
    public String getRange(long offset, long length)
    {
        String tmp = null;
        if(offset >= 0)
        {
            if(length >= offset )
            {
                tmp = "bytes=" + offset + "-" + length;
            }
        }
        if(offset == -1 && length == -1)
        {
            tmp = "all";
        }
        return tmp;
    }
    public boolean decide_ObjectMetadata(ObjectMetadata meta) throws ParameterException
    {
        boolean return_value = false;
        if( meta.getDetect_Type() == false)
        {
        	if(meta.getContent_Type() == null)
        	{
                if(meta.getObject_Meta() == null)
                {
                    return_value = true;
                }
//                else//此处判断是不是需要放在外面
//                {
//                    for(Map.Entry<String,String> entry:meta.getObject_Meta().entrySet())
//                    {
//                        if(entry.getKey() == null || entry.getValue() == null)
//                        {
//                            return_value = true;
//                            break;
//                        }
//                        if(!judge_meta_format(entry.getKey(), entry.getValue()))
//                        {
//                            throw new ParameterException(PARAMETER_ERROR);
//                        }
//                    }
//                }
        	}
        }
        
        if(meta.getObject_Meta() != null)
        {
        	for(Map.Entry<String, String> entry:meta.getObject_Meta().entrySet())
            {
                if(entry.getKey() == null || entry.getValue() == null)
                {
                    return_value = true;
                    break;
                }
                if(!judge_meta_format(entry.getKey(), entry.getValue()))
                {
                    throw new ParameterException(PARAMETER_ERROR);
                }
            }
        }
        //只设置delete—after设置参数小于0的时候要判断是否小于0,抛错误异常而不是NULL异常
        return return_value;
    }
    public Map<String, String> get_para_ObjectMetadata(ObjectMetadata meta) throws ParameterException
    {
        Map<String, String> query = new LinkedHashMap<String, String>();
        if(meta != null)
        {
            if(meta.getObject_Meta() != null)// set meta
            {
                for(Map.Entry<String, String> entry:meta.getObject_Meta().entrySet())
                {
                     if(entry.getKey() != null && entry.getValue()!= null && judge_meta_format(entry.getKey(), entry.getValue()))
                    {
                        query.put(OBJECT_META + entry.getKey(), entry.getValue());
                    }
                }
            }
            if(meta.getDetect_Type() != false)
            {
                query.put(TYPE_DETECT, String.valueOf(meta.getDetect_Type()));
            }
        }
        return query;
    }
    
    public boolean decide_ContainerMetadata(ContainerMetadata meta) throws ParameterException
    {
        boolean not_null = false;
        if(meta.getContainer_Meta() == null )
            {
               not_null = true;
            }
            else
            {
                for(Map.Entry<String, String> entry:meta.getContainer_Meta().entrySet())
                {
                    if(entry.getKey() == null || entry.getValue() == null)
                    {
                        not_null = true;
                        break;
                    }
                    if(!judge_meta_format(entry.getKey(), entry.getValue()))
                    {
                        throw new ParameterException(PARAMETER_ERROR);
                    }
                }
            }
       return not_null;
    }
    public Map<String, String> get_para_ContainerMetadata(ContainerMetadata meta) throws ParameterException
    {
        Map<String, String> query = new LinkedHashMap<String, String>();
        if(meta != null)
        {
            if(meta.getContainer_Meta() != null)// set meta
            {
                for(Map.Entry<String, String> entry:meta.getContainer_Meta().entrySet())
                {
                    if(entry.getKey() != null && entry.getValue() != null && judge_meta_format(entry.getKey(), entry.getValue()))
                    { 
                        query.put(Container_META + entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        return query;
    }
   public boolean judge_meta_format(String key, String value)
    {
        boolean is_match = true;
        Pattern pattern = Pattern.compile(metakey_regex);  
        Matcher matcher=pattern.matcher(key); 
        Pattern pattern_valus = Pattern.compile(metavalue_regex);  
        Matcher matcher_value = pattern_valus.matcher(value);
        if( !matcher.matches() || !matcher_value.matches())
        {
            is_match = false;
        }
        return is_match;
    }
    
    public void judge_UploadID_exsit(LinkedHashMap<String, String> UploadID_list, String name, String UploadID) throws ParameterException
    {
        if(UploadID_list.size() <=0 )
        {
             throw new ParameterException(PARAMETER_ERROR);
        }
        boolean is_exist_id = false;
        for(Map.Entry<String, String> entry: UploadID_list.entrySet())
        {
            if(entry.getKey() != null && entry.getValue() != null)
            {
            	String key = entry.getKey();
            	String value = entry.getValue();
            	if(key.equals(name) && value.equals(UploadID))
            	{
            		is_exist_id = true;
            		break;
            	}
            }
            else
            {
                 throw new ParameterException(PARAMETER_ERROR);
            }
        }
        if(is_exist_id == false)
        {
        	throw new ParameterException(PARAMETER_ERROR);
        }
    }
}
