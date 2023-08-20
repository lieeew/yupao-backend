package com.yupi.user_center.once.importuser;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.format.NumberFormat;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.converters.DefaultConverterLoader;
import com.alibaba.excel.enums.CellExtraTypeEnum;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.util.ListUtils;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

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
    @Test
    public void synchronousRead() {
        String fileName = "E:\\yupao\\yupao-backend\\target\\demo.xls";

        // 这里 也可以不指定class，返回一个list，然后读取第一个sheet 同步读取会自动finish
        List<XingQiuUserInfo> listMap = EasyExcel.read(fileName).sheet().doReadSync();
        log.info("读取到数据:{}", listMap + "\n");
    }

    /**
     * 指定列的下标或者列名
     *
     * <p>1. 创建excel对应的实体对象,并使用{@link ExcelProperty}注解.
     * <p>2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器
     * <p>3. 直接读即可
     */
    @Test
    public void indexOrNameRead() {
        String fileName = "E:\\yupao\\yupao-backend\\target\\demo.xls";
        // 这里默认读取第一个sheet
        EasyExcel.read(fileName, XingQiuUserInfo.class, new XingQiuDataListener()).sheet().doRead();
    }
}