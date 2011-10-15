package com.novoda.demos.activitylaunchmode;

import java.util.Stack;

import android.app.Activity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class StackInfoDisplayer implements Runnable, Constants{
    
    private Activity activity;
    private TextView currentStackId;
    private LinearLayout currentTaskView;

    public StackInfoDisplayer(Activity activity, TextView currentStackId, LinearLayout currentTaskView){
        this.activity = activity;
        this.currentStackId = currentStackId;
        this.currentTaskView = currentTaskView;
    }
    
    @Override
    public void run() {
        Log.v(LOG_TAG, "===============================");
        BaseApplication app = (BaseApplication) activity.getApplication();
        showCurrentTaskInfo(app);
        Log.v(LOG_TAG, "===============================");
    }

    private void showCurrentTaskInfo(BaseApplication app) {
        int taskId = app.getCurrentTaskId();
        showCurrentTaskId(taskId);
        Stack<BaseActivity> task = app.getCurrentTask();
        showCurrentTaskActivites(task);
    }
    
    private void showCurrentTaskId(int taskId) {
        String taskMessage = "Activities in current task (id: " + taskId + ")";
        currentStackId.setText("Task id: " + taskId);
        Log.v(LOG_TAG, taskMessage);
    }

    private void showCurrentTaskActivites(Stack<BaseActivity> task) {
        for (int location = task.size() - 1; location >= 0; location--) {
            BaseActivity activity = task.get(location);
            showActivityDetails(activity);
        }
    }

    private void showActivityDetails(BaseActivity activity) {
        String activityName = activity.getClass().getSimpleName();
        Log.v(LOG_TAG, activityName);
        ImageView activityRepresentation = getActivityRepresentation(activity);
        currentTaskView.addView(activityRepresentation);
    }

    private ImageView getActivityRepresentation(BaseActivity activity) {
        ImageView image = new ImageView(activity);
        int color = activity.getBackgroundColour();
        image.setBackgroundResource(color);
        LinearLayout.LayoutParams params = new LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 10);
        params.setMargins(2, 2, 2, 2);
        image.setLayoutParams(params);
        return image;
    }
}
