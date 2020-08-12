package schaubeck.eike.qrcreator;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import jpp.qrcode.ErrorCorrection;
import schaubeck.eike.qrcreator.QRCode.QRCode;
import schaubeck.eike.qrcreator.QRCode.QRCodeException;
import schaubeck.eike.qrcreator.QRCode.encode.Encoder;

public class MainActivity extends AppCompatActivity {

    Bitmap img;
    ErrorCorrection correction;
    Spinner correctionLevel;
    QRCode qrCode = null;
    String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText msg = (EditText) findViewById(R.id.enterQRText);
        ImageView qrCodeImage = (ImageView) findViewById(R.id.QRCode);
        correctionLevel = (Spinner) findViewById(R.id.correctionLevel);
        Button save = (Button) findViewById(R.id.save);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        correctionLevel.setAdapter(adapter);
        correctionLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        correction = ErrorCorrection.LOW;
                        qrCodeImage.setImageBitmap(createQrCode(msg.getText().toString(), correction));
                        break;
                    case 1:
                        correction = ErrorCorrection.MEDIUM;
                        qrCodeImage.setImageBitmap(createQrCode(msg.getText().toString(), correction));
                        break;
                    case 2:
                        correction = ErrorCorrection.QUARTILE;
                        qrCodeImage.setImageBitmap(createQrCode(msg.getText().toString(), correction));
                        break;
                    case 3:
                        correction = ErrorCorrection.HIGH;
                        qrCodeImage.setImageBitmap(createQrCode(msg.getText().toString(), correction));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                qrCodeImage.setImageBitmap(createQrCode(msg.getText().toString(), correction));
            }


            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileOutputStream fileOutputStream = null;
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "QRCodes");
                if (!file.exists() && !file.mkdirs()) {
                    Toast.makeText(getApplicationContext(), "Fehler im Ordner, bzw. Datei", Toast.LENGTH_SHORT).show();
                    return;
                }
                String name = filename + correction.toString() + ".png";
                String fileName = file.getAbsolutePath() + "/" + name;
                File newFile = new File(fileName);
                try {
                    fileOutputStream = new FileOutputStream(newFile);
                    img.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    fileOutputStream.close();
                    Toast.makeText(getApplicationContext(), "Datei gespeichert unter " + fileName, Toast.LENGTH_SHORT).show();
                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "Datei nicht gefunden", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "IOException", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(file));
                sendBroadcast(intent);
            }
        });
    }

    public Bitmap createQrCode(String msg, ErrorCorrection correction) {
        try {
            qrCode = Encoder.createFromString(msg, correction);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "QR Message to long", Toast.LENGTH_SHORT).show();
        }
        filename = msg;
        if (filename.length() > 30) filename = filename.substring(0, 29);
        int resize;
        if (qrCode.version().number() < 10) resize = 10;
        else if (qrCode.version().number() < 20) resize = 8;
        else if (qrCode.version().number() < 30) resize = 6;
        else resize = 4;
        img = Bitmap.createBitmap(qrCode.version().size() * resize, qrCode.version().size() * resize, Bitmap.Config.ARGB_8888);
        for (int x = 0; x < qrCode.version().size(); x++) {
            for (int y = 0; y < qrCode.version().size(); y++) {
                if (qrCode.data()[x][y]) {
                    for (int ii = x * resize; ii < x * resize + resize; ii++) {
                        for (int j = y * resize; j < y * resize + resize; j++) {
                            img.setPixel(ii, j, Color.BLACK);
                        }
                    }
                } else {
                    for (int ii = x * resize; ii < x * resize + resize; ii++) {
                        for (int j = y * resize; j < y * resize + resize; j++) {
                            img.setPixel(ii, j, Color.WHITE);
                        }
                    }
                }
            }
        }
        return img;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mShare:
                Intent i = new Intent(Intent.ACTION_SEND);
                Uri uri = null;
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                try {
                    File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), filename + ".png");
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    img.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    fileOutputStream.close();
                    uri = Uri.fromFile(file);
                } catch (IOException e) {
                }
                i.setType("image/png");
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                i.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(i, "Choose an app"));
        }
        return super.onOptionsItemSelected(item);
    }
}
