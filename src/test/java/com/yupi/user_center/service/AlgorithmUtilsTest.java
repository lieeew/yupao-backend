package com.yupi.user_center.service;

import com.beust.ah.A;
import com.yupi.user_center.utils.AlgorithmUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

/**
 * @author leikooo
 * @create 2023-09-16 12:33
 * @Package com.yupi.user_center.service
 * @Description
 */
@SpringBootTest
public class AlgorithmUtilsTest {
    @Test
    void AlgorithmTest() {
        String str1 = "leikooo不是leikooo";
        String str2 = "leikooo very leikooo";
        String str3 = "lieeew very nice";
        int i1 = AlgorithmUtils.minDistance(str1, str2);
        int i2 = AlgorithmUtils.minDistance(str2, str3);

        System.out.println("i1 = " + i1);
        System.out.println("i2 = " + i2);
    }

    @Test
    void AlgorithmTestTags() {
        List<String> str1 = Arrays.asList("Java", "Python", "男");
        List<String> str2 = Arrays.asList("CPP", "Python", "男");
        List<String> str3 = Arrays.asList("Java", "JavaScrip", "女");
        int i1 = AlgorithmUtils.minDistance(str1, str2);
        int i2 = AlgorithmUtils.minDistance(str2, str3);

        System.out.println("i1 = " + i1);
        System.out.println("i2 = " + i2);
    }


}
