# Autorregulación de Acceso a Zona de Exclusión Mutua - Algoritmo Ricart y Agrawala, Algoritmo NTP

Este proyecto implementa un conjunto de procesos distribuidos para autorregular el acceso a una zona de exclusión mutua. La implementación se basa en el algoritmo de Ricart y Agrawala, con corrección de tiempos mediante el algoritmo NTP.

## Ejecución del Proyecto
Para ejecutar el proyecto desde un único nodo, sigue los siguientes pasos:

1. Descarga todo el proyecto en una carpeta vacía
2. Comprime todos los archivos en una nueva carpeta
3. Modifica las rutas necesarias del script `lanzar.sh`
3. Da permisos de ejecución a los servidores de apache y también a los scripts `.sh`
4. Ejecuta el script `lanzar.sh` para iniciar los servidores y clientes en los nodos de ejecución. El script necesita 4 parámetros:
- Nombre de la carpeta donde se encuentran los archivos.
- Dirección IP local.
- Dirección IP del primer nodo remoto.
- Dirección IP del segundo nodo remoto.
5. Para comprobar su correcta ejecución, ejecuta el script `comprobar.sh` . Este script se necesita de 8 parámetros
- Los cuatro primeros son iguales que los del script `lanzar.sh`: el nombre de la carpeta donde se encuentran los archivos (también tiene que haber en esa ubicación el zip correspondiente a esa carpeta con el mismo nombre) y las 3 direcciones ip de los dispositivos siendo primera la local. 
- Los otros cuatro corresponden a los offsets y delays: primero el offset y delay de la primera máquina remota que se pasa como parámetro y luego el offset y delay de la otra máquina remota.



