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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Configuration {

    private JsonObject config;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public Configuration() {
        File configFile = new File("config.json");
        try {
            // Create config file if missing
            if (!configFile.exists()) {
                JsonObject newConfigFile = new JsonObject();
                newConfigFile.addProperty("run", false);
                newConfigFile.addProperty("brokerUrl", "tcp://127.0.0.1:1883");
                newConfigFile.addProperty("username", "sampleUser");
                newConfigFile.addProperty("password", "samplePass");
                newConfigFile.addProperty("lastWillTopicPrefix", "client");
                newConfigFile.addProperty("notificationTopic", "notification/json");
                newConfigFile.addProperty("iconTopic", "notification/icon");

                try (FileWriter writer = new FileWriter(configFile)) {
                    gson.toJson(newConfigFile, writer);
                }
            }
            // Read config file
            try (FileReader reader = new FileReader(configFile)) {
                config = JsonParser.parseReader(reader).getAsJsonObject();
            }

            if (!config.get("run").getAsBoolean()) {
                JOptionPane.showMessageDialog(null, "Instructions:\n1) Open the config.json file\n2) Adjust the settings\n3) Set 'run' to true\n4) Start the application\n\nFor more information visit:\n" + this.get("githubUrl"));
                System.exit(0);
            }

        } catch (IOException e) {
            System.out.println("Configuration: ERROR!");
            System.exit(1);
        }
    }

    public String get(String key) {
        switch (key) {
            case "name":
                return "MQTT Desktop Notification";
            case "version":
                return "1.0";
            case "githubUrl":
                return "https://github.com/marcel-domke/mqtt-desktop-notification";
            case "copyright":
                return "Copyright 2024 Marcel Domke";
            default:
                return config.get(key).getAsString();
        }
    }

    public void printHeader() {
        System.out.println(this.get("name"));
        System.out.println("Version: " + this.get("version"));
        System.out.println(this.get("githubUrl"));
        System.out.println(this.get("copyright"));
        System.out.println("----------------------------------");
    }

    public void printConfig() {
        String prettyJson = gson.toJson(config);
        System.out.println("Configuration:\n" + prettyJson);
    }
}