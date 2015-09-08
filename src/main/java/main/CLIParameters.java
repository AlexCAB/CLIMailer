package main;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import java.io.File;


/**
 * Parsing, checking of CLI parameters
 * Created by CAB on 04.09.2015.
 */

public class CLIParameters {

    private CommandLine cmd;

    /**
     * Parse and check given args.
     * @param args - arguments from main method.
     * @throws Exception - on parsing error or incorrect parameter.
     */
    public CLIParameters(String[] args) throws Exception{
        Options options = buildOptions();
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
                "If “s” the tool sends emails. If “d” the tool saves for every recipient a eml file.");
        options.addOption("d", "destination", true,
                "Path to the file containing the list of recipients");
        options.addOption("i", "index", true,
                "The first recipient to start from N. Default = 1");
        options.addOption("c", "configuration", true,
                "Path to the file containing the configuration info.");
        options.addOption("m", "message", true,
                "Path to the file containing the message information.");
        return options;
    }

    private void checkOptions() throws IllegalArgumentException{
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
        if(configuration == null)
            throw new IllegalArgumentException("No or incorrect -configuration argument.");
        File configurationFile = new File(configuration);
        if(! configurationFile.exists() || configurationFile.isDirectory())
            throw new IllegalArgumentException("File '" + configuration + "' not exists or is directory.");
        //Message
        String message = cmd.getOptionValue("m");
        if(message == null)
            throw new IllegalArgumentException("No or incorrect -message argument.");
        File messageFile = new File(message);
        if(! messageFile.exists() || messageFile.isDirectory())
            throw new IllegalArgumentException("File '" + message + "' not exists or is directory.");
    }
}
