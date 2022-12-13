package com.gousade.util;

/**
 * @author woxigousade
 * @date 2021/11/5
 */
public class ExcelUtils {
    public static String getExcelColumn(int columnIndex) {
        // 列索引从0开始
        if (columnIndex <= 0) {
            return "A";
        }
        StringBuilder columnStr = new StringBuilder();
        do {
        /* 索引从0开始，如果不减1，从倒数第二位向前都会递加一位
        如730，减一是ABC，不减是BCC */
            if (columnStr.length() > 0) {
                columnIndex--;
            }

            columnStr.insert(0, ((char) (columnIndex % 26 + (int) 'A')));
            columnIndex = (columnIndex - columnIndex % 26) / 26;
        } while (columnIndex > 0);
        return columnStr.toString();
    }

    public static void main(String[] args) {
        System.out.println(getExcelColumn(0));
        System.out.println(getExcelColumn(1));
    }
}
