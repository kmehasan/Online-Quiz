package com.ayon.testnow;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by mugdha on 9/25/18.
 */
public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.MyViewHolder> {

    private List<Student> studentList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name,reg,mark;

        public MyViewHolder(View view) {
            super(view);

            name = view.findViewById(R.id.name);
            reg = view.findViewById(R.id.reg);
            mark = view.findViewById(R.id.mark);

        }
    }


    public StudentAdapter(List<Student> studentList) {
        this.studentList = studentList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Student student = studentList.get(position);
        holder.name.setText(student.name);
        holder.reg.setText(student.reg);
        holder.mark.setText(student.mark);
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }
}
