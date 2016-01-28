/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iflytek.cssp.exception;

/**
 *
 * @author ttsun4
 */
public class NotLoginException extends CSSPException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4786615730997661548L;

	public NotLoginException(String error_message)
    {
        super(error_message);
    }
    
}
