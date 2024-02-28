@ECHO OFF
SET contractName=b3-contract
SET tag=1.0.0
SET imageURL=localhost:5000/%contractName%:%tag%

FOR /F "tokens=3 delims=: " %%a IN ('wsl cat ~/credentials.txt ^|find "blockchain"') DO SET blockchainAddress=%%a
FOR /F "tokens=3 delims=: " %%a IN ('wsl cat ~/credentials.txt ^|find "keypair"') DO SET keypairPassword=%%a
FOR /F "tokens=2 delims=:" %%a IN ('docker images --quiet --no-trunc %imageURL%') DO SET id=%%a
FOR /F "tokens=2 delims=,: " %%a IN ('curl -s -X POST "http://localhost:6882/transactions/signAndBroadcast" -H "accept: application/json" -H "Content-Type: application/json" -d "{ \"image\": \"%imageURL%\", \"fee\": 0, \"imageHash\": \"%id%\", \"type\": 103, \"params\": [ { \"type\": \"string\", \"value\": \"init\", \"key\": \"action\" } ], \"version\": 2, \"sender\": \"%blockchainAddress%\", \"password\": \"%keypairPassword%\", \"feeAssetId\": null, \"contractName\": \"%contractName%\"}"^|find "id"') DO (
	ECHO export const contractAddress=%%a > B3-front\src\constants\contractAddress.js
)
ECHO Deploying contract...
PING localhost -n 10 >NUL
