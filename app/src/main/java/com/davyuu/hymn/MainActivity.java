package com.davyuu.hymn;

import android.content.Intent;
import android.database.SQLException;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText searchEditText;
    private ImageButton searchRemoveBtn;
    private ImageButton searchVoiceBtn;
    private ListView itemListView;
    private ArrayAdapter arrayAdapter;

    private List<String> itemList;
    private Map<String, Integer> itemNumberMap;
    private Map<String, Integer> imageIdMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.createDataBase();
        }
        catch(Exception e){
            Log.d("dbHelper", e.getMessage().toString());
            throw new Error("Unable to create database");
        }
        try {
            dbHelper.openDataBase();
        }catch(SQLException sqle){
            throw sqle;
        }

        searchEditText = (EditText) findViewById(R.id.search_edit_text);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() > 0){
                    searchRemoveBtn.setVisibility(View.VISIBLE);
                    searchVoiceBtn.setVisibility(View.GONE);
                }
                else{
                    searchRemoveBtn.setVisibility(View.GONE);
                    searchVoiceBtn.setVisibility(View.VISIBLE);
                }
                doSearch(s.toString());
            }
        });
        searchRemoveBtn = (ImageButton) findViewById(R.id.search_remove_btn);
        searchRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setText("");
            }
        });

        searchVoiceBtn = (ImageButton) findViewById(R.id.search_voice_btn);
        searchVoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySpeechRecognizer();
            }
        });

        itemListView = (ListView) findViewById(R.id.item_listview);

        itemList = dbHelper.getAllNames();
        itemNumberMap = dbHelper.getAllNumbers();
        imageIdMap = dbHelper.getAllImageIds();

        setArrayAdapter(formatDisplayValue(""));
    }

    private void setArrayAdapter(List<String> displayList){
        arrayAdapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, displayList);
        itemListView.setAdapter(arrayAdapter);

        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id){
                String text = ((TextView) view).getText().toString();
                String name = text.split("\\.")[1].trim();

                Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("imageId", imageIdMap.get(name));
                startActivity(intent);
            }
        });
    }

    private List<String> formatDisplayValue(String searchText){
        List<String> displayList = new ArrayList<>();
        for(String name : itemList){
            if(searchText.isEmpty() || searchText == "") {
                displayList.add(itemNumberMap.get(name) + ". " + name);
            }
            else{
                if ((itemNumberMap.get(name).toString()).contains(searchText) ||
                        name.contains(searchText)) {
                    displayList.add(itemNumberMap.get(name) + ". " + name);
                }
            }
        }
        return displayList;
    }

    public void doSearch(String searchText){
        setArrayAdapter(formatDisplayValue(searchText));
    }

    private static final int SPEECH_REQUEST_CODE = 0;
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            searchEditText.setText(spokenText);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
