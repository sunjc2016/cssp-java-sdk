package com.iflytek.cssp;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.iflytek.cssp.exception.CSSPException;
import com.iflytek.cssp.model.FaceVerificationRequest;
import com.iflytek.cssp.model.FaceVerificationResult;

import junit.framework.TestCase;

public class CSSPClientTest extends TestCase {
	CSSPClient instance = new CSSPClient();
	public void testFaceVerificationOperate() {
		System.out.println("Client");
		for(int i = 0; i < 3; i++){
        try {
        	
    		String accesskey = "";
            String secretkey = "";
            instance.Client(accesskey, secretkey,"demo.hf.openstorage.cn/example");
            boolean result = instance.isContainerExist();
            System.out.println(result);
		} catch (CSSPException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
	}

}
