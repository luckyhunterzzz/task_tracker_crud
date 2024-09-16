FROM postgres:16

ENV POSTGRES_DB=mypostgres
ENV POSTGRES_USER=luckyhunter
ENV POSTGRES_PASSWORD=luckyhunter

COPY ./mypostgres.sql /docker-entrypoint-initdb.d/