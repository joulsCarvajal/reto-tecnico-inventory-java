## RETO TÉCNICO

```
En el presente documento encontrará los detalles de la prueba técnica. La prueba deberá ser resuelta y
entregada en la fecha enviada a tu correo. Toda prueba entregada posterior a dicha fecha no será válida.
```
**Objetivo de la prueba:**

```
Validar tus conocimientos y habilidades en el desarrollo de aplicaciones con Java , Spring Boot , Hibernate ,
Quasar Framework, Vue.js y PostgreSQL.
```
```
Adicionalmente, se espera la aplicación de buenas prácticas de ingeniería de software, incluyendo principios
SOLID , Clean Architecture , Clean Code , desarrollo asistido por Inteligencia Artificial , pruebas unitarias ,
documentación técnica y patrones arquitectónicos que contribuyan a la construcción de soluciones
modernas, escalables y mantenibles.
```
```
Descripción de la prueba: Construir una aplicación que exponga las siguientes vistas:
```
```
a) Vista Empresa con un formulario que capture la siguiente información:
```
- NIT (Llave primaria).
- Nombre de la empresa.
- Dirección.
- Teléfono.

```
b) Vista de Productos con un formulario que captura la siguiente información:
```
- Código.
- Nombre del producto.
- Características.
- Precio en varias monedas.
- Empresa.

```
c) Vista de Inicio de Sesión con un formulario que capture la información del usuario: correo y contraseña.
```
```
d) Vista de Inventario con un formulario que permita la descargar de un PDF con la información de esa
tabla y adicional utilizar alguna API REST o SOAP para poder enviar ese PDF a un correo deseado.
```
```
e) Deben existir dos tipos de usuarios:
```
**_-_** **Administrador:** Tiene acceso a las funciones de eliminación, registro y/o edición de una Empresa.
    Adicionalmente, este usuario podrá registrar productos por empresa y guardarlos en una tabla
    inventario, donde se vean los productos por empresa.
**_-_** **Externo:** Puede visualizar las empresas como visitante.


```
f) El modelo entidad-relación de la base de datos para guardar la información anterior debe contener:
Empresa , Productos , Categorías , Clientes y Ordenes. Asegúrate que la Base de Datos que plantees cumpla
con los siguientes requisitos:
```
- Un Producto puede pertenecer a múltiples Categorías.
- Un Cliente puede tener múltiples Órdenes.
- Las ordenes pueden tener múltiples Productos.

```
g) La contraseña utilizada debe estar encriptada para autenticación del Usuario Administrador.
```
**Entregables:**

```
Entregar la siguiente información junto con la solución desarrollada:
```
- Credenciales de acceso (usuario y contraseña) para los perfiles Administrador y Externo.
- Enlace al repositorio donde se encuentra almacenado el código fuente.
- Presentación funcional del proyecto en ambiente local o enlace de la aplicación desplegada en AWS o
    GCP.
- Archivo README con la documentación completa del proyecto, incluyendo instrucciones de instalación,
    configuración, ejecución, despliegue y cualquier consideración técnica relevante.

```
Nota:
```
```
La funcionalidad de la aplicación es completamente abierta a tu análisis, criterio y diseño, ya que parte
del objetivo de esta prueba es evaluar tu capacidad para definir e implementar una solución adecuada.
Por lo tanto, eres libre de establecer la forma en que se capturará, procesará y presentará la
información.
```
```
Adicionalmente, se espera la aplicación de buenas prácticas de ingeniería y arquitectura de software
para el desarrollo de aplicaciones web modernas, incluyendo aspectos relacionados con diseño,
mantenibilidad, escalabilidad, seguridad, calidad de código, experiencia de usuario y documentación
técnica.
```
```
Se permite y fomenta el uso de herramientas de Inteligencia Artificial para acelerar el proceso de
desarrollo. Sin embargo, el uso de estas herramientas deberá estar acompañado de rigor técnico y
criterio profesional, garantizando la calidad de la solución, la comprensión del código implementado, la
correcta aplicación de patrones y buenas prácticas de ingeniería de software, así como la capacidad
de justificar las decisiones arquitectónicas y técnicas adoptadas.
```
```
Toda la información registrada a través de los formularios deberá almacenarse de manera persistente
en una base de datos.
```

