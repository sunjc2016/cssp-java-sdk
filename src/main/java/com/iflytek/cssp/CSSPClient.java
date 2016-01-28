package com.iflytek.cssp;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.log4j.Logger;

import com.iflytek.cssp.SwiftAPI.SwiftClient;
import com.iflytek.cssp.SwiftAPI.SwiftClientResponse;
import com.iflytek.cssp.common.Config;
import com.iflytek.cssp.common.ParameterHandler;
import com.iflytek.cssp.exception.ACLException;
import com.iflytek.cssp.exception.CSSPException;
import com.iflytek.cssp.exception.NotLoginException;
import com.iflytek.cssp.exception.ParameterException;
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
import com.iflytek.cssp.model.ContainerACL.*;

/**
 * @author ttsun4
 */
public class CSSPClient implements CSSP {
	static Logger logger = Logger.getLogger(CSSPClient.class.getName());
	private static final String CONTENT_TYPE ="Content-Type";
	private static int ENUM_LIMIT_NUM  = 10000;
	private static String PRIVATE_REFER;
	private static String NOTLOGINERROR;
	private static String PARAMETER_ERROR;
	private static String PARAMETER_NULL;
	private static String PARAMETER_TOO_LARGE;
	//private static long LENGTH_500M = 524288000L;
	private static String Container_OBJECT_COUNT;
	private static String Container_BYTES_USED ;
	private static String MANIFEST_HEADER;
	private static String REMOVE_Container_META;
	private static String OBJECT_META;
	private static String Container_META;
	private static String Container_READ;
	private static String PUBLIC_READ;
	private LinkedHashMap<String, String> UploadID_list = new LinkedHashMap<String, String>();
	private  String Access_URL;
	private String AccessKey;
	private String SecretKey;
	private String ContainerName;
	public CSSPClient() {
		PRIVATE_REFER = "PRIVATE_REFER";
		NOTLOGINERROR = "NOT_LOGIN";
		PARAMETER_ERROR = "PARAMETER_ERROR";
		PARAMETER_NULL = "PARAMETER_NULL";
		PARAMETER_TOO_LARGE = "LENGTH_LARGEER_500M";
		Container_OBJECT_COUNT = "X-Container-Object-Count";
		Container_BYTES_USED = "X-Container-Bytes-Used";
		MANIFEST_HEADER = "X-Object-Manifest";
		REMOVE_Container_META = "X-Remove-Container-Meta-";
		OBJECT_META = "X-Object-Meta-";
		Container_META = "X-Container-Meta-";
		Container_READ = "X-Container-Read";
		PUBLIC_READ = ".r:*,.rlistings";
	}

	@Override
	public void LogoutClient() {
		this.UploadID_list = new LinkedHashMap<String, String>();
		this.Access_URL = null;
		this.AccessKey = null;
		this.SecretKey = null;
		this.ContainerName = null;
		logger.info("The CSSPClient logout");
	}

	@Override
	public void Client(String accesskeyID, String accesskeySecret, String url) throws CSSPException {
		if (accesskeyID == null || accesskeySecret == null  || url == null
				|| url.trim().isEmpty() || accesskeyID.trim().isEmpty()
				||accesskeySecret.trim().isEmpty()) {
			throw new ParameterException(PARAMETER_NULL);
		}
		String tmp_url = "";
		if(url.startsWith("http://"))
		{
			tmp_url = url.substring(7);
		}
		else{
			tmp_url = url;
		}
		if(tmp_url.contains("/") && tmp_url.lastIndexOf("/") != tmp_url.length())
		{
			this.Access_URL = "http://" + tmp_url.substring(0, tmp_url.lastIndexOf("/"));
			this.ContainerName = tmp_url.substring(tmp_url.lastIndexOf("/") +1);
		}
		else {
			throw new ParameterException(PARAMETER_ERROR);
		}
		this.AccessKey = accesskeyID;
		this.SecretKey = accesskeySecret;
		logger.info("The CSSPClient init");
	}
	@Override
	public boolean isContainerExist() throws CSSPException,
			IOException {
		judge_is_login();
		SwiftClient swiftclient = new SwiftClient();
		SwiftClientResponse headContainer;
		headContainer = swiftclient.HeadContainer(this.Access_URL,
				this.ContainerName,this.AccessKey,this.SecretKey);
		return true;
	}
	
