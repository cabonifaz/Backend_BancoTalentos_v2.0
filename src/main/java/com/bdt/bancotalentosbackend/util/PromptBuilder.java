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

}
