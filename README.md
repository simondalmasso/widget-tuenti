# Tuenti Odómetro

Widget Android para consultar el consumo del combo de datos de Tuenti Argentina desde la pantalla de inicio.

## Estado del proyecto

- App oficial observada: `com.tuentiargentina`.
- Método de lectura en desarrollo: servicio Android Accessibility restringido a la app oficial.
- No se solicitan credenciales ni se implementa un segundo inicio de sesión dentro del widget.
- Interfaz objetivo: medidor circular/donut de consumo, sin textos pequeños; muestra GB usados y total (o GB restantes, según el dato real disponible).
- Tamaño del widget: se conserva el formato compacto existente (75 × 42 dp mínimos).

## Limitaciones importantes

Android aísla el almacenamiento privado de cada aplicación. Un widget de terceros no puede leer directamente las preferencias o bases privadas de Tuenti salvo que Tuenti publique una API o proveedor compartido. La captura de pantalla de otra app tampoco es una solución silenciosa: requiere consentimiento del usuario mediante MediaProjection y una sesión de captura activa; no es apropiada para un widget persistente en segundo plano.

La ruta menos invasiva es leer los textos que la app oficial expone en su árbol de accesibilidad mientras el usuario abre la pantalla de consumos. La captura debe validarse contra la interfaz real de la versión instalada, no solo con ejemplos sintéticos.

## Requisitos

- Android 6.0 o superior (minSdk según `app/build.gradle`).
- Android Studio o JDK + Android SDK.
- Para lectura: habilitar manualmente el servicio de accesibilidad de Tuenti Odómetro en Ajustes de Android y abrir Tuenti en la pantalla de consumos.

## Build

```bash
gradle testDebugUnitTest assembleDebug
```

En Windows:

```bat
gradle testDebugUnitTest assembleDebug
```

APK debug: `app/build/outputs/apk/debug/app-debug.apk`.

## Privacidad

El proyecto no debe almacenar contraseñas, tokens de sesión ni contenido ajeno a los datos de consumo. El servicio debe filtrar estrictamente el paquete `com.tuentiargentina` y guardar solo los valores de consumo necesarios para actualizar el widget. La caché usa preferencias privadas de esta app; no almacena credenciales, aunque todavía no cifra esos valores.

## Referencias

- App Tuenti Argentina: `com.tuentiargentina`
- Proyecto histórico de referencia: [wilcome/UnofficialTuentiWidget](https://github.com/wilcome/UnofficialTuentiWidget) (código legado; no asumir compatibilidad con la app actual).
- [Android AccessibilityService](https://developer.android.com/reference/android/accessibilityservice/AccessibilityService)
- [Android MediaProjection](https://developer.android.com/reference/android/media/projection/MediaProjection)

## Licencia

Por definir. Revisar licencias de cualquier código externo antes de incorporarlo.
