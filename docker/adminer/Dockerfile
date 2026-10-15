FROM adminer
RUN sed -i -e 's/$xb=array("UID"=>$V,"PWD"=>$F,"CharacterSet"=>"UTF-8")/$xb=array("UID"=>$V,"PWD"=>$F,"CharacterSet"=>"UTF-8", "Encrypt"=>false)/g' /var/www/html/adminer.php

USER root
RUN chmod 777 -R /var/www/html
RUN mkdir /var/www/html/adminer
RUN mv /var/www/html/adminer.php /var/www/html/adminer/
RUN mv /var/www/html/index.php /var/www/html/adminer/
# RUN mv /var/www/html/.adminer-init /var/www/html/adminer/
RUN mv /var/www/html/plugin* /var/www/html/adminer/
RUN mv /var/www/html/designs /var/www/html/adminer/

USER adminer

COPY index.html /var/www/html/index.html

EXPOSE 8080