/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iflytek.cssp.model;

/**
 * CopyObjectResult:ETag,X-Copied-From,Last-Modified
 * @author ttsun4
 */
public class CopyObjectResult {
    
    private String copied_from;
    
    private String Container;
    private String ETag;
    private String content_type;
    public CopyObjectResult(){
        
    }
    public void setCopied_From(String from_Container)
    {
        copied_from = from_Container;
    }
    public void setEtag(String ETag_str)
    {
        ETag =ETag_str;
    }
    public void setContainer(String Container_name)
    {
        Container = Container_name;
    }
    
    public void setType(String type)
    {
        content_type = type;
    }
    
    public String getCopied_From()
    {
        return copied_from;
    }
    public String getETag()
    {
        return ETag;
    }
    public String getContainer()
    {
        return Container;
    }
    
    public String getType()
    {
        return content_type;
    }

}
