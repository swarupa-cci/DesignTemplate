package com.creativecapsuleprojects.testandroid;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class StartActivity extends Activity implements OnItemClickListener {

	private static final String DEBUG_TAG = "Utility - Start Menu";

	ListView startMenuList;
	ArrayList<String> classes, options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		startMenuList = (ListView) findViewById(R.id.menu_list);

		classes = new ArrayList<String>();
		options = new ArrayList<String>();

		classes.add("ImagePickerSample");
		options.add("Image Picker");

		classes.add("TextCustomizer");
		options.add("Text Customizer.");

		classes.add("WaitForit");
		options.add("Wait For it.");

		classes.add("WaitForit");
		options.add("Wait For it.");

		classes.add("WaitForit");
		options.add("Wait For it.");

		classes.add("WaitForit");
		options.add("Wait For it.");

		classes.add("WaitForit");
		options.add("Wait For it.");

		classes.add("WaitForit");
		options.add("Wait For it.");

		classes.add("WaitForit");
		options.add("Wait For it.");

		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
				R.layout.option_row, R.id.rowTextView, options);
		startMenuList.setAdapter(listAdapter);
		startMenuList.setOnItemClickListener(this);

	}

	@Override
	public void onItemClick(AdapterView<?> listView, View view, int index,
			long id) {
		// TODO Auto-generated method stub
		Log.d(DEBUG_TAG, "Item Clicked at :" + index);

		switch (listView.getId()) {
		case R.id.menu_list:

			try {
				Class<?> activityClass = Class.forName("com.creativecapsuleprojects.testandroid."
						+ classes.get(index));
				Intent nextIntent = new Intent(this, activityClass);
				startActivity(nextIntent);

			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d(DEBUG_TAG, "Wait for it..........!!!!!");
			}

			break;

		default:
			break;
		}

	}

}
