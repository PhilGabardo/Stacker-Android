package philgabardo.stacker;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Stacker extends Activity {
	
	//handler for updating ui
	final Handler myHandler = new Handler();
	
	//block position pointers
	int rowPoint = 5;
	int colPoint = 0;
	
	//chosen column index (-1 by default, changes when user selects a column)
	int chosenIndex = -1;
	
	
	//timer for updating ui
	Timer myTimer;
	
	//ui components
	static TextView livesLabel;
	static TextView scoreLabel;
	TimerTask GUIUpdate;
	Toast powerUpToast;
	Toast levelToast;
	LinearLayout llMain;
	LinearLayout[][] ll = new LinearLayout[6][5]; //These layouts are the blocks!
	Typeface myTypeface;
	
	//game components (self explanatory)
	static int level;
	static int lives;
	static int score;
	boolean directionRight = true;
	boolean pause = false;
	PowerUp powerUp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stacker);
		
		//Retrieve game data
		Bundle extras = getIntent().getExtras();
		if(extras !=null)
		{
		     level = Integer.parseInt(extras.getString("level"));
		     lives = Integer.parseInt(extras.getString("lives"));
		     score = Integer.parseInt(extras.getString("score"));
		}
		
		
		// Initialize UI components
		livesLabel = (TextView) findViewById(R.id.livesLabel);
		livesLabel.setTextColor(Color.WHITE);
		myTypeface = Typeface.createFromAsset(getAssets(), "fonts/seasrn.ttf");
		livesLabel.setTypeface(myTypeface);
		scoreLabel = (TextView) findViewById(R.id.scoreLabel);
		scoreLabel.setTextColor(Color.WHITE);
		scoreLabel.setTypeface(myTypeface);
		powerUpToast = new Toast(getBaseContext());
		levelToast = Toast.makeText(getBaseContext(),"Level", Toast.LENGTH_LONG);
        levelToast.setGravity(Gravity.CENTER, levelToast.getXOffset() / 2, levelToast.getYOffset() / 2);
        TextView textView = new TextView(getBaseContext());
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(30);
        textView.setText("LEVEL " + level);
        textView.setTypeface(myTypeface);
        textView.setPadding(10, 10, 10, 10);
        levelToast.setView(textView);
        levelToast.show();
		ll[0][0] = (LinearLayout) findViewById(R.id.ll1);
		ll[0][1] = (LinearLayout) findViewById(R.id.ll2);
		ll[0][2] = (LinearLayout) findViewById(R.id.ll3);
		ll[0][3] = (LinearLayout) findViewById(R.id.ll4);
		ll[0][4] = (LinearLayout) findViewById(R.id.ll5);
		ll[1][0] = (LinearLayout) findViewById(R.id.ll6);
		ll[1][1] = (LinearLayout) findViewById(R.id.ll7);
		ll[1][2] = (LinearLayout) findViewById(R.id.ll8);
		ll[1][3] = (LinearLayout) findViewById(R.id.ll9);
		ll[1][4] = (LinearLayout) findViewById(R.id.ll10);
		ll[2][0] = (LinearLayout) findViewById(R.id.ll11);
		ll[2][1] = (LinearLayout) findViewById(R.id.ll12);
		ll[2][2] = (LinearLayout) findViewById(R.id.ll13);
		ll[2][3] = (LinearLayout) findViewById(R.id.ll14);
		ll[2][4] = (LinearLayout) findViewById(R.id.ll15);
		ll[3][0] = (LinearLayout) findViewById(R.id.ll16);
		ll[3][1] = (LinearLayout) findViewById(R.id.ll17);
		ll[3][2] = (LinearLayout) findViewById(R.id.ll18);
		ll[3][3] = (LinearLayout) findViewById(R.id.ll19);
		ll[3][4] = (LinearLayout) findViewById(R.id.ll20);
		ll[4][0] = (LinearLayout) findViewById(R.id.ll21);
		ll[4][1] = (LinearLayout) findViewById(R.id.ll22);
		ll[4][2] = (LinearLayout) findViewById(R.id.ll23);
		ll[4][3] = (LinearLayout) findViewById(R.id.ll24);
		ll[4][4] = (LinearLayout) findViewById(R.id.ll25);
		ll[5][0] = (LinearLayout) findViewById(R.id.ll26);
		ll[5][1] = (LinearLayout) findViewById(R.id.ll27);
		ll[5][2] = (LinearLayout) findViewById(R.id.ll28);
		ll[5][3] = (LinearLayout) findViewById(R.id.ll29);
		ll[5][4] = (LinearLayout) findViewById(R.id.ll30);
		
		
		//set lives and score
		setLives();
		setScore();
		
		
		//generate powerup
		powerUp = new PowerUp(new ImageView(this));
		ll[powerUp.row][powerUp.column].addView(powerUp.icon);
		
		//initialize blocks
		for(int i = 0; i < ll.length; i++){
			for(int j = 0; j < ll[i].length; j++){
				setBackground(ll[i][j], R.drawable.normalblock);
			}
		}
		
		//setup timer for moving the blocks (blocks moves every -90*log(level)+300 seconds
		myTimer = new Timer();
		
		myTimer.schedule(new TimerTask() {
		       @Override
		       public void run() {UpdateGUI();}
		    }, 0, (long) ((long) -90*Math.log(level)+300));
	      
	    llMain = (LinearLayout) findViewById(R.id.ll);
	    
	    //click handler (if block stacks, move to next row or level, otherwise, start from bottom or go to loser screen)
	    llMain.setOnClickListener(new OnClickListener() {

	         @Override
	         public void onClick(View v) {
	        	 
	        	 //dont update ui on pause
	        	 pause = true;
	        	 
	        	 //column index unchosen
	        	 if(chosenIndex == -1){
	        		 chosenIndex = colPoint;
	        		 
	        		 //colour block above chosen block(seems smoother)
	        		 setBackground(ll[rowPoint-1][chosenIndex],R.drawable.highlightedblock);
	        		 
	        		 //if power up clicked, set invisible
	        		 if(checkPowerUp()){
		        		 powerUp.icon.setVisibility(View.GONE);
		        	 }
	        		 
	        		 //recolor row
	        		 for(int i = 0 ; i < ll[rowPoint].length; i++){
    					 if(i==chosenIndex){
    						 setBackground(ll[rowPoint][i],R.drawable.highlightedblock);
    					 }
    					 else{
    						 setBackground(ll[rowPoint][i],R.drawable.normalblock);
    					 }
    				 }
	        		 
	        		 //move to next row
	        		 rowPoint--;
	        		 
	        		 
    	
	        	 }
	        	 else{
	        		 
	        		 // if power up clicked, set invisible
	        		 if(checkPowerUp()){
	        			 powerUp.icon.setVisibility(View.GONE);
		        	 }
	        		 
	        		 // block stacked
	        		 if(colPoint==chosenIndex){
	        			 //next level
	        			 if(rowPoint == 0){
	        				 //stop ui updater and cancel toasts, then move to next level
	        				 myTimer.cancel();
	        				 myTimer.purge();
	        				 powerUpToast.cancel();
	        				 levelToast.cancel();
	        				 Intent intent = new Intent(getBaseContext(), Stacker.class); 
	        	        	 intent.putExtra("level", ""+(level+1)+"");
	        	        	 intent.putExtra("lives", ""+lives+"");
	        	        	 intent.putExtra("score", ""+(score+100)+"");
	        	        	 startActivity(intent);
	        			 }
	        			 //next row
	        			 else{
	        				//colour block above chosen block(seems smoother)
	        				 setBackground(ll[rowPoint-1][chosenIndex],R.drawable.highlightedblock);
	        				 
	        				//recolor row
	        				 for(int i = 0 ; i < ll[rowPoint].length; i++){
	        					 if(i==chosenIndex){
	        						 setBackground(ll[rowPoint][i],R.drawable.highlightedblock);
	        					 }
	        					 else{
	        						 setBackground(ll[rowPoint][i],R.drawable.normalblock);
	        					 }
	        				 }
	        				 //next row
	        				 rowPoint--;
	        			 }
	        		 }
	        		 else{
	        			 
	        			 //reset blocks and start from row 1
	        			 for(int i = 0; i < ll.length; i++){
	        					for(int j = 0; j < ll[i].length; j++){
	        						setBackground(ll[i][j], R.drawable.normalblock);
	        					}
	        				}
	        			 rowPoint = 5;
	        			 chosenIndex = -1;
	        			 lives = lives - 1;
	        			 
	        			 // if out of lives, go to loser screen
	        			 if(lives == 0){
	        				 myTimer.cancel();
	        				 myTimer.purge();
	        				 powerUpToast.cancel();
	        				 levelToast.cancel();
	        				 Intent intent = new Intent(getBaseContext(), LoserScreen.class); 
	        				 intent.putExtra("score", ""+score+"");
	        	        	 startActivity(intent);
	        			 }
	        			 setLives();
	        		 }
	        	 }
	        	 //ui can update now
	        	 pause = false;
	        	 
	         }

	      });
		
	}
	

	private void UpdateGUI() {
		  if(!pause){
			  
			  // move left if at 5th column to the right (out of 5 columns)
			  if(colPoint==4){
				  directionRight = false;
			  }
			  
			  //move right if at 1st column to the right
			  if(colPoint==0){
				  directionRight = true;
			  }
		   
			  if(directionRight){
				  colPoint++;
			  }
			  else{
				  colPoint--;
			  }
	      
			  myHandler.post(myRunnable);
		  }
	   }

	   final Runnable myRunnable = new Runnable() {
	      public void run() {
	    	  
	    	 
	    	  if(!pause){
	    		  //update blocks
		    	  if(directionRight){
		    		  setBackground(ll[rowPoint][colPoint], R.drawable.highlightedblock);
		    		  setBackground(ll[rowPoint][colPoint-1], R.drawable.normalblock);
		    	  }
		    	  else{
		    		  setBackground(ll[rowPoint][colPoint], R.drawable.highlightedblock);
		    		  setBackground(ll[rowPoint][colPoint+1], R.drawable.normalblock);
		    	  }
	    	  }
			  
			  
	      }
	   };

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.stacker, menu);
		return true;
	}
	
	@SuppressLint("NewApi")
	public void setBackground(LinearLayout ll, int png){
		int sdk = android.os.Build.VERSION.SDK_INT;
		if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			ll.setBackgroundDrawable( getResources().getDrawable(png) );
		} else {
			ll.setBackground( getResources().getDrawable(png));
		}
	}
	
	
	//check if powerup has been clicked
	public boolean checkPowerUp(){
		
		//powerup clicked
		if(powerUp.row == rowPoint && powerUp.column== colPoint && powerUp.icon.getVisibility()!=View.GONE){ 
			//cancel level toast if still showing
			levelToast.cancel();
			
			//create new toast
			View toastView = getLayoutInflater().inflate(R.layout.toast, (ViewGroup)findViewById(R.id.toastlayout));
			TextView powerUpDisplay = (TextView)toastView.findViewById(R.id.powerUpText);
			Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fonts/grapple.ttf");
			powerUpDisplay.setTypeface(myTypeface);
			
			//heart (3 more lives)
			if(powerUp.type==PowerUp.Types.HEART){
				powerUpDisplay.setText("More Lives!");
				powerUpDisplay.setShadowLayer(30, 10, 10, Color.WHITE);
				powerUpDisplay.setTextColor(Color.RED);
				lives = lives + 3;
				setLives();
			}
			
			//turtle (speed decreases, blocks are now updated every -90*Math.log(level)+400 seconds)
			else if(powerUp.type==PowerUp.Types.TURTLE){
				powerUpDisplay.setText("Slow Down!");
				powerUpDisplay.setTextColor(Color.GREEN);
				powerUpDisplay.setShadowLayer(30, 10, 10, Color.BLUE);
				myTimer.cancel();
				myTimer.purge();
				
				myTimer = new Timer();
				myTimer.schedule(new TimerTask() {
				       @Override
				       public void run() {UpdateGUI();}
				    }, 0, (long) ((long)-90*Math.log(level/2)+400));
				      
				
			}
			
			//coins (add 50 points to score)
			else{
				score = score + 50;
				setScore();
				powerUpDisplay.setText("More Points!");
				powerUpDisplay.setTextColor(Color.YELLOW);
				powerUpDisplay.setShadowLayer(30, 10, 10, Color.RED);
			}
			
			//finish toast and show it, return true
			TextView textView = (TextView)toastView.findViewById(R.id.welldone);
			textView.setShadowLayer(30, 10, 10, Color.BLACK);
			Typeface myTypeface2 = Typeface.createFromAsset(getAssets(), "fonts/seasrn.ttf");
			textView.setTypeface(myTypeface2);
			textView.setText("Well Done!");
			textView.setGravity(Gravity.CENTER_VERTICAL);
			powerUpToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			powerUpToast.setDuration(Toast.LENGTH_SHORT);
			powerUpToast.setView(toastView);
			powerUpToast.show();
			return true;
		}
		//powerup not hit
		return false;
	}
	
	
	//update lives label
	public void setLives(){
		livesLabel.setText("Lives x "+lives);
	}
	
	//update score label
	private void setScore() {
		scoreLabel.setText("Score = " + score);
	} 
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {

	    if(keyCode == KeyEvent.KEYCODE_BACK)
	    {
	    		powerUpToast.cancel();
	    		levelToast.cancel();
	            moveTaskToBack(true);
	            return true;
	    }
		return false;
	}
	
}



