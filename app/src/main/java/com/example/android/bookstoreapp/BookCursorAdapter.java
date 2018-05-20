package com.example.android.bookstoreapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        TextView nameTextView = view.findViewById(R.id.name);
        TextView priceTextView = view.findViewById(R.id.price);
        TextView quantityTextView = view.findViewById(R.id.quantity);

        int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);

        String bookName = cursor.getString(nameColumnIndex);
        int bookPrice = cursor.getInt(priceColumnIndex);
        String bookQuantity = cursor.getString(quantityColumnIndex);

        int cents = bookPrice % 100;
        bookPrice = bookPrice / 100;
        String priceString = "Price: " + bookPrice + "," + cents + context.getString(R.string.unit_currency) + " ";
        String quantityString = "Quantity: " + bookQuantity;


        nameTextView.setText(bookName);
        priceTextView.setText(priceString);
        quantityTextView.setText(quantityString);

        final int id = cursor.getInt(cursor.getColumnIndex(BookEntry._ID));
        final int quantity = cursor.getInt(cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY));
        Button soldButton = view.findViewById(R.id.sold);
        soldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);

                int bookQuantity = quantity;
                if (bookQuantity > 0) {
                    bookQuantity--;
                    ContentValues values = new ContentValues();

                    values.put(BookEntry.COLUMN_BOOK_QUANTITY, bookQuantity);

                    context.getContentResolver().update(uri, values, null, null);

                    Toast.makeText(v.getContext(), "Book sold", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(v.getContext(), "No more books to sell", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
