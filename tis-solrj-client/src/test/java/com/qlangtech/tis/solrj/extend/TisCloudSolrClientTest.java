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

import junit.framework.TestCase;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.client.solrj.io.Tuple;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.CursorMarkParams;
import java.util.LinkedList;
import java.util.List;

/*
 * Created by Qinjiu on 4/18/2017.
 *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class TisCloudSolrClientTest extends TestCase {

    static final String zkHost = "10.1.6.65:2181,10.1.6.67:2181,10.1.6.80:2181/tis/cloud";

    static final TisCloudSolrClient client;

    static {
        client = new TisCloudSolrClient(zkHost);
    }

    public void testGetCount() throws Exception {
        SolrQuery query = new SolrQuery("entity_id:99927119");
        QueryResponse response = client.query("search4supplyUnionTabs", "99927119", query);
        System.out.println(response.getResults().getNumFound());
    }

    public void testStreamGet() throws Exception {
        SolrQuery query = new SolrQuery("authorizer_app_id:wxa52bcbabd9527822 AND months:4");
        // query.setRows(20);
        query.setFields("customer_register_id", "third_open_id");
        query.setSort("customer_register_id", SolrQuery.ORDER.asc);
        client.queryAndStreamResponse("search4personasMember", query, "wxa52bcbabd9527822", new TisCloudSolrClient.ResponseCallback<PersonasMember>() {

            @Override
            public void process(PersonasMember pojo) {
                // 流式 处理其中的每一个结果对象
                System.out.println(pojo.getCustomerRegisterId() + " " + pojo.getThirdOpenId() + " " + pojo.getAuthorizerAppId());
            }

            @Override
            public void lististInfo(long numFound, long start) {
            }
        }, PersonasMember.class);
        int i = 1;
    }

    // Query and stream response. 在回调方法的process方法中对每个对象进行处理
    public void testStreamFactory() throws Exception {
        // 第一个route值 填写对应的app_id
        String query = "searchExtend(search4personasMember, qt=/export, _route_=wxa52bcbabd9527822, " + "q=authorizer_app_id:wxa52bcbabd9527822 AND months:4, fl=\"customer_register_id, third_open_id\", " + "sort=customer_register_id asc)";
        System.out.println(query);
        List<PersonasMember> list = new LinkedList<>();
        client.queryAndStreamResponse(query, PersonasMember.class, new TisCloudSolrClient.ResponseCallback<PersonasMember>() {

            @Override
            public void process(PersonasMember pojo) {
                System.out.println(pojo.getCustomerRegisterId() + " " + pojo.getThirdOpenId());
                list.add(pojo);
            }

            @Override
            public void lististInfo(long numFound, long start) {
            // 这个方法可以不用管
            }
        });
        System.out.println(list);
    }

    // add by baisui
    public void testStreamTuple() throws Exception {
        System.out.println("testStreamTuple start");
        // 第一个route值 填写对应的app_id
        String query = "searchExtend(search4personasMember, qt=/export, _route_=wxa52bcbabd9527822, " + "q=authorizer_app_id:wxa52bcbabd9527822 AND months:4, fl=\"customer_register_id, third_open_id\", " + "sort=customer_register_id asc)";
        System.out.println(query);
        List<Tuple> list = new LinkedList<>();
        client.queryAndStreamResponse(query, Tuple.class, new TisCloudSolrClient.ResponseCallback<Tuple>() {

            @Override
            public void process(Tuple pojo) {
                System.out.println(pojo.get("customer_register_id") + " " + pojo.get("third_open_id"));
                list.add(pojo);
            }

            @Override
            public void lististInfo(long numFound, long start) {
            // 这个方法可以不用管
            }
        });
        System.out.println(list);
    }

    // Gets stream query count. 获得流式查询的总数
    public void testStreamFactoryCount1() throws Exception {
        String query = "search(search4personasMember, qt=/export, _route_=wxa52bcbabd9527822, q=authorizer_app_id" + ":wxa52bcbabd9527822 AND NOT third_open_id:\\-1 AND months:4, fl=\"customer_register_id, " + "third_open_id\", sort=customer_register_id asc)";
        System.out.println(query);
        System.out.println(client.getStreamQueryCount(query));
    }

    // Gets stream query count. 获得流式查询的总数
    public void testStreamFactoryCount() throws Exception {
        String query = "innerJoin(\n" + "    search(search4personasMember, qt=/export, _route_=-1, q=authorizer_app_id:\\-1 AND " + "customer_register_id:0002fef66c994ac1a0c963f205c29f96, fl=\"customer_register_id, third_open_id\", " + "sort=customer_register_id asc),\n" + "    search(search4personasBase, qt=/export, q=birthday:\\-1, fl=\"customer_register_id, " + "birthday\", sort=customer_register_id asc),\n" + "    on=customer_register_id=customer_register_id\n" + "    )";
        System.out.println(query);
        System.out.println(client.getStreamQueryCount(query));
    }

    // third_open_id去重
    public void testStreamDistinct() throws Exception {
        String query = "unique(search(search4personasMember, qt=/export, _route_=wxa52bcbabd9527822, " + "q=authorizer_app_id:wxa52bcbabd9527822 AND member_level:3, fl=\"customer_register_id, " + "third_open_id\", sort=third_open_id asc), over=\"third_open_id\")";
        System.out.println(query);
        List<PersonasMember> list = new LinkedList<>();
        client.queryAndStreamResponse(query, PersonasMember.class, new TisCloudSolrClient.ResponseCallback<PersonasMember>() {

            @Override
            public void process(PersonasMember pojo) {
                System.out.println(pojo.getThirdOpenId());
                list.add(pojo);
            }

            @Override
            public void lististInfo(long numFound, long start) {
            // 这个方法可以不用管
            }
        });
        System.out.println(list);
    }

    public static class PersonasMember {

        @Field("customer_register_id")
        String customerRegisterId;

        @Field("third_open_id")
        String thirdOpenId;

        @Field("authorizer_app_id")
        String authorizerAppId;

        public String getCustomerRegisterId() {
            return customerRegisterId;
        }

        public void setCustomerRegisterId(String customerRegisterId) {
            this.customerRegisterId = customerRegisterId;
        }

        public String getThirdOpenId() {
            return thirdOpenId;
        }

        public void setThirdOpenId(String thirdOpenId) {
            this.thirdOpenId = thirdOpenId;
        }

        public String getAuthorizerAppId() {
            return authorizerAppId;
        }

        public void setAuthorizerAppId(String authorizerAppId) {
            this.authorizerAppId = authorizerAppId;
        }
    }

    // 一次查询两个customer_register_id
    public void testTermsQuery() throws Exception {
        SolrQuery query = new SolrQuery();
        query.setQuery("{!terms f=customer_register_id}0000e89d71be4021a11687fc14c8b596," + "0000f10f7bbb4942ad880ee8785de3e4");
        query.setRows(10);
        query.setFields("*");
        TisCloudSolrClient.SimpleQueryResult<PersonasBase> response = client.query("search4personasBase", "-1", query, PersonasBase.class);
        List<PersonasBase> result = response.getResult();
        for (PersonasBase personasBase : result) {
            System.out.println(personasBase.getCustomerRegisterId());
        }
    }

    // 翻页查询
    public void testDeepPaging() throws Exception {
        SolrQuery q = (new SolrQuery("*:*")).setRows(2).setSort(SolrQuery.SortClause.desc("third_open_id"));
        // 翻页用的游标
        String cursorMark = CursorMarkParams.CURSOR_MARK_START;
        boolean done = false;
        int cnt = 3;
        while (!done && cnt-- > 0) {
            q.set(CursorMarkParams.CURSOR_MARK_PARAM, cursorMark);
            TisCloudSolrClient.SimpleQueryResult<PersonasMember> response = client.query("search4personasMember", "-1", q, PersonasMember.class);
            List<PersonasMember> result = response.getResult();
            for (PersonasMember personasMember : result) {
                System.out.println(personasMember.getThirdOpenId() + " " + personasMember.getCustomerRegisterId());
            }
            System.out.println(cursorMark);
            System.out.println("-----------");
            String nextCursorMark = response.getResponse().getNextCursorMark();
            if (cursorMark.equals(nextCursorMark)) {
                done = true;
            }
            cursorMark = nextCursorMark;
        }
    }

    public static class PersonasBase {

        @Field("customer_register_id")
        private String customerRegisterId;

        @Field("mobile")
        private String mobile;

        @Field("sex")
        private Integer sex;

        @Field("birthday")
        private String birthday;

        @Field("years")
        private Integer years;

        @Field("months")
        private Integer months;

        @Field("days")
        private Integer days;

        @Field("age")
        private Integer age;

        @Field("generation")
        private Integer generation;

        @Field("contry")
        private String contry;

        @Field("province")
        private String province;

        @Field("city")
        private String city;

        @Field("town")
        private String town;

        @Field("occupation")
        private String occupation;

        @Field("_version_")
        private Long Version;

        public void setCustomerRegisterId(String customerRegisterId) {
            this.customerRegisterId = customerRegisterId;
        }

        public String getCustomerRegisterId() {
            return this.customerRegisterId;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getMobile() {
            return this.mobile;
        }

        public void setSex(Integer sex) {
            this.sex = sex;
        }

        public Integer getSex() {
            return this.sex;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getBirthday() {
            return this.birthday;
        }

        public void setYears(Integer years) {
            this.years = years;
        }

        public Integer getYears() {
            return this.years;
        }

        public void setMonths(Integer months) {
            this.months = months;
        }

        public Integer getMonths() {
            return this.months;
        }

        public void setDays(Integer days) {
            this.days = days;
        }

        public Integer getDays() {
            return this.days;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public Integer getAge() {
            return this.age;
        }

        public void setGeneration(Integer generation) {
            this.generation = generation;
        }

        public Integer getGeneration() {
            return this.generation;
        }

        public void setContry(String contry) {
            this.contry = contry;
        }

        public String getContry() {
            return this.contry;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getProvince() {
            return this.province;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCity() {
            return this.city;
        }

        public void setTown(String town) {
            this.town = town;
        }

        public String getTown() {
            return this.town;
        }

        public void setOccupation(String occupation) {
            this.occupation = occupation;
        }

        public String getOccupation() {
            return this.occupation;
        }

        public void setVersion(Long Version) {
            this.Version = Version;
        }

        public Long getVersion() {
            return this.Version;
        }
    }
}
