
package com.cloverstudio.imagechange.app.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用文件工具
 * 
 * @author wlei 2018-06-21
 */
public class CommonFileUtil {
    // 验证字符串是否为正确路径名的正则表达式
    private String matches = "[A-Za-z]:\\\\[^:?\"><*]*";

    // 通过 sPath.matches(matches) 方法的返回值判断是否正确
    // sPath 为路径字符串
    boolean flag = false;

    File file;

    public static CommonFileUtil get() {
        return new CommonFileUtil();
    }

    private CommonFileUtil() {
    }

    public boolean DeleteFolder(String deletePath) {// 根据路径删除指定的目录或文件，无论存在与否
        flag = false;
        if (deletePath.matches(matches)) {
            file = new File(deletePath);
            if (!file.exists()) {// 判断目录或文件是否存在
                return flag; // 不存在返回 false
            }
            else {

                if (file.isFile()) {// 判断是否为文件
                    return deleteFile(deletePath);// 为文件时调用删除文件方法
                }
                else {
                    return deleteDirectory(deletePath);// 为目录时调用删除目录方法
                }
            }
        }
        else {
            System.out.println("要传入正确路径！");
            return false;
        }
    }

    public boolean deleteFile(String filePath) {// 删除单个文件
        flag = false;
        file = new File(filePath);
        if (file.isFile() && file.exists()) {// 路径为文件且不为空则进行删除
            file.delete();// 文件删除
            flag = true;
        }
        return flag;
    }

