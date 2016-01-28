/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iflytek.cssp.model;

import java.util.HashMap;
import java.util.Map;

/**
* 
 
 * @author ttsun4
 */
public class ContainerMetadata {
	//public enum locationlist {hf,bj,gz};
    private String num_object;//X-Container-Object-Count
    private String sum_bytes;//X-Container-Bytes-Used
    private String content_type;
    //private String location;
    private Map<String, String> Container_meta; //= new HashMap<>();
    private Map<String, String> remove_Container_meta; //= new HashMap<>();
    
    public ContainerMetadata(){
    }
    
    
    public void setNum_Object(String sum)
    {
        num_object = sum;
    }
    public void setSum_Bytes(String sum)
    {
        sum_bytes = sum;
    }
    public void setContent_Type(String type)
    {
        content_type = type;
    }
    public void setContainer_Meta(Map<String, String> meta)
    {
        Container_meta =  new HashMap<String, String>();
        if(meta != null){
	        for(Map.Entry<String, String> entry :meta.entrySet())
	        {
	            Container_meta.put(entry.getKey(), entry.getValue());
	        }
        }
    }
    public void setRemove_Container_Meta(Map<String, String> meta)
    {
        remove_Container_meta = new HashMap<String, String>();
        if(meta != null){
	        for(Map.Entry<String,String> entry :meta.entrySet())
	        {
	            remove_Container_meta.put(entry.getKey(), entry.getValue());
	        }
        }
    }
    
    public String getNum_Object()
    {
        return num_object;
    }
    public String getSum_Bytes()
    {
        return sum_bytes;
    }
    public String getContent_Type()
    {
        return content_type;
    }
    public Map<String, String> getContainer_Meta()
    {
        return Container_meta;
    }
    public Map<String, String> getContainer_Remove_Meta()
    {
        return remove_Container_meta;
    }
}
