/*
 * To change this license header,  choose License Headers in Project Properties.
 * To change this template file,  choose Tools | Templates
 * and open the template in the editor.
 */

package com.iflytek.cssp.SwiftAPI;

import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import com.iflytek.cssp.exception.AuthorizationException;
import com.iflytek.cssp.exception.BadRequestException;
import com.iflytek.cssp.exception.ContainerNotFoundException;
import com.iflytek.cssp.exception.CSSPException;
import com.iflytek.cssp.exception.LengthRequestException;
import com.iflytek.cssp.exception.ObjectNotFoundException;
import com.iflytek.cssp.exception.UnprocessableEntityException;

/**
 *
 * @author ttsun4
 */
public class ExceptionHandle {
    public ExceptionHandle()
    { 
    }
    public SwiftClientResponse AuthExceptionHandle(HttpResponse response, Map<String, String> headers) throws CSSPException 
    {
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED)//401
        {
            throw new AuthorizationException(response.getStatusLine().getReasonPhrase(), headers, response.getStatusLine());
        }
        else
        {
            throw new CSSPException(response.getStatusLine().getReasonPhrase(), 
            		headers, response.getStatusLine());
        }
    }
    
    public SwiftClientResponse ContainerExceptionHandle(HttpResponse response, Map<String, String> headers) throws CSSPException 
    {
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED)//401
        {
            throw new AuthorizationException(response.getStatusLine().getReasonPhrase(), headers, response.getStatusLine());
        }
        else if(response.getStatusLine().getStatusCode() == HttpStatus.SC_BAD_REQUEST)//400
        {
            throw new BadRequestException(response.getStatusLine().getReasonPhrase(), headers, response.getStatusLine());
        }
        else if(response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND)//404
        {
            throw new ContainerNotFoundException(response.getStatusLine().getReasonPhrase(), headers, response.getStatusLine());
        }
        else
        {
            throw new CSSPException(response.getStatusLine().getReasonPhrase(), headers, response.getStatusLine());
        }
    }
    public SwiftClientResponse ObjectExceptionHandle(HttpResponse response, Map<String, String> headers) throws CSSPException 
    {
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED)//401
        {
            throw new AuthorizationException(response.getStatusLine().getReasonPhrase(), headers, response.getStatusLine());
        }
        else if(response.getStatusLine().getStatusCode() == HttpStatus.SC_BAD_REQUEST)//400
        {
            throw new BadRequestException(response.getStatusLine().getReasonPhrase(), headers, response.getStatusLine());
        }
        else if(response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND)//404
        {
            throw new ObjectNotFoundException(response.getStatusLine().getReasonPhrase(), headers, response.getStatusLine());
        }
        else if(response.getStatusLine().getStatusCode() == HttpStatus.SC_LENGTH_REQUIRED)//411
        {
            throw new LengthRequestException(response.getStatusLine().getReasonPhrase(), headers, response.getStatusLine());
        }
        else if(response.getStatusLine().getStatusCode() == HttpStatus.SC_UNPROCESSABLE_ENTITY)//422
        {
            throw new UnprocessableEntityException(response.getStatusLine().getReasonPhrase(), headers, response.getStatusLine());
        }
        else
        {
            throw new CSSPException(response.getStatusLine().getReasonPhrase(), headers, response.getStatusLine());
        }
    }
}
