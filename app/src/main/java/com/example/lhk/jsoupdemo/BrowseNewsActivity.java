package com.example.lhk.jsoupdemo;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class BrowseNewsActivity extends Activity {

	private WebView webView;
	private ProgressBar bar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle("��������");  
		setContentView(R.layout.activity_browse_news);
		ActionBar actionBar = getActionBar();  
		actionBar.setDisplayHomeAsUpEnabled(true);
		webView = (WebView) findViewById(R.id.webView);
		bar = (ProgressBar) findViewById(R.id.progress_bar);
		
		String content_url = getIntent().getStringExtra("content_url");
		
		 webView.setWebChromeClient(new WebChromeClient(){
			 @Override
	          public void onProgressChanged(WebView view, int newProgress) {
	              if (newProgress == 100) {
	                  bar.setVisibility(View.INVISIBLE);
	              } else {
	                  if (View.INVISIBLE == bar.getVisibility()) {
	                      bar.setVisibility(View.VISIBLE);
	                  }
	                  bar.setProgress(newProgress);
	              }
	              super.onProgressChanged(view, newProgress);
	          }
		 });
		 
		 webView.setWebViewClient(new WebViewClient() {
			 @Override  
	            public boolean shouldOverrideUrlLoading(WebView view, String url) {  
	                // ʹ���Լ���WebView�������ӦUrl�����¼���������ʹ��Ĭ�������������ҳ��  
	                webView.loadUrl(url);  
	                // ���ĵ�����¼���Android�з���True�ļ�����Ϊֹ��,�¼��ͻ᲻��ð�ݴ����ˣ����ǳ�֮Ϊ���ĵ�  
	                return true;  
	            }  
			 });
		webView.loadUrl(content_url);
//		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		
	}
	
	@Override  
	public boolean onOptionsItemSelected(MenuItem item) {  
	    switch (item.getItemId()) {  
	    case android.R.id.home:  
	        finish();  
	    }  
	    return true;
	}
}
