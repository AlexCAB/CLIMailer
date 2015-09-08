package main;
import com.sun.mail.util.BASE64EncoderStream;
import org.apache.commons.csv.CSVRecord;
import org.dom4j.Element;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/**
 * Build a mail of given parameters.
 * Created by CAB on 04.09.2015.
 */


public class MailBuilder {

    List<MimeBodyPart> attachmentsList = new ArrayList<MimeBodyPart>();
    String messageBody;
    String subject;


    public MailBuilder(Element messageFile, Element configuration) throws Exception {
        //Get message body and subject
        String bodyFilename = messageFile.element("body").getText();
        messageBody = new String(Files.readAllBytes(Paths.get(bodyFilename)));
        subject = configuration.element("sender").element("subject").getText();
        //Get and prepare attachments
        List<Element> elements = messageFile.elements("attachment");
        for(Element attachment : elements){
            String filename = attachment.element("file").getText();
            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(filename);
            attachmentBodyPart.setDataHandler(new DataHandler(source));
            attachmentBodyPart.setFileName(filename);
            attachmentsList.add(attachmentBodyPart);
        }
    }


    public Multipart buildMailForSand(CSVRecord record) throws Exception{
        Multipart multipart = new MimeMultipart();
        //Build and add message
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setHeader("Content-Type", "text/html");
        messageBodyPart.setText(buildEmailBody(record));
        multipart.addBodyPart(messageBodyPart);
        //Add attachment
        for(MimeBodyPart attachment : attachmentsList){
            multipart.addBodyPart(attachment);
        }

        return multipart;
    }


    public String buildMailForSave(CSVRecord record) throws Exception{
        //Body
        StringBuilder sb = new StringBuilder(buildEmailBody(record));
        sb.append("\n\n");
        //Attachments
        for(MimeBodyPart attachment : attachmentsList){
            OutputStream bos = new ByteArrayOutputStream();
            OutputStream eos = new BASE64EncoderStream(bos);
            attachment.writeTo(eos);
            sb.append(bos);
            sb.append("\n\n");
        }
        return sb.toString();
    }

    private String buildEmailBody(CSVRecord record){
        StringBuilder sb = new StringBuilder();
//        sb.append("<html>\n");
//        sb.append("  <head>\n");
//        sb.append("    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n");
//        sb.append("    <title>"); sb.append(subject); sb.append("</title>\n");
//        sb.append("  </head>\n");
//        sb.append("  <body>\n");
//        sb.append(record.get(0)); //salutation
//        sb.append(" ");
//        sb.append(record.get(1)); //name
//        sb.append(" ");
//        sb.append(record.get(2)); //surname
//        sb.append("\n");
//        sb.append(record.get(3)); //email
//        sb.append("\n");
//        sb.append(record.get(4)); //company
//        sb.append(", ");
//        sb.append(record.get(5)); //address
//        sb.append(" ");
//        sb.append(record.get(6)); //zip
//        sb.append(" ");
//        sb.append(record.get(7)); //city
//        sb.append(" ");
//        sb.append(record.get(8)); //state
//        sb.append("\n");
        sb.append(messageBody);
//        sb.append("  </body>\n");
//        sb.append("</html>");
        return sb.toString();
    }
}
