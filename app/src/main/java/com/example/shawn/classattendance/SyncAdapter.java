package com.example.shawn.classattendance;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

public class SyncAdapter extends RecyclerView.Adapter<SyncAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public Context context;

        public ViewHolder(Context context,View itemView) {
            super(itemView);
            this.textView = (TextView)itemView.findViewById(R.id.text_view);
            this.context = context;

        }

    }

    ArrayList<Student> studentList;
    public SyncAdapter(ArrayList<Student> studentName) {
        this.studentList = studentName;
    }


    @NonNull
    @Override
    public SyncAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View studentView = layoutInflater.inflate(R.layout.student_sync,viewGroup,false);

        ViewHolder viewHolder = new ViewHolder(context, studentView);
        return viewHolder;
    }

    public ArrayList<Student> getStudentList(){
        return studentList;
    }

    @Override
    public void onBindViewHolder(@NonNull SyncAdapter.ViewHolder viewHolder, int i) {
        String roll = studentList.get(i).getRoll();
        String name = studentList.get(i).getName();

        TextView textView = viewHolder.textView;
        textView.setText(roll+ ". "+name);

    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }


}
