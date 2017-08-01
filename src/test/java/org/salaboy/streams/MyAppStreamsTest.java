/*
 * Copyright 2017 Alfresco and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.salaboy.streams;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.salaboy.streams.model.ComplexDataStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.binder.test.junit.rabbit.RabbitTestSupport;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:test-application.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@EnableBinding(ClientStreams.class)
public class MyAppStreamsTest {

    private static final String relativeMessagesEndpoint = "/api/messages";
    private static final String relativeComplexStuffEndpoint = "/api/complex";

    @ClassRule
    public static RabbitTestSupport rabbitTestSupport = new RabbitTestSupport();

    @Autowired
    private TestRestTemplate restTemplate;



    public static boolean notificationArrived = false;

    public static boolean complexNotificationArrived = false;

    public static String messageSent = "Message From Test";


    @Autowired
    public MessageChannel myClientProducer;


    @EnableAutoConfiguration
    public static class StreamHandler {




        @StreamListener(ClientStreams.MY_CLIENT_CONSUMER)
        public void consumeNotification(String notif) {
            assertThat(notif).isEqualTo("Message Arrived: " + messageSent);
            notificationArrived = true;
        }

        @StreamListener(ClientStreams.MY_CLIENT_COMPLEX_CONSUMER)
        public void consumeComplexNotif(ComplexDataStructure complexNotif) {
            assertThat(complexNotif).isNotNull();
            assertThat(complexNotif.getId()).isNotNull();
            assertThat(complexNotif.getId()).isNotEmpty();
            assertThat(complexNotif.getLongNumber()).isNotNull();
            assertThat(complexNotif.getSomeOtherValue()).isNotNull();
            assertThat(complexNotif.getSomeOtherValue()).isNotEmpty();

            complexNotificationArrived = true;
        }
    }

    @Test
    public void getAllMessagesTests() throws Exception {

        assertThat(myClientProducer).isNotNull();

        //given
        ResponseEntity<Resources<String>> messagesResources = restTemplate.exchange(relativeMessagesEndpoint + "?pageable={pageable}&size={size}",
                                                                                    HttpMethod.GET,
                                                                                    null,
                                                                                    new ParameterizedTypeReference<Resources<String>>() {
                                                                                    },
                                                                                    0,
                                                                                    2);
        //then
        assertThat(messagesResources).isNotNull();
        assertThat(messagesResources.getBody().getContent()).hasSize(1);

        //given

        myClientProducer.send(MessageBuilder.withPayload(messageSent).build());

        while (!notificationArrived) {
            Thread.sleep(100);
            System.out.println("Waiting for notification ...");
        }

        messagesResources = restTemplate.exchange(relativeMessagesEndpoint + "?pageable={pageable}&size={size}",
                                                  HttpMethod.GET,
                                                  null,
                                                  new ParameterizedTypeReference<Resources<String>>() {
                                                  },
                                                  0,
                                                  2);

        //then
        assertThat(messagesResources).isNotNull();
        assertThat(messagesResources.getBody().getContent()).hasSize(2);

        assertThat(notificationArrived).isTrue();
        assertThat(complexNotificationArrived).isTrue();
    }

    @Test
    public void getAllComplexStuffTests() throws Exception {
        notificationArrived = false;

        assertThat(myClientProducer).isNotNull();

        //given
        ResponseEntity<Resources<ComplexDataStructure>> complexStuffResources = restTemplate.exchange(relativeComplexStuffEndpoint + "?pageable={pageable}&size={size}",
                                                                                                      HttpMethod.GET,
                                                                                                      null,
                                                                                                      new ParameterizedTypeReference<Resources<ComplexDataStructure>>() {
                                                                                                      },
                                                                                                      0,
                                                                                                      2);
        //then
        assertThat(complexStuffResources).isNotNull();
        assertThat(complexStuffResources.getBody().getContent()).hasSize(1);

        myClientProducer.send(MessageBuilder.withPayload(messageSent).build());

        while (!notificationArrived) {
            Thread.sleep(100);
            System.out.println("Waiting for notification ...");
        }

        while (!complexNotificationArrived) {
            Thread.sleep(100);
            System.out.println("Waiting for complex notification ...");
        }

        assertThat(notificationArrived).isTrue();
        assertThat(complexNotificationArrived).isTrue();
    }
}
