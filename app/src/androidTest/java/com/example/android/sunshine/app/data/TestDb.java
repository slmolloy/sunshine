/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine.app.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    private SQLiteDatabase db = null;
    private Cursor cursor = null;

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
        db = new WeatherDbHelper(this.mContext).getWritableDatabase();
    }

    public void tearDown() {
        if (db != null && db.isOpen()) {
            db.close();
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    /*
        Students: Uncomment this test once you've written the code to create the Location
        table.  Note that you will have to have chosen the same column names that I did in
        my solution for this test to compile, so if you haven't yet done that, this is
        a good time to change your column names to match mine.

        Note that this only tests that the Location table has the correct columns, since we
        give you the code for the weather table.  This test does not look at the
     */
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(WeatherContract.LocationEntry.TABLE_NAME);
        tableNameHashSet.add(WeatherContract.WeatherEntry.TABLE_NAME);

        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                cursor.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(cursor.getString(0));
        } while( cursor.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        cursor = db.rawQuery("PRAGMA table_info(" + WeatherContract.LocationEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                cursor.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(WeatherContract.LocationEntry._ID);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_CITY_NAME);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_COORD_LAT);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_COORD_LONG);
        locationColumnHashSet.add(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING);

        int columnNameIndex = cursor.getColumnIndex("name");
        do {
            String columnName = cursor.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(cursor.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required location entry columns",
                locationColumnHashSet.isEmpty());
    }

    public void testLocationTable() {
        // Create ContentValues of what you want to insert
        // (you can use the createNorthPoleLocationValues if you wish)
        ContentValues values = TestUtilities.createNorthPoleLocationValues();

        // Insert ContentValues into database and get a row ID back
        long locationRowId = db.insert(
                WeatherContract.LocationEntry.TABLE_NAME,
                null,
                values);

        assertTrue(locationRowId != -1);

        // Query the database and receive a Cursor back
        cursor = db.query(
                WeatherContract.LocationEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        // Validate a column is returned from query
        assertTrue("Empty cursor returned", cursor.moveToFirst());

        // Validate valid cursor and that the data matches expected values
        TestUtilities.validateCurrentRecord("Invalid location columns found from query", cursor, values);

        // Verify there are no additional rows returned
        assertFalse("More than one record returned by query", cursor.moveToNext());

        // Finally, close the cursor and database
        cursor.close();
    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can use the "createWeatherValues" function.  You can
        also make use of the validateCurrentRecord function from within TestUtilities.
     */
    public void testWeatherTable() {
        // Insert location test data
        long locationRowId = TestUtilities.insertNorthPoleLocationValues(db);

        // Create ContentValues for weather record
        ContentValues values = TestUtilities.createWeatherValues(locationRowId);

        // Insert ContentValues into database and get a row ID back
        long weatherRowId = db.insert(
                WeatherContract.WeatherEntry.TABLE_NAME,
                null,
                values);

        // Verify weather row id comes back as valid row id
        assertTrue("Error: failed to insert weather data", weatherRowId != -1);

        // Query the database and receive a Cursor back
        cursor = db.query(
                WeatherContract.WeatherEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        // Verify a record is returned through cursor
        assertTrue("Error: no record returned", cursor.moveToFirst());

        // Verify cursor values match expected values
        TestUtilities.validateCurrentRecord("Weather table test failed", cursor, values);

        // Verify only one record exists in Weather table
        assertFalse("Error: more than one record exists", cursor.moveToNext());
    }
}