package net.runelite.client.plugins;

import org.json.JSONObject;
import org.json.JSONTokener;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class Main {
    public static void main(String[] args) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream("RuneBotInstaller.jar");
        String file = "config.json";
        if(args.length != 0) {
            if (args[0].contains("help")) {
                System.out.println("RuneBot-VERSION.jar When ran without args assumes default download dir\n when ran with a arg assumes path to donwload DIR\n  Runebot.jar FILEPATH");
                return;
            }

            if (isUnix()) {
                //unpack appimage
                try {
                    // Extracting the AppImage
                    System.out.println(args[0] + "/RuneLite.AppImage" + " --appimage-extract");
                    Process extractionProcess = Runtime.getRuntime().exec(args[0] + "/RuneLite.AppImage" + " --appimage-extract");
                    extractionProcess.waitFor();

                    // Adding the file to the extraction folder
                    String extractedAppImagePath = args[0] + "/squashfs-root";
                    fileOutputStream = new FileOutputStream(args[0]+"/squashfs-root/RuneBotInstaller.jar");
                    file = args[0] + "/squashfs-root/config.json";

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, String.format("Error1 Occured: %s Contact Staff if that error makes no sense", e),
                            "Installer", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        try {
            ReadableByteChannel readableByteChannel = Channels.newChannel(new URL("https://github.com/KALE1111/runebot/releases/download/v0.0.2/RuneLiteHijack.jar").openStream());


            if (System.getProperty("os.name").contains("Windows")) {
                if(args.length != 0){
                    fileOutputStream = new FileOutputStream(args[0]+"\\RuneBotInstaller.jar");
                    }
                else {
                    fileOutputStream = new FileOutputStream(System.getProperty("user.home") + "\\AppData\\Local\\RuneLite\\RuneBotInstaller.jar");
                    }
                }
            if (System.getProperty("os.name").contains("Mac OS X")) {
                if(args.length != 0){
                    fileOutputStream = new FileOutputStream(args[0]+"/Contents/Resources/RuneBotInstaller.jar");
                }
                else {
                    fileOutputStream = new FileOutputStream("/Applications/RuneLite.app/Contents/Resources/RuneBotInstaller.jar");
                }
            }

            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            //String file = null;
            if (System.getProperty("os.name").contains("Windows")) {
                if(args.length != 0){
                    file = args[0]+"/config.json";
                }
                else {
                    file = System.getProperty("user.home") + "\\AppData\\Local\\RuneLite\\config.json";
                }
            }
            if (System.getProperty("os.name").contains("Mac OS X")) {
                if(args.length != 0){
                    file = args[0]+"/Contents/Resources/config.json";
                }
                else {
                    file = "/Applications/RuneLite.app/Contents/Resources/config.json";
                }
            }


            InputStream inputStream = new FileInputStream(file);
            JSONTokener tokener = new JSONTokener(inputStream);
            JSONObject object = new JSONObject(tokener);
            inputStream.close();
            object.remove("mainClass");
            object.put("mainClass", "ca.arnah.runelite.LauncherHijack");
            object.remove("classPath");
            object.append("classPath", "RuneBotInstaller.jar");
            object.append("classPath", "RuneLite.jar");
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(object.toString());
            fileWriter.flush();
            fileWriter.close();
            fileOutputStream.close();

        }
        catch(Exception e) {
            JOptionPane.showMessageDialog(null, String.format("Error Occured2: %s Contact Staff if that error makes no sense",e),
                    "Installer", JOptionPane.ERROR_MESSAGE);
        }

        try{
        if(args.length != 0){
            if (isUnix()) {
                //pack appimage
                System.out.println(args[0]+ "/appimagetool-x86_64.AppImage " +args[0]+ "/squashfs-root" + " RuneBot.AppImage");
                Process extractionProcess = Runtime.getRuntime().exec(args[0]+ "/appimagetool-x86_64.AppImage " +args[0]+ "/squashfs-root" + " RuneBot.AppImage");
                extractionProcess.waitFor();
            }
        }}catch(Exception e){
            JOptionPane.showMessageDialog(null, String.format("Error Occured3: %s Contact Staff if that error makes no sense",e),
                    "Installer", JOptionPane.ERROR_MESSAGE);
        }

        JOptionPane.showMessageDialog(null, "Installed successfully, Please place this .jar in .runelite/sideloaded-plugins",
                "Installer", JOptionPane.PLAIN_MESSAGE);
        System.exit(1);
    }

    private static String OS = System.getProperty("os.name").toLowerCase();
    public static boolean isUnix() {
        return (OS.indexOf("nix") >= 0
                || OS.indexOf("nux") >= 0
                || OS.indexOf("aix") > 0);
    }


}
