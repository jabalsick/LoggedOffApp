package com.fdv.loggedoff.Activtys;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.fdv.loggedoff.Model.Person;
import com.fdv.loggedoff.R;
import com.firebase.client.Firebase;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public abstract class BaseActivity extends AppCompatActivity {

    private static final String username = "masajes.fdv@gmail.com";
    private static final String password = "fdv123456";
    static protected Person mUser;
    public static String DEFAULT_PHOTO ="DEFAULT";
    public static Firebase userRef  = new Firebase("https://loggedoffapp.firebaseio.com/users");;

    public static Person getmUser() {
        return mUser;
    }

    public static void setmUser(Person mUser) {
        BaseActivity.mUser = mUser;
    }

    /**
     * Starts and Activity set it up in the intent adding the page animation to the transition.
     * @param intent    intent holding the activity to start.
     */
    public void nextScreen(Intent intent){
        startActivity(intent);
      //  overridePendingTransition (R.anim.open_next, R.anim.close_main);
    }


    /**
     * Redefine to add page effect to the back transition.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
       // overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }



    public void startAnimation( int resId, int anim) {
        View view = findViewById(resId);
        Animation animation = AnimationUtils.loadAnimation(this, anim);
        view.startAnimation(animation);
        view.setVisibility(View.VISIBLE);
    }

    public void startAnimation( View view, int anim) {
        Animation animation = AnimationUtils.loadAnimation(this, anim);
        view.startAnimation(animation);
        view.setVisibility(View.VISIBLE);
    }




   //  * Closes the pop up

    public void closePopUp() {
        ViewGroup currentLayout = (ViewGroup) getWindow().getDecorView().getRootView();
        View pop_up = findViewById(R.id.pop_up);
        currentLayout.removeView(pop_up);
    }

    public void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

//EMAIL IMPLEMENTATION
    public void sendMail(String email, String subject, String messageBody) {
        Session session = createSessionObject();

        try {
            Message message = createMessage(email, subject, messageBody, session);
            new SendMailTask().execute(message);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private Message createMessage(String email, String subject, String messageBody, Session session) throws MessagingException, UnsupportedEncodingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username, "[MASAJES]"));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email, email));
        message.setSubject(subject);
        message.setText(messageBody);
        return message;
    }

    private Session createSessionObject() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        return Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    private class SendMailTask extends AsyncTask<Message, Void, Void> {
     //   private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
         // progressDialog = ProgressDialog.show(BaseActivity.this, "Please wait", "Sending mail", true, false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
         //   progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static Firebase getUserRef() {
        return userRef;
    }

    public static void setUserRef(Firebase userRef) {
        BaseActivity.userRef = userRef;
    }
}
