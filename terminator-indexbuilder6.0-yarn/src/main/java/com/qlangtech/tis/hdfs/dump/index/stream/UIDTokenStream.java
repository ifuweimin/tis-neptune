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
package com.qlangtech.tis.hdfs.dump.index.stream;

import java.io.IOException;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.util.BytesRef;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class UIDTokenStream extends TokenStream {

    private boolean returnToken = false;

    private PayloadAttribute payloadAttr;

    private CharTermAttribute termAttr;

    public UIDTokenStream(long uid) {
        byte[] buffer = new byte[8];
        buffer[0] = (byte) (uid);
        buffer[1] = (byte) (uid >> 8);
        buffer[2] = (byte) (uid >> 16);
        buffer[3] = (byte) (uid >> 24);
        buffer[4] = (byte) (uid >> 32);
        buffer[5] = (byte) (uid >> 40);
        buffer[6] = (byte) (uid >> 48);
        buffer[7] = (byte) (uid >> 56);
        payloadAttr = (PayloadAttribute) addAttribute(PayloadAttribute.class);
        payloadAttr.setPayload(new BytesRef(buffer));
        termAttr = (CharTermAttribute) addAttribute(CharTermAttribute.class);
        // Constants.TERM_VALUE
        // termAttr.setTermBuffer("");
        termAttr.append("_UID_");
        returnToken = true;
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (returnToken) {
            returnToken = false;
            return true;
        } else {
            return false;
        }
    }
}
