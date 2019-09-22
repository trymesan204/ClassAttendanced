package com.example.shawn.classattendance;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class RemoveList extends
        RecyclerView.Adapter<RemoveList.ViewHolder> {


    SQLiteDatabase db;
    private Cursor cursor;


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
            final String studentName;
            switch (v.getId()) {
                case R.id.class_name:
                    /*className = (String) nameTextView.getText();
                    Intent intent = new Intent(context, ClassDetails.class);
                    intent.putExtra("TABLE_NAME",className);
                    context.startActivity(intent);*/
                    break;
                case R.id.delete_button:
                    SQLiteOpenHelper helper = new Database(context);
                    db=helper.getReadableDatabase();

                    position = getAdapterPosition();
                    studentName= mStudents.get(position).getName();
                    //Log.d("nameClicked",studentName);

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Delete Class"+studentName+"?");

                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.delete(className," NAME=?",new String[] {studentName});
                            dialog.dismiss();
                            Intent intent1 = new Intent(context,ClassDetails.class);
                            intent1.putExtra("TABLE_NAME",className);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent1);

                            Toast.makeText(context,studentName+" DELETED",Toast.LENGTH_SHORT).show();
                        }
                    });

                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.create().show();
                    db.delete(className," NAME=?",new String[] {studentName});
                    break;
            }
        }
    }

    public int position;

    public int getAdapterPosition() {
        return position;
    }

    //Pass in the contact array into the constructor
    private ArrayList<Student> mStudents;
    public Context context;
    private String className;

    public RemoveList(Context context, ArrayList<Student> studentList, String className) {
        this.mStudents = studentList;
        this.context = context;
        this.className= className;
    }


    @Override
    public RemoveList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(RemoveList.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        String roll = mStudents.get(position).getRoll();
        String name = mStudents.get(position).getName();

        TextView textView = viewHolder.nameTextView;
        textView.setText(roll+ ". "+name);

        ImageView imageView = viewHolder.imageView;

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mStudents.size();
    }

}