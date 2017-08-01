package org.salaboy.streams;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface MyChannels {

    String MY_CONSUMER = "myConsumer";

    @Input(MY_CONSUMER)
    SubscribableChannel myConsumer();

    String MY_PRODUCER = "myProducer";

    @Output(MY_PRODUCER)
    MessageChannel myProducer();

    String MY_COMPLEX_PRODUCER = "myComplexProducer";

    @Output(MY_COMPLEX_PRODUCER)
    MessageChannel myComplexProducer();
}
