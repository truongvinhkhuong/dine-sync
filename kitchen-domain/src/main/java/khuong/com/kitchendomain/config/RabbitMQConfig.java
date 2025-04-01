package khuong.com.kitchendomain.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    // Exchange names
    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String KITCHEN_EXCHANGE = "kitchen.exchange";
    
    // Queue names
    public static final String NEW_ORDER_QUEUE = "order.new.queue";
    public static final String ORDER_STATUS_QUEUE = "order.status.queue";
    public static final String MENU_AVAILABILITY_QUEUE = "menu.availability.queue";
    
    // Routing keys
    public static final String NEW_ORDER_ROUTING_KEY = "order.new";
    public static final String ORDER_STATUS_ROUTING_KEY = "order.status.update";
    public static final String MENU_AVAILABILITY_ROUTING_KEY = "menu.availability.update";
    
    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(ORDER_EXCHANGE);
    }
    
    @Bean
    public TopicExchange kitchenExchange() {
        return new TopicExchange(KITCHEN_EXCHANGE);
    }
    
    @Bean
    public Queue newOrderQueue() {
        return new Queue(NEW_ORDER_QUEUE);
    }
    
    @Bean
    public Queue orderStatusQueue() {
        return new Queue(ORDER_STATUS_QUEUE);
    }
    
    @Bean
    public Queue menuAvailabilityQueue() {
        return new Queue(MENU_AVAILABILITY_QUEUE);
    }
    
    @Bean
    public Binding newOrderBinding() {
        return BindingBuilder.bind(newOrderQueue())
                .to(orderExchange())
                .with(NEW_ORDER_ROUTING_KEY);
    }
    
    @Bean
    public Binding orderStatusBinding() {
        return BindingBuilder.bind(orderStatusQueue())
                .to(kitchenExchange())
                .with(ORDER_STATUS_ROUTING_KEY);
    }
    
    @Bean
    public Binding menuAvailabilityBinding() {
        return BindingBuilder.bind(menuAvailabilityQueue())
                .to(kitchenExchange())
                .with(MENU_AVAILABILITY_ROUTING_KEY);
    }
    
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }
}