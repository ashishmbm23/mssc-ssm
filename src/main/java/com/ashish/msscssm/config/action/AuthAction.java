package com.ashish.msscssm.config.action;

import com.ashish.msscssm.domain.PaymentEvent;
import com.ashish.msscssm.domain.PaymentState;
import com.ashish.msscssm.services.PaymentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@Slf4j
public class AuthAction implements Action<PaymentState, PaymentEvent> {
    @Override
    public void execute(StateContext<PaymentState, PaymentEvent> stateContext) {
        log.info("auth action was called");
        if( new Random().nextInt(10) < 8 ){
            System.out.println("auth approved was called");
            stateContext.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.AUTH_APPROVED)
                    .setHeader(PaymentServiceImpl.PAYMENT_HEADER_ID, stateContext.getMessageHeader(PaymentServiceImpl.PAYMENT_HEADER_ID))
                    .build());
        }else{
            System.out.println("auth declined was called.");
            stateContext.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.AUTH_DECLINED)
                    .setHeader(PaymentServiceImpl.PAYMENT_HEADER_ID, stateContext.getMessageHeader(PaymentServiceImpl.PAYMENT_HEADER_ID))
                    .build());
        }
    }
}
