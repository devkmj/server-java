package kr.hhplus.be.server.infrastructure.aop;

import kr.hhplus.be.server.domain.product.event.ProductViewedEvent;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ViewEventAspect {
    private final ApplicationEventPublisher publisher;

    @Pointcut("execution(* kr.hhplus.be.server.interfaces.api.product.ProductController.getProductDetails(..))")
    public void productDetail() {}

    @AfterReturning(pointcut = "productDetail()", returning = "response")
    public void afterDetail(JoinPoint jp, Object response) {
        Long productId = (Long) jp.getArgs()[0];
        publisher.publishEvent(new ProductViewedEvent(this, productId));
    }
}
