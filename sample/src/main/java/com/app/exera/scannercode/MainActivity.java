package com.app.exera.scannercode;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.app.exera.scanner.CameraScan;
import com.app.exera.scanner.utils.ScannerConstant;

import net.sourceforge.zbar.Symbol;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent scan = new Intent(this, CameraScan.class);
        scan.putExtra(ScannerConstant.SCAN_MODES, new int[]{Symbol.CODABAR});
        startActivityForResult(scan, 4);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 4) {
            resultQRCode(data);
        }

    }

    protected void resultQRCode(Intent data) {
        Toast.makeText(this, data.getStringExtra(ScannerConstant.SCAN_RESULT), Toast.LENGTH_SHORT).show();
    }
}
