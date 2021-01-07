package com.oest.usereucomb.providers.vales;

/**
 * Created by OeSt on 25/10/2017.
 */

public class SectionVales {

        private int id;
        private String folio;
        private String numero;
        private String lugar;
        private String puntos;
        private String valor;
        private String estatus;
        private String todate_certificado;
        //historial
        private String membresia;
        private String tipo;
        //Vales
        private String estacion;
        private String vales;
        private String imagen1;
    //Premios
        private String premios;
        private String dias;
        //estado de cuentas
        private String concepto;

        public SectionVales(){
            id=0;
            folio="";
            numero="";
            lugar="";
            puntos="";
            valor="";
            estatus="";
            todate_certificado="";
            //historial
            membresia="";
            tipo="";
            //vales
            imagen1 = "";
            estacion="";
            vales="";
            //premio
            premios="";
            dias="";
            //estado de cuenta
            concepto="";

        }

        public SectionVales(int id, String folio, String numero, String lugar, String puntos, String valor, String estatus, String todate_certificado, String membresia, String tipo, String imagen1, String estacion, String vales, String premios, String dias, String concepto){

            this.id=id;
            this.folio=folio;
            this.numero=numero;
            this.lugar=lugar;
            this.puntos=puntos;
            this.valor=valor;
            this.estatus=estatus;
            this.todate_certificado=todate_certificado;
            //historial
            this.membresia=membresia;
            this.tipo=tipo;
            //vales
            this.imagen1=imagen1;
            this.estacion=estacion;
            this.vales=vales;
            //premio
            this.premios=premios;
            this.dias=dias;
            //estado de cuenta
            this.concepto=concepto;

        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getFolio() {
            return folio;
        }

        public void setFolio(String folio) {
            this.folio = folio;
        }

        public String getNumero() {
            return numero;
        }

        public void setNumero(String numero) {
            this.numero = numero;
        }

        public String getLugar() {
            return lugar;
        }

        public void setLugar(String lugar){
            this.lugar = lugar;
        }

        public String getPuntos() {
            return puntos;
        }

        public void setPuntos(String puntos){
            this.puntos = puntos;
        }

        public String getValor() {
            return valor;
        }

        public void setValor(String valor){
            this.valor = valor;
        }

        public String getEstatus() {
            return estatus;
        }

        public void setEstatus(String estatus){
            this.estatus = estatus;
        }

        public String getTodate_certificado() {
            return todate_certificado;
        }

        public void setTodate_certificado(String todate_certificado) {
            this.todate_certificado = todate_certificado;
        }

        public String getMembresia() {
            return membresia;
        }

        public void setMembresia(String membresia){
            this.membresia = membresia;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo){
            this.tipo = tipo;
        }

        public String getImagen1() {
            return imagen1;
        }

        public void setImagen1(String imagen1) {
            this.imagen1 = imagen1;
        }

        public String getEstacion() {
            return estacion;
        }

        public void setEstacion(String estacion){
            this.estacion = estacion;
        }

        public String getVales() {
            return vales;
        }

        public void setVales(String vales){
            this.vales = vales;
        }

        public String getPremios() {
            return premios;
        }

        public void setPremios(String premios){
            this.premios = premios;
        }

        public String getDias() {
            return dias;
        }

        public void setDias(String dias){
            this.dias = dias;
        }

        public String getConcepto() {
            return concepto;
        }

        public void setConcepto(String concepto){
            this.concepto = concepto;
        }

    }
