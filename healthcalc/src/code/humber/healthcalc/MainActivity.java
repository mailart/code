package code.humber.healthcalc;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;



import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.TabHost.OnTabChangeListener;

public class MainActivity extends Activity implements OnTouchListener{

	public static Dataengine dataengine;
	private UserProfile usr;
	public static Fooditems food = null;
	ListView lv=null;
	
	boolean isrightside=false;
	
	void UpdateDay(){
		
		String strdata;
		if(food.currentlist==null){
			Calendar c = Calendar.getInstance();
	        c.getTime();
	        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        strdata = df.format(c.getTime());
		}
		else
			strdata=food.currentlist.m_date;
		TextView currentdate=(TextView) findViewById(R.id.date);
		currentdate.setText(strdata);
		
		
		TextView usertotal=(TextView) findViewById(R.id.leftcal);
		usertotal.setText(String.valueOf(dataengine.GetTotal(food.currentlist)) + "Cal");
		
		TextView plan=(TextView) findViewById(R.id.title2);
		plan.setText(String.valueOf(dataengine.GetCalBudget()) + "Cal");
		
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		dataengine = new Dataengine(getApplicationContext());
		if(dataengine.InitDatabase()){
			usr = dataengine.GetUser();
			if (usr==null){//profile dlg
				Intent p=new Intent(MainActivity.this,UserInfoActivity.class);
				startActivity(p);
			}
			
			food = new Fooditems();
			food.currentlist=dataengine.GetToday();
			lv=(ListView) this.findViewById(R.id.foodlist);
	        
			lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			lv.setAdapter(food);
			lv.setOnTouchListener(this);
			
			
			UpdateDay();
			
			  
			lv.setOnItemClickListener(new OnItemClickListener(){
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					
					int index = 0;
					dayfooditem sel = null;
					Iterator it1 = food.currentlist.foods.iterator();
					while(it1.hasNext()){
						dayfooditem entry = (dayfooditem) it1.next();
					    
					    if(index == arg2){
					    	sel = entry;
					    	break;
					    }
					    
					    index++;
					}
					
					if (!isrightside){
						removenum(sel);
						
					}
					else{
						addnum(sel);
						
					}
						
				}
			});
			
		}
		else{
			
		}
	}
	
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		float x=event.getRawX();
		float	y=event.getRawY();
    	int width = lv.getWidth();
    	if(event.getAction()==event.ACTION_DOWN){
    	//	lastx=event.getRawX();
    	//	lasty=event.getRawY();
    		if( width/2 > x ){
    			isrightside = false;
    		}
    		else{
    			isrightside = true;
    			
    		}
    	}
    	
    	else if(event.getAction()==event.ACTION_UP){
    	
    	}
    	
		return false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onUser(View view){
		Intent p=new Intent(MainActivity.this,UserInfoActivity.class);
		startActivity(p);
		//finish();
	}
	
	public void onDate(View view){
		   
		  //finish();
	 }
	
	public void onAdd(View view){
		Intent p=new Intent(MainActivity.this,FoodSelectorActivity.class);
		startActivity(p);
		
		  //finish();
	 }
	
	public void removenum( dayfooditem item){
		dataengine.EditFood(food.currentlist,item,false);
		food.notifyDataSetChanged();
		
		  //finish();
	 }
	
	public void addnum( dayfooditem item){
		dataengine.EditFood(food.currentlist,item,true);
		food.notifyDataSetChanged();
		  //finish();
	 }
	
	class Fooditems extends BaseAdapter{
	   	LayoutInflater listitempane;
	   	float lasttouchx=0;int lastsel=0;
   	
   		dayfoods currentlist;
 
   		int layer=0;
	   	
   		Fooditems(){
   			currentlist = null;
	   		listitempane=LayoutInflater.from(MainActivity.this);
	   	}
		public int getCount() {
			// TODO Auto-generated method stub
			if (currentlist== null)
				return 0;
			
			return currentlist.foods.size();
		}
		
		public dayfooditem getItem(int position) {
			// TODO Auto-generated method stub
			int index =0;
			Iterator it1 = currentlist.foods.iterator();
			while(it1.hasNext()){
				dayfooditem entry = (dayfooditem) it1.next(); 
			    
			    if(index == position){
			    	return entry;
			    }
			    
			    index++;
			}
					
			return null;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			
			dayfooditem	ci = getItem(position);
	  		

  			View item = listitempane.inflate(R.layout.listitem3, null);	
  			TextView Name=(TextView) item.findViewById(R.id.Title);
			Name.setText(ci.m_food.mname.toUpperCase());
			TextView v1=(TextView) item.findViewById(R.id.measure);
			v1.setText(ci.m_food.mmeasure);
			TextView v2=(TextView) item.findViewById(R.id.weight);
			v2.setText(String.valueOf(ci.m_food.mweight) + "g");
			
			TextView v3=(TextView) item.findViewById(R.id.tcal);
			v3.setText(String.valueOf(ci.m_food.mCal*ci.m_foodamount) + "Cal");
  		
			TextView v4=(TextView) item.findViewById(R.id.count);
			v4.setText(String.valueOf(ci.m_foodamount));
			
			
			
			return item;
		}
   }
	
	
}
