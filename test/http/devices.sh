#!/bin/sh
#-------------------------------------------------------------------------------
# Copyright (C) 2017 Create-Net / FBK.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License 2.0
# which accompanies this distribution, and is available at
# https://www.eclipse.org/legal/epl-2.0/
# 
# SPDX-License-Identifier: EPL-2.0
# 
# Contributors:
#     Create-Net / FBK - initial API and implementation
#-------------------------------------------------------------------------------

HOST=${1:-localhost}

#Print Protocol devices
echo "ProtocolManager devices"
curl -X GET --header 'Accept: application/json' "http://$HOST:8080/api/protocols/devices" | jq '.[] | {id: .id, name: .name, protocol: .protocol, status: .status }'

#Print registered devices
echo "Registered devices"
curl -X GET --header 'Accept: application/json' "http://$HOST:8080/api/devices" | jq '.[] | {id: .deviceId, name: .name, protocol: .protocol, address: .address }'
