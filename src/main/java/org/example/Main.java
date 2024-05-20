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

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Main {

    MqttController mqttController;
    TrayController trayController;
    Configuration config;

    public Main() {
        config = new Configuration();
        config.printHeader();
        config.printConfig();
        trayController = new TrayController(config);
        mqttController = new MqttController(config, this);
    }

    public static void main(String[] args) {
        new Main();
    }

    public void handleMessage(String mqttMessage) {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(mqttMessage, JsonObject.class);

        if (jsonObject.has("flag") && jsonObject.has("topic") && jsonObject.has("message")) {
            String flag = jsonObject.get("flag").getAsString();
            String topic = jsonObject.get("topic").getAsString();
            String message = jsonObject.get("message").getAsString();
            trayController.notification(flag, topic, message);
        } else {
            System.out.println("Invalid JSON");
        }
    }

    public void handleIcon(String mqttMessage) {
        trayController.icon(mqttMessage);
    }
}