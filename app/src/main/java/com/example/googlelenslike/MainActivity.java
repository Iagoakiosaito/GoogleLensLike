package com.example.googlelenslike;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ImageView captureIV;
    private Button snapBtn, searchBtn;
    private RecyclerView resultRecyclerView;
    private SearchRecyclerViewAdapter searchRecyclerViewAdapter;
    private ArrayList<SearchRecyclerViewModal> searchRecyclerViewModalArrayList;
    int REQUEST_CODE = 1;
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private ProgressBar pbloading;
    private Bitmap imageBitmap;
    String title, link, displayedLink, snippet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        askForPermission();

        captureIV = findViewById(R.id.imageResult);
        snapBtn = findViewById(R.id.btnSnap);
        searchBtn = findViewById(R.id.btnResults);
        pbloading = findViewById(R.id.pBLoading);
        resultRecyclerView = findViewById(R.id.recyclerViewResults);
        searchRecyclerViewModalArrayList = new ArrayList<>();
        searchRecyclerViewAdapter = new SearchRecyclerViewAdapter(this, searchRecyclerViewModalArrayList);
        resultRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        resultRecyclerView.setAdapter(searchRecyclerViewAdapter);

        snapBtn.setOnClickListener(view -> {
            searchRecyclerViewModalArrayList.clear();
            searchRecyclerViewAdapter.notifyDataSetChanged();
            takePictureIntent();
        });

        searchBtn.setOnClickListener(view -> {
            searchRecyclerViewModalArrayList.clear();
            searchRecyclerViewAdapter.notifyDataSetChanged();
            pbloading.setVisibility(View.VISIBLE);
            getResults();
        });

    }

    private void askForPermission() {
        if (ContextCompat.checkSelfPermission(this, CAMERA_SERVICE)
                == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[] {CAMERA_SERVICE}, MY_CAMERA_REQUEST_CODE);
            String a = "@23232";
        }
    }

    private void getResults() {
        InputImage inputImage = InputImage.fromBitmap(imageBitmap, 0);
        ImageLabeler imageLabeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);

        imageLabeler.process(inputImage).addOnSuccessListener(firebaseVisionImageLabels -> {
            String searchQuery = firebaseVisionImageLabels.get(0).getText();
            getSearchResults(searchQuery);
        }).addOnFailureListener(e -> Toast.makeText(this, "Fail to detect image!", Toast.LENGTH_SHORT).show());

    }

    private void getSearchResults(String searchQuery) {
        String apiKey = "c2731a96481d310cb8c954c6a2686fdeaff28143a78448405d4ec364b2d5f02d";
        String url = "https://serpapi.com/search.json?engine=google&q="+searchQuery+"&location=Seattle-Tacoma,+WA,+Washington,+United+States&hl=en&gl=us&google_domain=google.com&api_key="+apiKey;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, (Response.Listener<JSONObject>)
            response -> {
                pbloading.setVisibility(View.GONE);
                try {
                    JSONArray organicResultArray = response.getJSONArray("organic_results");
                    for (int i = 0; i < organicResultArray.length(); i++) {
                        JSONObject organicObj = organicResultArray.getJSONObject(i);
                        if (organicObj.has("title")) {
                            title = organicObj.getString("title");

                        } else if (organicObj.has("link")) {
                            link = organicObj.getString("link");

                        } else if (organicObj.has("displayed_link")) {
                            displayedLink = organicObj.getString("displayed_link");

                        } else if (organicObj.has("snippet")) {
                            snippet = organicObj.getString("snippet");
                        }
                        searchRecyclerViewModalArrayList.add(new SearchRecyclerViewModal(title, link, displayedLink, snippet));
                    }
                    searchRecyclerViewAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            },
            error -> Toast.makeText(MainActivity.this, "No result found to your search", Toast.LENGTH_SHORT).show());

        queue.add(jsonObjectRequest);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            captureIV.setImageBitmap(imageBitmap);
        }
    }

    private void takePictureIntent() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (i.resolveActivity(getPackageManager()) != null){
            startActivityForResult(i, REQUEST_CODE);
        }
    }
}