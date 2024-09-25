package com.example.artifacts_sdk.helper;

import com.example.artifacts_sdk.ArtifactsSdkApplication;
import org.hyperledger.fabric.gateway.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;

public class TransactionHelperV2 {
    public static void test() throws CertificateException, IOException, InvalidKeyException, ContractException {
        Properties properties = new Properties();
        InputStream inputStream = ArtifactsSdkApplication.class.getResourceAsStream("/fabric.config.properties");
        properties.load(inputStream);

        String networkConfigPath = properties.getProperty("networkConfigPath");
        String channelName = properties.getProperty("channelName");
        String contractName = properties.getProperty("contractName");

        String certificatePath = properties.getProperty("certificatePath");
        X509Certificate certificate = readX509Certificate(Paths.get(certificatePath));

        String privateKeyPath = properties.getProperty("privateKeyPath");
        PrivateKey privateKey = getPrivateKey(Paths.get(privateKeyPath));

        Wallet wallet = Wallets.newInMemoryWallet();
        wallet.put("user1", Identities.newX509Identity("Org1MSP", certificate, privateKey));

        Gateway.Builder builder = Gateway.createBuilder().identity(wallet, "user1").networkConfig(Paths.get(networkConfigPath));

        Gateway gateway = builder.connect();

        Network network = gateway.getNetwork(channelName);
        Contract contract = network.getContract(contractName);

        byte[] queryAllCars = contract.evaluateTransaction("QueryCar", "CAR11");
        System.out.println(new String(queryAllCars, StandardCharsets.UTF_8));
    }

    private static X509Certificate readX509Certificate(final Path certificatePath) throws IOException, CertificateException {
        try (Reader certificateReader = Files.newBufferedReader(certificatePath, StandardCharsets.UTF_8)){
            return Identities.readX509Certificate(certificateReader);
        }
    }

    private static PrivateKey getPrivateKey(final Path privateKeyPath) throws IOException, InvalidKeyException {
        try (Reader privateKeyReader = Files.newBufferedReader(privateKeyPath, StandardCharsets.UTF_8)){
            return Identities.readPrivateKey(privateKeyReader);
        }
    }
}
