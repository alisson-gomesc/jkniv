package net.sf.jkniv.whinstone.cassandra;

import java.util.Properties;

import org.junit.Ignore;
import org.junit.Test;

import net.sf.jkniv.sqlegance.RepositoryProperty;
import net.sf.jkniv.sqlegance.RepositoryType;
import net.sf.jkniv.whinstone.QueryFactory;
import net.sf.jkniv.whinstone.Repository;
import net.sf.jkniv.whinstone.RepositoryService;

public class AstraCassandraTest
{

    private static final String KEY_FILE = "/database/astra-secure-connect-jkniv.zip";
    private static final String USER = "sysadmin";
    private static final String PASS = "Whin123456";
    
    /*
java.lang.IllegalStateException: Cannot construct cloud config from the cloudConfigUrl: file:/C:/dev/wks/wks-jkniv-git/jkniv-whinstone/jkniv-whinstone-cassandra/target/test-classes/database/astra-secure-connect-jkniv.zip
    at com.datastax.driver.core.Cluster$Builder.withCloudSecureConnectBundle(Cluster.java:1429)
    at net.sf.jkniv.whinstone.cassandra.CassandraSessionFactory.<init>(CassandraSessionFactory.java:76)
    at net.sf.jkniv.whinstone.cassandra.RepositoryCassandra.<init>(RepositoryCassandra.java:127)
    at net.sf.jkniv.whinstone.cassandra.RepositoryCassandra.<init>(RepositoryCassandra.java:86)
    at net.sf.jkniv.whinstone.cassandra.RepositoryFactoryCassandra.newInstance(RepositoryFactoryCassandra.java:41)
    at net.sf.jkniv.whinstone.cassandra.AstraCassandraTest.whenConnectToAstraCassandra(AstraCassandraTest.java:31)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
    at java.lang.reflect.Method.invoke(Method.java:597)
    at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
    at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
    at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
    at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
    at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
    at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
    at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
    at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
    at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
    at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
    at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
    at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
    at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
    at org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:89)
    at org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:41)
    at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:541)
    at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:763)
    at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:463)
    at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:209)
Caused by: javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: KeyUsage does not allow key encipherment
    at com.sun.net.ssl.internal.ssl.Alerts.getSSLException(Alerts.java:174)
    at com.sun.net.ssl.internal.ssl.SSLSocketImpl.fatal(SSLSocketImpl.java:1747)
    at com.sun.net.ssl.internal.ssl.Handshaker.fatalSE(Handshaker.java:241)
    at com.sun.net.ssl.internal.ssl.Handshaker.fatalSE(Handshaker.java:235)
    at com.sun.net.ssl.internal.ssl.ClientHandshaker.serverCertificate(ClientHandshaker.java:1209)
    at com.sun.net.ssl.internal.ssl.ClientHandshaker.processMessage(ClientHandshaker.java:135)
    at com.sun.net.ssl.internal.ssl.Handshaker.processLoop(Handshaker.java:593)
    at com.sun.net.ssl.internal.ssl.Handshaker.process_record(Handshaker.java:529)
    at com.sun.net.ssl.internal.ssl.SSLSocketImpl.readRecord(SSLSocketImpl.java:943)
    at com.sun.net.ssl.internal.ssl.SSLSocketImpl.performInitialHandshake(SSLSocketImpl.java:1188)
    at com.sun.net.ssl.internal.ssl.SSLSocketImpl.startHandshake(SSLSocketImpl.java:1215)
    at com.sun.net.ssl.internal.ssl.SSLSocketImpl.startHandshake(SSLSocketImpl.java:1199)
    at sun.net.www.protocol.https.HttpsClient.afterConnect(HttpsClient.java:434)
    at sun.net.www.protocol.https.AbstractDelegateHttpsURLConnection.connect(AbstractDelegateHttpsURLConnection.java:166)
    at sun.net.www.protocol.http.HttpURLConnection.getInputStream(HttpURLConnection.java:1195)
    at sun.net.www.protocol.https.HttpsURLConnectionImpl.getInputStream(HttpsURLConnectionImpl.java:234)
    at com.datastax.driver.core.CloudConfigFactory.fetchProxyMetadata(CloudConfigFactory.java:210)
    at com.datastax.driver.core.CloudConfigFactory.createCloudConfig(CloudConfigFactory.java:112)
    at com.datastax.driver.core.Cluster$Builder.withCloudSecureConnectBundle(Cluster.java:1424)
Caused by: sun.security.validator.ValidatorException: KeyUsage does not allow key encipherment
    ... 28 more
    at sun.security.validator.EndEntityChecker.checkTLSServer(EndEntityChecker.java:247)
    at sun.security.validator.EndEntityChecker.check(EndEntityChecker.java:124)
    at sun.security.validator.Validator.validate(Validator.java:221)
    at com.sun.net.ssl.internal.ssl.X509TrustManagerImpl.validate(X509TrustManagerImpl.java:126)
    at com.sun.net.ssl.internal.ssl.X509TrustManagerImpl.checkServerTrusted(X509TrustManagerImpl.java:209)
    at com.sun.net.ssl.internal.ssl.X509TrustManagerImpl.checkServerTrusted(X509TrustManagerImpl.java:249)
    at com.sun.net.ssl.internal.ssl.ClientHandshaker.serverCertificate(ClientHandshaker.java:1188)
    ... 42 more
     */
    @Test @Ignore("JDK 6 throws ValidatorException: KeyUsage does not allow key encipherment")
    public void whenConnectToAstraCassandra()
    {
        Properties props = new Properties();
        props.put(RepositoryProperty.JDBC_USER.key(), USER);
        props.put(RepositoryProperty.JDBC_PASSWORD.key(), PASS);
        props.put(RepositoryProperty.JDBC_SCHEMA.key(), "whinstone");
        props.put(RepositoryProperty.KEY_FILE.key(), KEY_FILE);
        
        Repository repository = RepositoryService.getInstance().lookup(RepositoryType.CASSANDRA).newInstance(props);
        
        repository.list(QueryFactory.of("colors-by-name", "name", "blue"));
        
    }
}
