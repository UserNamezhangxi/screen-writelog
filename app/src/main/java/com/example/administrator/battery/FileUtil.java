package com.example.administrator.battery;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {
    private static String TAG = "FileUtil";
    private static String filenameTemp = Environment.getExternalStorageDirectory().getAbsolutePath() ;//文件路径+名称+文件类型;
    private static String fileName = "/zxtime.txt";
    // 生成文件夹
    public static boolean makeRootDirectory() {
        File file = null;
        try {
            file = new File(filenameTemp);
            if (!file.exists()) {
               return file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e+"");
        }
        return false;
    }
    public  static boolean createFile(){
        makeRootDirectory();
        Boolean bool = false;
        Log.d(TAG, "file path = "+filenameTemp+fileName);
        File file = new File(filenameTemp+fileName);
        try {
            //如果文件不存在，则创建新的文件
            if(!file.exists()){
               bool = file.createNewFile();
               Log.d(TAG,"success create file");
            }
        } catch (Exception e) {
            Log.e(TAG,"fail create file");
            e.printStackTrace();
        }
        return bool;
    }

    public static void writeFileContent(String newstr) throws IOException {
        try {
            File file = new File(Environment.getExternalStorageDirectory(), fileName);
            FileWriter writer = new FileWriter(file, true);
            writer.write("\n\r"+newstr);
            writer.close();
            Log.d(TAG,"writeFileContent success");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readToString() {
        File file = new File(filenameTemp);
        Long filelength = file.length();
        FileInputStream  in = null;
        byte[] filecontent = new byte[filelength.intValue()];
        try {
            in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
            return  filecontent.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (in!=null){
                try {
                    in.close();
                }catch (IOException e){

                }
            }
        }
        return "";
    }
}
