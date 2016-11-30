package com.example.fragmenttabs;

import java.util.ArrayList;


import com.example.fragmenttabs.R;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Switching between the tabs of a TabHost through fragments, using FragmentTabHost.
 */
public class FragmentTabsHost extends FragmentActivity 
{
    private FragmentTabHost mTabHost; 
    int mTabCount = 5;
	String TAB_SPEC_PREFIX = "tab";
	String TAB_SPEC;
	boolean bTabNameByDefault = true; //false;//true;
	// for DB
	private static Cursor mNotesCursor;
	private static DB mDbHelper;
	
	private static SharedPreferences lastPageView;
	private static int iLastTabId;
	private static int iCurrentTabNum;
	private ArrayList<String> tabIndicatorArrayList = new ArrayList<String>();
	
    @Override
    protected void onCreate(final Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        
    	//set tab indicator
    	setTabIndicator();
    	
    	// set listener
    	setListener();
    	
    	// divider
//    	mTabHost.getTabWidget().setDividerDrawable(R.drawable.divider_vertical_bright_9);
//    	if(Build.VERSION.SDK_INT >= 11)
//    		mTabHost.getTabWidget().setShowDividers(TabWidget.SHOW_DIVIDER_MIDDLE);

   } 
    
    @Override
    protected void onStart() {
    	super.onStart();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
	    	lastPageView.edit().putString("KEY_LAST_PAGE_VIEW", String.valueOf(iCurrentTabNum+1)).commit();
	}
	
	protected void setTabIndicator()
	{
    	// set default tab indicator
    	if(bTabNameByDefault)
    	{
			final Context context = getApplicationContext();
			
	        // set default tab : Tab1
	        String lastPage = null;
	        lastPageView = getSharedPreferences("last_page_view", 0);
	        if(lastPageView.getString("KEY_LAST_PAGE_VIEW","").equalsIgnoreCase("") )
	        {
	        	lastPage = "1"; //initialization
	        	
	        }
	        else
	        {
	        	lastPage = lastPageView.getString("KEY_LAST_PAGE_VIEW","");
	        }
	        
	        DB.setTableNumber(lastPage);
	        iLastTabId = Integer.valueOf(lastPage) - 1;
	    	
			mDbHelper = new DB(context).open();
			mNotesCursor = mDbHelper.getAllTab();
			// insert when table is empty 
			if(mNotesCursor.getCount() == 0)
			{
				mDbHelper.insertTab("TAB_INFO","N1"); 
				mDbHelper.insertTab("TAB_INFO","N2"); 
				mDbHelper.insertTab("TAB_INFO","N3"); 
				mDbHelper.insertTab("TAB_INFO","N4"); 
				mDbHelper.insertTab("TAB_INFO","N5"); 
			}
			mNotesCursor = mDbHelper.getAllTab();
	        

	    	for(int i=0;i < mTabCount;i++)
	    	{
	    		mNotesCursor.moveToPosition(i);
	    		tabIndicatorArrayList.add(i,  
						 mNotesCursor.getString(mNotesCursor.getColumnIndex("tab_name"))
	    						 );
	    	}
	    	
    	}
    	else
    	{
    		tabIndicatorArrayList.add(0,"購物");
    		tabIndicatorArrayList.add(1,"待辦");
    		tabIndicatorArrayList.add(2,"普通");
    		tabIndicatorArrayList.add(3,"重要");
    		tabIndicatorArrayList.add(4,"極重要");
    	}
	}
	
