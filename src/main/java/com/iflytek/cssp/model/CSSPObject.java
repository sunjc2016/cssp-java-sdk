/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iflytek.cssp.model;

import java.io.InputStream;

/**
 * @author ttsun4
 */
public class CSSPObject {
    private InputStream objectContent;
    private ObjectMetadata object_metadata;
    private String content_range;
    public CSSPObject(){
        
    }
    public void setContent_Range(String range)
    {
    	this.content_range = range;
    }
    
    public void setObject_Content(InputStream object_Content)
    { 
        objectContent = object_Content;
    }
    public void setObject_Meta(ObjectMetadata metadata)
    { 
    	object_metadata = metadata;
    }
    public InputStream getObject_Content()
    {
        return objectContent;
    }
    public ObjectMetadata getObject_Meta()
    {
        return object_metadata;
    }
    public String getContent_Range()
    {
    	return this.content_range;
    }
}
