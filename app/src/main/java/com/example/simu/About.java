package com.example.simu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

public class About extends AppCompatActivity {
    TextView textViewWebsite;
    TextView textViewYouTube;
    TextView textViewFacebook;
    WebView mvideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        textViewWebsite = findViewById(R.id.web_link);
        textViewYouTube = findViewById(R.id.youtube_link);
        textViewFacebook = findViewById(R.id.facebook_link);
        mvideo = findViewById(R.id.web_view);


        String sassJsScript = "<script src=\"https://cdn.jsdelivr.net/npm/sass.js/dist/sass.js\"></script>";


        String sassCode = "#container { background-color: black; margin: 10px}";


        String loadSassScript = "window.onload = function() { " +
                "var sass = new Sass(); sass.compile('" + sassCode + "', function(result) {" +
                "document.getElementById('styled_content').innerHTML = '<style>' + result.text + '</style>';});}";


        String youtubeVideoCode = "<iframe width=\"100%\" height=\"100%\"" +
                "src=\"https://www.youtube.com/embed/E5qHYKi7zX8?si=8mIpIx1HJcr0qJK_\" " +
                "title=\"YouTube video player\" frameborder=\"0\" " +
                "allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; " +
                "picture-in-picture; web-share\" allowfullscreen></iframe>";

        String combinedScript = sassJsScript + "<script>" + loadSassScript + "</script>"; // Closing bracket added


        String finalHtml = "<div id=\"container\">" +
                "<div id=\"styled_content\"></div>" +
                "<div id=\"youtube_video\">" + youtubeVideoCode + "</div>" +
                "<link rel=\"stylesheet\" href=\"#styled_content\" />" +
                "</div>";

        mvideo.loadDataWithBaseURL("file:///android_asset/", combinedScript + finalHtml, "text/html", "UTF-8", null);

        mvideo.getSettings().setJavaScriptEnabled(true);
        mvideo.setWebChromeClient(new WebChromeClient());



        textViewWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink("http://www.google.com");
            }
        });

        textViewYouTube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink("https://www.youtube.com/watch?v=ZYvbmVEwX14&list=PLgH5QX0i9K3p9xzYLFGdfYliIRBLVDRV5");
            }
        });

        textViewFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLink("https://www.facebook.com");
            }
        });
    }

    private void openLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}