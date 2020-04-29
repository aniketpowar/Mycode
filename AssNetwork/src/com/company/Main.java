//package com.company;
//
//
//import org.kohsuke.args4j.CmdLineException;
//import org.kohsuke.args4j.CmdLineParser;
//
///**
// * Demonstrates how to parse command line args with args4j
// *
// * @author Adel
// *
// */
//public class Main {
//
//    /**
//     * @param args
//     */
//    public static void main(String[] args) {
//        CommandLineValues values = new CommandLineValues();
//        CmdLineParser parser = new CmdLineParser(values);
//
//        try{
//            // parse the command line options with the args4j library
//            parser.parseArgument(args);
//            // print values of the command line options
//            System.out.println(values.getHost());
//            System.out.println(values.getPort());
//
//        } catch (CmdLineException e) {
//            System.err.println(e.getMessage());
//            parser.printUsage(System.err);
//            System.exit(-1);
//        }
//
//    }
//
//}
