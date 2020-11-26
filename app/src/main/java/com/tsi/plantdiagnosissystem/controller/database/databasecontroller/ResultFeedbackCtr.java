package com.tsi.plantdiagnosissystem.controller.database.databasecontroller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tsi.plantdiagnosissystem.controller.database.DatabaseHelper;
import com.tsi.plantdiagnosissystem.data.model.ResultFeedback;
import com.tsi.plantdiagnosissystem.data.model.User;

import java.util.ArrayList;
import java.util.List;

public class ResultFeedbackCtr {

    public static final String RESULT_FEEDBACK_TABLE_NAME = "result_feedback";

    //resultFeedback
    String idColumn = "id";
    String userNameColumn = "user_name";
    String plantNameColumn = "plant_name";
    String feedBackColumn = "feedback";

    private static SQLiteDatabase getDB() {
        return DatabaseHelper.instance().getWritableDatabase();
    }

    //add the new ResultFeedback
    public void addResultFeedback(User user, String plantName, String feedBack) {

            SQLiteDatabase sqLiteDatabase = getDB();
            ContentValues values = new ContentValues();
            values.put(this.userNameColumn, user.getUsername());
            values.put(this.plantNameColumn, plantName);
            values.put(this.feedBackColumn, feedBack);

            //inserting new row
            sqLiteDatabase.insert(RESULT_FEEDBACK_TABLE_NAME, null, values);
            //close database connection
            sqLiteDatabase.close();
    }

    //delete the ResultFeedback
    public void delete(String ID) {
        SQLiteDatabase sqLiteDatabase = getDB();
        //deleting row
        sqLiteDatabase.delete(RESULT_FEEDBACK_TABLE_NAME, "ID=" + ID, null);
        sqLiteDatabase.close();
    }

    public List<ResultFeedback> getResultFeedback(String findByPlantName) {
        ResultFeedback resultFeedback = null;
        List<ResultFeedback> resultFeedbacks = new ArrayList<>();

        // select all query
        String select_query = "SELECT * FROM " + RESULT_FEEDBACK_TABLE_NAME + " WHERE " + plantNameColumn + " = '" + findByPlantName + "'";

        SQLiteDatabase db = getDB();
        Cursor cursor = db.rawQuery(select_query, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String userName = cursor.getString(1);
                String plantName = cursor.getString(2);
                String feedback = cursor.getString(3);
                resultFeedback = new ResultFeedback(userName, plantName, feedback);
                resultFeedbacks.add(resultFeedback);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return resultFeedbacks;
    }

    //get the all users
    public List<ResultFeedback> getResultFeedbacks() {
        ResultFeedback resultFeedback = null;
        List<ResultFeedback> resultFeedbacks = new ArrayList<>();

        // select all query
        String select_query = "SELECT * FROM " + RESULT_FEEDBACK_TABLE_NAME;

        SQLiteDatabase db = getDB();
        Cursor cursor = db.rawQuery(select_query, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String userName = cursor.getString(1);
                String plantName = cursor.getString(2);
                String feedback = cursor.getString(3);
                resultFeedback = new ResultFeedback(userName, plantName, feedback);
                resultFeedbacks.add(resultFeedback);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return resultFeedbacks;
    }
}
