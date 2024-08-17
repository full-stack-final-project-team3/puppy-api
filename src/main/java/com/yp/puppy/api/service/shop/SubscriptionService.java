//import com.yp.puppy.api.entity.shop.Bundle;
//import com.yp.puppy.api.repository.shop.BundleRepository;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//import java.time.temporal.ChronoUnit;
//import java.util.List;
//
//@Component
//public class SubscriptionService {
//
//    private final BundleRepository bundleRepository;
//
//    public SubscriptionService(BundleRepository bundleRepository) {
//        this.bundleRepository = bundleRepository;
//    }
//
//    // 한 달에 한 번 실행
//    @Scheduled(cron = "0 0 1 * * ?") // 매월 1일 01:00 AM에 실행
//    public void updateSubscriptions() {
//        List<Bundle> bundles = bundleRepository.findAll();
//        for (Bundle bundle : bundles) {
//            if (isSubscriptionExpired(bundle)) {
//                bundle.incrementCycle();
//                bundleRepository.save(bundle); // 변경 사항 저장
//            }
//        }
//    }
//
//    private boolean isSubscriptionExpired(Bundle bundle) {
//        // 구독 시작일로부터 한 달 후가 만료일
//        LocalDateTime subscriptionEndDate = bundle.getSubscriptionsStartDate()
//                .plus(1, ChronoUnit.MONTHS);
//        return LocalDateTime.now().isAfter(subscriptionEndDate);
//    }
//}
