package jp.android.SOS;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class SOSMain extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
	private CheckBox cbMail;
	private CheckBox cbTwitter;
	private EditText etAddr;
	private EditText etMsg;
	private CheckBox cbSound;
	
	private static final int MENU_ID_MENU1 = (Menu.FIRST + 1);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        SharedPreferences sp;
        sp = getPreferences(MODE_PRIVATE);
        boolean isMail = sp.getBoolean("MAIL", true);
        boolean isTwitter = sp.getBoolean("TWITTER", true);
        String addr = sp.getString("ADDR", "");
        String message = sp.getString("MESSAGE", "LOCATION\nの近くにいます。助けを呼んでください！");
        boolean isSound = sp.getBoolean("SOUND", true);
        
        cbMail = (CheckBox)findViewById(R.id.CheckBox01);
        cbMail.setText("メール");
        cbMail.setChecked(isMail);

        cbTwitter = (CheckBox)findViewById(R.id.CheckBox02);
        cbTwitter.setText("Twitter");
        cbTwitter.setChecked(isTwitter);

        etAddr = (EditText)findViewById(R.id.EditText01);
        etAddr.setText(addr);
        
        etMsg = (EditText)findViewById(R.id.EditText02);
        etMsg.setText(message);
        
        Button btn = (Button)findViewById(R.id.Button01);
        btn.setText("テスト送信");
        btn.setOnClickListener(this);

        cbSound = (CheckBox)findViewById(R.id.CheckBox03);
        cbSound.setChecked(isSound);
    }
    
    @Override
    public void onStop() {
    	super.onStop();
    	
        SharedPreferences sp;
        sp = getPreferences(MODE_PRIVATE);
        Editor editor = sp.edit();
        
        editor.putBoolean("MAIL", cbMail.isChecked());
        editor.putBoolean("TWITTER", cbTwitter.isChecked());
        editor.putString("ADDR", etAddr.getText().toString());
        editor.putString("MESSAGE", etMsg.getText().toString());
        editor.putBoolean("SOUND", cbSound.isChecked());
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_ID_MENU1, Menu.NONE, "Help").setIcon(android.R.drawable.ic_menu_help);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean ret = true;
        switch (item.getItemId()) {
        default:
            ret = super.onOptionsItemSelected(item);
            break;
        case MENU_ID_MENU1:
        	Intent intent = new Intent();
        	intent.setAction(Intent.ACTION_VIEW);
        	intent.setData(Uri.parse("http://d.hatena.ne.jp/out-of-kaya/20110319/1300528221"));
        	startActivity(intent);
            ret = true;
            break;
        }
        return ret;
    }

	@Override
	public void onClick(View v) {
		if(cbMail.isChecked()) {
			sendMailTest();
		}
		if(cbTwitter.isChecked()) {
			sendTwitterTest();
		}
	}
	
	private void sendMailTest() {
		String to = etAddr.getText().toString();
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_SENDTO);
		intent.setData(Uri.parse("mailto:" + to));
		intent.putExtra(Intent.EXTRA_SUBJECT, "SOS");
		intent.putExtra(Intent.EXTRA_TEXT, "これはアドレスが正しいかどうかのテスト送信メールです。");
		startActivity(intent);
	}
	
	private void sendTwitterTest() {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, "これはTwitterへのツイートテストです。");
        startActivity(intent);
	}
}