package com.android.chrishsu.gsbookstore.data;

import android.provider.BaseColumns;

// Create a custom class for DB contract
public final class BookContract {

    // Empty constructor
    private BookContract() {}

    // Create a BookEntry class based on BaseColumns class
    public static final class BookEntry implements BaseColumns {
        // Name of the table
        public final static String TABLE_NAME = "books";
        // Name of ID column
        public final static String _ID = BaseColumns._ID;
        // Name of product
        public final static String COLUMN_PRODUCT_NAME = "name";
        // Name of the price
        public final static String COLUMN_PRICE = "price";
        // Name of qty
        public final static String COLUMN_QTY = "qty";
        // Name of supplier
        public final static String COLUMN_SUPPLIER_NAME = "supplier";
        // Name of the supplier phone
        public final static String COLUMN_SUPPLIER_PHONE = "supplier_phone";
    }
}
