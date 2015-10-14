package com.app.proj942_ns_mb;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends Activity {

    //Declaration of the layout elements
    public Button               buttonTakePhoto;
    public Button               buttonUploadPhoto;
    public Button               buttonAddPhoto;

    private TextView            textView_Result;
    private TextView            textView_Path;

    private ImageView           mImageView;

    private EditText            editTextIPByte1 ;
    private EditText            editTextIPByte2 ;
    private EditText            editTextIPByte3 ;
    private EditText            editTextIPByte4 ;
    private EditText            editTextFileName;

    private RadioGroup          radioGrp;

    public Resources res;

    //Declaration for intern variables
    private String              mCurrentPhotoPath   = null;
    private String              im_64;
    private String              stPHPFile           = null;
    private String              stOrder             = null;

    private int                 iToast2Display      = 0;

    public static String        URL                 = null;

    static final int            CAMERA_PIC_REQUEST  = 001;

    private int                 iByte1IP_Value ;
    private int                 iByte2IP_Value ;
    private int                 iByte3IP_Value ;
    private int                 iByte4IP_Value ;

    public boolean              bCheckResultIP ;
    public boolean              bCheckResultStr ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*-------- Set links for the components --------*/
        this.textView_Path          = (TextView) this.findViewById(R.id.textView_Path);
        this.textView_Result        = (TextView)this.findViewById(R.id.textView_Result);

        this.buttonTakePhoto        = (Button)this.findViewById(R.id.button_TakePicture);
        this.buttonUploadPhoto      = (Button)this.findViewById(R.id.buttonUploadPhoto);

        this.editTextIPByte1        = (EditText)this.findViewById(R.id.editText_Address_Byte1);
        this.editTextIPByte2        = (EditText)this.findViewById(R.id.editText_Address_Byte2);
        this.editTextIPByte3        = (EditText)this.findViewById(R.id.editText_Address_Byte3);
        this.editTextIPByte4        = (EditText)this.findViewById(R.id.editText_Address_Byte4);
        this.editTextFileName       = (EditText)this.findViewById(R.id.editText_PHPFile);

        this.mImageView             = (ImageView)this.findViewById(R.id.imageView_Picture);

        this.radioGrp               = (RadioGroup)this.findViewById(R.id.radioGrp);

        res                         = getResources();

        /**
         * Launch the camera
         */
        buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, R.string.toast_TakePhoto, Toast.LENGTH_LONG).show();
                dispatchTakePictureIntent();
            }
        });

        /**
         * Send the picture to the server
         */
        buttonUploadPhoto.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    //Recover each byte value of the IP
                    iByte1IP_Value = Integer.parseInt(editTextIPByte1.getText().toString());
                    iByte2IP_Value = Integer.parseInt(editTextIPByte2.getText().toString());
                    iByte3IP_Value = Integer.parseInt(editTextIPByte3.getText().toString());
                    iByte4IP_Value = Integer.parseInt(editTextIPByte4.getText().toString());

                    stPHPFile = editTextFileName.getText().toString();

                    bCheckResultIP = ToolBox.checkIP(iByte1IP_Value, iByte2IP_Value, iByte3IP_Value, iByte4IP_Value);
                    bCheckResultStr = ToolBox.checkPHPFileName(stPHPFile);

                    bCheckResultIP = ToolBox.checkIP(iByte1IP_Value, iByte2IP_Value, iByte3IP_Value, iByte4IP_Value);
                    bCheckResultStr = ToolBox.checkPHPFileName(stPHPFile);

                    //Check if the picture can be send
                    if (bCheckResultIP == true && bCheckResultStr == true && mCurrentPhotoPath != null) {
                        URL = res.getString(R.string.server_Prefix)
                                + iByte1IP_Value + '.'
                                + iByte2IP_Value + '.'
                                + iByte3IP_Value + '.'
                                + iByte4IP_Value + '/'
                                + stPHPFile
                                + res.getString(R.string.server_Suffix);

                        textView_Result.setText(URL);

                        upload();
                        iToast2Display = R.string.toast_Upload_File;
                    } else if (bCheckResultStr == true && bCheckResultIP == false) {
                        iToast2Display = R.string.toast_Error_In_IP;
                    } else if (bCheckResultStr == false && bCheckResultIP == true) {
                        iToast2Display = R.string.toast_Error_In_FileName;
                    } else if (mCurrentPhotoPath == null) {
                        iToast2Display = R.string.toast_No_Picture;
                    } else {
                        iToast2Display = R.string.toast_Error_PHP_IP;
                    }

                    Toast.makeText(MainActivity.this, iToast2Display, Toast.LENGTH_LONG).show();
                }

                //Catch if an EditText is empty
                catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, R.string.toast_Empty_Field, Toast.LENGTH_LONG).show();
                }
            }

        });

        /**
         *  Attach CheckedChangeListener to radio group
         *  */
     /*   radioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb          = (RadioButton) group.findViewById(checkedId);
                if(null!=rb && checkedId > -1){

                }

            }
        });*/
    }

    /**
     * Preparing image before uploading to server
     */

    private void upload() {
        //Image Location URL
        Log.e("path","------------" + mCurrentPhotoPath);

        //Converting image to base64
        Bitmap bmp                  = BitmapFactory.decodeFile(mCurrentPhotoPath);
        ByteArrayOutputStream bao   = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG,90,bao);
        byte[] ba                   = bao.toByteArray();
        im_64                       = Base64.encodeToString(ba, 0);


        Log.e("base64", "----" + im_64);

        //Upload image to server
        new uploadToServer().execute();
    }

    /**
     * Uploading Image to Server Using HTTPClient
     */

    public class uploadToServer extends AsyncTask<Void, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        protected void onPreExecute() {
            super.onPreExecute();

            dialog.setMessage("Image Uploading !");
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("base64", im_64));
            nameValuePairs.add(new BasicNameValuePair("ImageName", System.currentTimeMillis() + ".jpg"));
            try {
                HttpClient httpclient       = new DefaultHttpClient();
                HttpPost httppost           = new HttpPost(URL);
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response        = httpclient.execute(httppost);
                String st                    = EntityUtils.toString(response.getEntity());
                Log.v("log_tag", "In the try Loop" + st);

            } catch (Exception e) {
                Log.v("log_tag", "Error in http connection " + e.toString());
            }
            return "Success";

        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dialog.hide();
            dialog.dismiss();
        }
    }

    /**
     * Save the picture's path in order to reload the picture when the screen rotates
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Store the picture's path
        CharSequence picturePath        = textView_Path.getText();
        outState.putCharSequence("savedPicturePath", picturePath);
    }

    /**
     * Recover the picture's path and reload it
     */

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        CharSequence picturePath        = savedInstanceState.getCharSequence("savedPicturePath");
        textView_Path.setText(picturePath);

        //Recover the picture's path and load the picture
        mCurrentPhotoPath               = String.valueOf(textView_Path.getText());
        Bitmap bMap                     = BitmapFactory.decodeFile(mCurrentPhotoPath);
        mImageView.setImageBitmap(bMap);
        mImageView.setRotation(90);
    }

    /**
     * Method that takes a picture
     */
    private void dispatchTakePictureIntent(){
        Intent takePictureIntent        = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null){

            File photoFile              = null;
            try {
                photoFile = createImageFile();
            }
            catch (IOException ex){
                ex.printStackTrace();
            }

            if(photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, CAMERA_PIC_REQUEST);
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == CAMERA_PIC_REQUEST){
            ImageView mImageView        = (ImageView) findViewById(R.id.imageView_Picture);
            Bitmap bMap                 = BitmapFactory.decodeFile(mCurrentPhotoPath);
            mImageView.setImageBitmap(bMap);
            mImageView.setRotation(90);
        }
    }

    /**
     * Method that saves the picture
     */

    private File createImageFile() throws IOException{

        //Create an image file name
        String timeStamp                = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName            = "JPEG_" + timeStamp ;

        //Define the storage location
        File storageDir                 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        Log.d("DEBUG", storageDir.toString());
        File image                      = new File(storageDir, imageFileName+".jpg");
        Log.d("Debug",image.toString());

        //Save the file
        mCurrentPhotoPath               = image.getAbsolutePath();
        textView_Path.setText(mCurrentPhotoPath);

        return image;
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}