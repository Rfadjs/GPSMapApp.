# ✅ Buenas Prácticas de Seguridad - GPSMapApp

Este documento resume las mejores prácticas recomendadas para fortalecer la seguridad de la aplicación Android **GPSMapApp**, según el análisis estático realizado con **Mobile Security Framework (MobSF)**.

---

## 🔒 Firma y compilación
- Firmar la aplicación con un **certificado de producción (release)**, no con el certificado de depuración.  
- Desactivar `android:debuggable` en el `AndroidManifest.xml` para la versión final.  
- Usar **Google Play App Signing** para asegurar la integridad y autenticidad del APK.

---

## 📱 Configuración del AndroidManifest
- Establecer `android:allowBackup="false"` para impedir que los datos sean copiados con ADB.  
- Configurar `android:exported="false"` en actividades, servicios y receptores que no deban ser públicos.  
- Aumentar `minSdkVersion` a **29 o superior (Android 10+)** para garantizar actualizaciones de seguridad.  

---

## 🔑 Manejo de claves y secretos
- No almacenar claves API ni tokens dentro del código fuente.  
- Guardarlas en archivos locales (`local.properties`) o variables de entorno.  
- Rotar las claves periódicamente y restringir su uso en Google Cloud Console.

---

## 🌐 Seguridad de red
- Implementar **Network Security Config** para forzar conexiones HTTPS.  
- Verificar certificados SSL en cada conexión.  
- Evitar el uso de dominios o URLs no confiables.

---

## 🧩 Código y librerías
- Aplicar **ofuscación con ProGuard/R8** para dificultar la ingeniería inversa.  
- Mantener dependencias y SDK actualizados a versiones seguras.  
- Eliminar permisos y librerías que no se utilicen.

---

## 🧠 Privacidad y permisos
- Solicitar solo los permisos estrictamente necesarios.  
- Explicar al usuario por qué se solicita acceso a la ubicación u otros datos.  
- Implementar el principio de **mínimos privilegios** para cada componente.

---

## 📘 Conclusión
Aplicando estas prácticas, **GPSMapApp** puede mejorar significativamente su nivel de seguridad, reducir vulnerabilidades y cumplir con los estándares recomendados por **OWASP Mobile Top 10** y **Google Play Security Guidelines**.