	@Override
	public boolean setContainerMetadata(ContainerMetadata meta)
			throws CSSPException, IOException {
		judge_is_login();
		ParameterHandler judge_Container_meta = new ParameterHandler();
		if (meta == null || judge_Container_meta.decide_ContainerMetadata(meta)) {
			throw new ParameterException(PARAMETER_NULL);
		}
		Map<String, String> query = new LinkedHashMap<String, String>();
		query = judge_Container_meta.get_para_ContainerMetadata(meta);
		if (query.size() <= 0){
			throw new ParameterException(PARAMETER_NULL);
		}
		SwiftClient swiftclient = new SwiftClient();
		SwiftClientResponse postContainer;
		postContainer = swiftclient.PostContainer(this.Access_URL,
				this.ContainerName, query,this.AccessKey,this.SecretKey);
		return true;
	}

	
	@Override
	public ContainerMetadata getContainerMetadata()throws CSSPException, IOException {
		judge_is_login();
		ContainerMetadata Container_meta = new ContainerMetadata();
		SwiftClient swiftclient = new SwiftClient();
		SwiftClientResponse headContainer;
		headContainer = swiftclient.HeadContainer(this.Access_URL,
				this.ContainerName, this.AccessKey, this.SecretKey);
		ParameterHandler Container_meta_handler = new ParameterHandler();
		String type = Container_meta_handler.getdata_from_header(headContainer.Headers,
				CONTENT_TYPE);
		if (type != null) {
			Container_meta.setContent_Type(type);
		}
		String count = Container_meta_handler.getdata_from_header(headContainer.Headers,
				Container_OBJECT_COUNT);
		if (count != null) {
			Container_meta.setNum_Object(count);
		}
		String bytes = Container_meta_handler.getdata_from_header(headContainer.Headers,
				Container_BYTES_USED);
		if (bytes != null) {
			Container_meta.setSum_Bytes(bytes);
		}
		Map<String,String> meta = Container_meta_handler.getMetadata_from_header(headContainer.Headers,
				Container_META);
		if (meta != null) {
			Container_meta.setContainer_Meta(meta);
		}
		return Container_meta;
	}
	@Override
	public boolean removeContainerMetadata(ContainerMetadata meta)
			throws CSSPException, IOException {
		judge_is_login();
		Map<String, String> query = new LinkedHashMap<String, String>();
		if ( meta == null || meta.getContainer_Remove_Meta() == null) {
			throw new ParameterException(PARAMETER_NULL);
		}
		for (Map.Entry<String, String> entry : meta.getContainer_Remove_Meta()
				.entrySet()) {
			if (entry.getKey() == null || entry.getValue() == null) {
				throw new ParameterException(PARAMETER_NULL);
			}
			query.put(REMOVE_Container_META + entry.getKey(), entry.getValue());
		}
		SwiftClient swiftclient = new SwiftClient();
		SwiftClientResponse postContainer;
		postContainer = swiftclient.PostContainer(this.Access_URL,
				this.ContainerName, query, this.AccessKey, this.SecretKey);
		return true;
	}
	@Override
	public boolean setContainerACL(AccessControlList acl)throws CSSPException, IOException
	{
		judge_is_login();
		//在设置公共枚举的时候必须是公共读，如果之前有公共读这把这个公共枚举加上
		//如果之前不是公共读，只设置了公共枚举就抛异常
		SwiftClient swiftclient = new SwiftClient();
		
		Map<String, String> query = new LinkedHashMap<String, String>();
		
		String acl_list = "" ;
		if(acl.name().equals("Private"))//设置为私有读写之后，其他的参数都可以覆盖
		{
			query.put(Container_READ, "*");
		}
		else if(acl.name().equals("PublicRead_PublicList"))
		{
			query.put(Container_READ, PUBLIC_READ);
		}
		else if(acl.name().equals("PublicRead"))
		{
			acl_list = ".r:*";
			query.put(Container_READ, acl_list);
		}
		if(query.size() <= 0)
		{
			throw new ParameterException(PARAMETER_NULL);
		}
		SwiftClientResponse postContainer;
		postContainer = swiftclient.PostContainer(this.Access_URL,
				this.ContainerName, query, this.AccessKey, this.SecretKey);
		return true;
	}
	//必须设置为公共读之后才能设置白名单，要不就不可以执行,或者之前设置过白名单
	//设置白名单和黑名单必须是之前设置为公共读，第一次不能调用白名单和黑名单功能,强制设定黑白名单都已*.开头
	@Override
	public boolean setContainerACLReferList(String whitelist, String blacklist)throws CSSPException, IOException{
    	judge_is_login();
    	ParameterHandler handler = new ParameterHandler();
		SwiftClient swiftclient = new SwiftClient();
		if(whitelist == null && blacklist == null)
		{
			throw new ParameterException(PARAMETER_NULL);
		}
		String newwhitelist = "";
		String newblacklist = "";
		if(whitelist != null)
		{
			if(!whitelist.startsWith("*."))
			{
				throw new ParameterException(PARAMETER_ERROR);
			}
			if(!handler.judge_list(whitelist.substring(whitelist.indexOf("*.")+2)))
			{
				throw new ParameterException(PARAMETER_ERROR);
			}
			newwhitelist =  whitelist.substring(whitelist.indexOf("*.")+2);
		}
		if(blacklist != null)
		{
			if(!blacklist.startsWith("*."))
			{
				throw new ParameterException(PARAMETER_ERROR);
			}
			if(!handler.judge_list(blacklist.substring(blacklist.indexOf("*.")+2)))
			{
				throw new ParameterException(PARAMETER_ERROR);
			}
			newblacklist = blacklist.substring(blacklist.indexOf("*.")+2);
		}
		//先判断之前的权限：如果是私有读写，那么白名单和黑名单就设置不了，只有公共读和公共枚举就可以枚举
		//如果已经设置为公共读或者有白名单和黑名单，那么就可以继续设置白名单和黑名单
		SwiftClientResponse headContainer;
		headContainer = swiftclient.HeadContainer(Access_URL,
				this.ContainerName, this.AccessKey, this.SecretKey);
		ParameterHandler Container_meta_handler = new ParameterHandler();
		String acl_old;
		String ACL = Container_meta_handler.getdata_from_header(headContainer.Headers, Container_READ);
		if(ACL == null || ACL.equals("*"))//没有设置过或为私有读写则报错:也就是判断是否为public权限,公共权限要包含.r:
		{
			throw new ACLException(PRIVATE_REFER);
		}
		else if(ACL.indexOf(".r:") == -1)//私有权限或公共枚举
		{
			throw new ACLException(PRIVATE_REFER);
		}
		else if(ACL.indexOf(".r:*,.rlistings") != -1)//公共读写
		{
			acl_old = ACL.replace(".r:*,.rlistings", "");
		}
		else if(ACL.indexOf(".r:*") != -1)//公共读写
		{
			if(ACL.indexOf(".r:*,") != -1)
			{
				acl_old = ACL.replace(".r:*,", "");
			}
			else
			{
				acl_old = ACL.replace(".r:*", "");
			}
		}
		else
		{
			acl_old = ACL;
		}
		List<String> list_acl = new ArrayList<String>();
		String[] list = acl_old.split(",");
		for(int i = 0; i< list.length;i++)
		{
			list_acl.add(list[i]);
		}
		Map<String, String> query = new LinkedHashMap<String, String>();
		boolean para_is_exist = false;
		if(whitelist != null)
		{
			for(int i = 0; i< list_acl.size();i++)
			{
				if(list_acl.get(i).equals(".r:-." + newwhitelist ))
				{
					throw new ParameterException(PARAMETER_ERROR);
				}
				if(list_acl.get(i).equals(".r:" + newwhitelist ))
				{
					para_is_exist = true;
					break;
				}
			}
		}
		if(whitelist != null && para_is_exist == false)
		{
			list_acl.add(".r:." + newwhitelist);
		}
		
		para_is_exist = false;
		if(blacklist != null)
		{
			for(int i = 0; i< list_acl.size();i++)
			{
				if(list_acl.get(i).equals(".r:." + newblacklist ))
				{
					throw new ParameterException(PARAMETER_ERROR);
				}
				if(list_acl.get(i).equals(".r:" + newblacklist))
				{
					para_is_exist = true;
					break;
				}
			}
		}
		if(blacklist != null && para_is_exist == false)
		{
			list_acl.add(".r:-." + newblacklist);
		}
		String acl_new = list_acl.get(list_acl.size()-1);
		if(list_acl.size() -2 >=0)
		{
			for(int i= list_acl.size()-2;i >= 0;i--)
			{
				if(!list_acl.get(i).equals(""))
				{
					acl_new += "," + list_acl.get(i);
				}
			}
		}
		query.put(Container_READ, acl_new);
		SwiftClientResponse postContainer;
		postContainer = swiftclient.PostContainer(Access_URL,
				this.ContainerName, query, this.AccessKey, this.SecretKey);
		return true;
    }
    
