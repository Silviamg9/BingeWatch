# 🎬 BingeWatch - Release 1.0

[![Versión](https://img.shields.io/badge/VERSIÓN-1.0_RELEASE-00E676?style=for-the-badge&logo=android&logoColor=white)](https://github.com/Silviamg9/BingeWatch)
[![Platform](https://img.shields.io/badge/ANDROID-KOTLIN%20%2F%20JAVA-00E676?style=for-the-badge&logo=android&logoColor=white)](https://github.com/Silviamg9/BingeWatch)
[![SDK](https://img.shields.io/badge/SDK_MIN-26_--_35-00E676?style=for-the-badge)](https://github.com/Silviamg9/BingeWatch)

**BingeWatch** es una plataforma móvil integral diseñada para los amantes del contenido multimedia, permitiendo centralizar, organizar y realizar un seguimiento exhaustivo de series y películas en tiempo real. La aplicación combina un diseño de interfaz inmersivo en modo oscuro con la robustez de una arquitectura moderna en la nube 🍿 ✨ 🎬.

---

## 🎯 Propósito del Proyecto

En el panorama actual, los usuarios consumen contenido en multitud de plataformas de streaming de forma fragmentada, perdiendo el control de los episodios vistos o las películas pendientes. **BingeWatch** soluciona esta problemática ofreciendo un panel de control único donde catalogar el estado de cada producción ("Sin Comenzar", "Viendo", "Terminada", "Abandonada"), consultar sinopsis detalladas, visualizar trailers oficiales y simular una pasarela de pago VIP para obtener el distintivo estelar de la comunidad.

---

## 🚀 Características Innovadoras

* **Interfaz Inmersiva "Cinema Dark":** Diseño optimizado en tonos oscuros con colapsado dinámico de barras de herramientas (`CollapsingToolbarLayout`) que resalta las carátulas y fondos artísticos del contenido multimedia.
* **Internacionalización Nativa Completa (i18n):** Adaptabilidad idiomática total mediante recursos estructurados (`strings.xml`), permitiendo alternar de forma transparente entre **Español** e **Inglés** según la configuración del sistema.
* **Sincronización de Estados Dinámica:** Control exhaustivo de progreso por temporadas y episodios interactivos con componentes de selección inteligente (`Spinner`, `CheckBox`) vinculados al flujo del perfil.
* **Pasarela de Simulación Premium 🔒:** Módulo interactivo embebido que simula un entorno seguro de suscripción económica para desbloquear ventajas estéticas avanzadas y el badge **PRO** dentro de la app.
* **Consumo de Datos Atómico:** Integración fluida con bases de datos y APIs externas para la renderización asíncrona de títulos, logotipos de plataformas disponibles y transmisiones de vídeo.

---

## 👥 Roles de Usuario

### 👤 Usuario Estándar (Gratuito)
* Acceso completo al buscador avanzado de series y películas.
* Gestión básica de sus listas de seguimiento personales en pestañas dedicadas.
* Visualización de fichas de información, trailers y sinopsis de producciones.

### ✨ Usuario Premium (BingeWatch PRO)
* Todas las funciones del nivel estándar.
* Desbloqueo del distintivo estelar VIP **(PRO)** junto a su nombre en el perfil.
* Acceso prioritario simulado a métricas avanzadas de consumo (contador analítico de minutos y episodios totales consumidos).

---

## 🛠️ Tecnologías Utilizadas

| Componente | Tecnología / Librería | Propósito |
| :--- | :--- | :--- |
| **Lenguaje Core** | Java / Kotlin | Lógica de negocio y control de actividades. |
| **Diseño UI/UX** | Material Design Component | `TabLayout`, `CardView`, `RecyclerView`, `CoordinatorLayout`. |
| **Persistencia** | Firebase Client / API Rest | Gestión de identidades, almacenamiento de perfiles y consumo de datos. |
| **Conectividad** | Retrofit2 / Glide | Peticiones HTTP asíncronas y renderizado eficiente de imágenes en caché. |

---

## 🏗️ Arquitectura del Sistema

El proyecto sigue una **Arquitectura en Capas** enfocada en la separación de responsabilidades y la modularidad de componentes:

* **Capa de Modelos (`modelo`):** Entidades estructuradas (POJOs) y Enums que blindan los datos de los usuarios, producciones y su progreso.
* **Capa de Presentación (`vista` / `adaptador`):** Interfaces de usuario (`Activities` y `Fragments`) optimizadas con Material Design y adaptadores dinámicos para el reciclaje eficiente de vistas.
* **Capa de Conectividad (`api`):** Configuración del cliente HTTP asíncrono y mapeo de endpoints remotos para la ingesta de datos multimedia.

---

## 📂 Estructura del Proyecto

```text
com.example.bingewatch
├── 📂 adaptador/      # Enlace y gestión de listas (RecyclerView Adapters)
├── 📂 api/            # Clientes de API REST y configuración de red (Retrofit)
├── 📂 modelo/         # Modelos de datos estructurados y Enumeraciones (Enums)
└── 📂 vista/          # Lógica operativa de UI (Login, Registro, Explorar, Perfil)
