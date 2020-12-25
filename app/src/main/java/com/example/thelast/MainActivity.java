package com.example.thelast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


import com.google.gson.Gson;


public class MainActivity extends AppCompatActivity {
//для сохранения языков
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_LANG = "lang";

    SharedPreferences mSettings;

    String to_lang;

    Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create()) // ответ от сервера в виде строки
            .baseUrl(AzureTranslationAPI.API_URL) // адрес API сервера
            .build();

    AzureTranslationAPI api = retrofit.create(AzureTranslationAPI.class); // описываем, какие функции реализованы
    EditText to_translate;

    Spinner lang_list;
    TextView selection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//находим вьюверы и сохраняем в переменную
        lang_list = (Spinner) findViewById(R.id.spinner);
        selection = (TextView) findViewById(R.id.text2);
        to_translate = (EditText) findViewById(R.id.input);
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        Call<Translation> call = api.getLanguages(); // создаём объект-вызов
        call.enqueue(new LanguagesCallback());

        setupSpinner(lang_list, selection);

    }
//сохранение языков, вставка в спиннер
    private void setupSpinner(Spinner lang_list, TextView selection) {
        Translation json_langs = getTranslation();
        if (json_langs == null)
            return;
        ArrayList<String> langs = json_langs.getFullName();


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, langs);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lang_list.setAdapter(adapter);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Получаем выбранный объект
                String item = (String) parent.getItemAtPosition(position);
                selection.setText(item);
                to_lang = json_langs.getKey(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        lang_list.setOnItemSelectedListener(itemSelectedListener);
    }
//после нажатия кнопки создаем объект-вызов для получения ответа
    //объект выдается в виде массива
    public void onMyButtonClick(View v) {
        String input_text = to_translate.getText().toString();

        myObject myObj = new myObject();
        myObj.Text = input_text;

        Log.d("INPUT", "response: " + myObj.Text);

        myObject[] t;
        t = new myObject[1];
        t[0] = myObj;

        Call<List<TranslatedText>> input_call = api.translate(to_lang, t);
        input_call.enqueue(new TranslateCallback());
    }
//достаем из сохраненных языки
    protected Translation getTranslation() {
        Gson gson = new Gson();

        String strlangs = "";
        if (mSettings.contains(APP_PREFERENCES_LANG)) {
            strlangs = mSettings.getString(APP_PREFERENCES_LANG, "");
        }

        Translation json_langs = gson.fromJson(strlangs, Translation.class);

        return json_langs;
    }
//проверяется успешность проходящего вызова
    class LanguagesCallback implements Callback<Translation> {

        @Override
        public void onResponse(Call<Translation> call, Response<Translation> response) {

            if (response.isSuccessful()) {
                Log.d("mytag", "response: " + response.body());
                saveLang(response);
                setupSpinner(lang_list, selection);

            } else {
                Log.d("mytag", "error " + response.code());
                Toast toast = Toast.makeText(getApplicationContext(),
                        "error " + response.errorBody(), Toast.LENGTH_SHORT);
                toast.show();
            }
        }

//сохраняем языки в APP_PREFERENCES
        protected void saveLang(Response<Translation> response) {

            Gson gson = new Gson();
            String jsonLang = gson.toJson(response.body());

            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString(APP_PREFERENCES_LANG, jsonLang);
            editor.apply();
        }


        @Override
        public void onFailure(Call<Translation> call, Throwable t) {
            Log.d("mytag", "error " + t.getLocalizedMessage());

            Toast toast = Toast.makeText(getApplicationContext(),
                    "error " + t.getLocalizedMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

//для обработки и перевода
    class TranslateCallback implements Callback<List<TranslatedText>> {

        @Override
        public void onResponse(Call<List<TranslatedText>> call, Response<List<TranslatedText>> response) {

            if (response.isSuccessful()) {
                Log.d("translate", "response: " + response.body());

                Gson gson = new Gson();
                String jsonLang = gson.toJson(response.body());
                Log.d("kek", "error " + jsonLang);


            } else {
                Log.d("translate", "error " + response.code());
                Toast toast = Toast.makeText(getApplicationContext(),
                        "error " + response.errorBody(), Toast.LENGTH_SHORT);
                toast.show();
            }
        }
//при ошибке выдает запись в журнал
        @Override
        public void onFailure(Call<List<TranslatedText>> call, Throwable t) {
            Log.d("translate", "error " + t.getLocalizedMessage());

            Toast toast = Toast.makeText(getApplicationContext(),
                    "error " + t.getLocalizedMessage(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}