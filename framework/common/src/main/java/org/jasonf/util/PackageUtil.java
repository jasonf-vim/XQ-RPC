package org.jasonf.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author jasonf
 * @Date 2023/11/12
 * @Description 包扫描
 */

@Slf4j
public class PackageUtil {
    private static final String SUFFIX = ".class";

    /**
     * 扫包
     *
     * @param packageName 包名
     * @return 包内所有 class文件 的全限定名
     */
    public static List<String> traversal(String packageName) {
        List<String> classNames = new ArrayList<>();
        URL url = ClassLoader.getSystemResource(packageName.replaceAll("\\.", "/"));
        if (url == null) {
            log.error("资源 [{}] 不存在", packageName);
            return classNames;
        }
        File file = new File(url.getPath());
        recursive(file, classNames, file.getPath().length() - packageName.length());
        return classNames;
    }

    /**
     * 递归处理 file(java), 转换成类的全限定名
     *
     * @param file file实例
     * @param list 返回结果
     * @param base classpath 的长度
     */
    private static void recursive(File file, List<String> list, int base) {
        if (file.isFile()) {
            String path = file.getPath();
            if (!path.endsWith(SUFFIX)) return;
            list.add(path.substring(0, path.lastIndexOf(SUFFIX))
                    .substring(base).replaceAll("\\\\", "."));
        } else {
            File[] children = file.listFiles();
            if (children == null || children.length == 0) return;
            for (File child : children) recursive(child, list, base);
        }
    }
}
