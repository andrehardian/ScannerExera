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
     compile 'com.github.andrehardian:scannerexera:1.0.0'
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

2. get data from `ScannerExera` 

```
  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            data.getStringExtra(ScannerConstant.SCAN_RESULT
        }

    }
```

## Credit
Almost all of the code for these library projects is based on:
1. [ZBar Android SDK](https://sourceforge.net/projects/zbar/files/AndroidSDK/)

## License
ScannerExera by [andrehardian](https://github.com/andrehardian) is licensed under a [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)
