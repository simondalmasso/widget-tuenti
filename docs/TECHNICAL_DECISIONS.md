# Decisiones técnicas

## Fuente de datos
La app oficial Tuenti usa el paquete `com.tuentiargentina`. Android aísla el almacenamiento privado entre aplicaciones: este widget no puede abrir sus bases SQLite, preferencias ni archivos privados sin root, cooperación explícita de Tuenti o una interfaz pública.

## Lectura elegida
AccessibilityService limitado a `com.tuentiargentina`, leyendo solo nodos visibles de la pantalla de consumos. El usuario debe habilitar el servicio y abrir la sección Consumos de la app oficial. Se cachean únicamente bytes disponibles, bytes iniciales y marca temporal.

## Por qué no capturar pantallas en segundo plano
MediaProjection exige consentimiento del usuario mediante un diálogo del sistema y una sesión de captura activa. No permite que un widget obtenga capturas silenciosas permanentes de otra app. Además, OCR sobre capturas es menos fiable y más invasivo que leer el árbol de accesibilidad cuando la app expone texto semántico.

## Visualización
La vista compacta usa un bitmap generado localmente por el proveedor del widget: arco circular de progreso y cifras grandes. No depende de red ni imágenes remotas. El tamaño mínimo compacto se conserva.

## Riesgos abiertos
La jerarquía real de Tuenti debe comprobarse con la app abierta en Consumos. Los tests sintéticos no demuestran que la versión instalada exponga esos textos a Accesibilidad.
