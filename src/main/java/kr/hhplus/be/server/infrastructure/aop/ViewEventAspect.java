package kr.hhplus.be.server.infrastructure.aop;

import kr.hhplus.be.server.domain.ranking.event.RankingEventPublisher;
import kr.hhplus.be.server.domain.product.event.ProductViewedEvent;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ViewEventAspect {
    private final RankingEventPublisher publisher;

    @Pointcut("execution(* kr.hhplus.be.server.interfaces.api.product.ProductController.getProductDetails(..))")
    public void productDetail() {}

    @AfterReturning(pointcut = "productDetail()", returning = "response")
    public void afterDetail(JoinPoint jp, Object response) {
        Long productId = (Long) jp.getArgs()[0];
        publisher.publish(new ProductViewedEvent(this, productId));
    }
}
