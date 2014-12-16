package philgabardo.stacker;

import android.app.ActionBar.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;


// Class for powerups (heart, turtle, coins)
public class PowerUp {
	public static enum Types{
		HEART, COIN, TURTLE
	}
	int row;
	Types type;
	int column;
	ImageView icon;
	
	PowerUp(ImageView image){
		this.row = Generic.randInt(1,5);
		this.column = Generic.randInt(0,4);
		this.icon = image;
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT);
		params.weight=1;
		
		int typeChooser = Generic.randInt(0,2);
		if(typeChooser==0){
			this.type = Types.HEART;
			this.icon.setImageResource(R.drawable.heart);
		}
		else if(typeChooser==1){
			this.type = Types.COIN;
			this.icon.setImageResource(R.drawable.coins);
		}
		else{
			this.type = Types.TURTLE;
			this.icon.setImageResource(R.drawable.turtle);
		}
		
	}
	
}
