# MQTT Desktop Notification

MQTT Desktop Notification is a lightweight Windows desktop application written in Java that can be used to send notifications to the Windows desktop via MQTT. The tool is ideal for displaying notifications from platforms like Node-RED, Home Assistant, or any other system that utilizes MQTT.

## Features

- **Real-time Notifications**: Receive and display desktop notifications sent via an MQTT message.
- **Icon Integration**: 
  - Allows users to represent the status of their smart home systems using a tray icon.
  - Users can update the tray icon dynamically via an MQTT message.
- **Customizable**: Easily configure MQTT broker settings, topics, and notification preferences via a `settings.json` file.
- **Windows Only**: Specifically designed for Windows OS.
- **Lightweight**: Minimal system resource usage, designed to run in the background.

## Installation

### Prerequisites

- Ensure you have [Java 8](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html) installed.
- An MQTT broker to connect to (e.g., [Mosquitto](https://mosquitto.org/)).

### Download and Run

1. **Download** the latest release from the [Releases](https://github.com/marcel-domke/mqtt-desktop-notification/releases) page.
2. **Extract** the downloaded zip file to your desired location.
3. **Run** the application by double-clicking on `mqtt-desktop-notification.jar` or by running the following command in the terminal:

    ```bash
    java -jar mqtt-desktop-notification.jar
    ```
    - **Initial Configuration**: Upon the first launch, the application will automatically generate the `settings.json` file in its directory before closing.

4. **Edit Configuration**: Customize settings by opening the `settings.json` file in a text editor. Ensure the `run` value is set to `true`.

5. **Restart Application**: After adjusting the settings, relaunch the application by running the `mqtt-desktop-notification.jar` file.

6. **Receive Notifications**: Once restarted, the application will seamlessly operate in the background, delivering desktop notifications for incoming MQTT messages.

### Auto-run on Startup

To enable the application to run automatically at startup:

1. Move the `mqtt-desktop-notification.jar` file and the `settings.json` file into the Windows startup folder. You can access this folder by pressing `Win + R`, typing `shell:startup`, and pressing `Enter`.
2. The application will now run automatically every time you start your computer.

## Configuration

Upon first startup, a `settings.json` file is automatically created in the application directory. This file has the following structure:

```json
{
  "run": false,
  "brokerUrl": "tcp://127.0.0.1:1883",
  "username": "sampleUser",
  "password": "samplePass",
  "lastWillTopicPrefix": "client",
  "notificationTopic": "notification/json",
  "iconTopic": "notification/icon"
}
```

- **run**: Boolean to control whether the application should run.
- **brokerUrl**: The URL of your MQTT broker.
- **username**: The username for connecting to the MQTT broker.
- **password**: The password for connecting to the MQTT broker.
- **lastWillTopicPrefix**: The prefix for the Last Will and Testament (LWT) topic.
- **notificationTopic**: The MQTT topic to subscribe to for notifications.
- **iconTopic**: The MQTT topic to subscribe to for icon updates.

### Editing Configuration

1. Open the `settings.json` file in a text editor.
2. Modify the fields as needed.
3. Save the file and restart the application for changes to take effect.

## Usage

### To send notifications, follow these steps:

1. **Notification Message**: Create a JSON object / String with the following structure:
   
    ```json
    {
      "flag": "info",
      "topic": "Message Header",
      "message": "Message Body"
    }
    ```

    - **Flag**: Represents the urgency of the notification in the Windows message system. Allowed values are `none`, `info`, `warning`, or `error`. If the flag is set to `error`, an additional popup will be opened.
    - **Topic**: Describes the header or title of the notification message.
    - **Message**: Contains the body or content of the notification.

2. **Send Notification**: Publish the JSON message to the MQTT topic specified in the `notificationTopic` setting configured in the `settings.json` file.

3. **Receive Notifications**: Once the message is sent, the application will process it and display the desktop notification accordingly.


### To change the tray icon, follow these steps:

1. **Select Icon**: Choose the desired icon from the available options. The icons are located in the [icons](https://github.com/marcel-domke/mqtt-desktop-notification/tree/master/src/main/resources/icons) directory of the project repository.

2. **Prepare Icon Message**: Create a MQTT message string containing the name of the selected icon. For example, to select `circle-black.png`, pass the string `circle-black` without the `.png` extension.

3. **Send Icon Message**: Publish the icon message string to the MQTT topic specified in the `iconTopic` setting configured in the `settings.json` file.

4. **Receive Confirmation**: Once the message is sent, the tray icon will change to the selected icon, providing visual feedback to the user.




## Development

### Building from Source

To build the application from source, follow these steps:

1. **Clone the repository:**

    ```bash
    git clone https://github.com/marcel-domke/mqtt-desktop-notification.git
    cd mqtt-desktop-notification
    ```

2. **Compile the project** using your preferred Java IDE or build tool (e.g., Maven or Gradle).

3. **Run the application:**

    ```bash
    java -jar target/mqtt-desktop-notification.jar
    ```
## License

This project is licensed under the Apache 2.0 License. See the [LICENSE](LICENSE) file for details.

## Acknowledgements

- [Gson](https://github.com/google/gson) - For handling JSON.
- [Eclipse Paho MQTT](https://www.eclipse.org/paho/) - MQTT client library.

## Contact

For any questions or suggestions, please open an issue.

