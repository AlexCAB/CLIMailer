package main;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import java.io.*;
import java.util.Calendar;
import java.util.Map;
import javax.mail.internet.MimeMultipart;
import org.yaml.snakeyaml.Yaml;


/**
 * Mailer app.
 * Created by CAB on 04.09.2015.
 */

public class Mailer {
    public static void main(String[] args){
        try{
            //Get params
            CLIParameters parameters = new CLIParameters(args);
            //Load destination
            Reader destinationFile = new FileReader(parameters.getOptionValue("d"));
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(destinationFile);
            //Load message and configuration
            Yaml yaml =  new Yaml();
            Map<String,Object> configuration = null;
            if(parameters.getOptionValue("o").equals("s"))
                configuration = (Map<String,Object>) yaml.load(new FileInputStream(parameters.getOptionValue("c")));
            Map<String,Object> message =
                    (Map<String,Object>) yaml.load(new FileInputStream(parameters.getOptionValue("m")));
            //Create helpers
            MailBuilder mailBuilder = new MailBuilder(message);
            MailSender mailSender = null;
            if(parameters.getOptionValue("o").equals("s"))
                mailSender = new MailSender(configuration, message);
            //Get index
            int index;
            if(parameters.getOptionValue("i") == null){
                index = 0;
            }
            else{
                index = Integer.parseInt(parameters.getOptionValue("i")) - 1;
            }
            //Date
            Calendar calendar = Calendar.getInstance();
            String date =
                    calendar.get(Calendar.DAY_OF_MONTH) + "_" +
                    calendar.get(Calendar.MONTH) + "_" +
                    calendar.get(Calendar.YEAR);
            //Build and send or save each message
            for(CSVRecord r : records){
                 if(index > 0){
                     index -= 1; //Skip index
                 }
                 else{
                     if(parameters.getOptionValue("o").equals("s")){
                         MimeMultipart sendContent = mailBuilder.buildMailForSand(r);
                         //Sending
                         mailSender.sendMail(r.get(0), sendContent);
                     }
                     else{
                         //Saving
                         String saveContent = mailBuilder.buildMailForSave(r);
                         String filename = r.get(0).replace("@","_").replace(".","_") + "_" + date + ".eml";
                         PrintWriter out = new PrintWriter(filename);
                         out.println(saveContent);
                         out.close();
                     }
                 }
            }
        }
        catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
