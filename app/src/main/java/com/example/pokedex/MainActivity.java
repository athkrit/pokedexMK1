package com.example.pokedex;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    ProgressDialog pd;
    JSONObject json = new JSONObject();
    JSONArray pokemon = new JSONArray();
    JSONObject pokemonJsonName = new JSONObject();

    ArrayList<String> pokemonName = new ArrayList<String>();

    SQLiteDatabase db;
//    public static String resultMain;


    Integer count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DB myDb = new DB(this);
        db = myDb.getWritableDatabase();

        myDb.InsertData("PKM-dummy");

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        new CALLDATA().execute("https://pokeapi.co/api/v2/pokemon?offset=0&limit=20");

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                Log.e("DY",""+dy);
//                if(dy>0){
//                    if(count<=960) {
//                        final GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 3);
//                        recyclerView.setHasFixedSize(true);
//                        int visibleItemCount = layoutManager.getChildCount();
//                        int totalItemCount = layoutManager.getItemCount();
//                        int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
//
//                        if (true) {
//                            Log.e("TAG", ""+visibleItemCount+" + "+pastVisibleItems+" + "+totalItemCount);
//
//                            if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
//                                Log.e("DY","load");
//                            }
//                        }
//                        count += 20;
//                        String urlnextpage = "https://pokeapi.co/api/v2/pokemon?offset=" + count.toString() + "&limit=20";
//                        new CALLDATA().execute(urlnextpage);
//                    }
//                    else{
//                        Toast.makeText(MainActivity.this, "No more pokemon", Toast.LENGTH_LONG).show();
//                    }
//                }
//            }
//        });

        final Button buttonLoadmore = findViewById(R.id.buttonLoadmore);
        buttonLoadmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(count<=960) {
                    count += 20;
                    String urlnextpage = "https://pokeapi.co/api/v2/pokemon?offset=" + count.toString() + "&limit=20";
                    new CALLDATA().execute(urlnextpage);
                    final GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 1);

                    if(count==960){
                        buttonLoadmore.setVisibility(View.INVISIBLE);
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "No more pokemon", Toast.LENGTH_LONG).show();
                    buttonLoadmore.setVisibility(View.INVISIBLE);

                }
            }
        });
    }


    public  class CALLDATA extends AsyncTask<String, String, String> {

        private Object ViewTreeObserver;

        public String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        public void onPostExecute(String result) {
            String nextpokemonpage = null;
            try {
                json = new JSONObject(result.toString());
                nextpokemonpage = json.getString("next");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                json = new JSONObject(result);
                pokemon = json.getJSONArray("results");
                for (int i = 0; i < pokemon.length(); i++) {
                    pokemonJsonName = pokemon.getJSONObject(i);
                    pokemonName.add(pokemonJsonName.getString("name"));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            Set<String> PokeN = new HashSet<String>();
//            String[] PokeN =new String[pokemonName.size()];

            for (int i = 0; i < pokemonName.size(); i++) {
                PokeN.add(pokemonName.get(i));
            }
//            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycleView);
            ArrayList<String> pokedel = new ArrayList<String>();
            final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycleView);
            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            RecyclerV adapter = new RecyclerV(MainActivity.this, pokemonName);
            recyclerView.setAdapter(adapter);
            final GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 1);

//            recyclerView.setHasFixedSize(true);
//            int visibleItemCount = layoutManager.getChildCount();
//            int totalItemCount =   recyclerView.getAdapter().getItemCount();
//            int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
//
//                Log.e("TAG", ""+visibleItemCount+" + "+pastVisibleItems+" + "+totalItemCount);
//
//                if ((visibleItemCount + pastVisibleItems) >= totalItemCount)
//                    Log.e("DY","load");

        }
    }

}