	@SuppressWarnings("deprecation")
	protected void setListener()
	{
        for(int i=0;i < mTabCount;i++)
        {
        	TAB_SPEC = TAB_SPEC_PREFIX.concat(String.valueOf(i));
            mTabHost.addTab(mTabHost
            					.newTabSpec(TAB_SPEC)
            					.setIndicator(tabIndicatorArrayList.get(i)),
            				NoteFragment.class, //interconnection
	                        null);	
            
            //mTabHost.getTabWidget().setStripEnabled(false);
          
	    	//set text color
	        TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
//	        tv.setTextColor(Color.rgb(255, 255, 255));
	        tv.setTextColor(Color.rgb(0, 0, 0));
	        
	        //unselected background color
            Drawable draw = getResources().getDrawable(R.drawable.btn_square_sel);
            if(Build.VERSION.SDK_INT >= 16)
            	mTabHost.getTabWidget().getChildAt(i).setBackground(draw);
            else
    	    	mTabHost.getTabWidget().getChildAt(i).setBackgroundDrawable(draw);

            // set tab text center
	    	int tabCount = mTabHost.getTabWidget().getTabCount();
	    	for (int j = 0; j < tabCount; j++) {
	    	    final View view = mTabHost.getTabWidget().getChildTabViewAt(j);
	    	    if ( view != null ) {
	    	        //  get title text view
	    	        final View textView = view.findViewById(android.R.id.title);
	    	        if ( textView instanceof TextView ) {
	    	            // just in case check the type
	    	            // center text
	    	            ((TextView) textView).setGravity(Gravity.CENTER);
	    	            // wrap text
	    	            ((TextView) textView).setSingleLine(false);
	    	            // explicitly set layout parameters
	    	            textView.getLayoutParams().height = ViewGroup.LayoutParams.FILL_PARENT;
	    	        }
	    	    }
	    	}
        }
        
        System.out.println("iLastTabId = " + iLastTabId);
        
        //set background color to selected tab 
		mTabHost.setCurrentTab(iLastTabId); 
//    	mTabHost.getTabWidget()
//    			.getChildAt(iLastTabId)
//    			.setBackgroundColor(Color.rgb(0, 128, 0)); //selected background color

        //last selected background
        Drawable draw = getResources().getDrawable(R.drawable.btn_square_unsel);
        if(Build.VERSION.SDK_INT >= 16)
        	mTabHost.getTabWidget().getChildAt(iLastTabId).setBackground(draw);
        else
	    	mTabHost.getTabWidget().getChildAt(iLastTabId).setBackgroundDrawable(draw);
        
        // set on tab changed listener
	    mTabHost.setOnTabChangedListener(new OnTabChangeListener()
	    {
			@Override
			public void onTabChanged(String tabId)
			{
//				System.out.println("_onTabChanged tabId = " + tabId);
				for(int i=0;i<mTabCount;i++)
				{
					TAB_SPEC = TAB_SPEC_PREFIX.concat(String.valueOf(i));
			    	
			    	if(TAB_SPEC.equals(tabId))
			    	{
			    		iCurrentTabNum = i;
			    		System.out.println("iCurrentTabNum " + iCurrentTabNum);
			    		
				    	mTabHost.setCurrentTab(i); 
//				    	mTabHost.getTabWidget()
//				    			.getChildAt(mTabHost.getCurrentTab())
//				    			.setBackgroundColor(Color.rgb(0, 128, 0)); //selected background color
			    		
				        //selected background
			            Drawable draw = getResources().getDrawable(R.drawable.btn_square_unsel);
			            if(Build.VERSION.SDK_INT >= 16)
			            	mTabHost.getTabWidget().getChildAt(i).setBackground(draw);
			            else
			    	    	mTabHost.getTabWidget().getChildAt(i).setBackgroundDrawable(draw);
			    		
				    	for(int j=0;j<mTabCount;j++){
				    		if(j != i){
//						    	mTabHost.getTabWidget()
//				    			.getChildAt(j)
////				    		.setBackgroundColor(Color.rgb(0, 0, 0)); //unselected background color
//								.setBackgroundColor(Color.rgb(16, 48, 16)); 
						        //unselected background
					            Drawable draw2 = getResources().getDrawable(R.drawable.btn_square_sel);
					            if(Build.VERSION.SDK_INT >= 16)
					            	mTabHost.getTabWidget().getChildAt(j).setBackground(draw2);
					            else
					    	    	mTabHost.getTabWidget().getChildAt(j).setBackgroundDrawable(draw2);
				    		}
				    		new FragmentLoader(String.valueOf(i+1)); //?
				    	}
			    	} //if(TAB_SPEC.equals(tabId))
				}//for(int i=0;i<mTabCount;i++)
			}//for(int i=0;i<mTabCount;i++)
		}//mTabHost.setOnTabChangedListener
	    );    

    	
	    // set listener for editing tab info
		for(int i=0;i<mTabCount;i++)
		{
			final int j = i;
			View tabView= mTabHost.getTabWidget().getChildAt(i);
			tabView.setOnLongClickListener(new OnLongClickListener() 
	    	{	
				@Override
				public boolean onLongClick(View v) 
				{
					if(j == iCurrentTabNum)
					{
						mNotesCursor = mDbHelper.getAllTab();
						mNotesCursor.moveToPosition(j);
	
						// get tab Id number
						final int tabId =  mNotesCursor.getInt(mNotesCursor.getColumnIndex("tab_id"));
						// get tab name
						String tabName = mNotesCursor.getString(mNotesCursor.getColumnIndex("tab_name"));
				        
				        final EditText editText1 = new EditText(getBaseContext());
				        editText1.setText(tabName);
				        editText1.setSelection(tabName.length()); // set edit text start position
				        //update tab info
				        Builder builder = new Builder(mTabHost.getContext());
				        builder.setTitle("修改標籤")
				                .setMessage("輸入標籤內容")
				                .setView(editText1)   
				                .setNegativeButton("更新", new OnClickListener()
				                {   @Override
				                    public void onClick(DialogInterface dialog, int which)
				                    {
				                        mDbHelper.updateTab("TAB_INFO", tabId, editText1.getText().toString());
				                        // Before _recreate, store late page number currently viewed
				                        lastPageView.edit().putString("KEY_LAST_PAGE_VIEW", String.valueOf(j+1)).commit();
				                        System.out.println("====recreate=====");
				                        recreate();
				                    }
				                })	                
				                .setPositiveButton("取消", new OnClickListener()
				                {   
				                	@Override
				                    public void onClick(DialogInterface dialog, int which)
				                    {}
				                })
				                .show();  
						}
					return true;
				}
			});
		}
	}//setListener()
}