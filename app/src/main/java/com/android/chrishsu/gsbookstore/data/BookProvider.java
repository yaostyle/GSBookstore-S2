package com.android.chrishsu.gsbookstore.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

// Create a custom ContentProvider
public class BookProvider extends ContentProvider {

    // Vars
    private static final String LOG_TAG = BookProvider.class.getSimpleName();
    private static final int BOOKS = 100;
    private static final int BOOK_ID = 101;

    // Uri matcher object
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static class to init matcher
    static {
        // URI for multiple rows
        sUriMatcher.addURI(BookContract.CONTENT_AUTHROITY
                , BookContract.PATH_BOOKS, BOOKS);
        // URI for single row
        sUriMatcher.addURI(BookContract.CONTENT_AUTHROITY
                , BookContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    // Database helper object
    private BookDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        // Create DB helper
        mDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {
        // Get readable db
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        // Create a cursor
        Cursor cursor;

        // Validate which uri is matched
        int match = sUriMatcher.match(uri);

        switch (match) {
            case BOOKS:
                // For multiple records
                cursor = database.query(BookContract.BookEntry.TABLE_NAME
                        , projection
                        , selection
                        , selectionArgs
                        , null
                        , null
                        , sortOrder);
                break;
            case BOOK_ID:
                // For single record with args
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(BookContract.BookEntry.TABLE_NAME
                        , projection
                        , selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknow URI " + uri);
        }
        // Set notification uri on the cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    // For insert URI method
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Single pass to insert function
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    // Insert book function
    private Uri insertBook(Uri uri, ContentValues values) {
        // Get the data and setup vars
        String name = values.getAsString(BookContract.BookEntry.COLUMN_PRODUCT_NAME);
        Double price = values.getAsDouble(BookContract.BookEntry.COLUMN_PRICE);
        Integer qty = values.getAsInteger(BookContract.BookEntry.COLUMN_QTY);
        String supplier = values.getAsString(BookContract.BookEntry.COLUMN_SUPPLIER_NAME);
        String supplier_phone = values.getAsString(BookContract.BookEntry.COLUMN_SUPPLIER_PHONE);

        // Check if the name is not null
        if (name == null) {
            throw new IllegalArgumentException("This book requires a name");
        }
        // Check if price is not zero
        if (price <= 0) {
            throw new IllegalArgumentException("This book requires a valid price.");
        }

        // Insert new book
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(BookContract.BookEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners the data has changed
        getContext().getContentResolver().notifyChange(uri, null);

        // Return new URI
        return ContentUris.withAppendedId(uri, id);
    }

    // For update URI method
    @Override
    public int update(@NonNull Uri uri,
                      ContentValues contentValues,
                      String selection,
                      String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case BOOKS:
                // Single pass to update function
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                // Extract out ID from the URI and pass to update function
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    // Update book function
    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If COLUMN_PRODUCT_NAME is present
        // Check the name value is not null
        if (values.containsKey(BookContract.BookEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(BookContract.BookEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("This book requires a name");
            }
        }
        // If COLUMN_PRICE is present
        // Check the price value is not null
        if (values.containsKey(BookContract.BookEntry.COLUMN_PRICE)) {
            Double price = values.getAsDouble(BookContract.BookEntry.COLUMN_PRICE);
            if (price <= 0) {
                throw new IllegalArgumentException("This book requires valid price.");
            }
        }

        // If COLUMN_QTY is present
        // Check the qty value is not null
        if (values.containsKey(BookContract.BookEntry.COLUMN_QTY)) {
            Integer qty = values.getAsInteger(BookContract.BookEntry.COLUMN_QTY);
            if (qty == null || qty < 0) {
                throw new IllegalArgumentException("This book requires a qty.");
            }
        }

        // If there are no values to update, then return 0 and no update db
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable db to update
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Get the num of rows that was affected
        int rowsUpdated = database
                .update(BookContract.BookEntry.TABLE_NAME
                        , values
                        , selection
                        , selectionArgs);

        // If there are rows more than 0, notify all listeners
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return number of rows
        return rowsUpdated;
    }

    // For delete URI method
    @Override
    public int delete(@NonNull Uri uri,
                      String selection,
                      String[] selectionArgs) {

        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the num of rows that were deleted
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case BOOKS:
                // Delete all rows that match the selection and args
                rowsDeleted = database
                        .delete(BookContract.BookEntry.TABLE_NAME
                                , selection
                                , selectionArgs);
                break;
            case BOOK_ID:
                // Delete a single row given by the ID
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database
                        .delete(BookContract.BookEntry.TABLE_NAME
                                , selection
                                , selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, notify the listeners
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the num of rows deleted
        return rowsDeleted;
    }

    // Getting the URI type
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case BOOKS:
                return BookContract.BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookContract.BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri);
        }
    }
}
