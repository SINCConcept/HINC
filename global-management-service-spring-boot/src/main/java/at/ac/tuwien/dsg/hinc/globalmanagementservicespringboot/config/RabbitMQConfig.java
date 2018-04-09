package at.ac.tuwien.dsg.hinc.globalmanagementservicespringboot.config;

import at.ac.tuwien.dsg.hinc.globalmanagementservicespringboot.Receiver;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sinc.hinc.common.communication.MessageDistributingConsumer;

@Configuration
public class RabbitMQConfig {

    @Value("${hinc.global.rabbitmq.input}")
    private String inputExchange;

    @Value("${hinc.global.rabbitmq.input}")
    private String inputQueue;

    @Value("${hinc.global.rabbitmq.output.broadcast}")
    private String outputBroadcast;

    @Value("${hinc.global.rabbitmq.output.groupcast}")
    private String outputGroupcast;

    @Value("${hinc.global.rabbitmq.output.unicast}")
    private String outputUnicast;

    @Bean
    Queue inputQueue() {
        //TODO queue settings
        return new Queue(inputQueue,false);
    }

    @Bean
    FanoutExchange inputExchange() {
        //TODO exchange settings
        return new FanoutExchange(inputExchange);
    }

    @Bean
    Binding binding() {
        return BindingBuilder.bind(inputQueue()).to(inputExchange());
    }

    @Bean
    FanoutExchange broadcastExchange(){
        //TODO exchange settings
        return new FanoutExchange(outputBroadcast);
    }

    @Bean
    DirectExchange groupcastExchange(){
        //TODO exchange settings
        return new DirectExchange(outputGroupcast);
    }

    @Bean
    DirectExchange unicastExchange(){
        //TODO exchange settings
        return new DirectExchange(outputUnicast);
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(inputQueue);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(Receiver receiver) {

        return new MessageListenerAdapter(receiver, "receiveMessage");
    }
}
