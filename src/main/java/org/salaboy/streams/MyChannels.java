package org.salaboy.streams;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface MyChannels {
    String My_CONSUMER = "myConsumer";

    @Input(My_CONSUMER)
    SubscribableChannel myConsumer();
}
