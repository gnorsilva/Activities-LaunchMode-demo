package com.novoda.demos.activitylaunchmode;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public abstract class BaseActivity extends Activity implements Constants {

	private final int DISPLAY_STACK_DELAY = 500;
    
    private String[] intentFlagsText = { "CLEAR_TOP", "CLEAR_WHEN_TASK_RESET", "EXCLUDE_FROM_RECENTS",
            "FORWARD_RESULT", "MULTIPLE_TASK", "NEW_TASK", "NO_HISTORY", "NO_USER_ACTION", "PREVIOUS_IS_TOP",
            "REORDER_TO_FRONT", "RESET_TASK_IF_NEEDED", "SINGLE_TOP" };

    private int[] intentFlags = { Intent.FLAG_ACTIVITY_CLEAR_TOP, Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET,
            Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS, Intent.FLAG_ACTIVITY_FORWARD_RESULT,
            Intent.FLAG_ACTIVITY_MULTIPLE_TASK, Intent.FLAG_ACTIVITY_NEW_TASK, Intent.FLAG_ACTIVITY_NO_HISTORY,
            Intent.FLAG_ACTIVITY_NO_USER_ACTION, Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP,
            Intent.FLAG_ACTIVITY_REORDER_TO_FRONT, Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED,
            Intent.FLAG_ACTIVITY_SINGLE_TOP };

    private TextView lifecycle;
    private StringBuilder lifecycleFlow = new StringBuilder();
    private Handler handler = new Handler();
    private BaseApplication app;

    private int chosenFlags;
    
    private void logMethodName() {
        String methodName = getMethodName();
        Log.v(LOG_TAG, getLaunchMode() + ": " + methodName);
        lifecycleFlow.append(methodName).append("\n");
        if (lifecycle != null) {
            lifecycle.setText(lifecycleFlow.toString());
        }
    }

    private String getMethodName() {
        Thread current = Thread.currentThread();
        StackTraceElement trace = current.getStackTrace()[4];
        return trace.getMethodName();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logMethodName();
        setContentView(R.layout.main);
        setupView();
        app = (BaseApplication) getApplication();
        app.pushToStack(this);
    }
    
    private void setupView() {
        View activityLayout = findViewById(R.id.main_layout);
        activityLayout.setBackgroundResource(getBackgroundColour());
        TextView header = (TextView) findViewById(R.id.header);
        String launchMode = getLaunchMode();
        header.setText(launchMode);
        lifecycle = (TextView) findViewById(R.id.lifecycle_content);
        lifecycle.setMovementMethod(new ScrollingMovementMethod());
    }
    
    @Override
    protected void onResume() {
        logMethodName();
        checkIfReorderToFront();
        Runnable taskInfoDisplayer = new TaskInfoDisplayer(this);
        handler.postDelayed(taskInfoDisplayer, DISPLAY_STACK_DELAY);
        super.onResume();
    }

    private void checkIfReorderToFront() {
    	Intent intent = getIntent();
    	if (shouldReorderToFront(intent)){
    		app.removeFromStack(this);
    		app.pushToStack(this);
    	}
	}

	private boolean shouldReorderToFront(Intent intent) {
		int flags = intent.getFlags();
		return (flags & Intent.FLAG_ACTIVITY_REORDER_TO_FRONT) > 0;
	}

	private String getLaunchMode() {
        return "[" + hashCode() + "] " + getClass().getSimpleName();
    }

    public void generalOnClick(View v) {
        if (app.isIntentFilterMode()) {
            showIntentFilterDialog(v);
        } else {
            startActivity(getNextIntent(v));
        }
    }

    private void showIntentFilterDialog(final View nextActivityBtn) {
        chosenFlags = 0;
        Builder builder = new Builder(this);
        prepareIntentFiltedDialog(builder);
        builder.setTitle("List selection");
        builder.setCancelable(true);
        final OnMultiChoiceClickListener onClick = new OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				chosenFlags |= intentFlags[which];
            }
        };
        builder.setMultiChoiceItems(intentFlagsText, null, onClick);
        builder.setPositiveButton("Done", new OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                Intent intent = getNextIntent(nextActivityBtn);
                intent.setFlags(chosenFlags);
                startActivity(intent);
            }
        });
        builder.show();
    }

    private void prepareIntentFiltedDialog(Builder build) {
    }

    private Intent getNextIntent(View v) {
        Class<? extends BaseActivity> nextActivity = null;
        switch (v.getId()) {
        case R.id.btn_standard:
            nextActivity = Standard.class;
            break;
        case R.id.btn_singletop:
            nextActivity = SingleTop.class;
            break;
        case R.id.btn_singletask:
            nextActivity = SingleTask.class;
            break;
        case R.id.btn_singleInstance:
            nextActivity = SingleInstance.class;
            break;
        }
        return new Intent(this, nextActivity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        logMethodName();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.base_activity, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        logMethodName();
        MenuItem filterOption = menu.findItem(R.id.menuitem_intentfilter_mode);
        String title = "Turn IntentFilter mode " + (app.isIntentFilterMode() ? "OFF" : "ON");
        filterOption.setTitle(title);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        logMethodName();
        switch (item.getItemId()) {
        case R.id.menuitem_intentfilter_mode:
            app.toggleIntentFilterMode();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onContentChanged() {
        logMethodName();
        super.onContentChanged();
    }

    @Override
    protected void onDestroy() {
        logMethodName();
        app.removeFromStack(this);
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent newIntent) {
        logMethodName();
        super.onNewIntent(newIntent);
        setIntent(newIntent);
    }

    @Override
    protected void onPause() {
        logMethodName();
        super.onPause();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        logMethodName();
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onPostResume() {
        logMethodName();
        super.onPostResume();
    }

    @Override
    protected void onRestart() {
        logMethodName();
        super.onRestart();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        logMethodName();
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        logMethodName();
        return super.onRetainNonConfigurationInstance();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        logMethodName();
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        logMethodName();
        super.onStart();
    }

    @Override
    protected void onStop() {
        logMethodName();
        super.onStop();
    }

    public abstract int getBackgroundColour();
    
}
