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
package com.qlangtech.tis.solrextend.handler.component.search4OperationStatistic.returnmenu;

import com.qlangtech.tis.solrextend.handler.component.search4OperationStatistic.CommonFieldColumn;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.search.DocListAndSet;
import java.io.IOException;

/*
 * Created by lingxiao on 2016/7/27.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class ReturnMenuComponent extends SearchComponent {

    public static String COMPONENT_NAME = "returnmenu_component";

    @Override
    public void prepare(ResponseBuilder responseBuilder) throws IOException {
        if (responseBuilder.req.getParams().getBool(COMPONENT_NAME, false)) {
            responseBuilder.setNeedDocSet(true);
        }
    }

    @Override
    public void process(ResponseBuilder responseBuilder) throws IOException {
        if (!responseBuilder.req.getParams().getBool(COMPONENT_NAME, false)) {
            return;
        }
        IndexSchema schema = responseBuilder.req.getSchema();
        ReturnMenuDocValues docValues = new ReturnMenuDocValues();
        docValues.rm_menu_id = new CommonFieldColumn(schema.getField("rm_menu_id"));
        docValues.rm_menu_name = new CommonFieldColumn(schema.getField("rm_menu_name"));
        docValues.rm_kind_id = new CommonFieldColumn(schema.getField("rm_kind_id"));
        docValues.rm_kind_name = new CommonFieldColumn(schema.getField("rm_kind_name"));
        docValues.rm_unit = new CommonFieldColumn(schema.getField("rm_unit"));
        docValues.rm_price = new CommonFieldColumn(schema.getField("rm_price"));
        docValues.rm_num = new CommonFieldColumn(schema.getField("rm_num"));
        docValues.rm_fee = new CommonFieldColumn(schema.getField("rm_fee"));
        DocListAndSet results = responseBuilder.getResults();
        ReturnMenuCollector collector = new ReturnMenuCollector(docValues);
        responseBuilder.req.getSearcher().search(results.docSet.getTopFilter(), collector);
        responseBuilder.rsp.add("returnmenu_component", collector.write(collector.rmSummaryMap));
    }

    @Override
    public String getDescription() {
        return COMPONENT_NAME;
    }
}
