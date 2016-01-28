package com.iflytek.cssp.common;



public class Config {

    private final String Container_regex = "^[a-z0-9][a-z0-9]{0,15}$";//1-16
    private final String object_regex = "^[a-zA-Z0-9_-\\u4e00-\\u9fa5]{0,255}$";
    
    public  Config() {
    
    }
    public String getContainer_regex()
    {
        return Container_regex;
    }
    public String getObject_regex()
    {
        return object_regex;
    }
    
    public boolean match_object(String objectname)
    {
        boolean is_object = true;
        if(objectname.length() < 0 || objectname.length() > 256 || objectname.trim().isEmpty())
        {
            is_object = false;
        }
        if(objectname.contains("*"))
        {
            is_object = false;  
        }
        if(objectname.contains("?"))
        {
            is_object = false;
        }
//        if(objectname.contains("\""))
//        {
//            is_object = false;  
//        }
//        if(objectname.contains("\\"))
//        {
//            is_object = false;  
//        }
//        if(objectname.contains("/"))
//        {
//            is_object = false;  
//        }
//        if(objectname.contains(":"))
//        {
//            is_object = false;  
//        }
//        if(objectname.contains("<"))
//        {
//            is_object = false;  
//        }
//        if(objectname.contains(">"))
//        {
//            is_object = false;  
//        }
//        if(objectname.contains("|"))
//        {
//            is_object = false;  
//        }
        return is_object;
    }
}
