# ScannerExera
android scanner camera library from laxus exera technology

## Usage

### Include Library
Add it in your root build.gradle at the end of repositories:

```
allprojects {
   repositories {
    maven { url "https://jitpack.io" }
   }
}
```
Add dependencies :

```
dependencies {
     compile 'com.github.andrehardian:ScannerExera:1.0.2'
}

```

### Using activity
Start ScannerExera using builder pattern from your activity

1. start activity result
```
        Intent scan = new Intent(this, CameraScan.class);
        scan.putExtra(ScannerConstant.SCAN_MODES, new int[]{Symbol.CODABAR});
        startActivityForResult(scan, REQUEST_CODE);
```

You can pass an array or integers for SCAN_MODES. The exact values are specified in net.sourceforge.zbar.Symbol class:
```
public static final int NONE = 0;
public static final int PARTIAL = 1;
public static final int EAN8 = 8;
public static final int UPCE = 9;
public static final int ISBN10 = 10;
public static final int UPCA = 12;
public static final int EAN13 = 13;
public static final int ISBN13 = 14;
public static final int I25 = 25;
public static final int DATABAR = 34;
public static final int DATABAR_EXP = 35;
public static final int CODABAR = 38;
public static final int CODE39 = 39;
public static final int PDF417 = 57;
public static final int QRCODE = 64;
public static final int CODE93 = 93;
public static final int CODE128 = 128;
```

2. get data from `ScannerExera` 

```
  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            String tag = data.getStringExtra(ScannerConstant.SCAN_RESULT;
        }

    }
```

## Credit
Almost all of the code for these library projects is based on:
1. [ZBar Android SDK](https://sourceforge.net/projects/zbar/files/AndroidSDK/)

## License
ScannerExera by [andrehardian](https://github.com/andrehardian) is licensed under a [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)
