package com.ayon.testnow;

import android.graphics.Movie;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mugdha on 9/25/18.
 */
public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private List<Qus> qusList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView qus_text;
        AppCompatRadioButton ans1,ans2,ans3,ans4;
        RadioGroup ans;
        View view;

        public MyViewHolder(View view) {
            super(view);
            this.view = view;
            qus_text = view.findViewById(R.id.qus_text);
            ans1 = view.findViewById(R.id.ans1);
            ans2 = view.findViewById(R.id.ans2);
            ans3 = view.findViewById(R.id.ans3);
            ans4 = view.findViewById(R.id.ans4);

            ans = view.findViewById(R.id.ans);
        }
    }


    public Adapter(List<Qus> qusList) {
        this.qusList = qusList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.qus_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Qus qus = qusList.get(position);
        holder.qus_text.setText(qus.getQus());
        holder.ans1.setText(qus.getAns1());
        holder.ans2.setText(qus.getAns2());
        holder.ans3.setText(qus.getAns3());
        holder.ans4.setText(qus.getAns4());


        holder.ans.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                qus.submit(holder.view.findViewById(checkedId).getTag().toString());
            }
        });

    }

    @Override
    public int getItemCount() {
        return qusList.size();
    }
}
