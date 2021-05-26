/*
Inspired by:
https://www.tutorialspoint.com/how-to-display-a-list-of-images-and-text-in-a-listview-in-android
https://google-developer-training.github.io/android-developer-fundamentals-course-practicals/en/Unit%204/101b_p_searching_an_sqlite_database.html
https://www.tutorialspoint.com/android/android_sqlite_database.htm
https://medium.com/@evanbishop/popupwindow-in-android-tutorial-6e5a18f49cc7
https://www.tutorialspoint.com/how-to-pick-an-image-from-image-gallery-in-android
https://medium.com/@bhandarimansubh/getting-started-with-google-cloud-vision-api-with-java-cacc0b2defa0
https://stackoverflow.com/questions/13209494/how-to-get-the-full-file-path-from-uri#answer-55469368
 */

package com.example.inciresolver;

import androidx.appcompat.app.AppCompatActivity;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.inciresolver.db.DBProvider;
import com.example.inciresolver.popup.PopUpClass;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DBProvider mydb;
    private static final int PICK_IMAGE = 100;
    Uri imageUri;

    private final String TAG = "CloudVisionExample";
    static final int REQUEST_GALLERY_IMAGE = 100;
    static final int REQUEST_CODE_PRINT_API_RESULT = 101;
    static final int REQUEST_ACCOUNT_AUTHORIZATION = 102;
    static final int REQUEST_PERMISSIONS = 13;

    private static String accessToken;
    private Account mAccount;
    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mydb = new DBProvider(this);
        //mydb.insertIngredient("AQUA", "HUMEKTANT, ROZPUSZCZLNIK", "1");
        //ArrayList<String> dbcontent = mydb.getAllCotacts();
        mProgressDialog = new ProgressDialog(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, ShowIngredient.class)));
        searchView.setQueryHint("INCI name ex. AQUA");
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.search:
//                Intent intent = new Intent(getBaseContext(), ShowIngredient.class);
//                startActivity(intent);
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    public void click(View view) {
        switch(view.getId()){
            case R.id.imageButtonGallery:
                openGallery();
                break;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEND.equals(intent.getAction())) {
            PopUpClass popUpClass = new PopUpClass();
            popUpClass.showPopupWindow(findViewById(R.id.main_page));
        }
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        //Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            performCloudVisionRequest(data.getData());
        }
    }

    public void performCloudVisionRequest(Uri uri) {
        if (uri != null) {
            try {
                Bitmap bitmap = resizeBitmap(
                        MediaStore.Images.Media.getBitmap(getContentResolver(), uri));
                callCloudVision(bitmap);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void callCloudVision(final Bitmap bitmap) throws IOException {
        mProgressDialog = ProgressDialog.show(this, null,"Scanning image with Vision API...", true);

        new AsyncTask<Object, Void, BatchAnnotateImagesResponse>() {
            @Override
            protected BatchAnnotateImagesResponse doInBackground(Object... params) {
                try {
                    List<Feature> featureList = new ArrayList<>();

                    Feature textDetection = new Feature();
                    textDetection.setType("TEXT_DETECTION");
                    textDetection.setMaxResults(10);
                    featureList.add(textDetection);

                    List<AnnotateImageRequest> imageList = new ArrayList<>();
                    AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();
                    Image base64EncodedImage = getBase64EncodedJpeg(bitmap);
                    annotateImageRequest.setImage(base64EncodedImage);
                    annotateImageRequest.setFeatures(featureList);
                    imageList.add(annotateImageRequest);

                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
                    VisionRequestInitializer requestInitializer = new VisionRequestInitializer("AIzaSyBPKK0rx4CLUHAZJlQHEd_-6QCdy02u-HQ");
                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(requestInitializer);
                    Vision vision = builder.build();
                    BatchAnnotateImagesRequest batchAnnotateImagesRequest = new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(imageList);
                    Vision.Images.Annotate annotateRequest = vision.images().annotate(batchAnnotateImagesRequest);
                    annotateRequest.setDisableGZipContent(true);
                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return response;

                } catch (GoogleJsonResponseException e) {
                    Log.e(TAG, "Request error: " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "Request error: " + e.getMessage());
                }
                return null;
            }

            protected void onPostExecute(BatchAnnotateImagesResponse response) {
                mProgressDialog.dismiss();
                String[] sklad = getDetectedTexts(response);
                Intent intent = new Intent(MainActivity.this, ShowIngredient.class);
                intent.setAction(Intent.ACTION_VIEW);
                intent.putExtra("strings", sklad);
                startActivity(intent);
                System.out.println("aaa");
            }

        }.execute();
    }

    private String[] getDetectedTexts(BatchAnnotateImagesResponse response){
        StringBuilder message = new StringBuilder("");
        ArrayList<String> inredientsList = new ArrayList<>();
        List<EntityAnnotation> texts = response.getResponses().get(0)
                .getTextAnnotations();
        if (texts != null) {
            for (EntityAnnotation text : texts) {
                message.append(String.format(Locale.getDefault(), "%s",
                        text.getDescription()));
                message.append("\n");
                inredientsList.add(String.format(Locale.getDefault(), "%s",
                        text.getDescription()));
            }
        }
        String fullMessage = message.toString();
        fullMessage = fullMessage.replace("\n", "");
        int inciIndex = fullMessage.indexOf("INCI");
        int ingredientsIndex = fullMessage.indexOf("Ingredients");
        int colonIndex, dotIndex;
        if(inciIndex > -1 || ingredientsIndex > -1) {
            colonIndex = fullMessage.indexOf(":", inciIndex > ingredientsIndex ? inciIndex : ingredientsIndex);
            if(colonIndex > -1) {
                fullMessage = fullMessage.substring(colonIndex + 1);
                if(fullMessage.indexOf(" ") == 0)
                    fullMessage = fullMessage.substring(1);
                dotIndex = fullMessage.indexOf(".", colonIndex);
                if(dotIndex > -1)
                    fullMessage = fullMessage.substring(0, dotIndex);
            }
        }
        fullMessage = fullMessage.replace(", ", ",");
        fullMessage = fullMessage.replace(" ,", ",");
        List<String> ingredientsList = Arrays.asList(fullMessage.split(","));
        ArrayList<String> ingredientsList2 = new ArrayList<>();
        for(String name : ingredientsList){
            ingredientsList2.add(name.replaceAll("([a-z])([A-Z])", "$1 $2"));
        }
        String[] ingredientsArray = ingredientsList2.toArray(new String[0]);
        return ingredientsArray;
    }

    public Bitmap resizeBitmap(Bitmap bitmap) {

        int maxDimension = 1024;
        int originalWidth = bitmap.getWidth();
        int originalHeight = bitmap.getHeight();
        int resizedWidth = maxDimension;
        int resizedHeight = maxDimension;

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension;
            resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension;
            resizedWidth = maxDimension;
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);
    }

    public Image getBase64EncodedJpeg(Bitmap bitmap) {
        Image image = new Image();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        image.encodeContent(imageBytes);
        return image;
    }

}