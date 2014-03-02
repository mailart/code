package code.humber.healthcalc;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;



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
import android.widget.Toast;

public class UserInfoActivity extends Activity {
	ListView lv=null;
	public UserProfile p = null;
	public int sel;
	public UserInfoItem listitem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userprofile);
		if ( MainActivity.dataengine.GetUser() != null){
			p = MainActivity.dataengine.GetUser();
		}
		else{
			p = new UserProfile();
		}

		lv=(ListView) this.findViewById(R.id.useritem);
		        
		lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listitem=new UserInfoItem();
		lv.setAdapter(listitem);
		
	
		lv.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				String[] gender = {"I'm a girl","I'm a guy"}; 
				String [] age = new String[90];
				String [] height = new String[100];
				String [] weight = new String[200];
				String [] dlgoptions = null;
				String title = "unknown";
				
				
				
				for (int i=0;i < 90; i++){
					age[i] = String.valueOf(i+20);
				}
				
				for (int i=0;i < 100; i++){
					height[i] = String.valueOf(i+120) + "cm";
				}
				
				for (int i=0;i < 200; i++){
					weight[i] = String.valueOf(i+20) + "kg";
				}
				
				
				switch (arg2){
					case 0:{
						dlgoptions = gender;
						title = "Gender";
						sel=0;
						break;
					}
					case 1:{
						dlgoptions = age;
						title = "Age";
						sel=1;
						break;
					}
					case 2:{
						dlgoptions = height;
						title = "Height";
						sel=2;
						break;
					}
					case 3:{
						dlgoptions = weight;
						title = "Weight";
						sel=3;
						break;
					}
				}
				
				AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoActivity.this);   
		        builder.setTitle(title);  
		        builder.setItems(dlgoptions, new DialogInterface.OnClickListener() {  
		            public void onClick(DialogInterface dialog, int which) {  
		                //点击后弹出窗口选择了第几项  
		                //showDialog("你选择的id为" + which + " , " + dlgoptions[which]); 
		            	
		            	switch (UserInfoActivity.this.sel){
							case 0:{
								UserInfoActivity.this.p.m_gender = (which == 0);
								break;
							}
							case 1:{
								UserInfoActivity.this.p.m_age = which + 20;
								break;
							}
							case 2:{
								UserInfoActivity.this.p.m_height = which + 120;
								break;
							}
							case 3:{
								UserInfoActivity.this.p.m_weight = which + 20;
								break;
							}
						}
		            	
		            	UserInfoActivity.this.listitem.notifyDataSetChanged();
		            }  
		        });  
		        builder.create().show();  
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}
    
   class UserInfoItem extends BaseAdapter{
   	LayoutInflater listitempane;
   	float lasttouchx=0;int lastsel=0;
   	
   	
	   	
	   	UserInfoItem(){
	   		
	   		listitempane=LayoutInflater.from(UserInfoActivity.this);
	   	}
		public int getCount() {
			// TODO Auto-generated method stub
			return 4;
		}

		public String getItem(int position) {
			// TODO Auto-generated method stub
			switch(position){
				case 0:{
					return "Gender";
				}
				case 1:
					return "Age";
				case 2:
					return "Height";
				case 3:
					return "Weight";
			}
			return "unknown";
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			boolean isnew=false;
			String ci=(String) getItem(position);
			View item = listitempane.inflate(R.layout.listitem4, null);
			
			TextView Name=(TextView) item.findViewById(R.id.Title);
			Name.setText(ci);
			
			TextView value=(TextView) item.findViewById(R.id.status);
			String str="";
			boolean isshow=false;
			switch(position){
				case 0:{
					str = UserInfoActivity.this.p.m_gender ?"I'm a girl": "I'am a guy";
					isshow=true;
					break;
				}
				case 1:{
					str = String.valueOf(UserInfoActivity.this.p.m_age);
					if(UserInfoActivity.this.p.m_age >0)
						isshow=true;
					break;
				}
				case 2:{
					str = String.valueOf(UserInfoActivity.this.p.m_height)+"cm";
					if(UserInfoActivity.this.p.m_height >0)
						isshow=true;
					break;
				}
				case 3:{
					str = String.valueOf(UserInfoActivity.this.p.m_weight)+"kg";
					if(UserInfoActivity.this.p.m_weight >0)
						isshow=true;
					break;
				}
			}
			if (isshow)
				value.setText(str);
			
			
			return item;
		}
   }
   
   public void onClick_Save(View view){
	   if(!(UserInfoActivity.this.p.m_age >0)||
			   !(UserInfoActivity.this.p.m_height >0)||
			   !(UserInfoActivity.this.p.m_weight >0)){
		   Toast.makeText(getApplicationContext(), "Please input valid information", Toast.LENGTH_LONG).show();
		   return;
	   }
	   MainActivity.dataengine.SaveUserProfile(UserInfoActivity.this.p.m_gender,UserInfoActivity.this.p.m_age,
			   (int)UserInfoActivity.this.p.m_height,UserInfoActivity.this.p.m_weight);
	   finish();
   }
}
