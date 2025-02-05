package com.example.ingenio;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ingenio.Models.Users;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.ingenio.databinding.ActivityFormBinding;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Vector;

public class FormActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    ActivityFormBinding binding;
    FirebaseDatabase database;
    DatabaseReference users;
    private FirebaseAuth auth;
    FirebaseUser uid;
    Uri uri;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance (); // obtiene la referencia a la BD
        users = database.getReference ("users"); // obtiene la referencia al nodo users
        FirebaseStorage storage = FirebaseStorage.getInstance();
        //printDatabaseChildren (users);
        binding = ActivityFormBinding.inflate (getLayoutInflater ());
        var registerView = binding.getRoot ();
        setContentView(registerView);

        // pone una etiqueda en el IV con el ID de la foto @mipmap/profile
        // servirá más adelante para saber si se cargó una foto (tomó foto con la cámara) o no
        binding.ivProfilePic.setTag (R.mipmap.profile);
        binding.btnChangePic.setOnClickListener (view1 -> {
            if (checkSelfPermission (Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions (new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
            } else {
                Intent cameraIntent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
                // como ya se tomó la foto, se pone a 0 la etiqueta del IV, 0 != @mipmap/profile
                // entonces en la llamada al método hasDefaultProfilePicture() obtendremos tagCode=0
                binding.ivProfilePic.setTag (0);
                startActivityForResult (cameraIntent, CAMERA_REQUEST);
            }
        });
        binding.btnRegister.setOnClickListener (view2 -> {
            // checar por campos obligatorios: Nombre, apellidos, correo, contraseña
            boolean hasNoName = binding.edtName.getText ().toString ().isEmpty ();
            boolean hasNoLastName = binding.edtlastName.getText ().toString ().isEmpty ();
            boolean hasNoEmail = binding.edtEmail.getText ().toString ().isEmpty ();
            boolean hasNoPassword = binding.edtPassword.getText ().toString ().isEmpty ();
            boolean promptMissingFieldsWarning = false;
            if (hasNoName) {
                binding.edtlastName.setError ("Por favor llena este campo");
                promptMissingFieldsWarning = true;
            }
            if (hasNoLastName) {
                binding.edtName.setError ("Por favor llena este campo");
                promptMissingFieldsWarning = true;
            }
            if (hasNoEmail) {
                binding.edtEmail.setError ("Por favor llena este campo");
                promptMissingFieldsWarning = true;
            }

            if (hasNoPassword) {
                binding.edtPassword.setError ("Por favor llena este campo");
                promptMissingFieldsWarning = true;
            }

            if (promptMissingFieldsWarning)
                Toast.makeText (this, "Por favor, rellena los campos marcados", Toast.LENGTH_SHORT).show ();
            else {
                String name = binding.edtName.getText ().toString ();
                String lastName = binding.edtlastName.getText ().toString ();
                String email = binding.edtEmail.getText ().toString ();
                int password = binding.edtPassword.getText ().toString ().hashCode ();
                String birthday = binding.edtBirthday.getText ().toString ();
                String phoneNumber = binding.edtTelephone.getText ().toString ();

                //aqui primero voy a registarme en FB, y luego recuperar el uid, el logueo viene hasta el final
                //ya despues voy a actualizar la base de datos


                if (!hasDefaultProfilePicture ()) {
                    // TODO: subir imagen a FB Storage
                    // si entra, significa que tomó foto con el cel, subir imagen a FB Storage
                    // user.setProfilePictureUrl (LINK_DE_STORAGE);
                    ImageView iv = binding.ivProfilePic;
                    StorageReference imagesFolder = storage.getReference ("/UsersFotos");
                    StorageReference  image = imagesFolder.child (email);

                    ByteArrayOutputStream bos = new ByteArrayOutputStream ();
                    Bitmap bitmap = getBitmapFromDrawable (iv.getDrawable ());
                    bitmap.compress (Bitmap.CompressFormat.PNG, 100, bos);
                    byte [] buffer = bos.toByteArray ();

                    image.putBytes (buffer)
                            .addOnFailureListener (e -> {
                                Toast.makeText (getBaseContext (), "Error uploading file: " + e.getMessage (), Toast.LENGTH_LONG).show ();
                                Log.e ("INGENIO", "Error uploading file: " + e.getMessage ());
                            })
                            .addOnCompleteListener (task -> {
                                if (task.isComplete ()) {
                                    Task<Uri> getUriTask = image.getDownloadUrl ();

                                    getUriTask.addOnCompleteListener (t -> {
                                        uri = t.getResult ();
                                        if (uri == null) return;

                                        //Si nos fijamos en la secuencia, primero registro al usuario, en caso de exito entonces recupero el uid
                                        //y lo subo a la base de datos
                                        //y asi ya puedo logearme
                                        Users user = new Users (birthday, email, lastName, name, password, phoneNumber, uri.toString());
                                        auth = FirebaseAuth.getInstance();
                                        String contrasena = binding.edtPassword.getText ().toString ();
                                        registerUser(email,contrasena, user);

                                        //Toast.makeText (getBaseContext (), "Download URL " + uri.toString (), Toast.LENGTH_LONG).show ();
                                        Log.i ("INGENIO", "Download URL " + uri.toString ());
                                    });
                                }
                            });
                }


                //Si nos fijamos en la secuencia, primero registro al usuario, en caso de exito entonces recupero el uid
                //y lo subo a la base de datos
                //y asi ya puedo logearme
                /*Users user = new Users (name, lastName, email, password, birthday, phoneNumber);
                auth = FirebaseAuth.getInstance();
                String contrasena = binding.edtPassword.getText ().toString ();
                registerUser(email,contrasena, user);*/

                // TODO: idear forma de crear claves únicas pues si dos usuarios se llaman igual
                // lo sobreescribirá en lugar de crear uno nuevo (creo)
                //saveNewUser (name.toLowerCase (), user);
                //saveNewUser (uid.getUid(), user); // guarda al usuario en la BD
                // TODO: Crear perfil en FB con los datos ingresados e iniciar sesión


            }
        });
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult (requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText (this, "camera permission granted", Toast.LENGTH_LONG).show ();
                Intent cameraIntent = new Intent (android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult (cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText (this, "camera permission denied", Toast.LENGTH_LONG).show ();
            }
        }
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras ().get ("data");
            binding.ivProfilePic.setImageBitmap (photo);
        }
    }

    private boolean hasDefaultProfilePicture () {
        Integer tagCode = (Integer) binding.ivProfilePic.getTag();
        tagCode = (tagCode == null) ? 0 : tagCode;

        return tagCode == R.mipmap.profile; // si es diferente de @mipmap/profile, tiene foto
    }

    private void printDatabaseChildren (DatabaseReference reference) {
        //Vector<Users> vUsers = new Vector<>();
        reference.addListenerForSingleValueEvent (new ValueEventListener() {
            @Override
            public void onDataChange (@NonNull DataSnapshot dataSnapshot) {
                // datasnapShot hace referencia a la rama users
                // el método getChildren devuelve cada subelemento user
                for (DataSnapshot item: dataSnapshot.getChildren ()) {
                    Log.d ("TAG", item.getKey() + " - " +  item.getValue ().toString());
                    // getValue devuelve referencia al objeto contenido en cada nodo
                    // y lo convierte al tipo de datos indicado en el parámetro
                    Users user = item.getValue (Users.class);
          //          vUsers.add (user);
                    Log.d ("TAG", user.getName() + " " + user.getLastName());
                }
            }

            @Override
            public void onCancelled (@NonNull DatabaseError databaseError) {
                Log.e ("Ingenio", databaseError.getMessage ());
            }
        });
    }

    /**
     * agrega un nuevo elemento a la rama users
     */
    private void saveNewUser (String nodeName, Users newUser) {
        FirebaseDatabase database = FirebaseDatabase.getInstance ();
        DatabaseReference users = database.getReference ("users");

        HashMap<String, Object> node = new HashMap<>();
        node.put (nodeName, newUser);

        users.updateChildren (node)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText (getBaseContext (), "Ndde added successfully", Toast.LENGTH_LONG).show ();

                })
                .addOnFailureListener(e -> Toast.makeText (getBaseContext (), "Ndde add failed: " + e.getMessage (), Toast.LENGTH_LONG).show ());
    }

    private void registerUser (String email, String password, Users user) {
        auth.createUserWithEmailAndPassword (email, password)
                .addOnCompleteListener (task -> {
                    if (task.isSuccessful ()) {
                        Toast.makeText (getBaseContext(), "Register completed!", Toast.LENGTH_LONG).show ();
                        //comentamos el login ya que todavia no queremos que cambie de contexto

                        saveNewUser (auth.getUid(), user);
                        login (email, password);
                    } else {
                        if (task.getException () != null) {
                            Log.e("ingenio", task.getException().getMessage());
                        }

                        Toast.makeText (getBaseContext(), "Register failed!", Toast.LENGTH_LONG).show ();
                    }
                });
    }

    private void login (String email, String password) {
        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        FirebaseUser user = auth.getCurrentUser();
                        String name = "";

                        if(user != null){
                            name = user.getDisplayName();
                        }

                        Toast.makeText(getBaseContext(),"Usuario" + name, Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(getBaseContext(), PresentActivity.class);

                        startActivity(intent);
                    } else {
                        Toast.makeText(getBaseContext(),"Usuario y/o contraseña no reconocida",Toast.LENGTH_LONG).show();
                    }
                });
    }

    private Bitmap getBitmapFromDrawable (Drawable drble) {
        // debido a la forma que el sistema dibuja una imagen en un el sistema gráfico
        // es necearios realzar comprobaciones para saber del tipo de objeto Drawable
        // con que se está trabajando.
        //
        // si el objeto recibido es del tipo BitmapDrawable no se requieren más conversiones
        if (drble instanceof BitmapDrawable) {
            return  ((BitmapDrawable) drble).getBitmap ();
        }

        // en caso contrario, se crea un nuevo objeto Bitmap a partir del contenido
        // del objeto Drawable
        Bitmap bitmap = Bitmap.createBitmap (drble.getIntrinsicWidth (), drble.getIntrinsicHeight (), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drble.setBounds (0, 0, canvas.getWidth (), canvas.getHeight ());
        drble.draw (canvas);

        return bitmap;
    }
}
