/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iflytek.cssp.model;

/**
 *
 * @author ttsun4
 */
public class InitiateMultipartUploadResult {
    private String UploadID;
    public void setUploadID(String UploadID_)
    {
        this.UploadID = UploadID_;
    }
    public String getUploadID()
    {
        return this.UploadID;
    }
}
