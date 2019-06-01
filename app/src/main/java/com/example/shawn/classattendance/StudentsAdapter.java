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

public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public Context context;
        public CheckBox checkBox;

        public ViewHolder(Context context,View itemView) {
            super(itemView);
            this.textView = (TextView)itemView.findViewById(R.id.text_view);
            this.checkBox = (CheckBox)itemView.findViewById(R.id.check_box);
            this.context = context;

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    studentList.get(getAdapterPosition()).setPresent(isChecked);
                    Log.i("myLog",getAdapterPosition()+" "+isChecked);
                }
            });

        }

    }

    ArrayList<Student> studentList;
    public StudentsAdapter(ArrayList<Student> studentName) {
        this.studentList = studentName;
    }


    @NonNull
    @Override
    public StudentsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View studentView = layoutInflater.inflate(R.layout.student_attendance,viewGroup,false);

        ViewHolder viewHolder = new ViewHolder(context, studentView);
        return viewHolder;
    }

    public ArrayList<Student> getStudentList(){
        return studentList;
    }

    @Override
    public void onBindViewHolder(@NonNull StudentsAdapter.ViewHolder viewHolder, int i) {
        String roll = studentList.get(i).getRoll();
        String name = studentList.get(i).getName();
//        Student student;
//        student.setPresent(true);
        TextView textView = viewHolder.textView;
        textView.setText(roll+ ") "+name);
        viewHolder.checkBox.setChecked(studentList.get(i).isPresent());
//        viewHolder.checkBox.setChecked(false);
        //CheckBox checkBox = viewHolder.checkBox;
        //checkBox.isEnabled();
//        if(checkBox.isChecked())
//            //status.add("yes");
//            studentList.get(i).setPresent(true);
//        else
//            studentList.get(i).setPresent(false);
//            //status.add("no");

    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }


}
