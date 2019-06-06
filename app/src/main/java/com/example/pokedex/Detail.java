package com.example.pokedex;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
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

public class Detail extends AppCompatActivity {
    ProgressDialog pd;
    JSONObject json = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        TextView textView2 = findViewById(R.id.textView2);



        String name;

        Bundle extras = getIntent().getExtras();
        name = extras.getString("name");

        name = "/"+name;

        String URL = "https://pokeapi.co/api/v2/pokemon" + name;
        new Detail.JsonTask().execute(URL);
        textView2.setText(name.substring(1));

    }

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(Detail.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {


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
        protected void onPostExecute(String result) {
            JSONArray stats = new JSONArray();
            JSONObject stat = new JSONObject();
            JSONObject style = new JSONObject();

            JSONArray types = new JSONArray();
            JSONObject typein = new JSONObject();
            JSONObject slot = new JSONObject();

            JSONObject sprites = new JSONObject();


            ArrayList<String> allstat = new ArrayList<String>();
            allstat.add("type");
            allstat.add("attack");
            allstat.add("defense");
            allstat.add("speed");
            allstat.add("special-attack");
            allstat.add("special-defense");

            final int[] pos = {R.id.textView3, R.id.textView4, R.id.textView5, R.id.textView6, R.id.textView7, R.id.textView8};

            try {
                json = new JSONObject(result);
                stats = json.getJSONArray("stats");
                for (int i = 0; i < stats.length(); i++) {
                    String base_stat;
                    style = stats.getJSONObject(i);
                    base_stat = style.getString("base_stat");
                    stat = style.getJSONObject("stat");
                    String name = stat.getString("name");
                    for (int a = 0; a < stats.length(); a++) {
                        for (int b = 0; b < allstat.size(); b++) {
                            if (name.matches(allstat.get(b))) {
                                TextView textView = findViewById(pos[b]);
                                textView.setText(name + " = " + base_stat);
                            }
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                ArrayList<String> alltype = new ArrayList<String>();

                json = new JSONObject(result);
                types = json.getJSONArray("types");
                for (int i = 0; i < types.length(); i++) {
                    String typ;
                    slot = types.getJSONObject(i);
                    typein = slot.getJSONObject("type");

                    typ = typein.getString("name");
                    alltype.add(typ);
                    TextView textView1 = findViewById(R.id.textView3);
                    textView1.setText("type = " + alltype.toString().replace("]", "").replace("[", ""));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                json = new JSONObject(result);
                sprites = json.getJSONObject("sprites");
                String B_D,F_D,B_S,F_S;
                B_D = sprites.getString("back_default");
                F_D = sprites.getString("front_default");
                B_S = sprites.getString("back_shiny");
                F_S = sprites.getString("front_shiny");
                new DownloadImageTask((ImageView) findViewById(R.id.imageView))
                        .execute(F_D);
                new DownloadImageTask((ImageView) findViewById(R.id.imageView2))
                        .execute(B_D);
                new DownloadImageTask((ImageView) findViewById(R.id.imageView3))
                        .execute(F_S);
                new DownloadImageTask((ImageView) findViewById(R.id.imageView4))
                        .execute(B_S);


            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (pd.isShowing()) {
                pd.dismiss();
            }
        }
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