	@Override
	public ContainerACL getContainerACL() throws CSSPException, IOException {
		judge_is_login();
		ContainerACL Container_acl = new ContainerACL();
		SwiftClient swiftclient = new SwiftClient();
		SwiftClientResponse headContainer;
		headContainer = swiftclient.HeadContainer(Access_URL,
				this.ContainerName, this.AccessKey, this.SecretKey);
		ParameterHandler Container_meta_handler = new ParameterHandler();
		String ACL = Container_meta_handler.getdata_from_header(headContainer.Headers, Container_READ);
		if ( ACL != null) {
			if (ACL.equals(".r:*,.rlistings")) {
				Container_acl.setRead("PublicRead and PublicList");
				Container_acl.setWrite("Private");
			}
			else if (ACL.equals(".r:*"))
			{
				Container_acl.setRead("PublicRead");
				Container_acl.setWrite("Private");
			}
			else if (ACL.equals("*")) 
			{
				Container_acl.setRead("Private");
				Container_acl.setWrite("Private");
			}
			else
			{
				Container_acl.setRead(ACL.replace(".r:", ""));
				Container_acl.setWrite("Private");
			}
		}
		else// default for private
		{
			Container_acl.setRead("Private");
			Container_acl.setWrite("Private");
		}
		return Container_acl;
	}

	@Override
	public List<ObjectList> listObjects() throws CSSPException,
			IOException {
		return public_list_Objects(-1, null, null, null);
	}

