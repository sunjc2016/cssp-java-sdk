/*
 * To change this license header, choose License Headers in Project Properties.
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
public class CSSPException extends Exception {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 5270253647078515183L;
	private String error_message;
    private Map<String,String> httpHeaders;
    private StatusLine httpStatusLine;
    

    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public CSSPException() {
        super();
    }

    public CSSPException(String error_code)
    {
        super(error_code);
        this.error_message = error_code;
    }
    public CSSPException(String error_code,Map<String,String> httpHeader)
    {
        super(error_code);
        this.error_message = error_code;
        this.httpHeaders = httpHeader;
    }
    public CSSPException(String error_code,Map<String,String> httpHeader,StatusLine StatusLine)
    {
        super(error_code);
        this.error_message = error_code;
        this.httpHeaders = httpHeader;
        this.httpStatusLine = StatusLine;
    }
    
    public String getError_code()
    {
        return error_message;
    }
   
    public Map<String,String> getHttpHeaders() {
        return httpHeaders;
    }

    public String getHttpHeadersAsString() {
        StringBuilder httpHeaderString = new StringBuilder();
        for(Map.Entry<String,String> h : httpHeaders.entrySet()) {
            httpHeaderString.append(h.getKey()).append(": ").append(h.getValue()).append("\n");
        }
        return httpHeaderString.toString();
    }
    
    
    /**
     * @return The HTTP status line from the server
     */
    public StatusLine getHttpStatusLine() {
        return httpStatusLine;
    }

    /**
     * @return The numeric HTTP status code from the server
     */
    public int getHttpStatusCode() {
        return httpStatusLine.getStatusCode();
    }

    /**
     * @return The HTTP status message from the server
     */
    public String getHttpStatusMessage() {
        return httpStatusLine.getReasonPhrase();
    }

    /**
     * @return The version of HTTP used.
     */
    public String getHttpVersion() {
        return httpStatusLine.getProtocolVersion().toString();
    }
 
}

