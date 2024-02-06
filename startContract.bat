@ECHO OFF
SET username=xzst4nce
SET password=pa55WORD1
SET contract name=test_b3_2
SET tag=1.0.1

wsl docker run --rm -ti --quiet -v ~/config-manager/output wavesenterprise/config-manager:latest
wsl docker-compose -f ~/docker-compose.yml up -d --no-recreate
wsl cat ~/credentials.txt ^| sed -n ^'/node-2/^,$ p^' ^| grep -e address -e keypair -e API > credentials.txt
FOR /F "tokens=3 delims=: " %%a IN ('type credentials.txt^|find "address"') DO (
	SET blockchain address=%%a
)
FOR /F "tokens=3 delims=: " %%a IN ('type credentials.txt^|find "keypair"') DO (
	SET keypair password=%%a
)
FOR /F "tokens=3 delims=: " %%a IN ('type credentials.txt^|find "API"') DO (
	SET API key=%%a
)
wsl docker login -u %username% -p %password% registry.hub.docker.com
wsl sh $(pwd -P)/B3-contract/build_and_push_to_docker.sh registry.hub.docker.com/%username%/%contract name%:%tag%
docker pull --quiet %username%/%contract name%:%tag%
FOR /F "tokens=2 delims=:" %%a IN ('docker images --quiet --no-trunc %username%/%contract name%:%tag%') DO (
	SET id=%%a
)
FOR /F "tokens=2 delims=: " %%a IN ('curl -s -X POST "http://localhost:6882/transactions/signAndBroadcast" -H "accept: application/json" -H "Content-Type: application/json" -H "X-API-Key: %API key%" -d "{ \"image\": \"registry.hub.docker.com/%username%/%contract name%:%tag%\", \"fee\": 0, \"imageHash\": \"%id%\", \"type\": 103, \"params\": [ { \"type\": \"string\", \"value\": \"init\", \"key\": \"action\" }, { \"type\": \"string\", \"value\": \"%username%\", \"key\": \"login\" } ], \"version\": 2, \"sender\": \"%blockchain address%\", \"password\": \"%keypair password%\", \"feeAssetId\": null, \"contractName\": \"%contract name%\"}"^|find "id"') DO (
	SET contract address=%%a
)
FOR /F delims^=^"^ tokens^=1 %%a IN (%contract address%) DO (
    SET contract address=%%a
)
ECHO %contract address%