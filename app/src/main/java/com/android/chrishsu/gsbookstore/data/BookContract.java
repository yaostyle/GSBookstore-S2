package com.android.chrishsu.gsbookstore.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

// Create a custom class for DB contract
public final class BookContract {

    // Empty constructor
    private BookContract() {}

    public static final String CONTENT_AUTHROITY = "com.android.chrishsu.gsbookstore";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHROITY);
    public static final String PATH_BOOKS = "books";

    // Create a BookEntry class based on BaseColumns class
    public static final class BookEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

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

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/"
                + CONTENT_AUTHROITY
                + "/"
                + PATH_BOOKS;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/"
                + CONTENT_AUTHROITY
                + "/"
                + PATH_BOOKS;


    }
}
