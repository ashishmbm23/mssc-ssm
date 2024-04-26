package com.ashish.msscssm.services;

import com.ashish.msscssm.domain.Payment;
import com.ashish.msscssm.domain.PaymentEvent;
import com.ashish.msscssm.domain.PaymentState;
import com.ashish.msscssm.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
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
        return null;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId) {
        StateMachine<PaymentState,PaymentEvent> stateMachine = build(paymentId);
        return null;
    }

    @Override
    public StateMachine<PaymentState, PaymentEvent> declineAuth(Long paymentId) {
        StateMachine<PaymentState,PaymentEvent> stateMachine = build(paymentId);
        return null;
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