	@Override
	public List<ObjectList> listObjects(int limit) throws CSSPException, IOException {
		if (limit <= 0 || limit > ENUM_LIMIT_NUM) {
			throw new ParameterException(PARAMETER_ERROR);
		}
		return public_list_Objects(limit, null, null, null);
	}

	public List<ObjectList> listObjects(int limit, String marker) throws CSSPException, IOException {
		if (limit <= 0 || limit > ENUM_LIMIT_NUM) {
			throw new ParameterException(PARAMETER_ERROR);
		}
		if (marker == null) {
			throw new ParameterException(PARAMETER_NULL);
		}
		return public_list_Objects(limit, null, marker, null);
	}

	@Override
	public List<ObjectList> listObjects(String prefix)
			throws CSSPException, IOException {
		if (prefix == null || prefix.trim().isEmpty()) {
			throw new ParameterException(PARAMETER_NULL);
		}
		return public_list_Objects(-1, prefix, null, null);
	}

	@Override
	public List<ObjectList> listObjects(String prefix, char delimiter) throws CSSPException, IOException {
		if (prefix == null || prefix.trim().isEmpty() || delimiter == '\0') {
			throw new ParameterException(PARAMETER_NULL);
		}
		return public_list_Objects(-1, prefix, null,
				Character.toString(delimiter));
	}

	@Override
	public List<ObjectList> listObjects(int limit,
			String prefix, String marker) throws CSSPException, IOException {
		if (prefix == null || marker == null || prefix.trim().isEmpty()) {
			throw new ParameterException(PARAMETER_NULL);
		}
		if (limit <= 0 || limit > ENUM_LIMIT_NUM) {
			throw new ParameterException(PARAMETER_ERROR);
		}
		return public_list_Objects(limit, prefix, marker, null);
	}
	@Override
	public List<ObjectList> listObjects(String prefix, char delimiter, int limit)throws CSSPException, IOException{
		if (prefix == null || prefix.trim().isEmpty() || delimiter == '\0') {
			throw new ParameterException(PARAMETER_NULL);
		}
		if (limit <= 0 || limit > ENUM_LIMIT_NUM) {
			throw new ParameterException(PARAMETER_ERROR);
		}
		return public_list_Objects(limit, prefix, null,
				Character.toString(delimiter));
	}
	private List<ObjectList> public_list_Objects(int limit, String prefix, String marker, 
			String delimiter) throws CSSPException, IOException {
		judge_is_login();
		Map<String, String> query = new LinkedHashMap<String, String>();
		boolean list_all_objects = true;
		query.put("format", "json");
		if (limit >= 0 && limit <= ENUM_LIMIT_NUM)
		{
			query.put("limit", Integer.toString(limit));
			list_all_objects = false;
		}
		if (marker != null) {
			query.put("marker", encode(marker));
		}
		if (prefix != null) {
			query.put("prefix", encode(prefix));
		}
		if (delimiter != null) {
			query.put("delimiter", encode(delimiter));
		}
		SwiftClient swiftclient = new SwiftClient();
		SwiftClientResponse getContainer;
		getContainer = swiftclient.GetContainer(Access_URL, list_all_objects,
				this.ContainerName, query, this.AccessKey, this.SecretKey);
		List<LinkedHashMap<String, String>> objects_ = new ArrayList<LinkedHashMap<String, String>>();
		objects_.addAll(getContainer.Lists);
		List<ObjectList> object_listing = new ArrayList<ObjectList>();
		for (LinkedHashMap<String, String> object_list : getContainer.Lists) {
			query.clear();
			query.putAll(object_list);
			ObjectList ObjectList = new ObjectList(query.get("hash"),
					query.get("last_modified"), query.get("bytes"),
					query.get("name"), query.get("content_type"),
					query.get("subdir"));
			object_listing.add(ObjectList);
		}
		return object_listing;
	}
	
	@Override
	public PutObjectResult putObject(String objectname,
			InputStream content, ObjectMetadata meta) throws CSSPException,
			IOException {
		if (content == null || meta == null) {
			throw new ParameterException(PARAMETER_NULL);
		}
		if (meta.getContent_Length() <= 0) {
			throw new ParameterException(PARAMETER_ERROR);
		}
//		if (meta.getContent_Length() >= LENGTH_500M) {
//			throw new ParameterException(PARAMETER_TOO_LARGE);
//		}
		return public_putObject(objectname, content, null, meta);
	}

	@Override
	public PutObjectResult putObject(String objectname,
			InputStream content, String MD5, ObjectMetadata meta)
			throws CSSPException, IOException {
		if (content == null || MD5 == null || meta == null) {
			throw new ParameterException(PARAMETER_NULL);
		}
		if (meta.getContent_Length() <= 0) {
			throw new ParameterException(PARAMETER_ERROR);
		}
//		if (meta.getContent_Length() >= LENGTH_500M) {
//			throw new ParameterException(PARAMETER_TOO_LARGE);
//		}
		return public_putObject(objectname, content, MD5, meta);
	}

