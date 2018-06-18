#!/usr/bin/env bash

homepath="/home/usr_ten_taka/ten-taka"
alljars=`ls ${homepath}/lib | sed "s|^|${homepath}/lib/|g" | tr '\n' ','`

job_uuid=`uuidgen`
params=$*

/data/disk1/spark/spark-2.0.2-bin-hadoop2.6/bin/spark-submit \
--keytab /home/usr_ten_taka/usr_ten_taka.keytab --principal usr_ten_taka \
--class cogroup.GenerateCaseClassCustom \
--master yarn \
--deploy-mode client \
--name "generate-case-class $*" \
--driver-class-path ${homepath}/conf \
--jars "${alljars%?}" \
${homepath}/lib/ten-taka_2.11-1.0.jar $*
