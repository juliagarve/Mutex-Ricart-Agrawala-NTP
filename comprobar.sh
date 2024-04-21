#usage comprobar nombre iplocal ipmaquina2 ipmaquina3 offset1 delay1 offset2 delay2

if [ $# -eq 8 ]
then
	nombre=$1
	host1=$2
	host2=$3
	host3=$4
	offset1=$5
	delay1=$6
	offset2=$7
	delay2=$8
	
	scp i0961775@$host2:/home/i0961775/Descargas/$nombre/log.txt /home/i0961775/Descargas/$nombre/log1.txt

	scp i0961775@$host3:/home/i0961775/Descargas/$nombre/log.txt /home/i0961775/Descargas/$nombre/log2.txt

	javac CorregirTiempos.java 
	
	java CorregirTiempos $nombre log1.txt $offset1

	java CorregirTiempos $nombre log2.txt $offset2

	cat log.txt >> fusion.txt
	cat corregidolog1.txt >> fusion.txt
	cat corregidolog2.txt >> fusion.txt 

	sort -k 3 fusion.txt >> logTOTAL.txt

	javac Comprobador.java -Xlint

	java Comprobador logTOTAL.txt $delay1 $delay2

	echo "Paro los servidores en local"
	cd ~
	./Descargas/$nombre/apache-tomcat-9.0.72/bin/catalina.sh stop 
	./Descargas/$nombre/apache-tomcat-9.0.72_copia/bin/catalina.sh stop

	echo "Paro los servidores en maquina remota 1"
	ssh $host2 "./Descargas/$nombre/apache-tomcat-9.0.72/bin/catalina.sh stop"
	ssh $host2 "./Descargas/$nombre/apache-tomcat-9.0.72_copia/bin/catalina.sh stop"

	echo "Paro los servidores en maquina remota 2"
	ssh $host3 "./Descargas/$nombre/apache-tomcat-9.0.72/bin/catalina.sh stop"
	ssh $host3 "./Descargas/$nombre/apache-tomcat-9.0.72_copia/bin/catalina.sh stop"

else
	echo "Uso: $0 usuario máquina"
	exit
fi
