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
package com.qlangtech.tis.solrj.extend;

import com.qlangtech.tis.solrj.extend.search4supplyUnionTabs.UnionTabBase;
import com.qlangtech.tis.solrj.io.stream.ExtendCloudSolrStream;
import com.qlangtech.tis.solrj.io.stream.ExtendUniqueStream;
import com.qlangtech.tis.solrj.io.stream.NotExistStream;
import com.qlangtech.tis.solrj.io.stream.expr.StreamFactoryWithClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.io.Tuple;
import org.apache.solr.client.solrj.io.stream.CloudSolrStream;
import org.apache.solr.client.solrj.io.stream.ComplementStream;
import org.apache.solr.client.solrj.io.stream.InnerJoinStream;
import org.apache.solr.client.solrj.io.stream.LeftOuterJoinStream;
import org.apache.solr.client.solrj.io.stream.RankStream;
import org.apache.solr.client.solrj.io.stream.ReducerStream;
import org.apache.solr.client.solrj.io.stream.TupleStream;
import org.apache.solr.client.solrj.io.stream.expr.StreamFactory;
import org.apache.solr.common.SolrException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/*
 * Created by Qinjiu(Qinjiu@2dfire.com) on 2/28/2017.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class InOptimizeClientAgent {

    private static final String DOCID = "docid";

    private static final String ROWS_UNLIMITED = "rows=unlimited";

    public static StreamFactory streamFactory = new StreamFactoryWithClient().withFunctionName("searchExtend", ExtendCloudSolrStream.class).withFunctionName("search", CloudSolrStream.class).withFunctionName("unique", ExtendUniqueStream.class).withFunctionName("top", RankStream.class).withFunctionName("group", ReducerStream.class).withFunctionName("innerJoin", InnerJoinStream.class).withFunctionName("leftOuterJoin", LeftOuterJoinStream.class).withFunctionName("complement", ComplementStream.class).withFunctionName("notExist", NotExistStream.class).withFunctionName("count", CountStream.class);

    private String query;

    private String[] fields;

    private String collection;

    private TisCloudSolrClient client;

    private String sharedKey;

    private String primaryField;

    private int rows;

    private Tuple last;

    public String getQuery() {
        return this.query;
    }

    public String[] getFields() {
        return this.fields;
    }

    public String getCollection() {
        return this.collection;
    }

    public String getSharedKey() {
        return this.sharedKey;
    }

    public String getPrimaryField() {
        return this.primaryField;
    }

    public int getRows() {
        return rows;
    }

    /**
     * @param zkHost
     */
    public InOptimizeClientAgent(String zkHost) {
        this(new TisCloudSolrClient(zkHost));
    }

    /**
     * @param client
     */
    public InOptimizeClientAgent(TisCloudSolrClient client) {
        this.client = client;
        streamFactory.withDefaultZkHost(client.getZkClient().getZkServerAddress());
        ((StreamFactoryWithClient) streamFactory).withExtendClient(client.getExtendSolrClient());
    }

    private static String collectionToString(Collection<String> collection) {
        if (collection == null || collection.size() == 0) {
            return null;
        } else {
            return org.apache.commons.lang.StringUtils.join(collection, ",");
        }
    }

    public static String topNFieldRowsUnlimited(String query) {
        return query.replaceFirst("topNField", "topNField " + ROWS_UNLIMITED);
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setFields(String... fields) {
        this.fields = fields;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public void setSharedKey(String sharedKey) {
        this.sharedKey = sharedKey;
    }

    public void setPrimaryField(String primaryField) {
        this.primaryField = primaryField;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public <T extends UnionTabBase> StreamResultList<T> query(Class<T> clazz) throws Exception {
        return this.query(clazz, new SortField[] {});
    }

    public <T extends UnionTabBase> StreamResultList<T> query(Class<T> clazz, SortField[] sortFields) throws Exception {
        if (query == null) {
            throw new SolrException(SolrException.ErrorCode.INVALID_STATE, "please set query");
        }
        if (sharedKey == null) {
            throw new SolrException(SolrException.ErrorCode.INVALID_STATE, "please set sharedKey");
        }
        if (collection == null) {
            throw new SolrException(SolrException.ErrorCode.INVALID_STATE, "please set collection");
        }
        if (fields == null) {
            throw new SolrException(SolrException.ErrorCode.INVALID_STATE, "please set fields");
        }
        if (primaryField == null) {
            throw new SolrException(SolrException.ErrorCode.INVALID_STATE, "please set primaryField");
        }
        if (rows == 0) {
            throw new SolrException(SolrException.ErrorCode.INVALID_STATE, "please set rows");
        }
        return reverseQuery(query, clazz, sortFields);
    }

    /**
     * 反查出来的id是supplyGoods的id
     *
     * @param query
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    private <T extends UnionTabBase> StreamResultList<T> reverseQuery(String query, Class<T> clazz, SortField[] sortFields) throws Exception {
        LinkedHashMap<String, Integer> idAndDocIds = new LinkedHashMap<>();
        streamQuery(query, idAndDocIds, clazz);
        if (idAndDocIds.size() < rows && !org.apache.commons.lang.StringUtils.contains(query, ROWS_UNLIMITED)) {
            // 查询到的记录数不够，需要补充查询
            query = topNFieldRowsUnlimited(query);
            streamQuery(query, idAndDocIds, clazz);
        }
        SolrQuery reverseQuery = new SolrQuery();
        reverseQuery.setFields(fields);
        String reverseQueryString = String.format("{!terms f=%s}%s", primaryField, collectionToString(idAndDocIds.keySet()));
        // System.out.println(reverseQueryString);
        reverseQuery.setQuery(reverseQueryString);
        reverseQuery.setRows(rows);
        for (SortField sort : sortFields) {
            reverseQuery.addSort(sort.field, sort.order);
        }
        TisCloudSolrClient.SimpleQueryResult<T> response = this.client.query(collection, sharedKey, reverseQuery, clazz);
        Map<String, T> resultMap = new HashMap<>();
        for (T t : response.getResult()) {
            String id = t.getId();
            if (id == null) {
                throw new IllegalStateException("id can not be null");
            }
            resultMap.put(id, t);
            t.setDocId(idAndDocIds.get(id));
        }
        StreamResultList<T> resultList = new StreamResultList<>();
        idAndDocIds.keySet().stream().map(resultMap::get).forEach(resultList::add);
        resultList.setLastTuple(last);
        return resultList;
    }

    public static class SortField {

        public final String field;

        public final SolrQuery.ORDER order;

        public SortField(String field, ORDER order) {
            super();
            this.field = field;
            this.order = order;
        }
    }

    private <T extends UnionTabBase> void streamQuery(String query, Map<String, Integer> idAndDocIds, Class<T> clazz) throws Exception {
        idAndDocIds.clear();
        TupleStream joinStream = streamFactory.constructStream(query);
        try {
            joinStream.open();
            while (true) {
                Tuple tuple = joinStream.read();
                if (tuple.EOF) {
                    break;
                } else {
                    idAndDocIds.put(tuple.getString(primaryField), Integer.parseInt(tuple.getString(DOCID)));
                    last = tuple;
                }
            }
        } finally {
            try {
                joinStream.close();
            } catch (Throwable e) {
            }
        }
    }

    public long getCountStreamValue(String query) throws IOException {
        TupleStream stream = streamFactory.constructStream(query);
        long count = 0L;
        stream.open();
        while (true) {
            Tuple tuple = stream.read();
            if (tuple.EOF) {
                break;
            } else {
                if (count != 0L) {
                    throw new IOException(String.format("there should be only one effective tuple in this stream, tuple %s is " + "invalid", tuple));
                }
                count = tuple.getLong("count");
            }
        }
        return count;
    }

    public TupleStream query(String query) throws IOException {
        return streamFactory.constructStream(query);
    }

    @SuppressWarnings("all")
    public static class StreamResultList<T> extends LinkedList<T> {

        private Tuple lastTuple;

        public Tuple getLastTuple() {
            return lastTuple;
        }

        @Override
        public boolean add(T e) {
            if (e == null) {
                return false;
            }
            return super.add(e);
        }

        public void setLastTuple(Tuple lastTuple) {
            this.lastTuple = lastTuple;
        }

        // 这里的lastTuple保存的是join之后的最后一个元组，用来获取翻页的字段
        public String getField(String key) {
            if (lastTuple == null) {
                return null;
            } else {
                return lastTuple.getString(key);
            }
        }

        public Long getSortFieldMinusOne(String key) {
            try {
                return Long.parseLong(getField(key)) - 1;
            } catch (NumberFormatException e) {
                return null;
            }
        }

        public Long getSortFieldPlusOne(String key) {
            try {
                return Long.parseLong(getField(key)) + 1;
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }
}
