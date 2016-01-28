package com.iflytek.cssp.model;

public class FaceVerificationRequest {
	public enum FaceVerificationOperate { reg, verify, detect, align };
    private FaceVerificationOperate sst;
    private String gid;

    public FaceVerificationOperate Get_Sst()
    {
        return this.sst;
    }
    public String Get_Gid()
    {
        return this.gid; 
    }
    public void Set_Sst(FaceVerificationOperate value)
    {
    	this.sst = value;
    }
    public void Set_Gid(String value)
    {
    	this.gid = value;
    }
}
