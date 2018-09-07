package com.android.chrishsu.gsbookstore;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.chrishsu.gsbookstore.data.BookContract;

public class BookCursorAdapter extends CursorAdapter{
    String bookName;
    String bookPrice;
    String bookQty;
    String bookSupplier;
    String bookSupplierPhone;

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameTextView = view.findViewById(R.id.book_name);
        TextView priceTextView =  view.findViewById(R.id.book_price);
        TextView qtyTextView = view.findViewById(R.id.book_qty);
        ImageView sellButton = view.findViewById(R.id.btn_sale);

        int nameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRICE);
        int qtyColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_QTY);

        int supplierColumnIndex = cursor.getColumnIndex(BookContract
                .BookEntry
                .COLUMN_SUPPLIER_NAME);
        int supplierPhoneColIndex = cursor.getColumnIndex(BookContract
                .BookEntry
                .COLUMN_SUPPLIER_PHONE);

        bookName = cursor.getString(nameColumnIndex);
        bookPrice = cursor.getString(priceColumnIndex);
        bookQty = cursor.getString(qtyColumnIndex);

        bookSupplier = cursor.getString(supplierColumnIndex);
        bookSupplierPhone = cursor.getString(supplierPhoneColIndex);


        if (TextUtils.isEmpty(bookName)) {
            bookName = context.getString(R.string.unknow_book_name);
        }

        if (Integer.parseInt(bookQty) < 0 || bookQty == null){
            bookQty = "0";
        }

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(bookQty) > 0){
                    bookQty = String.valueOf(Integer.parseInt(bookQty)-1);

                    Toast.makeText(context, "1 x " + bookName+" sold.",
                            Toast.LENGTH_SHORT).show();

                    ContentValues values = new ContentValues();
                    values.put(BookContract.BookEntry.COLUMN_PRODUCT_NAME, bookName);
                    values.put(BookContract.BookEntry.COLUMN_PRICE, bookPrice);
                    values.put(BookContract.BookEntry.COLUMN_QTY, bookQty);
                    values.put(BookContract.BookEntry.COLUMN_SUPPLIER_NAME, bookSupplier);
                    values.put(BookContract.BookEntry.COLUMN_SUPPLIER_PHONE, bookSupplierPhone);

                    int rowsAffected = context
                            .getApplicationContext()
                            .getContentResolver()
                            .update(BookContract.BookEntry.CONTENT_URI
                                    , values
                                    , null
                                    , null);
                } else {
                    Toast.makeText(context, bookName+" has zero qty."
                            , Toast.LENGTH_SHORT).show();
                }

       }
        });

        nameTextView.setText(bookName);
        priceTextView.setText("$" + bookPrice);
        qtyTextView.setText(bookQty);
    }
}
