package com.tsi.plantdiagnosissystem.ui.plantdiagnosis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tsi.plantdiagnosissystem.R;
import com.tsi.plantdiagnosissystem.controller.ImageUploadService;
import com.tsi.plantdiagnosissystem.controller.UserController;
import com.tsi.plantdiagnosissystem.controller.Utils;
import com.tsi.plantdiagnosissystem.data.model.DiagnosisResult;
import com.tsi.plantdiagnosissystem.data.model.User;
import com.tsi.plantdiagnosissystem.ui.takepicture.TakePictureActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class PlantDiagnosisActivity extends AppCompatActivity {
    TextView diseaseNameTextView;
    ImageView sampleImageImageView;
    User user;
    RadioButton yesRadioButton, noRadioButton;
    Button okButton;
    String feedBack = null;
    String responseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_diagnosis);
        Bundle extras = getIntent().getExtras();
        String imageFileName = extras.getString("file_name");
        String imageUri = extras.getString("image_uri");
        String plantName = extras.getString("plant_name");
        responseId = extras.getString("response_id");

        ArrayList<DiagnosisResult> diagnosisResults = (ArrayList<DiagnosisResult>) getIntent().getSerializableExtra("diagnosis_results");

        //read bundle
        user = UserController.getLoginInfo(this);
        
        //setActionBar
//        String titleText = "Early Blight";
//        setActionBar(titleText);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle(Html.fromHtml("<font color='#6699CC'>" + titleText + "</font>"));


        diseaseNameTextView = findViewById(R.id.diagnosisResultTextView);
        sampleImageImageView = findViewById(R.id.imageView);

        yesRadioButton = findViewById(R.id.yesRadioButton);
        noRadioButton = findViewById(R.id.noRadioButton);
        okButton = findViewById(R.id.okayButton);



        sampleImageImageView.setImageURI(Uri.parse(imageUri));
//        try {
//            InputStream imageStream = getContentResolver().openInputStream(Uri.parse(imageUri));
//            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//            sampleImage.setImageBitmap(selectedImage);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        setDummyDiagnosisResult(imageFileName);

        Collections.sort(diagnosisResults);

        //disease, diagnosis
        String diagnosisResult = "";
        for (int i = 0; i < diagnosisResults.size(); i++) {
            diagnosisResult = diagnosisResult + "Disease Name: " + diagnosisResults.get(i).getDiseaseName() +
                    "\nProbability: " + diagnosisResults.get(i).getDiagnosisProbability() + "%\n";

            if(i!=diagnosisResults.size()-1){
                diagnosisResult = diagnosisResult+"\n";
            }


        }


        setActionBar(plantName);
        diseaseNameTextView.setText(diagnosisResult);


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(feedBack != null){
                    //upload image to the server
                    new SendFeedbackAsyncTask().execute();
                } else {
                    Toast.makeText(PlantDiagnosisActivity.this, "Please select a option!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.yesRadioButton:
                if (checked)
                    feedBack = "YES";
                    break;
            case R.id.noRadioButton:
                if (checked)
                    feedBack = "NO";
                    break;
        }
    }

    private void setDummyDiagnosisResult(String imageFileName) {

        switch (imageFileName) {
            case "early_blight.JPG":
                setActionBar("Early Blight");
                diseaseNameTextView.setText("Disease Found, Probability-92.75%");
                break;
            case "late_blight.JPG":
                setActionBar("Late Blight");
                diseaseNameTextView.setText("Disease Found, Probability-98.12%");
                break;
            case "healthy_leaf.JPG":
                setActionBar("Disease not found");
                diseaseNameTextView.setText("Disease Not Found, Probability-92.75%");
                break;
            default:
                setActionBar("Not a Plant");
                diseaseNameTextView.setText("This is not a Plant!");
                break;
        }
    }

    //set custom actionBar
    public void setActionBar(String title) {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(title);
        Spannable text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorBlueGray)), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(ContextCompat.getColor(this, R.color.colorBlueGray), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                UserController.logout(PlantDiagnosisActivity.this);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //send FEEDBACK AsyncTask
    public class SendFeedbackAsyncTask extends AsyncTask<Void, Void, String> {

        private ProgressDialog pd = new ProgressDialog(PlantDiagnosisActivity.this);

        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Wait image uploading!");
            pd.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            if (Utils.isInternetAvailable()) {
                String imageSizeUnit = "KB";
                return ImageUploadService.uploadImage(user, null, "NA", feedBack, responseId,
                        "NA", "FEEDBACK");
            } else {
                Toast.makeText(PlantDiagnosisActivity.this, "Please check internet connection and try again!", Toast.LENGTH_LONG).show();
                return null;
            }
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            Response='d1=EarlyBlight#p1=92.07%;d2=EarlyBlight#p2=92.07%'
            if (result != null && !"".equalsIgnoreCase(result) && "SUCCESS".equalsIgnoreCase(result)) {
                goToCropSelection();
               
            } else {
                Toast.makeText(PlantDiagnosisActivity.this, "Please give your feedback again.", Toast.LENGTH_LONG).show();
            }

            pd.hide();
            pd.dismiss();
        }
    }

    private void goToCropSelection() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(PlantDiagnosisActivity.this);
        builder1.setMessage("Your feedback received. Go back to Home?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Utils.goToHome(PlantDiagnosisActivity.this);
                    }
                });

        builder1.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

}
