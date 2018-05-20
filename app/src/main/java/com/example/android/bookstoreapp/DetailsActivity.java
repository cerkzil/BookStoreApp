package com.example.android.bookstoreapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.BookContract.BookEntry;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DETAILS_LOADER = 1;
    private EditText mNameEditText;
    private EditText mSupplierNameEditText;
    private EditText mQuantityEditText;
    private EditText mPriceEditText;
    private EditText mPhoneEditText;
    private Uri currentBookUri;
    private boolean mBookHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        final Button quantitySubtract = findViewById(R.id.quantity_subtract);
        final Button quantityAdd = findViewById(R.id.quantity_add);
        final Button callSupplier = findViewById(R.id.order);

        mNameEditText = findViewById(R.id.edit_book_name);
        mSupplierNameEditText = findViewById(R.id.edit_supplier_name);
        mQuantityEditText = findViewById(R.id.edit_book_quantity);
        mPriceEditText = findViewById(R.id.edit_book_price);
        mPhoneEditText = findViewById(R.id.edit_supplier_phone);

        mNameEditText.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mPhoneEditText.setOnTouchListener(mTouchListener);

        Intent intent = getIntent();
        currentBookUri = intent.getData();

        if (currentBookUri == null) {
            setTitle("Add a Book");

            quantitySubtract.setVisibility(View.GONE);
            quantityAdd.setVisibility(View.GONE);
            callSupplier.setVisibility(View.GONE);

            mQuantityEditText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            mPhoneEditText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            invalidateOptionsMenu();
        } else {
            setTitle("Edit a Book");
            getLoaderManager().initLoader(DETAILS_LOADER, null, this);
        }

        quantitySubtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String valueQuantity = mQuantityEditText.getText().toString().trim();
                ContentValues values = new ContentValues();
                int quantity = 0;
                if (!TextUtils.isEmpty(valueQuantity)) {
                    quantity = Integer.parseInt(valueQuantity);
                    if (quantity > 0) {
                        quantity--;
                    }

                }
                values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);

                getContentResolver().update(currentBookUri, values, null, null);
            }

        });

        quantityAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String valueQuantity = mQuantityEditText.getText().toString().trim();
                int quantity = Integer.parseInt(valueQuantity);
                quantity++;
                ContentValues values = new ContentValues();

                values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);

                getContentResolver().update(currentBookUri, values, null, null);
            }

        });

        callSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String valuePhone = mPhoneEditText.getText().toString().trim();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + valuePhone));
                startActivity(intent);
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                boolean didItSave = saveBook();
                if (didItSave) {
                    finish();
                }
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean saveBook() {
        ContentValues values = new ContentValues();
        String valueName = mNameEditText.getText().toString().trim();
        String valueSupplier = mSupplierNameEditText.getText().toString().trim();
        String valueQuantity = mQuantityEditText.getText().toString().trim();
        String valuePrice = mPriceEditText.getText().toString().trim();
        String valuePhone = mPhoneEditText.getText().toString().trim();

        if (TextUtils.isEmpty((valueName))) {
            Toast.makeText(this, R.string.enter_valid_name, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty((valueSupplier))) {
            Toast.makeText(this, R.string.enter_valid_supplier, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty((valueQuantity))) {
            Toast.makeText(this, R.string.enter_valid_quantity, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty((valuePrice))) {
            Toast.makeText(this, R.string.enter_valid_price, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty((valuePhone))) {
            Toast.makeText(this, R.string.enter_valid_phone, Toast.LENGTH_SHORT).show();
            return false;
        }


        int quantity = 0;
        if (!TextUtils.isEmpty(valueQuantity)) {
            quantity = Integer.parseInt(valueQuantity);
        }

        int price = 0;
        if (!TextUtils.isEmpty(valuePrice)) {
            price = Integer.parseInt(valuePrice);
        }

        values.put(BookEntry.COLUMN_BOOK_NAME, valueName);
        values.put(BookEntry.COLUMN_BOOK_SUPPLIER, valueSupplier);
        values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);
        values.put(BookEntry.COLUMN_BOOK_PRICE, price);
        values.put(BookEntry.COLUMN_SUPPLIER_NUMBER, valuePhone);

        if (currentBookUri == null) {
            Uri uri = getContentResolver().insert(BookEntry.CONTENT_URI, values);

            long newRowId = ContentUris.parseId(uri);
            if (newRowId == -1) {
                Toast.makeText(this, getString(R.string.editor_insert_book_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_book_successful), Toast.LENGTH_SHORT).show();
            }
        } else {
            long rowsAffected = getContentResolver().update(currentBookUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_book_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_book_successful), Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        switch (id) {
            case DETAILS_LOADER:
                String[] projection = {
                        BookEntry._ID,
                        BookEntry.COLUMN_BOOK_NAME,
                        BookEntry.COLUMN_BOOK_PRICE,
                        BookEntry.COLUMN_BOOK_QUANTITY,
                        BookEntry.COLUMN_BOOK_SUPPLIER,
                        BookEntry.COLUMN_SUPPLIER_NUMBER,
                };
                return new CursorLoader(this,
                        currentBookUri,
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
        if (cursor.moveToFirst()) {

            int nameIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_NAME);
            int supplierIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_SUPPLIER);
            int quantityIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_QUANTITY);
            int priceIndex = cursor.getColumnIndex(BookEntry.COLUMN_BOOK_PRICE);
            int phoneIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NUMBER);

            mNameEditText.setText(cursor.getString(nameIndex));
            mSupplierNameEditText.setText(cursor.getString(supplierIndex));
            mQuantityEditText.setText(Integer.toString(cursor.getInt(quantityIndex)));
            mPriceEditText.setText(Integer.toString(cursor.getInt(priceIndex)));
            mPhoneEditText.setText(cursor.getString(phoneIndex));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mSupplierNameEditText.setText("");
        mQuantityEditText.setText("");
        mPriceEditText.setText("");
        mPhoneEditText.setText("");
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteBook() {
        if (currentBookUri != null) {
            int rowsDeleted = getContentResolver().delete(currentBookUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_book_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_book_successful), Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (currentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

}
