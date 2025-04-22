package com.intense.services;

import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class GetRegeneratedCAFStatus {
	
	@Test
	public void getRegeneratedCAFStatusResult() {
		
		String baseURL="http://172.16.0.227:8083/GetRegeneratedCAFStatus/update/ekycStampedFlag";
		RequestSpecification req=RestAssured.given();

		req.header("username", "intense").header("password", "intense@2020");
		req.config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs("application/form-data", ContentType.TEXT)));
		
		req.formParam("caf_number", "SKYC922025");
		Response res=req.request(Method.POST, baseURL);
		String receivedRes=res.getBody().asPrettyString();
		System.out.println("Status code is:: "+res.getStatusCode());
		System.out.println("headers are:: "+res.getHeaders());
		
		
		System.out.println(receivedRes);
		
	}

}
