package org.arig.robot.system.capteurs;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.socket.AbstractQuery;
import org.arig.robot.communication.socket.AbstractResponse;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@RunWith(BlockJUnit4ClassRunner.class)
public class SocketServerTest {

    enum TestEnum {
        ACTION
    }

    static class TestQuery extends AbstractQuery<TestEnum> {
        public TestQuery() {
            super(TestEnum.ACTION);
        }
    }

    static class TestResponse extends AbstractResponse<TestEnum> {
        public TestResponse() {
            this.setAction(TestEnum.ACTION);
        }
    }

    static class TestServer extends AbstractSocketServer<TestEnum> {
        List<TestEnum> receivedQueries = new ArrayList<>();

        public TestServer(final Integer port, final ThreadPoolExecutor executor)  {
            super(port, executor);
        }

        @Override
        protected Class<TestEnum> getActionEnum() {
            return TestEnum.class;
        }

        @Override
        protected Class<? extends AbstractQuery<TestEnum>> getQueryClass(TestEnum action) {
            return TestQuery.class;
        }

        @Override
        protected AbstractResponse<TestEnum> handleQuery(AbstractQuery<TestEnum> query) {
            receivedQueries.add(query.getAction());
            return new TestResponse();
        }
    }

    static class TestClient extends AbstractSocketClient<TestEnum> {
        List<TestEnum> receivedReponses = new ArrayList<>();

        public TestClient(final Integer port) {
            super("127.0.0.1", port, 1000);
        }

        @SneakyThrows
        public void testQuery() {
            TestResponse response = sendToSocketAndGet(new TestQuery(), TestResponse.class);
            receivedReponses.add(response.getAction());
        }
    }

    @Test
    @SneakyThrows
    public void testSimple() {
        final int port = 9000;
        final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

        TestServer server = new TestServer(port, executor);
        server.openSocket();

        TestClient client = new TestClient(port);
        client.openSocket();

        client.testQuery();

        Assertions.assertThat(server.receivedQueries).containsExactly(TestEnum.ACTION);
        Assertions.assertThat(client.receivedReponses).containsExactly(TestEnum.ACTION);

        client.end();
        server.end();
    }

}
