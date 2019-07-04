package com.example.listview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.*;

import org.json.JSONArray;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static android.os.Build.VERSION_CODES.P;

public class MainActivity extends AppCompatActivity {

    private EditText etNombre, etPrecio, etCantidad;
    private Button btnAlmacenar;
    private ListView lvDatos;
    private AsyncHttpClient cliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNombre = (EditText) findViewById(R.id.etNombre);
        etPrecio = (EditText) findViewById(R.id.etPrecio);
        etCantidad = (EditText) findViewById(R.id.etCantidad);
        btnAlmacenar = (Button) findViewById(R.id.btnAlmacenar);
        lvDatos = (ListView) findViewById(R.id.lvDatos);
        cliente = new AsyncHttpClient();
        botonAlmacenar();
        obtenerProductos();
    }

    private void botonAlmacenar(){
        btnAlmacenar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etNombre.getText().toString().isEmpty() || etPrecio.getText().toString().isEmpty() || etCantidad.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Hay Campos Vacios!!", Toast.LENGTH_SHORT).show();
                }else{
                    Producto p = new Producto();
                    p.setNombre(etNombre.getText().toString());
                    p.setPrecio(Integer.parseInt(etPrecio.getText().toString()));
                    p.setCantidad(Integer.parseInt(etCantidad.getText().toString()));
                    p.setTotal(p.getPrecio()*p.getCantidad());
                    agregarProducto(p);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    obtenerProductos();
                }
            }
        });
    }
    private void agregarProducto(Producto p){
        String url = "http://192.168.1.15/agregar.php?";
        String parametros = "Nombre="+p.getNombre()+"&Precio="+p.getPrecio()+"&Cantidad="+p.getCantidad()+"&Total="+p.getTotal();
        cliente.post(url + parametros, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200){
                    Toast.makeText(MainActivity.this, "Producto Agregado COrrectamente!!", Toast.LENGTH_SHORT).show();
                    etNombre.setText("");
                    etCantidad.setText("");
                    etPrecio.setText("");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void obtenerProductos(){
        String url = "http://192.168.1.15/obtenerDatos.php";
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode == 200){
                    listarProductos(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void listarProductos(String respuesta){
        ArrayList <Producto> lista = new ArrayList <Producto>();
        try {
            JSONArray jsonArreglo = new JSONArray(respuesta);
            for (int i =0; i<jsonArreglo.length();i++){
                Producto p = new Producto();
                p.setId(jsonArreglo.getJSONObject(i).getInt("id"));
                p.setNombre(jsonArreglo.getJSONObject(i).getString("nom_pro"));
                p.setPrecio(jsonArreglo.getJSONObject(i).getInt("pre_pro"));
                p.setCantidad(jsonArreglo.getJSONObject(i).getInt("can_pro"));
                p.setTotal(jsonArreglo.getJSONObject(i).getInt("tot_pro"));
                lista.add(p);
            }
            ArrayAdapter <Producto> a = new ArrayAdapter(this,android.R.layout.simple_list_item_1,lista);
            lvDatos.setAdapter(a);
        }catch (Exception e1){
            e1.printStackTrace();
        }
    }
}
