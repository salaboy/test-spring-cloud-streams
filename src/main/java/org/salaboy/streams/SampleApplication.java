package org.salaboy.streams;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.salaboy.streams.model.ComplexDataStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.Resources;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@SpringBootApplication
@EnableBinding(MyChannels.class)
@RestController
@RequestMapping(value = "/api/", produces = MediaTypes.HAL_JSON_VALUE)
public class SampleApplication implements CommandLineRunner {

    private List<String> messages = new ArrayList<>();
    private List<ComplexDataStructure> complexStuff = new ArrayList<>();

    @Autowired
    private MessageChannel myProducer;

    @Autowired
    private MessageChannel myComplexProducer;

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class,
                              args);
    }

    @Override
    public void run(String... strings) throws Exception {
        messages.add(" - initial message  - ");
        complexStuff.add(new ComplexDataStructure("A",
                                                  new Long(1),
                                                  "B"));
        assert (myProducer != null);
        assert (myComplexProducer != null);
    }

    @StreamListener(MyChannels.MY_CONSUMER)
    public void consumeMessages(String message) {
        System.out.println(">>> Message Arrived: " + message);
        messages.add(message);
        myProducer.send(MessageBuilder.withPayload("Message Arrived: " + message).build());
        myComplexProducer.send(MessageBuilder.withPayload(new ComplexDataStructure(UUID.randomUUID().toString(),
                                                                                   new Random().nextLong(),
                                                                                   "B")).build());
    }

    @RequestMapping(value = "/messages", method = RequestMethod.GET)
    public Resources<String> getMessages() {
        return new Resources<String>(messages,
                                     linkTo(methodOn(SampleApplication.class).getMessages()).withSelfRel());
    }

    @RequestMapping(value = "/complex", method = RequestMethod.GET)
    public Resources<ComplexDataStructure> getComplexStuff() {
        return new Resources<ComplexDataStructure>(complexStuff,
                                                   linkTo(methodOn(SampleApplication.class).getComplexStuff()).withSelfRel());
    }
}
