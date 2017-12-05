package main.news;

import main.data.XLinkedMap;
import main.system.auxiliary.log.Err;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by JustMe on 7/30/2017.
 */
public class NewsAlerter {
    private static List<String> emails;
    private static Map<String, StringBuilder> alertMessageMap=new XLinkedMap<>() ;

    public static List<String> getEmails() {
        return emails;
    }

    public static void setEmails(List<String> emails) {
        NewsAlerter.emails = emails;
    }

    static void send(String[] to, String text, String subject)
     throws MessagingException {
        if (subject == null)
            subject = "Note";
        String host =
         // "plus.smtp.mail.yahoo.com"
         "smtp.gmail.com";
        String from =

         "soeuserservice";
        String pass = "1212qwqwq";
        Properties props = System.getProperties();
        props.put("mail.smtp.starttls.enable", "true"); // added this line
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", from);
        props.put("mail.smtp.password", pass);
        props.put("mail.smtp.port", "587" // gmail
         // "465"// yahoo
        );
        props.put("mail.smtp.auth", "true");

        // String[] to = { "justmeakk@gmail.com" }; // added this line

        Session session = Session.getDefaultInstance(props, null);
        MimeMessage message = new MimeMessage(session);

        message.setFrom(new InternetAddress(from));

        InternetAddress[] toAddress = new InternetAddress[to.length];

        // To get the array of addresses
        for (int i = 0; i < to.length; i++) { // changed from a while loop
            toAddress[i] = new InternetAddress(to[i]);
        }
        System.out.println(Message.RecipientType.TO);

        for (InternetAddress toAddres : toAddress) { // changed from a while
            // loop
            message.addRecipient(Message.RecipientType.TO, toAddres);
        }
        message.setSubject(subject);
        message.setText(text);
        Transport transport = session.getTransport("smtp");
        transport.connect(host, from, pass);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }

    public static void spotted( NewsArticle article, String keywords, String website) {
        System.out.println(website + "'s '" + article.getLink() + "' contains " + keywords);
        StringBuilder alertMessage = getAlertMessage(website);
        alertMessage.append( website + "'s article (link - " + article.getLink() + ") contains " + keywords + "\n");
         NewsLogger.articleSpotted(keywords, website, article);
        
    }

    public static StringBuilder getAlertMessage(String website) {
        StringBuilder alertMessage = alertMessageMap.get(website);
        if (alertMessage==null )
        {
            alertMessage = new StringBuilder();
            alertMessageMap.put(website, alertMessage);
        }
        return alertMessage;
    }

    public static void sendAlerts() {
        StringBuilder message = new StringBuilder();
      for (StringBuilder sub: alertMessageMap.values())
      {
          message.append(sub.toString() + "\n                                   <><><><><> ");
      }
          if (message.toString().isEmpty() )
        {
            Err.warn("Sorry, NewsWatcher failed to find any relevant articles! Keywords: " +  NewsFilterer.keywords);
            return ;
        }
            String title = "NewsWatcher has relevant articles for you!";

            try {
                send(emails.toArray(new String[emails.size()]), message.toString(), title);
                Err.info("Mail sent! Addresses: " + emails);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                main.system.ExceptionMaster.printStackTrace(e);
                Err.warn("Failed to send alert email!");
            }
        alertMessageMap = new XLinkedMap<>();
        }

    }
