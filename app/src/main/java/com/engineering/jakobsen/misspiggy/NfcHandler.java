
package com.engineering.jakobsen.misspiggy;

        import android.app.Activity;
        import android.app.PendingIntent;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.nfc.NdefMessage;
        import android.nfc.NdefRecord;
        import android.nfc.NfcAdapter;
        import android.nfc.Tag;
        import android.nfc.tech.Ndef;
        import android.nfc.tech.NdefFormatable;
        import android.os.Build;
        import android.os.Parcelable;
        import android.util.Log;
        import android.widget.Toast;

        import java.io.ByteArrayInputStream;
        import java.io.ByteArrayOutputStream;
        import java.io.IOException;
        import java.io.ObjectInput;
        import java.io.ObjectInputStream;
        import java.io.ObjectOutput;
        import java.io.ObjectOutputStream;
        import java.io.UnsupportedEncodingException;
        import java.util.Arrays;

public class NfcHandler {

    NfcAdapter _nfcAdapter;
    Context _context;

    public NfcHandler(Context context) {
        _context = context;
        _nfcAdapter = NfcAdapter.getDefaultAdapter(context);
    }

    public void start() {
        Intent intent = new Intent(_context, MainActivity.class)
                .addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent
                = PendingIntent.getActivity(_context,0,intent, 0);

        IntentFilter[] intentFilter = new IntentFilter[]{};

        String[][] techList = new String[][] {
                { android.nfc.tech.NfcA.class.getName(),
                        android.nfc.tech.NdefFormatable.class.getName() } };

        if ( Build.DEVICE.matches(".*generic.*") ) {
            //clean up the tech filter when in emulator since it doesn't work properly.
            techList = null;
        }

        _nfcAdapter.enableForegroundDispatch((Activity) _context, pendingIntent, intentFilter, null);//techList);
    }

    public void stop() {
        _nfcAdapter.disableForegroundDispatch((Activity)_context);
    }

    public static boolean isNfcIntent(Intent intent) {
        return intent.hasExtra(NfcAdapter.EXTRA_TAG);
    }

    public boolean formatTag(Tag tag, NdefMessage ndefMessage) {
        try {
            NdefFormatable ndefFormat = NdefFormatable.get(tag);
            if (ndefFormat != null) {
                ndefFormat.connect();
                ndefFormat.format(ndefMessage);
                ndefFormat.close();
                return true;
            }
        } catch (Exception e) {
            Log.e("formatTag", e.getMessage());
        }
        return false;
    }

    public boolean writeNdefMessage(Tag tag, NdefMessage
            ndefMessage) {
        try {
            if (tag != null) {
                Ndef ndef = Ndef.get(tag);
                if (ndef == null) {
                    return formatTag(tag, ndefMessage);
                } else {
                    ndef.connect();
                    if (ndef.isWritable()) {
                        ndef.writeNdefMessage(ndefMessage);
                        ndef.close();
                        return true;
                    }
                    ndef.close();
                }
            }
        } catch (Exception e) {
            Log.e("formatTag", e.getMessage());
        }
        return false;
    }

    public NdefMessage readNdefMessage(Intent intent) {
        try {
            if (isNfcIntent(intent)) {

                NdefMessage ndefMessage = null;
                Parcelable[] payload = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                if(payload != null && payload.length > 0) {
                    ndefMessage = (NdefMessage) payload[0];
                }
                return ndefMessage;
            }
        } catch (Exception e) {
            Log.e("onNewIntent", e.getMessage());
        }
        return null;
    }

    public NdefRecord getNdefRecord(NdefMessage ndefMessage, int index) {
        NdefRecord ndefRecord = null;
        NdefRecord[] ndefRecords = ndefMessage.getRecords();
        if (ndefRecords != null && ndefRecords.length >0) {
            ndefRecord = ndefRecords[index];
        }
        return ndefRecord;
    }

    public boolean isNdefRecordOfTnfAndRdt(NdefRecord ndefRecord, short tnf, byte[] rdt) {
        return ndefRecord.getTnf() == tnf && Arrays.equals(ndefRecord.getType(), rdt);
    }

    public String getTextFromNdefRecord(NdefRecord ndefRecord)
    {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1,payload.length - languageSize - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        return tagContent;
    }


    public static byte[] objectToBytes(Object obj) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            out.close();
            bos.close();
        } catch (Exception e) {
            Log.e("objectToBytes", e.getMessage());
        }
        return bos.toByteArray();
    }

    public static Object bytesToObject(byte[] bytes) {
        Object o = null;
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            o = in.readObject();
            bis.close();
        } catch (Exception e) {
            Log.e("bytesToObject", e.getMessage());
        }
        return o;
    }

    public static int sizeOf(Object object) throws IOException {

        if (object == null)
            return -1;

        // Special output stream use to write the content
        // of an output stream to an internal byte array.
        ByteArrayOutputStream byteArrayOutputStream =
                new ByteArrayOutputStream();

        // Output stream that can write object
        ObjectOutputStream objectOutputStream =
                new ObjectOutputStream(byteArrayOutputStream);

        // Write object and close the output stream
        objectOutputStream.writeObject(object);
        objectOutputStream.flush();
        objectOutputStream.close();

        // Get the byte array
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        // TODO can the toByteArray() method return a
        // null array ?
        return byteArray == null ? 0 : byteArray.length;
    }

    Object handleCustomMime(NdefRecord ndefRecord) {
        byte[] payload = ndefRecord.getPayload();

        Object o = null;
        ByteArrayInputStream bis = new
                ByteArrayInputStream(payload);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            o = in.readObject();
            bis.close();
            in.close();
        } catch (Exception e) {
            Log.e("bytesToObject", e.getMessage());
        }
        return o;
    }
}