	private PutObjectResult public_putObject(String objectname, InputStream content, String MD5,
			ObjectMetadata meta) throws CSSPException, IOException {
		judge_is_login();
		ParameterHandler judge_object_meta = new ParameterHandler();
		Config is_object = new Config();
		if (!is_object.match_object(objectname)) {
			throw new ParameterException(PARAMETER_ERROR);
		}
		if(meta.getDetect_Type() != false && meta.getContent_Type() != null 
				&& meta.getObject_Meta() != null){
		if(judge_object_meta.decide_ObjectMetadata(meta)){
			throw new ParameterException(PARAMETER_ERROR);
		}}
		Map<String, String> query = new LinkedHashMap<String, String>();
		String MD5_using = null;
		if (MD5 != null) {
			MD5_using = MD5;
		}
		String content_type = null;
		if(meta.getContent_Type() != null)
		{
			content_type = meta.getContent_Type();
		}
		query = judge_object_meta.get_para_ObjectMetadata(meta);
		SwiftClient swiftclient = new SwiftClient();
		SwiftClientResponse putObject;
		putObject = swiftclient.PutObject(Access_URL,
				this.ContainerName, objectname, content, meta.getContent_Length(),
				MD5_using,content_type, query,this.AccessKey,this.SecretKey);
		PutObjectResult putobjectresult = new PutObjectResult();
		putobjectresult.setContainer(this.ContainerName);
		String ETag = judge_object_meta.getdata_from_header(putObject.Headers, "ETag") ;
		if (ETag != null) {
			putobjectresult.setEtag(ETag);
		}
		ETag = judge_object_meta.getdata_from_header(putObject.Headers, "Etag") ;
		if (ETag != null) {
			putobjectresult.setEtag(ETag);
		}
		return putobjectresult;
	}

	@Override
	public CSSPObject getObject(String objectname)throws CSSPException, IOException {
		return public_getObject(objectname, -1, -1);
	}

	@Override
	public CSSPObject getObject(String objectname,
			long start_byte, long end_byte) throws CSSPException, IOException {
		if (start_byte == -1 && end_byte == -1) {
			throw new ParameterException(PARAMETER_ERROR);
		}
		return public_getObject(objectname, start_byte, end_byte);
	}
	private void privatesetObjectMeta(Map<String,String> Headers, ObjectMetadata metadata)
	{
		ParameterHandler set_object_para = new ParameterHandler();
		String time = set_object_para.getdata_from_header(Headers,
				"Last-Modified");
		if ( time != null) {
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'",Locale.US);
		Date date;
		try {
			date = sdf.parse(time);
			Calendar rightNow = Calendar.getInstance();
	        rightNow.setTime(date);
	        rightNow.add(Calendar.HOUR, 8);
	        Date dt1=rightNow.getTime();
	        sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
			metadata.setLast_Modified(sdf.format(dt1)); 
		} catch (ParseException e) {
			logger.info(e.getMessage());
			}
		}
		String content_length = set_object_para.getdata_from_header(Headers,
				"Content-Length");
		if ( content_length != null) {
			metadata.setContent_Length(Long.parseLong(content_length));
		}
		String type = set_object_para.getdata_from_header(Headers,
				CONTENT_TYPE);
		if ( type != null) {
			metadata.setContent_Type(type);
		}
		Map<String,String> meta = set_object_para.getMetadata_from_header(Headers,
				OBJECT_META);
		if (meta != null) {
			metadata.setObject_Meta(meta);
		}
		String manifest = set_object_para.getdata_from_header(Headers,
				MANIFEST_HEADER);
		if (manifest != null) {
			metadata.setObject_Manifest(manifest);
		}
	}
	private CSSPObject public_getObject(String objectname,
			long offset, long length) throws CSSPException, IOException {
		judge_is_login();
		ParameterHandler set_object_para = new ParameterHandler();
		if (objectname == null || objectname.trim().isEmpty()) {
			throw new ParameterException(PARAMETER_NULL);
		}
		Map<String, String> query = new LinkedHashMap<String, String>();
		if (set_object_para.getRange(offset, length) == null) {
			throw new ParameterException(PARAMETER_ERROR);
		}
		if (!"all".equals(set_object_para.getRange(offset, length))) {
			query.put("Range", set_object_para.getRange(offset, length));
		}
		SwiftClient swiftclient = new SwiftClient();
		SwiftClientResponse getObject;
		getObject = swiftclient.GetObject(Access_URL,
				this.ContainerName, objectname, query,this.AccessKey,this.SecretKey);
		CSSPObject cssp_object = new CSSPObject();
		if (getObject.ObjectData != null) {
			if(set_object_para.getdata_from_header(getObject.Headers, "Content-Range") != null)
			{
				cssp_object.setContent_Range(set_object_para.getdata_from_header(getObject.Headers, "Content-Range"));
			}
			cssp_object.setObject_Content(getObject.ObjectData.getStream());
		}
		ObjectMetadata metadata = new ObjectMetadata();
		privatesetObjectMeta(getObject.Headers,metadata);
		String ETag = set_object_para.getdata_from_header(getObject.Headers, "ETag") ;
		if (ETag != null) {
			metadata.setEtag(ETag);
		}
		ETag = set_object_para.getdata_from_header(getObject.Headers, "Etag") ;
		if (ETag != null) {
			metadata.setEtag(ETag);
		}
		cssp_object.setObject_Meta(metadata);
		return cssp_object;
	}

