package com.android.chrishsu.gsbookstore;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.chrishsu.gsbookstore.data.BookContract;

// Create a custom CursorAdapter
public class BookCursorAdapter extends CursorAdapter {
    // Setup vars
    private String bookName;
    private Double bookPrice;
    private int bookQty;
    private String bookSupplier;
    private String bookSupplierPhone;
    
    // View holder
    static class ViewHolder {
        TextView name;
        TextView price;
        TextView qty;
    }

    // Constructor
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    // Override newView method
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate list item view layout
        return LayoutInflater.from(context)
                .inflate(R.layout.list_item, parent, false);
    }

    // Override bindView method
    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        // Wire up vars & views in ViewHolder
        ViewHolder holder = new ViewHolder();
        
        holder.name = view.findViewById(R.id.book_name);
        holder.price = view.findViewById(R.id.book_price);
        holder.qty = view.findViewById(R.id.book_qty);
        final ImageView sellButton = view.findViewById(R.id.btn_sale);

        // Getting column indexes
        int nameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRICE);
        int qtyColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_QTY);
        int supplierColumnIndex = cursor.getColumnIndex(BookContract
                .BookEntry
                .COLUMN_SUPPLIER_NAME);
        int supplierPhoneColIndex = cursor.getColumnIndex(BookContract
                .BookEntry
                .COLUMN_SUPPLIER_PHONE);

        // Getting data form the indexes
        bookName = cursor.getString(nameColumnIndex);
        bookPrice = cursor.getDouble(priceColumnIndex);
        bookQty = cursor.getInt(qtyColumnIndex);

        // If qty is zero, dim the Sale button
        if (bookQty == 0) {
            sellButton.setImageAlpha(60);
        }

        bookSupplier = cursor.getString(supplierColumnIndex);
        bookSupplierPhone = cursor.getString(supplierPhoneColIndex);

        // Passing tag data to each Sale button
        sellButton.setTag(R.id.tag_id
                , cursor.getString(cursor
                        .getColumnIndex(BookContract.BookEntry._ID)));
        sellButton.setTag(R.id.book_name
                , cursor.getString(cursor
                        .getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_NAME)));
        sellButton.setTag(R.id.book_qty
                , cursor.getString(cursor
                        .getColumnIndex(BookContract.BookEntry.COLUMN_QTY)));

        // If no book name provided, override to "unknown"
        if (TextUtils.isEmpty(bookName)) {
            bookName = context.getString(R.string.unknow_book_name);
        }
        // If qty is 0 or below, override to 0
        if (bookQty <= 0) {
            bookQty = 0;
        }

        // Setup Sale button onclickListener
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve tag data
                String currentBookIdTag = (String) sellButton.getTag(R.id.tag_id);
                String currentBookName = (String) sellButton.getTag(R.id.book_name);
                String currentBookQty = (String) sellButton.getTag(R.id.book_qty);

                // If the book qty is more than 0, deduct the qty number by 1
                if (Integer.parseInt(currentBookQty) > 0) {
                    // Getting the Uri
                    Uri currentPetUri = ContentUris
                            .withAppendedId(BookContract.BookEntry.CONTENT_URI
                                    , Long.parseLong(currentBookIdTag));

                    // Wire up views
                    TextView currentBookView = view.findViewById(R.id.book_qty);
                    String currentNewBookQty = String.valueOf(Integer.parseInt(currentBookQty) - 1);
                    currentBookView.setText(currentNewBookQty);

                    // Make a Toast message indicating the book is sold.
                    Toast.makeText(context
                            , currentBookName + context.getString(R.string.toast_sold),

                            Toast.LENGTH_SHORT).show();

                    ContentValues values = new ContentValues();
                    values.put(BookContract.BookEntry.COLUMN_PRODUCT_NAME, currentBookName);
                    values.put(BookContract.BookEntry.COLUMN_QTY, currentNewBookQty);

                    // Update the new qty to db
                    int rowsAffected = context
                            .getApplicationContext()
                            .getContentResolver()
                            .update(currentPetUri
                                    , values
                                    , null
                                    , null);

                    // If successfully, update the cursor
                    if (rowsAffected > 0) {
                        changeCursor(cursor);
                        notifyDataSetChanged();
                    }

                } else {
                    // If update fails, make a Toast message and indicating it's failed.
                    Toast.makeText(context
                            , currentBookName + context.getString(R.string.toast_zero_qty)
                            , Toast.LENGTH_SHORT).show();
                }

            }
        });

        // Update each book's name, price and qty.
        holder.name.setText(bookName);
        holder.price.setText(context.getString(R.string.price_sign) + String.valueOf(bookPrice));
        holder.qty.setText(String.valueOf(bookQty));
    }
}
