package com.example.simpletodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import org.apache.commons.io.FileUtils;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20 ;

    List<String> items;

    Button btnAdd;
    EditText editem;
    RecyclerView rvitems;
    itemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        editem = findViewById(R.id.edItem);
        rvitems = findViewById(R.id.rvitems);

        loadItems();

        itemsAdapter.OnLongClickListener onLongClickListener = new itemsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                // delete item from the model
                items.remove(position);
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(),"item removed",Toast.LENGTH_SHORT).show();
                saveItems();


            }
        };
        itemsAdapter.OnClickListener onClickListener = new itemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                Log.d("mainActivity", "single click at postiion" + position);
                // create new activity
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                // pass relevant date
                i.putExtra(KEY_ITEM_TEXT,items.get(position));
                i.putExtra(KEY_ITEM_POSITION,position);
                // display the activity
                startActivityForResult(i,EDIT_TEXT_CODE);

            }
        };
        itemsAdapter = new itemsAdapter(items,onLongClickListener, onClickListener);
        rvitems.setAdapter(itemsAdapter);
        rvitems.setLayoutManager(new LinearLayoutManager(this));
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todoItem = editem.getText().toString();
                // add item to model
                items.add(todoItem);
                itemsAdapter.notifyItemInserted(items.size() -1);
                editem.setText("");
                Toast.makeText(getApplicationContext(),"item added",Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });
    }

    // handle result of edit activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            // retrive value
            String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            // update value
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);
            items.set(position,itemText);
            itemsAdapter.notifyItemChanged(position);
            saveItems();
            Toast.makeText(getApplicationContext(),"item Changed",Toast.LENGTH_SHORT).show();


        } else {
            Log.w("Mainactivity", "Unknown call to onActivity result");
        }
    }

    private File getDataFile() {
        return new File(getFilesDir(), "data.txt");
    }

    // this function will load item by reading every line of data file
    private void loadItems() {
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("Mainactivity", "Error reading items", e);
            items = new ArrayList<>();
        }
    }
    // this function will save items that was saved
    private void saveItems() {
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("Mainactivity", "Error writing items", e);
        }
    }
}