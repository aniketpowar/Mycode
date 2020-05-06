package com.company;

import java.io.*;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.kohsuke.args4j.CmdLineParser;



public class Client {

    public static void main(String[] args) {

        CommandLineValues values = new CommandLineValues();
        CmdLineParser parser = new CmdLineParser(values);
        Socket socket = null;
        String GET = "get";
        String PUT = "put";
        String DELETE = "delete";
        String DISCONNECT = "disconnect";
        Boolean flag = false;
        String requestType = null;
        String temp = null;
        String target = null;
        String connect = "connect";
        String ip = null;
        String port = null;

        String content = null;
        JSONObject jsonObjectGET = new JSONObject();
        JSONObject jsonObjectPUT = new JSONObject();
        JSONParser par = new JSONParser();
        String fileRelativePath = (new File("").getAbsolutePath());
        String pathOfFile = fileRelativePath + "/www";
        try {
            Scanner cmdin1 = new Scanner(System.in);
            String msg12 = cmdin1.nextLine();
            StringTokenizer stringTokenizer1 = new StringTokenizer(msg12, " ");
            while (stringTokenizer1.hasMoreTokens()) {
                String type = stringTokenizer1.nextToken();
                if (type.equals(connect)) {
//                  requestType = GET;
                    ip = stringTokenizer1.nextToken();
                    port = stringTokenizer1.nextToken();
                    socket = new Socket(ip, Integer.parseInt(port));
                    System.out.println("Client Connected...");
                } else {
                    main(null);
                }
            }


            // Preparing sending and receiving streams
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            // Reading from console
            Scanner cmdin = new Scanner(System.in);
            while (true) {
                String msg = cmdin.nextLine();
                //System.out.println(msg);
                // forcing TCP to send data immediately
                StringTokenizer stringTokenizer = new StringTokenizer(msg, " ");
                try {
                    while (stringTokenizer.hasMoreTokens()) {
                        String type = stringTokenizer.nextToken();
                        if (type.equals(GET)) {
                            requestType = GET;
                            target = stringTokenizer.nextToken();
                        } else if (type.equals(PUT)) {
                            requestType = PUT;
                            temp = stringTokenizer.nextToken();
                            target = stringTokenizer.nextToken();
                        } else if (type.equals(DELETE)) {
                            requestType = DELETE;
                            target = stringTokenizer.nextToken();
                        } else if (type.equals(DISCONNECT)) {
                            requestType = DISCONNECT;
                            //target = stringTokenizer.nextToken();
                        }
                    }
                    if (requestType.equals(GET)) {
                        jsonObjectGET.put("message", "request");
                        jsonObjectGET.put("type", GET.toUpperCase());
                        jsonObjectGET.put("target", target);
                        out.write((String.valueOf(jsonObjectGET) + "\n").getBytes("UTF-8"));
                        out.flush();

//                            System.out.println("CLIENT reading data");
                        int c;
                        String msg1 = "";
                        do {
                            c = in.read();
                            msg1 += (char) c;
                        } while (in.available() > 0);

                        String[] str = msg1.split("}");
                        String response = str[0] + "}";
                        //System.out.println(msg1);

                        JSONObject jsonObject2 = (JSONObject) par.parse(response);

                        String reply = (String) jsonObject2.get("content");
                        System.out.println(reply);

                    } else if (requestType.equals(PUT)) {
                        String getPath = pathOfFile + temp;
                        boolean flag1 = Files.exists(Paths.get(getPath));
                        if (flag1) {
//                            System.out.println("File exist");
                            FileReader read = new FileReader(getPath);
                            Scanner scanFile = new Scanner(read);
                            String content1 = "";
                            while (scanFile.hasNextLine()) {
                                content1 = content1 + scanFile.nextLine();
                            }
                            //System.out.println(content1);
                            jsonObjectPUT.put("message", "request");
                            jsonObjectPUT.put("type", PUT.toUpperCase());
                            jsonObjectPUT.put("target", target);
                            jsonObjectPUT.put("content", content1);
                            out.write((String.valueOf(jsonObjectPUT) + "\n").getBytes("UTF-8"));
                            out.flush();
                            int c;
                            String msg1 = "";
                            do {
                                c = in.read();
                                msg1 += (char) c;
                            } while (in.available() > 0);
                            String[] tempStr = msg1.split("}");
                            String serverReply = tempStr[0] + "}";
                            //System.out.println(msg1);
                            JSONObject jsonObject2 = (JSONObject) par.parse(serverReply);
                            String reply = (String) jsonObject2.get("content");
                            System.out.println(reply);
                        } else System.out.println("Source file not found.");
                    } else if (requestType.equals(DELETE)) {

                        jsonObjectGET.put("message", "request");
                        jsonObjectGET.put("type", DELETE.toUpperCase());
                        jsonObjectGET.put("target", target);
                        out.write((String.valueOf(jsonObjectGET) + "\n").getBytes("UTF-8"));
                        out.flush();

                        int c;
                        String msg1 = "";
                        do {
                            c = in.read();
                            msg1 += (char) c;
                        } while (in.available() > 0);
                        String[] tempStr = msg1.split("}");
                        String serverReply = tempStr[0] + "}";

                        JSONObject jsonObject2 = (JSONObject) par.parse(serverReply);
                        String reply = (String) jsonObject2.get("content");
                        System.out.println(reply);

                    } else if (requestType.equals(DISCONNECT)) {
                        jsonObjectGET.put("message", "request");
                        jsonObjectGET.put("type", DISCONNECT.toUpperCase());
                        System.out.println("Successfully disconnected");
                        out.write((String.valueOf(jsonObjectGET) + "\n").getBytes("UTF-8"));
                        out.flush();
                        in.close();
                        out.close();
                        socket.close();
                    } else {
                        break;
                    }
                } catch (NoSuchElementException e) {
                    continue;
                }
            }
            cmdin.close();
            in.close();
            out.close();
            socket.close();
        } catch (NoRouteToHostException | ConnectException e){
            System.out.println("No server");
            main(null);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            System.out.println("null");
        }catch (NoSuchElementException e){
            System.out.println("in null");
        }
    }
}

//192.168.1.105.