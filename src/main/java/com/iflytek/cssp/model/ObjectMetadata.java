/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iflytek.cssp.model;

import java.util.HashMap;
import java.util.Map;

/**
* post
 * X-Object-Meta-Book
 * X-Object-Meta-Author
 * X-Object-Meta-Subject
 * X-Object-Meta-Century
 * @author ttsun4
 */
public class ObjectMetadata {
    private String Object_Manifest;
    private String last_modified;
    private long content_length;
    private String ETag;
    private String content_type; 
    private Map<String,String> object_meta;
    private boolean detect_content_type;

    public ObjectMetadata(){
        
    }
    public void setLast_Modified(String time)
    {
        last_modified = time;
    }
     public void setObject_Manifest(String Manifest)
    {
        Object_Manifest = Manifest;
    }

    public void setContent_Length(long ContentLength)
    {
        content_length = ContentLength;
    }
    public void setContent_Type(String type)
    {
        content_type = type;
    }
    public void setObject_Meta(Map<String,String> meta)
    {
        object_meta = new HashMap<String,String>();
        if(meta != null){
	        for(Map.Entry<String,String> entry :meta.entrySet())
	        {
	            object_meta.put(entry.getKey(),entry.getValue());
	        }
        }
    }
    public void setEtag(String etag)
    {
        ETag = etag;
    }
    public void setDetect_Type(boolean type)
    {
        detect_content_type = type;
    }

    public String getLast_Modified()
    {
        return last_modified;
    }

    public long getContent_Length()
    {
        return content_length;
    }
    public String getContent_Type()
    {
        return content_type;
    }
    public Map<String,String> getObject_Meta()
    {
        return object_meta;
    }
    public String getEtag()
    {
        return ETag;
    }
    public boolean getDetect_Type()
    {
        return detect_content_type;
    }
    public String getObject_Manifest()
    {
        return Object_Manifest;
    }
    
}
