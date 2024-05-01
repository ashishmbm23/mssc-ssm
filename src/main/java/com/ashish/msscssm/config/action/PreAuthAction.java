package com.ashish.msscssm.config.action;

import com.ashish.msscssm.domain.PaymentEvent;
import com.ashish.msscssm.domain.PaymentState;
import com.ashish.msscssm.services.PaymentServiceImpl;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class PreAuthAction implements Action<PaymentState, PaymentEvent> {
    @Override
    public void execute(StateContext<PaymentState, PaymentEvent> context) {
        System.out.println("PreAuth was called!!!");

        if (new Random().nextInt(10) < 8) {
            System.out.println("Pre Auth Approved");
            context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.PRE_AUTHORIZE_APPROVED)
                    .setHeader(PaymentServiceImpl.PAYMENT_HEADER_ID, context.getMessageHeader(PaymentServiceImpl.PAYMENT_HEADER_ID))
                    .build());

        } else {
            System.out.println("Per Auth Declined! No Credit!!!!!!");
            context.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.PRE_AUTHORIZE_DECLINED)
                    .setHeader(PaymentServiceImpl.PAYMENT_HEADER_ID, context.getMessageHeader(PaymentServiceImpl.PAYMENT_HEADER_ID))
                    .build());
        }
    }
}
