package com.android.chrishsu.gsbookstore;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.android.chrishsu.gsbookstore.data.BookContract;

public class BookCursorAdapter extends CursorAdapter{

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = view.findViewById(R.id.book_name);
        TextView priceTextView =  view.findViewById(R.id.book_price);
        TextView qtyTextView = view.findViewById(R.id.book_qty);

        int nameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRICE);
        int qtyColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_QTY);

        String bookName = cursor.getString(nameColumnIndex);
        String bookPrice = cursor.getString(priceColumnIndex);
        String bookQty = cursor.getString(qtyColumnIndex);

        if (TextUtils.isEmpty(bookName)) {
            bookName = context.getString(R.string.unknow_book_name);
        }

        if (Integer.parseInt(bookQty) < 0){
            bookQty = "0";
        }

        nameTextView.setText(bookName);
        priceTextView.setText("$" + bookPrice);
        qtyTextView.setText(bookQty);
    }
}
