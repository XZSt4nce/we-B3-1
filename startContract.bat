@ECHO OFF
SET username=xzst4nce
SET password=pa55WORD1
SET contract name=test_b3_2
SET tag=1.0.2

FOR /F "tokens=3 delims=: " %%a IN ('wsl cat ~/credentials.txt ^|wsl grep blockchain') DO SET blockchain address=%%a
FOR /F "tokens=3 delims=: " %%a IN ('wsl cat ~/credentials.txt ^|wsl grep keypair') DO SET keypair password=%%a
docker pull --quiet %username%/%contract name%:%tag%
FOR /F "tokens=2 delims=:" %%a IN ('docker images --quiet --no-trunc %username%/%contract name%:%tag%') DO SET id=%%a
wsl curl 'http://localhost:6882/transactions/signAndBroadcast' --header 'Content-Type: application/json' --data '{"image": "registry.hub.docker.com/%username%/%contract name%:%tag%","fee": 0,"imageHash":"%id%","type":103,"params":[{"type":"string","value":"init","key":"action"}],"version":2,"sender":"%blockchain address%","password":"%keypair password%","feeAssetId":null,"contractName":"%contract name%"}'