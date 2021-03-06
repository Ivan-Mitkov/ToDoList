

package com.example.android.todolist.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import static com.example.android.todolist.data.TaskContract.TaskEntry.TABLE_NAME;


// completed (1) Verify that TaskContentProvider extends from ContentProvider and implements required methods
public class TaskContentProvider extends ContentProvider {
    // Define final integer constants for the directory of tasks and a single item.
    // It's convention to use 100, 200, 300, etc for directories,
    // and related ints (101, 102, ..) for items in that directory.
    public static final int TASKS=100;
    public static final int TASKS_WITH_ID=101;

    //declare static UriMatcher for accessing in provider code
    private static final UriMatcher sUriMatcher = buildUriMatcher();


    //this function tell wich uri we need the matcher to recognize
    // Define a static buildUriMatcher method that associates URI's with their int match
    public static UriMatcher buildUriMatcher (){

        UriMatcher uriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
        //directory
        uriMatcher.addURI(TaskContract.AUTHORITY,TaskContract.PATH_TASK,TASKS);
        //single task
        uriMatcher.addURI(TaskContract.AUTHORITY,TaskContract.PATH_TASK+"/#",TASKS_WITH_ID);
        return uriMatcher;

    }

    /* onCreate() is where you should initialize anything you’ll need to setup
    your underlying data source.
    In this case, you’re working with a SQLite database, so you’ll need to
    initialize a DbHelper to gain access to it.
     */
    private TaskDbHelper mDBHelper;
    @Override
    public boolean onCreate() {
        //  Complete onCreate() and initialize a TaskDbhelper on startup
        // [Hint] Declare the DbHelper as a global variable
        Context context=getContext();
        mDBHelper=new TaskDbHelper(context);
        return true;
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
// Get access to the task database (to write new data to)
        // database is final and we are using SQLiteOpenHelper to getWritableDatabase
        final SQLiteDatabase db = mDBHelper.getWritableDatabase();
        // Write URI matching code to identify the match for the tasks directory
        //UriMatcher.match Try to match against the path in a url
        int match = sUriMatcher.match(uri);
        // Defines a new Uri object that receives the result of the insertion
        Uri mNewUri;

        switch (match){
            case TASKS:
                //inserting values into table using SQLiteDatabase insert
                //db.insert return row ID of the newly inserted row, or -1 if an error occurred
                long id = db.insert(TaskContract.TaskEntry.TABLE_NAME,null,values);
                if(id>0){
                    //success
                    /*ContentUris are utility methods useful for working with Uri objects
                    that use the "content" (content://) scheme. Content URIs have the syntax
                    *content://authority/path/id
                    *Content URIs appendId returns Uri.builder but Content URIs withAppendedId
                    *returns uri - that is what we need
                    */
                    //Set the value for the returnedUri and write the default case for unknown URI's
                    mNewUri= ContentUris.withAppendedId(TaskContract.TaskEntry.CONTENT_URI,id);
                }

                else {
                    throw new SQLException("Failed to insert row"+uri);
                }

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri"+uri);

        }
        //Notify the resolver if the uri has been changed, and return the newly inserted URI
        //Return a ContentResolver instance and  notify registered observers that a row was updated.
        getContext().getContentResolver().notifyChange(uri,null);
        return mNewUri;
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // Get access to underlying database (read-only for query)
        final SQLiteDatabase db =mDBHelper.getReadableDatabase();
        // Write URI match code and set a variable to return a Cursor
        int match =sUriMatcher.match(uri);
        Cursor cursor;
        switch (match){
            case TASKS:
                // Query for the tasks directory and write a default case
                cursor=db.query(
                        TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case TASKS_WITH_ID:
                //we need two stings one for selection and string[] for selectionArgs
                String mSelection="_id=?";//for selection
                //getPathSegments gets the decoded path segments as List<String> without '/'
                // than get() returns the element on position in uri
                //position 0 is table name, 1 is id wich we need
                String id=uri.getPathSegments().get(1);
                String[] mSelectionArgs=new String[]{id};//initializing the array for selectionArgs
                cursor=db.query(TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri"+uri);

        }
        //Set a notification URI on the Cursor and return that Cursor
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        // Get access to the database and write URI matching code to recognize a single item
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int row=0;

        switch (match) {

            case TASKS_WITH_ID:
                //we need two stings one for selection and string[] for selectionArgs
                String mSelection = "_id=?";//for selection
                //getPathSegments gets the decoded path segments as List<String> without '/'
                // than get() returns the element on position in uri
                //position 0 is table name, 1 is id wich we need
                String id = uri.getPathSegments().get(1);
                String[] mSelectionArgs = new String[]{id};//initializing the array for selectionArgs
                row =db.delete(TABLE_NAME,
                        mSelection,
                        mSelectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri" + uri);

                // Notify the resolver of a change and return the number of items deleted
        }
        if(row>0){
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return row;
    }

    // Update won't be used in the final ToDoList app but is implemented here for completeness
    // This updates a single item (by it's ID) in the tasks directory
    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        //Keep track of if an update occurs
        int tasksUpdated;

        // match code
        int match = sUriMatcher.match(uri);

        switch (match) {
            case TASKS_WITH_ID:
                //update a single task by getting the id
                String id = uri.getPathSegments().get(1);
                //using selections
                tasksUpdated = mDBHelper.getWritableDatabase()
                        .update(TABLE_NAME, values, "_id=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (tasksUpdated != 0) {
            //set notifications if a task was updated
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // return number of tasks updated
        return tasksUpdated;
    }


    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);

        switch (match) {
            case TASKS:
                // directory
                return "vnd.android.cursor.dir" + "/" + TaskContract.AUTHORITY + "/" + TaskContract.PATH_TASK;
            case TASKS_WITH_ID:
                // single item type
                return "vnd.android.cursor.item" + "/" + TaskContract.AUTHORITY + "/" + TaskContract.PATH_TASK;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

}