	@Override
	public boolean isObjectExist(String objectname)
			throws CSSPException, IOException {
		judge_is_login();
		if (objectname == null || objectname.trim().isEmpty()) {
			throw new ParameterException(PARAMETER_NULL);
		}
		SwiftClient swiftclient = new SwiftClient();
		SwiftClientResponse headObject;
		headObject = swiftclient.HeadObject(Access_URL,
				this.ContainerName, objectname, this.AccessKey, this.SecretKey);
		return true;
	}

	@Override
	public boolean deleteObject( String objectname)
			throws CSSPException, IOException {
		judge_is_login();
		if ( objectname == null || objectname.trim().isEmpty()) {
			throw new ParameterException(PARAMETER_NULL);
		}
		SwiftClient swiftclient = new SwiftClient();
		SwiftClientResponse deleteObject;
		deleteObject = swiftclient.DeleteObject(Access_URL,
				this.ContainerName, objectname, this.AccessKey, this.SecretKey);
		return true;
	}
	@Override
	public CopyObjectResult copyObject(String srcContainer, String srcobject,
			String desobject) throws CSSPException,IOException {
		judge_is_login();
		if (srcContainer == null || srcobject == null
				|| desobject == null || srcContainer.trim().isEmpty()
				|| srcobject.trim().isEmpty() || desobject.trim().isEmpty()) {
			throw new ParameterException(PARAMETER_NULL);
		}
		Config is_object = new Config();
		if (!is_object.match_object(srcobject)
				|| !is_object.match_object(desobject)) {
			throw new ParameterException(PARAMETER_ERROR);
		}
		SwiftClient swiftclient = new SwiftClient();
		SwiftClientResponse copyObject;
		copyObject = swiftclient.CopyObject(Access_URL,
				srcContainer, srcobject, this.ContainerName, desobject, this.AccessKey, this.SecretKey);
		CopyObjectResult copyobjectresult = new CopyObjectResult();
		copyobjectresult.setContainer(this.ContainerName);
		ParameterHandler handler_object_meta = new ParameterHandler();
		copyobjectresult.setCopied_From(srcContainer + "/" + srcobject);
		String ETag = handler_object_meta.getdata_from_header(copyObject.Headers, "ETag") ;
		if (ETag != null) {
			copyobjectresult.setEtag(ETag);
		}
		ETag = handler_object_meta.getdata_from_header(copyObject.Headers, "Etag") ;
		if (ETag != null) {
			copyobjectresult.setEtag(ETag);
		}
		String type = handler_object_meta.getdata_from_header(copyObject.Headers,
				CONTENT_TYPE);
		if ( type != null) {
			copyobjectresult.setType(type);
		}
		return copyobjectresult;
	}

	@Override
	public boolean setObjectMetadata(String objectname,
			ObjectMetadata meta) throws CSSPException, IOException {
		judge_is_login();
		ParameterHandler judge_object_meta = new ParameterHandler();
		if (objectname == null 
				|| objectname.trim().isEmpty() || meta == null
				|| judge_object_meta.decide_ObjectMetadata(meta)) {
			throw new ParameterException(PARAMETER_NULL);
		}
		Map<String, String> query = new LinkedHashMap<String, String>();
		query = judge_object_meta.get_para_ObjectMetadata(meta);
		String content_type = null;
		if(meta.getContent_Type() != null)
		{
			content_type = meta.getContent_Type();
		}
		if (query.size() <= 0 && content_type == null)
		{
			throw new ParameterException(PARAMETER_NULL);
		}
		SwiftClient swiftclient = new SwiftClient();
		SwiftClientResponse postObject;
		postObject = swiftclient.PostObject(this.Access_URL,
				this.ContainerName, objectname, query, content_type, this.AccessKey, this.SecretKey);
		return true;
	}

	@Override
	public ObjectMetadata getObjectMetadata(String objectname)
			throws CSSPException, IOException {
		judge_is_login();
		if ( objectname == null || objectname.trim().isEmpty()) {
			throw new ParameterException(PARAMETER_NULL);
		}
		ObjectMetadata objectmeta = new ObjectMetadata();
		SwiftClient swiftclient = new SwiftClient();
		SwiftClientResponse headObject;
		headObject = swiftclient.HeadObject(Access_URL,
				this.ContainerName, objectname,this.AccessKey,this.SecretKey);
		ParameterHandler handler_object_meta = new ParameterHandler();
		privatesetObjectMeta(headObject.Headers, objectmeta);
		String ETag = handler_object_meta.getdata_from_header(headObject.Headers, "ETag");
		if ( ETag != null) {
			objectmeta.setEtag(ETag);
		}
		ETag = handler_object_meta.getdata_from_header(headObject.Headers, "Etag");
		if ( ETag != null) {
			objectmeta.setEtag(ETag);
		}
		return objectmeta;
	}

