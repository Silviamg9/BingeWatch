# ✅ Registro de Pruebas Funcionales - BingeWatch Release 1.0

Este documento certifica la validación de las funcionalidades principales de la aplicación, simulando el comportamiento real del usuario y verificando la respuesta correcta del sistema bajo criterios de aceptación formales.

---

## 1. Pruebas de Autenticación y Seguridad

| ID | Acción | Entrada | Resultado Esperado | Resultado Real |
| :--- | :--- | :--- | :--- | :--- |
| **01** | Registro de usuario | Nombre, Email válido, Pass | Creación de cuenta en Firebase Auth y asignación de Rol básico. | **Correcto** |
| **02** | Login por roles (Premium) | Credenciales de Usuario PRO | Redirección inmediata a `MainActivity` con flag de interfaz VIP activo. | **Correcto** |
| **03** | Blindaje de contraseña | "123" | Firebase Auth debe rechazar la transacción por longitud insuficiente (< 6 caracteres). | **Correcto** |
| **04** | Cerrar sesión | Botón Logout | Purga del token de sesión local y vuelta automática a `LoginActivity`. | **Correcto** |

---

## 2. Gestión Operativa de Series y Listas

| ID | Acción | Entrada | Resultado Esperado | Resultado Real |
| :--- | :--- | :--- | :--- | :--- |
| **05** | Cambio de estado de visualización | Selección "Viendo" en Spinner | Persistencia inmediata en Cloud Firestore actualizando el nodo del usuario. | **Correcto** |
| **06** | Ingesta de posters | API Remota -> Glide | Las carátulas deben renderizarse de forma asíncrona y guardarse en caché. | **Correcto** |
| **07** | Contador de progreso | CheckBox de episodio marcado | El sistema calcula dinámicamente el porcentaje de la temporada (`completados / totales`). | **Correcto** |

---

## 3. Simulación de Pasarela Premium y Perfil

| ID | Acción | Entrada | Resultado Esperado | Resultado Real |
| :--- | :--- | :--- | :--- | :--- |
| **08** | Simulación de Pago PRO | Clic en "Suscribirse" (Formulario) | Modificación del atributo `isPremium` a `true` en el documento del usuario. | **Correcto** |
| **09** | Actualización dinámica UI | Evento post-pago | Aparición instantánea del Badge estelar dorado **(PRO)** en el Perfil. | **Correcto** |
| **10** | Filtro idiomático internacional | Cambio de idioma del sistema | Traducción en caliente de todos los textos mediante mapeo de `strings.xml`. | **Correcto** |

---

## 🏆 Resumen de Certificación

Todas las pruebas críticas de caja negra han sido ejecutadas de forma satisfactoria en entornos virtuales con niveles de **API 26 a 35**. El sistema muestra resiliencia ante pérdidas de conectividad y una persistencia en la nube 100% íntegra.
