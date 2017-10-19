package todolist.studio.com.todolist;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private ListView listView;
    private EditText editText;
    private SQLiteDatabase dataBase;

    private ArrayAdapter<String> itemsAdapter;
    private ArrayList<String> items;
    private ArrayList<Integer> ids;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            //Recover components
            button = (Button) findViewById(R.id.buttonAddId);
            editText = (EditText) findViewById(R.id.textId);
            listView = (ListView) findViewById(R.id.listViewId);


            //Data base
            dataBase = openOrCreateDatabase("apptasks", MODE_PRIVATE, null);

            //Tasks table
            dataBase.execSQL("CREATE TABLE IF NOT EXISTS tasks (id INTEGER PRIMARY KEY AUTOINCREMENT, task VARCHAR)");

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String textTyped = editText.getText().toString();
                    saveTask(textTyped);

                }
            });

            listView.setLongClickable(true);
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    removeTask(ids.get(position));
                    return true;
                }
            });

            //Return tasks
            returnTasks();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void saveTask(String textTyped){
        try{
            if(!textTyped.equals("")){
                dataBase.execSQL("INSERT INTO tasks (task) VALUES('" + textTyped + "')");
                Toast.makeText(MainActivity.this, "Tarefa salva com sucesso.", Toast.LENGTH_SHORT).show();
                returnTasks();
                editText.setText("");
            }else{
                Toast.makeText(MainActivity.this, "Digite uma tarefa.", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void returnTasks(){
        try{
            //Return tasks
            Cursor cursor = dataBase.rawQuery("SELECT * FROM tasks ORDER BY id DESC", null);

            //Returns ids from column
            int indexColumnId = cursor.getColumnIndex("id");
            int indexColumnTask = cursor.getColumnIndex("task");

            //Create adapter
            items = new ArrayList<String>();
            ids = new ArrayList<Integer>();
            itemsAdapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_list_item_2,
                    android.R.id.text2,
                    items);
            listView.setAdapter(itemsAdapter);

            //To list the tasks
            cursor.moveToFirst();
            while (cursor != null){
                items.add(cursor.getString(indexColumnTask));
                ids.add(Integer.parseInt(cursor.getString(indexColumnId)));
                cursor.moveToNext();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void removeTask(Integer id){
        try{
            dataBase.execSQL("DELETE FROM tasks WHERE id="+id);
            returnTasks();
            Toast.makeText(MainActivity.this, "Tarefa removida com sucesso.", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
