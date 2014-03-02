package code.humber.healthcalc;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;




import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FoodSelectorActivity extends Activity {
	ListView lv=null;
	FoodMenu m_foodmenu = null;
//	SearchKeyword keywork = new SearchKeyword();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.foodsel);
		m_foodmenu = new FoodMenu();
		

		lv=(ListView) this.findViewById(R.id.foodlist);
		        
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		lv.setAdapter(m_foodmenu);
		
		lv.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				int index =0;
				if (FoodSelectorActivity.this.m_foodmenu.layer == 0){
					for (String key : MainActivity.dataengine.cateroothash.keySet()) {
						CateRoot entry = MainActivity.dataengine.cateroothash.get(key); 
					    
					    if( index == arg2){
					    	FoodSelectorActivity.this.m_foodmenu.currentroot = entry;
					    	break;
					    }
					    
					    index++;
					}
					FoodSelectorActivity.this.m_foodmenu.layer = 1;
				}
				else if (FoodSelectorActivity.this.m_foodmenu.layer == 1){
					if (arg2 == 0){
						FoodSelectorActivity.this.m_foodmenu.layer = 0;
						FoodSelectorActivity.this.m_foodmenu.currentsub = null;
					}
					else{
						FoodSelectorActivity.this.m_foodmenu.layer = 2;
						arg2--;
						Iterator it1 = FoodSelectorActivity.this.m_foodmenu.currentroot.sub.iterator();
						while(it1.hasNext()){
							CateSub entry = (CateSub) it1.next(); 
						    
						    if(index == arg2){
						    	FoodSelectorActivity.this.m_foodmenu.currentsub = entry;
						    	break;
						    }
						    
						    index++;
						}
					}
				}
				else if (FoodSelectorActivity.this.m_foodmenu.layer == 2){
					if (arg2 == 0){
						FoodSelectorActivity.this.m_foodmenu.layer = 1;
					}
					else{
						arg2--;
						Iterator it1 = FoodSelectorActivity.this.m_foodmenu.currentsub.foods.iterator();
						while(it1.hasNext()){
							FoodItem entry = (FoodItem) it1.next(); 
						    
						    if(index == arg2){
						    	MainActivity.dataengine.AddFood(entry);
						    	finish();
						    	break;
						    }
						    
						    index++;
						}
						
						
						
					}	
				}
				
				FoodSelectorActivity.this.m_foodmenu.notifyDataSetChanged();
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}
    
	
			
	class FoodMenu extends BaseAdapter{
	   	LayoutInflater listitempane;
	   	float lasttouchx=0;int lastsel=0;
   	
   		CateRoot currentroot;
   		CateSub currentsub;
 
   		int layer=0;
	   	
   		FoodMenu(){
   			currentroot = null;
   			currentsub = null;
	   		listitempane=LayoutInflater.from(FoodSelectorActivity.this);
	   	}
		public int getCount() {
			// TODO Auto-generated method stub
			switch(layer){
				case 0:{
					return MainActivity.dataengine.cateroothash.size();
				}
				case 1:
				{
					return currentroot.sub.size()+1;
				}
				case 2:
				{
					return currentsub.foods.size()+1;
				}
			}
			return 4;
		}
		
		public FoodItem getFoodItem(int position) {
			// TODO Auto-generated method stub
			if ((layer != 0 && position ==0) || layer !=2){
				
				return null;
			}
			
			position --;
			
			
			int index=0;
			Iterator it1 = currentsub.foods.iterator();
			while(it1.hasNext()){
				FoodItem entry = (FoodItem) it1.next(); 
			    
			    if(index == position)
			    	return entry;
			    
			    index++;
			}
			
			return null;
		}
		public String getItem(int position) {
			// TODO Auto-generated method stub
			if (layer != 0 && position ==0){
				
				return "..";
			}
			if (layer != 0)
				position --;
			
			switch(layer){
				case 0:{
					int index=0;
					
					for (String key : MainActivity.dataengine.cateroothash.keySet()) {
						CateRoot entry = MainActivity.dataengine.cateroothash.get(key); 
					    
					    if(index == position)
					    	return entry.mname;
					    
					    index++;
					}
					
					
					
					break;
				}
				case 1:
				{
					int index=0;
					Iterator it1 = currentroot.sub.iterator();
					while(it1.hasNext()){
						CateSub entry = (CateSub) it1.next(); 
					    
					    if(index == position)
					    	return entry.mname;
					    
					    index++;
					}
					break;
				}
			}
			
			
			
			
			
			return "unknown";
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			
			String ci="";
			if(layer>0 && position ==0)
				ci ="..";
			else
				ci = getItem(position);
	  		FoodItem currentfooditem = getFoodItem(position);
	  		
	  		View item = null;
	  		if (layer!=2 || position ==0){
	  			item = listitempane.inflate(R.layout.listitem1, null);
	  			TextView Name=(TextView) item.findViewById(R.id.Title);
	  			ci = ci.toUpperCase();
				Name.setText(ci);
	  		}
	  		else{
	  			item = listitempane.inflate(R.layout.listitem2, null);	
	  			TextView Name=(TextView) item.findViewById(R.id.Title);
				Name.setText(currentfooditem.mname.toUpperCase());
				TextView v1=(TextView) item.findViewById(R.id.measure);
				v1.setText(currentfooditem.mmeasure);
				TextView v2=(TextView) item.findViewById(R.id.weight);
				v2.setText(String.valueOf(currentfooditem.mweight) + "g");
				
				TextView v3=(TextView) item.findViewById(R.id.cal);
				v3.setText(String.valueOf(currentfooditem.mCal) + "Cal");
	  		}

			
			
			
			
			return item;
		}
   }
   
   
}
