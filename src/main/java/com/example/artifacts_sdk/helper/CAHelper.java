package com.example.artifacts_sdk.helper;

import org.apache.commons.io.FileUtils;
import org.hyperledger.fabric.gateway.*;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.Properties;
import java.util.Set;

public class CAHelper {
    public static void enrollAdmin() {

        String pemFile = "D:/School/Korea Software HRD Center/Advance course/sdk/artifacts_sdk/src/main/resources/crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/tls/ca.crt";

        // Using admin ca certificate org 1
        // Create a CA client for interacting with the CA.
        Properties props = new Properties();
        props.put("pemFile", pemFile);
        props.put("allowAllHostNames", "true");
        HFCAClient caClient = null;

        try {
            caClient = HFCAClient.createNewInstance("https://localhost:7054", props);
            CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
            caClient.setCryptoSuite(cryptoSuite);

            // Create a wallet for managing identities
            Wallet wallet = null;
            String enrollmentID = "admin";
            String password = "adminpw";

            // Delete wallet if it exists from prior runs
//            FileUtils.deleteDirectory(new File("wallet"));

            // Create a wallet for managing identities
            wallet = Wallets.newFileSystemWallet(Paths.get("wallet"));

            // Check to see if we've already enrolled the admin user.
            if(wallet.get(enrollmentID) != null) {
                System.out.println("An identity for the admin user \"admin\" already exists in the wallet");
                return;
            }

            // Enroll the admin user, and import the new identity into the wallet.
            EnrollmentRequest enrollmentRequestTLS = new EnrollmentRequest();
            enrollmentRequestTLS.addHost("localhost");
            enrollmentRequestTLS.setProfile("tls");

            org.hyperledger.fabric.sdk.Enrollment enrollment = caClient.enroll(enrollmentID, password, enrollmentRequestTLS);
            Identity user = Identities.newX509Identity("Org1MSP", enrollment);
            wallet.put(enrollmentID, user);

            System.out.println("Successfully enrolled " + enrollmentID + " and imported into the wallet");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void registerUser() {

        String pemFile = "D:/School/Korea Software HRD Center/Advance course/sdk/artifacts_sdk/src/main/resources/crypto-config/peerOrganizations/org1.example.com/users/Admin@org1.example.com/tls/ca.crt";

        // Create a CA client for interacting with the CA.
        Properties props = new Properties();
        props.put("pemFile", pemFile);
        props.put("allowAllHostNames", "true");
        HFCAClient caClient = null;

        try {
            caClient = HFCAClient.createNewInstance("https://localhost:7054", props);
            CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
            caClient.setCryptoSuite(cryptoSuite);

            // Create a wallet for managing identities
            Wallet wallet = Wallets.newFileSystemWallet(Paths.get("wallet"));
            String enrollmentID = "user4";
            String affiliation = "org1.department1";

            // Check to see if we've already enrolled the user.
            if(wallet.get(enrollmentID) != null) {
                System.out.println("An identity for the user \"" + enrollmentID + "\" already exists in the wallet");
                return;
            }

            X509Identity adminIdentity = (X509Identity)wallet.get("admin");
            if(adminIdentity == null) {
                System.out.println("\"admin\" needs to be enrolled and added to the wallet first");
                return;
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
            System.out.println("Successfully enrolled user \"" + enrollmentID + "\" and imported it into the wallet");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
