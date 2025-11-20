package com.fourweekdays.fourweekdays.common.email;

import com.fourweekdays.fourweekdays.purchaseorder.model.entity.PurchaseOrder;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmailService {

    private final EmailSender emailSender;

    public void sendPurchaseOrderMail(PurchaseOrder order) throws MessagingException {
        String to = order.getVendor().getEmail();
        String subject = "[발주서 승인] " + order.getOrderCode();
        String body = EmailBodyBuilder.buildPurchaseOrderBody(order);

        emailSender.send(to, subject, body);
    }
}
