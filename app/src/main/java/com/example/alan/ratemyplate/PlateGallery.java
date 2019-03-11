package com.example.alan.ratemyplate;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class PlateGallery extends AppCompatActivity {
    GridView gridView;
    ArrayList<Plate> list;
    PlateGalleryAdapter adapter = null;

//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState){
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.plate_list_activity);
//
//        gridView = (GridView) findViewById(R.id.gridView);
//        list = new ArrayList<>();
//        adapter = new PlateGalleryAdapter(this, R.layout.plate_items, list);
//        gridView.setAdapter(adapter);
//
//        Cursor cursor = MainActivity.plateUploader.getData("SELECT * FROM PLATE");
//        list.clear();
//        while (cursor.moveToNext()){
//            int id = cursor.getInt(0);
//            String name = cursor.getString(1);
//            String caption = cursor.getString(2);
//            byte[] image = cursor.getBlob(3);
//
//            list.add(new Plate(name, caption, image, id));
//        }
//        adapter.notifyDataSetChanged();
//
//        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
//                CharSequence[] items = {"Update", "Delete"};
//                AlertDialog.Builder dialog = new AlertDialog.Builder(PlateGallery.this);
//
//                dialog.setTitle("Choose an action");
//                dialog.setItems(items, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int item) {
//                        if (item == 0) {
//                            Cursor c = MainActivity.plateUploader.getData("SELECT id FROM PLATE");
//                            ArrayList<Integer> arrID = new ArrayList<Integer>();
//                            while (c.moveToNext()){
//                                arrID.add(c.getInt(0));
//                            }
//                            showDialogUpdate(PlateGallery.this, arrID.get(position));
//
//                        } else{
//                            Cursor c = MainActivity.plateUploader.getData("SELECT id FROM PLATE");
//                            ArrayList<Integer> arrID = new ArrayList<Integer>();
//                            while (c.moveToNext()){
//                                arrID.add(c.getInt(0));
//                            }
//                            showDialogDelete(arrID.get(position));
//                        }
//                    }
//                });
//                dialog.show();
//                return true;
//            }
//
//        });
//    }

    ImageView imageViewPlate;
    private void showDialogUpdate(Activity activity, final int position){

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update_plate_activity);
        dialog.setTitle("Update");

        imageViewPlate = (ImageView) dialog.findViewById(R.id.imageViewPlate);
        final EditText edtName = (EditText) dialog.findViewById(R.id.edtName);
        final EditText edtCaption = (EditText) dialog.findViewById(R.id.edtCaption);
        Button btnUpdate = (Button) dialog.findViewById(R.id.btnUpdate);

        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);

        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.7);
        dialog.getWindow().setLayout(width, height);
        dialog.show();

        imageViewPlate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                ActivityCompat.requestPermissions(
                        PlateGallery.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        888
                );
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                try {
                    MainActivity.plateUploader.updateData(
                            edtName.getText().toString().trim(),
                            edtCaption.getText().toString().trim(),
                            MainActivity.imageViewToByte(imageViewPlate),
                            position
                    );
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Update successful!", Toast.LENGTH_SHORT).show();
                }
                catch (Exception error){
                    Log.e("Update error", error.getMessage());
                }
                updatePlateList();
            }
        });
    }

    private void showDialogDelete(final int idPlate){
        final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(PlateGallery.this);

        dialogDelete.setTitle("Warning!");
        dialogDelete.setMessage("Are you sure you want to delete this plate?");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    MainActivity.plateUploader.deleteData(idPlate);
                    Toast.makeText(getApplicationContext(), "Delete successful!",Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    Log.e("Error", e.getMessage());
                }
                updatePlateList();
            }
        });

        dialogDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogDelete.show();
    }

    private void updatePlateList(){
        // get all data from sqlite
        Cursor cursor = MainActivity.plateUploader.getData("SELECT * FROM PLATE");
        list.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String caption = cursor.getString(2);
            byte[] image = cursor.getBlob(3);

            list.add(new Plate(name, caption, image, id));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 888){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 888);
            }
            else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access the file location!", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 888 && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageViewPlate.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


}
