package org.arig.robot.system.communication;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.socket.AbstractQuery;
import org.arig.robot.communication.socket.AbstractResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@ExtendWith(SpringExtension.class)
public class BiDirectionalSocketTest {

    enum TestEnum {
        ACTION_WITH_REPONSE,
        ACTION_WITHOUT_REPONSE
    }

    static class TestQueryWithResponse extends AbstractQuery<TestEnum> {
        public TestQueryWithResponse() {
            super(TestEnum.ACTION_WITH_REPONSE);
        }
    }

    static class TestQueryWithoutResponse extends AbstractQuery<TestEnum> {
        public TestQueryWithoutResponse() {
            super(TestEnum.ACTION_WITHOUT_REPONSE);
        }
    }

    static class TestResponse extends AbstractResponse<TestEnum> {
        public TestResponse() {
            this.setAction(TestEnum.ACTION_WITH_REPONSE);
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
            switch (action) {
                case ACTION_WITH_REPONSE:
                    return TestQueryWithResponse.class;
                default:
                    return null;
            }
        }

        @Override
        protected AbstractResponse<TestEnum> handleQuery(AbstractQuery<TestEnum> query) {
            receivedQueries.add(query.getAction());
            switch (query.getAction()) {
                case ACTION_WITH_REPONSE:
                    return new TestResponse();
                default:
                    return null;
            }
        }

        @SneakyThrows
        public void testQueryWithReponse() {
            TestResponse response = sendToSocketAndGet(new TestQueryWithResponse(), TestResponse.class);
            receivedReponses.add(response.getAction());
        }

        @SneakyThrows
        public void testQueryWithoutReponse() {
            sendToSocketAndGet(new TestQueryWithoutResponse(), null);
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

        client.testQueryWithReponse();
        Assertions.assertThat(server.receivedQueries).containsExactly(TestEnum.ACTION_WITH_REPONSE);
        Assertions.assertThat(client.receivedReponses).containsExactly(TestEnum.ACTION_WITH_REPONSE);

        server.testQueryWithReponse();
        Assertions.assertThat(client.receivedQueries).containsExactly(TestEnum.ACTION_WITH_REPONSE);
        Assertions.assertThat(server.receivedReponses).containsExactly(TestEnum.ACTION_WITH_REPONSE);

        client.testQueryWithoutReponse();

        client.end();
        server.end();
    }

}
