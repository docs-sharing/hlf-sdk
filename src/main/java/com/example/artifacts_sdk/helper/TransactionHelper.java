package com.example.artifacts_sdk.helper;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;

public class TransactionHelper {

    private static final String CHANNEL_NAME = System.getenv().getOrDefault("CHANNEL_NAME", "mychannel");
    private static final String CHAINCODE_NAME = System.getenv().getOrDefault("CHAINCODE_NAME", "fabcar");

    public static void queryAllCars() {
        try (Gateway gateway = GatewayHelper.connect()){

            // get the network and contract
            Network network = gateway.getNetwork(CHANNEL_NAME);
            Contract contract = network.getContract(CHAINCODE_NAME);

            byte[] result;
            result = contract.evaluateTransaction("QueryAllCars");
            System.out.println("Evaluate Transaction: GetAllAssets, result: " + new String(result));

        } catch (Exception  e) {
            System.err.println(e);
            System.exit(1);
        }
    }

    public static void createCar() {
        try (Gateway gateway = GatewayHelper.connect()){

            // get the network and contract
            Network network = gateway.getNetwork(CHANNEL_NAME);
            Contract contract = network.getContract(CHAINCODE_NAME);

            // create car
            contract.submitTransaction("CreateCar", "CAR11", "BMW", "F1", "Black", "Lany");

        } catch (Exception  e) {
            System.err.println(e);
            System.exit(1);
        }
    }

    public static void queryCarByNumber() {
        try (Gateway gateway = GatewayHelper.connect()){

            // get the network and contract
            Network network = gateway.getNetwork(CHANNEL_NAME);
            Contract contract = network.getContract(CHAINCODE_NAME);

            // ReadAsset returns an asset with given carID
            byte[] result = contract.evaluateTransaction("QueryCar", "CAR11");
            System.out.println("result: " + new String(result));

        } catch (Exception  e) {
            System.err.println(e);
            System.exit(1);
        }
    }

    public static void changeCarOwner() {
        try (Gateway gateway = GatewayHelper.connect()){

            // get the network and contract
            Network network = gateway.getNetwork(CHANNEL_NAME);
            Contract contract = network.getContract(CHAINCODE_NAME);

            // change car owner
            contract.submitTransaction("ChangeCarOwner", "CAR11", "Hongmeng");

        } catch (Exception  e) {
            System.err.println(e);
            System.exit(1);
        }
    }

    public static void getHistoryForAsset() {
        try (Gateway gateway = GatewayHelper.connect()){

            // get the network and contract
            Network network = gateway.getNetwork(CHANNEL_NAME);
            Contract contract = network.getContract(CHAINCODE_NAME);

            // get history
            byte[] result = contract.evaluateTransaction("GetHistoryForAsset", "CAR11");
            System.out.println("result: " + new String(result));

        } catch (Exception  e) {
            System.err.println(e);
            System.exit(1);
        }
    }
}
