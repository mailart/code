package code.humber.healthcalc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



class UserProfile{
	public boolean m_gender = false;
	public int m_age = 0;
	public float m_height = 0;
	public int m_weight = 0;
	UserProfile(boolean gender, int age,float height, int weight){
		m_gender = gender;
		m_age = age;
		m_height = height;
		m_weight = weight;
		
	}
	
	UserProfile(){}
}

class FoodItem{
	public String mname;
	public String mmeasure;
	public int mweight,mindex;
	public int mCal=0;
	
	public FoodItem(String name,String measure,int weight,int Cal,int index){
		mname = name;
		mmeasure = measure;
		mweight = weight;
		mCal = Cal;
		mindex=index;
	}
}

class CateSub{
	public String mname;
	public List<FoodItem> foods = new ArrayList<FoodItem>();
	public int i;
	public CateSub(String name,int index){
		mname = name;
		i = index;
	}
}

class CateRoot{
	public String mname;
	public List<CateSub> sub = new ArrayList<CateSub>();
	public CateRoot(String name){
		mname = name;
	}
}

class dayfooditem{
	public FoodItem m_food;
	public int m_foodatype;
	public int m_foodamount;
	public int m_index;
	
	public dayfooditem(FoodItem food,int amount,int type,int index){
		m_food = food;
		m_foodamount = amount;
		m_foodatype = type;
		m_index = index;
	}
}

class dayfoods{
	public String m_date;
	public List<dayfooditem> foods= new ArrayList<dayfooditem>();
	
	public dayfoods(String date){
		m_date = date;
	}
}

class dayfoodscollection{
	public List<dayfoods> dayrecord = new ArrayList<dayfoods>();
}

public class Dataengine {
	
	public Map<Integer,FoodItem> fooditemhash = new HashMap<Integer,FoodItem>();
	public Map<Integer,CateSub> catesubhash = new HashMap<Integer,CateSub>();
	public Map<String,CateRoot> cateroothash = new HashMap<String,CateRoot>();
	public Map<String,dayfoods> dayfoodshash = new HashMap<String,dayfoods>();
	int userecordcount = 0;
	public static void Message(String str){
		
	}
	
	public boolean InitDatabase(){
		OpenDataBase();
		//database.execSQL("select * from foodcate");
		Cursor mCursor =  
			database.query(true, "userinfo", null, null, null, null,  
	                null, null, null);  
	          
	        //mCursor不等于null,将标识指向第一条记录  
	        while (mCursor.moveToNext()) {  
	        	boolean gender = mCursor.getInt(mCursor.getColumnIndex("gender")) > 0;  
	            int age = mCursor.getInt(mCursor.getColumnIndex("age"));  
	            float height = mCursor.getFloat(mCursor.getColumnIndex("height"));  
	            int weight = mCursor.getInt(mCursor.getColumnIndex("weight")); 
	            
	            usr = new UserProfile(gender,age,height,weight);
	        }  
	       
	        
	        mCursor =  
	    			database.query(true, "foodcate", null, null, null, null,  
	    	                null, null, null);  
	    	          
	    	        //mCursor不等于null,将标识指向第一条记录  
	        while (mCursor.moveToNext()) {  
	        	int id = mCursor.getInt(mCursor.getColumnIndex("index"));  
	        	String mcate = mCursor.getString(mCursor.getColumnIndex("MainCate"));  
	        	String scate = mCursor.getString(mCursor.getColumnIndex("SubCate"));  
	        	
	        	CateRoot root = cateroothash.get(mcate);
	        	if(root == null){
	        		root = new CateRoot(mcate);
	        		cateroothash.put(mcate,root);
	        	}
	        	CateSub sub = new CateSub(scate,id);
	        	root.sub.add(sub);
	        	catesubhash.put(id,sub);
	        } 
	    	        
	    	        
	        
	        
	        mCursor =  
	    			database.query(true, "fooditems", null, null, null, null,  
	    	                null, null, null);  
	    	          
	    	        //mCursor不等于null,将标识指向第一条记录  
	        while (mCursor.moveToNext()) {  
	        	int cateindex = mCursor.getInt(mCursor.getColumnIndex("cateindex"));  
	        	String name = mCursor.getString(mCursor.getColumnIndex("Name"));  
	        	String Measure = mCursor.getString(mCursor.getColumnIndex("Measure"));  
	        	int Weight = mCursor.getInt(mCursor.getColumnIndex("Weight"));  
	        	int energy = mCursor.getInt(mCursor.getColumnIndex("Energy")); 
	        	int index = mCursor.getInt(mCursor.getColumnIndex("index")); 
	        	
	        	FoodItem item = new FoodItem(name,Measure,Weight,energy,index);
	        	CateSub sub = catesubhash.get(cateindex);
	        	if(sub!=null){
	        		sub.foods.add(item);
	        		fooditemhash.put(index,item);
	        	}
	        } 
	    	        
	        mCursor =  
	    			database.query(true, "userecord", null, null, null, null,  
	    	                null, null, null);  
	    	          
	    	        //mCursor不等于null,将标识指向第一条记录  
	        while (mCursor.moveToNext()) {  
	        	int foodindex = mCursor.getInt(mCursor.getColumnIndex("foodindex"));  
	        	int total = mCursor.getInt(mCursor.getColumnIndex("total"));  
	        	int index = mCursor.getInt(mCursor.getColumnIndex("recordid"));  
	            int mealtype = mCursor.getInt(mCursor.getColumnIndex("mealtype")); 
	            String date = mCursor.getString(mCursor.getColumnIndex("fooddate"));
	            boolean archived = mCursor.getInt(mCursor.getColumnIndex("archive"))>0;
	            
	            if(archived == true)
	            	continue;
	            
	            String shoartdate = date.substring(0,date.indexOf(" "));
	            
	            dayfooditem dayitem = new dayfooditem(fooditemhash.get(foodindex),total,mealtype,index);
	            dayfoods day = dayfoodshash.get(shoartdate);
	            if (day== null){
	            	day = new dayfoods(shoartdate);
	            	dayfoodshash.put(shoartdate,day);
	            }
	            day.foods.add(dayitem);
	            userecordcount ++;
	        //    Date fooddate = new Date();
	        //    fooddate.SetDate(dateFormat.parse()));

	        }  
	        
		return true;
	}
	public dayfoods GetToday(){
		dayfoods ret = null;
		Calendar c = Calendar.getInstance();
        c.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        
        
		for (String key : dayfoodshash.keySet()) {
			dayfoods entry = dayfoodshash.get(key); 
			int aa = key.indexOf(formattedDate);
		    if(key.indexOf(formattedDate)==0)
		    	return entry;
		}
		
		
		return ret;
	}
	
