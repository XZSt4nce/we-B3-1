@ECHO OFF
SET user=xzst4nce
SET contractName=waves-shop
SET tag=latest
SET imageURL=%user%/%contractName%:%tag%

FOR /F "tokens=3 delims=: " %%a IN ('wsl cat ~/credentials.txt ^|find "blockchain"') DO SET blockchainAddress=%%a
FOR /F "tokens=3 delims=: " %%a IN ('wsl cat ~/credentials.txt ^|find "keypair"') DO SET keypairPassword=%%a
ECHO export const nodeCredentials = {blockchainAddress: "%blockchainAddress%", keypairPassword: "%keypairPassword%"}; > B3-front\src\constants\NodeCredentials.js

docker pull xzst4nce/waves-shop:latest
FOR /F "tokens=2 delims=:" %%a IN ('docker images --quiet --no-trunc %imageURL%') DO SET id=%%a
FOR /F "tokens=2 delims=,: " %%a IN ('CALL curl -s -X POST "http://localhost:6882/transactions/signAndBroadcast" -H "accept: application/json" -H "Content-Type: application/json" -d "{ \"image\": \"registry.hub.docker.com/%imageURL%\", \"fee\": 0, \"imageHash\": \"%id%\", \"type\": 103, \"params\": [ { \"type\": \"string\", \"value\": \"init\", \"key\": \"action\" } ], \"version\": 2, \"sender\": \"%blockchainAddress%\", \"password\": \"%keypairPassword%\", \"feeAssetId\": null, \"contractName\": \"%contractName%\"}"^|find "id"') DO (
	ECHO export const contractAddress=%%a; > B3-front\src\constants\ContractAddress.js
)
