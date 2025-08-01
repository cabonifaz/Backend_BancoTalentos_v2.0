package com.bdt.bancotalentosbackend.util;

public class Constante {
    // FUNCIONALIDADES
    public static final String ACTUALIZAR_TALENTO = "12";
    public static final String LISTAR_TALENTOS = "13";

    public static final String TXT_SEPARADOR = "=========================================";


    // RUTAS BASE DE REPOSITORIO
    public static final String RUTA_REPOSITORIO_FOTO_TALENTO = "repositorio/talento/[ID]/";
    public static final String RUTA_REPOSITORIO_CV_TALENTO = "repositorio/talento/[ID]/CV/";
    public static final String RUTA_REPOSITORIO_TALENTO_ARCHIVOS = "repositorio/talento/[ID]/archivos/";

    // CONFIG LINK TOKEN JWT
    public static final long TIEMPO_EXPIRACION = 172800000L;
    public static final String URL_PART_FRONT_FORM_POSTULANT = "/#/formPostulante?token=";
}
