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
package com.qlangtech.tis.fullbuild.servlet;

/* *
 * @author 百岁（baisui@qlangtech.com）
 * @date 2019年1月17日
 */
public class JettyTISRunner {
    // Server server;
    // FilterHolder dispatchFilter;
    // String context;
    // 
    // /**
    // * A main class that starts jetty+solr This is useful for debugging
    // */
    // public static void main(String[] args) {
    // try {
    // JettyTISRunner jetty = new JettyTISRunner("/", 3456);
    // jetty.addServlet(new TisServlet(null), "/trigger");
    // jetty.start();
    // } catch (Exception ex) {
    // ex.printStackTrace();
    // }
    // }
    // 
    // public JettyTISRunner(String context, int port) {
    // this.init(context, port);
    // }
    // 
    // public JettyTISRunner(String context, int port, String solrConfigFilename) {
    // this.init(context, port);
    // if (solrConfigFilename != null)
    // dispatchFilter.setInitParameter("solrconfig-filename",
    // solrConfigFilename);
    // }
    // 
    // // public JettySolrRunner( String context, String home, String dataDir, int
    // // port, boolean log )
    // // {
    // // if(!log) {
    // // System.setProperty("org.mortbay.log.class", NoLog.class.getName() );
    // // System.setProperty("java.util.logging.config.file",
    // // home+"/conf/logging.properties");
    // // NoLog noLogger = new NoLog();
    // // org.mortbay.log.Log.setLog(noLogger);
    // // }
    // //
    // // // Initalize JNDI
    // // Config.setInstanceDir(home);
    // // new SolrCore(dataDir, new IndexSchema(home+"/conf/schema.xml"));
    // // this.init( context, port );
    // // }
    // 
    // private ServletContextHandler rootContext;
    // 
    // // private void init(String context, int port) {
    // // this.context = context;
    // // server = new Server(port);
    // // if (System.getProperty("jetty.testMode") != null) {
    // // // SelectChannelConnector connector = new SelectChannelConnector();
    // // // Normal SocketConnector is what solr's example server uses by
    // // // default
    // // SocketConnector connector = new SocketConnector();
    // // connector.setPort(port);
    // // connector.setReuseAddress(true);
    // // server.setConnectors(new Connector[] { connector });
    // // server.setSessionIdManager(new HashSessionIdManager(new Random()));
    // // }
    // // server.setStopAtShutdown(true);
    // //
    // // // server.setThreadPool(new QueuedThreadPool(100));
    // //
    // // // Initialize the servlets
    // // this.rootContext = new Context(server, context, Context.SESSIONS);
    // // // for some reason, there must be a servlet for this to get applied
    // // rootContext.addServlet(Servlet404.class, "/*");
    // // dispatchFilter = rootContext.addFilter(InnerFilter.class, "*",
    // // Handler.REQUEST);
    // // }
    // 
    // private void init(String context, int port) {
    // this.context = context;
    // server = new Server(port);
    // if (System.getProperty("jetty.testMode") != null) {
    // // SelectChannelConnector connector = new SelectChannelConnector();
    // // Normal SocketConnector is what solr's example server uses by
    // // default
    // SocketConnector connector = new SocketConnector();
    // connector.setPort(port);
    // connector.setReuseAddress(true);
    // server.setConnectors(new Connector[] { connector });
    // server.setSessionIdManager(new HashSessionIdManager(new Random()));
    // }
    // server.setStopAtShutdown(true);
    // 
    // // ServletContextHandler
    // 
    // this.rootContext = new ServletContextHandler(server, context// ,ServletContextHandler.SESSIONS
    // );
    // 
    // // server.setThreadPool(new QueuedThreadPool(100));
    // 
    // // Initialize the servlets
    // // this.rootContext = new Context(server, context,
    // // ServletContextHandler.SESSIONS);
    // // for some reason, there must be a servlet for this to get applied
    // rootContext.addServlet(Servlet404.class, "/*");
    // dispatchFilter = rootContext.addFilter(InnerFilter.class, "*",
    // EnumSet.of(DispatcherType.REQUEST));
    // }
    // 
    // public void addServlet(HttpServlet servlet, String pathSpec) {
    // this.rootContext.addServlet(new ServletHolder(servlet), pathSpec);
    // }
    // 
    // public static class InnerFilter implements Filter {
    // 
    // @Override
    // public void init(FilterConfig filterConfig) throws ServletException {
    // }
    // 
    // @Override
    // public void doFilter(ServletRequest request, ServletResponse response,
    // FilterChain chain) throws IOException, ServletException {
    // chain.doFilter(request, response);
    // 
    // }
    // 
    // @Override
    // public void destroy() {
    // }
    // 
    // }
    // 
    // // ------------------------------------------------------------------------------------------------
    // // ------------------------------------------------------------------------------------------------
    // 
    // public void start() throws Exception {
    // start(true);
    // }
    // 
    // public void start(boolean waitForSolr) throws Exception {
    // if (!server.isRunning()) {
    // server.start();
    // // server.join();
    // }
    // // if (waitForSolr)
    // // waitForSolr(context);
    // }
    // 
    // public void stop() throws Exception {
    // if (server.isRunning()) {
    // server.stop();
    // server.join();
    // }
    // }
    // 
    // // /**
    // // * Waits until a ping query to the solr server succeeds, retrying every
    // // 200
    // // * milliseconds up to 2 minutes.
    // // */
    // // public void waitForSolr(String context) throws Exception {
    // // int port = getLocalPort();
    // //
    // // // A raw term query type doesn't check the schema
    // // URL url = new URL("http://localhost:" + port + context
    // // + "/select?q={!raw+f=junit_test_query}ping");
    // //
    // // Exception ex = null;
    // // // Wait for a total of 20 seconds: 100 tries, 200 milliseconds each
    // // for (int i = 0; i < 600; i++) {
    // // try {
    // // InputStream stream = url.openStream();
    // // stream.close();
    // // } catch (IOException e) {
    // // // e.printStackTrace();
    // // ex = e;
    // // Thread.sleep(200);
    // // continue;
    // // }
    // //
    // // return;
    // // }
    // //
    // // throw new RuntimeException("Jetty/Solr unresponsive", ex);
    // // }
    // 
    // /**
    // * Returns the Local Port of the first Connector found for the jetty Server.
    // *
    // * @exception RuntimeException
    // *                if there is no Connector
    // */
    // public int getLocalPort() {
    // Connector[] conns = server.getConnectors();
    // if (0 == conns.length) {
    // throw new RuntimeException("Jetty Server has no Connectors");
    // }
    // return conns[0].getLocalPort();
    // }
    // 
    // // --------------------------------------------------------------
    // // --------------------------------------------------------------
    // 
    // /**
    // * This is a stupid hack to give jetty something to attach to
    // */
    // public static class Servlet404 extends HttpServlet {
    // 
    // private static final long serialVersionUID = 1L;
    // 
    // @Override
    // public void service(HttpServletRequest req, HttpServletResponse res)
    // throws IOException {
    // res.sendError(404, "Can not find: " + req.getRequestURI());
    // }
    // }
}
