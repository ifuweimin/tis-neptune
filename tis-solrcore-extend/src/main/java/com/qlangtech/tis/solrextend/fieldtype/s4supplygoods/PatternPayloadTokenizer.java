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
package com.qlangtech.tis.solrextend.fieldtype.s4supplygoods;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.util.AttributeFactory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.NumericUtils;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class PatternPayloadTokenizer extends Tokenizer {

    private int prefixTermLength;

    public PatternPayloadTokenizer(AttributeFactory factory) {
        super(factory);
    }

    private final CharTermAttribute termAtt = (CharTermAttribute) addAttribute(CharTermAttribute.class);

    private final PayloadAttribute payloadAtt = (PayloadAttribute) addAttribute(PayloadAttribute.class);

    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);

    public static final Pattern KV_PAIR_LIST_PATTERN = Pattern.compile("([^,]+?)_(20\\d{15})");

    private static final ThreadLocal<SimpleDateFormat> dateFormat_yyyyMMddHHmmssSSS = new ThreadLocal<SimpleDateFormat>() {

        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMddHHmmssSSS");
        }
    };

    private static final long DIFFER_START;

    static {
        Calendar start = Calendar.getInstance();
        start.set(2016, 5, 1, 0, 0, 0);
        DIFFER_START = start.getTime().getTime();
    }

    private List<PayLoadTerm> payloadTerms;

    private int termsIndex;

    private int termsLength;

    @Override
    public final boolean incrementToken() throws IOException {
        this.clearAttributes();
        PayLoadTerm t = null;
        try {
            if (payloadTerms == null) {
                payloadTerms = new ArrayList<PayLoadTerm>();
                Matcher kvPairMatcher = KV_PAIR_LIST_PATTERN.matcher(IOUtils.toString(this.input));
                int start = 0;
                long payload;
                int groupLength = 0;
                while (kvPairMatcher.find()) {
                    groupLength = kvPairMatcher.group(1).length();
                    payload = getTimePayload(kvPairMatcher);
                    BytesRefBuilder builder = new BytesRefBuilder();
                    // FIXME longToPrefixCoded
                    // NumericUtils.longToPrefixCoded(payload > 0 ? payload : 0, 0, builder);
                    t = new PayLoadTerm(kvPairMatcher.group(1), builder.get());
                    t.offsetStart = start;
                    t.offsetEnd = (start + groupLength - 1);
                    payloadTerms.add(t);
                    if (this.prefixTermLength > 0) {
                        t = new PayLoadTerm(StringUtils.left(kvPairMatcher.group(1), this.prefixTermLength), builder.get());
                        t.offsetStart = start;
                        t.offsetEnd = (start + this.prefixTermLength - 1);
                        payloadTerms.add(t);
                    }
                    start = start + kvPairMatcher.group(0).length() + 1;
                }
                termsIndex = 0;
                termsLength = payloadTerms.size();
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        if (termsIndex >= termsLength) {
            termsIndex = -1;
            payloadTerms = null;
            return false;
        }
        t = payloadTerms.get(termsIndex++);
        termAtt.setEmpty().append(t.key);
        offsetAtt.setOffset(correctOffset(t.offsetStart), correctOffset(t.offsetEnd));
        payloadAtt.setPayload(t.payload);
        return true;
    }

    public int getPrefixTermLength() {
        return prefixTermLength;
    }

    public void setPrefixTermLength(int prefixTermLength) {
        this.prefixTermLength = prefixTermLength;
    }

    private static final class PayLoadTerm {

        private final String key;

        private final BytesRef payload;

        private int offsetStart;

        private int offsetEnd;

        /**
         * @param key
         * @param payload
         */
        public PayLoadTerm(String key, BytesRef payload) {
            super();
            this.key = key;
            this.payload = payload;
        }
    }

    public static long getTimePayload(Matcher kvPairMatcher) throws ParseException {
        return dateFormat_yyyyMMddHHmmssSSS.get().parse(kvPairMatcher.group(2)).getTime() - DIFFER_START;
    }

    public static void main(String[] args) throws Exception {
        System.out.println((float) Math.log(8423085591l));
        System.out.println((float) Math.log(8423085592l));
    }
}
