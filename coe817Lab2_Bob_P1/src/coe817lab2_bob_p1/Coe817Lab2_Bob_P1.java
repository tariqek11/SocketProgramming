/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coe817lab2_bob_p1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author tariq
 */
public class Coe817Lab2_Bob_P1 {

    private static final String ID = "Bob";
    private static final String NB = "128";
    private static final String KAB = "0945367491274536";
    private static String ID_Alice;
    private static String NA;
    private static int count = 0;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java EchoServer <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);

        try (
                ServerSocket serverSocket
                = new ServerSocket(Integer.parseInt(args[0]));
                Socket clientSocket = serverSocket.accept();
                PrintWriter out
                = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (count == 0) {
                    System.out.println(inputLine);

                    String parts[] = inputLine.split(" ");
                    ID_Alice = parts[0];
                    NA = parts[1];

                    try {
                        byte[] encryptedBytes = encrypt(ID + " " + NA);
                        String encryptedmsg = Base64.getEncoder().encodeToString(encryptedBytes);
                        out.println(NB + " " + encryptedmsg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    count++;
                } else if (count == 1) {
                    System.out.println(inputLine);
                    
                    try {
                        byte[] encryptedBytes = Base64.getDecoder().decode(inputLine);
                        String decryptedmsg = decrypt(encryptedBytes);
                        System.out.println(decryptedmsg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
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
