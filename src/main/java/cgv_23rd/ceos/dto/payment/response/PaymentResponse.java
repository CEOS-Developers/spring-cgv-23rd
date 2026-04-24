package cgv_23rd.ceos.dto.payment.response;

public record PaymentResponse(
        Integer code,
        String message,
        PaymentData data
) {
    public record PaymentData(
            String paymentId,
            String paymentStatus,
            String orderName,
            String pgProvider,
            String currency,
            String customData,
            String paidAt
    ) {
    }
}
