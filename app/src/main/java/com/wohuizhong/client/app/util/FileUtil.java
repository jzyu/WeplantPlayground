package com.wohuizhong.client.app.util;

import android.content.Context;
import android.os.Environment;

import com.zhy.utils.L;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class FileUtil {
    public static final String TAG = "FileUtil";

    private FileUtil() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static String getAppExternalDir() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + Consts.APP_EXTERNAL_DIR;
    }

    public static boolean touchAppExternalDir() {
        return touchDir(getAppExternalDir());
    }

    public static boolean touchDir(String dirPath) {
        boolean dirJustCreated = false;
        try {
            File f = new File(dirPath);
            if (! f.exists()) {
                dirJustCreated = f.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dirJustCreated;
    }

    public static boolean copy(String srcPath, String dstPath) {
        return copy(new File(srcPath), dstPath);
    }

    public static boolean copy(File src, String dstPath) {
        try {
            File dst = new File(dstPath);
            if (! dst.createNewFile()) {
                L.d(TAG, "dst.createNewFile() failed!");
            }

            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getExtensionName(String filepath) {
        if ((filepath != null) && (filepath.length() > 0)) {
            int dot = filepath.lastIndexOf('.');
            if ((dot > -1) && (dot < (filepath.length() - 1))) {
                return filepath.substring(dot + 1);
            }
        }
        return "";
    }

    public static String getMainName(String filepath) {
        if ((filepath != null) && (filepath.length() > 0)) {
            int dot = filepath.lastIndexOf('.');
            int pathSep = filepath.lastIndexOf(File.separator);

            if (dot > -1
                    && dot < (filepath.length() - 1)
                    && pathSep > -1
                    && pathSep < (filepath.length() - 1)
                    ) {
                return filepath.substring(pathSep + 1, dot);
            }
        }
        return "";
    }

    public static String getName(String filepath) {
        return (new File(filepath)).getName();
    }

    public static String getDir(String filepath) {
        if ((filepath != null) && (filepath.length() > 0)) {
            int dot = filepath.lastIndexOf(File.separator);
            if ((dot > -1) && (dot < (filepath.length() - 1))) {
                return filepath.substring(0, dot);
            }
        }
        return "";
    }

    public static ArrayList<String> listFiles(String dir, final String extName) {
        File fDir = new File(dir);
        File[] files = fDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(extName);
            }
        });

        ArrayList<String> paths = new ArrayList<>();
        if (files == null) return paths;

        for (File f : files) paths.add(f.getPath());
        return paths;
    }

    public static void cleanDir(String dirPath) {
        try {
           cleanDirectory(new File(dirPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -- from AndroidVideoCache sample's util --
    private static void cleanDirectory(File file) throws IOException {
        if (!file.exists()) {
            return;
        }
        File[] contentFiles = file.listFiles();
        if (contentFiles != null) {
            for (File contentFile : contentFiles) {
                delete(contentFile);
            }
        }
    }

    private static void delete(File file) throws IOException {
        if (file.isFile() && file.exists()) {
            deleteOrThrow(file);
        } else {
            cleanDirectory(file);
            deleteOrThrow(file);
        }
    }

    private static void deleteOrThrow(File file) throws IOException {
        if (file.exists()) {
            boolean isDeleted = file.delete();
            if (!isDeleted) {
                throw new IOException(String.format("File %s can't be deleted", file.getAbsolutePath()));
            }
        }
    }

    public static long getDirSize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += getDirSize(file);
        }
        return length;
    }

    //return dir path under: /data/data/com.appPackageName/cache
    public static String getCacheDirPath(Context context, final String dirName) {
        String dirPath = context.getCacheDir() + File.separator + dirName;
        touchDir(dirPath);
        return dirPath;
    }

    public static String getExternalDirPath(final String dirName) {
        String dirPath = getAppExternalDir() + File.separator + dirName;
        touchDir(dirPath);
        return dirPath;
    }
}
