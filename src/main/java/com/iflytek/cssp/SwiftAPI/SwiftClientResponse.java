/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iflytek.cssp.SwiftAPI;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.StatusLine;

/**
 *
 * @author ttsun4
 */
public class SwiftClientResponse {
     /// <summary>
    /// Account Headers
    /// </summary>
    public  Map<String, String> Headers;
    /// <summary>
    /// the Status number of the request
    /// </summary>
    public int Status;
    /// <summary>
    /// The status description of the request
    /// </summary>
    public StatusLine httpStatusLine;
    /// <summary>
    /// The container or object list returned if a get request otherwise this will be null
    /// </summary>
    public List<LinkedHashMap<String, String>> Lists;
    /// <summary>
    /// A Stream and length of the object data only used for get requests
    /// </summary>
    public ContentStream ObjectData;
    /// <summary>
    /// Initializes a new instance of the <see><cref>Openstack.Swift.SwiftClientResponse</cref></see> class.
    /// </summary>
    /// <param name='headers'>
    /// The response headers
    /// </param>
    /// <param name='reason'>
    /// The status description
    /// </param>
    /// <param name='status'>
    /// The status code of the request
    /// </param>
    /// <param name='containers'>
    /// The Container List if one is needed null otherwise
    /// </param>
    public SwiftClientResponse(Map<String, String> headers,  int status,StatusLine StatusLine, List<LinkedHashMap<String,String>> Containers,ContentStream object_data)
    {
        Headers = headers;
        Status = status;
        httpStatusLine = StatusLine;
        Lists = Containers;
        ObjectData = object_data;
    }
}
