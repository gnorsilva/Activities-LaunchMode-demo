package com.novoda.demos.activitylaunchmode;

import java.util.Stack;

import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class TaskInfoDisplayer implements Runnable, Constants {

    private final BaseApplication app;
    private final TextView taskIdField;
    private final LinearLayout taskView;

    public TaskInfoDisplayer(BaseActivity baseActivity) {
        app = (BaseApplication) baseActivity.getApplication();
        taskIdField = (TextView) baseActivity.findViewById(R.id.task_id_field);
        taskView = (LinearLayout) baseActivity.findViewById(R.id.task_view);
        taskView.removeAllViews();
    }

    @Override
    public void run() {
        Log.v(LOG_TAG, "===============================");
        showCurrentTaskId();
        showCurrentTaskActivites();
        Log.v(LOG_TAG, "===============================");
    }

    private void showCurrentTaskId() {
        int taskId = app.getCurrentTaskId();
        String taskMessage = "Activities in current task (id: " + taskId + ")";
        taskIdField.setText("Task id: " + taskId);
        Log.v(LOG_TAG, taskMessage);
    }

    private void showCurrentTaskActivites() {
        Stack<BaseActivity> task = app.getCurrentTask();
        for (int location = task.size() - 1; location >= 0; location--) {
            BaseActivity activity = task.get(location);
            showActivityDetails(activity);
        }
    }

    private void showActivityDetails(BaseActivity activity) {
        String activityName = activity.getClass().getSimpleName();
        Log.v(LOG_TAG, activityName);
        ImageView activityRepresentation = getActivityRepresentation(activity);
        taskView.addView(activityRepresentation);
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
