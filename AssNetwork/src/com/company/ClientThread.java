package com.company;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.io.*;

import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class ClientThread implements Runnable {
    private Socket socket;

    public ClientThread(Socket socket) {
        this.socket = socket;
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
//                    String msg = in.readUTF();
                    System.out.println("Server reading data");

                    int c;
                    String msg = "";
                    do {
                        c = in.read();
                        msg+=(char)c;
                    } while(in.available()>0);
                    System.out.println("read byte string is  \n"+msg);
                    JSONObject jsonObjectGETResponse = new JSONObject();
                    JSONParser parser = new JSONParser();
                    JSONObject object = (JSONObject) parser.parse(msg);
                    String type = (String) object.get("type");
                    if (type.equals("GET")){
                        String target = (String) object.get("target");
                        try{
                            String getPath = pathOfFile + target;
                            FileReader read = new FileReader(getPath);
                            Scanner scanFile = new Scanner(read);
                            String tempLine = "";
                            while(scanFile.hasNextLine())
                            {
                                tempLine = tempLine + scanFile.nextLine();
                            }
                            System.out.println(tempLine);
                            jsonObjectGETResponse.put("message", "response");
                            jsonObjectGETResponse.put("code", "200");
                            jsonObjectGETResponse.put("content",  tempLine);
                            out.write((String.valueOf(jsonObjectGETResponse)+ "\n").getBytes("UTF-8"));
                            out.flush();
                            scanFile.close();
                        } catch (FileNotFoundException e) {
                            System.out.println("File Not Found");
                            jsonObjectGETResponse.put("message", "response");
                            jsonObjectGETResponse.put("code", "400");
                            jsonObjectGETResponse.put("content",  "Not Found");
                            out.write((String.valueOf(jsonObjectGETResponse)+ "\n").getBytes("UTF-8"));
                            out.flush();
                            //e.printStackTrace();
                        }
                    }
//                    else if(type.equals("PUT")){
//                        System.out.println("Inside put");
//                    }

                    out.writeUTF(msg);
                    out.flush();

                    if (msg.equals("end"))
                        break;
                }
                in.close();
                out.close();
                socket.close();
            } catch (EOFException e) {
                if (socket != null)
                    socket.close();
                System.out.println("Client disconnected.");
            } //catch (ParseException e) {
            catch (ParseException e) {
                e.printStackTrace();
            }
            //e.printStackTrace();
            //}
        } catch (IOException e) {
            e.printStackTrace();
        }

        //A thread finishes if run method finishes
    }




}



// String finalname =  name.substring(4);
//  /Users/aniketpowar/IdeaProjects/AssNetwork/src/index.html
//                    String directory = "/Users/aniketpowar/IdeaPrjects/AssNetwork/";
//                    File folder = new File(directory);
//
//                    if (folder.isDirectory()) {
//                        File[] listOfFiles = folder.listFiles();
//                        if (listOfFiles.length < 1)
//                            System.out.println(
//                                    "There is no File inside Folder");
//                        else System.out.println("List of Files & Folder");
//                        for (File file : listOfFiles) {
//                            if(!file.isDirectory()) {
//                                System.out.println(file.getCanonicalPath().toString());
//                                BufferedReader inp = new BufferedReader(new FileReader("/Users/aniketpowar/IdeaProjects/AssNetwork/src/index1.html"));
//                                String line;
//                                while ((line = inp.readLine()) != null) {
//                                    //System.out.println(line);
//                                }
//                                inp.close();
//                            }
//
//
//                        }
//                    }
//                    else System.out .println("There is no Folder @ given path :" + directory);
