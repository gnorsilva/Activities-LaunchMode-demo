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
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class BaseActivity extends Activity implements Constants {

    private String[] intentFlagsText = { "CLEAR_TOP", "CLEAR_WHEN_TASK_RESET", "EXCLUDE_FROM_RECENTS",
            "FORWARD_RESULT", "MULTIPLE_TASK", "NEW_TASK", "NO_HISTORY", "NO_USER_ACTION", "PREVIOUS_IS_TOP",
            "REORDER_TO_FRONT", "RESET_TASK_IF_NEEDED", "SINGLE_TOP" };

    private int[] intentFlags = { Intent.FLAG_ACTIVITY_CLEAR_TOP, Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET,
            Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS, Intent.FLAG_ACTIVITY_FORWARD_RESULT,
            Intent.FLAG_ACTIVITY_MULTIPLE_TASK, Intent.FLAG_ACTIVITY_NEW_TASK, Intent.FLAG_ACTIVITY_NO_HISTORY,
            Intent.FLAG_ACTIVITY_NO_USER_ACTION, Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP,
            Intent.FLAG_ACTIVITY_REORDER_TO_FRONT, Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED,
            Intent.FLAG_ACTIVITY_SINGLE_TOP };

    private int chosenFlags;

    private final int DISPLAY_STACK_DELAY = 500;

    private TextView lifecycle;

    private StringBuilder lifecycleFlow = new StringBuilder();

    private Handler handler = new Handler();

    private LinearLayout currentTaskView;

    private TextView currentStackId;

    private void log() {
        Thread current = Thread.currentThread();
        StackTraceElement trace = current.getStackTrace()[3];
        String method = trace.getMethodName();
        Log.v(LOG_TAG, getLaunchMode() + ": " + method);
        lifecycleFlow.append(method).append("\n");
        if (lifecycle != null) {
            lifecycle.setText(lifecycleFlow.toString());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log();
        setContentView(R.layout.main);
        setupViewWidgets();
        addToStack();
    }

    @Override
    protected void onResume() {
        log();
        currentTaskView.removeAllViews();
        Runnable stackInfoDisplayer = new StackInfoDisplayer(this, currentStackId, currentTaskView);
        handler.postDelayed(stackInfoDisplayer, DISPLAY_STACK_DELAY);
        super.onResume();
    }

    private void setupViewWidgets() {
        View activityLayout = findViewById(R.id.main_layout);
        activityLayout.setBackgroundResource(getBackgroundColour());
        TextView header = (TextView) findViewById(R.id.header);
        String launchMode = getLaunchMode();
        header.setText(launchMode);
        lifecycle = (TextView) findViewById(R.id.lifecycle_content);
        lifecycle.setMovementMethod(new ScrollingMovementMethod());
        currentTaskView = (LinearLayout) findViewById(R.id.current_stack_view);
        currentStackId = (TextView) findViewById(R.id.current_stack_id);
    }

    private void addToStack() {
        BaseApplication app = (BaseApplication) getApplication();
        app.pushToStack(this);
    }

    private void removeFromStack() {
        BaseApplication app = (BaseApplication) getApplication();
        app.removeFromStack(this);
    }

    private String getLaunchMode() {
        return "[" + hashCode() + "] " + getClass().getSimpleName();
    }

    public void generalOnClick(View v) {
        if (isIntentFilterMode()) {
            showIntentFilterDialog(v);
        } else {
            startActivity(getNextIntent(v));
        }
    }

    private void showIntentFilterDialog(final View nextActivityBtn) {
        chosenFlags = 0;
        final Builder build = new Builder(this);
        build.setTitle("List selection");
        build.setCancelable(true);
        final OnMultiChoiceClickListener onClick = new OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                chosenFlags |= intentFlags[which];
            }
        };
        build.setMultiChoiceItems(intentFlagsText, null, onClick);
        build.setPositiveButton("Done", new OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                Intent intent = getNextIntent(nextActivityBtn);
                intent.setFlags(chosenFlags);
                startActivity(intent);
            }
        });
        build.show();
    }

    private boolean isIntentFilterMode() {
        BaseApplication app = (BaseApplication) getApplication();
        return app.isIntentFilterMode();
    }

    private void setIntentFilterMode(boolean mode) {
        BaseApplication app = (BaseApplication) getApplication();
        app.setIntentFilterMode(mode);
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
        log();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.base_activity, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        log();
        MenuItem filterOption = menu.findItem(R.id.menuitem_intentfilter_mode);
        String title = "Turn " + (isIntentFilterMode() ? "off" : "on") + " IntentFilter mode";
        filterOption.setTitle(title);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        log();
        switch (item.getItemId()) {
        case R.id.menuitem_intentfilter_mode:
            setIntentFilterMode(!isIntentFilterMode());
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    public abstract int getBackgroundColour();

    @Override
    public void onContentChanged() {
        log();
        super.onContentChanged();
    }

    @Override
    protected void onDestroy() {
        log();
        removeFromStack();
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        log();
        super.onNewIntent(intent);
    }

    @Override
    protected void onPause() {
        log();
        super.onPause();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        log();
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onPostResume() {
        log();
        super.onPostResume();
    }

    @Override
    protected void onRestart() {
        log();
        super.onRestart();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        log();
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        log();
        return super.onRetainNonConfigurationInstance();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        log();
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        log();
        super.onStart();
    }

    @Override
    protected void onStop() {
        log();
        super.onStop();
    }

}
