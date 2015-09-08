package main;
import org.dom4j.Element;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


/**
 * Send mails with given configuration.
 * Created by CAB on 04.09.2015.
 */

public class MailSender {

    Properties props;
    Session mailSession;
    String from;
    String replayTo;
    List<Element> ccList;
    List<Element> bccList;
    String subject;

    /**
     * Extract parameters from given configuration and create mail session.
     * @param configuration - XML root element from configuration file.
     */
    public MailSender(final Element configuration){
        //Get mail params
        from  = configuration.element("sender").element("email").getText();
        replayTo = configuration.element("sender").element("replayto").getText();
        ccList = configuration.element("sender").elements("cc");
        bccList = configuration.element("sender").elements("bcc");
        subject = configuration.element("sender").element("subject").getText();
        //Build properties
        props = new Properties();
        props.put("mail.smtp.host", configuration.element("server").element("smtp").getText());
        props.put("mail.smtp.auth", "true");
//        props.put("mail.debug", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", configuration.element("server").element("port").getText());
        props.put("mail.smtp.socketFactory.port", configuration.element("server").element("port").getText());
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        //New session
        mailSession = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        configuration.element("server").element("ui").getText(),
                        configuration.element("server").element("password").getText());
            }
        });
//        mailSession.setDebug(true);
    }

    /**
     * Send one email to the given address with given content.
     * @param address - email address.
     * @param content - email content.
     * @throws Exception
     */
    public void sendMail(String address, Multipart content) throws Exception{
        Message msg = new MimeMessage(mailSession);
        //Set the FROM, TO, DATE and SUBJECT fields
        msg.setFrom(new InternetAddress(from));
        msg.setReplyTo(InternetAddress.parse(replayTo));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(address));
        for(Element cc: ccList){
            System.out.println(cc.getText());
            msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc.getText()));
        }
        for(Element bcc: bccList){
            System.out.println(bcc.getText());
            msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc.getText()));
        }
        msg.setSentDate(new Date());
        msg.setSubject(subject);
        //Create the body of the mail
        msg.setContent(content,"text/html");
//        msg.setContent("<h1>Hello</h1>", "text/html");
        System.out.println(msg.getContentType());
        //Ask the Transport class to send our mail message
        Transport.send( msg );
    }
}
