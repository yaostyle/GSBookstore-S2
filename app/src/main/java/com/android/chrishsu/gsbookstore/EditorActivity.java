package com.android.chrishsu.gsbookstore;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.chrishsu.gsbookstore.data.BookContract;

import org.w3c.dom.Text;

public class EditorActivity
        extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQtyEditText;
    private EditText mSupplierEditText;
    private EditText mSupplierPhoneEditText;

    private Uri mCurrentBookUri;

    private static final int EDITOR_LOADER = 1;

    private boolean mBookHasChanged = false;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // This is for adding new book
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Getting intent data
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        // If intent doesn't contain URI, then it's for create new book record
        // Else, it's using the URI data and edit the book record
        if (mCurrentBookUri ==  null) {
            setTitle(getString(R.string.editor_activity_title_new_book));
            invalidateOptionsMenu();
            Button contactButton = findViewById(R.id.btn_contact_supplier);
            contactButton.setVisibility(View.INVISIBLE);
        } else {
            setTitle(getString(R.string.editor_acitivity_titile_edit_book));
        }

        // Wire up views
        mNameEditText = findViewById(R.id.edit_book_name);
        mPriceEditText = findViewById(R.id.edit_book_price);
        mQtyEditText = findViewById(R.id.edit_book_qty);
        mSupplierEditText = findViewById(R.id.edit_book_supplier);
        mSupplierPhoneEditText = findViewById(R.id.edit_book_supplier_phone);

        // Hide the keyboard initially
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Wire up Qty buttons view
        ImageButton mQtyBtnPlus = findViewById(R.id.edit_book_qty_btn_plus);
        ImageButton mQtyBtnMinus = findViewById(R.id.edit_book_qty_btn_minus);

        // Logic to add qty when press +/-
        mQtyBtnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentQty = Integer.parseInt(mQtyEditText.getText().toString());
                mQtyEditText.setText(String.valueOf(currentQty+1));
            }
        });

        mQtyBtnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentQty = Integer.parseInt(mQtyEditText.getText().toString());
                // Prevent qty number from deduct less than 0
                if (currentQty > 0) {
                    mQtyEditText.setText(String.valueOf(currentQty - 1));
                }
            }
        });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.COLUMN_PRODUCT_NAME,
                BookContract.BookEntry.COLUMN_PRICE,
                BookContract.BookEntry.COLUMN_QTY,
                BookContract.BookEntry.COLUMN_SUPPLIER_NAME,
                BookContract.BookEntry.COLUMN_SUPPLIER_PHONE
        };

        return new CursorLoader(this,
                mCurrentBookUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                //TODO Save the book
                saveBook();
                finish();
                return true;

            case R.id.action_delete:
                //TODO Delete Confirmation Dialog
                return true;

            case android.R.id.home:
                if (!mBookHasChanged) {
                    //Navigate back to parent activity
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                //TODO: Setup dialog for unsaved changes
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            // Getting column indexes
            int nameColumnIndex = data
                    .getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = data
                    .getColumnIndex(BookContract.BookEntry.COLUMN_PRICE);
            int qtyColumnIndex = data
                    .getColumnIndex(BookContract.BookEntry.COLUMN_QTY);
            int supplierColumnIndex = data.
                    getColumnIndex(BookContract.BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = data
                    .getColumnIndex(BookContract.BookEntry.COLUMN_SUPPLIER_PHONE);

            // Getting data from the column indexes
            String name = data.getString(nameColumnIndex);
            String price = data.getString(priceColumnIndex);
            String qty = data.getString(qtyColumnIndex);
            String suppplier = data.getString(supplierColumnIndex);
            String supplierPhone = data.getString(supplierPhoneColumnIndex);

            // Set the data to the view
            mNameEditText.setText(name);
            mPriceEditText.setText(price);
            mQtyEditText.setText(qty);
            mSupplierEditText.setText(suppplier);
            mSupplierPhoneEditText.setText(supplierPhone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mPriceEditText.setText("0");
        mQtyEditText.setText("1");
        mSupplierEditText.setText("");
        mSupplierPhoneEditText.setText("");
    }

    private void saveBook() {
        // Read the data from inputs
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String qtyString = mQtyEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();
        String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();

        // Double check if it's for new book
        // If fields are blank, return early
        if (mCurrentBookUri == null
                && TextUtils.isEmpty(nameString)
                && TextUtils.isEmpty(priceString)
                && TextUtils.isEmpty(qtyString)
                && TextUtils.isEmpty(supplierString)
                && TextUtils.isEmpty(supplierPhoneString)) {

            Toast.makeText(this, getString(R.string.editor_insert_book_failed), Toast.LENGTH_SHORT).show();
            return;
        }

        // Create content value and retrieve data
        ContentValues contentValues = new ContentValues();
        contentValues.put(BookContract.BookEntry.COLUMN_PRODUCT_NAME, nameString);
        contentValues.put(BookContract.BookEntry.COLUMN_PRICE, priceString);
        contentValues.put(BookContract.BookEntry.COLUMN_QTY, qtyString);
        contentValues.put(BookContract.BookEntry.COLUMN_SUPPLIER_NAME, supplierString);
        contentValues.put(BookContract.BookEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneString);

        // Sanitize price & qty fields and filling default values
        String sanitized_price = "0";
        if (!TextUtils.isEmpty(priceString)) {
            sanitized_price = priceString;
        }
        contentValues.put(BookContract.BookEntry.COLUMN_PRICE, sanitized_price);

        String sanitized_qty = "0";
        if (!TextUtils.isEmpty(qtyString)) {
            sanitized_qty = qtyString;
        }
        contentValues.put(BookContract.BookEntry.COLUMN_PRICE, sanitized_qty);

        // Depending Uri, do insert or update the current book data
        if (mCurrentBookUri == null) {
            // Creating new book into db
            Uri newUri = getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, contentValues);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_book_failed), Toast.LENGTH_SHORT).show();
            }

        } else {
            //TODO: Process existing book, update it
        }
    }
}
