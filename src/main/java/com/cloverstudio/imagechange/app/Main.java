
package com.cloverstudio.imagechange.app;

import com.cloverstudio.imagechange.app.utils.CommonFileUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

/**
 * 图片类型转换
 * 
 * @author wlei 2018-06-21
 */
public class Main {

    // png文件后缀
    private static final String SUFFIX_PNG = ".png";

    // jpg文件后缀
    private static final String SUFFIX_JPG = ".jpg";

    // 文件格式名称
    private static final String FORMATNAME_JPG = "jpg";

    // 标题标签
    private Label mLabel;

    // 获取jar所在文件路径
    private String mPath = "";

    // 按钮宽度
    private static final int BTN_WIDTH = 380;

    // 按钮高度
    private static final int BTN_HEIGHT = 30;

    // 位置
    private static int Y = 50;

    private static final int MAGIN_TOP = 10;

    // 需要被删除的文件后缀
    private static final String[] DEL_FILE_SUFFIX = {
            ".jpg", ".png", ".mp4"
    };

    ExecutorService executorService = Executors.newFixedThreadPool(1);

    public static void main(String[] args) {
        Main main = new Main();
        main.init();
    }

    /*
     * 初始化
     */
    private void init() {
        EventBus.getDefault().register(this);
        mPath = getJarPath();

        Frame root = addRootView();
        // 添加按钮
        addPng2JpgButton(root);
        // 添加删除文件的按钮
        addDelFileButton(root);
        // 添加提示label
        addProgressLabel(root);

        root.setLocation(300, Y);
        root.setVisible(true);
    }

    /**
     * 添加根视图
     * 
     * @return
     */
    private Frame addRootView() {
        Frame root = new Frame("图片转换");
        root.setSize(400, 200);
        root.setLocation(300, 200);
        root.setLayout(null);
        root.addWindowListener(new MyWin());
        return root;
    }

    /**
     * 添加进度标签
     * 
     * @param root
     */
    private void addProgressLabel(Frame root) {
        Label infoLabel = new Label("解析路径：" + mPath, Label.CENTER);
        infoLabel.setBounds(0, Y, 400, 30);
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setBackground(new Color(22, 160, 93));
        infoLabel.setFont(new java.awt.Font("MS Song", 1, 9));
        root.add(infoLabel);
        mLabel = infoLabel;
        Y += BTN_HEIGHT + MAGIN_TOP;
    }

    /**
     * 添加png转jpg图片的按钮
     * 
     * @param root
     */
    private void addPng2JpgButton(Frame root) {
        Button button = new Button("PNG转JPG");
        button.setBounds(10, Y, BTN_WIDTH, BTN_HEIGHT);
        root.add(button);
        button.addActionListener(png2jpgActionListener);
        Y += BTN_HEIGHT + MAGIN_TOP;
    }

    /**
     * 添加删除文件按钮
     * 
     * @param root
     */
    private void addDelFileButton(Frame root) {
        Button button = new Button("删除文件（不包含文件夹内容）");
        button.setBounds(10, Y, BTN_WIDTH, BTN_HEIGHT);
        root.add(button);
        button.addActionListener(delFileActionListener());
        Y += BTN_HEIGHT + MAGIN_TOP;
    }

    /**
     * 删除文件的监听
     * 
     * @return
     */
    private ActionListener delFileActionListener() {
        return new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List<File> files = CommonFileUtil.get().getFileList(mPath, "", false);
                mLabel.setText("共获取到文件：" + files.size() + "个");
                Callable<String> task = new Callable<String>() {

                    @Override
                    public String call() throws Exception {
                        int count = 0;
                        for (File file : files) {
                            for (String suffix : DEL_FILE_SUFFIX) {
                                if (file.getName().contains(suffix)) {
                                    boolean delete = file.delete();
                                    if (delete) {
                                        count++;
                                    }
                                }
                            }
                        }
                        MessageEvent event = new MessageEvent();
                        event.msg = "删除完成！共删除文件：" + count + "个";
                        EventBus.getDefault().post(event);
                        return "complete";
                    }
                };
                executorService.submit(task);
            }
        };
    }

    /**
     * png图片转jpg图片按钮监听
     */
    ActionListener png2jpgActionListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {

            // 获取桌面上所有的png文件
            List<File> files = CommonFileUtil.get().getFileList(mPath, SUFFIX_PNG, false);
            mLabel.setText("共获取到文件：" + files.size() + "个");

            Callable<String> task = new Callable<String>() {

                @Override
                public String call() throws Exception {
                    for (File file : files) {
                        changePng2Jpg(file, mPath);
                    }
                    MessageEvent event = new MessageEvent();
                    event.msg = "转换完成！";
                    EventBus.getDefault().post(event);
                    return "complete";
                }
            };
            executorService.submit(task);
        }
    };

    /**
     * 将png图片转换成jpg
     * 
     * @param file
     */
    private void changePng2Jpg(File file, String path) {
        BufferedImage bufferedImage;
        try {
            String fileName = file.getName();
            if (fileName.contains(SUFFIX_PNG)) {
                fileName = fileName.split(SUFFIX_PNG)[0];
            }
            bufferedImage = ImageIO.read(file);
            BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(),
                    bufferedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
            // 创建一个RBG图像，24位深度，成功将32位图转化成24位
            newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);
            ImageIO.write(
                    newBufferedImage,
                        FORMATNAME_JPG,
                        new File(path + File.separator + fileName + SUFFIX_JPG));
        }
        catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    class MyWin extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {
            EventBus.getDefault().unregister(this);
            System.exit(0);
        }

        @Override
        public void windowActivated(WindowEvent e) {
        }

        @Override
        public void windowOpened(WindowEvent e) {
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        mLabel.setText(event.msg);
    };

    /**
     * 获取jar包所在的路径
     * 
     * @return
     */
    private String getJarPath() {
        String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            // 解决获取中文目录的乱码问题
            path = java.net.URLDecoder.decode(path, "utf-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int firstIndex = path.lastIndexOf(System.getProperty("path.separator")) + 1;
        int lastIndex = path.lastIndexOf(File.separator) + 1;
        // jar包所在目录
        path = path.substring(firstIndex, lastIndex);
        return path;
    }

    public static class MessageEvent {
        String msg = "";
    }
}
