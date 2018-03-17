package com.example.messenger;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private static int SIGN_IN_REQUEST_CODE = 1;
//    private FirebaseListAdapter<Message> adapter;
    RelativeLayout activity_main;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        activity_main = (RelativeLayout)findViewById(R.id.activity_main);
        button = (Button)findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.editText);
                FirebaseDatabase.getInstance().getReference().push()
                        .setValue(new Message(input.getText().toString(),
                                FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                input.setText("");
            }
        });

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .build(), SIGN_IN_REQUEST_CODE);
        } else {
            this.displayChat();
        }
    }

    private void displayChat() {

        ListView listMessages = (ListView)findViewById(R.id.listView);
        DatabaseReference dbf = FirebaseDatabase.getInstance().getReference();
        FirebaseListOptions options = new FirebaseListOptions.Builder<Message>()
                .setQuery(dbf, Message.class).setLayout(R.layout.item).build();

        System.out.println("Hee1");


        FirebaseListAdapter<Message> adapter = new FirebaseListAdapter<Message>(options) {
            @Override
            protected void populateView(View v, Message model, int position) {
                System.out.println("Hee2");

                TextView textMessage, autor, timeMessage;
                textMessage = (TextView)v.findViewById(R.id.tvMessage);
                autor = (TextView)v.findViewById(R.id.tvUser);
                timeMessage = (TextView)v.findViewById(R.id.tvTime);
                textMessage.setText(model.getTextMessage());
                autor.setText(model.getAutor());
                timeMessage.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getTimeMessage()));
            }
        };
        System.out.println("Hee3");
        listMessages.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                Snackbar.make(activity_main, "Вход выполнен", Snackbar.LENGTH_SHORT).show();
                displayChat();
            } else {
                Snackbar.make(activity_main, "Вход не выполнен", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_signout)
        {
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            Snackbar.make(activity_main, "Выход выполнен", Snackbar.LENGTH_SHORT).show();
                            finish();

                        }
                    });
        }
        return true;
    }
}