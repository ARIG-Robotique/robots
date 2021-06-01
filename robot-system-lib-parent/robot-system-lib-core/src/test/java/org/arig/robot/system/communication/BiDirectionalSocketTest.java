package org.arig.robot.system.communication;

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
public class BiDirectionalSocketTest {

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

    static class TestSocket extends AbstractBidirectionalSocket<TestEnum> {
        List<TestEnum> receivedQueries = new ArrayList<>();
        List<TestEnum> receivedReponses = new ArrayList<>();

        public TestSocket(final int serverPort, final int port, final ThreadPoolExecutor executor) {
            super(serverPort, "127.0.0.1", port, 1000, executor);
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

        @SneakyThrows
        public void testQuery() {
            TestResponse response = sendToSocketAndGet(new TestQuery(), TestResponse.class);
            receivedReponses.add(response.getAction());
        }
    }

    @Test
    @SneakyThrows
    public void testSimple() {
        final int serverPort = 9000;
        final int clientPort = 9001;
        final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

        TestSocket server = new TestSocket(serverPort, clientPort, executor);
        TestSocket client = new TestSocket(clientPort, serverPort, executor);

        server.openSocket();
        client.openSocket();

        server.tryConnect();
        client.tryConnect();

        client.testQuery();
        Assertions.assertThat(server.receivedQueries).containsExactly(TestEnum.ACTION);
        Assertions.assertThat(client.receivedReponses).containsExactly(TestEnum.ACTION);

        server.testQuery();
        Assertions.assertThat(client.receivedQueries).containsExactly(TestEnum.ACTION);
        Assertions.assertThat(server.receivedReponses).containsExactly(TestEnum.ACTION);

        client.end();
        server.end();
    }

}
