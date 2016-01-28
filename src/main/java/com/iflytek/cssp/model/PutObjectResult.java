/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iflytek.cssp.model;

/**
 * @author ttsun4
 */
public class PutObjectResult {
    private String Container;
    private String ETag;
    public PutObjectResult(){
        
    }
    public void setEtag(String ETag_str)
    {
        ETag =ETag_str;
    }
    public void setContainer(String Container_name)
    {
        Container = Container_name;
    }
    public String getETag()
    {
        return ETag;
    }
    public String getContainer()
    {
        return Container;
    }

}
