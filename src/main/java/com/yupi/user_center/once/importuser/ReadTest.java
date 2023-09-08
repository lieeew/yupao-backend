package com.yupi.user_center.once.importuser;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 读的常见写法
 *
 * @author Jiaju Zhuang
 */

@Slf4j
public class ReadTest {

    /**
     * 同步的返回，不推荐使用，如果数据量大会把数据放到内存里面, 一次要获得完整的数据
     */
//    @Test
    public void synchronousRead() {
        String fileName = "E:\\yupao\\yupao-backend\\target\\demo.xls";

        // 这里 也可以不指定class，返回一个list，然后读取第一个sheet 同步读取会自动finish
        List<XingQiuUserInfo> listMap = EasyExcel.read(fileName).head(XingQiuUserInfo.class).sheet().doReadSync();
//        System.out.println("listMap = " + listMap);
        Map<String, List<XingQiuUserInfo>> collect =
                listMap.stream()
                        .filter(list -> StringUtils.isNotEmpty(list.getCollageName()))
                        .collect(Collectors.groupingBy(XingQiuUserInfo::getCollageName));
        System.out.println("重复的昵称：" + collect.keySet().size());
        for (Map.Entry<String, List<XingQiuUserInfo>> entry : collect.entrySet()) {
            // 这里就找孩子大于 1 的值
            if (entry.getValue().size() > 1) {
                System.out.println(entry.getKey());
            }
        }
    }

    /**
     * 指定列的下标或者列名
     *
     * <p>1. 创建excel对应的实体对象,并使用{@link ExcelProperty}注解.
     * <p>2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器
     * <p>3. 直接读即可
     */
//    @Test
    public void indexOrNameRead() {
        String fileName = "E:\\yupao\\yupao-backend\\target\\demo.xls";
        // 这里默认读取第一个sheet
        EasyExcel.read(fileName, XingQiuUserInfo.class, new XingQiuDataListener()).sheet().doRead();

    }
}