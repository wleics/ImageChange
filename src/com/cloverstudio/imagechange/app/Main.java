
package com.cloverstudio.imagechange.app;

import com.cloverstudio.imagechange.app.utils.CommonFileUtil;

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

    public static void main(String[] args) {
        Main main = new Main();
        main.init();
    }

    /*
     * 初始化
     */
    private void init() {

        // 获取jar所在文件路径
        String path = getJarPath();

        Frame root = new Frame("图片转换");
        root.setSize(400, 200);
        root.setLocation(300, 200);
        root.setLayout(null);
        root.addWindowListener(new MyWin());
        root.setVisible(true);
        // 添加按钮
        Button button = new Button("PNG转JPG");
        button.setBounds(10, 50, 380, 30);
        root.add(button);
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // 获取桌面上所有的png文件
                List<File> files = CommonFileUtil.get().getFileList(path, SUFFIX_PNG, false);
                System.out.println("共获取到文件：" + files.size() + "个");
                for (File file : files) {
                    changePng2Jpg(file);
                }
                mLabel.setText("转换完成！");
            }

            /**
             * 将png图片转换成jpg
             * 
             * @param file
             */
            private void changePng2Jpg(File file) {
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
                    newBufferedImage
                            .createGraphics()
                            .drawImage(bufferedImage, 0, 0, Color.WHITE, null);
                    ImageIO.write(
                            newBufferedImage,
                                FORMATNAME_JPG,
                                new File(path + File.separator + fileName + SUFFIX_JPG));
                    System.out.println("转换完成");
                }
                catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        });

        Label infoLabel = new Label("解析路径：" + path, Label.CENTER);
        infoLabel.setBounds(0, 90, 400, 30);
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setBackground(new Color(22, 160, 93));
        infoLabel.setFont(new java.awt.Font("MS Song", 1, 9));
        root.add(infoLabel);
        mLabel = infoLabel;
    }

    class MyWin extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }

        @Override
        public void windowActivated(WindowEvent e) {
        }

        @Override
        public void windowOpened(WindowEvent e) {
        }

    }

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
}
