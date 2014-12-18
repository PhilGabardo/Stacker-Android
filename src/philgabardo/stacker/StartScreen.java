package philgabardo.stacker;

import java.util.Timer;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StartScreen extends Activity {
	
	// handler for updating ui
	final Handler myHandler = new Handler();
	
	// boolean for choosing background scheme
	boolean backgroundPicker = true;
	
	// custom typefaces
	Typeface grapple;
	Typeface seasrn;

	// other ui components
	TextView clickanywhere;
	TextView stackerlogo;
	RelativeLayout llstartMain;
	LinearLayout[] llstart = new LinearLayout[5]; // background panels (black and blue)
	
	private InterstitialAd interstitial;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		//initialize ui components
		llstart[0] = (LinearLayout) findViewById(R.id.llstart1);
		llstart[1] = (LinearLayout) findViewById(R.id.llstart2);
		llstart[2] = (LinearLayout) findViewById(R.id.llstart3);
		llstart[3] = (LinearLayout) findViewById(R.id.llstart4);
		llstart[4] = (LinearLayout) findViewById(R.id.llstart5);
		grapple = Typeface.createFromAsset(getAssets(), "fonts/grapple.ttf");
		seasrn = Typeface.createFromAsset(getAssets(), "fonts/seasrn.ttf");
		clickanywhere = (TextView) findViewById(R.id.clickanywhere);
		clickanywhere.setTypeface(seasrn);
		clickanywhere.setTextColor(Color.WHITE);
		stackerlogo = (TextView) findViewById(R.id.stackerlogo);
		stackerlogo.setTypeface(grapple);
		stackerlogo.setTextColor(Color.WHITE);
		clickanywhere.bringToFront();
		stackerlogo.bringToFront();
		llstartMain = (RelativeLayout) findViewById(R.id.startGame);
		llstartMain.setOnClickListener(new OnClickListener() {
	
		         @Override
		         public void onClick(View v) {
		        	 Intent intent = new Intent(getBaseContext(), Stacker.class); 
		        	 intent.putExtra("level", "1");
		        	 intent.putExtra("lives", "3");
		        	 intent.putExtra("score", "0");
		        	 startActivity(intent);
		         }
	
		      });
	    
	        // start timer for updating ui
	       Timer myTimer = new Timer();
		myTimer.schedule(new TimerTask() {
	         @Override
	         public void run() {UpdateGUI();}
	        }, 0, 500);
		
		
		// Prepare the Interstitial Ad
		interstitial = new InterstitialAd(StartScreen.this);
		// Insert the Ad Unit ID
		interstitial.setAdUnitId("<PRIVATE>");
		 
		//Locate the Banner Ad in activity_main.xml
		AdView adView = (AdView) this.findViewById(R.id.adView);
		adView.bringToFront();
		 
		// Request for Ads
		AdRequest adRequest = new AdRequest.Builder().build();
		 
		// Load ads into Banner Ads
		adView.loadAd(adRequest);
		 
		// Load ads into Interstitial Ads
		interstitial.loadAd(adRequest);
		 
		// Prepare an Interstitial Ad Listener
		interstitial.setAdListener(new AdListener() {
		public void onAdLoaded() {
		// Call displayInterstitial() function
		displayInterstitial();
		}
		});
		   
		
	}
	
	private void UpdateGUI() {
		  //switch between background schemes
		  backgroundPicker = !backgroundPicker;
	      myHandler.post(myRunnable);
	   }

	   final Runnable myRunnable = new Runnable() {
	      public void run() {
	    	  
			  if(backgroundPicker){
				  setOrientation1();
			  }
			  else{
				  setOrientation2();
			  }
	      }

	   };
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {

	    if(keyCode == KeyEvent.KEYCODE_BACK)
	    {
	            moveTaskToBack(true);
	            return true;
	    }
		return false;
	}
	
	
	// background scheme 1
	private void setOrientation2() {
		llstart[0].setBackgroundColor(Color.BLUE);
		llstart[1].setBackgroundColor(Color.BLACK);
		llstart[2].setBackgroundColor(Color.BLUE);
		llstart[3].setBackgroundColor(Color.BLACK);
		llstart[4].setBackgroundColor(Color.BLUE);
		
	}
	
	//background scheme 2
	private void setOrientation1() {
		llstart[0].setBackgroundColor(Color.BLACK);
		llstart[1].setBackgroundColor(Color.BLUE);
		llstart[2].setBackgroundColor(Color.BLACK);
		llstart[3].setBackgroundColor(Color.BLUE);
		llstart[4].setBackgroundColor(Color.BLACK);
		
	}
	
	// convenient method for setting background
	@SuppressLint("NewApi")
	private void setBackground(LinearLayout ll, int png){
		int sdk = android.os.Build.VERSION.SDK_INT;
		if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			ll.setBackgroundDrawable( getResources().getDrawable(png) );
		} else {
			ll.setBackground( getResources().getDrawable(png));
		}
	}
	
	private void displayInterstitial() {
		// If Ads are loaded, show Interstitial else show nothing.
		if (interstitial.isLoaded()) {
		interstitial.show();
		}
   }
}



