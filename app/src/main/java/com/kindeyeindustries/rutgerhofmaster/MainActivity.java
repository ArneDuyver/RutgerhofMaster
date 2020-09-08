package com.kindeyeindustries.rutgerhofmaster;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static com.kindeyeindustries.rutgerhofmaster.App.CHANNEL_ID;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private Button restaurant, taverne, terras;
    private Boolean restaurantBool, taverneBool, terrasBool;
    private FirebaseFirestore db;
    private NotificationManagerCompat notificationManager;

    //TODO: fix problem where notification goes off again when u press the notification

    @SuppressLint("BatteryLife")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }

        db = FirebaseFirestore.getInstance();

        restaurant = findViewById(R.id.main_btn_restaurant);
        taverne = findViewById(R.id.main_btn_taverne);
        terras = findViewById(R.id.main_btn_terras);

        getFirestoreData();

        //<editor-fold desc="OnClickListeners">
        restaurant.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {https://stackoverflow.com/questions/8070212/why-set-setbackgroundcolor-is-not-working-in-my-custom-listview
                clearFirebase(1);
                restaurant.setBackgroundResource(R.color.buttonColor);
            }
        });
        taverne.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                clearFirebase(2);
                taverne.setBackgroundResource(R.color.buttonColor);
            }
        });
        terras.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                clearFirebase(3);
                terras.setBackgroundResource(R.color.buttonColor);
            }
        });
        //</editor-fold>

        notificationManager = NotificationManagerCompat.from(this);


        DocumentReference docRef = db.collection("Rutgerhof").document("N91YwwPaBhR4p1vy09TK");
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    restaurantBool = snapshot.getBoolean("Restaurant");
                    taverneBool = snapshot.getBoolean("Taverne");
                    terrasBool = snapshot.getBoolean("Terras");

                    if (restaurantBool){
                        restaurant.setBackgroundResource(R.color.buttonColorRed);
                        sendNotification("Restaurant",1);
                    } else {
                        restaurant.setBackgroundResource(R.color.buttonColor);
                    }
                    if (taverneBool){
                        taverne.setBackgroundResource(R.color.buttonColorRed);
                        sendNotification("Taverne",2);

                    }else {
                        taverne.setBackgroundResource(R.color.buttonColor);
                    }
                    if (terrasBool){
                        terras.setBackgroundResource(R.color.buttonColorRed);
                        sendNotification("Terras",3);

                    }else {
                        terras.setBackgroundResource(R.color.buttonColor);
                    }
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    private void clearFirebase(int index) {
        DocumentReference rutgerhofRef = db.collection("Rutgerhof").document("N91YwwPaBhR4p1vy09TK");
        String field = null;
        switch (index){
            case 1:
                field = "Restaurant";
                break;
            case 2:
                field = "Taverne";
                break;
            case 3:
                field = "Terras";
                break;
        }
        assert field != null;
        rutgerhofRef.update(field,false)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating document", e);
                        }
                    });
    }

    public void getFirestoreData(){
        db.collection("Rutgerhof")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                restaurantBool = document.getBoolean("Restaurant");
                                taverneBool = document.getBoolean("Taverne");
                                terrasBool = document.getBoolean("Terras");

                                if (restaurantBool){
                                    restaurant.setBackgroundResource(R.color.buttonColorRed);
                                }
                                if (taverneBool){
                                    taverne.setBackgroundResource(R.color.buttonColorRed);
                                }
                                if (terrasBool){
                                    terras.setBackgroundResource(R.color.buttonColorRed);
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void sendNotification(String message,int notificationId){
        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_add_alert_24)
                .setContentTitle("Bel gaat")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(contentIntent)
                .build();

        notificationManager.notify(notificationId,notification);
    }

}