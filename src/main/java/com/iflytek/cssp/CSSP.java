package com.iflytek.cssp;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.iflytek.cssp.exception.CSSPException;
import com.iflytek.cssp.model.ContainerACL;
import com.iflytek.cssp.model.ContainerMetadata;
import com.iflytek.cssp.model.CopyObjectResult;
import com.iflytek.cssp.model.CSSPObject;
import com.iflytek.cssp.model.FaceVerificationRequest;
import com.iflytek.cssp.model.FaceVerificationResult;
import com.iflytek.cssp.model.InitiateMultipartUploadResult;
import com.iflytek.cssp.model.ObjectList;
import com.iflytek.cssp.model.ObjectMetadata;
import com.iflytek.cssp.model.PutObjectResult;
import com.iflytek.cssp.model.ContainerACL.AccessControlList;
/**
 * 
 * @author ttsun4
 */
public interface CSSP {
	
    public void LogoutClient();
    /**
     * init for CSSP 
     * @param accesskeyID: accesskeyID for your account
     * @param accesskeySecret: accesskeySecret for your account
     * @param url: url for your storage
     * @throws CSSPException
     */
    public void Client(String accesskeyID, String accesskeySecret, String url) throws CSSPException;
 
    public boolean isContainerExist()throws CSSPException, IOException;
    
    public boolean setContainerMetadata(ContainerMetadata meta)throws CSSPException,IOException;
    
    public ContainerMetadata getContainerMetadata() throws CSSPException, IOException;

    public boolean removeContainerMetadata(ContainerMetadata meta) throws CSSPException,IOException;
    
    public boolean setContainerACLReferList(String whitelist, String blacklist)throws CSSPException, IOException;

    public boolean setContainerACL(AccessControlList acl)throws CSSPException, IOException;

    public ContainerACL getContainerACL()throws CSSPException,IOException;
    
    public PutObjectResult putObject(String objectname, InputStream content, ObjectMetadata meta) throws CSSPException, IOException; 
    
    public PutObjectResult putObject(String objectname, byte[] content, ObjectMetadata meta) throws CSSPException, IOException; 
   
    public PutObjectResult putObject(String objectname, InputStream content, String MD5, ObjectMetadata meta)throws CSSPException, IOException;
    
    public PutObjectResult putObject(String objectname, byte[] content, String MD5, ObjectMetadata meta)throws CSSPException, IOException;
    public List<ObjectList> listObjects() throws CSSPException,  IOException;
    public List<ObjectList> listObjects(int limit) throws CSSPException,  IOException;
    public List<ObjectList> listObjects(int limit, String marker)throws CSSPException, IOException;
    public List<ObjectList> listObjects(String prefix) throws CSSPException,  IOException;
    public List<ObjectList> listObjects(String prefix, char delimiter)throws CSSPException, IOException;
    public List<ObjectList> listObjects(int limit, String prefix, String marker)throws CSSPException, IOException;
    public List<ObjectList> listObjects(String prefix, char delimiter, int limit)throws CSSPException, IOException;
    
    public CSSPObject getObject(String objectname) throws CSSPException, IOException;
    public CSSPObject getObject(String objectname,long start_byte, long end_byte)throws CSSPException, IOException;

    public boolean isObjectExist(String objectname)throws CSSPException, IOException;
    
    public boolean deleteObject(String objectname)throws CSSPException, IOException;
  
    public CopyObjectResult copyObject(String srcContainer, String srcobject, String desobject)throws CSSPException, IOException;
    
    public boolean setObjectMetadata(String objectname, ObjectMetadata meta)throws CSSPException, IOException;
    
    public ObjectMetadata getObjectMetadata(String objectname) throws CSSPException, IOException;

    public InitiateMultipartUploadResult initiateMultipartUploadRequest(String objectname)throws CSSPException, IOException;
   
    public PutObjectResult uploadPart(String objectname, InputStream content, long content_length, String MD5, String UploadID, int partnumber)throws CSSPException, IOException;
   
    public PutObjectResult uploadPart(String objectname, byte[] content, long content_length, String MD5, String UploadID, int partnumber)throws CSSPException, IOException;

    public List<ObjectList> listMultipartUpload(String objectname, String UploadID)throws CSSPException, IOException;

    public boolean abortMultipartUpload(String objectname, String UploadID)throws CSSPException,IOException;
  
    public PutObjectResult completeMultipartUpload(String objectname, String UploadID)throws CSSPException, IOException;
    public void set_UploadID(String Containername, String objectname, String UploadID) throws CSSPException;
    
    public FaceVerificationResult faceVerificationOperate(String objectname, FaceVerificationRequest request)throws  CSSPException,IOException;
}

