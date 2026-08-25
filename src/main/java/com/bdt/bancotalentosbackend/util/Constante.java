package com.bdt.bancotalentosbackend.util;

import java.util.Set;

public class Constante {
    // FUNCIONALIDADES
    public static final String ACTUALIZAR_TALENTO = "12";
    public static final String LISTAR_TALENTOS = "13";
    public static final String ACTUALIZAR_USUARIO = "1040";
    public static final String LISTA_NEGRA = "1041";
    public static final String MANEJO_PARAMETROS = "2042";
    public static final String MANEJO_CLIENTES = "2043";
    public static final String MANEJO_USUARIOS = "2044";
    public static final String MANEJO_GESTORES = "2045";
    public static final String MANEJO_TARIFARIO = "2047";

    public static final String TXT_SEPARADOR = "=========================================";

    // RUTAS BASE DE REPOSITORIO
    public static final String RUTA_REPOSITORIO_FOTO_TALENTO = "repositorio/talento/[ID]/";
    public static final String RUTA_REPOSITORIO_CV_TALENTO = "repositorio/talento/[ID]/CV/";
    public static final String RUTA_REPOSITORIO_TALENTO_ARCHIVOS = "repositorio/talento/[ID]/archivos/";
    public static final String RUTA_REPOSITORIO_CV_EN_TALENTO = "repositorio/talento/[ID]/CV/EN/";
    public static final String RUTA_REPOSITORIO_CV_ES_TALENTO = "repositorio/talento/[ID]/CV/ES/";
    public static final String RUTA_REPOSITORIO_FIRMA_USUARIO = "repositorio/usuario/[ID]/firma/";

    // VALIDACIÓN DE ARCHIVOS DE TALENTO
    // Misma whitelist que EXT_ARCHIVO_POSTULANTE en FMI, sin zip (el talento no
    // admite comprimidos). Debe coincidir con TALENT_ALLOWED_EXTENSIONS del front.
    public static final Set<String> EXT_ARCHIVO_TALENTO = Set.of(
            "pdf", "doc", "docx", "xls", "xlsx", "png", "jpg", "jpeg", "webp");
    // La foto de perfil es sólo imagen.
    public static final Set<String> EXT_FOTO_TALENTO = Set.of("png", "jpg", "jpeg");
    public static final long MAX_TAMANIO_ARCHIVO_TALENTO = 10L * 1024 * 1024; // 10 MB

    // CONFIG LINK TOKEN JWT
    public static final long TIEMPO_EXPIRACION = 172800000L;
    public static final String URL_PART_FRONT_FORM_POSTULANT = "/#/formPostulante?token=";
}
