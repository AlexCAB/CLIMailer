package main;
import com.sun.mail.util.BASE64EncoderStream;
import org.apache.commons.csv.CSVRecord;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


/**
 * Build a mail of given parameters.
 * Created by CAB on 04.09.2015.
 */

public class MailBuilder {

    //Variables

    private List<MimeBodyPart> attachmentsList = new ArrayList<MimeBodyPart>();
    private String messageTemplate;
    private String from;
    private String subject;
    private String replayTo;

    //Methods

    /**
     * Constructor
     * @param message - message parameters map (from '-m').
     * @throws Exception
     */
    public MailBuilder(Map<String,Object> message) throws Exception {
        //Get message template
        String templatePath = (String)((Map<String, Object>) message.get("content")).get("file");
        messageTemplate = new Scanner(new File(templatePath)).useDelimiter("\\Z").next();
        Map<String, Object> sender = (Map<String, Object>) message.get("sender");
        from = (String) sender.get("email");
        subject = (String) sender.get("subject");
        replayTo = (String) sender.get("replayTo");
        //Get and prepare attachments
        List<String> attachments = (List<String>)message.get("attachments");
        for(String filename : attachments){
            MimeBodyPart attachmentBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(filename);
            attachmentBodyPart.setDataHandler(new DataHandler(source));
            attachmentBodyPart.setFileName(filename);
            attachmentsList.add(attachmentBodyPart);
        }
    }

    /**
     * Build mail for sending.
     * @param record - recipients data line (from '-d')
     * @return - built email.
     * @throws Exception
     */
    public MimeMultipart buildMailForSand(CSVRecord record) throws Exception{
        MimeMultipart multipart = new MimeMultipart();
        //Build and add message
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(buildEmailBody(record), "text/html; charset=utf-8");
        multipart.addBodyPart(messageBodyPart);
        //Add attachment
        for(MimeBodyPart attachment : attachmentsList){
            multipart.addBodyPart(attachment);
        }
        return multipart;
    }

    /**
     * Build mail for saving as '*.eml' file.
     * @param record - recipients data line (from '-d')
     * @return - serialized email.
     * @throws Exception
     */
    public String buildMailForSave(CSVRecord record) throws Exception{
        StringBuilder sb = new StringBuilder();
        //Params
        sb.append("From:      "); sb.append(from); sb.append("\n");
        sb.append("To:        "); sb.append(record.get(0)); sb.append("\n");
        sb.append("Subject:   "); sb.append(subject); sb.append("\n");
        sb.append("Replay to: "); sb.append(replayTo); sb.append("\n");
        sb.append("\n\n");
        //Body
        sb.append(buildEmailBody(record));
        sb.append("\n\n\n");
        //Attachments
        for(MimeBodyPart attachment : attachmentsList){
            OutputStream bos = new ByteArrayOutputStream();
            OutputStream eos = new BASE64EncoderStream(bos);
            attachment.writeTo(eos);
            sb.append("Attachment: "); sb.append(attachment.getFileName()); sb.append("\n");
            sb.append(bos);
            sb.append("\n\n");
        }
        return sb.toString();
    }

    //Functions

    private String buildEmailBody(CSVRecord record){
        StringBuffer sb = new StringBuffer(messageTemplate);
        for(int i = 0; i < record.size(); i += 1){
            String from = "@" + i + "@";
            String to = record.get(i);
            int j = sb.indexOf(from);
            while(j != -1){
                sb.replace(j, j + from.length(), to);
                j += to.length();
                j = sb.indexOf(from, j);
            }
        }
        return sb.toString();
    }
}
