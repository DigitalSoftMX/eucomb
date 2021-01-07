package com.oest.usereucomb.billing;

import java.util.UUID;

public class Rendimiento {
    private String id;
    private String kl;
    private String carga;
    private String fecha;
    private String kilometros;

    public Rendimiento(String kl,
                    String carga, String fecha, String kilometros) {
        this.id = UUID.randomUUID().toString();
        this.kl = kl;
        this.carga = carga;
        this.fecha = fecha;
        this.kilometros = kilometros;
    }


    public String getId() {
        return id;
    }

    public String getKl() {
        return kl;
    }

    public String getCarga() {
        return carga;
    }

    public String getFecha() {
        return fecha;
    }

    public String getKilometros() {
        return kilometros;
    }

}

