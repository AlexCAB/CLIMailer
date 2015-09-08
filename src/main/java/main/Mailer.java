package main;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import java.io.*;
import java.util.Calendar;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import javax.mail.Multipart;


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
            SAXReader reader = new SAXReader();
            Document messageDocument = reader.read(parameters.getOptionValue("m"));
            Element message = messageDocument.getRootElement();
            Document configurationDocument = reader.read(parameters.getOptionValue("c"));
            Element configuration = configurationDocument.getRootElement();
            //Create helpers
            MailBuilder mailBuilder = new MailBuilder(message, configuration);
            MailSender mailSender = new MailSender(configuration);
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
                         Multipart sendContent = mailBuilder.buildMailForSand(r);
                         //Sending
                         mailSender.sendMail(r.get(3), sendContent);
                     }
                     else{
                         //Saving
                         String saveContent = mailBuilder.buildMailForSave(r);
                         String filename = r.get(1) + "_" + r.get(2) + "_" + date + ".eml";
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
