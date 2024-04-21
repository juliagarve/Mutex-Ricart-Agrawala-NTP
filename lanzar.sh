#usage lanzar nombreCarpetaEnDescargas iplocal ipmaquina2 ipmaquina3
#p.ej. lanzar practicaObligatoria 128.34.5.1 128.34.5.2 128.34.5.3

if [ $# -eq 4 ]
then
	nombre=$1
	host1=$2
	host2=$3
	host3=$4

	cd ~
	
	ssh-keygen -t rsa

	echo "Setting public and private keys on $host2"
	ssh $host2 mkdir -p .ssh
	cat .ssh/id_rsa.pub | ssh $host2 'cat >> .ssh/authorized_keys'

	echo "Setting public and private keys on $host3"
	ssh $host3 mkdir -p .ssh
	cat .ssh/id_rsa.pub | ssh $host3 'cat >> .ssh/authorized_keys'

	
	#comenzar el agente ssh
	echo "Starting SSH agent"
	eval `ssh-agent`
	
	#agnadir la clave privada a la cachE
	echo "Adding private key to cache"
	ssh-add

	cd ~
	cd Descargas

	scp $nombre.zip i0961775@$host2:/home/i0961775/Descargas
	scp $nombre.zip i0961775@$host3:/home/i0961775/Descargas

	ssh $host2 "cd Descargas; unzip $nombre.zip"
	ssh $host3 "cd Descargas; unzip $nombre.zip"

	



	#echo "Paro los servidores en local"
	#cd ~
	#./Descargas/$nombre/apache-tomcat-9.0.72/bin/catalina.sh stop 
	#./Descargas/$nombre/apache-tomcat-9.0.72_copia/bin/catalina.sh stop

	#echo "Paro los servidores en maquina remota 1"
	#ssh $host2 "./Descargas/$nombre/apache-tomcat-9.0.72/bin/catalina.sh stop"
	#ssh $host2 "./Descargas/$nombre/apache-tomcat-9.0.72_copia/bin/catalina.sh stop"

	#echo "Paro los servidores en maquina remota 2"
	#ssh $host3 "./Descargas/$nombre/apache-tomcat-9.0.72/bin/catalina.sh stop"
	#ssh $host3 "./Descargas/$nombre/apache-tomcat-9.0.72_copia/bin/catalina.sh stop"





	echo "Lanzo los servidores en local"
	cd ~
	./Descargas/$nombre/apache-tomcat-9.0.72/bin/catalina.sh start 
	./Descargas/$nombre/apache-tomcat-9.0.72_copia/bin/catalina.sh start

	echo "Lanzo los servidores en maquina remota 1"
	ssh $host2 "./Descargas/$nombre/apache-tomcat-9.0.72/bin/catalina.sh start"
	ssh $host2 "./Descargas/$nombre/apache-tomcat-9.0.72_copia/bin/catalina.sh start"

	echo "Lanzo los servidores en maquina remota 2"
	ssh $host3 "./Descargas/$nombre/apache-tomcat-9.0.72/bin/catalina.sh start"
	ssh $host3 "./Descargas/$nombre/apache-tomcat-9.0.72_copia/bin/catalina.sh start"


	echo "lanzamos cliente5 en remoto"
	
	ssh $host3 "cd ~/Descargas/$nombre; java -jar practicaObligatoria.jar 5 localhost:8080 localhost:8081 $host1:8080 $host1:8081 $host2:8080 $host2:8081 &" &
	echo "lanzamos cliente6 en remoto"
	ssh $host3 "cd ~/Descargas/$nombre; java -jar practicaObligatoria.jar 6 localhost:8081 localhost:8080 $host1:8080 $host1:8081 $host2:8080 $host2:8081 &" &


	echo "lanzamos cliente3 en remoto"
	
	ssh $host2 "cd ~/Descargas/$nombre; java -jar practicaObligatoria.jar 3 localhost:8080 localhost:8081 $host1:8080 $host1:8081 $host3:8080 $host3:8081 &" &
	echo "lanzamos cliente4 en remoto"
	ssh $host2 "cd ~/Descargas/$nombre; java -jar practicaObligatoria.jar 4 localhost:8081 localhost:8080 $host1:8080 $host1:8081 $host3:8080 $host3:8081 &" &

	cd ~
	cd Descargas/$nombre

	echo "lanzamos cliente2 en local"
	java -jar practicaObligatoria.jar 2 localhost:8081 localhost:8080 $host2:8080 $host2:8081 $host3:8080 $host3:8081 &

	echo "lanzamos cliente1 en local"
	java -jar practicaObligatoria.jar 1 localhost:8080 localhost:8081 $host2:8080 $host2:8081 $host3:8080 $host3:8081

else
	echo "Uso: $0 usuario máquina"
	exit
fi
