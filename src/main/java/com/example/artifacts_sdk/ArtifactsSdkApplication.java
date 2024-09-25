package com.example.artifacts_sdk;

import com.example.artifacts_sdk.helper.CAHelper;
import com.example.artifacts_sdk.helper.TransactionHelper;
import com.example.artifacts_sdk.helper.TransactionHelperV2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ArtifactsSdkApplication {

    static {
        System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
    }

    public static void main(String[] args) {
        SpringApplication.run(ArtifactsSdkApplication.class, args);

//        CAHelper.enrollAdmin();
//        CAHelper.registerUser();

//        TransactionHelper.queryAllCars();
//        TransactionHelper.createCar();
//        TransactionHelper.queryCarByNumber();
//        TransactionHelper.changeCarOwner();
//        TransactionHelper.getHistoryForAsset();

//        try {
//            TransactionHelperV2.test();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
    }
}
