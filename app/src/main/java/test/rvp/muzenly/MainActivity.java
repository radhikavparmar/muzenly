package test.rvp.muzenly;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_REQUEST_CONTACT = 123;
    private RecyclerView recyclerView;
    private ContactsAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    Button addButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        askPermission();
        displayContacts();
    }

    private void displayContacts() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Contacts> realmResults = realm.where(Contacts.class).findAllAsync().sort("name");

                mAdapter = new ContactsAdapter(realmResults);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(mAdapter);
                recyclerView.setFocusable(false);
    }

    private void init() {
        addButton = (Button)findViewById(R.id.button);
        addButton.setOnClickListener(this);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
    }

    private void readPhoneContactsAndAddTwentyK() {
        //add only once //todo | if file exists or not
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", false)) {
            // <---- run your one time code here


            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE NOCASE ASC");
            Log.i("Size is ", " " + phones.getCount());

            if (phones != null && (phones.getCount() > 0)) {
                phones.moveToFirst();
                phones.move(0);

                for (int i = 0; i < phones.getCount(); i++) {


                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumberStr = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    final Contacts object = new Contacts();
                    object.setName(name);
                    object.setNumber(phoneNumberStr);


                    //Enter Data to Realm
                    Realm realm = Realm.getDefaultInstance();
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            realm.insertOrUpdate(object);
                        }
                    });


                    phones.moveToNext();
                }
                phones.close();

                //add extra 20k contacts
                Realm realm = Realm.getDefaultInstance();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {

                        for(int i = 0 ;i<20000;i++){
                            final Contacts object = new Contacts();
                            object.setName("ztest_"+i+"_");
                            object.setNumber("1234567890");

                            realm.insertOrUpdate(object);
                        }

                    }
                });

                // mark first time has ran.
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("firstTime", true);
                editor.commit();
            }
        }
    }

    private void askPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Access required");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("Please confirm access to contacts");//TODO put real question
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {Manifest.permission.READ_CONTACTS}
                                    , PERMISSION_REQUEST_CONTACT);
                        }
                    });
                    builder.show();
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            PERMISSION_REQUEST_CONTACT);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }else{
                readPhoneContactsAndAddTwentyK();
            }
        }
        else{
            readPhoneContactsAndAddTwentyK();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CONTACT: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readPhoneContactsAndAddTwentyK();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(this, "No Permissions ", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button:
                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(this);
                View promptsView = li.inflate(R.layout.alert_layout, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

                alertDialogBuilder.setView(promptsView);

                final EditText nameEditText = (EditText) promptsView.findViewById(R.id.name_edittext);
                final EditText numberEditText = (EditText) promptsView.findViewById(R.id.numb_edittext);

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {


                                        Realm realm = Realm.getDefaultInstance();
                                        realm.executeTransaction(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {

                                                   Contacts object = new Contacts();
                                                    object.setName(nameEditText.getText().toString());
                                                    object.setNumber(numberEditText.getText().toString());
                                                    realm.insertOrUpdate(object);


                                            }
                                        });
                                        displayContacts();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertDialog = alertDialogBuilder.create();

                alertDialog.show();
                break;
        }
    }
}
