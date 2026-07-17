package com.bdt.bancotalentosbackend.util;

public class Constante {
    // FUNCIONALIDADES
    public static final String ACTUALIZAR_TALENTO = "12";
    public static final String LISTAR_TALENTOS = "13";
    public static final String ACTUALIZAR_USUARIO = "1040";
    public static final String LISTA_NEGRA = "1041";

    public static final String TXT_SEPARADOR = "=========================================";

    // RUTAS BASE DE REPOSITORIO
    public static final String RUTA_REPOSITORIO_FOTO_TALENTO = "repositorio/talento/[ID]/";
    public static final String RUTA_REPOSITORIO_CV_TALENTO = "repositorio/talento/[ID]/CV/";
    public static final String RUTA_REPOSITORIO_TALENTO_ARCHIVOS = "repositorio/talento/[ID]/archivos/";
    public static final String RUTA_REPOSITORIO_CV_EN_TALENTO = "repositorio/talento/[ID]/CV/EN/";
    public static final String RUTA_REPOSITORIO_CV_ES_TALENTO = "repositorio/talento/[ID]/CV/ES/";

    // CONFIG LINK TOKEN JWT
    public static final long TIEMPO_EXPIRACION = 172800000L;
    public static final String URL_PART_FRONT_FORM_POSTULANT = "/#/formPostulante?token=";
}
