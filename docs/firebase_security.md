# Firebase y claves en Google Cloud

Pasos manuales que debes aplicar en [Google Cloud Console](https://console.cloud.google.com/) y en el proyecto Firebase. El código del repo no puede sustituirlos.

## Restringir la API key de Android

1. Abre **Google Cloud Console** → **APIs & Services** → **Credentials**.
2. Localiza la clave usada por la app Android (la misma que aparece como `current_key` en `google-services.json` cuando descargas el archivo desde Firebase).
3. Edita la credencial:
   - **Application restrictions**: elige **Android apps**.
   - Añade el nombre del paquete (`com.example.disneycast` o el que uses en `applicationId`) y el **SHA-1** del keystore de **debug** y del keystore de **release** (Play App Signing incluye un SHA-1 distinto al del upload key).
4. En **API restrictions**: elige **Restrict key** y limita solo a las APIs que Firebase Analytics necesita para tu proyecto (por ejemplo APIs de Firebase/Google Analytics según lo que tenga habilitado tu proyecto).

Así reduces el abuso de cuotas si la clave se copia desde un APK o desde un repositorio.

## Rotar la clave si el repositorio fue o será público

1. En **Credentials**, crea una **nueva** API key con las mismas restricciones Android + APIs que arriba.
2. En **Firebase Console** → configuración del proyecto Android, vuelve a descargar `google-services.json` cuando el nuevo archivo refleje la clave nueva (o sustituye la clave en Firebase según el flujo actual del producto).
3. Coloca el archivo **solo en local** o en secretos de CI (véase `.gitignore` y `app/google-services.json.example`).
4. En Cloud Console, **desactiva o borra** la clave antigua cuando confirmes que las builds usan la nueva.

## Publicación en Play y privacidad

Firebase Analytics puede implicar declaración de recolección de datos en Play Console y una política de privacidad si la app está en producción. Alinea esto con el alcance del blueprint del proyecto.

## Archivos en el repo

- **`app/google-services.json`**: no debe estar en Git; cada entorno lo obtiene desde Firebase Console o CI (secretos). Plantilla: [`app/google-services.json.example`](../app/google-services.json.example).
