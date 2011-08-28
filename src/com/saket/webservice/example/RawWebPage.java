package com.saket.webservice.example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RawWebPage extends Activity implements OnClickListener {

	EditText mAddress;
	TextView mHtmlText;
	Button mReadPage;
	String xmlUrl = "http://dl.dropbox.com/u/7215751/JavaCodeGeeks/AndroidFullAppTutorialPart03/Transformers+2007.xml";
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mAddress = (EditText) findViewById(R.id.address);
		mAddress.setText(xmlUrl);

		mReadPage = (Button) findViewById(R.id.ReadWebPage);
		mReadPage.setOnClickListener(this);

		mHtmlText = (TextView) findViewById(R.id.pagetext);

	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.ReadWebPage:

			DownloadWebPageTask task = new DownloadWebPageTask();
			System.out.println(mAddress.getText().toString());
			task.execute(mAddress.getText().toString());
		}
	}

	private class DownloadWebPageTask extends AsyncTask<String, Void, String>{
		private StringBuffer usefulString = new StringBuffer();
		ProgressDialog dialog;
		@Override
		protected String doInBackground(String... urls) {
			System.out.println("URL: "+urls[0]);
			publishProgress();
			try {
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet(urls[0].toString());
				HttpResponse response = client.execute(request);
				BufferedReader rd = new BufferedReader(new InputStreamReader(
						response.getEntity().getContent()));
				System.out.println("Inside");

				if(rd == null){
					System.out.println("RD NULL");
				}else{
					System.out.println("IN ELSE");
					String line = "";
					StringBuffer xmlString = new StringBuffer();
					while ((line = rd.readLine()) != null) {
						System.out.println("INSIDE WHILE");
						//mHtmlText.append(line);
						//Cannot update elements of UI thread in this method
						xmlString.append(line);
						//counter++;
						
						Log.d("WEBSERIVICE", line);
					}
					String xmlData = xmlString.toString();
					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();
					Document doc = db.parse(new InputSource(new StringReader(xmlData)));
					NodeList resultNodes = doc.getElementsByTagName("original_name");
					if(resultNodes == null){
						Toast.makeText(RawWebPage.this, "XML Retrieval Failed", Toast.LENGTH_LONG).show();
					}else{
						//System.out.println(resultNodes.item(0).getNodeValue());
						//Node resultNode = resultNodes.item(0);       
						//NodeList attrsList = resultNodes.getChildNodes();
						//System.out.println(attrsList.getLength());
						for (int i = 0; i < resultNodes.getLength(); i++) {
							//Node node = attrsList.item(i);
							Node node = resultNodes.item(i);
							usefulString.append("\n"+node.getNodeName()+":\t"+node.getTextContent()+"\n");
							//Node firstChild = node.getFirstChild();
							//Node insideFirstChild = firstChild.getFirstChild();
						}
						System.out.println(usefulString);
					}
				}
			} catch (Exception e){
				e.printStackTrace();
				System.out.println("IN CATCH");
			}
			System.out.println("RETURNING FILE");
			return usefulString.toString();
		}

		@Override
		protected void onPostExecute(final String result) {

			if(result == null){
				Toast.makeText(RawWebPage.this, "Result Is Null", Toast.LENGTH_LONG).show();
				dialog.dismiss();
			}else{
				dialog.dismiss();
				AlertDialog.Builder builder = new AlertDialog.Builder(RawWebPage.this);
				builder.setMessage("Downloading Complete")
				.setCancelable(false)
				.setNeutralButton("Ok", new Dialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int arg1) {
					
						dialog.cancel();
						mHtmlText.setText(result);

					}
				});
				AlertDialog alertDialog = builder.create();
				alertDialog.show();
			}
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
		 */
		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			dialog = ProgressDialog.show(RawWebPage.this, "Reading Web Page", "Downloading...", true);
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.setCancelable(true);
		}
	}
}