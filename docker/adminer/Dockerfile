FROM adminer
RUN sed -i -e 's/$xb=array("UID"=>$V,"PWD"=>$F,"CharacterSet"=>"UTF-8")/$xb=array("UID"=>$V,"PWD"=>$F,"CharacterSet"=>"UTF-8", "Encrypt"=>false)/g' /var/www/html/adminer.php

EXPOSE 8080