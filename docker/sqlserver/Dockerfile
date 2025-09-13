FROM mcr.microsoft.com/mssql/server:2019-CU21-ubuntu-20.04

USER root
# COPY ./init-db /init-db
COPY ./entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

USER mssql
ENTRYPOINT ["/entrypoint.sh"]