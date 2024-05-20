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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Objects;

@SuppressWarnings("Convert2Lambda")
public class TrayController {

    private TrayIcon trayIcon;
    private SystemTray tray;
    private final Configuration config;

    public TrayController(Configuration newConfig) {
        config = newConfig;
        SwingUtilities.invokeLater(this::createAndShowGUI);
    }

    private void createAndShowGUI() {
        if (!SystemTray.isSupported()) {
            JOptionPane.showMessageDialog(null, "Error");
            return;
        }

        PopupMenu popup = new PopupMenu();
        trayIcon = new TrayIcon(Objects.requireNonNull(createImage("/icons/info-blue.png")));
        tray = SystemTray.getSystemTray();

        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip(config.get("name"));

        MenuItem exitItem = new MenuItem("Close");
        popup.add(exitItem);

        trayIcon.setPopupMenu(popup);
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            JOptionPane.showMessageDialog(null, "Error");
            return;
        }

        trayIcon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, config.get("name") + "\nVersion: " + config.get("version") + "\n\n" + config.get("githubUrl") + "\n" + config.get("copyright"));
            }
        });

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tray.remove(trayIcon);
                System.exit(0);
            }
        });
    }

    protected static Image createImage(String path) {
        URL imageURL = Main.class.getResource(path);
        if (imageURL == null) {
            System.err.println("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, "icon")).getImage();
        }
    }

    public void notification(String flag, String topic, String msg) {
        switch (flag) {
            case "none":
                trayIcon.displayMessage(topic, msg, TrayIcon.MessageType.NONE);
                break;
            case "info":
                trayIcon.displayMessage(topic, msg, TrayIcon.MessageType.INFO);
                break;
            case "warning":
                trayIcon.displayMessage(topic, msg, TrayIcon.MessageType.WARNING);
                break;
            case "error":
                trayIcon.displayMessage(topic, msg, TrayIcon.MessageType.ERROR);
                break;
            default:
                System.err.println("Unknown flag");
        }
        if (flag.equals("error")) {
            JOptionPane.showMessageDialog(null, msg, topic, JOptionPane.ERROR_MESSAGE);
        }
    }

    public void icon(String icon) {
        try{
            trayIcon.setImage(createImage("/icons/" + icon + ".png"));
        } catch (Exception e) {
            System.err.println("Invalid icon");
        }

    }
}