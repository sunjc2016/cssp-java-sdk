/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iflytek.cssp.model;

/**
 * @author ttsun4
 */
public class ObjectList {
   private String MD5;
   private String last_modified;
   private String bytes;
   private String object_name;
   private String content_type;
   private String subdir;
   public ObjectList(String MD5, String last_modified, String bytes, String object_name, 
		   String content_type, String subdir)
   {
       this.MD5 = MD5;
       this.bytes = bytes;
       this.content_type = content_type;
       this.object_name = object_name;
       this.last_modified = last_modified;
       this.subdir = subdir;
   }
   public String getMD5()
   {
       return this.MD5;
   }
   public String getLast_Modified()
   {
       return this.last_modified;
   }
   public String getObject_name()
   {
       return this.object_name;
   }
   public String getContent_Type()
   {
       return this.content_type;
   }
   public String getBytes()
   {
       return this.bytes;
   }
   public String getSubdir()
   {
       return this.subdir;
   }
}
