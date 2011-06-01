package jp.android.SOS;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.RemoteViews;
import android.widget.Toast;

public class SOSService extends Service implements LocationListener {
	public static final String ACTION_BTNCLICK = "AppWidgetService.ACTION_BTNCLICK";
	private LocationManager locationManager;
	boolean isMail;
	boolean isTwitter;
	String to;
	String message;
	boolean isSound;
	
	@Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        
        getPreferenceData();        

        //リモートビューの取得
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        RemoteViews view = new RemoteViews(getPackageName(), R.layout.widget_layout);
        
        if (ACTION_BTNCLICK.equals(intent.getAction())) {
            btnClicked(view);
        }
        else {
        	//button1とボタンクリックイベントの関連付け
        	Intent newintent = new Intent();
        	newintent.setAction(ACTION_BTNCLICK);
        	PendingIntent pending = PendingIntent.getService(this, 0, newintent, 0);
        	view.setOnClickPendingIntent(R.id.Widget_layout, pending);
        }
        //ホームウィジェットの更新
        ComponentName widget = new ComponentName(this, SOSWidget.class);
        manager.updateAppWidget(widget, view);        
    }
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		if(locationManager != null) {
			locationManager.removeUpdates(this);
		}
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
    //ボタンクリック時に呼ばれる
    public void btnClicked(RemoteViews view){
		// ロケーションマネージャのインスタンスを取得する
		locationManager = (LocationManager)getSystemService(SOSService.LOCATION_SERVICE);
		if(isSound) beepSOS();

		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			// 位置情報の更新を受け取るように設定
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, // プロバイダ
					2000, // 通知のための最小時間間隔
					0, // 通知のための最小距離間隔
					this); // 位置情報リスナー
		}
		else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			// 位置情報の更新を受け取るように設定
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // プロバイダ
					2000, // 通知のための最小時間間隔
					0, // 通知のための最小距離間隔
					this); // 位置情報リスナー			
		}
		else {
			Toast.makeText(this, "位置情報が取得できませんでした。設定を見直してください", Toast.LENGTH_LONG).show();
		}
    }
    
    private void beepSOS() {
    	AudioManager audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
    	audio.setStreamVolume(AudioManager.STREAM_MUSIC, audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
    	MediaPlayer _mp;
    	try {
    		_mp = MediaPlayer.create(this, R.raw.sos);
    		//_mp.prepare(); // 準備
    		_mp.start(); // 再生
    	}
    	catch (Exception e) {
    	}
    }
    
	@Override
	public void onLocationChanged(Location location) {
		if(location == null) {
			Toast.makeText(this, "位置情報が取得できませんでした。設定を見直してください", Toast.LENGTH_LONG).show();
			return;
		}
		String url = "http://maps.google.co.jp/?ie=UTF8&q=" + Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude());

		if(isMail) {
			sendSOSMail(to, message.replaceAll("LOCATION", url));
		}
		if(isTwitter) {
			sendSOSTwitter(message.replaceAll("LOCATION", url) + "#SOS");
		}

		locationManager.removeUpdates(this);
	}
	
	private void getPreferenceData() {
		SharedPreferences sp = getSharedPreferences("SOSMain", MODE_PRIVATE);
		isMail = sp.getBoolean("MAIL", true);
		isTwitter = sp.getBoolean("TWITTER", true);
		to = sp.getString("ADDR", "");
        message = sp.getString("MESSAGE", "LOCATION\nの周辺にいます。助けを呼んでください！");
        isSound = sp.getBoolean("SOUND", true);
	}
	
	private void sendSOSMail(String to, String data) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_SENDTO);
		intent.setData(Uri.parse("mailto:" + to));
		intent.putExtra(Intent.EXTRA_SUBJECT, "SOS");
		intent.putExtra(Intent.EXTRA_TEXT, data);
		startActivity(intent);
	}

	private void sendSOSTwitter(String data) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, data);
        startActivity(intent);
	}
	
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
}
