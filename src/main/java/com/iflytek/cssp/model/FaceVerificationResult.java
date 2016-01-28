package com.iflytek.cssp.model;

public class FaceVerificationResult {
	private String wfr_gid;
    private String wfr_score;
    private String wfr_face;

    
    public void Set_WFR_GID(String value)
    {
        this.wfr_gid = value;
    }
    public void Set_WFR_SCORE(String value)
    {
        this.wfr_score = value;
    }
   
    public void Set_WFR_FACE(String value)
    {
       this.wfr_face = value; 
    }
    public String Get_WFR_GID ()
    {
       return this.wfr_gid ;
    }
    public String Get_WFR_SCORE ()
    {
       return this.wfr_score ;
    }
    public String Get_WFR_FACE ()
    {
       return this.wfr_face ;
    }
}