	@Override
	public InitiateMultipartUploadResult initiateMultipartUploadRequest(
			String objectname) throws CSSPException,IOException {
		judge_is_login();
		if (objectname == null || objectname.trim().isEmpty()) {
			throw new ParameterException(PARAMETER_NULL);
		}
		Config is_object = new Config();
		if (!is_object.match_object(objectname)) {
			throw new ParameterException(PARAMETER_ERROR);
		}
		SwiftClient swiftclient = new SwiftClient();
		SwiftClientResponse headContainer;
		headContainer = swiftclient.HeadContainer(Access_URL,
				this.ContainerName, this.AccessKey, this.SecretKey);
		InitiateMultipartUploadResult ini_uoload = new InitiateMultipartUploadResult();
		String uploadid = get_UploadID();
		ini_uoload.setUploadID(uploadid);
		UploadID_list.put(this.ContainerName + "/" + objectname, uploadid);
		return ini_uoload;
	}

	@Override
	public PutObjectResult uploadPart(String objectname,
			InputStream content, long content_length, String MD5, String UploadID, int partnumber)
			throws CSSPException, IOException {
		judge_is_login();
		ParameterHandler judge_UploadID;
		String MD5_using = null;
		if (content == null || UploadID == null) {
			throw new ParameterException(PARAMETER_NULL);
		}
		if (MD5 != null) {
			MD5_using = MD5;
		}
		if (partnumber <= 0 && partnumber >= ENUM_LIMIT_NUM) {
			throw new ParameterException(PARAMETER_ERROR);
		}
		if(content_length <= 0)
		{
			throw new ParameterException(PARAMETER_ERROR);
		}
//		if (content_length >= LENGTH_500M) {
//			throw new ParameterException(PARAMETER_TOO_LARGE);
//		}
		ObjectMetadata meta = new ObjectMetadata();
		meta.setContent_Length(content_length);
		judge_UploadID = new ParameterHandler();
		judge_UploadID.judge_UploadID_exsit(UploadID_list, this.ContainerName + "/"
				+ objectname, UploadID);
		String DLO_name = null;
		String partnumberString = String.format("%04d", partnumber);
		DLO_name = encode(UploadID) + "/"
				+ encode(partnumberString);
		PutObjectResult putobjectresult = new PutObjectResult();
		putobjectresult = public_putObject(objectname + "/"
				+ DLO_name, content, MD5_using, meta);
		return putobjectresult;
	}

	@Override
	public List<ObjectList> listMultipartUpload(String objectname, String UploadID) 
			throws CSSPException,IOException {
		judge_is_login();
		if (objectname == null || UploadID == null
				|| objectname.trim().isEmpty()) {
			throw new ParameterException(PARAMETER_NULL);
		}
		ParameterHandler judge_UploadID;
		judge_UploadID = new ParameterHandler();
		judge_UploadID.judge_UploadID_exsit(UploadID_list, this.ContainerName + "/"
				+ objectname, UploadID);
		String prefix = objectname + "/" + encode(UploadID) + "/";
		List<ObjectList> upload_part_list = new ArrayList<ObjectList>();
		upload_part_list = listObjects(prefix);
		return upload_part_list;
	}

	@Override
	public boolean abortMultipartUpload(String objectname,
			String UploadID) throws CSSPException, IOException {
		judge_is_login();
		if ( objectname == null || UploadID == null
				||objectname.trim().isEmpty()) {
			throw new ParameterException(PARAMETER_NULL);
		}
		ParameterHandler judge_UploadID;
		judge_UploadID = new ParameterHandler();
		judge_UploadID.judge_UploadID_exsit(UploadID_list, this.ContainerName + "/"
				+ objectname, UploadID);
		SwiftClient swiftclient = new SwiftClient();
		String prefix = objectname + "/" + encode(UploadID) + "/";
		List<ObjectList> upload_part_list = new ArrayList<ObjectList>();
		upload_part_list = listObjects(prefix);
		boolean abort_value = false;
		List<String> objects_name_list = new ArrayList<String>();
		for (int i = 0; i < upload_part_list.size(); i++) {
			ObjectList tmp = upload_part_list.get(i);
			objects_name_list.add(upload_part_list.get(i).getObject_name());
		}
		for (int j = 0; j < objects_name_list.size();) {
			SwiftClientResponse deleteObject;
			deleteObject = swiftclient.DeleteObject(this.Access_URL,
					this.ContainerName, objects_name_list.get(j), this.AccessKey, this.SecretKey);
			j++;
			if (j == objects_name_list.size())
			{
				abort_value = true;
			}
		}
		return abort_value;
	}

