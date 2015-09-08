package main;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


/**
 * Send mails with given configuration.
 * Created by CAB on 04.09.2015.
 */

public class MailSender {

    //Variables

    private Session mailSession;
    private String from;
    private String subject;
    private String replayTo;
    private List<String> ccList;
    List<String> bccList;


    /**
     * Extract parameters from given configuration and create mail session.
     * @param configuration - XML root element from configuration file.
     */
    public MailSender(final Map<String,Object> configuration, final Map<String,Object> message) throws Exception{
        //Get mail params
        Map<String, Object> sender = (Map<String, Object>) message.get("sender");
        if(sender == null){throw new Exception("Not found 'sender' section in the message file");}
        from = (String) sender.get("email");
        subject = (String) sender.get("subject");
        replayTo = (String) sender.get("replayTo");
        ccList = (List<String>) sender.get("cc");
        if(ccList == null){ccList = new ArrayList<String>();}
        bccList = (List<String>) sender.get("bcc");
        if(bccList == null){bccList = new ArrayList<String>();}
        //Get server params
        Map<String, Object> server = (Map<String, Object>) configuration.get("server");
        if(server == null){throw new Exception("Not found 'server' section in the configuration file");}
        String host = (String) server.get("host");
        Integer port = (Integer) server.get("port");
        final String user = (String) server.get("user");
        final String passw = (String) server.get("passw");
        if(host == null || port == null || user == null || passw == null){
            throw new Exception("Not found one ore more params from 'server' section in the configuration file:" +
                    " host = " + host + ", port = " + port + ", user = " + user + ", passw = " + passw);}
        //Build properties
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.auth", "true");
        props.put("mail.debug", "false");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", port.toString());
        props.put("mail.smtp.socketFactory.port", port.toString());
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        //New session
        mailSession = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, passw);
            }
        });
        mailSession.setDebug(false);
    }

    //Methods


    /**
     * Send one email to the given address with given content.
     * @param address - email address.
     * @param content - email content.
     * @throws Exception
     */
    public void sendMail(String address, MimeMultipart content) throws Exception{
        MimeMessage msg = new MimeMessage(mailSession);
        //Set the FROM, TO, DATE and SUBJECT fields
        msg.setFrom(new InternetAddress(from));
        msg.setReplyTo(InternetAddress.parse(replayTo));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(address));
        for(String cc: ccList){
            msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc));
        }
        for(String bcc: bccList){
            msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc));
        }
        msg.setSentDate(new Date());
        msg.setSubject(subject);
        //Create the body of the mail
        msg.setContent(content, "text/html; charset=utf-8");
        //Ask the Transport class to send
        Transport.send(msg);
    }
}
