package com.example.android.bookstoreapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.support.design.widget.FloatingActionButton;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER = 0;
    BookCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, DetailsActivity.class);
                startActivity(intent);
            }
        });

        ListView listView = findViewById(R.id.book_list);
        mCursorAdapter = new BookCursorAdapter(this, null);
        listView.setAdapter(mCursorAdapter);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, DetailsActivity.class);

                Uri currentPetUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                intent.setData(currentPetUri);

                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertData();
                return true;
            case R.id.action_delete_all_entries:
                getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void insertData() {
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_BOOK_NAME, "Looking for Alaska");
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER, "Amazon.com");
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, 8);
        values.put(BookEntry.COLUMN_BOOK_PRICE, 399);
        values.put(BookEntry.COLUMN_SUPPLIER_NUMBER, "1-206-266-2992");
        getContentResolver().insert(BookEntry.CONTENT_URI, values);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        switch (id) {
            case BOOK_LOADER:
                String[] projection = {
                        BookEntry._ID,
                        BookEntry.COLUMN_BOOK_NAME,
                        BookEntry.COLUMN_BOOK_PRICE,
                        BookEntry.COLUMN_BOOK_QUANTITY,
                        BookEntry.COLUMN_BOOK_SUPPLIER,
                        BookEntry.COLUMN_SUPPLIER_NUMBER,
                };
                return new CursorLoader(this,
                        BookEntry.CONTENT_URI,
                        projection,
                        null,
                        null,
                        null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
