package com.example.danie.ipconfig;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private TextView tvIp;
    private TextView tvConexion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.tvConexion = findViewById(R.id.tvConexion);
        this.tvIp = findViewById(R.id.tvIfconfig);
        this.button = findViewById(R.id.button);
    }

    public void obtener(View v) {
        Thread f = new Thread(new Runnable() {
            @Override
            public void run() {
                Runtime r = Runtime.getRuntime();
                Process p;
                try {
                    p = r.exec("ifconfig");

                    InputStream is = p.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String linea;
                    String anterior="";
                    String salida="";
                    while ((linea = br.readLine()) != null) {
                        System.out.println(linea);
                        if (linea.contains("inet addr:")){
                            linea=linea.substring(linea.indexOf("inet addr:")+10,linea.length());
                            linea=linea.substring(0,linea.indexOf(" "));
                            anterior=anterior.substring(0,anterior.indexOf("Link"));
                            anterior=anterior.trim();
                            salida+=anterior+":"+linea+"\n";
                        }
                        anterior=linea;
                    }
                    tvSetText(tvIp, salida);


                    p = r.exec("ping -c 1 8.8.8.8");

                    is = p.getInputStream();
                    isr = new InputStreamReader(is);
                    br = new BufferedReader(isr);
                    boolean conectado = false;
                    while ((linea = br.readLine()) != null) {
                        if (linea.contains("packets transmitted") && !linea.contains(", 0 received")) {
                            conectado = true;
                            break;
                        }
                    }
                    if(conectado)
                        tvSetText(tvConexion, "Tiene conexion");
                    else
                        tvSetText(tvConexion, "No tiene conexion");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        f.start();
    }

    private void tvSetText(final TextView tv, final String datos) {
        tv.post(new Runnable() {
            @Override
            public void run() {
                tv.setText(datos);
            }
        });
    }
}
