package gemu.rest.resource;

import gemu.rest.annotation.MyUrl;

import java.io.*;
import java.net.URL;

/**
 * 医学类资源
 * Created by gemu on 3/19/17.
 */
@MyUrl("/medicine")
public class MedicalScienceResource {

    public static void main(String[] args) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        BufferedWriter bw = null;
        BufferedReader br = null;
        URL generate = classLoader.getResource("generate_tmp/bcgm_g"); // 解析文献后生成文件
        URL literature = classLoader.getResource("literature_tmp/bcgm"); // 文献
        try {
            bw = new BufferedWriter(new FileWriter(generate.getPath()));
            br = new BufferedReader(new FileReader(literature.getPath()));
            String line = "";
            String menu = "";
            while ((line = br.readLine()) != null) {
                if (line.startsWith("<目录>")) {
                    String menuName = line.substring(4);
                    if (!menu.equals(menuName)) {
                        bw.write(line);
                        bw.newLine();
                        bw.flush();
                        menu = line.substring(4);
                    }
                }
                if (line.startsWith("<篇名>")) {
                    bw.write(line);
                    bw.newLine();
                    bw.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bw.close();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
