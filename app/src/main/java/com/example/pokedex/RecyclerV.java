package com.example.pokedex;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class RecyclerV extends RecyclerView.Adapter<RecyclerV.EXViewHolder> {
    ArrayList<String> pokemonName = new ArrayList<String>();
    Context context;
    View view;
    //    final DB myDb = new DB(context);
    SQLiteDatabase db;

    public class EXViewHolder extends RecyclerView.ViewHolder {

        TextView Name;

        EXViewHolder(View itemView) {
            super(itemView);
            Name = view.findViewById(R.id.button);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Detail.class);
                    intent.putExtra("name", pokemonName.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE: {
                                    DB myDb = new DB(context);
                                    db = myDb.getWritableDatabase();
                                    long flg1 = myDb.InsertData(pokemonName.get(getAdapterPosition()));
                                    if (flg1 > 0) {
                                        Toast.makeText(context, "Delete " + pokemonName.get(getAdapterPosition()) + " Successfully", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
                                    }
                                    pokemonName.remove(getAdapterPosition());
                                    notifyItemRemoved(getAdapterPosition());
                                    notifyItemRangeChanged(getAdapterPosition(), pokemonName.size());
                                }
                                case DialogInterface.BUTTON_NEGATIVE: {
                                    break;
                                }
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Delete this pokemon from this device ?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                    return false;
                }
            });
        }
    }

    public RecyclerV(Context context, ArrayList<String> pokemonName) {

        ArrayList<String> pokedel = new ArrayList<String>();

        DB myDb = new DB(context);
        pokedel = myDb.SelectData();
        for (int i = 0; i < pokedel.size(); i++) {
            for (int j = 0; j < pokemonName.size(); j++) {
                if (pokedel.get(i).matches(pokemonName.get(j))) {
                    pokemonName.remove(j);
                    break;
                }
            }
        }
        this.pokemonName = pokemonName;
        this.context = context;
    }

    @NonNull
    @Override
    public EXViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        view = mInflater.inflate(R.layout.pokename, viewGroup, false);
        return new EXViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull EXViewHolder viewHolder, int i) {
        viewHolder.Name.setText(pokemonName.get(i));
    }

    @Override
    public int getItemCount() {
        return pokemonName.size();
    }
}
