package com.ayon.testnow;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.ayon.testnow.Login.uid;

public class MainActivity extends AppCompatActivity {
    DatabaseReference user;
    DatabaseReference qusReference;
    boolean status = false;
    private List<Qus> quses = new ArrayList<>();
    private RecyclerView recyclerView;
    private Adapter mAdapter;
    private long finish = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        user = database.getReference(getResources().getString(R.string.db_key_users)).child(uid);
        qusReference = database.getReference(getResources().getString(R.string.qus_ref)).child(getString(R.string.current_qus_set));
//        quses.add(new Qus("Qus 1","A","B","C","D",2));
//        quses.add(new Qus("Qus 2","A","B","C","D",3));
//        quses.add(new Qus("Qus 2","A","B","C","D",3));
//        quses.add(new Qus("Qus 2","A","B","C","D",1));
//        quses.add(new Qus("Qus 2","A","B","C","D",4));
//        quses.add(new Qus("Qus 2","A","B","C","D",2));
//        for(Qus q: quses)
//            qusReference.push().setValue(q);
        quses.clear();
        final ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                quses.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    quses.add(snapshot.getValue(Qus.class));
                }
                if (status) mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        database.getReference(getString(R.string.qus_ref) + "/" + getString(R.string.flag)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(getString(R.string.status)).exists())
                {
                    status = (boolean) dataSnapshot.child(getString(R.string.status)).getValue();
                    if (status) {
                        qusReference.addValueEventListener(valueEventListener);
                    }
                    else {
                        qusReference.removeEventListener(valueEventListener);
                    }
                }
                if(dataSnapshot.child(getString(R.string.finish)).exists())
                {
                    Log.d("TAG", "onDataChange: "+dataSnapshot.child(getString(R.string.finish)).getValue());
                    finish = (long) dataSnapshot.child(getString(R.string.finish)).getValue();
                    if (finish==2) {
                        Log.d("TAG", "onDataChange: finish");
                        done(null);
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        qusReference.addValueEventListener(valueEventListener);
        user = database.getReference(getResources().getString(R.string.db_key_users)).child(uid);
        final AppCompatButton done = findViewById(R.id.done);
        final TextView score = findViewById(R.id.score);
        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String mark = "";
                if (dataSnapshot.child(getString(R.string.mark)).exists())
                {
                    mark = dataSnapshot.child(getString(R.string.mark)).getValue() + "";

                    qusReference.removeEventListener(valueEventListener);
                    quses.clear();
                    mAdapter.notifyDataSetChanged();
                    done.setVisibility(View.GONE);
                    score.setVisibility(View.VISIBLE);
                    score.setText("Your score: "+mark);

                }
                else {
                    qusReference.addValueEventListener(valueEventListener);
                    done.setVisibility(View.VISIBLE);
                    score.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        recyclerView = findViewById(R.id.recyclerview);
        mAdapter = new Adapter(quses);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

    }


    public void done(View view) {
        int mark = 0;
        for (Qus q : quses) {
            if (q.isRight())
                mark++;
            Log.d("TAG", "done: " + q.isRight + " " + q.rightAns);
        }
        if(quses.size()>0)
            user.child(getString(R.string.mark)).setValue(mark);
        quses.clear();
        mAdapter.notifyDataSetChanged();

    }
}
