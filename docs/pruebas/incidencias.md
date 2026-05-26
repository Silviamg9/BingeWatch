# 🐞 Registro de Incidencias - BingeWatch Release 1.0

Este documento detalla los retos técnicos y errores críticos identificados durante el ciclo de desarrollo, pruebas y refinamiento de la aplicación, así como las soluciones de ingeniería aplicadas para garantizar la estabilidad final del sistema.

---

## 1. Gestión de Interfaz y Usabilidad (UX/UI)

| Incidencia | Descripción | Gravedad | Resolución |
| :--- | :--- | :--- | :--- |
| **I-01: Obstrucción por Teclado** | En los campos de Login y Registro, el teclado virtual de Android tapaba los botones de acción principales al desplegarse. | **Crítica** | Implementación de `android:windowSoftInputMode="adjustResize"` en el Manifiesto y anidación de elementos en un `ScrollView`. |
| **I-02: Desborde de Carátulas** | En pantallas de alta densidad (pantallas grandes), las imágenes escaladas con Glide distorsionaban el ratio de aspecto de los posters. | **Alta** | Reconfiguración del atributo de escalado a `android:scaleType="centerCrop"` en las tarjetas dinámicas del `RecyclerView`. |

---

## 2. Persistencia y Backend (Firebase Firestore)

| Incidencia | Descripción | Gravedad | Resolución |
| :--- | :--- | :--- | :--- |
| **I-03: Latencia en Sincronización** | Al cambiar el estado de una serie (ej. de "Viendo" a "Terminada"), la UI tardaba varios segundos en reflejar el cambio. | **Alta** | Migración de peticiones imperativas únicas a listeners en tiempo real usando el método `.addSnapshotListener()` de Firestore. |
| **I-04: Conflicto de Tipos** | Crasheos esporádicos al intentar leer registros antiguos donde el contador de capítulos guardaba datos nulos. | **Muy Alta** | Implementación de una lógica de control con operadores "Safe Getters" de Kotlin para asignar valores por defecto (`0`) si venían nulos. |

---

## 3. Lógica de Negocio y Seguridad

| Incidencia | Descripción | Gravedad | Resolución |
| :--- | :--- | :--- | :--- |
| **I-05: Bucle en Pasarela Premium** | El estado VIP no persistía de forma correcta si el usuario cerraba la aplicación abruptamente durante la simulación de pago. | **Crítica** | Implementación de bloques transaccionales atómicos en Firestore, asegurando que el flag `isPremium` se confirme antes de cerrar la vista. |
| **I-06: Fuga de Textos Hardcoded** | El sistema de traducción (i18n) fallaba en ciertas alertas emergentes (`Toast`), mostrando mensajes estáticos únicamente en español. | **Baja** | Extracción exhaustiva de cadenas literales del código fuente y su debida migración al archivo indexado centralizado `strings.xml`. |

---

## 📘 Conclusión Técnica

El proceso de control de errores ha permitido robustecer la arquitectura de **BingeWatch**. La combinación de un entorno asíncrono controlado para la carga de datos de red junto con la gestión transaccional segura en la nube de Firebase garantiza que la aplicación sea estable, reactiva y esté lista para entornos de producción reales.
