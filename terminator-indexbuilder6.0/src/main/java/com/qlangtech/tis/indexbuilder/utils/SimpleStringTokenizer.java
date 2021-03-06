/* 
 * The MIT License
 *
 * Copyright (c) 2018-2022, qinglangtech Ltd
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.qlangtech.tis.indexbuilder.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class SimpleStringTokenizer {

    private static final Log log = LogFactory.getLog(SimpleStringTokenizer.class);

    StringTokenizer st = null;

    String delim;

    int length = -1;

    /**
     * @param str
     * 		要切分的内容
     * @param delim
     * 		分隔符
     */
    public SimpleStringTokenizer(String str, String delim) {
        st = new StringTokenizer(str, delim, true);
        this.delim = delim;
    }

    /**
     * @param str
     * 		要切分的内容
     * @param delim
     * 		分隔符
     * @param length
     * 		默认分隔结果的个数,如果实际分割结果小于该长度,后面的都会被填成空
     */
    public SimpleStringTokenizer(String str, String delim, int length) {
        st = new StringTokenizer(str, delim, true);
        this.delim = delim;
        this.length = length;
    }

    /**
     * 原来为空的字段都填上空字符,然后存放在list里面返回
     * @return
     */
    public static List<String> getAllElements(String src, String delimiter) {
        List<String> values = new ArrayList<String>();
        boolean dataflag = false;
        StringTokenizer st = new StringTokenizer(src, delimiter, true);
        while (st.hasMoreTokens()) {
            String word = st.nextToken();
            if (!delimiter.equals(word)) {
                values.add(word);
                dataflag = true;
            } else {
                if (dataflag)
                    dataflag = false;
                else {
                    values.add("");
                }
            }
        }
        if (!dataflag) {
            values.add("");
        }
        return values;
    }

    /**
     * 原来为空的字段都填上空字符,然后存放数组里面返回
     * @return
     */
    public static String[] split(String src, String delimiter) {
        // log.warn("----->"+src);
        List<String> values = new ArrayList<String>();
        boolean dataflag = false;
        StringTokenizer st = new StringTokenizer(src, delimiter, true);
        while (st.hasMoreTokens()) {
            String word = st.nextToken();
            // System.out.println("token="+word);
            if (!delimiter.equals(word)) {
                values.add(word);
                dataflag = true;
            } else {
                if (dataflag)
                    dataflag = false;
                else {
                    values.add("");
                }
            }
        }
        if (!dataflag) {
            values.add("");
        }
        /*if(length >= 1 && values.size() < length){
			for(int i=0; i<length-values.size(); i++)
				values.add("");
		}*/
        String[] a = new String[values.size()];
        return values.toArray(a);
    }

    public static void main(String[] args) throws IOException {
        /*File file = new File("d:/part-00008--2-5");
		FileInputStream fi = new FileInputStream(file);
		//BufferedReader br = new BufferedReader(new InputStreamReader(fi,"UTF-8"));
		BufferedReader br = new BufferedReader(new InputStreamReader(fi,"UTF-8"));
		String line = "";
		while((line = br.readLine())!=null)
		{
			String a[]=line.split("\t");//SimpleStringTokenizer.split(src, "\t");
			System.out.println(a.length);
			if(a.length!=9)
			{
				System.out.println(a.length);
				for(int i=0;i<a.length;i++)
				{
					System.out.println(i+"--"+a[i]+"--");
				}
			}
		}
*/
        String line = "69969026487148633412450621789469905544901019320140623卧室 现代 大户型 木纹砖0";
        String[] lines = split(line, "\001");
        System.out.println(lines.length);
        for (int i = 0; i < lines.length; i++) {
            System.out.println(lines[i]);
        }
    }
}
