package khuong.com.kitchendomain.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String NEW_ORDER_QUEUE = "kitchen-orders-queue";
    public static final String ORDERS_EXCHANGE = "orders-exchange";
    public static final String NEW_ORDER_ROUTING_KEY = "new-order";
    
    public static final String ORDER_UPDATES_QUEUE = "kitchen-order-updates-queue";
    public static final String ORDER_UPDATE_ROUTING_KEY = "order-update";

    @Bean
    Queue ordersQueue() {
        return new Queue(NEW_ORDER_QUEUE, true);
    }
    
    @Bean
    Queue orderUpdatesQueue() {
        return new Queue(ORDER_UPDATES_QUEUE, true);
    }

    @Bean
    DirectExchange ordersExchange() {
        return new DirectExchange(ORDERS_EXCHANGE);
    }

    @Bean
    Binding orderBinding() {
        return BindingBuilder.bind(ordersQueue())
                .to(ordersExchange())
                .with(NEW_ORDER_ROUTING_KEY);
    }
    
    @Bean
    Binding orderUpdateBinding() {
        return BindingBuilder.bind(orderUpdatesQueue())
                .to(ordersExchange())
                .with(ORDER_UPDATE_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}