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

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import ie.wit.collegeplanner.R;
import ie.wit.collegeplanner.model.Assignment;

public class ViewAssignments extends Base {

    SQLiteDatabase database;
    List<Assignment> assignmentList;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_assignments);
        database = openOrCreateDatabase(Subjects.DATABASE_NAME, MODE_PRIVATE, null);
        assignmentList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listViewAssignments);
        loadAssignmentsFromDatabase();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_assignment_search, menu);
        MenuItem searchViewItem = menu.findItem(R.id.assignmentSearch);
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

    private void loadAssignmentsFromDatabase() {
        String sql="SELECT * FROM assignment";
        Cursor cursor = database.rawQuery(sql, null);

        if(cursor.moveToFirst()) {
            do {
                assignmentList.add(new Assignment(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3)
                ));
            } while (cursor.moveToNext());

            AssignmentAdapter adapter = new AssignmentAdapter(this, R.layout.list_layout_assignment, assignmentList, database);
            listView.setAdapter(adapter);
        }
    }
}

class AssignmentAdapter extends ArrayAdapter<Assignment> {

    Context context;
    int listLayout;
    List<Assignment> assignmentList;
    SQLiteDatabase database;

    public AssignmentAdapter(Context context, int listLayout, List<Assignment> assignmentList, SQLiteDatabase database) {
        super(context, listLayout, assignmentList);

        this.context = context;
        this.listLayout = listLayout;
        this.assignmentList = assignmentList;
        this.database = database;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(listLayout, null);

        //getting views
        TextView assignmentNameTextView = view.findViewById(R.id.assignmentNameTextView);
        TextView assignmentSubjectTextView = view.findViewById(R.id.assignmentSubjectTextView);
        TextView assignmentNoteTextView = view.findViewById(R.id.assignmentNoteTextView);

        final Assignment assignment = assignmentList.get(position);

        //adding data to views
        assignmentNameTextView.setText(assignment.getAssignmentName());
        assignmentSubjectTextView.setText(assignment.getAssignmentSubject());
        assignmentNoteTextView.setText(assignment.getAssignmentNote());

        //use these buttons for update and delete operation
        Button buttonDelete = view.findViewById(R.id.buttonDeleteAssignment);
        Button buttonEdit = view.findViewById(R.id.buttonEditAssignment);

        //adding a clicklistener to button
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAssignment(assignment);
            }
        });

        buttonDelete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Are you sure you want to delete all assignment?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String sql = "DELETE FROM assignment";
                        database.execSQL(sql);
                        Toast.makeText(context, "All Assignments Deleted! (refresh page to show results)", Toast.LENGTH_SHORT).show();
                        reloadAssignmentsFromDatabase();
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
        //the delete operation
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String sql = "DELETE FROM assignment WHERE id = ?";
                        database.execSQL(sql, new Integer[]{assignment.getId()});
                        reloadAssignmentsFromDatabase();
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
        return view;
    }

    private void updateAssignment(final Assignment assignment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_update_assignment, null);
        builder.setView(view);

        final EditText editTextAssignmentName = view.findViewById(R.id.editTextAssignmentName);
        final Spinner spinnerSubject = (Spinner) view.findViewById(R.id.spinnerSubject);
        final EditText editTextAssignmentNote = view.findViewById(R.id.editTextAssignmentNote);

        /*editTextAssignmentName.setText(assignment.getAssignmentName());
        editTextAssignmentNote.setText(assignment.getAssignmentNote());
*/
        final AlertDialog dialog = builder.create();
        dialog.show();

        view.findViewById(R.id.buttonUpdateAssignment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String assignmentName = editTextAssignmentName.getText().toString().trim();
                String assignmentSubject = spinnerSubject.getSelectedItem().toString().trim();
                String assignmentNote = editTextAssignmentNote.getText().toString().trim();

                if (assignmentName.isEmpty()) {
                    editTextAssignmentName.setError("Assignment name can't be blank");
                    editTextAssignmentName.requestFocus();
                    return;
                }

                if (assignmentNote.isEmpty()) {
                    editTextAssignmentNote.setError("Assignment note can't be blank");
                    editTextAssignmentNote.requestFocus();
                    return;
                }

                String sql = "UPDATE assignment SET assignmentName = ?, assignmentSubject = ?, assignmentNote = ? WHERE id = ?";

                database.execSQL(sql, new String[]{assignmentName, assignmentSubject, assignmentNote, String.valueOf(assignment.getId())});
                Toast.makeText(context, "Assignment Updated", Toast.LENGTH_SHORT).show();
                reloadAssignmentsFromDatabase();
                dialog.dismiss();
            }
        });
    }

    private void reloadAssignmentsFromDatabase() {
        Cursor cursorAssignments = database.rawQuery("SELECT * FROM assignment", null);
        if (cursorAssignments.moveToFirst()) {
            assignmentList.clear();
            do {
                assignmentList.add(new Assignment(
                        cursorAssignments.getInt(0),
                        cursorAssignments.getString(1),
                        cursorAssignments.getString(2),
                        cursorAssignments.getString(3)
                ));
            } while (cursorAssignments.moveToNext());
        }
        cursorAssignments.close();
        notifyDataSetChanged();
    }
}


