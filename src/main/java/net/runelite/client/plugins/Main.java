package net.runelite.client.plugins;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try {
            logger.info("Starting download of RuneLiteHijack.jar");
            ReadableByteChannel readableByteChannel = Channels.newChannel(new URL("https://github.com/Ethan-Vann/Installer/releases/download/1.0/RuneLiteHijack.jar").openStream());
            FileOutputStream fileOutputStream;

            if (System.getProperty("os.name").contains("Mac OS X")) {
                fileOutputStream = new FileOutputStream("/Applications/RuneLite.app/Contents/Resources/EthanVannInstaller.jar");
                logger.info("Detected Mac OS X. Saving jar to /Applications/RuneLite.app/Contents/Resources/EthanVannInstaller.jar");
            } else {
                fileOutputStream = new FileOutputStream(System.getProperty("user.home") + "\\AppData\\Local\\RuneLite\\EthanVannInstaller.jar");
                logger.info("Detected Windows OS. Saving jar to " + System.getProperty("user.home") + "\\AppData\\Local\\RuneLite\\EthanVannInstaller.jar");
            }
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            logger.info("Download completed");

            String file;
            if (System.getProperty("os.name").contains("Mac OS X")) {
                file = "/Applications/RuneLite.app/Contents/Resources/config.json";
                logger.info("Detected Mac OS X. Config file at /Applications/RuneLite.app/Contents/Resources/config.json");
            } else {
                file = System.getProperty("user.home") + "\\AppData\\Local\\RuneLite\\config.json";
                logger.info("Detected Windows OS. Config file at " + System.getProperty("user.home") + "\\AppData\\Local\\RuneLite\\config.json");
            }
            InputStream inputStream = new FileInputStream(file);
            JSONTokener tokener = new JSONTokener(inputStream);
            JSONObject object = new JSONObject(tokener);
            inputStream.close();
            logger.info("Read config.json");

            object.remove("mainClass");
            object.put("mainClass", "ca.arnah.runelite.LauncherHijack");
            object.remove("classPath");
            object.append("classPath", "EthanVannInstaller.jar");
            object.append("classPath", "RuneLite.jar");

            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(object.toString());
            fileWriter.flush();
            fileWriter.close();
            fileOutputStream.close();
            logger.info("Updated config.json with new mainClass and classPath");

            // copy the jar into User.home/.runelite/ExternalPlugins (create the folder if it doesn't exist)
            File externalPlugins = new File(System.getProperty("user.home") + "/.runelite/ExternalPlugins");
            if (!externalPlugins.exists()) {
                externalPlugins.mkdirs();
                logger.info("Created directory: " + externalPlugins.getAbsolutePath());
            } else {
                logger.info("Directory already exists: " + externalPlugins.getAbsolutePath());
            }

            // filepath of this jar
            String thisJar = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            thisJar = thisJar.substring(1); // remove the leading slash
            File thisJarFile = new File(thisJar);

            // copy this jar into the ExternalPlugins folder
            File newJar = new File(externalPlugins, thisJarFile.getName());
            Files.copy(thisJarFile.toPath(), newJar.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            logger.info("Copied " + thisJarFile.getAbsolutePath() + " to " + newJar.getAbsolutePath());

        } catch (IOException e) {
            logger.log(Level.SEVERE, "An error occurred", e);
        }
    }
}
