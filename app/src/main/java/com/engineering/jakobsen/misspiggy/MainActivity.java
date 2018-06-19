package com.engineering.jakobsen.misspiggy;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.engineering.jakobsen.misspiggy.NfcHandler.isNfcIntent;
import static com.engineering.jakobsen.misspiggy.NfcHandler.objectToBytes;
import static com.engineering.jakobsen.misspiggy.NfcHandler.sizeOf;

public class MainActivity extends AppCompatActivity {

    static final int READ_MODE = 0;
    static final int WRITE_MODE = 1;
    static final int FORMAT_MODE = 2;

    int _mode =  READ_MODE;
    ArrayList<ViewModel> _dataList;
    NfcHandler _nfcHandler;
    ListViewAdapter _adapter;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (isNfcIntent(intent)) {

            switch (_mode) {
                case WRITE_MODE:
                    writeDataObject(intent);
                    break;
                case READ_MODE:
                    readDataObject(intent);
                    break;
                case FORMAT_MODE:
                    Toast.makeText(this, "Not implemented yet", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(this, "Not implemented yet", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    private void writeDataObject(Intent intent) {
        try {
            if (isNfcIntent(intent)) {

                DataObject dataObject = new DataObject();
                dataObject.set_vNr(122);
                dataObject.setVersion(5);
                dataObject.addData(1,0,"AUT");
                dataObject.addData(33,1,"5");
                dataObject.addData(34,2,"63");
                dataObject.addData(35,3,"46");
                dataObject.addData(37,4,"2T");

                int size = sizeOf(dataObject);
                if(size > 1024) {
                    Toast.makeText(this, "Object too large!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Object size ok", Toast.LENGTH_SHORT).show();
                }

                NdefRecord mimeRecord = NdefRecord.createMime("application/vnd." + this.getPackageName() + ".dataobject", objectToBytes(dataObject));
                NdefMessage ndefMessage = new NdefMessage(new NdefRecord[] {
                        mimeRecord });
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                Boolean res = _nfcHandler.writeNdefMessage(tag, ndefMessage);
                if (res) {
                    Log.d("", "Tag written");
                    Toast.makeText(this, "Tag written!", Toast.LENGTH_SHORT).
                            show();
                } else {
                    Toast.makeText(this, "Tag write failed!", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Tag write failed!", Toast.LENGTH_SHORT).show();
            Log.e("onNewIntent", e.getMessage());
        }
    }

    private void readDataObject(Intent intent) {
        NdefMessage ndefMessage = _nfcHandler.readNdefMessage(intent);
        if (ndefMessage != null) {
            NdefRecord ndefRecord = _nfcHandler.getNdefRecord(ndefMessage, 0);

            if (ndefRecord != null) {
                Toast.makeText(this, String.format("Ndef record found! data length: %s type: %s",
                        ndefRecord.getPayload().length, ndefRecord.getType()), Toast.LENGTH_SHORT).show();

                String mimeStr = "application/vnd." + this.getPackageName() + ".dataobject";

                String rxMime = ndefRecord.toMimeType();

                if(rxMime.equals(mimeStr)) {

                    DataObject obj = (DataObject) _nfcHandler.handleCustomMime(ndefRecord);

                    _adapter.notifyDataSetInvalidated();
                    populateList(obj);
                    _adapter.notifyDataSetChanged();

                    String dump = obj.toString();
                    Log.d("", dump);
                    Toast.makeText(this, String.format("Success: Record is DataObject formatted. Contents: %s", dump), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failure: Record is not DataObject formatted.", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(this, "No Ndef record found.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "No Ndef message found.", Toast.LENGTH_LONG).show();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _nfcHandler = new NfcHandler(this);

        _dataList = new ArrayList<ViewModel>();
        ListView lview = findViewById(R.id.listview);
        _adapter = new ListViewAdapter(this, _dataList);
        lview.setAdapter(_adapter);

        lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String code = ((TextView)view.findViewById(R.id.code)).getText().toString();
                String text = ((TextView)view.findViewById(R.id.text)).getText().toString();
                String data = ((TextView)view.findViewById(R.id.data)).getText().toString();

                //Toast.makeText(getApplicationContext(), "S no : " + sno +"\n" +"Product : " + product +"\n" +"Category : " +category +"\n" +"Price : " +price, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClickSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void onClickShowAll(View view) {
        if (_mode == READ_MODE) {
            _mode = WRITE_MODE;
            ((CheckBox)view.findViewById(R.id.checkBox)).setText("WRITE_MODE");
        } else {
            _mode = READ_MODE;
            ((CheckBox)view.findViewById(R.id.checkBox)).setText("READ_MODE");
        }
    }

    private void populateList(DataObject dataObject) {

        String[] guiText_DK = new String[12];
        guiText_DK[0] = "Manuel betjening";
        guiText_DK[1] = "Alder, dage";
        guiText_DK[2] = "Slukdag";
        guiText_DK[3] = "Temeperatur dag 2";
        guiText_DK[4] = "Temeperatur dag 14";
        guiText_DK[5] = "Temeperatur dag 28";
        guiText_DK[6] = "Aktuel varme, %";
        guiText_DK[7] = "Aktuel temperatur";
        guiText_DK[8] = "Aktuel ønsket temperatur";
        guiText_DK[9] = "Alarm";
        guiText_DK[10] = "";

        String[] guiText_EN = new String[12];
        guiText_EN[0] = "Manuel operation";
        guiText_EN[1] = "Age, days";
        guiText_EN[2] = "Off day";
        guiText_EN[3] = "Temperature day 2";
        guiText_EN[4] = "Temperature day 14";
        guiText_EN[5] = "Temperature day 28";
        guiText_EN[6] = "Actual heat, %";
        guiText_EN[7] = "Actual temperature";
        guiText_EN[8] = "Actual requested temperature";
        guiText_EN[9] = "Alarm";
        guiText_EN[10] = "";


        String[] guiText = guiText_DK;
        ViewModel item = null;
        for (int i=0; i < dataObject.getDataList().size(); i++) {
            DataObject.Data data = dataObject.getDataList().get(i);
            item = new ViewModel(String.format("%d", data.getCode()), guiText[data.getTextId()], data.getData());
            _dataList.add(item);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        _nfcHandler.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        _nfcHandler.stop();
    }

}



