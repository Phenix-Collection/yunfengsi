package com.yunfengsi.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;

public class FileUtils {

    private static final String TAG = "FileUtils";
    public static final String TEMPPAH = Environment.getExternalStorageDirectory() + File.separator
            + Constants.cacheO + "/temp/";

    public static void saveBitmap(Bitmap bm, String picName) {
        System.out.println("-----------------------------");
        try {
            if (!isFileExist(picName)) {
                System.out.println("创建文件");
                File tempf = createSDDir(picName);
            }
            File f = new File(TEMPPAH, picName);
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 30, out);
            out.flush();
            out.close();
            bm.recycle();
            bm = null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File createSDDir(String dirName) throws IOException {
        File dir = new File(TEMPPAH + dirName);
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {

            Log.e(TAG, "createSDDir: " + dir.getAbsolutePath());
            Log.e(TAG, "createSDDir: " + dir.mkdirs());
        }
        return dir;
    }

    public static boolean isFileExist(String fileName) {
        File file = new File(TEMPPAH + fileName);
        file.isFile();
        System.out.println(file.exists());
        return file.exists();
    }

    public static void delFile(String fileName) {
        File file = new File(TEMPPAH + fileName);
        if (file.isFile()) {
            file.delete();
        }
        file.exists();
    }

    public static void deleteDir() {
        File dir = new File(TEMPPAH);
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;

        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete();
            else if (file.isDirectory())
                deleteDir();
        }
        dir.delete();
    }

    public static boolean fileIsExists(String path) {
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {

            return false;
        }
        return true;
    }

    /**
     * 请空文件夹
     *
     * @param dir
     */
    public static void deleteFile(File dir) {
        if (dir.isDirectory()) {
            String list[] = dir.list();
            for (String f : list) {
                File file = new File(dir.getAbsolutePath() + "/" + f);
                if (file.isDirectory()) {
                    deleteFile(file);
                } else {
                    file.delete();
                }
            }
        } else if (dir.isFile()) {
            dir.delete();
        }

    }

    /**
     * 获取文件夹大小
     *
     * @param dir
     * @return
     * @throws Exception
     */
    public static long getFileSize(File dir) throws Exception
    //获取文件夹大小
    {
        long size = 0;
        if (dir.isDirectory()) {
            File flist[] = dir.listFiles();
            for (int i = 0; i < flist.length; i++) {
                File file = flist[i];
                if (file.isDirectory()) {
                    size = size + getFileSize(file);
                } else {
                    size = size + file.length();
                }
            }
        } else if (dir.isFile()) {
            size = dir.length();
        }


        return size;
    }

