package com.engineering.jakobsen.misspiggy;

        import java.io.Serializable;
        import java.util.ArrayList;
        import java.util.List;

public class DataObject implements Serializable {

    private int _vNr = -1;
    private int _version = -1;
    private List<Data> _data = new ArrayList<>();

    class Data implements Serializable {
        int _code;
        int _textId;
        String _data;

        public Data(int c, int t, String d) {
            _code = c;
            _textId = t;
            _data = d;
        }

        public int getCode() {return _code;}
        public int getTextId() {return _textId;}
        public String getData() {return _data;}

        @Override
        public String toString() {
            return String.format("%d, %d, %s", _code, _textId, _data);
        }
    }

    public int getVNr() {return _vNr;}
    public int getVersion() {return _version;}

    public void set_vNr(int vnr) {
        this._vNr = vnr;
    }
    public void setVersion(int version) { this._version = version; }

    public List<Data> getDataList() { return _data; }
    public void addData(int code, int text, String data) {
        _data.add(new Data(code, text, data));
    }

    @Override
    public String toString() {
        String outputStr = "";
        for (int i=0; i < _data.size(); i++) {
            outputStr += _data.get(i).toString();
        }
        return String.format("DataObject: VNR=%s, DATA=%s", _vNr, outputStr);
    }
}

