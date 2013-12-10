package com.bignerranch.android.runtracker;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.bignerranch.android.runtracker.RunDatabaseHelper.RunCursor;

public class RunListFragment extends ListFragment implements LoaderCallbacks<Cursor>{
	private static final int REQUEST_NEW_RUN = 0;
	
	//no longer needed with loader and destroy method
	//private RunCursor mCursor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//for onOptionsMenu
		setHasOptionsMenu(true);
		//Query the list of runs
		//mCursor = RunManager.get(getActivity()).queryRuns();
		//Create an adapter to point at this cursor
		//RunCursorAdapter adapter = new RunCursorAdapter(getActivity(), mCursor);
		//setListAdapter(adapter);
		//initialize the loader to load the list of runs
		getLoaderManager().initLoader(0, null, this);
	}
	//called when it wants you to create the loader
	@Override
	public Loader<Cursor> onCreateLoader (int id, Bundle args) {
		//you only ever load the runs, so assume this is the case
		return new RunListCursorLoader(getActivity());
	}
	//called on main thread once data has been loaded in background
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		//Create an adapter to point at this cursor
		RunCursorAdapter adapter =
				new RunCursorAdapter (getActivity(), (RunCursor)cursor);
		setListAdapter(adapter);
	}
	//called in the event that the data is no longer available
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		//Stop using the cursor(via the adapter)
		setListAdapter(null);
	}
	/* no longer needed with loader
	@Override
	public void onDestroy() {
		mCursor.close();
		super.onDestroy();
	}*/
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.run_list_options, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item_new_run:
			Intent i = new Intent(getActivity(), RunActivity.class);
			startActivityForResult(i, REQUEST_NEW_RUN);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (REQUEST_NEW_RUN == requestCode) {
			//No longer needed with loader
			//mCursor.requery();
			//((RunCursorAdapter)getListAdapter()).notifyDataSetChanged();
			//Restart the loader to get any new run available
			getLoaderManager().restartLoader(0, null, this);
		}
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		//the argument will be the Run ID; CursorAdapter gives us this for free
		Intent i = new Intent(getActivity(), RunActivity.class);
		i.putExtra(RunActivity.EXTRA_RUN_ID, id);
		startActivity(i);
	}
	//for the loader, simple subclass
	private static class RunListCursorLoader extends SQLiteCursorLoader {
		
		public RunListCursorLoader(Context context) {
			super(context);
		}
		
		@Override
		protected Cursor loadCursor() {
			//Query the list of runs
			return RunManager.get(getContext()).queryRuns();
		}
	}
  	
	private static class RunCursorAdapter extends CursorAdapter {
		
		private RunCursor mRunCursor;
		
		public RunCursorAdapter(Context context, RunCursor cursor) {
			super(context, cursor, 0);
			mRunCursor = cursor;
		}
		
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			//Use a layout inflater to get a row view
			LayoutInflater inflater = (LayoutInflater)context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			return inflater
					.inflate(android.R.layout.simple_list_item_1, parent, false);
		}
		
		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			//Get the run for the current row
			Run run = mRunCursor.getRun();
			
			//set up the start date text view
			TextView startDateTextView = (TextView)view;
			String cellText =
					context.getString(R.string.cell_text, run.getStartDate());
			startDateTextView.setText(cellText);
		}
	}

}
