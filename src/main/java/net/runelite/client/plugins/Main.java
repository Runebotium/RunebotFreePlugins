package net.runelite.client.plugins;

import org.json.JSONObject;
import org.json.JSONTokener;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    // Downloads a file from the specified URL and saves it to the specified file path using NIO
    private static void downloadUsingNIO(String urlStr, String filePath) throws IOException {
        URL url = new URL(urlStr);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(filePath);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }

    public static void main(String[] args) {
        try {
            // Download RuneLiteHijack.jar
            String hijackJarUrl = "https://github.com/Arnuh/RuneLiteHijack/releases/download/latest/RuneLiteHijack.jar";
            String hijackJarPath = "RuneLiteHijack.jar";
            downloadUsingNIO(hijackJarUrl, hijackJarPath);

            // Default file path for RuneBotInstaller.jar
            String runeBotJarPath = "RuneBotInstaller.jar";

            // Check the operating system
            String osName = System.getProperty("os.name");
            if (osName.contains("Windows")) {
                runeBotJarPath = System.getProperty("user.home") + "\\AppData\\Local\\RuneLite\\RuneBotInstaller.jar";
            } else if (osName.contains("Mac OS X")) {
                runeBotJarPath = "/Applications/RuneLite.app/Contents/Resources/RuneBotInstaller.jar";
            }

            // Check if the RuneBotInstaller.jar file already exists
            File runeBotFile = new File(runeBotJarPath);
            if (!runeBotFile.exists()) {
                // Download RuneBotInstaller.jar from the GitHub release
                String runeBotUrl = "https://github.com/KALE1111/rblaunch/releases/download/v0.3.8/RuneBot-0.3.8.jar";
                downloadUsingNIO(runeBotUrl, runeBotJarPath);
            }

            // Read and update the config.json file
            String configFilePath = "";
            if (osName.contains("Windows")) {
                configFilePath = System.getProperty("user.home") + "\\AppData\\Local\\RuneLite\\config.json";
            } else if (osName.contains("Mac OS X")) {
                configFilePath = "/Applications/RuneLite.app/Contents/Resources/config.json";
            }
            InputStream inputStream = new FileInputStream(configFilePath);
            JSONTokener tokener = new JSONTokener(inputStream);
            JSONObject object = new JSONObject(tokener);
            inputStream.close();
            // Update the mainClass and classPath properties in the config.json file
            object.remove("mainClass");
            object.put("mainClass", "ca.arnah.runelite.LauncherHijack");
            object.remove("classPath");
            object.append("classPath", "RuneBotInstaller.jar");
            object.append("classPath", "RuneLite.jar");
            FileWriter fileWriter = new FileWriter(configFilePath);
            fileWriter.write(object.toString());
            fileWriter.flush();
            fileWriter.close();

            // Display success message
            JOptionPane.showMessageDialog(null, "Installed successfully. Please launch RuneLite normally.",
                    "Installer", JOptionPane.PLAIN_MESSAGE);
            System.exit(0);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, String.format("Error occurred: %s", e.getMessage()),
                    "Installer", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            System.exit(1);
        }
    }
}