    public boolean deleteDirectory(String dirPath) {// 删除目录（文件夹）以及目录下的文件
        // 如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!dirPath.endsWith(File.separator)) {
            dirPath = dirPath + File.separator;
        }
        File dirFile = new File(dirPath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();// 获得传入路径下的所有文件
        for (int i = 0; i < files.length; i++) {// 循环遍历删除文件夹下的所有文件(包括子目录)
            if (files[i].isFile()) {// 删除子文件
                flag = deleteFile(files[i].getAbsolutePath());
                System.out.println(files[i].getAbsolutePath() + " 删除成功");
                if (!flag) {
                    break;// 如果删除失败，则跳出
                }
            }
            else {// 运用递归，删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) {
                    break;// 如果删除失败，则跳出
                }
            }
        }
        if (!flag) {
            return false;
        }
        if (dirFile.delete()) {// 删除当前目录
            return true;
        }
        else {
            return false;
        }
    }

    // 创建单个文件
    public boolean createFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {// 判断文件是否存在
            System.out.println("目标文件已存在" + filePath);
            return false;
        }
        if (filePath.endsWith(File.separator)) {// 判断文件是否为目录
            System.out.println("目标文件不能为目录！");
            return false;
        }
        if (!file.getParentFile().exists()) {// 判断目标文件所在的目录是否存在
            // 如果目标文件所在的文件夹不存在，则创建父文件夹
            System.out.println("目标文件所在目录不存在，准备创建它！");
            if (!file.getParentFile().mkdirs()) {// 判断创建目录是否成功
                System.out.println("创建目标文件所在的目录失败！");
                return false;
            }
        }
        try {
            if (file.createNewFile()) {// 创建目标文件
                System.out.println("创建文件成功:" + filePath);
                return true;
            }
            else {
                System.out.println("创建文件失败！");
                return false;
            }
        }
        catch (IOException e) {// 捕获异常
            e.printStackTrace();
            System.out.println("创建文件失败！" + e.getMessage());
            return false;
        }
    }

    // 创建目录
    public boolean createDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {// 判断目录是否存在
            System.out.println("创建目录失败，目标目录已存在！");
            return false;
        }
        if (!destDirName.endsWith(File.separator)) {// 结尾是否以"/"结束
            destDirName = destDirName + File.separator;
        }
        if (dir.mkdirs()) {// 创建目标目录
            System.out.println("创建目录成功！" + destDirName);
            return true;
        }
        else {
            System.out.println("创建目录失败！");
            return false;
        }
    }

    // 创建临时文件
    public String createTempFile(String prefix, String suffix, String dirName) {
        File tempFile = null;
        if (dirName == null) {// 目录如果为空
            try {
                tempFile = File.createTempFile(prefix, suffix);// 在默认文件夹下创建临时文件
                return tempFile.getCanonicalPath();// 返回临时文件的路径
            }
            catch (IOException e) {// 捕获异常
                e.printStackTrace();
                System.out.println("创建临时文件失败：" + e.getMessage());
                return null;
            }
        }
        else {
            // 指定目录存在
            File dir = new File(dirName);// 创建目录
            if (!dir.exists()) {
                // 如果目录不存在则创建目录
                if (createDir(dirName)) {
                    System.out.println("创建临时文件失败，不能创建临时文件所在的目录！");
                    return null;
                }
            }
            try {
                tempFile = File.createTempFile(prefix, suffix, dir);// 在指定目录下创建临时文件
                return tempFile.getCanonicalPath();// 返回临时文件的路径
            }
            catch (IOException e) {// 捕获异常
                e.printStackTrace();
                System.out.println("创建临时文件失败!" + e.getMessage());
                return null;
            }
        }
    }

    /**
     * 从包中复制文件到指定文件夹中
     * 
     * @param oldPath
     * @param newPath
     */
    public void copyFileInPackage(String oldPath, String newPath) {
        copyFile(oldPath, newPath, true);
    }

    /**
     * 外部文件夹文件复制
     * 
     * @param oldPath
     * @param newPath
     */
    public void copyFileNotInPackage(String oldPath, String newPath) {
        copyFile(oldPath, newPath, false);
    }

    /**
     * 复制文件到指定目录
     * 
     * @param oldPath
     * @param newPath
     * @param inPackage 是否从包中读取
     */
    public void copyFile(String oldPath, String newPath, boolean inPackage) {
        try {
            int bytesum = 0;
            int byteread = 0;
            InputStream is = null;
            if (!inPackage) {
                is = new FileInputStream(oldPath); // 读入原文件
            }
            else {
                // SystemTools.log("文件位置:--->" + oldPath);
                is = this.getClass().getResourceAsStream(oldPath);
            }
            // InputStream is = this.getClass().getResourceAsStream(oldPath);
            if (is != null) { // 文件存在时
                // InputStream inStream = new FileInputStream(oldPath); // 读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = is.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    fs.write(buffer, 0, byteread);
                }
                is.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();

        }

    }

    public List<File> filelist = new ArrayList<>();

    /**
     * 获取指定目录下的所有excel文件
     * 
     * @param strPath
     * @return
     */
    public List<File> getExcelFileList(String strPath) {
        return getFileList(strPath, ".xlsx", false);
    }

    /**
     * 获取指定文件夹下对应的所有文件列表
     * 
     * @param strPath 搜索路径
     * @param suffix 文件后缀
     * @param recursionSearch 是否需要递归搜索
     * @return
     */
    public List<File> getFileList(String strPath, String suffix, boolean recursionSearch) {
        if (strPath == null || strPath.equals("")) {
            return filelist;
        }
        if (filelist.size() > 0) {
            filelist.clear();
        }

        // 初始化文件列表
        exeGetFileList(strPath, suffix, recursionSearch);

        // 移除不需要的文件
        removeFiles(suffix);

        return filelist;
    }

    /**
     * 移除非指定后缀的文件
     * 
     * @param suffix 指定的文件后缀名称，如果不是该后缀，则对应的文件将被移除
     */
    private void removeFiles(String suffix) {
        List<File> removes = new ArrayList<>();
        for (File file : filelist) {
            // 移除临时文件
            if (file.getName().equals(".DS_Store")) {
                removes.add(file);
            }
            // 移除非指定后缀的文件
            else if (!file.getName().contains(suffix)) {
                removes.add(file);
            }
        }
        filelist.removeAll(removes);
    }

    /**
     * 执行文件查找
     * 
     * @param strPath
     * @param suffix
     * @param recursionSearch
     */
    public void exeGetFileList(String strPath, String suffix, boolean recursionSearch) {
        File dir = new File(strPath);
        File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory() && recursionSearch) { // 判断是文件还是文件夹
                    exeGetFileList(files[i].getAbsolutePath(), suffix, recursionSearch); // 获取文件绝对路径
                }
                else {
                    filelist.add(files[i]);
                }
            }

        }
    }
}
