package philgabardo.stacker;


import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.leaderboard.Leaderboards.SubmitScoreResult;
import com.google.example.games.basegameutils.BaseGameActivity;

import android.os.Bundle;
import android.os.Handler;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

public class LoserScreen extends BaseGameActivity  implements View.OnClickListener {
	
	// Scores
	int score;
	int highscore;
	
	//ui components
	LinearLayout[] llend = new LinearLayout[5]; //background panels (black and blue)
	final Handler myHandler = new Handler();
	boolean backgroundPicker = true;
	Toast outOfLivesToast;
	Toast highScoreToast;
	Typeface seasrn;
	
	//preference keys for saving high score
	String myPreferenceKey = "myPreferenceKey";
	String highScoreKey = "myHighScoreKey";
	private InterstitialAd interstitial;
	
	
	//retrieve high score from preferences
	private int getHighScore(){
		SharedPreferences prefs = this.getSharedPreferences(myPreferenceKey, Context.MODE_PRIVATE);
    	int highscore = prefs.getInt(highScoreKey, 0); 
		return highscore;
	}
	
	//set high score in preferences (only if score is > high score)
	private void setHighScore(int score){
		SharedPreferences prefs = this.getSharedPreferences(myPreferenceKey, Context.MODE_PRIVATE);
    	int highscore = prefs.getInt(highScoreKey, 0); 
    	if(highscore < score){
        	Editor editor = prefs.edit();
        	editor.putInt(highScoreKey, score);
        	editor.commit();
    	}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loser);
		Bundle extras = getIntent().getExtras();
		
		if(extras !=null)
		{
		     score = Integer.parseInt(extras.getString("score"));
		}
		
		//dont attempt to sign in more than once (annoying!)
		getGameHelper().setMaxAutoSignInAttempts(1);
		
		
		//Initialize ui components
		highScoreToast = new Toast(getBaseContext());
		seasrn = Typeface.createFromAsset(getAssets(), "fonts/seasrn.ttf");
		outOfLivesToast = Toast.makeText(getBaseContext(),"Done", Toast.LENGTH_LONG);
        outOfLivesToast.setGravity(Gravity.TOP, outOfLivesToast.getXOffset() / 2, outOfLivesToast.getYOffset() / 2);
        TextView textView = new TextView(getBaseContext());
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(30);
        textView.setText("Out of lives!");
        textView.setTypeface(seasrn);
        textView.setPadding(10, 10, 10, 10);
        outOfLivesToast.setView(textView);
        outOfLivesToast.show();
        TextView highScoreLabel = (TextView) findViewById(R.id.high_score_label);
        highScoreLabel.setTextColor(Color.WHITE);
        highScoreLabel.setTypeface(seasrn);
        highScoreLabel.bringToFront();
        Button leaderboard = (Button) findViewById(R.id.show_leaderboard);
        leaderboard.setTypeface(seasrn);
        leaderboard.bringToFront();
        leaderboard.setTextColor(Color.WHITE);
        Button restart = (Button) findViewById(R.id.restart);
        restart.setTypeface(seasrn);
        restart.bringToFront();
        restart.setTextColor(Color.WHITE);
        llend[0] = (LinearLayout) findViewById(R.id.llend1);
		llend[1] = (LinearLayout) findViewById(R.id.llend2);
		llend[2] = (LinearLayout) findViewById(R.id.llend3);
		llend[3] = (LinearLayout) findViewById(R.id.llend4);
		llend[4] = (LinearLayout) findViewById(R.id.llend5);
        
        
        //handle high score
    	highscore = getHighScore(); 
    	
