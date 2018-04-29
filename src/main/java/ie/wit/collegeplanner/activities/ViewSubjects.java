package ie.wit.collegeplanner.activities;

/**
 * Created by Dylan on 16/03/2018.
 */

/* References
https://www.androidtutorialpoint.com/storage/android-sqlite-database-tutorial/
https://www.journaldev.com/9438/android-sqlite-database-example-tutorial
https://www.androidcode.ninja/android-sqlite-tutorial/
https://www.simplifiedcoding.net/android-sqlite-database-example/
https://www.simplifiedcoding.net/sqlite-crud-example-in-android-activeandroid/
https://inducesmile.com/android/how-to-create-android-spl
http://www.javarticles.com/2015/07/android-search-widget-example.html
 */

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ie.wit.collegeplanner.R;
import ie.wit.collegeplanner.model.Subject;


public class ViewSubjects extends Base {

    SQLiteDatabase database;
    List<Subject> subjectList;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_subjects);
        database = openOrCreateDatabase(Subjects.DATABASE_NAME, MODE_PRIVATE, null);

        subjectList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listViewSubjects);
        loadSubjectsFromDatabase();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_subject_search, menu);
        MenuItem searchViewItem = menu.findItem(R.id.subjectSearch);
        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchViewAndroidActionBar.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    /*
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_subject_search, menu);
        MenuItem item = menu.findItem(R.id.subjectSearch);
        SearchView subjectSearchView = (SearchView)item.getActionView();
        subjectSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                subjectList.getFilter().filter(newText);
                return false;
            }
        });
        /*
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));*/
        //return true;
    //}

    /*private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            search(query);
        }
    }*/

   /* private void search(String query) {

        String sql="SELECT subjectName FROM subject Where subjectName like '%%'";
        Cursor cursor = database.rawQuery(sql, null);
        ArrayAdapter(new SimpleCursorAdapter(this, R.layout.list_layout_subject, cursor,
                new String[] {subjectName}, new int[]{R.id.list_item}));
    }*/
    //database.execSQL(sql, new String[]{subjectName, subjectTimetableName, subjectRoom, subjectTeacher, String.valueOf(subject.getId())});

    private void loadSubjectsFromDatabase() {
        String sql="SELECT * FROM subject";
        Cursor cursor = database.rawQuery(sql, null);

        if(cursor.moveToFirst()) {
            do {
                subjectList.add(new Subject(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)
                ));
            } while (cursor.moveToNext());

            SubjectAdapter adapter = new SubjectAdapter(this, R.layout.list_layout_subject, subjectList, database);
            listView.setAdapter(adapter);
        }
    }
}

class SubjectAdapter extends ArrayAdapter<Subject> {

    Context context;
    int listLayout;
    List<Subject> subjectList;
    SQLiteDatabase database;