	@Override
	public PutObjectResult completeMultipartUpload(String objectname, String UploadID) 
			throws CSSPException,IOException {
		judge_is_login();
		if ( objectname == null || UploadID == null
				|| objectname.trim().isEmpty()) {
			throw new ParameterException(PARAMETER_NULL);
		}
		ParameterHandler judge_UploadID;
		judge_UploadID = new ParameterHandler();
		judge_UploadID.judge_UploadID_exsit(UploadID_list, this.ContainerName + "/"
				+ objectname, UploadID);
		Map<String, String> query = new LinkedHashMap<String, String>();
		query.put(MANIFEST_HEADER, encode(this.ContainerName) + "/"
				+ encode(objectname) + "/" + encode(UploadID));
		ObjectMetadata partmeta = new ObjectMetadata();
		partmeta.setObject_Meta(query);
		PutObjectResult putobjectresult = new PutObjectResult();
		SwiftClient swiftclient = new SwiftClient();
		SwiftClientResponse putObject;
		ParameterHandler judge_object_meta = new ParameterHandler();
		putObject = swiftclient.PutObject(Access_URL,
				this.ContainerName, objectname, null, 0, null, null, query, this.AccessKey, this.SecretKey);
		putobjectresult.setContainer(this.ContainerName);
		String ETag = judge_object_meta.getdata_from_header(putObject.Headers, "Etag");
		if (ETag != null) {
			putobjectresult.setEtag(ETag);
		}
		ETag = judge_object_meta.getdata_from_header(putObject.Headers, "ETag");
		if (ETag != null) {
			putobjectresult.setEtag(ETag);
		}
		return putobjectresult;
	}

	@Override
	public void set_UploadID(String Containername, String objectname,
			String UploadID) throws CSSPException {
		if (Containername == null || objectname == null || UploadID == null
				|| Containername.trim().isEmpty() || objectname.trim().isEmpty()) {
			throw new ParameterException(PARAMETER_NULL);
		}
		judge_is_login();
		UploadID_list.put(Containername + "/" + objectname, UploadID);
	}
	@Override
	public FaceVerificationResult faceVerificationOperate( String objectname, FaceVerificationRequest request) throws CSSPException,IOException 
    {
		judge_is_login();
		if ( objectname == null || objectname.trim().isEmpty()
				|| request == null) {
			throw new ParameterException(PARAMETER_NULL);
		}
		if(request.Get_Sst() == null)//必传的参数
		{
			throw new ParameterException(PARAMETER_NULL);
		}
        ParameterHandler judge_object_meta = new ParameterHandler();
        Map<String, String> query = new LinkedHashMap<String, String>();
        query.put("service", "wfr");
        query.put("sst",request.Get_Sst().toString());
        if (request.Get_Gid() != null)
        {
            query.put("gid", request.Get_Gid());
        }
        SwiftClient client = new SwiftClient();
        SwiftClientResponse putFaceVerity;
        putFaceVerity = client.PutFaceVerity(this.Access_URL,this.ContainerName,
        		objectname, null, -1, query, this.AccessKey, this.SecretKey);
        FaceVerificationResult value = new FaceVerificationResult();
        String gid = judge_object_meta.getdata_from_header(putFaceVerity.Headers, "X-Wfr-Gid");
        if (gid != null)
        {
            value.Set_WFR_GID(gid);
        }
        String score = judge_object_meta.getdata_from_header(putFaceVerity.Headers, "X-Wfr-Score");
        if (score != null)
        {
            value.Set_WFR_SCORE(score);
        }
        String face = judge_object_meta.getdata_from_header(putFaceVerity.Headers, "X-Wfr-Face");
        if (face != null)
        {
            value.Set_WFR_FACE(face);
        }
        return value;
    }
	private static String encode(String name) {
		return encode(name, false);
	}

	private static String encode(String object, boolean preserveslashes) {
		URLCodec codec = new URLCodec();
		try {
			final String encoded = codec.encode(object)
					.replaceAll("\\+", "%20");
			if (preserveslashes) {
				return encoded.replaceAll("%2F", "/");
			}
			return encoded;
		} catch (EncoderException ee) {
			return object;
		}
	}

	private static String get_UploadID() {
		String time = new SimpleDateFormat("yyyyMMddHHmmssSSS")
				.format(new Date());
		Random random = new Random();
		int random_1 = random.nextInt(99999);
		while (random_1 < 10000) {
			random_1 = random.nextInt(99999);
		}
		int random_2 = random.nextInt(99999);
		while (random_2 < 10000) {
			random_2 = random.nextInt(99999);
		}
		int random_3 = random.nextInt(99999);
		while (random_3 < 10000) {
			random_3 = random.nextInt(99999);
		}
		return time + random_1 + random_2 + random_3;
	}

	private void judge_is_login() throws NotLoginException {
		if (this.AccessKey == null) {
			throw new NotLoginException(NOTLOGINERROR);
		} else if (Access_URL == null || this.SecretKey == null) {
			throw new NotLoginException(NOTLOGINERROR);
		}
	}
}