	public UserProfile usr = null;
	public dayfoodscollection dayrecords = null;
	public CateRoot cateroot = null;
	
	private SQLiteDatabase database = null;
	
	public UserProfile GetUser(){
		
		return usr;
	}
	public UserProfile SaveUserProfile (boolean gender, int age,int height, int weight){
		String sql;
		if (usr==null) {//insert
			sql = "insert into userinfo (gender,age,height,weight) values(";
			sql += String.valueOf(gender)+",";
			sql += String.valueOf(age)+",";
			sql += String.valueOf(height)+",";
			sql += String.valueOf(weight)+")";
			
			ContentValues values = new ContentValues();
			values.put("gender",gender);
			values.put("age", age);
			values.put("height", height);
			values.put("weight", weight);
			long rowid = database.insert("userinfo", null, values);
			
			
		}
		else {//update
			sql = "update  userinfo set gender=";
			sql += String.valueOf(gender)+", age=";
			sql += String.valueOf(age)+", height=";
			sql += String.valueOf(height)+", weight=";
			sql += String.valueOf(weight);
			
			ContentValues values = new ContentValues();
			values.put("gender",gender);
			values.put("age", age);
			values.put("height", height);
			values.put("weight", weight);
			database.update("userinfo", values, null, null); 
			
			
		}

	//	database.execSQL(sql);
		usr = new UserProfile(gender, age,height,weight);
		return usr;
	}
	public int GetCalBudget(){
		
		
		if (usr == null)
			return 0;
		else{
			if (usr.m_gender == false){//women
				if (usr.m_age>18 && usr.m_age<30 ){
					return (int)((float)14.6 * (float)usr.m_weight + (float)450);
				}
				else if (usr.m_age>31 && usr.m_age<60 ){
					return (int)((float)8.6 * (float)usr.m_weight + (float)830);//(float)8.6 * (float)usr.m_weight + (float)830;
				}
				else if (usr.m_age>60){
					return (int)((float)10.6 * (float)usr.m_weight + (float)600);//(float)10.6 * (float)usr.m_weight + (float)600;
				}
			}
			else{
				if (usr.m_age>18 && usr.m_age<30 ){
					return (int)((float)15.2 * (float)usr.m_weight + (float)680);//15.2 * (float)usr.m_weight + 680;
				}
				else if (usr.m_age>31 && usr.m_age<60 ){
					return (int)((float)11.5 * (float)usr.m_weight + (float)830);//11.5 * (float)usr.m_weight + 830;
				}
				else if (usr.m_age>60){
					return (int)((float)13.4 * (float)usr.m_weight + (float)490);//13.4 * (float)usr.m_weight + 490;
				}
				
			}
		}
		
		return 0;
	}
	public void AddFood(FoodItem food){
		Calendar c = Calendar.getInstance();
        c.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        
        
		ContentValues values = new ContentValues();
		values.put("foodindex",food.mindex);
		values.put("total", 1);
		values.put("mealtype", 0);
		values.put("fooddate", formattedDate);
		values.put("recordid", userecordcount);
		values.put("archive", false);
		long rowid = database.insert("userecord", null, values);
		
		dayfooditem dayitem = new dayfooditem(fooditemhash.get(food.mindex),1,0,userecordcount);
		formattedDate = formattedDate.substring(0,formattedDate.indexOf(" "));
		dayfoods day = dayfoodshash.get(formattedDate);
        if (day== null){
        	day = new dayfoods(formattedDate);
        	dayfoodshash.put(formattedDate,day);
        	MainActivity.food.currentlist = day;
        	
        	MainActivity.food.notifyDataSetChanged();
        }
        day.foods.add(dayitem);
        
		
	}
	