    public SubjectAdapter(Context context, int listLayout, List<Subject> subjectList, SQLiteDatabase database) {
        super(context, listLayout, subjectList);

        this.context = context;
        this.listLayout = listLayout;
        this.subjectList = subjectList;
        this.database = database;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(listLayout, null);

        //getting views
        TextView subjectNameTextView = view.findViewById(R.id.subjectNameTextView);
        TextView subjectTimetableNameTextView = view.findViewById(R.id.subjectTimetableNameTextView);
        TextView subjectRoomTextView = view.findViewById(R.id.subjectRoomTextView);
        TextView subjectTeacherTextView = view.findViewById(R.id.subjectTeacherTextView);

        final Subject subject = subjectList.get(position);

        //adding data to views
        subjectNameTextView.setText(subject.getSubjectName());
        subjectTimetableNameTextView.setText(subject.getSubjectTimetableName());
        subjectRoomTextView.setText(subject.getSubjectRoom());
        subjectTeacherTextView.setText(subject.getSubjectTeacher());

        //use these buttons for update and delete operation
        Button buttonDelete = view.findViewById(R.id.buttonDeleteSubject);
        //Button buttonDeleteAllSubjects = view.findViewById(R.id.deleteAllSubjects);
        Button buttonEdit = view.findViewById(R.id.buttonEditSubject);

        //adding a clicklistener to button
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSubject(subject);
            }
        });

        //the delete operation
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String sql = "DELETE FROM subject WHERE id = ?";
                            database.execSQL(sql, new Integer[]{subject.getId()});
                            reloadSubjectsFromDatabase();
                        }
                    });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        //the delete operation
        buttonDelete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Are you sure you want to delete all subjects?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String sql = "DELETE FROM subject";
                        database.execSQL(sql);
                        Toast.makeText(context, "All Subjects Deleted! (refresh page to show results)", Toast.LENGTH_SHORT).show();
                        reloadSubjectsFromDatabase();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }
        });
        return view;
    }

    private void updateSubject(final Subject subject) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_update_subject, null);
        builder.setView(view);

        final EditText editTextSubjectName = view.findViewById(R.id.editTextSubjectName);
        final EditText editTextSubjectTimetableName = view.findViewById(R.id.editTextSubjectTimetableName);
        final EditText editTextSubjectRoom = view.findViewById(R.id.editTextSubjectRoom);
        final EditText editTextSubjectTeacher = view.findViewById(R.id.editTextSubjectTeacher);

        /*editTextSubjectName.setText(subject.getSubjectName());
        editTextSubjectTimetableName.setText(subject.getSubjectTimetableName());
        editTextSubjectRoom.setText(subject.getSubjectRoom());
        editTextSubjectTeacher.setText(subject.getSubjectTeacher());
         */
        final AlertDialog dialog = builder.create();
        dialog.show();

        view.findViewById(R.id.buttonUpdateSubject).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String subjectName = editTextSubjectName.getText().toString().trim();
                String subjectTimetableName = editTextSubjectTimetableName.getText().toString().trim();
                String subjectRoom = editTextSubjectRoom.getText().toString().trim();
                String subjectTeacher = editTextSubjectTeacher.getText().toString().trim();

                if (subjectName.isEmpty()) {
                    editTextSubjectName.setError("Subject name can't be blank");
                    editTextSubjectName.requestFocus();
                    return;
                }

                if (subjectTimetableName.isEmpty()) {
                    editTextSubjectTimetableName.setError("Timetable name can't be blank");
                    editTextSubjectTimetableName.requestFocus();
                    return;
                }

                if (subjectRoom.isEmpty()) {
                    editTextSubjectRoom.setError("Subject Room can't be blank");
                    editTextSubjectRoom.requestFocus();
                    return;
                }

                if (subjectTeacher.isEmpty()) {
                    editTextSubjectTeacher.setError("Subject Teacher can't be blank");
                    editTextSubjectTeacher.requestFocus();
                    return;
                }

                String sql = "UPDATE subject SET subjectName = ?, subjectTimetableName = ?, subjectRoom = ?, subjectTeacher = ? WHERE id = ?";

                database.execSQL(sql, new String[]{subjectName, subjectTimetableName, subjectRoom, subjectTeacher, String.valueOf(subject.getId())});
                Toast.makeText(context, "Subject Updated", Toast.LENGTH_SHORT).show();
                reloadSubjectsFromDatabase();
                dialog.dismiss();
            }
        });
    }

    private void reloadSubjectsFromDatabase() {
        Cursor cursorSubjects = database.rawQuery("SELECT * FROM subject", null);
        if (cursorSubjects.moveToFirst()) {
            subjectList.clear();
            do {
                subjectList.add(new Subject(
                        cursorSubjects.getInt(0),
                        cursorSubjects.getString(1),
                        cursorSubjects.getString(2),
                        cursorSubjects.getString(3),
                        cursorSubjects.getString(4)
                ));
            } while (cursorSubjects.moveToNext());
        }
        cursorSubjects.close();
        notifyDataSetChanged();
    }
}


