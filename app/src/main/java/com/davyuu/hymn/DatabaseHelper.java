package com.davyuu.hymn;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_PATH = "/data/data/com.davyuu.hymn/databases/";
    private static final String DB_NAME = "hymn.sqlite";

    private static final String TABLE_NAME = "hymn";

    public static final String COL_NAME = "hymn_name";
    public static final String COL_NUMBER = "hymn_number";
    public static final String COL_IMAGE = "hymn_image";

    private SQLiteDatabase mDatabase;
    private final Context mContext;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.mContext = context;
    }

    public void createDataBase() throws IOException{
        boolean dbExist = checkDataBase();
        if(dbExist){
        }else{
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase()
    {
        File dbFile = new File(DB_PATH + DB_NAME);
        //Log.v("dbFile", dbFile + "   "+ dbFile.exists());
        return dbFile.exists();
    }

    private void copyDataBase() throws IOException{
        //Open your local db as the input stream
        InputStream myInput = mContext.getAssets().open(DB_NAME);
        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;
        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);
        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }
        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public boolean openDataBase() throws SQLException
    {
        if(checkDataBase()){
            String mPath = DB_PATH + DB_NAME;
            //Log.v("mPath", mPath);
            mDatabase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);
            //mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
            return mDatabase != null;
        }
        else{
            return false;
        }
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor results = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY CAST(" + COL_NUMBER + " AS int)", null);
        return results;
    }

    public List<String> getAllNames(){
        Cursor cursor = getAllData();
        List<String> list = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int nameIndex = cursor.getColumnIndex(COL_NAME);
            String name = cursor.getString(nameIndex);
            list.add(name);
            cursor.moveToNext();
        }
        return list;
    }

    public Map<String, Integer> getAllNumbers(){
        Cursor cursor = getAllData();
        Map<String, Integer> numberMap = new HashMap<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int nameIndex = cursor.getColumnIndex(COL_NAME);
            int numberIndex = cursor.getColumnIndex(COL_NUMBER);
            if(nameIndex == -1 || numberIndex == -1){
                return numberMap;
            }
            String name = cursor.getString(nameIndex);
            int number = cursor.getInt(numberIndex);
            numberMap.put(name, number);
            cursor.moveToNext();
        }

        return numberMap;
    }

    public Map<String, Integer> getAllImageIds(){
        Cursor cursor = getAllData();
        Map<String, Integer> imageIdMap = new HashMap<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int nameIndex = cursor.getColumnIndex(COL_NAME);
            int imageIndex = cursor.getColumnIndex(COL_IMAGE);
            if(nameIndex == -1 || imageIndex == -1){
                return imageIdMap;
            }
            String name = cursor.getString(nameIndex);
            String imageUrl = cursor.getString(imageIndex);
            String imageName = formatImageUrl(imageUrl);
            int imageId = mContext.getResources().getIdentifier(imageName, "drawable", mContext.getPackageName());
            imageIdMap.put(name, imageId);
            cursor.moveToNext();
        }

        return imageIdMap;
    }

    private String formatImageUrl(String imageURL){
        String[] imageUrlArray = imageURL.split("/");
        String imageName = imageUrlArray[imageUrlArray.length-1];
        imageName = imageName.replace("-", "_");
        imageName = imageName.split("\\.")[0];
        return imageName;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}