	public int GetTotal(dayfoods day){
		int total =0;
		if (day==null)
			return GetCalBudget();
		else{
			
			Iterator it1 = day.foods.iterator();
			while(it1.hasNext()){
				dayfooditem entry = (dayfooditem) it1.next();
			    
				total += entry.m_foodamount*entry.m_food.mCal;
			}
		}
		return GetCalBudget() - total;
	}
	public void EditFood(dayfoods day,dayfooditem food, boolean way){
		if (way == false)
			food.m_foodamount--;
		else
			food.m_foodamount++;
		
		boolean isarchive = food.m_foodamount ==0 ? true: false;
		
		
		ContentValues values = new ContentValues();
		values.put("total", food.m_foodamount);
		values.put("archive", isarchive);
		String[] whereArgs={String.valueOf(food.m_index)};  
		
		database.update("userecord", values, "recordid=?", whereArgs); 
		
		if(isarchive){//del
			int index = 0;
			Iterator it1 = day.foods.iterator();
			while(it1.hasNext()){
				dayfooditem entry = (dayfooditem) it1.next();
			    
			    if(entry.m_index == food.m_index){
			    	day.foods.remove(index);
			    	break;
			    }
			    
			    index++;
			}
			
		}
		
        
	}
	public int CalcCal(Date date){
		return 0;
	}
	
	
	
	
	//SD卡下的目录
		 private final String DATABASE_PATH = android.os.Environment
		   .getExternalStorageDirectory().getAbsolutePath() + "/healthmgr";
		 //数据库名
		 private final String DATABASE_FILENAME = "food.db";
		 //这个context是必需的，没有context，怎么都不能实现数据库的拷贝操作；
		 private Context context;
		 //构造函数必需传入Context，数据库的操作都带有这个参数的传入
		 public Dataengine(Context ctx) {
		  this.context = ctx;
		 }

		 public SQLiteDatabase OpenDataBase() {
		  try {
		   String databaseFilename = DATABASE_PATH + "/" + DATABASE_FILENAME;
		   File dir = new File(DATABASE_PATH);
		   //判断SD卡下是否存在存放数据库的目录，如果不存在，新建目录
		   if (!dir.exists()) {
		    dir.mkdir();
		    Log.i("ReleaseDataBaseActivity", "dir made:" + DATABASE_PATH);
		   } else {
		    Log.i("ReleaseDataBaseActivity", "dir exist:" + DATABASE_PATH);
		   }
		   try {
		    //如果数据库已经在SD卡的目录下存在，那么不需要重新创建，否则创建文件，并拷贝/res/raw下面的数据库文件
		    if (!(new File(databaseFilename)).exists()) {
		     Log.i("ReleaseDataBaseActivity", "file not exist:"
		       + databaseFilename);
		     ///res/raw数据库作为输出流
		     InputStream is = this.context.getResources().openRawResource(
		       R.raw.food);
		     //测试用
		     int size = is.available();
		     Log.i( "ReleaseDataBaseActivity", "DATABASE_SIZE:" + 1 );
		     Log.i("ReleaseDataBaseActivity", "count:" + 0);
		     //用于存放数据库信息的数据流
		     FileOutputStream fos = new FileOutputStream(
		       databaseFilename);
		        byte[] buffer = new byte[8192];
		     int count = 0;
		     Log.i("ReleaseDataBaseActivity", "count:" + count);
		     //把数据写入SD卡目录下
		     while ((count = is.read(buffer)) > 0) {
		      fos.write(buffer, 0, count);
		     }
		     fos.flush();
		     fos.close();
		     is.close();
		    }
		   } catch (FileNotFoundException e) {
		    Log.e("Database", "File not found");
		    e.printStackTrace();
		   } catch (IOException e) {
		    Log.e("Database", "IO exception");
		    e.printStackTrace();
		   }
		   //实例化sd卡上得数据库，database作为返回值，是后面所有插入，删除，查询操作的借口。
		   database = SQLiteDatabase.openDatabase(
		     databaseFilename, null,SQLiteDatabase.OPEN_READWRITE);
		   return database;

		  } catch (Exception e) {
		  }
		  return null;
		 }
		 
}
