# 🛡️ Vulnerabilidades Detectadas - GPSMapApp

**Aplicación:** GPSMapApp (com.example.gpsmapapp)  
**Tipo de análisis:** Estático con MobSF  
**Fecha:** 26/10/2025  
**Resultado:** Riesgo medio (46 / 100)

---

## 🔴 Principales vulnerabilidades

### 1️⃣ Aplicación firmada con certificado de depuración
- **Severidad:** Alta  
- **Descripción:** El APK fue firmado con el certificado de depuración (`CN=Android Debug`).  
- **Riesgo:** Puede ser modificado o reempaquetado fácilmente.  
- **Corrección:** Generar un certificado de **release** con `keytool` y firmar la app antes de distribuirla.

---

### 2️⃣ Modo depuración habilitado
- **Severidad:** Alta  
- **Descripción:** En el `AndroidManifest.xml` se detectó `android:debuggable="true"`.  
- **Riesgo:** Permite a atacantes conectar depuradores o manipular el código en ejecución.  
- **Corrección:** Deshabilitar el modo depuración para versiones de producción.

```xml
<application android:debuggable="false" ... />
