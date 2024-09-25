package com.example.artifacts_sdk.controller;

import com.example.artifacts_sdk.helper.CAHelper;
import org.apache.commons.io.FileUtils;
import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@RestController
@RequestMapping("/api/")
public class EnrollmentController {

    String pemFile = "D:/School/Korea Software HRD Center/Advance course/sdk/artifacts_sdk/src/main/resources/crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/tls/ca.crt";
    Map<String, Object> res = null;
    Properties props = null;
    HFCAClient caClient = null;
    Wallet wallet = null;

    @PostMapping("/enroll_admin")
    public ResponseEntity<?> enrollAdmin() {

        props = new Properties();
        props.put("pemFile", pemFile);
        props.put("allowAllHostNames", "true");

        try {
            caClient = HFCAClient.createNewInstance("https://localhost:7054", props);
            CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
            caClient.setCryptoSuite(cryptoSuite);

            String enrollmentID = "admin";
            String password = "adminpw";

            wallet = Wallets.newFileSystemWallet(Paths.get("wallet"));

            if(wallet.get(enrollmentID) != null) {
                res = new HashMap<>();
                res.put("localDateTime", LocalDateTime.now());
                res.put("message", "An identity for the admin user \"admin\" already exists in the wallet");
                return ResponseEntity.status(HttpStatus.OK).body(res);
            }

            // Enroll the admin user, and import the new identity into the wallet.
            EnrollmentRequest enrollmentRequestTLS = new EnrollmentRequest();
            enrollmentRequestTLS.addHost("localhost");
            enrollmentRequestTLS.setProfile("tls");

            org.hyperledger.fabric.sdk.Enrollment enrollment = caClient.enroll(enrollmentID, password, enrollmentRequestTLS);
            Identity user = Identities.newX509Identity("Org1MSP", enrollment);
            wallet.put(enrollmentID, user);

            res = new HashMap<>();
            res.put("localDateTime", LocalDateTime.now());
            res.put("message", "Successfully enrolled " + enrollmentID + " and imported into the wallet");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}
