package com.gousade.util;

import cn.hutool.core.io.file.LineSeparator;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.List;

/**
 * @author woxigousade
 * @date 2020/6/15
 */
@Slf4j
public class FileUtils {
    public static final String USER_HOME = "user.home";
    public static final String USER_HOME_DIRECTORY = System.getProperty(USER_HOME);
    public static final String USER_DIR = "user.dir";
    public static final String USER_DIR_DIRECTORY = System.getProperty(USER_DIR);
    public static final String CATALINA_HOME = "catalina.home";
    public static final String ROOT_DIRECTORY = System.getProperty(CATALINA_HOME);
    public static final String FILES = "files";
    public static final String FILE_DIRECTORY = ROOT_DIRECTORY + File.separator + FILES;
    public static final String VMS_PROGRAM_HOME = "vmsProgram";
    public static final String VMS_PROGRAM_DIRECTORY = ROOT_DIRECTORY + File.separator + FILES
            + File.separator + VMS_PROGRAM_HOME;

    /**
     * 递归删除文件及文件夹
     *
     * @param file            file needed to be deleted
     * @param deletedFileName name of deleted file
     */
    public static void deleteFilesRecursively(File file, String deletedFileName) {
        if (!file.exists()) {
            return;
        }
        File[] fileArray = file.listFiles();
        if (fileArray != null) {
            for (File subFile : fileArray) {
                if (file.isDirectory()) {
                    deleteFilesRecursively(subFile, deletedFileName);
                } else {
                    boolean isDeleted = subFile.delete();
                    if (isDeleted) {
                        log.info("删除文件->{}, 路径为: {}", deletedFileName, subFile.getAbsolutePath());
                    }
                }
            }
        }
        boolean isDeleted = file.delete();
        if (isDeleted) {
            log.info("删除文件->{}, 路径为: {}", deletedFileName, file.getAbsolutePath());
        }
    }

    /**
     * @param url url
     * @return file from url
     * @throws IOException if an I/O error occurs.
     */
    public static File getFileByUrl(String url) throws IOException {
        File file;
        URL urlFile;
        InputStream inStream = null;
        OutputStream os = null;
        try {
            file = File.createTempFile("url", ".png");
            urlFile = new URL(url);
            inStream = urlFile.openStream();
            os = new FileOutputStream(file);
            int bytesRead;
            byte[] buffer = new byte[8192];
            while ((bytesRead = inStream.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } finally {
            if (null != os) {
                os.close();
            }
            if (null != inStream) {
                inStream.close();
            }
        }
        return file;
    }

    public static void saveFileFromInputStream(InputStream stream, String path, String fileName) throws IOException {
        FileOutputStream fs = new FileOutputStream(path + File.separator + fileName);
        byte[] buffer = new byte[1024 * 1024];
        int byteread;
        while ((byteread = stream.read(buffer)) != -1) {
            fs.write(buffer, 0, byteread);
            fs.flush();
        }
        fs.close();
        stream.close();
    }

    public static byte[] readFile(String fileName, String directory) {
        String fileWholePath;
        if (directory == null) {
            fileWholePath = fileName;
        } else {
            fileWholePath = directory + File.separator + fileName;
        }
        return readFile(fileWholePath);
    }

    public static byte[] readFile(String filePath) {
        byte[] data;
        File file = new File(filePath);
        try (FileInputStream input = new FileInputStream(file);
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] bytes = new byte[1024];
            int length;
            while ((length = input.read(bytes)) != -1) {
                output.write(bytes, 0, length);
                output.flush();
            }
            data = output.toByteArray();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("没有找到此文件：" + filePath);
        } catch (IOException e) {
            throw new RuntimeException("读取文件流失败：FileUtil.read()");
        }
        return data;
    }

    public static byte[] readFile(InputStream input) {
        byte[] data;
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] bytes = new byte[1024];
            int length;
            while ((length = input.read(bytes)) != -1) {
                output.write(bytes, 0, length);
                output.flush();
            }
            data = output.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("读取文件流失败。");
        }
        return data;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static byte[] write2File(byte[] data, String directory, String fileName) {
        String filePath = directory + File.separator + fileName;
        File pf = new File(directory);
        if (!pf.exists()) {
            pf.mkdirs();
        }
        File file = new File(filePath);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
            fos.flush();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("没有找到此文件：" + filePath);
        } catch (IOException e) {
            throw new RuntimeException("读取文件流失败：FileUtil.read()");
        }
        return data;
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    public static void write2File(InputStream inputStream, String directory, String fileName) {
        File pf = new File(directory);
        if (!pf.exists()) {
            pf.mkdirs();
        }
        try {
            try (OutputStream outputStream = Files.newOutputStream(Paths.get(directory + File.separator + fileName))) {
                ResponseUtils.writeStream(inputStream, outputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    public static void write2File(List<String> list, String directory, String fileName) {
        File parentFile = new File(directory);
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        try (FileWriter writer = new FileWriter(directory + File.separator + fileName)) {
            list.forEach(str -> {
                try {
                    writer.write(str + LineSeparator.LINUX.getValue());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void saveVMSProgram(String programDictionary, String fileName, String programContent) {
        String dictionary = VMS_PROGRAM_DIRECTORY + File.separator + programDictionary;
        File file = new File(dictionary);
        if (!file.exists()) {
            file.mkdirs();
        }
        try (FileWriter fileWriter = new FileWriter(dictionary + File.separator + fileName)) {
            fileWriter.write(programContent + "\r\n");
            fileWriter.flush();
            log.warn(dictionary + File.separator + fileName + "保存成功。");
        } catch (IOException e) {
            log.error("节目文件保存异常。", e);
            throw new RuntimeException("保存节目内容文件" + dictionary + File.separator + fileName + "失败。");
        }
    }

    public static String formatFileSize(long fileSize) {
        DecimalFormat df = new DecimalFormat("#.00");
        String result;
        if (fileSize < 1024) {
            result = df.format((double) fileSize) + "B";
        } else if (fileSize < 1048576) {
            result = df.format((double) fileSize / 1024) + "KB";
        } else if (fileSize < 1073741824) {
            result = df.format((double) fileSize / 1048576) + "MB";
        } else {
            result = df.format((double) fileSize / 1073741824) + "GB";
        }
        return result;
    }
}
