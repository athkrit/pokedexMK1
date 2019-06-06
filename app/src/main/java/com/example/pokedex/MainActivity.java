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
import android.widget.TextView;

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
    String nextpokemonpage = null;
    JSONObject json = new JSONObject();
    JSONArray pokemon = new JSONArray();
    JSONObject pokemonJsonName = new JSONObject();

    ArrayList<String> pokemonName = new ArrayList<String>();

    SQLiteDatabase db;
    public static String resultMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DB myDb = new DB(this);
        db = myDb.getWritableDatabase();

        myDb.InsertData("PKM-dummy");

        new CALLDATA().execute("https://pokeapi.co/api/v2/pokemon?offset=20&limit=20");

//        SharedPreferences editor = getPreferences( MODE_PRIVATE);
//        Set<String> fetch = editor.getStringSet("pokeName", null);
//        String[] PokemonFetch = fetch.toArray(new String[fetch.size()]);
//        TextView textView = findViewById(R.id.textView);
//        textView.setText(fetch.toArray().toString());
//
//        for(int i = 0 ;i<=fetch.size();i++){
//            pokemonName.add(PokemonFetch[i]);
//        }


    }

    public class CALLDATA extends AsyncTask<String, String, String> {

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
            resultMain = result;
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
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycleView);
            recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            ArrayList<String> pokedel = new ArrayList<String>();

            final GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this, 1);

            RecyclerV adapter = new RecyclerV(MainActivity.this, pokemonName);
            recyclerView.setAdapter(adapter);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if (dy > 0) {
                        int visibleItemCount = layoutManager.getChildCount();
                        int totalItemCount = layoutManager.getItemCount();
                        int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                        if (2>1) {
                            if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                                new CALLDATA().execute(nextpokemonpage);
                            }
                        }
                    }
                }
            });

            SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
            editor.putStringSet("pokeName", PokeN);
            editor.apply();
        }
    }
}



