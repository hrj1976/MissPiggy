package com.engineering.jakobsen.misspiggy;

public class ViewModel {
    private String _code;
    private String _text;
    private String _data;

    public ViewModel(String code, String text, String data) {
        this._code = code;
        this._text = text;
        this._data = data;
    }

    public String getCode() {
        return _code;
    }
    public String getText() {
        return _text;
    }
    public String getData() {
        return _data;
    }
}
