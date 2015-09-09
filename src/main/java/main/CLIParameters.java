package main;
import org.apache.commons.cli.*;

import java.io.File;


/**
 * Parsing, checking of CLI parameters
 * Created by CAB on 04.09.2015.
 */

public class CLIParameters {

    //Variables

    private Options options;
    private CommandLine cmd;
    private String usage = "[java -jar target/mailer.jar|mailer] [OPTIONS]";
    private HelpFormatter helpFormatter = new HelpFormatter();

    /**
     * Parse and check given args.
     * @param args - arguments from main method.
     * @throws Exception - on parsing error or incorrect parameter.
     */
    public CLIParameters(String[] args) throws Exception{
        //Options
        options = buildOptions();
        //If command line is empty print help
        if(args.length == 0){
            helpFormatter.printHelp(usage, options);
            System.exit(-1);
        }
        //Parse options and check
        CommandLineParser parser = new DefaultParser();
        cmd = parser.parse(options, args);
        checkOptions();
    }

    //Methods

    /**
     * Get command line value.
     * @param key - command key.
     * @return - command value.
     */
    public String getOptionValue(String key){
        return cmd.getOptionValue(key);
    }

    //Functions

    private Options buildOptions(){
        Options options = new Options();
        options.addOption("o", "output", true,
                "If “s” the tool sends emails. If “d” the tool saves for every recipient a eml file (mandatory).");
        options.addOption("d", "destination", true,
                "Path to the file containing the list of recipients (mandatory).");
        options.addOption("i", "index", true,
                "The first recipient to start from N. Default = 1");
        options.addOption("c", "configuration", true,
                "Path to the file containing the configuration info (mandatory).");
        options.addOption("m", "message", true,
                "Path to the file containing the message information (mandatory).");
        options.addOption("h", "help", false,
                "Print this message.");
        return options;
    }

    private void checkOptions() throws IllegalArgumentException{
        //If help print and exit
        if(cmd.hasOption("h")){
            helpFormatter.printHelp(usage, options);
            System.exit(0);
        }
        //Output
        String output = cmd.getOptionValue("o");
        if(output == null || !(output.equals("s") ||  output.equals("d")))
            throw new IllegalArgumentException("No or incorrect -output argument.");
        //Destination
        String destination = cmd.getOptionValue("d");
        if(destination == null)
            throw new IllegalArgumentException("No or incorrect -destination argument.");
        File destinationFile = new File(destination);
        if(! destinationFile.exists() || destinationFile.isDirectory())
            throw new IllegalArgumentException("File '" + destination + "' not exists or is directory.");
        //Destination
        String configuration  = cmd.getOptionValue("c");
        if(configuration == null && output.equals("s"))
            throw new IllegalArgumentException("No or incorrect -configuration argument.");
        if(output.equals("s")) {
            File configurationFile = new File(configuration);
            if (!configurationFile.exists() || configurationFile.isDirectory())
                throw new IllegalArgumentException("File '" + configuration + "' not exists or is directory.");
        }
        //Message
        String message = cmd.getOptionValue("m");
        if(message == null)
            throw new IllegalArgumentException("No or incorrect -message argument.");
        File messageFile = new File(message);
        if(! messageFile.exists() || messageFile.isDirectory())
            throw new IllegalArgumentException("File '" + message + "' not exists or is directory.");
    }
}
