/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coe817lab2_alice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 *
 * @author tariq
 */
public class Coe817Lab2_Alice_P1 {

    private static final String ID = "Alice";
    private static final String NA = "64";
    private static final String KAB = "0945367491274536";
    private static String ID_Bob;
    private static String NB;

    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println(
                    "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
                Socket echoSocket = new Socket(hostName, portNumber);
                PrintWriter out
                = new PrintWriter(echoSocket.getOutputStream(), true);
                BufferedReader in
                = new BufferedReader(
                        new InputStreamReader(echoSocket.getInputStream()));
                BufferedReader stdIn
                = new BufferedReader(
                        new InputStreamReader(System.in))) {

            String userInput;
            String response;

            userInput = ID + " " + NA;
            out.println(userInput);

            while ((response = in.readLine()) != null) {
                System.out.println(response);
                try {
                    String[] parts = response.split(" ");
                    byte[] encryptedBytes2 = Base64.getDecoder().decode(parts[1]);
                    String decryptedmsg = decrypt(encryptedBytes2);

                    NB = parts[0];
                    parts = decryptedmsg.split(" ");
                    ID_Bob = parts[0];
                    System.out.println(NB + " " + parts[0] + " " + parts[1]);


                    if (parts[1].equals(NA)) {
                        byte[] encryptedBytes = encrypt(ID + " " + NB);
                        String encryptedmsg = Base64.getEncoder().encodeToString(encryptedBytes);
                        out.println(encryptedmsg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to "
                    + hostName);
            System.exit(1);
        }
    }

    public static byte[] encrypt(String message) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(KAB.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return cipher.doFinal(message.getBytes("UTF-8"));
    }

    public static String decrypt(byte[] encryptedMessage) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(KAB.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decryptedBytes = cipher.doFinal(encryptedMessage);
        return new String(decryptedBytes, "UTF-8");
    }
}
