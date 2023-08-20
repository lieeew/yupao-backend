package com.yupi.user_center.once.importuser;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class XingQiuUserInfo {

    /**
     * 用名字去匹配，这里需要注意，如果名字重复，会导致只有一个字段读取到数据
     */
    @ExcelProperty("院校代码")
    private String collageCode;

    @ExcelProperty("院校国标码")
    private String countryCode;

    @ExcelProperty("院校名称")
    private String collageName;

    @ExcelProperty("专业类别")
    private String professionalCategory;

    @ExcelProperty("备注")
    private String remark;

}