#!/bin/bash
echo ""
echo "*********** RUN etcd Cluster : Start ************"

ETCD_PATH="$1"
infra_name="$2"
infra1="$3"
infra2="$4"
infra3="$5"
if [[ $infra_name == 'infra0' ]]; then 
	infra_ip=$infra1
elif [[ $infra_name == 'infra1' ]]; then
	infra_ip=$infra2
elif [[ $infra_name == 'infra2' ]]; then
	infra_ip=$infra3
fi

cluster="infra0=http://$infra1:2380,infra1=http://$infra2:2380,infra2=http://$infra3:2380"
listen="http://$infra_ip:2379,http://127.0.0.1:2379"

$ETCD_PATH/etcd --name $infra_name --initial-advertise-peer-urls "http://$infra_ip:2380" \
--listen-peer-urls "http://$infra_ip:2380" \
--listen-client-urls $listen \
--advertise-client-urls "http://$infra_ip:2379" \
--initial-cluster-token etcd-cluster-1 \
--initial-cluster $cluster \
--initial-cluster-state new


