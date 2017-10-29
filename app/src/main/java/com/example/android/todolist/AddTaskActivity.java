

package com.example.android.todolist;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.android.todolist.data.TaskContract;


public class AddTaskActivity extends AppCompatActivity {

    // Declare a member variable to keep track of a task's selected mPriority
    private int mPriority;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // Initialize to highest mPriority by default (mPriority = 1)
        ((RadioButton) findViewById(R.id.radButton1)).setChecked(true);
        mPriority = 1;
    }


    /**
     * onClickAddTask is called when the "ADD" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void onClickAddTask(View view) {
        EditText editTask =(EditText)findViewById(R.id.editTextTaskDescription);
        String task=editTask.getText().toString();
        ContentValues cv=new ContentValues();
        // Check if EditText is empty, if not retrieve input and store it in a ContentValues object
        if(task.length()>0){
            cv.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION,task);
            cv.put(TaskContract.TaskEntry.COLUMN_PRIORITY,mPriority);
            // Insert new task data via a ContentResolver
            Uri uri =getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI,cv);
            // Display the URI that's returned with a Toast
            if(uri!=null){
                Toast.makeText(this,uri.toString(),Toast.LENGTH_SHORT).show();
            }

        }
        else {
            return;
        }
        // call finish() to return to MainActivity after this insert is complete
        finish();
    }


    /**
     * onPrioritySelected is called whenever a priority button is clicked.
     * It changes the value of mPriority based on the selected button.
     */
    public void onPrioritySelected(View view) {
        if (((RadioButton) findViewById(R.id.radButton1)).isChecked()) {
            mPriority = 1;
        } else if (((RadioButton) findViewById(R.id.radButton2)).isChecked()) {
            mPriority = 2;
        } else if (((RadioButton) findViewById(R.id.radButton3)).isChecked()) {
            mPriority = 3;
        }
    }
}
