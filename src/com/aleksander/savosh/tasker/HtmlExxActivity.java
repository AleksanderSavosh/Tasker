package com.aleksander.savosh.tasker;


import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.widget.LinearLayout.LayoutParams;

public class HtmlExxActivity extends Activity {

    private View createLayout(){
        LinearLayout linLayout = new LinearLayout(this);
        linLayout.setOrientation(LinearLayout.VERTICAL);
        LayoutParams linLayoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        linLayout.setLayoutParams(linLayoutParam);

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

//        <a> (supports attribute "href")
//        <b>
//        <big>
//        <blockquote>
//        <br>
//        <cite>
//        <dfn>
//        <div>
//        <em>
//        <font> (supports attributes "color" and "face")
//        <i>
//        <img> (supports attribute "src". Note: you have to include an ImageGetter to handle retrieving a Drawable for this tag)
//        <p>
//        <small>
//        <strong>
//        <sub>
//        <sup>
//        <tt>
//        <u>
        String htmlTextExx = "<b>" +
                "<big><font color=\"red\">H</font></big>" +
                "<small>" +
                    "<font color=\"yellow\">e</font>" +
                    "<font color=\"blue\">l</font>" +
                "</small>" +
                "<big><font color=\"green\">l</font></big>" +
                "<small><font color=\"yellow\">o</font></small> " +
                "<i>world</i> !!!</b>";


        //можно фигачить все что угодно и даже стили атрибутом style
        final WebView webView = new WebView(this);
        webView.setLayoutParams(params);
        webView.loadData(htmlTextExx, "text/html", null);

        final TextView textView = new TextView(this);
        textView.setLayoutParams(params);
        textView.setText(Html.fromHtml(htmlTextExx));

        final EditText editText = new EditText(this);
        editText.setLayoutParams(params);
        Button button = new Button(this);
        button.setText("SET HTML STRING");
        button.setLayoutParams(params);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String htmlText = editText.getText().toString();
                textView.setText(Html.fromHtml(htmlText));
                webView.loadData(htmlText, "text/html", null);
            }
        });

        linLayout.addView(webView, 0);
        linLayout.addView(textView, 1);
        linLayout.addView(editText, 2);
        linLayout.addView(button, 3);

        return linLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(createLayout());
    }

}
