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
        TextView nameTextView = view.findViewById(R.id.name);
        TextView supplierTextView =  view.findViewById(R.id.supplier);

        int nameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRODUCT_NAME);
        int supplierColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_SUPPLIER_NAME);

        String bookName = cursor.getString(nameColumnIndex);
        String bookSupplier = cursor.getString(supplierColumnIndex);

        if (TextUtils.isEmpty(bookName)) {
            bookSupplier = context.getString(R.string.unknow_supplier);
        }

        nameTextView.setText(bookName);
        supplierTextView.setText(bookSupplier);
    }
}
