package com.example.thelast;

import java.util.ArrayList;
import java.util.Map;

class ToText {
    String to;
    String text;
}
//перевод полученного ответа в строку
public class TranslatedText {
    ArrayList<ToText> elem;

    public String getResult() {
        String res="";

        for (ToText t: elem) {
            res += "To language: " + t.to.toString() + "/n Your Text : "+t.text.toString();
        }

        return res;
    }
}
