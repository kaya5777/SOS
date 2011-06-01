package jp.android.SOS;

import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;

public class SOSWidget extends AppWidgetProvider {
    //更新時に呼ばれる
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    	super.onUpdate(context, appWidgetManager, appWidgetIds);
    	
        //ホームウィジェットを処理するサービスの実行
        Intent intent = new Intent(context, SOSService.class);        
        context.startService(intent);
    }
}
