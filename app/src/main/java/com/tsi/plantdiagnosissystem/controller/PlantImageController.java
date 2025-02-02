package com.tsi.plantdiagnosissystem.controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;

import com.tsi.plantdiagnosissystem.controller.database.databasecontroller.PlantImageCtr;
import com.tsi.plantdiagnosissystem.data.model.PlantImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class PlantImageController {

    public static void saveImageToDatabase(PlantImage plantImage) {
        try {
            PlantImageCtr plantImageCtr = new PlantImageCtr();
            plantImageCtr.addPlantImage(plantImage.getPlantName(), plantImage.getImageUrl());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File saveImageExternalStorage(Context context, Bitmap bmp, String cropName) throws IOException {
        String imageName = String.valueOf(System.currentTimeMillis());
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        //"/data/data/"
        String filePath = Utils.getAbsolutePath(context) + File.separator + "IMAGES" + File.separator + cropName;
        boolean isFileExist = Utils.createDirectoryIfNotExist(filePath);

        File f = null;
        try {
            f = Utils.getAbsoluteFile("IMAGES" + File.separator + cropName + File.separator + imageName + ".jpg", context); //new File(filePath , imageName + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return f;
    }

    public static ArrayList<PlantImage> getPlantImages() {
        ArrayList<PlantImage> plantImages = new ArrayList<PlantImage>();
        PlantImageCtr plantImageCtr = new PlantImageCtr();
        return plantImageCtr.getPlantImages();

    }

    public static Bitmap getBitmapFromUri(Context context, Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }
}
