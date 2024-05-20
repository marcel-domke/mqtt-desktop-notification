/*
Copyright 2024 Marcel Domke

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.example;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class MqttController implements MqttCallbackExtended {

    private final String notificationTopic;
    private final String iconTopic;

    private final Configuration config;
    private final Main main;
    private MqttClient client;
    private MqttConnectOptions connOpts;

    public MqttController(Configuration newConfig, Main newMain) {
        main = newMain;
        config = newConfig;

        notificationTopic = config.get("notificationTopic");
        iconTopic = config.get("iconTopic");

        Runtime.getRuntime().addShutdownHook(new executeClose());
        configure();
        while (true) {
            connect();
            if (client.isConnected()) break;
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void configure() {
        MemoryPersistence persistence = new MemoryPersistence();
        connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true); //Remember Client and deliver missed Messages
        connOpts.setAutomaticReconnect(true);
        connOpts.setWill(config.get("lastWillTopicPrefix") + "/" + getHostname(), "Offline".getBytes(), 2, false);
        connOpts.setUserName(config.get("username"));
        connOpts.setPassword(config.get("password").toCharArray());
        try {
            client = new MqttClient(config.get("brokerUrl"), getHostname(), persistence);
            client.setCallback(this);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
        System.out.println("MQTT: Configured " + connOpts.getUserName() + "@" + client.getServerURI());
        System.out.println("MQTT: ClientID: " + getHostname());
    }

    private String getHostname() {
        String hostname;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            hostname = "Java" + (int) (Math.random() * 1000000000);
        }
        return hostname;
    }

    private void connect() {
        System.out.println("MQTT: Connecting...");
        try {
            client.connect(connOpts);
        } catch (MqttException me) {
            System.out.println("MQTT: Connection failed");
            System.out.println("MQTT: " + me);
        }
    }

    private void subscribe() {
        try {
            client.subscribe(notificationTopic);
            client.subscribe(iconTopic);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
        System.out.println("MQTT: Subscribed");
    }

    public void connectComplete(boolean reconnect, String serverURI) {
        System.out.println("MQTT: Connected");
        subscribe();
        try {
            client.publish(connOpts.getWillDestination(), "Online".getBytes(), 2, false);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    public void connectionLost(Throwable arg0) {
        System.out.println("MQTT: Connection Lost");
    }

    public void deliveryComplete(IMqttDeliveryToken arg0) {
        System.out.println("MQTT: Sending Message...");
    }

    public void messageArrived(String topic, MqttMessage message) {
        System.out.println("MQTT: Incoming Message -> topic: " + topic + "  msg: " + message.toString());

        if (topic.equals(config.get("notificationTopic"))) {
            main.handleMessage(message.toString());
        } else if (topic.equals(config.get("iconTopic"))) {
            main.handleIcon(message.toString());
        }
    }

    private class executeClose extends Thread {
        public void run() {
            try {
                client.publish(connOpts.getWillDestination(), "Offline".getBytes(), 2, false);
                client.disconnect();
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
            System.out.println("MQTT: Disconnected");
        }
    }

}
