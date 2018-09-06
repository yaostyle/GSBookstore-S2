package com.android.chrishsu.gsbookstore;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.chrishsu.gsbookstore.data.BookDbHelper;
import com.android.chrishsu.gsbookstore.data.BookContract.BookEntry;

// Create main class CatalogActivity
public class CatalogActivity extends AppCompatActivity {

    // Create a global private DB helper
    private BookDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Initialize mDbHlper as BookDbHelper object
        mDbHelper = new BookDbHelper(this);

        // Run insertBooks to insert all 3 books together
        insertBooks();
    }

    // Overrid onStart to display all db records
    @Override
    protected void onStart() {
        super.onStart();
        // Run displayDatabaseInfo to read db
        displayDataBaseInfo();
    }

    // Custom class to insert all books together
    private void insertBooks() {
        // Insert first book
        insertBook(getString(R.string.book_1_name),
                Integer.parseInt(getString(R.string.book_1_price)),
                Integer.parseInt(getString(R.string.book_1_qty)),
                getString(R.string.book_1_supplier),
                getString(R.string.book_1_supplier_phone));

        // Insert second book
        insertBook(getString(R.string.book_2_name),
                Integer.parseInt(getString(R.string.book_2_price)),
                Integer.parseInt(getString(R.string.book_2_qty)),
                getString(R.string.book_2_supplier),
                getString(R.string.book_2_supplier_phone));

        // Insert third book
        insertBook(getString(R.string.book_3_name),
                Integer.parseInt(getString(R.string.book_3_price)),
                Integer.parseInt(getString(R.string.book_3_qty)),
                getString(R.string.book_3_supplier),
                getString(R.string.book_3_supplier_phone));
    }

    // Individual class to insert single book into db
    private void insertBook(String name,
                            int price,
                            int qty,
                            String supplier,
                            String suppplierPhone) {

        // Create a db SQLiteDatabase object with writable method
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValue object
        ContentValues values = new ContentValues();
        // Insert all the variables into the object
        values.put(BookEntry.COLUMN_PRODUCT_NAME, name);
        values.put(BookEntry.COLUMN_PRICE, price);
        values.put(BookEntry.COLUMN_QTY, qty);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, supplier);
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE, suppplierPhone);

        // Create a long variable to retrieve the last row ID
        long newRodId = db.insert(BookEntry.TABLE_NAME, null, values);

        // Log this id for debugging
        Log.v("CatalogActivity", "New row id: " + newRodId);
    }

    // Custom function to read the db
    private void displayDataBaseInfo() {
        // Create a db SQLiteDatabase object with readable method
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Create a string projection with contract's name fields
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QTY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE,
        };

        // Create a cursor object with db name & projection
        Cursor cursor = db.query(
                BookEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        // Using try block to run cursor command
        try {
            // Initialize a counter to store total db records
            int logTotalBooksCount = 0;

            // Create a string log variable to store record data
            // First, add a divider line for log display purpose
            String logDbRecords = "\n\n------------------------------------" +
                    "----------------------------------------------------------------------\n";

            // Create column indexes for all fields
            int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
            int qtyColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QTY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE);

            // Interate through the cursor and retrieve its value
            while (cursor.moveToNext()) {
                // Getting current value based on its column index
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQty = cursor.getInt(qtyColumnIndex);
                String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                String currentSupplierPhone = cursor.getString(supplierPhoneColumnIndex);

                // Append current value to log variable
                logDbRecords += getString(R.string.log_db_rec_id) + currentID + " | "
                        + getString(R.string.log_db_rec_name) + currentName + " | "
                        + getString(R.string.log_db_rec_price) + String.valueOf(currentPrice) + " | "
                        + getString(R.string.log_db_rec_qty) + String.valueOf(currentQty) + " | "
                        + getString(R.string.log_db_rec_supplier) + currentSupplierName + " | "
                        + getString(R.string.log_db_rec_supplier_phone) + currentSupplierPhone + "\n";

                // Increase the counter by 1
                logTotalBooksCount++;
            }

            // Closing divider for log disply purpose
            logDbRecords += "-------------------------------------------------------" +
                    "-------------------------------------------------\n\n";

            // Log the variable
            Log.d(getString(R.string.log_db_records), logDbRecords);

            // Display a Taost message for total records in the logTotalBoookCount counter
            Toast.makeText(this,
                    getString(R.string.log_total_books_count)
                            + String.valueOf(logTotalBooksCount),
                    Toast.LENGTH_SHORT).show();

        } finally {
            // Close the cursor
            cursor.close();
        }
    }
}
