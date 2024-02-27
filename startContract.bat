@ECHO OFF
SET contractName=b3-contract
SET tag=1.0.0
SET imageHost=localhost:5000
SET imageURL=%imageHost%/%contractName%:%tag%

docker build B3-contract -f B3-contract/Dockerfile --platform linux/amd64 -t localhost:5000/b3-contract:1.0.0
docker push localhost:5000/b3-contract:1.0.0
FOR /F "tokens=3 delims=: " %%a IN ('wsl cat ~/credentials.txt ^|find "blockchain"') DO SET blockchainAddress=%%a
FOR /F "tokens=3 delims=: " %%a IN ('wsl cat ~/credentials.txt ^|find "keypair"') DO SET keypairPassword=%%a
docker pull -q %imageURL% > NUL
FOR /F "tokens=2 delims=:" %%a IN ('docker images --quiet --no-trunc %imageURL%') DO SET id=%%a
FOR /F "tokens=2 delims=,: " %%a IN ('curl -s -X POST "http://localhost:6882/transactions/signAndBroadcast" -H "accept: application/json" -H "Content-Type: application/json" -H "X-API-Key: %API key%" -d "{ \"image\": \"%imageURL%\", \"fee\": 0, \"imageHash\": \"%id%\", \"type\": 103, \"params\": [ { \"type\": \"string\", \"value\": \"init\", \"key\": \"action\" } ], \"version\": 2, \"sender\": \"%blockchainAddress%\", \"password\": \"%keypairPassword%\", \"feeAssetId\": null, \"contractName\": \"%contractName%\"}"^|find "id"') DO (
	ECHO export const contractAddress=%%a > B3-front\src\constants\contractAddress.js
)
ECHO Deploying contract...
PING localhost -n 10 >NUL
