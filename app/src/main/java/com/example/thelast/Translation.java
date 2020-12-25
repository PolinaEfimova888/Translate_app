package com.example.thelast;

import java.util.ArrayList;
import java.util.Map;

public class Translation {
    Map<String, Language> translation;

    public String toString(){
        String languages = "";
        for (String l: translation.keySet()) {
            languages +=l + ":";
        }
        return languages;
    }

    public ArrayList<String> getFullName(){
        ArrayList<String> languages = new ArrayList<String>();

        for(Map.Entry<String, Language> item : translation.entrySet()){
            languages.add(item.getValue().name);  //получаем полное название
        }

        return languages;
    }

    public String getKey(String value) {
        String res="";
        for (Map.Entry<String, Language> item: translation.entrySet()) {
            if (item.getValue().name.equals(value)) {
                res = item.getKey();
            }
        }

        return res;
    }

}
/*
{"translation":
  {
   "af":{"name":"Африкаанс","nativeName":"Afrikaans","dir":"ltr"},
   "ar":{"name":"Арабский","nativeName":"العربية","dir":"rtl"},
   "as":{"name":"Assamese","nativeName":"Assamese","dir":"ltr"},
   ..
  }
 */