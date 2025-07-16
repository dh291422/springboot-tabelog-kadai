package com.example.nagoyameshi.service;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.param.SubscriptionCancelParams;
import com.stripe.param.checkout.SessionCreateParams;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StripeService {

    @Value("${stripe.api-key}")
    private String stripeApiKey;

    private final UserRepository userRepository;

    private static final String PRICE_ID = "price_1Rgc5DB6eHNhnOUxyl1DHeSp";

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    /**
     * Stripe Checkout用セッション作成（サブスクリプション）
     */
    public String createStripeSession(HttpServletRequest httpServletRequest) {
        String baseUrl = httpServletRequest.getScheme() + "://" +
                httpServletRequest.getServerName() +
                (httpServletRequest.getServerPort() == 80 || httpServletRequest.getServerPort() == 443
                        ? ""
                        : ":" + httpServletRequest.getServerPort());

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setPrice(PRICE_ID)
                        .setQuantity(1L)
                        .build())
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSuccessUrl(baseUrl + "/subscription/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(baseUrl + "/subscription/cancel?session_id={CHECKOUT_SESSION_ID}")
                .build();

        try {
            Session session = Session.create(params);
            return session.getId();
        } catch (StripeException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String createCustomerPortalSession(String customerId, String returnUrl) {
        com.stripe.param.billingportal.SessionCreateParams params =
            com.stripe.param.billingportal.SessionCreateParams.builder()
                .setCustomer(customerId)
                .setReturnUrl(returnUrl)
                .build();

        try {
            com.stripe.model.billingportal.Session session =
                com.stripe.model.billingportal.Session.create(params);
            return session.getUrl();  // これがポータルのURL
        } catch (StripeException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Stripeサブスクリプション解約処理
     */
    public void cancelSubscription(User user) {
        try {
            System.out.println("★ 解約処理開始 for user: " + user.getEmail());

            Subscription subscription = Subscription.retrieve(user.getSubscriptionId());
            SubscriptionCancelParams params = SubscriptionCancelParams.builder().build();
            subscription.cancel(params);

            System.out.println("★ Stripe側の解約完了: " + user.getSubscriptionId());

            user.setSubscriptionId(null);
            userRepository.save(user);

            System.out.println("★ ユーザー情報も更新済み");

        } catch (StripeException e) {
            e.printStackTrace();
        }
    }

    /**
     * 決済成功時のハンドリング処理
     */
    public void handleSuccess(HttpServletRequest request, User user) {
        try {
            String sessionId = request.getParameter("session_id");
            if (sessionId != null) {
                com.stripe.model.checkout.Session session = com.stripe.model.checkout.Session.retrieve(sessionId);

                // ここで customerId を取得！
                String customerId = session.getCustomer();
                String subscriptionId = session.getSubscription();

                if (customerId != null) {
                    user.setStripeCustomerId(customerId);
                }

                if (subscriptionId != null) {
                    user.setSubscriptionId(subscriptionId);
                }

                userRepository.save(user);
                System.out.println("★ ユーザー情報に subscriptionId と customerId を保存しました！");
            }
        } catch (StripeException e) {
            e.printStackTrace();
        }
    }

}
