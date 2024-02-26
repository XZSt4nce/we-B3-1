@ECHO OFF
FOR /F "tokens=3 delims=: " %%a IN ('wsl cat ~/credentials.txt^|find ^"blockchain^"') DO SET blockchainAddress=%%a
FOR /F "tokens=3 delims=: " %%a IN ('wsl cat ~/credentials.txt^|find ^"keypair^"') DO SET keypairPassword=%%a
ECHO export const nodeCredentials={blockchainAddress:"%blockchainAddress%", keypairPassword:"%keypairPassword%"}; > B3-front\src\constants\NodeCredentials.js
npm start --prefix B3-front