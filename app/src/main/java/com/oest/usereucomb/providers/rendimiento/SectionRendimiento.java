package com.oest.usereucomb.providers.rendimiento;


public class SectionRendimiento {


    private int id;
    private String kl;
    private String carga;
    private String fecha;
    private String kilometros;

    public SectionRendimiento(){
        id=0;
        kl="";
        carga="";
        fecha="";
        kilometros="";
    }

    public SectionRendimiento(int id, String kl, String carga, String fecha, String kilometros){

        this.id=id;
        this.kl=kl;
        this.carga=carga;
        this.fecha=fecha;
        this.kilometros=kilometros;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKl() {
        return kl;
    }

    public void setKl(String kl) {
        this.kl = kl;
    }

    public String getCarga() {
        return carga;
    }

    public void setCarga(String carga) { this.carga = carga; }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha){
        this.fecha = fecha;
    }

    public String getKilometros() {
        return kilometros;
    }

    public void setKilometros(String kilometros){
        this.kilometros = kilometros;
    }
}

