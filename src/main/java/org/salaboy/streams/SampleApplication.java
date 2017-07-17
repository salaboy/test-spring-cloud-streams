package org.salaboy.streams;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resources;
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

    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class,
                              args);

    }

    @Override
    public void run(String... strings) throws Exception {
        messages.add(" - initial message  - ");
    }

    @StreamListener(MyChannels.My_CONSUMER)
    public void consumeMessages(String message) {
        System.out.println(">>> Message Arrived: " + message);
        messages.add(message);
    }

    @RequestMapping(value = "/messages", method = RequestMethod.GET)
    public Resources<String> getMessages() {
        return new Resources<String>(messages,
                                     linkTo(methodOn(SampleApplication.class).getMessages()).withSelfRel());
    }
}
