

package com.example.android.todolist.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;


// completed (1) Verify that TaskContentProvider extends from ContentProvider and implements required methods
public class TaskContentProvider extends ContentProvider {
    // Define final integer constants for the directory of tasks and a single item.
    // It's convention to use 100, 200, 300, etc for directories,
    // and related ints (101, 102, ..) for items in that directory.
    private static final int TASKS=100;
    private static final int TASKS_WITH_ID=101;

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

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

}
