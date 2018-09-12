package com.android.chrishsu.gsbookstore;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.chrishsu.gsbookstore.data.BookContract.BookEntry;

// Create main class CatalogActivity
public class CatalogActivity
        extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // Setup vars
    private static final int BOOK_LOADER = 0;
    private BookCursorAdapter mCursorAdapter;

    // Override onCreate method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Connect layout view
        setContentView(R.layout.activity_catalog);

        // Wire up ListView
        ListView bookListView = findViewById(R.id.book_listview);

        // Setup FAB to open Add/EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create new intent and send to EditorActivity
                Intent intent = new Intent(CatalogActivity.this
                        , EditorActivity.class);
                startActivity(intent);
            }
        });

        // Connect CursorAdapter to ListView
        mCursorAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(mCursorAdapter);

        // Setup listview OnClickListener
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long longId) {
                // Create an intent to send to EditorActivity
                Intent editIntent = new Intent(CatalogActivity.this
                        , EditorActivity.class);

                // Get the current Uri
                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, longId);
                // Attach Uri
                editIntent.setData(currentBookUri);
                // Start intent
                startActivity(editIntent);

            }
        });

        // Start loader
        getLoaderManager().initLoader(BOOK_LOADER, null, this);

        // Setup empty ListView
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);
    }

    // Override onCreateLoader
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define projections and columns
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QTY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE,
        };
        // Return loader
        return new CursorLoader(this,
                BookEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Update the new cursor that contains updated data
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // When the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {

            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertDummyBooks();
                return true;

            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllEntries();
                return true;
        }
        // Return menu items
        return super.onOptionsItemSelected(item);
    }

    // Function to insert dummy books
    private void insertDummyBooks() {
        // Insert dummy data only if there are empty db rows;
        // this is to prevent duplications.
        if (getCurrentDBRowCounts() == 0) {
            // Dummy#1
            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_PRODUCT_NAME
                    , getString(R.string.dummy_book_1_name));
            values.put(BookEntry.COLUMN_PRICE
                    , Double.parseDouble(getString(R.string.dummy_book_1_price)));
            values.put(BookEntry.COLUMN_QTY
                    , Integer.parseInt(getString(R.string.dummy_book_1_qty)));
            values.put(BookEntry.COLUMN_SUPPLIER_NAME
                    , getString(R.string.dummy_book_1_supplier));
            values.put(BookEntry.COLUMN_SUPPLIER_PHONE
                    , getString(R.string.dummy_book_1_supplier_phone));

            getContentResolver().insert(BookEntry.CONTENT_URI, values);

            // Dummy#2
            values = new ContentValues();
            values.put(BookEntry.COLUMN_PRODUCT_NAME
                    , getString(R.string.dummy_book_2_name));
            values.put(BookEntry.COLUMN_PRICE
                    , Double.parseDouble(getString(R.string.dummy_book_2_price)));
            values.put(BookEntry.COLUMN_QTY
                    , Integer.parseInt(getString(R.string.dummy_book_2_qty)));
            values.put(BookEntry.COLUMN_SUPPLIER_NAME
                    , getString(R.string.dummy_book_2_supplier));
            values.put(BookEntry.COLUMN_SUPPLIER_PHONE
                    , getString(R.string.dummy_book_2_supplier_phone));

            getContentResolver().insert(BookEntry.CONTENT_URI, values);

            // Dummy#3
            values = new ContentValues();
            values.put(BookEntry.COLUMN_PRODUCT_NAME
                    , getString(R.string.dummy_book_3_name));
            values.put(BookEntry.COLUMN_PRICE
                    , Double.parseDouble(getString(R.string.dummy_book_3_price)));
            values.put(BookEntry.COLUMN_QTY
                    , Integer.parseInt(getString(R.string.dummy_book_3_qty)));
            values.put(BookEntry.COLUMN_SUPPLIER_NAME
                    , getString(R.string.dummy_book_3_supplier));
            values.put(BookEntry.COLUMN_SUPPLIER_PHONE
                    , getString(R.string.dummy_book_3_supplier_phone));

            getContentResolver().insert(BookEntry.CONTENT_URI, values);
        }
    }

    // Fucntion to find current database counts
    private int getCurrentDBRowCounts() {
        // Create projection
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QTY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE,
        };
        // Query the db
        Cursor countCursor = getContentResolver().query(BookEntry.CONTENT_URI
                , new String[]{"count(_id) AS count"}
                , null
                , null
                , null);

        // Get the counts
        countCursor.moveToFirst();
        return countCursor.getInt(0);
    }

    // Function to delete all entries.
    private void deleteAllEntries() {
        // Delete from db
        int deletedRows = getContentResolver().delete(BookEntry.CONTENT_URI,
                null,
                null);

        // If deleted rows are more than zero, it's successful
        if (deletedRows > 0) {
            Toast.makeText(this,
                    "Total record(s) removed: "
                            + String.valueOf(deletedRows),
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
