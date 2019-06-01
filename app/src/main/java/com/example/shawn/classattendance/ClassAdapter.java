package com.example.shawn.classattendance;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ClassAdapter extends
        RecyclerView.Adapter<ClassAdapter.ViewHolder> {


    boolean delete = false;


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public ImageView imageView;
        public Context context;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(Context context, final View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            this.nameTextView = (TextView) itemView.findViewById(R.id.class_name);
            this.imageView = (ImageView) itemView.findViewById(R.id.delete_button);
            this.context = context;

            nameTextView.setOnClickListener(this);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //Log.d("clicked",String.valueOf(v.getId()));
            //Log.d("position", String.valueOf(getAdapterPosition()));
            switch (v.getId()) {
                case R.id.class_name:
                    String className = (String) nameTextView.getText();
                    Intent intent = new Intent(context, ClassDetails.class);
                    intent.putExtra("TABLE_NAME",className);
                    context.startActivity(intent);
                    break;
                case R.id.delete_button:
                    position = getAdapterPosition();
                    Log.d("position",String.valueOf(getAdapterPosition()));
                    delete = true;
                    Log.d("deleted",String.valueOf(delete));
                    break;
            }
        }
    }

    public int position;

    public boolean getDeleteStatus() {
        return delete;
    }

    public int getAdapterPosition() {
        return position;
    }


    //Pass in the contact array into the constructor
    private ArrayList<String> mClasses;
    public Context context;

    public ClassAdapter(Context context,ArrayList<String> contacts) {
        mClasses = contacts;
        this.context = context;
    }


    @Override
    public ClassAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View classView = inflater.inflate(R.layout.class_list, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(context,classView);
        return viewHolder;

    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ClassAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        String contact = mClasses.get(position);

        // Set item views based on your views and data model
        TextView textView = viewHolder.nameTextView;
        textView.setText(contact);

        ImageView imageView = viewHolder.imageView;

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mClasses.size();
    }

}