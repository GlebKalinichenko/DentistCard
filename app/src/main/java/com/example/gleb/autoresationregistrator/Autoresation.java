package com.example.gleb.autoresationregistrator;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.gleb.dentistcard.R;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

/**
 * Created by gleb on 23.07.15.
 */
public class Autoresation extends ActionBarActivity {
    public static final String TAG = "TAG";
    public EditText emailEditText;
    public EditText passwordEditText;
    public Button autoresationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mail_autoresation);

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        autoresationButton = (Button) findViewById(R.id.autoresationButton);

        autoresationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new Loader("pop.yandex.ru", "pop3", emailEditText.getText().toString(), passwordEditText.getText().toString()).execute();
                Intent intent = new Intent(Autoresation.this, AutoresationMail.class);
                intent.putExtra(AutoresationMail.EMAIL, emailEditText.getText().toString());
                intent.putExtra(AutoresationMail.PASSWORD, passwordEditText.getText().toString());
                startActivity(intent);
            }
        });
    }

    public class Loader extends AsyncTask<String, String, String[]> {
        public String pop3Host;
        public String storeType;
        public String user;
        public String password;

        public Loader(String pop3Host, String storeType, String user, String password) {
            this.pop3Host = pop3Host;
            this.storeType = storeType;
            this.user = user;
            this.password = password;
        }

        @Override
        protected String[] doInBackground(String... params) {
            Properties props = new Properties();
            props.put("mail.smtp.port", 993);
            props.put("mail.smtp.socketFactory.port", 993);
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");
            props.setProperty("mail.store.protocol", "imaps");
            try {
                Session session = Session.getInstance(props, null);
                Store store = session.getStore();
                store.connect("imap.yandex.ru", "Makbluming@yandex.ua", "0954023873");
                Folder inbox = store.getFolder("INBOX");
                inbox.open(Folder.READ_ONLY);
//                int count = inbox.getMessageCount();
//                Message[] messages = inbox.getMessages(1, count);
//                for (Message message : messages) {
//                    if (!message.getFlags().contains(Flags.Flag.SEEN)) {
//                        Address[] fromAddresses = message.getFrom();
//                        System.out.println("...................");
//                        System.out.println("\t From: "
//                                + fromAddresses[0].toString());
//                        System.out.println("\t To: "
//                                + parseAddresses(message
//                                .getRecipients(Message.RecipientType.TO)));
//                        System.out.println("\t CC: "
//                                + parseAddresses(message
//                                .getRecipients(Message.RecipientType.CC)));
//                        System.out.println("\t Subject: "
//                                + message.getSubject());
//                        System.out.println("\t Sent Date:"
//                                + message.getSentDate().toString());
//                        try {
//                            System.out.println(message.getContent().toString());
//                        } catch (Exception ex) {
//                            System.out.println("Error reading content!!");
//                            ex.printStackTrace();
//                        }
//                    }
//
//                }
                //Message msg = inbox.getMessage(inbox.getMessageCount());

                FlagTerm ft = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
                Message messages[] = inbox.search(ft);

                for (Message msg : messages) {
                    Address[] in = msg.getFrom();
                    for (Address address : in) {
                        System.out.println("FROM:" + address.toString());
                    }

                    Object content = msg.getContent();
                    if (content instanceof String) {
                        String body = (String) content;
                        Log.d(TAG, "SENT DATE: " + msg.getSentDate());
                        Log.d(TAG, "SUBJECT: " + msg.getSubject());
                        Log.d(TAG, "Content " + body);
                    } else if (content instanceof Multipart) {
                        Multipart mp = (Multipart) content;
                        BodyPart bp = mp.getBodyPart(0);
//                String mp = (String) msg.getContent();
                        Log.d(TAG, "SENT DATE: " + msg.getSentDate());
                        Log.d(TAG, "SUBJECT: " + msg.getSubject());
                        Log.d(TAG, "Content " + bp.getContent());

                    }
                }

//                Multipart mp = (Multipart) msg.getContent();
//                BodyPart bp = mp.getBodyPart(0);
////                String mp = (String) msg.getContent();
//                System.out.println("SENT DATE: " + msg.getSentDate());
//                System.out.println("SUBJECT: " + msg.getSubject());
//                Log.d(TAG, "Content " + msg.getContent().toString());
////                System.out.println("CONTENT: " + mp);
            } catch (Exception mex) {
                mex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] value) {

        }
    }
}