    /**
     * 速度最快的复制文件方式
     *
     * @param source
     * @param dest
     * @throws IOException
     */
    public static void copyFileUsingFileChannels(File source, File dest) throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
        }
    }

    /**
     * 数据存放在本地
     *
     * @param tArrayList
     */
    public static void saveStorage2SDCard(Context context, ArrayList tArrayList, String fileName) {

        FileOutputStream fileOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        FileInputStream fileInputStream = null;
        try {
            File f = new File(context.getExternalCacheDir() + File.separator + fileName);
            fileOutputStream = new FileOutputStream(f);  //新建一个内容为空的文件
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(tArrayList);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (objectOutputStream != null) {
            try {
                objectOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (fileOutputStream != null) {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取本地的List数据
     *
     * @return
     */
    public static ArrayList<String> getStorageStringEntities(Context context, String fileName) {
        ObjectInputStream objectInputStream = null;
        FileInputStream fileInputStream = null;
        ArrayList<String> savedArrayList = null;
        try {
            File file = new File(context.getExternalCacheDir() + File.separator + fileName);
            fileInputStream = new FileInputStream(file.toString());
            objectInputStream = new ObjectInputStream(fileInputStream);
            savedArrayList = (ArrayList<String>) objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return savedArrayList;
    }

    /**
     * 获取本地的int数据
     *
     * @return
     */
    public static ArrayList<Integer> getStorageIntEntities(Context context, String fileName) {
        ObjectInputStream objectInputStream = null;
        FileInputStream fileInputStream = null;
        ArrayList<Integer> savedArrayList = null;
        try {
            File file = new File(context.getExternalCacheDir() + File.separator + fileName);
            fileInputStream = new FileInputStream(file.toString());
            objectInputStream = new ObjectInputStream(fileInputStream);
            savedArrayList = (ArrayList<Integer>) objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return savedArrayList;
    }

    /**
     * 获取本地的List数据
     *
     * @return
     */
    public static ArrayList<HashMap<String, Object>> getStorageMapEntities(Context context, String fileName) {
        ObjectInputStream objectInputStream = null;
        FileInputStream fileInputStream = null;
        ArrayList<HashMap<String, Object>> savedArrayList = null;
        try {
            File file = new File(context.getExternalCacheDir() + File.separator + fileName);
            fileInputStream = new FileInputStream(file.toString());
            objectInputStream = new ObjectInputStream(fileInputStream);
            savedArrayList = (ArrayList<HashMap<String, Object>>) objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return savedArrayList;
    }

    /**
     * 获取本地的List数据
     *
     * @return
     */
    public static ArrayList getStorageMapEntitiesEasy(Context context, String fileName) {
        ObjectInputStream objectInputStream = null;
        FileInputStream fileInputStream = null;
        ArrayList savedArrayList = null;
        try {
            File file = new File(context.getExternalCacheDir() + File.separator + fileName);
            fileInputStream = new FileInputStream(file.toString());
            objectInputStream = new ObjectInputStream(fileInputStream);
            savedArrayList = (ArrayList) objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return savedArrayList;
    }

    public static String getFileEncode2(String path) {
        InputStream inputStream = null;
        byte[] head = new byte[3];

        try {
            inputStream = new FileInputStream(path);
            inputStream.read(head);
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String code = "";

//        code = "gb2312";
//        if (head[0] == -1 && head[1] == -2)
//            code = "UTF-16";
//        if (head[0] == -2 && head[1] == -1)
//            code = "Unicode";
//        if (head[0] == -17 && head[1] == -69 && head[2] == -65)
            code = "UTF-8";

        return code;
    }

    public static String getFileEncode(String path) {
        String charset = "asci";
        byte[] first3Bytes = new byte[3];
        BufferedInputStream bis = null;
        try {
            boolean checked = false;
            bis = new BufferedInputStream(new FileInputStream(path));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1)
                return charset;
            if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "Unicode";//UTF-16LE
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF) {
                charset = "Unicode";//UTF-16BE
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF8";
                checked = true;
            }

            bis.reset();
            if (!checked) {
                int len = 0;
                int loc = 0;
                while ((read = bis.read()) != -1) {
                    loc++;
                    if (read >= 0xF0)
                        break;
                    if (0x80 <= read && read <= 0xBF) //单独出现BF以下的，也算是GBK
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF)
                            //双字节 (0xC0 - 0xDF) (0x80 - 0xBF),也可能在GB编码内
                            continue;
                        else
                            break;
                    } else if (0xE0 <= read && read <= 0xEF) { //也有可能出错，但是几率较小
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else
                                break;
                        } else
                            break;
                    }
                }
                //TextLogger.getLogger().info(loc + " " + Integer.toHexString(read));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException ex) {
                }
            }
        }
        return charset;
    }

    public static String getEncode(int flag1, int flag2, int flag3) {
        String encode = "";
        // txt文件的开头会多出几个字节，分别是FF、FE（Unicode）,
        // FE、FF（Unicode big endian）,EF、BB、BF（UTF-8）
        if (flag1 == 255 && flag2 == 254) {
            encode = "Unicode";
        } else if (flag1 == 254 && flag2 == 255) {
            encode = "UTF-16";
        } else if (flag1 == 239 && flag2 == 187 && flag3 == 191) {
            encode = "UTF8";
        } else {
            encode = "asci";// ASCII码
        }
        return encode;
    }

    // TODO: 2018/4/19 获取本地缓存路径
    public File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * 按照指定的路径和编码格式保存文件内容，这个方法因为用到了字符串作为载体，为了正确写入文件（不乱码），只能写入文本内容，安全方法
     *
     * @param data 将要写入到文件中的字节数据
     * @param path 文件路径,包含文件名
     * @return boolean
     * 当写入完毕时返回true;
     */
    public static boolean writeFile(byte data[], String path, String code) {
        boolean flag = true;
        OutputStreamWriter osw = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                file = new File(file.getParent());
                if (!file.exists()) {
                    file.mkdirs();
                }
            }
            if ("asci".equals(code)) {
                code = "GBK";
            }
            osw = new OutputStreamWriter(new FileOutputStream(path), code);
            osw.write(new String(data, code));
            osw.flush();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i("toFile IO Exception:" + e.getMessage());
            flag = false;
        } finally {
            try {
                if (osw != null) {
                    osw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                LogUtil.i("toFile IO Exception:" + e.getMessage());
                flag = false;
            }
        }
        return flag;
    }

    /**
     * 从指定路径读取文件到字节数组中,对于一些非文本格式的内容可以选用这个方法
     * 457364578634785634534
     *
     * @param path 文件路径,包含文件名
     * @return byte[]
     * 文件字节数组
     */
    public static byte[] getFile(String path) throws IOException {
        FileInputStream stream = new FileInputStream(path);
        int size = stream.available();
        byte data[] = new byte[size];
        stream.read(data);
        stream.close();
        stream = null;
        return data;
    }


    /**
     * 把字节内容写入到对应的文件，对于一些非文本的文件可以采用这个方法。
     *
     * @param data 将要写入到文件中的字节数据
     * @param path 文件路径,包含文件名
     * @return boolean isOK 当写入完毕时返回true;
     * @throws Exception
     */
    public static boolean toFile(byte data[], String path) throws Exception {
        FileOutputStream out = new FileOutputStream(path);
        out.write(data);
        out.flush();
        out.close();
        out = null;
        return true;
    }
}