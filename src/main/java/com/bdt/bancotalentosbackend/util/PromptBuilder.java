package com.bdt.bancotalentosbackend.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PromptBuilder {

  public static String buildSummaryPrompt(String activities, String instructions) {
    return String.format(
        """
            Tu tarea es resumir y mejorar la descripción de funciones laborales de un CV.

            TEXTO ORIGINAL:
            "%s"

            INSTRUCCIONES DEL USUARIO:
            "%s"

            REGLAS (en orden de prioridad):
            1. Si el usuario dio instrucciones específicas, priorízalas sobre las demás reglas.
            2. NUNCA omitas tecnologías, herramientas, plataformas o estándares técnicos mencionados en el texto original (ej: nombres de software, frameworks, protocolos, certificaciones).
            3. Mantén un tono profesional y orientado a logros.
            4. Usa verbos de acción en primera persona (ej. "Lideré", "Desarrollé", "Optimicé").
            5. Elimina frases de relleno, redundancias y palabras vacías sin valor técnico.
            6. El resumen no debe superar las 120 palabras salvo que el usuario indique lo contrario.
            7. Responde ÚNICAMENTE en formato JSON con la clave "summary".

            EJEMPLO DE SALIDA:
            { "summary": "Texto resumido aquí..." }
            """,
        activities, instructions.isBlank() ? "Resumen general profesional" : instructions);
  }

  public String buildCVPrompt(String extractedText) {
    return """
        Eres un asistente especializado en análisis de currículums (CV).
        Tu tarea es extraer la información relevante del siguiente texto extraído de un PDF.
        El texto puede provenir de OCR, por lo que algunas palabras pueden estar incompletas o tener errores.
        Debes reconstruir y corregir lo mínimo necesario para dar coherencia a la información,
        pero sin inventar datos que no estén presentes.

        Responde ÚNICAMENTE en formato JSON con la siguiente estructura exacta
        correspondiente al modelo `IACVResponse` en Java.
        Si no encuentras algún dato, usa `null`.
        No omitas ninguna propiedad.

        # Reglas para la extracción de experiencia laboral:

        Debes retornar las experiencias laborales en orden: Las más recientes primero (Si es actual primero, leugo ordena por fecha fin y finalmente por fecha inicio).

        Para las funciones laborales, trata de no resumirlas, sino de mantener la mayor similitud posible con el texto original, corrigiendo solo errores evidentes de ortografía y gramática. Puedes resumir apartir de la 10ma experiencia laboral, pero siempre manteniendo la esencia de las funciones descritas.

        Para las funciones laborales, incluye TANTO el párrafo descriptivo inicial
        COMO todos los puntos o bullets que aparezcan bajo la experiencia.
        No omitas ningún bullet point. Concaténalos en un solo string separados por salto de línea (\n).

        No elimines las habilidades técnicas que se mencionen en las funcionaes laborales.

        ### Formato de salida esperado (ejemplo con datos ficticios):

        {
          "result": {
            "code": 0,
            "message": "OK"
          },
          "nombres": "Juan Carlos",
          "apellidoPaterno": "Pérez",
          "apellidoMaterno": "García",
          "docIdentidad": "12345678",
          "contacto": {
            "celularNum": "987654321",
            "celularCod": "51",
            "email": "juan.perez@email.com"
          },
          "location": {
            "pais": "Perú",
            "ciudad": "Lima"
          },
          "tecSkills": [
            { "nombreHabilidad": "JAVA", "aniosExperiencia": 5 },
            { "nombreHabilidad": "SPRING BOOT", "aniosExperiencia": 3 }
          ],
          "social": {
            "linkedin": "https://linkedin.com/in/juanperez",
            "github": "https://github.com/juanperez"
          },
          "presentacion": "Profesional con experiencia en desarrollo backend...",
          "softSkills": [
            { "nombreHabilidad": "Trabajo en equipo" },
            { "nombreHabilidad": "Comunicación efectiva" }
          ],
          "workExps": [
            {
              "idExperiencia": null,
              "nombreEmpresa": "TechCorp",
              "puesto": "Desarrollador Backend",
              "funciones": "Desarrollo de microservicios y mantenimiento de APIs.",
              "fechaInicio": "2020-01-15",
              "fechaFin": null,
              "flActualidad": 1
            }
          ],
          "edExps": [
            {
              "idEducacion": null,
              "nombreInstitucion": "Universidad Nacional",
              "carrera": "Ingeniería de Sistemas",
              "grado": "1",
              "fechaInicio": "2015-03-01",
              "fechaFin": "2019-12-15",
              "flActualidad": 0
            }
          ],
          "langs": [
            {
              "idTalentoIdioma": null,
              "idIdioma": null,
              "nombreIdioma": "Inglés",
              "idNivel": 3,
              "nivelIdioma": "AVANZADO",
              "estrellas": 3
            },
            {
              "idTalentoIdioma": null,
              "idIdioma": null,
              "nombreIdioma": "Español",
              "idNivel": 4,
              "nivelIdioma": "NATIVO",
              "estrellas": 4
            }
          ]
        }

        ### Reglas adicionales:
        - Los campos `idExperiencia`, `idEducacion`, `idTalentoIdioma`, `idIdioma` deben ser siempre `null`.
        - `fechaInicio` y `fechaFin` deben estar en formato `yyyy-MM-dd`.
          Si `flActualidad = 1`, entonces `fechaFin = null`.
        - Para idiomas:
          - BASICO = estrellas 1
          - INTERMEDIO = estrellas 2
          - AVANZADO = estrellas 3
          - NATIVO = estrellas 4
        - En langs.idNivel retorna el número 1, 2, 3, o 4 para BASICO, INTERMEDIO, AVANZADO y NATIVO respectivamente
        - En langs.idIdioma retorna el número 1, 2, 3, 4, 5 para ESPAÑOL, INGLES FRANCES, ALEMAN, CHINO respectivamente
        - Para educación: devolver únicamente `grado` como string un número(ej. Bachiller es 1, Título es 2, Curso es 3, Técnico es 4, Egresado es 5, Estudiante es 6).
          Si no es posible identificarlo, poner `null`.
        - El campo `presentacion` debe ser exactamente como aparece en el CV, sin traducir ni reescribir.
        - Si algún dato no puede determinarse, asignar `null`.
        - No devuelvas texto adicional fuera del JSON.
        - En el codigo del celular no debes incluir +
          correcto: 51, incorrecto: +51
          ---

        ### Texto del CV a procesar:
        """
        + extractedText;
  }

  /**
   * Construye el prompt para el "Analizador de Diferencias".
   *
   * A diferencia de {@link #buildCVPrompt(String)}, este prompt NO extrae toda la
   * información del CV. Recibe la información actual del talento (ya almacenada en
   * la base de datos) y el texto del nuevo CV, y le pide a la IA que devuelva
   * ÚNICAMENTE la información nueva, adicional o mejorada respecto a la existente.
   *
   * @param extractedText     texto plano extraído del nuevo CV.
   * @param currentTalentJson estado actual del talento serializado como JSON
   *                          (misma estructura que {@code IACVResponse}).
   * @return prompt listo para enviar a la IA.
   */
  public String buildCVDiffPrompt(String extractedText, String currentTalentJson) {
    return """
        Eres un asistente especializado en actualizar la información de un talento (perfil profesional).
        NO eres un extractor de CV: eres un ANALIZADOR DE DIFERENCIAS.

        Recibirás dos fuentes de información:
        1. INFORMACIÓN ACTUAL DEL TALENTO: lo que ya está almacenado en el sistema (JSON).
        2. NUEVO CV: el texto extraído de un nuevo currículum (puede provenir de OCR y tener errores menores).

        Tu tarea es COMPARAR ambas fuentes y devolver ÚNICAMENTE:
        - Información NUEVA que aparece en el CV y NO existe en la información actual.
        - Información ADICIONAL que enriquece un elemento ya existente.
        - MEJORAS sobre información ya existente (por ejemplo, un puesto más senior o funciones más detalladas).

        REGLA FUNDAMENTAL: NUNCA repitas información que ya existe y es idéntica.
        Si un dato ya está presente en la información actual y el CV no aporta nada nuevo sobre él, NO lo devuelvas.

        # Reglas por sección

        ## Habilidades técnicas (tecSkills)
        - Devuelve SOLO las habilidades técnicas que NO estén ya en la lista actual (comparación sin distinguir mayúsculas/acentos).
        - Ejemplo: si el talento ya tiene [Java, Spring Boot, React] y el CV trae [Java, Spring Boot, React, Docker, Kubernetes],
          debes devolver ÚNICAMENTE [Docker, Kubernetes]. NUNCA repitas Java, Spring Boot ni React.

        ## Habilidades blandas (softSkills) — IGNORAR POR COMPLETO
        - NO analices, extraigas, compares ni infieras habilidades blandas (comunicación, trabajo en equipo,
          liderazgo, adaptabilidad, etc.).
        - Ignora por completo cualquier información relacionada con habilidades blandas presente en el CV.
        - El campo `softSkills` SIEMPRE debe devolverse como lista vacía `[]`.

        ## Ubicación (location) — IGNORAR POR COMPLETO
        - NO extraigas, detectes, normalices ni infieras la ubicación (país/ciudad) del talento a partir del CV.
        - La ubicación en los CV suele ser ambigua o inconsistente (ciudades, países, trabajo remoto o direcciones),
          por lo que NO es un campo confiable para actualización automática.
        - Ignora por completo cualquier información de ubicación presente en el CV.
        - El campo `location` SIEMPRE debe devolverse como `null`.

        ## Experiencia laboral (workExps)
        Cómo IDENTIFICAR y EXTRAER cada experiencia (haz esto SIEMPRE, antes de comparar):
        - En muchos CV cada experiencia empieza con el NOMBRE DE LA EMPRESA como título o viñeta (bullet),
          SIN una etiqueta "Empresa:". Trata cada bloque encabezado por un nombre de empresa como una experiencia
          independiente. La empresa NO es la ciudad ni el país (ej. "Lima, Perú" es ubicación, no empresa).
        - `nombreEmpresa`: el nombre de la empresa (la línea de título/viñeta). Nunca la dejes vacía si el bloque existe.
        - `puesto`: el cargo. Suele venir con prefijos como "Rol:", "Puesto:", "Cargo:" — ELIMINA ese prefijo y guarda
          SOLO el cargo (ej. "Rol: Asesor Comercial" → "Asesor Comercial"; "Rol: Desarrollador FullStack - Gestor de proyectos"
          → "Desarrollador FullStack - Gestor de proyectos").
        - `funciones`: el párrafo descriptivo inicial MÁS todos los bullets que aparezcan bajo la experiencia, concatenados
          con salto de línea (\\n). Mantén la mayor similitud posible con el texto original y NO elimines tecnologías
          mencionadas. Si el bloque no tiene ninguna descripción, usa `null`.
        - fechas: aplica las "Reglas de fechas" de abajo.
        REGLA CRÍTICA: NO descartes una experiencia porque le falten fechas o funciones. Si una experiencia no tiene
          fechas, devuélvela igualmente con `fechaInicio`/`fechaFin` en `null` y `flActualidad = 0`. Es SIEMPRE preferible
          una experiencia con campos en `null` que omitirla por completo.
        Lógica de diferencias (después de extraer):
        - Si el CV describe una experiencia que YA existe (misma empresa y puesto similar), NO la dupliques: MEJÓRALA
          reutilizando su `idExperiencia` de la información actual y combinando/mejorando los campos (puesto más senior,
          funciones más completas, o fechas que antes faltaban). Devuélvela SOLO si aporta algo nuevo.
        - Si es una experiencia completamente NUEVA, devuélvela con `idExperiencia` en `null`.
        - NO devuelvas experiencias existentes que no tengan ningún cambio.

        ## Educación (edExps)
        Cómo IDENTIFICAR y EXTRAER cada estudio (haz esto SIEMPRE, antes de comparar):
        - El nombre de la INSTITUCIÓN suele ser una línea propia (ej. "Universidad Peruana de Ciencias Aplicadas (UPC)").
          Encabezados como "Estudios Superiores:", "Educación" o "Formación" NO son la institución, solo son títulos de sección.
        - `nombreInstitucion`: la universidad/instituto/centro de estudios.
        - `carrera`: la carrera, programa o especialidad (ej. "Ingeniería de Sistemas de Información").
        - Indicadores como "En curso", "Actualidad", "Presente", "Actual", o "X ciclo/semestre" significan estudios en curso
          → `flActualidad = 1` y `fechaFin = null`.
        - `grado`: mapea según las reglas de formato (usa 6=Estudiante cuando el estudio está en curso).
        - fechas: aplica las "Reglas de fechas" de abajo.
        REGLA CRÍTICA: igual que en experiencia, NO descartes un estudio por falta de fechas u otros campos; complétalo con
          `null` donde no haya dato, pero NO lo omitas.
        Lógica de diferencias (después de extraer):
        - Si el estudio YA existe (misma institución y carrera similar), NO lo dupliques: MEJÓRALO reutilizando su
          `idEducacion` de la información actual. Devuélvelo SOLO si aporta algo nuevo.
        - Si es un estudio completamente NUEVO, devuélvelo con `idEducacion` en `null`.
        - NO devuelvas estudios existentes que no tengan ningún cambio.

        ## Reglas de fechas (aplican a workExps y edExps)
        - Formato de salida SIEMPRE `yyyy-MM-dd`.
        - Si el CV solo da el AÑO (ej. "2022"), usa el 1 de enero de ese año: `2022-01-01`.
        - Si da MES y AÑO (ej. "Marzo 2020", "03/2020", "Mar. 2020"), usa el día 01: `2020-03-01`.
        - Palabras como "Actualidad", "Presente", "En curso", "Actual" en la fecha de fin significan que sigue vigente
          → `flActualidad = 1` y `fechaFin = null`.
        - Si el elemento NO tiene ninguna fecha, deja `fechaInicio` y `fechaFin` en `null` y `flActualidad = 0`,
          pero NUNCA omitas el elemento por eso.

        ## Idiomas (langs)
        - Devuelve SOLO idiomas nuevos, o idiomas existentes cuyo nivel MEJORE según el CV.
        - Si mejoras un idioma existente, reutiliza su `idTalentoIdioma` e `idIdioma` de la información actual.

        ## Presentación (presentacion)
        - Devuelve un texto SOLO si el CV aporta una presentación materialmente más completa o mejor que la actual.
        - Si no hay mejora relevante, devuelve `null`.

        ## Datos personales, contacto y redes (nombres, apellidos, contacto, social, docIdentidad)
        - Devuelve un campo SOLO si el CV aporta un valor nuevo o corregido que NO coincide con el actual.
        - Si el dato ya existe y es equivalente, devuelve `null` en ese campo.
        - NO incluyas la ubicación (location): ese campo se ignora por completo (ver sección "Ubicación").

        # Formato de salida

        Responde ÚNICAMENTE con un JSON válido con EXACTAMENTE la misma estructura del modelo `IACVResponse`
        (las mismas claves que la INFORMACIÓN ACTUAL DEL TALENTO). No agregues texto fuera del JSON.
        - Las listas (tecSkills, workExps, edExps, langs) deben contener SOLO los elementos nuevos o mejorados.
          Si no hay nada nuevo en una lista, devuélvela como lista vacía `[]`.
        - El campo `softSkills` SIEMPRE debe ir como lista vacía `[]` (las habilidades blandas se ignoran).
        - El campo `location` SIEMPRE debe ir como `null` (la ubicación se ignora y no debe actualizarse).
        - Los campos escalares que no cambien deben ir en `null`.
        - Respeta las mismas reglas de formato del extractor original:
          - `fechaInicio` y `fechaFin` en formato `yyyy-MM-dd`. Si `flActualidad = 1`, entonces `fechaFin = null`.
          - Idiomas: BASICO=1, INTERMEDIO=2, AVANZADO=3, NATIVO=4 (para `idNivel` y `estrellas`).
          - `idIdioma`: 1=ESPAÑOL, 2=INGLES, 3=FRANCES, 4=ALEMAN, 5=CHINO.
          - Educación `grado` como string numérico (Bachiller=1, Título=2, Curso=3, Técnico=4, Egresado=5, Estudiante=6) o `null`.
          - En el código del celular no incluyas el signo `+` (correcto: 51, incorrecto: +51).
        - Para elementos NUEVOS de listas, los ids (`idExperiencia`, `idEducacion`, `idTalentoIdioma`, `idIdioma`) van en `null`,
          salvo que estés MEJORANDO un elemento existente, en cuyo caso debes reutilizar su id real de la información actual.

        =====================================================================
        INFORMACIÓN ACTUAL DEL TALENTO (JSON):
        """
        + currentTalentJson
        + """

            =====================================================================
            NUEVO CV A COMPARAR:
            """
        + extractedText;
  }

}
