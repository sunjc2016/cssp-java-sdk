/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iflytek.cssp.model;



/**
 *  
 * 
 * @author ttsun4
 */
public class ContainerACL {
	public enum AccessControlList {Private, PublicRead, PublicRead_PublicList};
    private String Container_read;
    private String Container_write;
    public ContainerACL(){
        
    }
    
    public void setRead(String read)
    {
        Container_read = read;
    }
    public void setWrite(String write)
    {
        Container_write =  write;
    }
    public String getRead()
    {
        return Container_read;
    }
    public String getWrite()
    {
        return Container_write;
    }
}
