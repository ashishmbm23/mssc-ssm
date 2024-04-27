package com.ashish.msscssm.services;

import com.ashish.msscssm.domain.Payment;
import com.ashish.msscssm.domain.PaymentEvent;
import com.ashish.msscssm.domain.PaymentState;
import com.ashish.msscssm.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    public static final String PAYMENT_HEADER_ID = "payment_id";
    private final PaymentRepository paymentRepository;
    private final StateMachineFactory<PaymentState, PaymentEvent> factory;

    @Override
    public Payment newPayment(Payment payment) {
        payment.setState(PaymentState.NEW);
        return paymentRepository.save(payment);
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId) {
        StateMachine<PaymentState,PaymentEvent> stateMachine = build(paymentId);
        sendEvent(paymentId,stateMachine, PaymentEvent.PRE_AUTHORIZE);
        return null;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId) {
        StateMachine<PaymentState,PaymentEvent> stateMachine = build(paymentId);
        sendEvent(paymentId, stateMachine, PaymentEvent.PRE_AUTHORIZE_APPROVED);
        return null;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId) {
        StateMachine<PaymentState,PaymentEvent> stateMachine = build(paymentId);
        sendEvent(paymentId,stateMachine,PaymentEvent.PRE_AUTHORIZE_DECLINED);
        return null;
    }

    public void sendEvent(Long paymentId, StateMachine<PaymentState, PaymentEvent> stateMachine, PaymentEvent event){
        Message message = MessageBuilder.withPayload(event)
                .setHeader(PAYMENT_HEADER_ID, paymentId)
                .build();
        stateMachine.sendEvent(Mono.just(message)).blockFirst();
    }

    public StateMachine<PaymentState, PaymentEvent> build(Long paymentId){
        Payment payment = paymentRepository.getReferenceById(paymentId);
        StateMachine<PaymentState,PaymentEvent> stateMachine = factory.getStateMachine(Long.toString(payment.getId()));
        stateMachine.stopReactively().block();
        stateMachine.getStateMachineAccessor()
                .doWithAllRegions( sma -> {
                    sma.resetStateMachineReactively(new DefaultStateMachineContext<>(payment.getState(), null, null, null)).block();
                });
        stateMachine.startReactively().block();
        return stateMachine;
    }
}
