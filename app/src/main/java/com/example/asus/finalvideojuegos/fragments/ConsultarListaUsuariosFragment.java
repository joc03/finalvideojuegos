package com.example.asus.finalvideojuegos.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.asus.finalvideojuegos.R;
import com.example.asus.finalvideojuegos.adapter.UsuarioAdapter;
import com.example.asus.finalvideojuegos.entidades.Producto;
import com.example.asus.finalvideojuegos.entidades.Usuario;
import com.example.asus.finalvideojuegos.entidades.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ConsultarListaUsuariosFragment extends Fragment implements Response.ErrorListener, Response.Listener<JSONObject> {

    RecyclerView recyclerUsuario;
    ArrayList<Usuario> listaUsuario;
    ImageView imagensinconexion;

    ProgressDialog dialog;

    //RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista=inflater.inflate(R.layout.fragment_consultar_lista_usuarios, container, false);
        listaUsuario= new ArrayList<>();
        recyclerUsuario = (RecyclerView) vista.findViewById(R.id.idRecyclerImagenUsuarios);
        recyclerUsuario.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerUsuario.setHasFixedSize(true);

       /* //PONER INVISIBLE LA PARTE DE SIN INTERNET
        imagensinconexion=(ImageView) vista.findViewById(R.id.imagensinconexion);
        imagensinconexion.setVisibility(View.INVISIBLE);*/

        // request= Volley.newRequestQueue(getContext());
        //VARIABLES ENCESARIAS PARA SABER LA COENXION A INTERNET
        ConnectivityManager con= (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=con.getActiveNetworkInfo();

       /* //VALIDANDO SI TIENE O NO INTERNET
        if(networkInfo!=null && networkInfo.isConnected()){
            imagensinconexion.setVisibility(View.INVISIBLE);
            cargarWebService();
        }else{
            //MENSAJE
            imagensinconexion.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "No hay conexion a INTERNET ", Toast.LENGTH_LONG).show();
        }
*/
        cargarWebService();



        return vista;
    }

    private void cargarWebService() {

        dialog=new ProgressDialog(getContext());
        dialog.setMessage("Consultando...");
        dialog.show();


        String ip=getString(R.string.ip);

       String url=ip+"/BD_APP/wsJSONConsultarListaImagenesUsuarios.php";

        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET,url,null,this,this);
        // request.add(jsonObjectRequest);
        VolleySingleton.getIntanciaVolley(getContext()).addToRequestQueue(jsonObjectRequest);
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), "No se puede conectar "+error.toString(), Toast.LENGTH_LONG).show();
        System.out.println();
        Log.d("ERROR: ", error.toString());
        dialog.hide();
    }

    @Override
    public void onResponse(JSONObject response) {

        Usuario usuario=null;

        JSONArray json=response.optJSONArray("usuarios");

        try {

            for (int i=0;i<json.length();i++){
                usuario=new Usuario();
                JSONObject jsonObject=null;
                jsonObject=json.getJSONObject(i);

                usuario.setNick(jsonObject.optString("nick"));
                usuario.setNombre(jsonObject.optString("nombre"));
                usuario.setApellidos(jsonObject.optString("apellidos"));
                usuario.setCelular(jsonObject.optInt("celular"));
                usuario.setCargo(jsonObject.optString("descripcion"));
                listaUsuario.add(usuario);
            }
            dialog.hide();
            UsuarioAdapter adapter=new UsuarioAdapter(listaUsuario,getContext());
            recyclerUsuario.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "No se ha podido establecer conexión con el servidor" +
                    " "+response, Toast.LENGTH_LONG).show();
            dialog.hide();
        }


    }
}
