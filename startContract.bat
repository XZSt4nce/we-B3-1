@ECHO OFF
SET username=xzst4nce
SET password=pa55WORD1
SET contractName=test_b3_2
SET tag=1.0.2

FOR /F "tokens=3 delims=: " %%a IN ('wsl cat ~/credentials.txt ^|wsl grep blockchain') DO SET blockchainAddress=%%a
FOR /F "tokens=3 delims=: " %%a IN ('wsl cat ~/credentials.txt ^|wsl grep keypair') DO SET keypairPassword=%%a
docker pull -q %username%/%contractName%:%tag% > NUL
FOR /F "tokens=2 delims=:" %%a IN ('docker images --quiet --no-trunc %username%/%contractName%:%tag%') DO SET id=%%a
FOR /F "tokens=2 delims=,: " %%a IN ('curl -s -X POST "http://localhost:6882/transactions/signAndBroadcast" -H "accept: application/json" -H "Content-Type: application/json" -H "X-API-Key: %API key%" -d "{ \"image\": \"registry.hub.docker.com/%username%/%contractName%:%tag%\", \"fee\": 0, \"imageHash\": \"%id%\", \"type\": 103, \"params\": [ { \"type\": \"string\", \"value\": \"init\", \"key\": \"action\" } ], \"version\": 2, \"sender\": \"%blockchainAddress%\", \"password\": \"%keypairPassword%\", \"feeAssetId\": null, \"contractName\": \"%contractName%\"}"^|find "id"') DO (
	ECHO export const contractAddress=%%a > B3-front\src\constants\contractAddress.js
)
