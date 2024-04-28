package com.ashish.msscssm.services;

import com.ashish.msscssm.domain.Payment;
import com.ashish.msscssm.domain.PaymentEvent;
import com.ashish.msscssm.domain.PaymentState;
import com.ashish.msscssm.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class PaymentStateChangeInterceptor extends StateMachineInterceptorAdapter<PaymentState, PaymentEvent> {

    private final PaymentRepository paymentRepository;

    @Override
    public void preStateChange(State<PaymentState, PaymentEvent> state, Message<PaymentEvent> message,
                               Transition<PaymentState, PaymentEvent> transition,
                               StateMachine<PaymentState, PaymentEvent> stateMachine,
                               StateMachine<PaymentState, PaymentEvent> rootStateMachine) {
        Optional.ofNullable(Long.class.cast(message.getHeaders().getOrDefault(PaymentServiceImpl.PAYMENT_HEADER_ID, -1L)))
                .ifPresent( paymentId -> {
                    Payment payment = paymentRepository.getReferenceById(paymentId);
                    payment.setState(state.getId());
                    paymentRepository.save(payment);
                });
    }
}
