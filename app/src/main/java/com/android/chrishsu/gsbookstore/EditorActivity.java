package com.android.chrishsu.gsbookstore;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.android.chrishsu.gsbookstore.data.BookContract;

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
        } else {
            setTitle(getString(R.string.editor_acitivity_titile_edit_book));
        }
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
}
