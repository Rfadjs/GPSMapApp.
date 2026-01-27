# 💡 Consejos de Seguridad - GPSMapApp

Consejos y recomendaciones prácticas para mantener la seguridad durante todo el ciclo de vida de desarrollo de **GPSMapApp**.

---

## 🧰 Durante el desarrollo
- Nunca incluir claves API, contraseñas o tokens en el repositorio de código.  
- Añadir un archivo `.gitignore` para excluir datos sensibles.  
- Revisar el `AndroidManifest.xml` antes de compilar, asegurando que `android:debuggable` esté en `false`.

---

## 🔐 En la configuración del proyecto
- Desactivar el tráfico sin cifrar con:  
  ```xml
  android:usesCleartextTraffic="false"
