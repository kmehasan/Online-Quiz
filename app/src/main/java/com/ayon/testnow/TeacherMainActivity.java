package com.ayon.testnow;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.obsez.android.lib.filechooser.ChooserDialog;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TeacherMainActivity extends AppCompatActivity {
    DatabaseReference user;
    private List<Student> students = new ArrayList<>();
    private RecyclerView recyclerView;
    private StudentAdapter mAdapter;

    FirebaseDatabase database;
    private boolean status = false;
    private long finish = 1;
    Button startnow;
    private FileWriter mFileWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);
        startnow = findViewById(R.id.start_now);

        database = FirebaseDatabase.getInstance();
        user = database.getReference(getResources().getString(R.string.db_key_users));
        database.getReference(getString(R.string.qus_ref) + "/" + getString(R.string.flag)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(getString(R.string.status)).exists())
                    status = (boolean) dataSnapshot.child(getString(R.string.status)).getValue();
                if(dataSnapshot.child(getString(R.string.finish)).exists())
                    finish = (long) dataSnapshot.child(getString(R.string.finish)).getValue();
                else finish = 1;
                if(status) startnow.setText("Finish");
                else startnow.setText("Start Now");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                students.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    String name = (String) snapshot.child(getString(R.string.name)).getValue();
                    String reg = (String) snapshot.child(getString(R.string.reg)).getValue();
                    String mark = "";
                    if(snapshot.child(getString(R.string.mark)).exists())
                        mark= snapshot.child(getString(R.string.mark)).getValue() +"";
                    boolean isAdmin = (boolean) snapshot.child(getString(R.string.admin)).getValue();
                    if(!isAdmin)
                        students.add(new Student(reg,name,mark));
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        students.add(new Student("2014132010", "Khan Mahmud Enamul Hasan", "10"));
//        students.add(new Student("2014132016", "Khan Mahmud", "8"));
//        students.add(new Student("2014132017", "Helal", "15"));
//        students.add(new Student("2014132023", "Ali omar", "12"));

        recyclerView = findViewById(R.id.student_recyler);
        mAdapter = new StudentAdapter(students);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

    }

    public void upload_qus(View view) {
        new ChooserDialog().with(this)
                .withFilter(false, false, "csv")
                .withStartFile(Environment.getExternalStorageDirectory().getAbsolutePath())
                .withResources(R.string.title_choose_file, R.string.title_choose, R.string.dialog_cancel)
                .withChosenListener(new ChooserDialog.Result() {
                    @Override
                    public void onChoosePath(String path, File pathFile) {
                        Toast.makeText(TeacherMainActivity.this, "FILE: " + path, Toast.LENGTH_SHORT).show();
                        try {
                            readFromCSV(pathFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .build()
                .show();
    }

    void readFromCSV(File file) throws IOException {
        CSVReader reader = new CSVReader(new FileReader(file));
        String[] nextLine;
        List<Qus> quses = new ArrayList<>();
        while ((nextLine = reader.readNext()) != null) {
            // nextLine[] is an array of values from the line
            quses.add(new Qus(nextLine[0], nextLine[1], nextLine[2],nextLine[3],nextLine[4],Integer.parseInt(nextLine[5])));
        }
        uploadToDataBase(quses);
    }
    void uploadToDataBase(List<Qus> quses)
    {
        database.getReference(getString(R.string.qus_ref)+"/"+getString(R.string.flag)+"/"+getString(R.string.status)).setValue(false);
        database.getReference(getString(R.string.qus_ref)+"/"+getString(R.string.flag)+"/"+getString(R.string.finish)).setValue(null);
        status = false;
        finish = 1;
        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    user.child(snapshot.getKey()).child(getString(R.string.mark)).setValue(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference qusReference = FirebaseDatabase.getInstance().getReference(getResources().getString(R.string.qus_ref)).child(getString(R.string.current_qus_set));
        qusReference.setValue(null);
        for(Qus q: quses)
            qusReference.push().setValue(q);



    }

    public void stat_now(View view) {
        if(!status){
            database.getReference(getString(R.string.qus_ref)+"/"+getString(R.string.flag)+"/"+getString(R.string.status)).setValue(true);
        }
        else if(finish != 2) {
            database.getReference(getString(R.string.qus_ref)+"/"+getString(R.string.flag)+"/"+getString(R.string.finish)).setValue(2);
        }
    }
    private void writeToFile(final String data) {
        new ChooserDialog().with(this)
                .withFilter(true, false)
                .withStartFile(Environment.getExternalStorageDirectory().getAbsolutePath())
                .withChosenListener(new ChooserDialog.Result() {
                    @Override
                    public void onChoosePath(String path, File pathFile) {
                        File file = new File(path, "result.csv");
                        Log.d("TAG", "writeToFile: "+file.getAbsolutePath());
                        FileOutputStream stream = null;
                        try {
                            stream = new FileOutputStream(file);
                            stream.write(data.getBytes());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                stream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .build()
                .show();

    }
    public void download(View view) {

        String s = "ID No,Name,Mark\n";
        for(Student student: students)
            s = s+student.reg+","+student.name+","+student.mark+"\n";
        writeToFile(s);
    }
}
