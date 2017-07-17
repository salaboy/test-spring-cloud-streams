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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.stream.annotation.EnableBinding;
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
@DirtiesContext
@EnableBinding(StreamProducer.class)
public class MyAppStreamsTests {

    private static final String relativeMessagesEndpoint = "/api/messages";

    @ClassRule
    public static RabbitTestSupport rabbitTestSupport = new RabbitTestSupport();

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MessageChannel myProducer;

    @Test
    public void getAllMessagesTests() throws Exception {

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


        myProducer.send(MessageBuilder.withPayload("Message From Test").build());



        Thread.sleep(5000);


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
    }
}
