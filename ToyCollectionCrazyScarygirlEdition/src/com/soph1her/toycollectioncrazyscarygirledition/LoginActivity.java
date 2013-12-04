package com.soph1her.toycollectioncrazyscarygirledition;

import java.io.IOException;
import java.util.Date;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson.JacksonFactory;
import com.soph1her.toycollectioncrazyscarygirledition.toyendpoint.Toyendpoint;
import com.soph1her.toycollectioncrazyscarygirledition.toyendpoint.model.Toy;

import android.content.Context;
import android.content.Intent;
import android.accounts.AccountManager;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	
	private String accountName;
	private GoogleAccountCredential credential;
	
	static final String WEB_CLIENT_ID = "1082565027983-nlr5uhv8oq3vmosfo2ndgjp6iq62q3nf.apps.googleusercontent.com";
	static final int REQUEST_ACCOUNT_PICKER = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);
		
		// AUTH
		credential = credential = GoogleAccountCredential.usingAudience(this,"server:client_id:" + WEB_CLIENT_ID);

		startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	   super.onActivityResult(requestCode, resultCode, data);
	   switch (requestCode) {
	      case REQUEST_ACCOUNT_PICKER:
	         if (data != null && data.getExtras() != null) {

	         accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
	         if (accountName != null) {
	            credential.setSelectedAccountName(accountName);
	            new EndpointsTask().execute(getApplicationContext());
	         }
	         startActivity(new Intent(getBaseContext(), RegisterActivity.class));
	      }
	      break;
	   }
	}
	
 	
	// using for test for end points
	public class EndpointsTask extends AsyncTask<Context, Integer, Long> {
        protected Long doInBackground(Context... contexts) {

               Toyendpoint.Builder endpointBuilder = new Toyendpoint.Builder(
              AndroidHttp.newCompatibleTransport(),
              new JacksonFactory(),
              new HttpRequestInitializer() {
              public void initialize(HttpRequest httpRequest) { }
              });
               Toyendpoint endpoint = CloudEndpointUtils.updateBuilder(
      endpointBuilder).build();
      try {
          Toy toy = new Toy().setDescription("Toy Test");
          String noteID = new Date().toString();
          toy.setId(noteID);

          toy.setDescriptionSet("Set Test");          
          Toy result = endpoint.insertToy(toy).execute();
      } catch (IOException e) {
        e.printStackTrace();
      }
          return (long) 0;
        }
    }
}
