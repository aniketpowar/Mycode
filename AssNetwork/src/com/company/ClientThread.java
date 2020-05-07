package com.company;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

import java.net.Socket;
import java.net.SocketException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ClientThread implements Runnable {
    private Socket socket;

    public ClientThread(Socket socket) {
        this.socket = socket;
    }
    public boolean validate(String target){
        String regularEx = "^\\/((\\w{1,20}\\/){0,10})\\w{1,10}\\.\\w{1,5}$";
        Pattern pattern_regular = Pattern.compile(regularEx);
        boolean is_Matching = Pattern.matches(regularEx,target);
        return  is_Matching;
    }

    @Override
    public void run() {
        try {
            DataInputStream in;
            in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            String fileRelativePath = (new File("").getAbsolutePath());
            String pathOfFile = fileRelativePath + "/www";
            File Root = new File(pathOfFile);
            boolean present = Root.exists();

            if (present == false){
                File file = new File(pathOfFile);
                file.mkdir();
            }
            try {
                while (true) {
                    int c;
                    String msg = "";
                    do {
                        c = in.read();
                        msg+=(char)c;
                    } while(in.available()>0);

                    //System.out.println("read byte string is  \n"+msg);
                    JSONObject jsonObjectGETResponse = new JSONObject();
                    JSONParser parser = new JSONParser();

                    JSONObject object = (JSONObject) parser.parse(msg);

                    String type = (String) object.get("type");
                    if (type.equals("GET")) {
                        try {
                            boolean check = validate((String) object.get("target"));
                            if (check == true) {
                                String target = (String) object.get("target");
                                try {
                                    String getPath = pathOfFile + target;
                                    FileReader read = new FileReader(getPath);
                                    Scanner scanFile = new Scanner(read);
                                    String tempLine = "";
                                    while (scanFile.hasNextLine()) {
                                        tempLine = tempLine + scanFile.nextLine();
                                    }
                                    System.out.println(tempLine);
                                    jsonObjectGETResponse.put("message", "response");
                                    jsonObjectGETResponse.put("code", "200");
                                    jsonObjectGETResponse.put("content", tempLine);
                                    out.write((String.valueOf(jsonObjectGETResponse) + "\n").getBytes("UTF-8"));
                                    out.flush();
                                    scanFile.close();

                                } catch (FileNotFoundException e) {
                                    System.out.println("File Not Found");
                                    jsonObjectGETResponse.put("message", "response");
                                    jsonObjectGETResponse.put("code", "400");
                                    jsonObjectGETResponse.put("content", "Not Found");
                                    out.write((String.valueOf(jsonObjectGETResponse) + "\n").getBytes("UTF-8"));
                                    out.flush();
                                    //e.printStackTrace();
                                }
                            }else {
                                jsonObjectGETResponse.put("message", "response");
                                jsonObjectGETResponse.put("code", "401");
                                jsonObjectGETResponse.put("content", "Bad Request");
                                out.write((String.valueOf(jsonObjectGETResponse) + "\n").getBytes("UTF-8"));
                                out.flush();
                            }
                        }
                       catch (NullPointerException e){
                                continue;
                            }
                        }
                    else if(type.equals("PUT")) {

                        String target = (String) object.get("target");
                        boolean check = validate((String) object.get("target"));
                        if (check == true) {
                            String content2 = (String) object.get("content");
                            String getPath = pathOfFile + target;
                            boolean flag1 = Files.exists(Paths.get(getPath));
                            if (flag1) {
                                System.out.println("Target File exist");
                                //FileReader read = new FileReader(getPath);
                                FileWriter writer = new FileWriter(getPath);
                                writer.write(content2);
                                jsonObjectGETResponse.put("message", "response");
                                jsonObjectGETResponse.put("code", "202");
                                jsonObjectGETResponse.put("content", "Modified");
                                out.write((String.valueOf(jsonObjectGETResponse) + "\n").getBytes("UTF-8"));
                                out.flush();
                                writer.flush();
                                writer.close();
                            } else {
                                System.out.println("Target Doesn't exist");
                                System.out.println(getPath);
                                File file12 = new File(getPath);
                                file12.getParentFile().mkdirs();
                                FileWriter writer = new FileWriter(file12);
                                writer.write(content2);
                                jsonObjectGETResponse.put("message", "response");
                                jsonObjectGETResponse.put("code", "201");
                                jsonObjectGETResponse.put("content", "ok");
                                out.write((String.valueOf(jsonObjectGETResponse) + "\n").getBytes("UTF-8"));
                                out.flush();
                                writer.flush();
                                writer.close();
                            }
                        }
                        else {
                            jsonObjectGETResponse.put("message", "response");
                            jsonObjectGETResponse.put("code", "401");
                            jsonObjectGETResponse.put("content", "Bad Request");
                            out.write((String.valueOf(jsonObjectGETResponse) + "\n").getBytes("UTF-8"));
                            out.flush();
                        }

                    }

                    else if(type.equals("DELETE")) {
                        String target = (String) object.get("target");
//                        boolean check = validate((String) object.get("target"));
//                        if (check == true) {

                                String getPath = pathOfFile + target;
                                //FileReader read1 = new FileReader(getPath);
                                File f = new File(getPath);
                                if(f.isDirectory()) {
                                    String[] arr = f.list();
                                    if (arr.length > 0) {
                                        jsonObjectGETResponse.put("message", "response");
                                        jsonObjectGETResponse.put("code", "402");
                                        jsonObjectGETResponse.put("content", "Unknown Error");
                                        out.write((String.valueOf(jsonObjectGETResponse) + "\n").getBytes("UTF-8"));
                                        out.flush();

                                    }
                                    else {
                                        f.delete();
                                        jsonObjectGETResponse.put("message", "response");
                                        jsonObjectGETResponse.put("code", "203");
                                        jsonObjectGETResponse.put("content", "ok");
                                        out.write((String.valueOf(jsonObjectGETResponse) + "\n").getBytes("UTF-8"));
                                        out.flush();
                                    }
                                }
                                else if (f.delete()){
                                    jsonObjectGETResponse.put("message", "response");
                                    jsonObjectGETResponse.put("code", "203");
                                    jsonObjectGETResponse.put("content", "ok");
                                    out.write((String.valueOf(jsonObjectGETResponse) + "\n").getBytes("UTF-8"));
                                    out.flush();
                                }
                                else {
                                    jsonObjectGETResponse.put("message", "response");
                                    jsonObjectGETResponse.put("code", "400");
                                    jsonObjectGETResponse.put("content", "Not Found");
                                    out.write((String.valueOf(jsonObjectGETResponse) + "\n").getBytes("UTF-8"));
                                    out.flush();

                                }

                        }
//                        else {
//                            jsonObjectGETResponse.put("message", "response");
//                            jsonObjectGETResponse.put("code", "401");
//                            jsonObjectGETResponse.put("content", "Bad Request");
//                            out.write((String.valueOf(jsonObjectGETResponse) + "\n").getBytes("UTF-8"));
//                            out.flush();
//                        }
//                    }
                    else if(type.equals("DISCONNECT")){
                        in.close();
//                        socket.shutdownInput();
//                        socket.shutdownOutput();
                        out.close();
                        socket.close();
                        break;
                    }


                }
                in.close();
                out.close();
                socket.close();
            }
            catch (EOFException e) {
                if (socket != null)
                    socket.close();
                System.out.println("Client disconnected.");
            } //catch (ParseException e) {
            catch (ParseException e) {
                e.printStackTrace();
            }
            //e.printStackTrace();
            //}
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //A thread finishes if run method finishes
    }
}

