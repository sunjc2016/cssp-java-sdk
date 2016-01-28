/*
 * To change this license header, choose License Headers in Project Properties
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iflytek.cssp.exception;

import java.util.Map;
import org.apache.http.StatusLine;

/**
 *
 * @author ttsun4
 */
public class AuthorizationException extends CSSPException{
    /**
	 * 
	 */
	private static final long serialVersionUID = -2109139390383920737L;

	public AuthorizationException(String error_message,Map<String,String> httpHeader,StatusLine StatusLine)
    {
        super(error_message,httpHeader,StatusLine);
    }
}

