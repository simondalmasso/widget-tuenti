package ar.simon.tuentiwidget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class LoginActivity extends Activity {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(32,32,32,32);
        TextView info=new TextView(this);
        info.setText("Tuenti Odometro no necesita otra cuenta ni contraseña. Habilita el lector de Accesibilidad y abre la app oficial de Tuenti en Consumos.");
        info.setTextSize(18); root.addView(info);
        Button settings=new Button(this); settings.setText("Abrir Accesibilidad");
        settings.setOnClickListener(v->startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))); root.addView(settings);
        Button tuenti=new Button(this); tuenti.setText("Abrir Tuenti");
        tuenti.setOnClickListener(v->{Intent i=getPackageManager().getLaunchIntentForPackage("com.tuentiargentina"); if(i!=null) startActivity(i);}); root.addView(tuenti);
        setContentView(root);
    }
}
