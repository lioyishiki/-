package com.hmall.trade.listener;

import com.hmall.api.client.PayClient;
import com.hmall.api.dto.PayOrderDTO;
import com.hmall.trade.constants.MQConstants;

import com.hmall.trade.domain.po.Order;
import com.hmall.trade.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @param
 * @return
 */
@Component
@RequiredArgsConstructor
public class OrderDelayMessageListener {

    private final IOrderService orderService;

    private final PayClient payClient;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue,
                    exchange = @Exchange(name = MQConstants.DELAY_EXCHANGE_NAME,delayed = "true"),
                    key = MQConstants.DELAY_ORDER_KEY
            )
    )
    public void ListenOrderDelayMessage(Long orderId){
        //1.查询本地状态
        Order order = orderService.getById(orderId);

        //2.检测订单状态，判断是否支付
        if(order==null||order.getStatus()!=1){
            //订单不存在或已支付
            return;
        }
        //3.未支付查询流水
        PayOrderDTO payOrder = payClient.queryPayOrderByBizOrderNo(orderId);
        //4.判断是否支付
        if(payOrder!=null||payOrder.getStatus()==3){
            //4.1已支付，标记订单状态为已支付
            orderService.markOrderPaySuccess(orderId);
        }else {
            //4.2未支付，取消订单，恢复库存
            orderService.cancelOrder(orderId);
        }



    }
}
