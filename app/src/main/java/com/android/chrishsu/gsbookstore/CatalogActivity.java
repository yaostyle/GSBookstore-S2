package com.android.chrishsu.gsbookstore;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import com.android.chrishsu.gsbookstore.data.BookContract.BookEntry;

// Create main class CatalogActivity
public class CatalogActivity
        extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "CatalogActivity";

    private static final int BOOK_LOADER = 0;

    BookCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open Add/EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);

        // Setup empty ListView
        ListView bookListView = findViewById(R.id.book_listview);
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);

        getLoaderManager().initLoader(BOOK_LOADER, null, this);

        // Connect CursorAdapter to ListView
        mCursorAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(mCursorAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QTY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE,
        };

        return new CursorLoader(this,
                BookEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
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
        return super.onOptionsItemSelected(item);
    }

    private void insertDummyBooks() {
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_NAME, "Harry Potter");
        values.put(BookEntry.COLUMN_PRICE, "35.99");
        values.put(BookEntry.COLUMN_QTY, "10");
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, "The Book Worm");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, "800-111-1111");

        getContentResolver().insert(BookEntry.CONTENT_URI, values);
    }

    private void deleteAllEntries() {
        int deletedRows = getContentResolver().delete(BookEntry.CONTENT_URI,
                null,
                null);
        if (deletedRows > 0) {
            Toast.makeText(this,
                    "Total record(s) removed: "
                            + String.valueOf(deletedRows),
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
