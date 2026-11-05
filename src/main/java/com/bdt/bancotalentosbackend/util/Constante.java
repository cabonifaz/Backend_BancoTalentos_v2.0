package com.bdt.bancotalentosbackend.util;

import java.util.Set;

public class Constante {
    // FUNCIONALIDADES
    public static final String ACTUALIZAR_TALENTO = "12";
    public static final String LISTAR_TALENTOS = "13";
    public static final String ACTUALIZAR_USUARIO = "1040";
    // La 1041 no existe en FUNCIONALIDADES: la lista negra usa la misma que ACTUALIZAR_USUARIO.
    public static final String LISTA_NEGRA = "1040";
    public static final String MANEJO_PARAMETROS = "2042";
    public static final String MANEJO_CLIENTES = "2043";
    public static final String MANEJO_USUARIOS = "2044";
    public static final String MANEJO_GESTORES = "2045";
    public static final String MANEJO_TARIFARIO = "2047";

    // Entrevistas (migradas de AutFMI)
    public static final String CREATE_INTERVIEW = "1030";
    public static final String UPDATE_INTERVIEW = "1031";
    public static final String VIEW_INTERVIEW = "1032";
    public static final String LIST_INTERVIEW = "1033";
    public static final String UPLOAD_DOWNLOAD_INTERVIEW_FILE = "1034";

    // Módulo Selección (migrado de AutFMI): estadísticas de entrevistas/ingresos
    public static final String SELECCION_ESTADISTICAS = "2049";
    // Búsqueda de usuarios de selección (desglose por-usuario). Solo Admin (rol 1).
    public static final String SELECCION_USUARIOS = "2050";

    // ID_MAESTROS
    public static final String TIPO_ENTREVISTA = "47";
    // Fila por evento de correo: STRING1 = clave del evento, NUM1 = 1 envía / 0 no.
    public static final String MAESTRO_NOTIFICACION_CORREO = "56";

    // TIPO DE ENTREVISTA (valores string1 del maestro 47)
    public static final String TIPO_ENTREVISTA_PRESENCIAL = "PRESENCIAL";
    // Sin tilde a propósito: la comparación normaliza el texto del maestro, que
    // puede venir como "TELEFÓNICA".
    public static final String TIPO_ENTREVISTA_TELEFONICA = "TELEFONICA";

    // TIPO DE ARCHIVO DE ENTREVISTA (maestro 45). El ICS es generado por el sistema.
    public static final int TIPO_ARCHIVO_ENTREVISTA_ICS = 4;

    public static final String TXT_SEPARADOR = "=========================================";

    // RUTAS BASE DE REPOSITORIO
    public static final String RUTA_REPOSITORIO_FOTO_TALENTO = "repositorio/talento/[ID]/";
    public static final String RUTA_REPOSITORIO_CV_TALENTO = "repositorio/talento/[ID]/CV/";
    public static final String RUTA_REPOSITORIO_TALENTO_ARCHIVOS = "repositorio/talento/[ID]/archivos/";
    public static final String RUTA_REPOSITORIO_CV_EN_TALENTO = "repositorio/talento/[ID]/CV/EN/";
    public static final String RUTA_REPOSITORIO_CV_ES_TALENTO = "repositorio/talento/[ID]/CV/ES/";
    public static final String RUTA_REPOSITORIO_FIRMA_USUARIO = "repositorio/usuario/[ID]/firma/";
    // Entrevistas: RUTA_REPOSITORIO + idEmpresa + RUTA_INTERVIEW (misma ruta que usaba AutFMI)
    public static final String RUTA_REPOSITORIO = "repositorio/";
    public static final String RUTA_INTERVIEW = "/interviews/[ID_INTERVIEW]/archivos/";

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
