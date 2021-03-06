package dead.photosaver;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Button Capture, Saveas;
    private ImageView image;
    private final int RequestCode = 1001;
    File file_before_saving;
    private final int request_Code = 20001;
    Uri uri;
    private final int ReqCode = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        image = findViewById(R.id.Image);
        Capture = findViewById(R.id.Camera);
        Saveas = findViewById(R.id.SaveAs);
        Saveas.setVisibility(View.INVISIBLE);


        Capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isMarshmellow()) {
                    if (!checkPermissionGranted())
                        AskPermission();
                    else
                        CaptureImage();
                } else
                    CaptureImage();
            }
        });


        Saveas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                startActivityForResult(intent, ReqCode);
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsForApp.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void CaptureImage() {

        if (isCameraAvailable()) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            final String name = "my-App";
            file_before_saving = GetFileForMedia(name);
            uri = Uri.fromFile(file_before_saving);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, request_Code);

        }else
            Toast.makeText(this,"Camera Not Present",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == request_Code && resultCode == RESULT_OK) {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;

            if (uri != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath(), options);

                  //  image.setImageBitmap(imageRotate);
                try {
                    ExifInterface exif = new ExifInterface(uri.getPath());
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                    Bitmap bm = rotateBitmap(bitmap, orientation);
                    image.setImageBitmap(bm);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                    ScanGallery(file_before_saving.getPath());
                    //   Capture.setVisibility(View.INVISIBLE);
                    Saveas.setVisibility(View.VISIBLE);
                }else{
                Toast.makeText(this, "Image photo is not yet set", Toast.LENGTH_LONG).show();
            }
            }




            if (requestCode == ReqCode && resultCode == RESULT_OK) {

                String name = data.getStringExtra("Name");
                name=name.toUpperCase();

                File mediaFile = GetFileForMedia(name);
                file_before_saving.renameTo(mediaFile);
                Saveas.setVisibility(View.INVISIBLE);
                //  Capture.setVisibility(View.VISIBLE);
                ScanGallery(file_before_saving.getPath());
                ScanGallery(mediaFile.getPath());


            }

    }

        //Scanning Gallery to update Photo created

        private void ScanGallery (String path){

            MediaScannerConnection.scanFile(this, new String[]{path}, null, null);

        }


        //checking if camera is available on device or not

        private boolean isCameraAvailable () {

            return getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
        }

        //  Checking For Permissions and taking Permissions

        private boolean isMarshmellow () {

            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
        }
        private boolean checkPermissionGranted () {

            int check = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return check == PackageManager.PERMISSION_GRANTED;
        }
        private void AskPermission () {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, RequestCode);

        }

        @Override
        public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults){
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == RequestCode && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //permission granted
                CaptureImage();
            } else
                Toast.makeText(this,"Access Denied",Toast.LENGTH_SHORT).show();

        }

        //Creating File to store image

        private File GetFileForMedia (String name){

            //Creating Folder Path and name
            File mediaFolder = new File(Environment.getExternalStoragePublicDirectory("Photo Saver"), name);

            if (!mediaFolder.exists()) {
                //Check each time if folder exists or not
                mediaFolder.mkdirs();
            }

            String filename = "Media-" + System.currentTimeMillis();
            File mediaFile = new File(mediaFolder.getPath() + "/" + filename + ".jpg");

            return mediaFile;

        }


    public Bitmap rotateBitmap(Bitmap bitmap, int orientation) throws IOException {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }
    }