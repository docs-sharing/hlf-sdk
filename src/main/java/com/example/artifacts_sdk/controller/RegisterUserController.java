package com.example.artifacts_sdk.controller;

import com.example.artifacts_sdk.helper.IdentityHelper;
import org.hyperledger.fabric.gateway.*;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@RestController
@RequestMapping("/api")
public class RegisterUserController {

    String pemFile = "D:/School/Korea Software HRD Center/Advance course/sdk/artifacts_sdk/src/main/resources/crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/tls/ca.crt";
    Map<String, Object> res = null;
    Properties props = null;
    HFCAClient caClient = null;
    Wallet wallet = null;

    @PostMapping("/register_user")
    public ResponseEntity<?> registerUser(String enrollmentID) {

        String affiliation = "org1.department1";

        props = new Properties();
        props.put("pemFile", pemFile);
        props.put("allowAllHostNames", "true");

        try {
            caClient = HFCAClient.createNewInstance("https://localhost:7054", props);
            CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
            caClient.setCryptoSuite(cryptoSuite);

            // Create a wallet for managing identities
            wallet = Wallets.newFileSystemWallet(Paths.get("wallet"));

            if(wallet.get(enrollmentID) != null) {
                res = new HashMap<>();
                res.put("localDateTime", LocalDateTime.now());
                res.put("message", "An identity for the user \"" + enrollmentID + "\" already exists in the wallet");
                return ResponseEntity.status(HttpStatus.OK).body(res);
            }

            X509Identity adminIdentity = (X509Identity)wallet.get("admin");
            if(adminIdentity == null) {
                res = new HashMap<>();
                res.put("localDateTime", LocalDateTime.now());
                res.put("message", "\"admin\" needs to be enrolled and added to the wallet first");
                return ResponseEntity.status(HttpStatus.OK).body(res);
            }

            User admin = IdentityHelper.getAdminIdentity();

            // Register the user, enroll the user, and import the new identity into the wallet.
            RegistrationRequest registrationRequest = new RegistrationRequest(enrollmentID);
            registrationRequest.setAffiliation(affiliation);
            registrationRequest.setEnrollmentID(enrollmentID);

            String enrollmentSecret = caClient.register(registrationRequest, admin);
            Enrollment enrollment = caClient.enroll(enrollmentID, enrollmentSecret);
            Identity user = Identities.newX509Identity("Org1MSP", enrollment);
            wallet.put(enrollmentID, user);

            res = new HashMap<>();
            res.put("localDateTime", LocalDateTime.now());
            res.put("message", "Successfully enrolled user \"" + enrollmentID + "\" and imported it into the wallet");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

}
