package com.ashish.msscssm.config;

import com.ashish.msscssm.domain.PaymentEvent;
import com.ashish.msscssm.domain.PaymentState;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import java.util.UUID;

@SpringBootTest
@Slf4j
class StateMachineConfigTest {

    @Autowired
    StateMachineFactory<PaymentState, PaymentEvent> factory;

    @Test
    public void testStateMachine(){
        log.info("Entering state machine");

        StateMachine<PaymentState,PaymentEvent> stateMachine = factory.getStateMachine(UUID.randomUUID());
        stateMachine.startReactively().block();
        log.info(stateMachine.getState().toString());

        stateMachine.sendEvent(PaymentEvent.PRE_AUTHORIZE);

        log.info(stateMachine.getState().toString());

        stateMachine.sendEvent(PaymentEvent.PRE_AUTHORIZE_APPROVED);

        log.info(stateMachine.getState().toString());
    }
}