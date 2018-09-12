package com.android.chrishsu.gsbookstore;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.chrishsu.gsbookstore.data.BookContract;

public class EditorActivity
        extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // Setup vars
    private EditText mNameEditText;
    private EditText mPriceEditText;
    private EditText mQtyEditText;
    private EditText mSupplierEditText;
    private EditText mSupplierPhoneEditText;
    private ImageButton mQtyBtnPlus;
    private ImageButton mQtyBtnMinus;
    private Button contactButton;

    private Uri mCurrentBookUri;

    private static final int EDITOR_LOADER = 1;

    private boolean mBookHasChanged = false;

    // Override onTouchListener method to watch text fields
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            // If this triggered, change mBookHasChanged to true
            mBookHasChanged = true;
            return false;
        }
    };

    // Override onPrepareOptionsMenu to remove delete function
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // This is for adding new book when mCurrentBookUri is null
        // When adding a new book, we do not need delete function
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    // Override onCreate method
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Getting intent data
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        // Wire up views
        mNameEditText = findViewById(R.id.edit_book_name);
        mPriceEditText = findViewById(R.id.edit_book_price);
        mQtyEditText = findViewById(R.id.edit_book_qty);
        mSupplierEditText = findViewById(R.id.edit_book_supplier);
        mSupplierPhoneEditText = findViewById(R.id.edit_book_supplier_phone);
        contactButton = findViewById(R.id.btn_contact_supplier);

        // If intent doesn't contain URI, then it's for create new book record
        // Else, it's using the URI data and edit the book record
        if (mCurrentBookUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_book));
            invalidateOptionsMenu();
            contactButton.setVisibility(View.INVISIBLE);
        } else {
            setTitle(getString(R.string.editor_acitivity_titile_edit_book));
            contactButton.setVisibility(View.VISIBLE);
            // Start loader
            getLoaderManager().initLoader(EDITOR_LOADER, null, this);
        }

        // Hide the keyboard initially
        this.getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Wire up Qty buttons view
        mQtyBtnPlus = findViewById(R.id.edit_book_qty_btn_plus);
        mQtyBtnMinus = findViewById(R.id.edit_book_qty_btn_minus);

        // Logic to add qty when press +/-
        // Logic to deduct qty when press plus icon
        mQtyBtnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When click, add qty by one
                int currentQty = Integer.parseInt(mQtyEditText.getText().toString());
                mQtyEditText.setText(String.valueOf(currentQty + 1));
            }
        });

        // Logic to deduct qty when press minus icon
        mQtyBtnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // When click, deduct qty by one
                int currentQty = Integer.parseInt(mQtyEditText.getText().toString());
                // Prevent qty number from deduct less than 0
                if (currentQty > 0) {
                    mQtyEditText.setText(String.valueOf(currentQty - 1));
                }
            }
        });

        // Setup onclickListener for contactbutton to dial the number
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Getting the number value
                String number = mSupplierPhoneEditText.getText().toString().trim();

                // Setup dial intent
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);

                // If the number is not empty, send the intend
                if (!TextUtils.isEmpty(number)) {
                    dialIntent.setData(Uri.parse(getString(R.string.dial_intent_tel_uri) + number));
                    startActivity(dialIntent);
                } else {
                    // Otherwise, make a Toast message and do nothing
                    Toast.makeText(getApplicationContext()
                            , getString(R.string.dial_error)
                            , Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    // Override onCreateLoader method
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Setup projection
        String[] projection = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.COLUMN_PRODUCT_NAME,
                BookContract.BookEntry.COLUMN_PRICE,
                BookContract.BookEntry.COLUMN_QTY,
                BookContract.BookEntry.COLUMN_SUPPLIER_NAME,
                BookContract.BookEntry.COLUMN_SUPPLIER_PHONE
        };

        // Return CursorLoader
        return new CursorLoader(this,
                mCurrentBookUri,
                projection,
                null,
                null,
                null);
    }

    // Override onCreateOptionsMenu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menus
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    // Override onOptionsItemSeledted
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Determined which menu item was selected
        switch (item.getItemId()) {
            case R.id.action_save:
                // When [save] was clicked, save the book data
                saveBook();
                return true;

            case R.id.action_delete:
                // When [delete] was clicked, show the alert dialog first
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                // Check if fields were changed, if not, navigate back to parent activity
                if (!mBookHasChanged) {
                    //Navigate back to parent activity
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                // Setup dialog for unsaved changes
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show dialog that notifies of unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }

        // Return OptionsItems
        return super.onOptionsItemSelected(item);
    }

    // Override onLoadFinished
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
            Double price = data.getDouble(priceColumnIndex);
            int qty = data.getInt(qtyColumnIndex);
            String suppplier = data.getString(supplierColumnIndex);
            String supplierPhone = data.getString(supplierPhoneColumnIndex);

            // Set the data to the view
            mNameEditText.setText(name);
            mPriceEditText.setText(String.valueOf(price));
            mQtyEditText.setText(String.valueOf(qty));
            mSupplierEditText.setText(suppplier);
            mSupplierPhoneEditText.setText(supplierPhone);

            // Setup OntouchListeners on the fields
            mNameEditText.setOnTouchListener(mTouchListener);
            mPriceEditText.setOnTouchListener(mTouchListener);
            mQtyEditText.setOnTouchListener(mTouchListener);
            mSupplierPhoneEditText.setOnTouchListener(mTouchListener);
            mSupplierPhoneEditText.setOnTouchListener(mTouchListener);
            mQtyBtnPlus.setOnTouchListener(mTouchListener);
            mQtyBtnMinus.setOnTouchListener(mTouchListener);

        }
    }

    // Override onBackPressed
    @Override
    public void onBackPressed() {
        // If the book never changed, continue
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // If something touched, show alert dialog to confirm
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                finish();
            }
        };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    // Override onLoaderReset
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Reset all fields to default value
        mNameEditText.setText(R.string.def_name_value);
        mPriceEditText.setText(R.string.def_price_value);
        mQtyEditText.setText(R.string.def_qty_value);
        mSupplierEditText.setText(R.string.def_supplier_value);
        mSupplierPhoneEditText.setText(R.string.def_supplier_phone_value);
    }

    // Save book function
    private void saveBook() {

        // Read the data from inputs
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String qtyString = mQtyEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();
        String supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();

        // In either add new or edit mode:
        // If fields are blank, promopt user to enter all fields
        if (TextUtils.isEmpty(nameString)
                || TextUtils.isEmpty(priceString)
                || TextUtils.isEmpty(qtyString)
                || TextUtils.isEmpty(supplierString)
                || TextUtils.isEmpty(supplierPhoneString)
                ) {

            // Make a Toast message
            Toast.makeText(this
                    , getString(R.string.editor_all_fields_required)
                    , Toast.LENGTH_SHORT).show();

        } else {

            // Sanitize price & qty fields and filling default values
            Double sanitized_price = 1.00;
            if (!TextUtils.isEmpty(priceString)) {
                sanitized_price = Double.parseDouble(priceString);
            }

            int sanitized_qty = 0;
            if (!TextUtils.isEmpty(qtyString)) {
                sanitized_qty = Integer.parseInt(qtyString);
            }

            // Create content value and retrieve data
            ContentValues contentValues = new ContentValues();
            contentValues.put(BookContract.BookEntry.COLUMN_PRODUCT_NAME, nameString);
            contentValues.put(BookContract.BookEntry.COLUMN_PRICE, sanitized_price);
            contentValues.put(BookContract.BookEntry.COLUMN_QTY, sanitized_qty);
            contentValues.put(BookContract.BookEntry.COLUMN_SUPPLIER_NAME, supplierString);
            contentValues.put(BookContract.BookEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneString);

            // Depending Uri, do insert or update the current book data
            if (mCurrentBookUri == null) {
                // Creating new book into db
                Uri newUri = getContentResolver()
                        .insert(BookContract.BookEntry.CONTENT_URI, contentValues);

                if (newUri == null) {
                    // Show Toast: Insert fail
                    Toast.makeText(this
                            , getString(R.string.editor_insert_book_failed)
                            , Toast.LENGTH_SHORT).show();
                } else {
                    // Show Toast: Insert successful
                    Toast.makeText(this
                            , getString(R.string.editor_insert_book_successful)
                            , Toast.LENGTH_SHORT).show();
                    finish();
                }

            } else {
                // Update existing book into db
                int rowsAffected = getContentResolver().update(mCurrentBookUri
                        , contentValues
                        , null
                        , null);

                // Show toast message for successful or fail
                if (rowsAffected == 0) {
                    Toast.makeText(this
                            , getString(R.string.editor_update_book_failed)
                            , Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this
                            , getString(R.string.editor_update_book_successful)
                            , Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    // Function to show alert dialog when unsaved change occurred
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener
                                                  discardButtonClickListener) {
        // Create alertdialog builder and set the message
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Function to show alert dialog when delete trigger
    private void showDeleteConfirmationDialog() {
        // Create alertdialog builder and set the message
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Function to delete a book
    private void deleteBook() {
        // Ensure there's Uri to confirm deletion
        if (mCurrentBookUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri
                    , null
                    , null);

            // If no rows were deleted, make a Toast message indicating it's failed
            if (rowsDeleted == 0) {
                Toast.makeText(this
                        , getString(R.string.editor_delete_book_failed)
                        , Toast.LENGTH_SHORT).show();
            } else {
                // Othersie, make a Toast message indicating it's successful
                Toast.makeText(this
                        , getString(R.string.editor_delete_book_successful)
                        , Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}
