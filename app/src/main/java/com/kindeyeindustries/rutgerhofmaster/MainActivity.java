package com.kindeyeindustries.rutgerhofmaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private Button restaurant, taverne, terras;
    private Boolean restaurantBool, taverneBool, terrasBool;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        restaurant = findViewById(R.id.main_btn_restaurant);
        taverne = findViewById(R.id.main_btn_taverne);
        terras = findViewById(R.id.main_btn_terras);

        getFirestoreData();



        restaurant.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
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

}