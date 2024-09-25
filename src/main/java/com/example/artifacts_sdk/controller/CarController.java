package com.example.artifacts_sdk.controller;

import com.example.artifacts_sdk.helper.GatewayHelper;
import com.example.artifacts_sdk.model.Car;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hyperledger.fabric.gateway.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CarController {
    private static final String CHANNEL_NAME = System.getenv().getOrDefault("CHANNEL_NAME", "mychannel");
    private static final String CHAINCODE_NAME = System.getenv().getOrDefault("CHAINCODE_NAME", "fabcar");
    Map res = null;

    @GetMapping("/get_all_cars")
    public ResponseEntity<?> queryAllCars() {
        byte[] result;

        try (Gateway gateway = GatewayHelper.connect()){

            // get the network and contract
            Network network = gateway.getNetwork(CHANNEL_NAME);
            Contract contract = network.getContract(CHAINCODE_NAME);

            result = contract.evaluateTransaction("QueryAllCars");
            System.out.println("Evaluate Transaction: QueryAllCars, result: " + new String(result));

            ObjectMapper objMapper = new ObjectMapper();
            ArrayList list = objMapper.readValue(new String(result, StandardCharsets.UTF_8), new TypeReference<>() {
            });

            res = new HashMap();
            res.put("localDateTime", LocalDateTime.now());
            res.put("message", "Get all cars successfully");
            res.put("payload", list);

        } catch (Exception  e) {
            System.out.println(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping("/get_car_by_id")
    public ResponseEntity<?> queryCarById(String carId) {

        byte[] result;

        try (Gateway gateway = GatewayHelper.connect()){

            // get the network and contract
            Network network = gateway.getNetwork(CHANNEL_NAME);
            Contract contract = network.getContract(CHAINCODE_NAME);

            result = contract.evaluateTransaction("QueryCar", carId);
            System.out.println("Evaluate Transaction: QueryCar, result: " + new String(result));

            ObjectMapper objMapper = new ObjectMapper();
            Object obj = objMapper.readValue(new String(result, StandardCharsets.UTF_8), new TypeReference<>() {
            });

            res = new HashMap();
            res.put("localDateTime", LocalDateTime.now());
            res.put("message", "Get car by id successfully");
            res.put("payload", obj);

        } catch (Exception  e) {
            System.out.println(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping("/get_car_by_owner")
    public ResponseEntity<?> queryCarsByOwner(String ownerName) {

        byte[] result;

        try (Gateway gateway = GatewayHelper.connect()){

            // get the network and contract
            Network network = gateway.getNetwork(CHANNEL_NAME);
            Contract contract = network.getContract(CHAINCODE_NAME);

            result = contract.evaluateTransaction("QueryCarsByOwner", ownerName);
            System.out.println("Evaluate Transaction: QueryCarsByOwner, result: " + new String(result));

            ObjectMapper objMapper = new ObjectMapper();
            ArrayList list = objMapper.readValue(new String(result, StandardCharsets.UTF_8), new TypeReference<>() {
            });

            res = new HashMap();
            res.put("localDateTime", LocalDateTime.now());
            res.put("message", "Get car by owner successfully");
            res.put("payload", list);

        } catch (Exception  e) {
            System.out.println(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @GetMapping("/get_car_history")
    public ResponseEntity<?> queryHistoryForCars(String carId) {

        byte[] result;

        try (Gateway gateway = GatewayHelper.connect()){

            // get the network and contract
            Network network = gateway.getNetwork(CHANNEL_NAME);
            Contract contract = network.getContract(CHAINCODE_NAME);

            result = contract.evaluateTransaction("GetHistoryForAsset", carId);
            System.out.println("Evaluate Transaction: GetHistoryForAsset, result: " + new String(result));

            ObjectMapper objMapper = new ObjectMapper();
            ArrayList list = objMapper.readValue(new String(result, StandardCharsets.UTF_8), new TypeReference<>() {
            });

            res = new HashMap();
            res.put("localDateTime", LocalDateTime.now());
            res.put("message", "Car created successfully");
            res.put("payload", list);

        } catch (Exception  e) {
            System.out.println(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @PostMapping("/create_car")
    public ResponseEntity<?> createCar(Car car){

        byte[] result;

        try (Gateway gateway = GatewayHelper.connect()){

            // get the network and contract
            Network network = gateway.getNetwork(CHANNEL_NAME);
            Contract contract = network.getContract(CHAINCODE_NAME);

            contract.submitTransaction("CreateCar", car.getCarNumber(), car.getMake(), car.getModel(), car.getColour(), car.getOwner());

            result = contract.evaluateTransaction("QueryCar", car.getCarNumber());
            System.out.println("Evaluate Transaction: QueryCar, result: " + new String(result));

            ObjectMapper objMapper = new ObjectMapper();
            Object obj = objMapper.readValue(new String(result, StandardCharsets.UTF_8), new TypeReference<>() {
            });

            res = new HashMap();
            res.put("localDateTime", LocalDateTime.now());
            res.put("message", "Car created successfully");
            res.put("payload", obj);

        } catch (Exception  e) {
            System.out.println(e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.OK).body(res);
    }
}