    	//new high score
        if(highscore < score){
        	setHighScore(score);
        	
        	//display toast
	        highScoreToast = Toast.makeText(getBaseContext(),"Done", Toast.LENGTH_LONG);
	        highScoreToast.setGravity(Gravity.BOTTOM, highScoreToast.getXOffset() / 2, highScoreToast.getYOffset() / 2);
		    TextView textViewHS = new TextView(getBaseContext());
		    textViewHS.setTextColor(Color.RED);
		    textViewHS.setTextSize(30);
		    textViewHS.setText("New high score!");
		    textViewHS.setTypeface(seasrn);
		    textViewHS.setPadding(10, 10, 10, 10);
		    highScoreToast.setView(textViewHS);
		    highScoreToast.show();
		    highScoreLabel.setText("High Score: " + score);
        }
        else{
        	highScoreLabel.setText("High Score: " + highscore);
         }
        
        
        // view leaderboard
        leaderboard.setOnClickListener(new OnClickListener() {

	         @Override
	         public void onClick(View v) {
	        	 outOfLivesToast.cancel();
	        	 highScoreToast.cancel();
	        	 if(isSignedIn()){
	        		 Games.Leaderboards.submitScoreImmediate(getApiClient(),
	        	                getString(R.string.number_guesses_leaderboard),
	        	                score).setResultCallback(new myLeaderBoardSubmitScoreCallback());
	        	 }
	        	 else{
	        		 beginUserInitiatedSignIn();
	        	 }
	        	 
	         }

	      });
        
        // restart game
        restart.setOnClickListener(new OnClickListener() {

	         @Override
	         public void onClick(View v) {
	        	 outOfLivesToast.cancel();
	        	 highScoreToast.cancel();
	        	 Intent intent = new Intent(getBaseContext(), Stacker.class); 
	        	 intent.putExtra("score", "0");
	        	 intent.putExtra("lives", "3");
	        	 intent.putExtra("level", "1");
	        	 startActivity(intent);
	         }

	      });
		
		// Prepare the Interstitial Ad
		interstitial = new InterstitialAd(LoserScreen.this);
		// Insert the Ad Unit ID
		interstitial.setAdUnitId("ca-app-pub-1710469862664561/6930249531");
		 
		//Locate the Banner Ad in activity_main.xml
		AdView adView = (AdView) this.findViewById(R.id.adViewLoser);
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
		
		
		Timer myTimer = new Timer();
	      myTimer.schedule(new TimerTask() {
	         @Override
	         public void run() {UpdateGUI();}
	      }, 0, 500);
		
		
		
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {

	    if(keyCode == KeyEvent.KEYCODE_BACK)
	    {
	    		outOfLivesToast.cancel();
	    		highScoreToast.cancel();
	            moveTaskToBack(true);
	            return true;
	    }
		return false;

	}
	
	// submit score on sign in
	public void onSignInSucceeded() {
		Games.Leaderboards.submitScoreImmediate(getApiClient(),
                getString(R.string.number_guesses_leaderboard),
                score);
	}
	 
	@Override
	public void onSignInFailed() {
		
	}

	@Override
	public void onClick(View v) {
		
	}
	
	private void UpdateGUI() {
		  backgroundPicker = !backgroundPicker;
	      
	      //tv.setText(String.valueOf(i));
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

	//background scheme 1
	private void setOrientation2() {
		llend[0].setBackgroundColor(Color.BLUE);
		llend[1].setBackgroundColor(Color.BLACK);
		llend[2].setBackgroundColor(Color.BLUE);
		llend[3].setBackgroundColor(Color.BLACK);
		llend[4].setBackgroundColor(Color.BLUE);
		
	}
	
	//background scheme 2
	private void setOrientation1() {
		llend[0].setBackgroundColor(Color.BLACK);
		llend[1].setBackgroundColor(Color.BLUE);
		llend[2].setBackgroundColor(Color.BLACK);
		llend[3].setBackgroundColor(Color.BLUE);
		llend[4].setBackgroundColor(Color.BLACK);
		
	}
	
	
	//display leaderboard on score submission successfuly
	class myLeaderBoardSubmitScoreCallback implements ResultCallback<SubmitScoreResult> {
        @Override
        public void onResult(SubmitScoreResult res) {
            if (res.getStatus().getStatusCode() == 0) {
            	startActivityForResult(Games.Leaderboards.getLeaderboardIntent(
            	        getApiClient(), getString(R.string.number_guesses_leaderboard)),
            	        2);
            }
        }
    }
	
	public void displayInterstitial() {
		// If Ads are loaded, show Interstitial else show nothing.
		if (interstitial.isLoaded()) {
		interstitial.show();
		}
		}
	
}