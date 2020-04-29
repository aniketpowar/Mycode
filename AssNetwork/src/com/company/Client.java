package com.company;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.kohsuke.args4j.CmdLineException;
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
        String target = null;
        String content = null;
        JSONObject jsonObjectGET = new JSONObject();
        JSONObject jsonObjectPUT = new JSONObject();
        JSONParser par = new JSONParser();

        try {
            parser.parseArgument(args);
            // print values of the command line options
            System.out.println(values.getHost());
            System.out.println(values.getPort());

            //connect to a server listening on port 4444 on localhost
            socket = new Socket(values.getHost(), values.getPort());
            System.out.println("Client Connected...");

            // Preparing sending and receiving streams
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(
                    socket.getOutputStream());

            // Reading from console
            Scanner cmdin = new Scanner(System.in);
            while (true) {
                String msg = cmdin.nextLine();
                System.out.println(msg);
                // forcing TCP to send data immediately
                StringTokenizer stringTokenizer = new StringTokenizer(msg, " ");
                    while (stringTokenizer.hasMoreTokens()) {
                        String type = stringTokenizer.nextToken();
                        if (type.equals(GET)) {
                            requestType = GET;
                            target = stringTokenizer.nextToken();
                        }
                        else if (type.equals(PUT)) {
                            requestType = PUT;
                            target = stringTokenizer.nextToken();
                        }
                        else if (type.equals(DELETE)) {
                            requestType = DELETE;
                            target = stringTokenizer.nextToken();
                        }
                        else if (type.equals(DISCONNECT)) {
                            requestType = DISCONNECT;
                            target = stringTokenizer.nextToken();
                        }

                    }

                if (requestType.equals(GET)){
                    System.out.println("Inside get");
                    jsonObjectGET.put("message","request");
                    jsonObjectGET.put("type", GET.toUpperCase());
                    jsonObjectGET.put("target", target);
                    out.write((String.valueOf(jsonObjectGET)+ "\n").getBytes("UTF-8"));
                    out.flush();

                    System.out.println("CLIENT reading data");

                    int c;
                    String msg1 = "";
                    do {
                        c = in.read();
                        msg1+=(char)c;
                    } while(in.available()>0);

                    String[] str = msg1.split("}");
                    String response = str[0] + "}";
                    //System.out.println(msg1);

                    JSONObject jsonObject2= (JSONObject) par.parse(response);

                    String reply= (String) jsonObject2.get("content");
                    System.out.println(reply);

                }else if (requestType.equals(PUT)){
                    System.out.println("Inside put");


//                    jsonObjectPUT.put("message","request");
//                    jsonObjectPUT.put("type", PUT.toUpperCase());
//                    jsonObjectPUT.put("target", target);
//                    out.write((String.valueOf(jsonObjectPUT)+ "\n").getBytes("UTF-8"));
//                    out.flush();

                    //TODO
                }else if(requestType.equals(DELETE)){
                    //TODO
                }else if(requestType.equals(DISCONNECT)){
                    //TODO
                }
                else {
                    break;
                }
            }

            cmdin.close();
            in.close();
            out.close();
            socket.close();

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.exit(-1);
        } catch (NullPointerException e){
            System.out.println("null");
        }catch (NoSuchElementException e){
            System.out.println("in null");
        }
    }
}
