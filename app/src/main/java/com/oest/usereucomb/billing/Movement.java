package com.oest.usereucomb.billing;

import java.util.UUID;

public class Movement {
    private String id;
    private String qr_membership;
    private String qr_dispatcher;
    private String qr_ticket;

    public Movement(String qr_membership,
                  String qr_dispatcher, String qr_ticket) {
        this.id = UUID.randomUUID().toString();
        this.qr_membership = qr_membership;
        this.qr_dispatcher = qr_dispatcher;
        this.qr_ticket = qr_ticket;
        }

    public String getId() {
        return id;
    }

    public String getQr_membership() {
        return qr_membership;
    }

    public String getQr_dispatcher() {
        return qr_dispatcher;
    }

    public String getQr_ticket() {
        return qr_ticket;
    }

}
