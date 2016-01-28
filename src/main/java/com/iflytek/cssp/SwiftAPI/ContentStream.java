/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.iflytek.cssp.SwiftAPI;

import java.io.FilterInputStream;
import java.io.InputStream;

/**
 *
 * @author ttsun4
 */
public class ContentStream extends FilterInputStream{
    
    private final Long length;
    private final InputStream Stream;

    public ContentStream(final InputStream in, final Long length) {
        super(in);
        Stream = in;
        this.length = length;
    }

    public Long getLength() {
        return length;
    }
    public InputStream getStream() {
        return Stream;
    }
}
