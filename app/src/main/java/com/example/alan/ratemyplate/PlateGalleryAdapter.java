package com.example.alan.ratemyplate;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PlateGalleryAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<Plate> plateList;

    public PlateGalleryAdapter(Context context, int layout, ArrayList<Plate> plateList){
        this.context = context;
        this.layout = layout;
        this.plateList = plateList;
    }

    @Override
    public int getCount(){
        return plateList.size();
    }

    @Override
    public Object getItem(int position){
        return plateList.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    private class ViewHolder{
        ImageView imageView;
        TextView txtName, txtCaption;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup){

        View row = view;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.txtName = (TextView) row.findViewById(R.id.txtName);
            holder.txtCaption = (TextView) row.findViewById(R.id.txtCaption);
            holder.imageView = (ImageView) row.findViewById(R.id.imgPlate);
            row.setTag(holder);
        }

        else{
            holder = (ViewHolder) row.getTag();
        }

        Plate plate = plateList.get(position);

        holder.txtName.setText(plate.getName());
        holder.txtCaption.setText(plate.getCaption());

        byte[] plateImage = plate.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(plateImage, 0, plateImage.length);
        holder.imageView.setImageBitmap(bitmap);

        return row;
    }

}
