package com.company;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

/**
 * Class with command line options.
 *
 * @author Adel
 *
 */
public class CommandLineValues {

    @Option(name = "-n",usage = "repent n times")
    private int num = -1;

    @Argument(index = 0,required = true,  usage = "Connect keyword")
    private String command;

    //@Option( required = true,  aliases = {"--host"},name = "-h", usage="IP Address")
    //private String host;

    @Argument(index = 1,required = true, usage="IP Address")
    private String host;


    // Give it a default value of 4444 sec
    //@Option(required = true, name = "-i", aliases = {"--port"}, usage="Port Address")
    // private int port = 4444;

    @Argument(index = 2,required = true, usage="Port Address")
    private int port = 4444;


    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }
}

