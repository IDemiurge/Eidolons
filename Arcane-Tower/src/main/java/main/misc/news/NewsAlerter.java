package main.misc.news;

import main.system.auxiliary.log.Err;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;

/**
 * Created by JustMe on 7/30/2017.
 */
public class NewsAlerter {
    private static List<String> emails;

    public static void setEmails(List<String> emails) {
        NewsAlerter.emails = emails;
    }

    public static List<String> getEmails() {
        return emails;
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

    public static void alert(NewsArticle article, String keywords, String website) {
        System.out.println(website+"'s '" +article.getLink()+"' contains " + keywords);
// emails.toArray(emails)
    }
        public static boolean sendAlert(String name,
                                                      String password, String mail, String code) {

            String text = "Username: " + name + "\nPassword: " + password
             + "\n" + "Your confirmation code is: ";
            String title = "Confirm SoE-Net account registration";

            text += code;
            try {
                send(new String[] { mail }, text, title);
            } catch (MessagingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Err.warn("Failed to send confirmation email!");
                return false;
            }
            System.out.println("CONFIRM MAIL SENT!");
            return true;

    }

}
