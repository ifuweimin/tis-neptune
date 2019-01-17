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
package com.qlangtech.tis.solrextend.handler.component.s4TimeStatistic;

import com.qlangtech.tis.solrextend.handler.component.TisMoney;

/*
 * Created by lingxiao on 2016/7/19.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TimeIntervalStatisSummary {

    public String hour;

    public int openOrderCount;

    public int endOrderCount;

    public int openOrderPeople;

    public int endOrderPeople;

    public TisMoney recieveAmount = TisMoney.create("0");

    public TisMoney discountAmount = TisMoney.create("0");

    public TisMoney profit = TisMoney.create("0");

    public TisMoney invoice = TisMoney.create("0");

    public TimeIntervalStatisSummary() {
    }

    public TimeIntervalStatisSummary(String hour) {
        this.hour = hour;
    }

    public void addIncrease(TimeIntervalStatisDocValues val) {
        this.openOrderCount += val.getOpenOrderCount();
        this.endOrderCount += val.getEndOrderCount();
        this.openOrderPeople += val.getOpenOrderPeople();
        this.endOrderPeople += val.getEndOrderPeople();
        this.recieveAmount.add(val.getRecieveAmout());
        this.discountAmount.add(val.getDiscountAmout());
        this.profit.add(val.getProfit());
        this.invoice.add(val.getInvoice());
    }
}
